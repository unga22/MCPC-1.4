package net.minecraft.server;

import java.util.List;

public class BlockPiston extends Block
{
    /** This pistons is the sticky one? */
    private boolean a;

    public BlockPiston(int var1, int var2, boolean var3)
    {
        super(var1, var2, Material.PISTON);
        this.a = var3;
        this.a(h);
        this.c(0.5F);
        this.a(CreativeModeTab.d);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        int var3 = e(var2);
        return var3 > 5 ? this.textureId : (var1 == var3 ? (!f(var2) && this.minX <= 0.0D && this.minY <= 0.0D && this.minZ <= 0.0D && this.maxX >= 1.0D && this.maxY >= 1.0D && this.maxZ >= 1.0D ? this.textureId : 110) : (var1 == Facing.OPPOSITE_FACING[var3] ? 109 : 108));
    }

    /**
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 16;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean c()
    {
        return false;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean interact(World var1, int var2, int var3, int var4, EntityHuman var5, int var6, float var7, float var8, float var9)
    {
        return false;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void postPlace(World var1, int var2, int var3, int var4, EntityLiving var5)
    {
        int var6 = b(var1, var2, var3, var4, (EntityHuman)var5);
        var1.setData(var2, var3, var4, var6);

        if (!var1.isStatic)
        {
            this.l(var1, var2, var3, var4);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (!var1.isStatic)
        {
            this.l(var1, var2, var3, var4);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onPlace(World var1, int var2, int var3, int var4)
    {
        if (!var1.isStatic && var1.getTileEntity(var2, var3, var4) == null)
        {
            this.l(var1, var2, var3, var4);
        }
    }

    /**
     * handles attempts to extend or retract the piston.
     */
    private void l(World var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);
        int var6 = e(var5);

