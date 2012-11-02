package net.minecraft.server;

import java.util.ArrayList;
import java.util.Random;

import net.minecraftforge.common.IShearable;

public class BlockVine extends Block implements IShearable
{
    public BlockVine(int var1)
    {
        super(var1, 143, Material.REPLACEABLE_PLANT);
        this.b(true);
        this.a(CreativeModeTab.c);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void f()
    {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 20;
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
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean b()
    {
        return false;
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void updateShape(IBlockAccess var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);
        float var6 = 1.0F;
        float var7 = 1.0F;
        float var8 = 1.0F;
        float var9 = 0.0F;
        float var10 = 0.0F;
        float var11 = 0.0F;
        boolean var12 = var5 > 0;

        if ((var5 & 2) != 0)
        {
            var9 = Math.max(var9, 0.0625F);
            var6 = 0.0F;
            var7 = 0.0F;
            var10 = 1.0F;
            var8 = 0.0F;
            var11 = 1.0F;
            var12 = true;
        }

        if ((var5 & 8) != 0)
        {
            var6 = Math.min(var6, 0.9375F);
            var9 = 1.0F;
            var7 = 0.0F;
            var10 = 1.0F;
            var8 = 0.0F;
            var11 = 1.0F;
            var12 = true;
        }

        if ((var5 & 4) != 0)
        {
            var11 = Math.max(var11, 0.0625F);
            var8 = 0.0F;
            var6 = 0.0F;
            var9 = 1.0F;
            var7 = 0.0F;
            var10 = 1.0F;
            var12 = true;
        }

        if ((var5 & 1) != 0)
        {
            var8 = Math.min(var8, 0.9375F);
            var11 = 1.0F;
            var6 = 0.0F;
            var9 = 1.0F;
            var7 = 0.0F;
            var10 = 1.0F;
            var12 = true;
        }

        if (!var12 && this.e(var1.getTypeId(var2, var3 + 1, var4)))
        {
            var7 = Math.min(var7, 0.9375F);
            var10 = 1.0F;
            var6 = 0.0F;
            var9 = 1.0F;
            var8 = 0.0F;
            var11 = 1.0F;
        }

        this.a(var6, var7, var8, var9, var10, var11);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        return null;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlace(World var1, int var2, int var3, int var4, int var5)
    {
        switch (var5)
        {
            case 1:
                return this.e(var1.getTypeId(var2, var3 + 1, var4));

            case 2:
                return this.e(var1.getTypeId(var2, var3, var4 + 1));

            case 3:
                return this.e(var1.getTypeId(var2, var3, var4 - 1));

            case 4:
                return this.e(var1.getTypeId(var2 + 1, var3, var4));

            case 5:
                return this.e(var1.getTypeId(var2 - 1, var3, var4));

            default:
                return false;
        }
    }

    /**
     * returns true if a vine can be placed on that block (checks for render as normal block and if it is solid)
     */
    private boolean e(int var1)
    {
        if (var1 == 0)
        {
            return false;
        }
        else
        {
            Block var2 = Block.byId[var1];
            return var2.b() && var2.material.isSolid();
        }
    }

    /**
     * Returns if the vine can stay in the world. It also changes the metadata according to neighboring blocks.
     */
    private boolean l(World var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);
        int var6 = var5;

        if (var5 > 0)
        {
            for (int var7 = 0; var7 <= 3; ++var7)
            {
                int var8 = 1 << var7;

                if ((var5 & var8) != 0 && !this.e(var1.getTypeId(var2 + Direction.a[var7], var3, var4 + Direction.b[var7])) && (var1.getTypeId(var2, var3 + 1, var4) != this.id || (var1.getData(var2, var3 + 1, var4) & var8) == 0))
                {
                    var6 &= ~var8;
                }
            }
        }

        if (var6 == 0 && !this.e(var1.getTypeId(var2, var3 + 1, var4)))
        {
            return false;
        }
        else
        {
            if (var6 != var5)
            {
                var1.setData(var2, var3, var4, var6);
            }

            return true;
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (!var1.isStatic && !this.l(var1, var2, var3, var4))
        {
            this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
            var1.setTypeId(var2, var3, var4, 0);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (!var1.isStatic && var1.random.nextInt(4) == 0)
        {
            byte var6 = 4;
            int var7 = 5;
            boolean var8 = false;
            int var9;
            int var10;
            int var11;
            label134:

            for (var9 = var2 - var6; var9 <= var2 + var6; ++var9)
            {
                for (var10 = var4 - var6; var10 <= var4 + var6; ++var10)
                {
                    for (var11 = var3 - 1; var11 <= var3 + 1; ++var11)
                    {
                        if (var1.getTypeId(var9, var11, var10) == this.id)
                        {
                            --var7;

                            if (var7 <= 0)
                            {
                                var8 = true;
                                break label134;
                            }
                        }
                    }
                }
            }

            var9 = var1.getData(var2, var3, var4);
            var10 = var1.random.nextInt(6);
            var11 = Direction.e[var10];
            int var12;
            int var13;

            if (var10 == 1 && var3 < 255 && var1.isEmpty(var2, var3 + 1, var4))
            {
                if (var8)
                {
                    return;
                }

                var12 = var1.random.nextInt(16) & var9;

                if (var12 > 0)
                {
                    for (var13 = 0; var13 <= 3; ++var13)
                    {
                        if (!this.e(var1.getTypeId(var2 + Direction.a[var13], var3 + 1, var4 + Direction.b[var13])))
                        {
                            var12 &= ~(1 << var13);
                        }
                    }

                    if (var12 > 0)
                    {
                        var1.setTypeIdAndData(var2, var3 + 1, var4, this.id, var12);
                    }
                }
            }
            else
            {
                int var14;

                if (var10 >= 2 && var10 <= 5 && (var9 & 1 << var11) == 0)
                {
                    if (var8)
                    {
                        return;
                    }

                    var12 = var1.getTypeId(var2 + Direction.a[var11], var3, var4 + Direction.b[var11]);

                    if (var12 != 0 && Block.byId[var12] != null)
                    {
                        if (Block.byId[var12].material.k() && Block.byId[var12].b())
                        {
                            var1.setData(var2, var3, var4, var9 | 1 << var11);
                        }
                    }
                    else
                    {
                        var13 = var11 + 1 & 3;
                        var14 = var11 + 3 & 3;

                        if ((var9 & 1 << var13) != 0 && this.e(var1.getTypeId(var2 + Direction.a[var11] + Direction.a[var13], var3, var4 + Direction.b[var11] + Direction.b[var13])))
                        {
                            var1.setTypeIdAndData(var2 + Direction.a[var11], var3, var4 + Direction.b[var11], this.id, 1 << var13);
                        }
                        else if ((var9 & 1 << var14) != 0 && this.e(var1.getTypeId(var2 + Direction.a[var11] + Direction.a[var14], var3, var4 + Direction.b[var11] + Direction.b[var14])))
                        {
                            var1.setTypeIdAndData(var2 + Direction.a[var11], var3, var4 + Direction.b[var11], this.id, 1 << var14);
                        }
                        else if ((var9 & 1 << var13) != 0 && var1.isEmpty(var2 + Direction.a[var11] + Direction.a[var13], var3, var4 + Direction.b[var11] + Direction.b[var13]) && this.e(var1.getTypeId(var2 + Direction.a[var13], var3, var4 + Direction.b[var13])))
                        {
                            var1.setTypeIdAndData(var2 + Direction.a[var11] + Direction.a[var13], var3, var4 + Direction.b[var11] + Direction.b[var13], this.id, 1 << (var11 + 2 & 3));
                        }
                        else if ((var9 & 1 << var14) != 0 && var1.isEmpty(var2 + Direction.a[var11] + Direction.a[var14], var3, var4 + Direction.b[var11] + Direction.b[var14]) && this.e(var1.getTypeId(var2 + Direction.a[var14], var3, var4 + Direction.b[var14])))
                        {
                            var1.setTypeIdAndData(var2 + Direction.a[var11] + Direction.a[var14], var3, var4 + Direction.b[var11] + Direction.b[var14], this.id, 1 << (var11 + 2 & 3));
                        }
                        else if (this.e(var1.getTypeId(var2 + Direction.a[var11], var3 + 1, var4 + Direction.b[var11])))
                        {
                            var1.setTypeIdAndData(var2 + Direction.a[var11], var3, var4 + Direction.b[var11], this.id, 0);
                        }
                    }
                }
                else if (var3 > 1)
                {
                    var12 = var1.getTypeId(var2, var3 - 1, var4);

                    if (var12 == 0)
                    {
                        var13 = var1.random.nextInt(16) & var9;

                        if (var13 > 0)
                        {
                            var1.setTypeIdAndData(var2, var3 - 1, var4, this.id, var13);
                        }
                    }
                    else if (var12 == this.id)
                    {
                        var13 = var1.random.nextInt(16) & var9;
                        var14 = var1.getData(var2, var3 - 1, var4);

                        if (var14 != (var14 | var13))
                        {
                            var1.setData(var2, var3 - 1, var4, var14 | var13);
                        }
                    }
                }
            }
        }
    }

    /**
     * called before onBlockPlacedBy by ItemBlock and ItemReed
     */
    public void postPlace(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8)
    {
        byte var9 = 0;

        switch (var5)
        {
            case 2:
                var9 = 1;
                break;

            case 3:
                var9 = 4;
                break;

            case 4:
                var9 = 8;
                break;

            case 5:
                var9 = 2;
        }

        if (var9 != 0)
        {
            var1.setData(var2, var3, var4, var9);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return 0;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int a(Random var1)
    {
        return 0;
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void a(World var1, EntityHuman var2, int var3, int var4, int var5, int var6)
    {
        super.a(var1, var2, var3, var4, var5, var6);
    }

    public boolean isShearable(ItemStack var1, World var2, int var3, int var4, int var5)
    {
        return true;
    }

    public ArrayList onSheared(ItemStack var1, World var2, int var3, int var4, int var5, int var6)
    {
        ArrayList var7 = new ArrayList();
        var7.add(new ItemStack(this, 1, 0));
        return var7;
    }

    public boolean isLadder(World var1, int var2, int var3, int var4)
    {
        return true;
    }
}
