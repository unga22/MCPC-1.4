package net.minecraft.server;

import java.util.Random;

public class WorldGenTaiga1 extends WorldGenerator
{
    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var6 = var2.nextInt(5) + 7;
        int var7 = var6 - var2.nextInt(2) - 3;
        int var8 = var6 - var7;
        int var9 = 1 + var2.nextInt(var8 + 1);
        boolean var10 = true;

        if (var4 >= 1 && var4 + var6 + 1 <= 128)
        {
            int var11;
            int var12;
            int var13;
            int var14;
            int var15;

            for (var11 = var4; var11 <= var4 + 1 + var6 && var10; ++var11)
            {
                boolean var16 = true;

                if (var11 - var4 < var7)
                {
                    var15 = 0;
                }
                else
                {
                    var15 = var9;
                }

                for (var12 = var3 - var15; var12 <= var3 + var15 && var10; ++var12)
                {
                    for (var13 = var5 - var15; var13 <= var5 + var15 && var10; ++var13)
                    {
                        if (var11 >= 0 && var11 < 128)
                        {
                            var14 = var1.getTypeId(var12, var11, var13);
                            Block var17 = Block.byId[var14];

                            if (var14 != 0 && (var17 == null || !var17.isLeaves(var1, var12, var11, var13)))
                            {
                                var10 = false;
                            }
                        }
                        else
                        {
                            var10 = false;
                        }
                    }
                }
            }

            if (!var10)
            {
                return false;
            }
            else
            {
                var11 = var1.getTypeId(var3, var4 - 1, var5);

                if ((var11 == Block.GRASS.id || var11 == Block.DIRT.id) && var4 < 128 - var6 - 1)
                {
                    this.setType(var1, var3, var4 - 1, var5, Block.DIRT.id);
                    var15 = 0;

                    for (var12 = var4 + var6; var12 >= var4 + var7; --var12)
                    {
                        for (var13 = var3 - var15; var13 <= var3 + var15; ++var13)
                        {
                            var14 = var13 - var3;

                            for (int var19 = var5 - var15; var19 <= var5 + var15; ++var19)
                            {
                                int var20 = var19 - var5;
                                Block var18 = Block.byId[var1.getTypeId(var13, var12, var19)];

                                if ((Math.abs(var14) != var15 || Math.abs(var20) != var15 || var15 <= 0) && (var18 == null || var18.canBeReplacedByLeaves(var1, var13, var12, var19)))
                                {
                                    this.setTypeAndData(var1, var13, var12, var19, Block.LEAVES.id, 1);
                                }
                            }
                        }

                        if (var15 >= 1 && var12 == var4 + var7 + 1)
                        {
                            --var15;
                        }
                        else if (var15 < var9)
                        {
                            ++var15;
                        }
                    }

                    for (var12 = 0; var12 < var6 - 1; ++var12)
                    {
                        var13 = var1.getTypeId(var3, var4 + var12, var5);
                        Block var21 = Block.byId[var13];

                        if (var13 == 0 || var21 == null || var21.isLeaves(var1, var3, var4 + var12, var5))
                        {
                            this.setTypeAndData(var1, var3, var4 + var12, var5, Block.LOG.id, 1);
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
