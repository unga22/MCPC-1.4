package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class BlockRedstoneWire extends Block
{
    /**
     * When false, power transmission methods do not look at other redstone wires. Used internally during
     * updateCurrentStrength.
     */
    private boolean a = true;
    private Set b = new HashSet();

    public BlockRedstoneWire(int var1, int var2)
    {
        super(var1, var2, Material.ORIENTABLE);
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        return this.textureId;
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
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 5;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var1.t(var2, var3 - 1, var4) || var1.getTypeId(var2, var3 - 1, var4) == Block.GLOWSTONE.id;
    }

    /**
     * Sets the strength of the wire current (0-15) for this block based on neighboring blocks and propagates to
     * neighboring redstone wires
     */
    private void l(World var1, int var2, int var3, int var4)
    {
        this.a(var1, var2, var3, var4, var2, var3, var4);
        ArrayList var5 = new ArrayList(this.b);
        this.b.clear();
        Iterator var6 = var5.iterator();

        while (var6.hasNext())
        {
            ChunkPosition var7 = (ChunkPosition)var6.next();
            var1.applyPhysics(var7.x, var7.y, var7.z, this.id);
        }
    }

    private void a(World var1, int var2, int var3, int var4, int var5, int var6, int var7)
    {
        int var8 = var1.getData(var2, var3, var4);
        int var9 = 0;
        this.a = false;
        boolean var10 = var1.isBlockIndirectlyPowered(var2, var3, var4);
        this.a = true;
        int var11;
        int var12;
        int var13;

        if (var10)
        {
            var9 = 15;
        }
        else
        {
            for (var11 = 0; var11 < 4; ++var11)
            {
                var12 = var2;
                var13 = var4;

                if (var11 == 0)
                {
                    var12 = var2 - 1;
                }

                if (var11 == 1)
                {
                    ++var12;
                }

                if (var11 == 2)
                {
                    var13 = var4 - 1;
                }

                if (var11 == 3)
                {
                    ++var13;
                }

                if (var12 != var5 || var3 != var6 || var13 != var7)
                {
                    var9 = this.getPower(var1, var12, var3, var13, var9);
                }

                if (var1.s(var12, var3, var13) && !var1.s(var2, var3 + 1, var4))
                {
                    if (var12 != var5 || var3 + 1 != var6 || var13 != var7)
                    {
                        var9 = this.getPower(var1, var12, var3 + 1, var13, var9);
                    }
                }
                else if (!var1.s(var12, var3, var13) && (var12 != var5 || var3 - 1 != var6 || var13 != var7))
                {
                    var9 = this.getPower(var1, var12, var3 - 1, var13, var9);
                }
            }

            if (var9 > 0)
            {
                --var9;
            }
            else
            {
                var9 = 0;
            }
        }

        if (var8 != var9)
        {
            var1.suppressPhysics = true;
            var1.setData(var2, var3, var4, var9);
            var1.e(var2, var3, var4, var2, var3, var4);
            var1.suppressPhysics = false;

            for (var11 = 0; var11 < 4; ++var11)
            {
                var12 = var2;
                var13 = var4;
                int var14 = var3 - 1;

                if (var11 == 0)
                {
                    var12 = var2 - 1;
                }

                if (var11 == 1)
                {
                    ++var12;
                }

                if (var11 == 2)
                {
                    var13 = var4 - 1;
                }

                if (var11 == 3)
                {
                    ++var13;
                }

                if (var1.s(var12, var3, var13))
                {
                    var14 += 2;
                }

                boolean var15 = false;
                int var16 = this.getPower(var1, var12, var3, var13, -1);
                var9 = var1.getData(var2, var3, var4);

                if (var9 > 0)
                {
                    --var9;
                }

                if (var16 >= 0 && var16 != var9)
                {
                    this.a(var1, var12, var3, var13, var2, var3, var4);
                }

                var16 = this.getPower(var1, var12, var14, var13, -1);
                var9 = var1.getData(var2, var3, var4);

                if (var9 > 0)
                {
                    --var9;
                }

                if (var16 >= 0 && var16 != var9)
                {
                    this.a(var1, var12, var14, var13, var2, var3, var4);
                }
            }

            if (var8 < var9 || var9 == 0)
            {
                this.b.add(new ChunkPosition(var2, var3, var4));
                this.b.add(new ChunkPosition(var2 - 1, var3, var4));
                this.b.add(new ChunkPosition(var2 + 1, var3, var4));
                this.b.add(new ChunkPosition(var2, var3 - 1, var4));
                this.b.add(new ChunkPosition(var2, var3 + 1, var4));
                this.b.add(new ChunkPosition(var2, var3, var4 - 1));
                this.b.add(new ChunkPosition(var2, var3, var4 + 1));
            }
        }
    }

    /**
     * Calls World.notifyBlocksOfNeighborChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void n(World var1, int var2, int var3, int var4)
    {
        if (var1.getTypeId(var2, var3, var4) == this.id)
        {
            var1.applyPhysics(var2, var3, var4, this.id);
            var1.applyPhysics(var2 - 1, var3, var4, this.id);
            var1.applyPhysics(var2 + 1, var3, var4, this.id);
            var1.applyPhysics(var2, var3, var4 - 1, this.id);
            var1.applyPhysics(var2, var3, var4 + 1, this.id);
            var1.applyPhysics(var2, var3 - 1, var4, this.id);
            var1.applyPhysics(var2, var3 + 1, var4, this.id);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onPlace(World var1, int var2, int var3, int var4)
    {
        super.onPlace(var1, var2, var3, var4);

        if (!var1.isStatic)
        {
            this.l(var1, var2, var3, var4);
            var1.applyPhysics(var2, var3 + 1, var4, this.id);
            var1.applyPhysics(var2, var3 - 1, var4, this.id);
            this.n(var1, var2 - 1, var3, var4);
            this.n(var1, var2 + 1, var3, var4);
            this.n(var1, var2, var3, var4 - 1);
            this.n(var1, var2, var3, var4 + 1);

            if (var1.s(var2 - 1, var3, var4))
            {
                this.n(var1, var2 - 1, var3 + 1, var4);
            }
            else
            {
                this.n(var1, var2 - 1, var3 - 1, var4);
            }

            if (var1.s(var2 + 1, var3, var4))
            {
                this.n(var1, var2 + 1, var3 + 1, var4);
            }
            else
            {
                this.n(var1, var2 + 1, var3 - 1, var4);
            }

            if (var1.s(var2, var3, var4 - 1))
            {
                this.n(var1, var2, var3 + 1, var4 - 1);
            }
            else
            {
                this.n(var1, var2, var3 - 1, var4 - 1);
            }

            if (var1.s(var2, var3, var4 + 1))
            {
                this.n(var1, var2, var3 + 1, var4 + 1);
            }
            else
            {
                this.n(var1, var2, var3 - 1, var4 + 1);
            }
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void remove(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        super.remove(var1, var2, var3, var4, var5, var6);

        if (!var1.isStatic)
        {
            var1.applyPhysics(var2, var3 + 1, var4, this.id);
            var1.applyPhysics(var2, var3 - 1, var4, this.id);
            var1.applyPhysics(var2 + 1, var3, var4, this.id);
            var1.applyPhysics(var2 - 1, var3, var4, this.id);
            var1.applyPhysics(var2, var3, var4 + 1, this.id);
            var1.applyPhysics(var2, var3, var4 - 1, this.id);
            this.l(var1, var2, var3, var4);
            this.n(var1, var2 - 1, var3, var4);
            this.n(var1, var2 + 1, var3, var4);
            this.n(var1, var2, var3, var4 - 1);
            this.n(var1, var2, var3, var4 + 1);

            if (var1.s(var2 - 1, var3, var4))
            {
                this.n(var1, var2 - 1, var3 + 1, var4);
            }
            else
            {
                this.n(var1, var2 - 1, var3 - 1, var4);
            }

            if (var1.s(var2 + 1, var3, var4))
            {
                this.n(var1, var2 + 1, var3 + 1, var4);
            }
            else
            {
                this.n(var1, var2 + 1, var3 - 1, var4);
            }

            if (var1.s(var2, var3, var4 - 1))
            {
                this.n(var1, var2, var3 + 1, var4 - 1);
            }
            else
            {
                this.n(var1, var2, var3 - 1, var4 - 1);
            }

            if (var1.s(var2, var3, var4 + 1))
            {
                this.n(var1, var2, var3 + 1, var4 + 1);
            }
            else
            {
                this.n(var1, var2, var3 - 1, var4 + 1);
            }
        }
    }

    /**
     * Returns the current strength at the specified block if it is greater than the passed value, or the passed value
     * otherwise. Signature: (world, x, y, z, strength)
     */
    private int getPower(World var1, int var2, int var3, int var4, int var5)
    {
        if (var1.getTypeId(var2, var3, var4) != this.id)
        {
            return var5;
        }
        else
        {
            int var6 = var1.getData(var2, var3, var4);
            return var6 > var5 ? var6 : var5;
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
            int var6 = var1.getData(var2, var3, var4);
            boolean var7 = this.canPlace(var1, var2, var3, var4);

            if (var7)
            {
                this.l(var1, var2, var3, var4);
            }
            else
            {
                this.c(var1, var2, var3, var4, var6, 0);
                var1.setTypeId(var2, var3, var4, 0);
            }

            super.doPhysics(var1, var2, var3, var4, var5);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return Item.REDSTONE.id;
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean c(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        return !this.a ? false : this.b(var1, var2, var3, var4, var5);
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean b(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        if (!this.a)
        {
            return false;
        }
        else if (var1.getData(var2, var3, var4) == 0)
        {
            return false;
        }
        else if (var5 == 1)
        {
            return true;
        }
        else
        {
            boolean var6 = g(var1, var2 - 1, var3, var4, 1) || !var1.s(var2 - 1, var3, var4) && g(var1, var2 - 1, var3 - 1, var4, -1);
            boolean var7 = g(var1, var2 + 1, var3, var4, 3) || !var1.s(var2 + 1, var3, var4) && g(var1, var2 + 1, var3 - 1, var4, -1);
            boolean var8 = g(var1, var2, var3, var4 - 1, 2) || !var1.s(var2, var3, var4 - 1) && g(var1, var2, var3 - 1, var4 - 1, -1);
            boolean var9 = g(var1, var2, var3, var4 + 1, 0) || !var1.s(var2, var3, var4 + 1) && g(var1, var2, var3 - 1, var4 + 1, -1);

            if (!var1.s(var2, var3 + 1, var4))
            {
                if (var1.s(var2 - 1, var3, var4) && g(var1, var2 - 1, var3 + 1, var4, -1))
                {
                    var6 = true;
                }

                if (var1.s(var2 + 1, var3, var4) && g(var1, var2 + 1, var3 + 1, var4, -1))
                {
                    var7 = true;
                }

                if (var1.s(var2, var3, var4 - 1) && g(var1, var2, var3 + 1, var4 - 1, -1))
                {
                    var8 = true;
                }

                if (var1.s(var2, var3, var4 + 1) && g(var1, var2, var3 + 1, var4 + 1, -1))
                {
                    var9 = true;
                }
            }

            return !var8 && !var7 && !var6 && !var9 && var5 >= 2 && var5 <= 5 ? true : (var5 == 2 && var8 && !var6 && !var7 ? true : (var5 == 3 && var9 && !var6 && !var7 ? true : (var5 == 4 && var6 && !var8 && !var9 ? true : var5 == 5 && var7 && !var8 && !var9)));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean isPowerSource()
    {
        return this.a;
    }

    /**
     * Returns true if the block coordinate passed can provide power, or is a redstone wire.
     */
    public static boolean f(IBlockAccess var0, int var1, int var2, int var3, int var4)
    {
        int var5 = var0.getTypeId(var1, var2, var3);

        if (var5 == Block.REDSTONE_WIRE.id)
        {
            return true;
        }
        else if (var5 == 0)
        {
            return false;
        }
        else if (var5 != Block.DIODE_OFF.id && var5 != Block.DIODE_ON.id)
        {
            return Block.byId[var5].isPowerSource() && var4 != -1;
        }
        else
        {
            int var6 = var0.getData(var1, var2, var3);
            return var4 == (var6 & 3) || var4 == Direction.f[var6 & 3];
        }
    }

    /**
     * Returns true if the block coordinate passed can provide power, or is a redstone wire, or if its a repeater that
     * is powered.
     */
    public static boolean g(IBlockAccess var0, int var1, int var2, int var3, int var4)
    {
        if (f(var0, var1, var2, var3, var4))
        {
            return true;
        }
        else
        {
            int var5 = var0.getTypeId(var1, var2, var3);

            if (var5 == Block.DIODE_ON.id)
            {
                int var6 = var0.getData(var1, var2, var3);
                return var4 == (var6 & 3);
            }
            else
            {
                return false;
            }
        }
    }
}
