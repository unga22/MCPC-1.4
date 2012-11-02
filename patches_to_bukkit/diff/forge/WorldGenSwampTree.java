package net.minecraft.server;

import java.util.Random;

public class WorldGenSwampTree extends WorldGenerator
{
    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var6;

        for (var6 = var2.nextInt(4) + 5; var1.getMaterial(var3, var4 - 1, var5) == Material.WATER; --var4)
        {
            ;
        }

        boolean var7 = true;

        if (var4 >= 1 && var4 + var6 + 1 <= 128)
        {
            int var8;
            int var9;
            int var10;
            int var11;

            for (var8 = var4; var8 <= var4 + 1 + var6; ++var8)
            {
                byte var12 = 1;

                if (var8 == var4)
                {
                    var12 = 0;
                }

                if (var8 >= var4 + 1 + var6 - 2)
                {
                    var12 = 3;
                }

                for (var9 = var3 - var12; var9 <= var3 + var12 && var7; ++var9)
                {
                    for (var10 = var5 - var12; var10 <= var5 + var12 && var7; ++var10)
                    {
                        if (var8 >= 0 && var8 < 128)
                        {
                            var11 = var1.getTypeId(var9, var8, var10);

                            if (var11 != 0 && Block.byId[var11] != null && !Block.byId[var11].isLeaves(var1, var9, var8, var10))
                            {
                                if (var11 != Block.STATIONARY_WATER.id && var11 != Block.WATER.id)
                                {
                                    var7 = false;
                                }
                                else if (var8 > var4)
                                {
                                    var7 = false;
                                }
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

                if ((var8 == Block.GRASS.id || var8 == Block.DIRT.id) && var4 < 128 - var6 - 1)
                {
                    this.setType(var1, var3, var4 - 1, var5, Block.DIRT.id);
                    int var13;
                    int var17;

                    for (var13 = var4 - 3 + var6; var13 <= var4 + var6; ++var13)
                    {
                        var9 = var13 - (var4 + var6);
                        var10 = 2 - var9 / 2;

                        for (var11 = var3 - var10; var11 <= var3 + var10; ++var11)
                        {
                            var17 = var11 - var3;

                            for (int var14 = var5 - var10; var14 <= var5 + var10; ++var14)
                            {
                                int var15 = var14 - var5;
                                Block var16 = Block.byId[var1.getTypeId(var11, var13, var14)];

                                if ((Math.abs(var17) != var10 || Math.abs(var15) != var10 || var2.nextInt(2) != 0 && var9 != 0) && (var16 == null || var16.canBeReplacedByLeaves(var1, var11, var13, var14)))
                                {
                                    this.setType(var1, var11, var13, var14, Block.LEAVES.id);
                                }
                            }
                        }
                    }

                    Block var18;

                    for (var13 = 0; var13 < var6; ++var13)
                    {
                        var9 = var1.getTypeId(var3, var4 + var13, var5);
                        var18 = Block.byId[var9];

                        if (var9 == 0 || var18 != null && var18.isLeaves(var1, var3, var4 + var13, var5) || var9 == Block.WATER.id || var9 == Block.STATIONARY_WATER.id)
                        {
                            this.setType(var1, var3, var4 + var13, var5, Block.LOG.id);
                        }
                    }

                    for (var13 = var4 - 3 + var6; var13 <= var4 + var6; ++var13)
                    {
                        var9 = var13 - (var4 + var6);
                        var10 = 2 - var9 / 2;

                        for (var11 = var3 - var10; var11 <= var3 + var10; ++var11)
                        {
                            for (var17 = var5 - var10; var17 <= var5 + var10; ++var17)
                            {
                                var18 = Block.byId[var1.getTypeId(var11, var13, var17)];

                                if (var18 != null && var18.isLeaves(var1, var11, var13, var17))
                                {
                                    if (var2.nextInt(4) == 0 && var1.getTypeId(var11 - 1, var13, var17) == 0)
                                    {
                                        this.b(var1, var11 - 1, var13, var17, 8);
                                    }

                                    if (var2.nextInt(4) == 0 && var1.getTypeId(var11 + 1, var13, var17) == 0)
                                    {
                                        this.b(var1, var11 + 1, var13, var17, 2);
                                    }

                                    if (var2.nextInt(4) == 0 && var1.getTypeId(var11, var13, var17 - 1) == 0)
                                    {
                                        this.b(var1, var11, var13, var17 - 1, 1);
                                    }

                                    if (var2.nextInt(4) == 0 && var1.getTypeId(var11, var13, var17 + 1) == 0)
                                    {
                                        this.b(var1, var11, var13, var17 + 1, 4);
                                    }
                                }
                            }
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

    /**
     * Generates vines at the given position until it hits a block.
     */
    private void b(World var1, int var2, int var3, int var4, int var5)
    {
        this.setTypeAndData(var1, var2, var3, var4, Block.VINE.id, var5);
        int var6 = 4;

        while (true)
        {
            --var3;

            if (var1.getTypeId(var2, var3, var4) != 0 || var6 <= 0)
            {
                return;
            }

            this.setTypeAndData(var1, var2, var3, var4, Block.VINE.id, var5);
            --var6;
        }
    }
}
