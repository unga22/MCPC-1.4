package net.minecraft.server;

import java.util.Random;

public class BlockNetherWart extends BlockFlower
{
    protected BlockNetherWart(int var1)
    {
        super(var1, 226);
        this.b(true);
        float var2 = 0.5F;
        this.a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
        this.a((CreativeModeTab)null);
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean d_(int var1)
    {
        return var1 == Block.SOUL_SAND.id;
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean d(World var1, int var2, int var3, int var4)
    {
        return this.d_(var1.getTypeId(var2, var3 - 1, var4));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        int var6 = var1.getData(var2, var3, var4);

        if (var6 < 3 && var5.nextInt(10) == 0)
        {
            ++var6;
            var1.setData(var2, var3, var4, var6);
        }

        super.b(var1, var2, var3, var4, var5);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        return var2 >= 3 ? this.textureId + 2 : (var2 > 0 ? this.textureId + 1 : this.textureId);
    }

    /**
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 6;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropNaturally(World var1, int var2, int var3, int var4, int var5, float var6, int var7)
    {
        if (!var1.isStatic)
        {
            int var8 = 1;

            if (var5 >= 3)
            {
                var8 = 2 + var1.random.nextInt(3);

                if (var7 > 0)
                {
                    var8 += var1.random.nextInt(var7 + 1);
                }
            }

            for (int var9 = 0; var9 < var8; ++var9)
            {
                this.a(var1, var2, var3, var4, new ItemStack(Item.NETHER_STALK));
            }
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
}
