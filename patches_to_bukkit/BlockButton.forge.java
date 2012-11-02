package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class BlockButton extends Block
{
    /** Whether this button is sensible to arrows, used by wooden buttons. */
    private final boolean a;

    protected BlockButton(int var1, int var2, boolean var3)
    {
        super(var1, var2, Material.ORIENTABLE);
        this.b(true);
        this.a(CreativeModeTab.d);
        this.a = var3;
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
     * How many world ticks before ticking
     */
    public int r_()
    {
        return this.a ? 30 : 20;
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
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlace(World var1, int var2, int var3, int var4, int var5)
    {
        return var5 == 2 && var1.s(var2, var3, var4 + 1) ? true : (var5 == 3 && var1.s(var2, var3, var4 - 1) ? true : (var5 == 4 && var1.s(var2 + 1, var3, var4) ? true : var5 == 5 && var1.s(var2 - 1, var3, var4)));
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var1.s(var2 - 1, var3, var4) ? true : (var1.s(var2 + 1, var3, var4) ? true : (var1.s(var2, var3, var4 - 1) ? true : var1.s(var2, var3, var4 + 1)));
    }

    /**
     * called before onBlockPlacedBy by ItemBlock and ItemReed
     */
    public void postPlace(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8)
    {
        int var9 = var1.getData(var2, var3, var4);
        int var10 = var9 & 8;
        var9 &= 7;

        if (var5 == 2 && var1.s(var2, var3, var4 + 1))
        {
            var9 = 4;
        }
        else if (var5 == 3 && var1.s(var2, var3, var4 - 1))
        {
            var9 = 3;
        }
        else if (var5 == 4 && var1.s(var2 + 1, var3, var4))
        {
            var9 = 2;
        }
        else if (var5 == 5 && var1.s(var2 - 1, var3, var4))
        {
            var9 = 1;
        }
        else
        {
            var9 = this.l(var1, var2, var3, var4);
        }

        var1.setData(var2, var3, var4, var9 + var10);
    }

    /**
     * Get side which this button is facing.
     */
    private int l(World var1, int var2, int var3, int var4)
    {
        return var1.s(var2 - 1, var3, var4) ? 1 : (var1.s(var2 + 1, var3, var4) ? 2 : (var1.s(var2, var3, var4 - 1) ? 3 : (var1.s(var2, var3, var4 + 1) ? 4 : 1)));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (this.n(var1, var2, var3, var4))
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

            if (var7)
            {
                this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
                var1.setTypeId(var2, var3, var4, 0);
            }
        }
    }

    /**
     * This method is redundant, check it out...
     */
    private boolean n(World var1, int var2, int var3, int var4)
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
        int var5 = var1.getData(var2, var3, var4);
        this.func_82534_e(var5);
    }

    private void func_82534_e(int var1)
    {
        int var2 = var1 & 7;
        boolean var3 = (var1 & 8) > 0;
        float var4 = 0.375F;
        float var5 = 0.625F;
        float var6 = 0.1875F;
        float var7 = 0.125F;

        if (var3)
        {
            var7 = 0.0625F;
        }

        if (var2 == 1)
        {
            this.a(0.0F, var4, 0.5F - var6, var7, var5, 0.5F + var6);
        }
        else if (var2 == 2)
        {
            this.a(1.0F - var7, var4, 0.5F - var6, 1.0F, var5, 0.5F + var6);
        }
        else if (var2 == 3)
        {
            this.a(0.5F - var6, var4, 0.0F, 0.5F + var6, var5, var7);
        }
        else if (var2 == 4)
        {
            this.a(0.5F - var6, var4, 1.0F - var7, 0.5F + var6, var5, 1.0F);
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
        int var10 = var1.getData(var2, var3, var4);
        int var11 = var10 & 7;
        int var12 = 8 - (var10 & 8);

        if (var12 == 0)
        {
            return true;
        }
        else
        {
            var1.setData(var2, var3, var4, var11 + var12);
            var1.e(var2, var3, var4, var2, var3, var4);
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.6F);
            this.func_82536_d(var1, var2, var3, var4, var11);
            var1.a(var2, var3, var4, this.id, this.r_());
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
            int var7 = var6 & 7;
            this.func_82536_d(var1, var2, var3, var4, var7);
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
            return var7 == 5 && var5 == 1 ? true : (var7 == 4 && var5 == 2 ? true : (var7 == 3 && var5 == 3 ? true : (var7 == 2 && var5 == 4 ? true : var7 == 1 && var5 == 5)));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean isPowerSource()
    {
        return true;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (!var1.isStatic)
        {
            int var6 = var1.getData(var2, var3, var4);

            if ((var6 & 8) != 0)
            {
                if (this.a)
                {
                    this.func_82535_o(var1, var2, var3, var4);
                }
                else
                {
                    var1.setData(var2, var3, var4, var6 & 7);
                    int var7 = var6 & 7;
                    this.func_82536_d(var1, var2, var3, var4, var7);
                    var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.5F);
                    var1.e(var2, var3, var4, var2, var3, var4);
                }
            }
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void f()
    {
        float var1 = 0.1875F;
        float var2 = 0.125F;
        float var3 = 0.125F;
        this.a(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void a(World var1, int var2, int var3, int var4, Entity var5)
    {
        if (!var1.isStatic)
        {
            if (this.a)
            {
                if ((var1.getData(var2, var3, var4) & 8) == 0)
                {
                    this.func_82535_o(var1, var2, var3, var4);
                }
            }
        }
    }

    private void func_82535_o(World var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);
        int var6 = var5 & 7;
        boolean var7 = (var5 & 8) != 0;
        this.func_82534_e(var5);
        List var9 = var1.a(EntityArrow.class, AxisAlignedBB.a().a((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)var3 + this.maxY, (double)var4 + this.maxZ));
        boolean var8 = !var9.isEmpty();

        if (var8 && !var7)
        {
            var1.setData(var2, var3, var4, var6 | 8);
            this.func_82536_d(var1, var2, var3, var4, var6);
            var1.e(var2, var3, var4, var2, var3, var4);
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (!var8 && var7)
        {
            var1.setData(var2, var3, var4, var6);
            this.func_82536_d(var1, var2, var3, var4, var6);
            var1.e(var2, var3, var4, var2, var3, var4);
            var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.5F);
        }

        if (var8)
        {
            var1.a(var2, var3, var4, this.id, this.r_());
        }
    }

    private void func_82536_d(World var1, int var2, int var3, int var4, int var5)
    {
        var1.applyPhysics(var2, var3, var4, this.id);

        if (var5 == 1)
        {
            var1.applyPhysics(var2 - 1, var3, var4, this.id);
        }
        else if (var5 == 2)
        {
            var1.applyPhysics(var2 + 1, var3, var4, this.id);
        }
        else if (var5 == 3)
        {
            var1.applyPhysics(var2, var3, var4 - 1, this.id);
        }
        else if (var5 == 4)
        {
            var1.applyPhysics(var2, var3, var4 + 1, this.id);
        }
        else
        {
            var1.applyPhysics(var2, var3 - 1, var4, this.id);
        }
    }
}
