package net.minecraft.server;

import java.util.Random;

public class WorldGenForest extends WorldGenerator
{
    public WorldGenForest(boolean var1)
    {
        super(var1);
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var6 = var2.nextInt(3) + 5;
        boolean var7 = true;

        if (var4 >= 1 && var4 + var6 + 1 <= 256)
        {
            int var8;
            int var9;
            int var10;
            int var11;
            Block var13;

            for (var8 = var4; var8 <= var4 + 1 + var6; ++var8)
            {
                byte var12 = 1;

                if (var8 == var4)
                {
                    var12 = 0;
                }

                if (var8 >= var4 + 1 + var6 - 2)
                {
                    var12 = 2;
                }

                for (var9 = var3 - var12; var9 <= var3 + var12 && var7; ++var9)
                {
                    for (var10 = var5 - var12; var10 <= var5 + var12 && var7; ++var10)
                    {
                        if (var8 >= 0 && var8 < 256)
                        {
                            var11 = var1.getTypeId(var9, var8, var10);
                            var13 = Block.byId[var11];

                            if (var11 != 0 && var13 != null && !var13.isLeaves(var1, var9, var8, var10))
                            {
                                var7 = false;
                            }
                        }
                        else
                        {
                            var7 = false;
                        }
                    }
                }
            }

            if (!var7)
            {
                return false;
            }
            else
            {
                var8 = var1.getTypeId(var3, var4 - 1, var5);

                if ((var8 == Block.GRASS.id || var8 == Block.DIRT.id) && var4 < 256 - var6 - 1)
                {
                    this.setType(var1, var3, var4 - 1, var5, Block.DIRT.id);
                    int var17;

                    for (var17 = var4 - 3 + var6; var17 <= var4 + var6; ++var17)
                    {
                        var9 = var17 - (var4 + var6);
                        var10 = 1 - var9 / 2;

                        for (var11 = var3 - var10; var11 <= var3 + var10; ++var11)
                        {
                            int var18 = var11 - var3;

                            for (int var14 = var5 - var10; var14 <= var5 + var10; ++var14)
                            {
                                int var15 = var14 - var5;
                                Block var16 = Block.byId[var1.getTypeId(var11, var17, var14)];

                                if ((Math.abs(var18) != var10 || Math.abs(var15) != var10 || var2.nextInt(2) != 0 && var9 != 0) && (var16 == null || var16.canBeReplacedByLeaves(var1, var11, var17, var14)))
                                {
                                    this.setTypeAndData(var1, var11, var17, var14, Block.LEAVES.id, 2);
                                }
                            }
                        }
                    }

                    for (var17 = 0; var17 < var6; ++var17)
                    {
                        var9 = var1.getTypeId(var3, var4 + var17, var5);
                        var13 = Block.byId[var9];

                        if (var9 == 0 || var13 == null || var13.isLeaves(var1, var3, var4 + var17, var5))
                        {
                            this.setTypeAndData(var1, var3, var4 + var17, var5, Block.LOG.id, 2);
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
