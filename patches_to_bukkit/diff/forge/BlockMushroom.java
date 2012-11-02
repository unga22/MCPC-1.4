package net.minecraft.server;

import java.util.Random;
import net.minecraftforge.common.ForgeDirection;

public class BlockMushroom extends BlockFlower
{
    protected BlockMushroom(int var1, int var2)
    {
        super(var1, var2);
        float var3 = 0.2F;
        this.a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var3 * 2.0F, 0.5F + var3);
        this.b(true);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (var5.nextInt(25) == 0)
        {
            byte var6 = 4;
            int var7 = 5;
            int var8;
            int var9;
            int var10;

            for (var8 = var2 - var6; var8 <= var2 + var6; ++var8)
            {
                for (var9 = var4 - var6; var9 <= var4 + var6; ++var9)
                {
                    for (var10 = var3 - 1; var10 <= var3 + 1; ++var10)
                    {
                        if (var1.getTypeId(var8, var10, var9) == this.id)
                        {
                            --var7;

                            if (var7 <= 0)
                            {
                                return;
                            }
                        }
                    }
                }
            }

            var8 = var2 + var5.nextInt(3) - 1;
            var9 = var3 + var5.nextInt(2) - var5.nextInt(2);
            var10 = var4 + var5.nextInt(3) - 1;

            for (int var11 = 0; var11 < 4; ++var11)
            {
                if (var1.isEmpty(var8, var9, var10) && this.d(var1, var8, var9, var10))
                {
                    var2 = var8;
                    var3 = var9;
                    var4 = var10;
                }

                var8 = var2 + var5.nextInt(3) - 1;
                var9 = var3 + var5.nextInt(2) - var5.nextInt(2);
                var10 = var4 + var5.nextInt(3) - 1;
            }

            if (var1.isEmpty(var8, var9, var10) && this.d(var1, var8, var9, var10))
            {
                var1.setTypeId(var8, var9, var10, this.id);
            }
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return super.canPlace(var1, var2, var3, var4) && this.d(var1, var2, var3, var4);
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean d_(int var1)
    {
        return Block.q[var1];
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean d(World var1, int var2, int var3, int var4)
    {
        if (var3 >= 0 && var3 < 256)
        {
            int var5 = var1.getTypeId(var2, var3 - 1, var4);
            Block var6 = Block.byId[var5];
            return (var5 == Block.MYCEL.id || var1.k(var2, var3, var4) < 13) && var6 != null && var6.canSustainPlant(var1, var2, var3 - 1, var4, ForgeDirection.UP, this);
        }
        else
        {
            return false;
        }
    }

    /**
     * Fertilize the mushroom.
     */
    public boolean grow(World var1, int var2, int var3, int var4, Random var5)
    {
        int var6 = var1.getData(var2, var3, var4);
        var1.setRawTypeId(var2, var3, var4, 0);
        WorldGenHugeMushroom var7 = null;

        if (this.id == Block.BROWN_MUSHROOM.id)
        {
            var7 = new WorldGenHugeMushroom(0);
        }
        else if (this.id == Block.RED_MUSHROOM.id)
        {
            var7 = new WorldGenHugeMushroom(1);
        }

        if (var7 != null && var7.a(var1, var5, var2, var3, var4))
        {
            return true;
        }
        else
        {
            var1.setRawTypeIdAndData(var2, var3, var4, this.id, var6);
            return false;
        }
    }
}
