package net.minecraft.server;

import java.util.Random;
import net.minecraftforge.common.ForgeDirection;

public class BlockTripwireHook extends Block
{
    public BlockTripwireHook(int var1)
    {
        super(var1, 172, Material.ORIENTABLE);
        this.a(CreativeModeTab.d);
        this.b(true);
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
        return 29;
    }

    /**
     * How many world ticks before ticking
     */
    public int r_()
    {
        return 10;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlace(World var1, int var2, int var3, int var4, int var5)
    {
        ForgeDirection var6 = ForgeDirection.getOrientation(var5);
        return var6 == ForgeDirection.NORTH && var1.isBlockSolidOnSide(var2, var3, var4 + 1, ForgeDirection.NORTH) || var6 == ForgeDirection.SOUTH && var1.isBlockSolidOnSide(var2, var3, var4 - 1, ForgeDirection.SOUTH) || var6 == ForgeDirection.WEST && var1.isBlockSolidOnSide(var2 + 1, var3, var4, ForgeDirection.WEST) || var6 == ForgeDirection.EAST && var1.isBlockSolidOnSide(var2 - 1, var3, var4, ForgeDirection.EAST);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var1.isBlockSolidOnSide(var2 - 1, var3, var4, ForgeDirection.SOUTH) || var1.isBlockSolidOnSide(var2 + 1, var3, var4, ForgeDirection.NORTH) || var1.isBlockSolidOnSide(var2, var3, var4 - 1, ForgeDirection.EAST) || var1.isBlockSolidOnSide(var2, var3, var4 + 1, ForgeDirection.WEST);
    }

    /**
     * called before onBlockPlacedBy by ItemBlock and ItemReed
     */
    public void postPlace(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8)
    {
        byte var9 = 0;

        if (var5 == 2 && var1.isBlockSolidOnSide(var2, var3, var4 + 1, ForgeDirection.WEST, true))
        {
            var9 = 2;
        }

        if (var5 == 3 && var1.isBlockSolidOnSide(var2, var3, var4 - 1, ForgeDirection.EAST, true))
        {
            var9 = 0;
        }

        if (var5 == 4 && var1.isBlockSolidOnSide(var2 + 1, var3, var4, ForgeDirection.NORTH, true))
        {
            var9 = 1;
        }

        if (var5 == 5 && var1.isBlockSolidOnSide(var2 - 1, var3, var4, ForgeDirection.SOUTH, true))
        {
            var9 = 3;
        }

        this.func_72143_a(var1, var2, var3, var4, this.id, var9, false, -1, 0);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (var5 != this.id && this.func_72144_l(var1, var2, var3, var4))
        {
            int var6 = var1.getData(var2, var3, var4);
            int var7 = var6 & 3;
            boolean var8 = false;

            if (!var1.isBlockSolidOnSide(var2 - 1, var3, var4, ForgeDirection.SOUTH) && var7 == 3)
            {
                var8 = true;
            }

            if (!var1.isBlockSolidOnSide(var2 + 1, var3, var4, ForgeDirection.NORTH) && var7 == 1)
            {
                var8 = true;
            }

            if (!var1.isBlockSolidOnSide(var2, var3, var4 - 1, ForgeDirection.EAST) && var7 == 0)
            {
                var8 = true;
            }

            if (!var1.isBlockSolidOnSide(var2, var3, var4 + 1, ForgeDirection.WEST) && var7 == 2)
            {
                var8 = true;
            }

            if (var8)
            {
                this.c(var1, var2, var3, var4, var6, 0);
                var1.setTypeId(var2, var3, var4, 0);
            }
        }
    }

    public void func_72143_a(World var1, int var2, int var3, int var4, int var5, int var6, boolean var7, int var8, int var9)
    {
        int var10 = var6 & 3;
        boolean var11 = (var6 & 4) == 4;
        boolean var12 = (var6 & 8) == 8;
        boolean var13 = var5 == Block.TRIPWIRE_SOURCE.id;
        boolean var14 = false;
        boolean var15 = !var1.isBlockSolidOnSide(var2, var3 - 1, var4, ForgeDirection.UP);
        int var16 = Direction.a[var10];
        int var17 = Direction.b[var10];
        int var18 = 0;
        int[] var19 = new int[42];
        int var21;
        int var20;
        int var23;
        int var22;
        int var24;

        for (var21 = 1; var21 < 42; ++var21)
        {
            var20 = var2 + var16 * var21;
            var23 = var4 + var17 * var21;
            var22 = var1.getTypeId(var20, var3, var23);

            if (var22 == Block.TRIPWIRE_SOURCE.id)
            {
                var24 = var1.getData(var20, var3, var23);

                if ((var24 & 3) == Direction.f[var10])
                {
                    var18 = var21;
                }

                break;
            }

            if (var22 != Block.TRIPWIRE.id && var21 != var8)
            {
                var19[var21] = -1;
                var13 = false;
            }
            else
            {
                var24 = var21 == var8 ? var9 : var1.getData(var20, var3, var23);
                boolean var25 = (var24 & 8) != 8;
                boolean var26 = (var24 & 1) == 1;
                boolean var27 = (var24 & 2) == 2;
                var13 &= var27 == var15;
                var14 |= var25 && var26;
                var19[var21] = var24;

                if (var21 == var8)
                {
                    var1.a(var2, var3, var4, var5, this.r_());
                    var13 &= var25;
                }
            }
        }

        var13 &= var18 > 1;
        var14 &= var13;
        var21 = (var13 ? 4 : 0) | (var14 ? 8 : 0);
        var6 = var10 | var21;

        if (var18 > 0)
        {
            var20 = var2 + var16 * var18;
            var23 = var4 + var17 * var18;
            var22 = Direction.f[var10];
            var1.setData(var20, var3, var23, var22 | var21);
            this.d(var1, var20, var3, var23, var22);
            this.a(var1, var20, var3, var23, var13, var14, var11, var12);
        }

        this.a(var1, var2, var3, var4, var13, var14, var11, var12);

        if (var5 > 0)
        {
            var1.setData(var2, var3, var4, var6);

            if (var7)
            {
                this.d(var1, var2, var3, var4, var10);
            }
        }

        if (var11 != var13)
        {
            for (var20 = 1; var20 < var18; ++var20)
            {
                var23 = var2 + var16 * var20;
                var22 = var4 + var17 * var20;
                var24 = var19[var20];

                if (var24 >= 0)
                {
                    if (var13)
                    {
                        var24 |= 4;
                    }
                    else
                    {
                        var24 &= -5;
                    }

                    var1.setData(var23, var3, var22, var24);
                }
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        this.func_72143_a(var1, var2, var3, var4, this.id, var1.getData(var2, var3, var4), true, -1, 0);
    }

    /**
     * only of the conditions are right
     */
    private void a(World var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7, boolean var8)
    {
        if (var6 && !var8)
        {
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.click", 0.4F, 0.6F);
        }
        else if (!var6 && var8)
        {
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.click", 0.4F, 0.5F);
        }
        else if (var5 && !var7)
        {
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.click", 0.4F, 0.7F);
        }
        else if (!var5 && var7)
        {
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.bowhit", 0.4F, 1.2F / (var1.random.nextFloat() * 0.2F + 0.9F));
        }
    }

    private void d(World var1, int var2, int var3, int var4, int var5)
    {
        var1.applyPhysics(var2, var3, var4, this.id);

        if (var5 == 3)
        {
            var1.applyPhysics(var2 - 1, var3, var4, this.id);
        }
        else if (var5 == 1)
        {
            var1.applyPhysics(var2 + 1, var3, var4, this.id);
        }
        else if (var5 == 0)
        {
            var1.applyPhysics(var2, var3, var4 - 1, this.id);
        }
        else if (var5 == 2)
        {
            var1.applyPhysics(var2, var3, var4 + 1, this.id);
        }
    }

    private boolean func_72144_l(World var1, int var2, int var3, int var4)
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
        int var5 = var1.getData(var2, var3, var4) & 3;
        float var6 = 0.1875F;

        if (var5 == 3)
        {
            this.a(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
        }
        else if (var5 == 1)
        {
            this.a(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
        }
        else if (var5 == 0)
        {
            this.a(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
        }
        else if (var5 == 2)
        {
            this.a(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void remove(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        boolean var7 = (var6 & 4) == 4;
        boolean var8 = (var6 & 8) == 8;

        if (var7 || var8)
        {
            this.func_72143_a(var1, var2, var3, var4, 0, var6, false, -1, 0);
        }

        if (var8)
        {
            var1.applyPhysics(var2, var3, var4, this.id);
            int var9 = var6 & 3;

            if (var9 == 3)
            {
                var1.applyPhysics(var2 - 1, var3, var4, this.id);
            }
            else if (var9 == 1)
            {
                var1.applyPhysics(var2 + 1, var3, var4, this.id);
            }
            else if (var9 == 0)
            {
                var1.applyPhysics(var2, var3, var4 - 1, this.id);
            }
            else if (var9 == 2)
            {
                var1.applyPhysics(var2, var3, var4 + 1, this.id);
            }
        }

        super.remove(var1, var2, var3, var4, var5, var6);
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean b(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        return (var1.getData(var2, var3, var4) & 8) == 8;
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean c(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        int var6 = var1.getData(var2, var3, var4);

        if ((var6 & 8) != 8)
        {
            return false;
        }
        else
        {
            int var7 = var6 & 3;
            return var7 == 2 && var5 == 2 ? true : (var7 == 0 && var5 == 3 ? true : (var7 == 1 && var5 == 4 ? true : var7 == 3 && var5 == 5));
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
