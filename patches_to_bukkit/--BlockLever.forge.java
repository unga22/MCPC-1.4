package net.minecraft.server;

public class BlockLever extends Block
{
    protected BlockLever(int var1, int var2)
    {
        super(var1, var2, Material.ORIENTABLE);
        this.a(CreativeModeTab.d);
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
        return 12;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlace(World var1, int var2, int var3, int var4, int var5)
    {
        return var5 == 0 && var1.s(var2, var3 + 1, var4) ? true : (var5 == 1 && var1.t(var2, var3 - 1, var4) ? true : (var5 == 2 && var1.s(var2, var3, var4 + 1) ? true : (var5 == 3 && var1.s(var2, var3, var4 - 1) ? true : (var5 == 4 && var1.s(var2 + 1, var3, var4) ? true : var5 == 5 && var1.s(var2 - 1, var3, var4)))));
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var1.s(var2 - 1, var3, var4) ? true : (var1.s(var2 + 1, var3, var4) ? true : (var1.s(var2, var3, var4 - 1) ? true : (var1.s(var2, var3, var4 + 1) ? true : (var1.t(var2, var3 - 1, var4) ? true : var1.s(var2, var3 + 1, var4)))));
    }

    /**
     * called before onBlockPlacedBy by ItemBlock and ItemReed
     */
    public void postPlace(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8)
    {
        int var9 = var1.getData(var2, var3, var4);
        int var10 = var9 & 8;
        var9 &= 7;
        var9 = -1;

        if (var5 == 0 && var1.s(var2, var3 + 1, var4))
        {
            var9 = var1.random.nextBoolean() ? 0 : 7;
        }

        if (var5 == 1 && var1.t(var2, var3 - 1, var4))
        {
            var9 = 5 + var1.random.nextInt(2);
        }

        if (var5 == 2 && var1.s(var2, var3, var4 + 1))
        {
            var9 = 4;
        }

        if (var5 == 3 && var1.s(var2, var3, var4 - 1))
        {
            var9 = 3;
        }

        if (var5 == 4 && var1.s(var2 + 1, var3, var4))
        {
            var9 = 2;
        }

        if (var5 == 5 && var1.s(var2 - 1, var3, var4))
        {
            var9 = 1;
        }

        if (var9 == -1)
        {
            this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
            var1.setTypeId(var2, var3, var4, 0);
        }
        else
        {
            var1.setData(var2, var3, var4, var9 + var10);
        }
    }

    /**
     * only used in ComponentScatteredFeatureJunglePyramid.addComponentParts"
     */
    public static int d(int var0)
    {
        switch (var0)
        {
            case 0:
                return 0;

            case 1:
                return 5;

            case 2:
                return 4;

            case 3:
                return 3;

            case 4:
                return 2;

            case 5:
                return 1;

            default:
                return -1;
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (this.l(var1, var2, var3, var4))
        {
            int var6 = var1.getData(var2, var3, var4) & 7;
            boolean var7 = false;

            if (!var1.s(var2 - 1, var3, var4) && var6 == 1)
            {
                var7 = true;
            }

            if (!var1.s(var2 + 1, var3, var4) && var6 == 2)
            {
                var7 = true;
            }

            if (!var1.s(var2, var3, var4 - 1) && var6 == 3)
            {
                var7 = true;
            }

            if (!var1.s(var2, var3, var4 + 1) && var6 == 4)
            {
                var7 = true;
            }

            if (!var1.t(var2, var3 - 1, var4) && var6 == 5)
            {
                var7 = true;
            }

            if (!var1.t(var2, var3 - 1, var4) && var6 == 6)
            {
                var7 = true;
            }

            if (!var1.s(var2, var3 + 1, var4) && var6 == 0)
            {
                var7 = true;
            }

            if (!var1.s(var2, var3 + 1, var4) && var6 == 7)
            {
                var7 = true;
            }

            if (var7)
            {
                this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
                var1.setTypeId(var2, var3, var4, 0);
            }
        }
    }

    /**
     * Checks if the block is attached to another block. If it is not, it returns false and drops the block as an item.
     * If it is it returns true.
     */
    private boolean l(World var1, int var2, int var3, int var4)
    {
        if (!this.canPlace(var1, var2, var3, var4))
        {
            this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
            var1.setTypeId(var2, var3, var4, 0);
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void updateShape(IBlockAccess var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4) & 7;
        float var6 = 0.1875F;

        if (var5 == 1)
        {
            this.a(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
        }
        else if (var5 == 2)
        {
            this.a(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
        }
        else if (var5 == 3)
        {
            this.a(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
        }
        else if (var5 == 4)
        {
            this.a(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
        }
        else if (var5 != 5 && var5 != 6)
        {
            if (var5 == 0 || var5 == 7)
            {
                var6 = 0.25F;
                this.a(0.5F - var6, 0.4F, 0.5F - var6, 0.5F + var6, 1.0F, 0.5F + var6);
            }
        }
        else
        {
            var6 = 0.25F;
            this.a(0.5F - var6, 0.0F, 0.5F - var6, 0.5F + var6, 0.6F, 0.5F + var6);
        }
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void attack(World var1, int var2, int var3, int var4, EntityHuman var5) {}

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean interact(World var1, int var2, int var3, int var4, EntityHuman var5, int var6, float var7, float var8, float var9)
    {
        if (var1.isStatic)
        {
            return true;
        }
        else
        {
            int var10 = var1.getData(var2, var3, var4);
            int var11 = var10 & 7;
            int var12 = 8 - (var10 & 8);
            var1.setData(var2, var3, var4, var11 + var12);
            var1.e(var2, var3, var4, var2, var3, var4);
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, var12 > 0 ? 0.6F : 0.5F);
            var1.applyPhysics(var2, var3, var4, this.id);

            if (var11 == 1)
            {
                var1.applyPhysics(var2 - 1, var3, var4, this.id);
            }
            else if (var11 == 2)
            {
                var1.applyPhysics(var2 + 1, var3, var4, this.id);
            }
            else if (var11 == 3)
            {
                var1.applyPhysics(var2, var3, var4 - 1, this.id);
            }
            else if (var11 == 4)
            {
                var1.applyPhysics(var2, var3, var4 + 1, this.id);
            }
            else if (var11 != 5 && var11 != 6)
            {
                if (var11 == 0 || var11 == 7)
                {
                    var1.applyPhysics(var2, var3 + 1, var4, this.id);
                }
            }
            else
            {
                var1.applyPhysics(var2, var3 - 1, var4, this.id);
            }

            return true;
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void remove(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        if ((var6 & 8) > 0)
        {
            var1.applyPhysics(var2, var3, var4, this.id);
            int var7 = var6 & 7;

            if (var7 == 1)
            {
                var1.applyPhysics(var2 - 1, var3, var4, this.id);
            }
            else if (var7 == 2)
            {
                var1.applyPhysics(var2 + 1, var3, var4, this.id);
            }
            else if (var7 == 3)
            {
                var1.applyPhysics(var2, var3, var4 - 1, this.id);
            }
            else if (var7 == 4)
            {
                var1.applyPhysics(var2, var3, var4 + 1, this.id);
            }
            else if (var7 != 5 && var7 != 6)
            {
                if (var7 == 0 || var7 == 7)
                {
                    var1.applyPhysics(var2, var3 + 1, var4, this.id);
                }
            }
            else
            {
                var1.applyPhysics(var2, var3 - 1, var4, this.id);
            }
        }

        super.remove(var1, var2, var3, var4, var5, var6);
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean b(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        return (var1.getData(var2, var3, var4) & 8) > 0;
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean c(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        int var6 = var1.getData(var2, var3, var4);

        if ((var6 & 8) == 0)
        {
            return false;
        }
        else
        {
            int var7 = var6 & 7;
            return var7 == 0 && var5 == 0 ? true : (var7 == 7 && var5 == 0 ? true : (var7 == 6 && var5 == 1 ? true : (var7 == 5 && var5 == 1 ? true : (var7 == 4 && var5 == 2 ? true : (var7 == 3 && var5 == 3 ? true : (var7 == 2 && var5 == 4 ? true : var7 == 1 && var5 == 5))))));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean isPowerSource()
    {
        return true;
    }
}
