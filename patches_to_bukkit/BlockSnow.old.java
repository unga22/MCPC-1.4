package net.minecraft.server;

import java.util.Random;

public class BlockSnow extends Block
{
    protected BlockSnow(int var1, int var2)
    {
        super(var1, var2, Material.SNOW_LAYER);
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.b(true);
        this.a(CreativeModeTab.c);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4) & 7;
        return var5 >= 3 ? AxisAlignedBB.a().a((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)((float)var3 + 0.5F), (double)var4 + this.maxZ) : null;
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
        int var5 = var1.getData(var2, var3, var4) & 7;
        float var6 = (float)(2 * (1 + var5)) / 16.0F;
        this.a(0.0F, 0.0F, 0.0F, 1.0F, var6, 1.0F);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        int var5 = var1.getTypeId(var2, var3 - 1, var4);
        return var5 != 0 && (var5 == Block.LEAVES.id || Block.byId[var5].c()) ? var1.getMaterial(var2, var3 - 1, var4).isSolid() : false;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        this.n(var1, var2, var3, var4);
    }

    /**
     * Checks if this snow block can stay at this location.
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
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void a(World var1, EntityHuman var2, int var3, int var4, int var5, int var6)
    {
        int var7 = Item.SNOW_BALL.id;
        this.a(var1, var3, var4, var5, new ItemStack(var7, 1, 0));
        var1.setTypeId(var3, var4, var5, 0);
        var2.a(StatisticList.C[this.id], 1);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return Item.SNOW_BALL.id;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int a(Random var1)
    {
        return 0;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (var1.b(EnumSkyBlock.Block, var2, var3, var4) > 11)
        {
            this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
            var1.setTypeId(var2, var3, var4, 0);
        }
    }
}