        if (var6 != 7)
        {
            boolean var7 = this.d(var1, var2, var3, var4, var6);

            if (var7 && !f(var5))
            {
                if (h(var1, var2, var3, var4, var6))
                {
                    var1.playNote(var2, var3, var4, this.id, 0, var6);
                }
            }
            else if (!var7 && f(var5))
            {
                var1.playNote(var2, var3, var4, this.id, 1, var6);
            }
        }
    }

    /**
     * checks the block to that side to see if it is indirectly powered.
     */
    private boolean d(World var1, int var2, int var3, int var4, int var5)
    {
        return var5 != 0 && var1.isBlockFaceIndirectlyPowered(var2, var3 - 1, var4, 0) ? true : (var5 != 1 && var1.isBlockFaceIndirectlyPowered(var2, var3 + 1, var4, 1) ? true : (var5 != 2 && var1.isBlockFaceIndirectlyPowered(var2, var3, var4 - 1, 2) ? true : (var5 != 3 && var1.isBlockFaceIndirectlyPowered(var2, var3, var4 + 1, 3) ? true : (var5 != 5 && var1.isBlockFaceIndirectlyPowered(var2 + 1, var3, var4, 5) ? true : (var5 != 4 && var1.isBlockFaceIndirectlyPowered(var2 - 1, var3, var4, 4) ? true : (var1.isBlockFaceIndirectlyPowered(var2, var3, var4, 0) ? true : (var1.isBlockFaceIndirectlyPowered(var2, var3 + 2, var4, 1) ? true : (var1.isBlockFaceIndirectlyPowered(var2, var3 + 1, var4 - 1, 2) ? true : (var1.isBlockFaceIndirectlyPowered(var2, var3 + 1, var4 + 1, 3) ? true : (var1.isBlockFaceIndirectlyPowered(var2 - 1, var3 + 1, var4, 4) ? true : var1.isBlockFaceIndirectlyPowered(var2 + 1, var3 + 1, var4, 5)))))))))));
    }

    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
     * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
     */
    public void b(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        if (var5 == 0)
        {
            var1.setRawData(var2, var3, var4, var6 | 8);
        }
        else
        {
            var1.setRawData(var2, var3, var4, var6);
        }

        if (var5 == 0)
        {
            if (this.i(var1, var2, var3, var4, var6))
            {
                var1.setData(var2, var3, var4, var6 | 8);
                var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "tile.piston.out", 0.5F, var1.random.nextFloat() * 0.25F + 0.6F);
            }
            else
            {
                var1.setRawData(var2, var3, var4, var6);
            }
        }
        else if (var5 == 1)
        {
            TileEntity var7 = var1.getTileEntity(var2 + Facing.b[var6], var3 + Facing.c[var6], var4 + Facing.d[var6]);

            if (var7 instanceof TileEntityPiston)
            {
                ((TileEntityPiston)var7).f();
            }

            var1.setRawTypeIdAndData(var2, var3, var4, Block.PISTON_MOVING.id, var6);
            var1.setTileEntity(var2, var3, var4, BlockPistonMoving.a(this.id, var6, var6, false, true));

            if (this.a)
            {
                int var8 = var2 + Facing.b[var6] * 2;
                int var9 = var3 + Facing.c[var6] * 2;
                int var10 = var4 + Facing.d[var6] * 2;
                int var11 = var1.getTypeId(var8, var9, var10);
                int var12 = var1.getData(var8, var9, var10);
                boolean var13 = false;

                if (var11 == Block.PISTON_MOVING.id)
                {
                    TileEntity var14 = var1.getTileEntity(var8, var9, var10);

                    if (var14 instanceof TileEntityPiston)
                    {
                        TileEntityPiston var15 = (TileEntityPiston)var14;

                        if (var15.c() == var6 && var15.b())
                        {
                            var15.f();
                            var11 = var15.a();
                            var12 = var15.p();
                            var13 = true;
                        }
                    }
                }

                if (!var13 && var11 > 0 && a(var11, var1, var8, var9, var10, false) && (Block.byId[var11].q_() == 0 || var11 == Block.PISTON.id || var11 == Block.PISTON_STICKY.id))
                {
                    var2 += Facing.b[var6];
                    var3 += Facing.c[var6];
                    var4 += Facing.d[var6];
                    var1.setRawTypeIdAndData(var2, var3, var4, Block.PISTON_MOVING.id, var12);
                    var1.setTileEntity(var2, var3, var4, BlockPistonMoving.a(var11, var12, var6, false, false));
                    var1.setTypeId(var8, var9, var10, 0);
                }
                else if (!var13)
                {
                    var1.setTypeId(var2 + Facing.b[var6], var3 + Facing.c[var6], var4 + Facing.d[var6], 0);
                }
            }
            else
            {
                var1.setTypeId(var2 + Facing.b[var6], var3 + Facing.c[var6], var4 + Facing.d[var6], 0);
            }

            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "tile.piston.in", 0.5F, var1.random.nextFloat() * 0.15F + 0.6F);
        }
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void updateShape(IBlockAccess var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);

        if (f(var5))
        {
            switch (e(var5))
            {
                case 0:
                    this.a(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;

                case 1:
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                    break;

                case 2:
                    this.a(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                    break;

                case 3:
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                    break;

                case 4:
                    this.a(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;

                case 5:
                    this.a(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
        }
        else
        {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void f()
    {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * if the specified block is in the given AABB, add its collision bounding box to the given list
     */
    public void a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7)
    {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.a(var1, var2, var3, var4, var5, var6, var7);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        this.updateShape(var1, var2, var3, var4);
        return super.e(var1, var2, var3, var4);
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean b()
    {
        return false;
    }

    /**
     * returns an int which describes the direction the piston faces
     */
    public static int e(int var0)
    {
        return var0 & 7;
    }

    /**
     * Determine if the metadata is related to something powered.
     */
    public static boolean f(int var0)
    {
        return (var0 & 8) != 0;
    }

    /**
     * gets the way this piston should face for that entity that placed it.
     */
    public static int b(World var0, int var1, int var2, int var3, EntityHuman var4)
    {
        if (MathHelper.a((float)var4.locX - (float)var1) < 2.0F && MathHelper.a((float)var4.locZ - (float)var3) < 2.0F)
        {
            double var5 = var4.locY + 1.82D - (double)var4.height;

            if (var5 - (double)var2 > 2.0D)
            {
                return 1;
            }

            if ((double)var2 - var5 > 0.0D)
            {
                return 0;
            }
        }

        int var7 = MathHelper.floor((double)(var4.yaw * 4.0F / 360.0F) + 0.5D) & 3;
        return var7 == 0 ? 2 : (var7 == 1 ? 5 : (var7 == 2 ? 3 : (var7 == 3 ? 4 : 0)));
    }

    /**
     * returns true if the piston can push the specified block
     */
    private static boolean a(int var0, World var1, int var2, int var3, int var4, boolean var5)
    {
        if (var0 == Block.OBSIDIAN.id)
        {
            return false;
        }
        else
        {
            if (var0 != Block.PISTON.id && var0 != Block.PISTON_STICKY.id)
            {
                if (Block.byId[var0].m(var1, var2, var3, var4) == -1.0F)
                {
                    return false;
                }

                if (Block.byId[var0].q_() == 2)
                {
                    return false;
                }

                if (!var5 && Block.byId[var0].q_() == 1)
                {
                    return false;
                }
            }
            else if (f(var1.getData(var2, var3, var4)))
            {
                return false;
            }

            return !(Block.byId[var0] instanceof BlockContainer);
        }
    }

    /**
     * checks to see if this piston could push the blocks in front of it.
     */
    private static boolean h(World var0, int var1, int var2, int var3, int var4)
    {
        int var5 = var1 + Facing.b[var4];
        int var6 = var2 + Facing.c[var4];
        int var7 = var3 + Facing.d[var4];
        int var8 = 0;

        while (true)
        {
            if (var8 < 13)
            {
                if (var6 <= 0 || var6 >= 255)
                {
                    return false;
                }

                int var9 = var0.getTypeId(var5, var6, var7);

                if (var9 != 0)
                {
                    if (!a(var9, var0, var5, var6, var7, true))
                    {
                        return false;
                    }

                    if (Block.byId[var9].q_() != 1)
                    {
                        if (var8 == 12)
                        {
                            return false;
                        }

                        var5 += Facing.b[var4];
                        var6 += Facing.c[var4];
                        var7 += Facing.d[var4];
                        ++var8;
                        continue;
                    }
                }
            }

            return true;
        }
    }

    /**
     * attempts to extend the piston. returns false if impossible.
     */
    private boolean i(World var1, int var2, int var3, int var4, int var5)
    {
        int var6 = var2 + Facing.b[var5];
        int var7 = var3 + Facing.c[var5];
        int var8 = var4 + Facing.d[var5];
        int var9 = 0;

        while (true)
        {
            int var10;

            if (var9 < 13)
            {
                if (var7 <= 0 || var7 >= 255)
                {
                    return false;
                }

                var10 = var1.getTypeId(var6, var7, var8);

                if (var10 != 0)
                {
                    if (!a(var10, var1, var6, var7, var8, true))
                    {
                        return false;
                    }

                    if (Block.byId[var10].q_() != 1)
                    {
                        if (var9 == 12)
                        {
                            return false;
                        }

                        var6 += Facing.b[var5];
                        var7 += Facing.c[var5];
                        var8 += Facing.d[var5];
                        ++var9;
                        continue;
                    }

                    Block.byId[var10].c(var1, var6, var7, var8, var1.getData(var6, var7, var8), 0);
                    var1.setTypeId(var6, var7, var8, 0);
                }
            }

            while (var6 != var2 || var7 != var3 || var8 != var4)
            {
                var9 = var6 - Facing.b[var5];
                var10 = var7 - Facing.c[var5];
                int var11 = var8 - Facing.d[var5];
                int var12 = var1.getTypeId(var9, var10, var11);
                int var13 = var1.getData(var9, var10, var11);

                if (var12 == this.id && var9 == var2 && var10 == var3 && var11 == var4)
                {
                    var1.setRawTypeIdAndData(var6, var7, var8, Block.PISTON_MOVING.id, var5 | (this.a ? 8 : 0), false);
                    var1.setTileEntity(var6, var7, var8, BlockPistonMoving.a(Block.PISTON_EXTENSION.id, var5 | (this.a ? 8 : 0), var5, true, false));
                }
                else
                {
                    var1.setRawTypeIdAndData(var6, var7, var8, Block.PISTON_MOVING.id, var13, false);
                    var1.setTileEntity(var6, var7, var8, BlockPistonMoving.a(var12, var13, var5, true, false));
                }

                var6 = var9;
                var7 = var10;
                var8 = var11;
            }

            return true;
        }
    }
}
