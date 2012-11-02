package net.minecraft.server;

import java.util.Random;

public class WorldGenTaiga2 extends WorldGenerator
{
    public WorldGenTaiga2(boolean var1)
    {
        super(var1);
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var6 = var2.nextInt(4) + 6;
        int var7 = 1 + var2.nextInt(2);
        int var8 = var6 - var7;
        int var9 = 2 + var2.nextInt(2);
        boolean var10 = true;

        if (var4 >= 1 && var4 + var6 + 1 <= 256)
        {
            int var11;
            int var12;
            int var13;
            int var14;
            int var16;

            for (var11 = var4; var11 <= var4 + 1 + var6 && var10; ++var11)
            {
                boolean var15 = true;

                if (var11 - var4 < var7)
                {
                    var14 = 0;
                }
                else
                {
                    var14 = var9;
                }

                for (var12 = var3 - var14; var12 <= var3 + var14 && var10; ++var12)
                {
                    for (var16 = var5 - var14; var16 <= var5 + var14 && var10; ++var16)
                    {
                        if (var11 >= 0 && var11 < 256)
                        {
                            var13 = var1.getTypeId(var12, var11, var16);
                            Block var17 = Block.byId[var13];

                            if (var13 != 0 && var17 != null && !var17.isLeaves(var1, var12, var11, var16))
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

                if ((var11 == Block.GRASS.id || var11 == Block.DIRT.id) && var4 < 256 - var6 - 1)
                {
                    this.setType(var1, var3, var4 - 1, var5, Block.DIRT.id);
                    var14 = var2.nextInt(2);
                    var12 = 1;
                    byte var22 = 0;
                    int var23;

                    for (var13 = 0; var13 <= var8; ++var13)
                    {
                        var23 = var4 + var6 - var13;

                        for (var16 = var3 - var14; var16 <= var3 + var14; ++var16)
                        {
                            int var18 = var16 - var3;

                            for (int var19 = var5 - var14; var19 <= var5 + var14; ++var19)
                            {
                                int var20 = var19 - var5;
                                Block var21 = Block.byId[var1.getTypeId(var16, var23, var19)];

                                if ((Math.abs(var18) != var14 || Math.abs(var20) != var14 || var14 <= 0) && (var21 == null || var21.canBeReplacedByLeaves(var1, var16, var23, var19)))
                                {
                                    this.setTypeAndData(var1, var16, var23, var19, Block.LEAVES.id, 1);
                                }
                            }
                        }

                        if (var14 >= var12)
                        {
                            var14 = var22;
                            var22 = 1;
                            ++var12;

                            if (var12 > var9)
                            {
                                var12 = var9;
                            }
                        }
                        else
                        {
                            ++var14;
                        }
                    }

                    var13 = var2.nextInt(3);

                    for (var23 = 0; var23 < var6 - var13; ++var23)
                    {
                        var16 = var1.getTypeId(var3, var4 + var23, var5);
                        Block var24 = Block.byId[var16];

                        if (var16 == 0 || var24 == null || var24.isLeaves(var1, var3, var4 + var23, var5))
                        {
                            this.setTypeAndData(var1, var3, var4 + var23, var5, Block.LOG.id, 1);
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
