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
            int var11;
            int var12;
            int var13;

            for (var9 = var4; var9 <= var4 + 1 + var7; ++var9)
            {
                byte var10 = 3;

                if (var9 <= var4 + 3)
                {
                    var10 = 0;
                }

                for (var11 = var3 - var10; var11 <= var3 + var10 && var8; ++var11)
                {
                    for (var12 = var5 - var10; var12 <= var5 + var10 && var8; ++var12)
                    {
                        if (var9 >= 0 && var9 < 256)
                        {
                            var13 = var1.getTypeId(var11, var9, var12);

                            if (var13 != 0 && var13 != Block.LEAVES.id)
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
                    int var16 = var4 + var7;

                    if (var6 == 1)
                    {
                        var16 = var4 + var7 - 3;
                    }

                    for (var11 = var16; var11 <= var4 + var7; ++var11)
                    {
                        var12 = 1;

                        if (var11 < var4 + var7)
                        {
                            ++var12;
                        }

                        if (var6 == 0)
                        {
                            var12 = 3;
                        }

                        for (var13 = var3 - var12; var13 <= var3 + var12; ++var13)
                        {
                            for (int var14 = var5 - var12; var14 <= var5 + var12; ++var14)
                            {
                                int var15 = 5;

                                if (var13 == var3 - var12)
                                {
                                    --var15;
                                }

                                if (var13 == var3 + var12)
                                {
                                    ++var15;
                                }

                                if (var14 == var5 - var12)
                                {
                                    var15 -= 3;
                                }

                                if (var14 == var5 + var12)
                                {
                                    var15 += 3;
                                }

                                if (var6 == 0 || var11 < var4 + var7)
                                {
                                    if ((var13 == var3 - var12 || var13 == var3 + var12) && (var14 == var5 - var12 || var14 == var5 + var12))
                                    {
                                        continue;
                                    }

                                    if (var13 == var3 - (var12 - 1) && var14 == var5 - var12)
                                    {
                                        var15 = 1;
                                    }

                                    if (var13 == var3 - var12 && var14 == var5 - (var12 - 1))
                                    {
                                        var15 = 1;
                                    }

                                    if (var13 == var3 + (var12 - 1) && var14 == var5 - var12)
                                    {
                                        var15 = 3;
                                    }

                                    if (var13 == var3 + var12 && var14 == var5 - (var12 - 1))
                                    {
                                        var15 = 3;
                                    }

                                    if (var13 == var3 - (var12 - 1) && var14 == var5 + var12)
                                    {
                                        var15 = 7;
                                    }

                                    if (var13 == var3 - var12 && var14 == var5 + (var12 - 1))
                                    {
                                        var15 = 7;
                                    }

                                    if (var13 == var3 + (var12 - 1) && var14 == var5 + var12)
                                    {
                                        var15 = 9;
                                    }

                                    if (var13 == var3 + var12 && var14 == var5 + (var12 - 1))
                                    {
                                        var15 = 9;
                                    }
                                }

                                if (var15 == 5 && var11 < var4 + var7)
                                {
                                    var15 = 0;
                                }

                                if ((var15 != 0 || var4 >= var4 + var7 - 1) && !Block.q[var1.getTypeId(var13, var11, var14)])
                                {
                                    this.setTypeAndData(var1, var13, var11, var14, Block.BIG_MUSHROOM_1.id + var6, var15);
                                }
                            }
                        }
                    }

                    for (var11 = 0; var11 < var7; ++var11)
                    {
                        var12 = var1.getTypeId(var3, var4 + var11, var5);

                        if (!Block.q[var12])
                        {
                            this.setTypeAndData(var1, var3, var4 + var11, var5, Block.BIG_MUSHROOM_1.id + var6, 10);
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
