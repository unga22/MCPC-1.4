package net.minecraft.server;

import java.util.Random;

public class WorldGenHugeMushroom extends WorldGenerator
{
    /** The mushroom type. 0 for brown, 1 for red. */
    private int a = -1;

    public WorldGenHugeMushroom(int var1)
    {
        super(true);
        this.a = var1;
    }

    public WorldGenHugeMushroom()
    {
        super(false);
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var6 = var2.nextInt(2);

        if (this.a >= 0)
        {
            var6 = this.a;
        }

        int var7 = var2.nextInt(3) + 4;
        boolean var8 = true;

        if (var4 >= 1 && var4 + var7 + 1 < 256)
        {
            int var9;
            int var10;
            int var11;
            int var12;
            Block var14;

            for (var9 = var4; var9 <= var4 + 1 + var7; ++var9)
            {
                byte var13 = 3;

                if (var9 <= var4 + 3)
                {
                    var13 = 0;
                }

                for (var10 = var3 - var13; var10 <= var3 + var13 && var8; ++var10)
                {
                    for (var11 = var5 - var13; var11 <= var5 + var13 && var8; ++var11)
                    {
                        if (var9 >= 0 && var9 < 256)
                        {
                            var12 = var1.getTypeId(var10, var9, var11);
                            var14 = Block.byId[var12];

                            if (var12 != 0 && var14 != null && !var14.isLeaves(var1, var10, var9, var11))
                            {
                                var8 = false;
                            }
                        }
                        else
                        {
                            var8 = false;
                        }
                    }
                }
            }

            if (!var8)
            {
                return false;
            }
            else
            {
                var9 = var1.getTypeId(var3, var4 - 1, var5);

                if (var9 != Block.DIRT.id && var9 != Block.GRASS.id && var9 != Block.MYCEL.id)
                {
                    return false;
                }
                else
                {
                    int var17 = var4 + var7;

                    if (var6 == 1)
                    {
                        var17 = var4 + var7 - 3;
                    }

                    for (var10 = var17; var10 <= var4 + var7; ++var10)
                    {
                        var11 = 1;

                        if (var10 < var4 + var7)
                        {
                            ++var11;
                        }

                        if (var6 == 0)
                        {
                            var11 = 3;
                        }

                        for (var12 = var3 - var11; var12 <= var3 + var11; ++var12)
                        {
                            for (int var18 = var5 - var11; var18 <= var5 + var11; ++var18)
                            {
                                int var15 = 5;

                                if (var12 == var3 - var11)
                                {
                                    --var15;
                                }

                                if (var12 == var3 + var11)
                                {
                                    ++var15;
                                }

                                if (var18 == var5 - var11)
                                {
                                    var15 -= 3;
                                }

                                if (var18 == var5 + var11)
                                {
                                    var15 += 3;
                                }

                                if (var6 == 0 || var10 < var4 + var7)
                                {
                                    if ((var12 == var3 - var11 || var12 == var3 + var11) && (var18 == var5 - var11 || var18 == var5 + var11))
                                    {
                                        continue;
                                    }

                                    if (var12 == var3 - (var11 - 1) && var18 == var5 - var11)
                                    {
                                        var15 = 1;
                                    }

                                    if (var12 == var3 - var11 && var18 == var5 - (var11 - 1))
                                    {
                                        var15 = 1;
                                    }

                                    if (var12 == var3 + (var11 - 1) && var18 == var5 - var11)
                                    {
                                        var15 = 3;
                                    }

                                    if (var12 == var3 + var11 && var18 == var5 - (var11 - 1))
                                    {
                                        var15 = 3;
                                    }

                                    if (var12 == var3 - (var11 - 1) && var18 == var5 + var11)
                                    {
                                        var15 = 7;
                                    }

                                    if (var12 == var3 - var11 && var18 == var5 + (var11 - 1))
                                    {
                                        var15 = 7;
                                    }

                                    if (var12 == var3 + (var11 - 1) && var18 == var5 + var11)
                                    {
                                        var15 = 9;
                                    }

                                    if (var12 == var3 + var11 && var18 == var5 + (var11 - 1))
                                    {
                                        var15 = 9;
                                    }
                                }

                                if (var15 == 5 && var10 < var4 + var7)
                                {
                                    var15 = 0;
                                }

                                Block var16 = Block.byId[var1.getTypeId(var12, var10, var18)];

                                if ((var15 != 0 || var4 >= var4 + var7 - 1) && (var16 == null || var16.canBeReplacedByLeaves(var1, var12, var10, var18)))
                                {
                                    this.setTypeAndData(var1, var12, var10, var18, Block.BIG_MUSHROOM_1.id + var6, var15);
                                }
                            }
                        }
                    }

                    for (var10 = 0; var10 < var7; ++var10)
                    {
                        var11 = var1.getTypeId(var3, var4 + var10, var5);
                        var14 = Block.byId[var11];

                        if (var14 == null || var14.canBeReplacedByLeaves(var1, var3, var4 + var10, var5))
                        {
                            this.setTypeAndData(var1, var3, var4 + var10, var5, Block.BIG_MUSHROOM_1.id + var6, 10);
                        }
                    }

                    return true;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
