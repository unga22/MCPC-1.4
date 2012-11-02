package net.minecraft.server;

import java.util.Random;

public class WorldGenMegaTree extends WorldGenerator
{
    /** The base height of the tree */
    private final int a;

    /** Sets the metadata for the wood blocks used */
    private final int b;

    /** Sets the metadata for the leaves used in huge trees */
    private final int c;

    public WorldGenMegaTree(boolean var1, int var2, int var3, int var4)
    {
        super(var1);
        this.a = var2;
        this.b = var3;
        this.c = var4;
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var6 = var2.nextInt(3) + this.a;
        boolean var7 = true;

        if (var4 >= 1 && var4 + var6 + 1 <= 256)
        {
            int var8;
            int var10;
            int var11;
            int var12;

            for (var8 = var4; var8 <= var4 + 1 + var6; ++var8)
            {
                byte var9 = 2;

                if (var8 == var4)
                {
                    var9 = 1;
                }

                if (var8 >= var4 + 1 + var6 - 2)
                {
                    var9 = 2;
                }

                for (var10 = var3 - var9; var10 <= var3 + var9 && var7; ++var10)
                {
                    for (var11 = var5 - var9; var11 <= var5 + var9 && var7; ++var11)
                    {
                        if (var8 >= 0 && var8 < 256)
                        {
                            var12 = var1.getTypeId(var10, var8, var11);

                            if (var12 != 0 && var12 != Block.LEAVES.id && var12 != Block.GRASS.id && var12 != Block.DIRT.id && var12 != Block.LOG.id && var12 != Block.SAPLING.id)
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
                    var1.setRawTypeId(var3, var4 - 1, var5, Block.DIRT.id);
                    var1.setRawTypeId(var3 + 1, var4 - 1, var5, Block.DIRT.id);
                    var1.setRawTypeId(var3, var4 - 1, var5 + 1, Block.DIRT.id);
                    var1.setRawTypeId(var3 + 1, var4 - 1, var5 + 1, Block.DIRT.id);
                    this.a(var1, var3, var5, var4 + var6, 2, var2);

                    for (int var14 = var4 + var6 - 2 - var2.nextInt(4); var14 > var4 + var6 / 2; var14 -= 2 + var2.nextInt(4))
                    {
                        float var15 = var2.nextFloat() * (float)Math.PI * 2.0F;
                        var11 = var3 + (int)(0.5F + MathHelper.cos(var15) * 4.0F);
                        var12 = var5 + (int)(0.5F + MathHelper.sin(var15) * 4.0F);
                        this.a(var1, var11, var12, var14, 0, var2);

                        for (int var13 = 0; var13 < 5; ++var13)
                        {
                            var11 = var3 + (int)(1.5F + MathHelper.cos(var15) * (float)var13);
                            var12 = var5 + (int)(1.5F + MathHelper.sin(var15) * (float)var13);
                            this.setTypeAndData(var1, var11, var14 - 3 + var13 / 2, var12, Block.LOG.id, this.b);
                        }
                    }

                    for (var10 = 0; var10 < var6; ++var10)
                    {
                        var11 = var1.getTypeId(var3, var4 + var10, var5);

                        if (var11 == 0 || var11 == Block.LEAVES.id)
                        {
                            this.setTypeAndData(var1, var3, var4 + var10, var5, Block.LOG.id, this.b);

                            if (var10 > 0)
                            {
                                if (var2.nextInt(3) > 0 && var1.isEmpty(var3 - 1, var4 + var10, var5))
                                {
                                    this.setTypeAndData(var1, var3 - 1, var4 + var10, var5, Block.VINE.id, 8);
                                }

                                if (var2.nextInt(3) > 0 && var1.isEmpty(var3, var4 + var10, var5 - 1))
                                {
                                    this.setTypeAndData(var1, var3, var4 + var10, var5 - 1, Block.VINE.id, 1);
                                }
                            }
                        }

                        if (var10 < var6 - 1)
                        {
                            var11 = var1.getTypeId(var3 + 1, var4 + var10, var5);

                            if (var11 == 0 || var11 == Block.LEAVES.id)
                            {
                                this.setTypeAndData(var1, var3 + 1, var4 + var10, var5, Block.LOG.id, this.b);

                                if (var10 > 0)
                                {
                                    if (var2.nextInt(3) > 0 && var1.isEmpty(var3 + 2, var4 + var10, var5))
                                    {
                                        this.setTypeAndData(var1, var3 + 2, var4 + var10, var5, Block.VINE.id, 2);
                                    }

                                    if (var2.nextInt(3) > 0 && var1.isEmpty(var3 + 1, var4 + var10, var5 - 1))
                                    {
                                        this.setTypeAndData(var1, var3 + 1, var4 + var10, var5 - 1, Block.VINE.id, 1);
                                    }
                                }
                            }

                            var11 = var1.getTypeId(var3 + 1, var4 + var10, var5 + 1);

                            if (var11 == 0 || var11 == Block.LEAVES.id)
                            {
                                this.setTypeAndData(var1, var3 + 1, var4 + var10, var5 + 1, Block.LOG.id, this.b);

                                if (var10 > 0)
                                {
                                    if (var2.nextInt(3) > 0 && var1.isEmpty(var3 + 2, var4 + var10, var5 + 1))
                                    {
                                        this.setTypeAndData(var1, var3 + 2, var4 + var10, var5 + 1, Block.VINE.id, 2);
                                    }

                                    if (var2.nextInt(3) > 0 && var1.isEmpty(var3 + 1, var4 + var10, var5 + 2))
                                    {
                                        this.setTypeAndData(var1, var3 + 1, var4 + var10, var5 + 2, Block.VINE.id, 4);
                                    }
                                }
                            }

                            var11 = var1.getTypeId(var3, var4 + var10, var5 + 1);

                            if (var11 == 0 || var11 == Block.LEAVES.id)
                            {
                                this.setTypeAndData(var1, var3, var4 + var10, var5 + 1, Block.LOG.id, this.b);

                                if (var10 > 0)
                                {
                                    if (var2.nextInt(3) > 0 && var1.isEmpty(var3 - 1, var4 + var10, var5 + 1))
                                    {
                                        this.setTypeAndData(var1, var3 - 1, var4 + var10, var5 + 1, Block.VINE.id, 8);
                                    }

                                    if (var2.nextInt(3) > 0 && var1.isEmpty(var3, var4 + var10, var5 + 2))
                                    {
                                        this.setTypeAndData(var1, var3, var4 + var10, var5 + 2, Block.VINE.id, 4);
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

    private void a(World var1, int var2, int var3, int var4, int var5, Random var6)
    {
        byte var7 = 2;

        for (int var8 = var4 - var7; var8 <= var4; ++var8)
        {
            int var9 = var8 - var4;
            int var10 = var5 + 1 - var9;

            for (int var11 = var2 - var10; var11 <= var2 + var10 + 1; ++var11)
            {
                int var12 = var11 - var2;

                for (int var13 = var3 - var10; var13 <= var3 + var10 + 1; ++var13)
                {
                    int var14 = var13 - var3;

                    if ((var12 >= 0 || var14 >= 0 || var12 * var12 + var14 * var14 <= var10 * var10) && (var12 <= 0 && var14 <= 0 || var12 * var12 + var14 * var14 <= (var10 + 1) * (var10 + 1)) && (var6.nextInt(4) != 0 || var12 * var12 + var14 * var14 <= (var10 - 1) * (var10 - 1)) && !Block.q[var1.getTypeId(var11, var8, var13)])
                    {
                        this.setTypeAndData(var1, var11, var8, var13, Block.LEAVES.id, this.c);
                    }
                }
            }
        }
    }
}
