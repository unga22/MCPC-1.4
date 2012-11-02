package net.minecraft.server;

import java.util.Random;

public class WorldGenTrees extends WorldGenerator
{
    /** The minimum height of a generated tree. */
    private final int a;

    /** True if this tree should grow Vines. */
    private final boolean b;

    /** The metadata value of the wood to use in tree generation. */
    private final int c;

    /** The metadata value of the leaves to use in tree generation. */
    private final int d;

    public WorldGenTrees(boolean var1)
    {
        this(var1, 4, 0, 0, false);
    }

    public WorldGenTrees(boolean var1, int var2, int var3, int var4, boolean var5)
    {
        super(var1);
        this.a = var2;
        this.c = var3;
        this.d = var4;
        this.b = var5;
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var6 = var2.nextInt(3) + this.a;
        boolean var7 = true;

        if (var4 >= 1 && var4 + var6 + 1 <= 256)
        {
            int var8;
            byte var9;
            int var10;
            int var11;

            for (var8 = var4; var8 <= var4 + 1 + var6; ++var8)
            {
                var9 = 1;

                if (var8 == var4)
                {
                    var9 = 0;
                }

                if (var8 >= var4 + 1 + var6 - 2)
                {
                    var9 = 2;
                }

                for (int var12 = var3 - var9; var12 <= var3 + var9 && var7; ++var12)
                {
                    for (var10 = var5 - var9; var10 <= var5 + var9 && var7; ++var10)
                    {
                        if (var8 >= 0 && var8 < 256)
                        {
                            var11 = var1.getTypeId(var12, var8, var10);
                            Block var13 = Block.byId[var11];

                            if (var11 != 0 && !var13.isLeaves(var1, var12, var8, var10) && var11 != Block.GRASS.id && var11 != Block.DIRT.id && !var13.isWood(var1, var12, var8, var10))
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
                    var9 = 3;
                    byte var19 = 0;
                    int var14;
                    int var15;
                    int var20;

                    for (var10 = var4 - var9 + var6; var10 <= var4 + var6; ++var10)
                    {
                        var11 = var10 - (var4 + var6);
                        var20 = var19 + 1 - var11 / 2;

                        for (var14 = var3 - var20; var14 <= var3 + var20; ++var14)
                        {
                            var15 = var14 - var3;

                            for (int var16 = var5 - var20; var16 <= var5 + var20; ++var16)
                            {
                                int var17 = var16 - var5;
                                Block var18 = Block.byId[var1.getTypeId(var14, var10, var16)];

                                if ((Math.abs(var15) != var20 || Math.abs(var17) != var20 || var2.nextInt(2) != 0 && var11 != 0) && (var18 == null || var18.canBeReplacedByLeaves(var1, var14, var10, var16)))
                                {
                                    this.setTypeAndData(var1, var14, var10, var16, Block.LEAVES.id, this.d);
                                }
                            }
                        }
                    }

                    Block var21;

                    for (var10 = 0; var10 < var6; ++var10)
                    {
                        var11 = var1.getTypeId(var3, var4 + var10, var5);
                        var21 = Block.byId[var11];

                        if (var11 == 0 || var21 == null || var21.isLeaves(var1, var3, var4 + var10, var5))
                        {
                            this.setTypeAndData(var1, var3, var4 + var10, var5, Block.LOG.id, this.c);

                            if (this.b && var10 > 0)
                            {
                                if (var2.nextInt(3) > 0 && var1.isEmpty(var3 - 1, var4 + var10, var5))
                                {
                                    this.setTypeAndData(var1, var3 - 1, var4 + var10, var5, Block.VINE.id, 8);
                                }

                                if (var2.nextInt(3) > 0 && var1.isEmpty(var3 + 1, var4 + var10, var5))
                                {
                                    this.setTypeAndData(var1, var3 + 1, var4 + var10, var5, Block.VINE.id, 2);
                                }

                                if (var2.nextInt(3) > 0 && var1.isEmpty(var3, var4 + var10, var5 - 1))
                                {
                                    this.setTypeAndData(var1, var3, var4 + var10, var5 - 1, Block.VINE.id, 1);
                                }

                                if (var2.nextInt(3) > 0 && var1.isEmpty(var3, var4 + var10, var5 + 1))
                                {
                                    this.setTypeAndData(var1, var3, var4 + var10, var5 + 1, Block.VINE.id, 4);
                                }
                            }
                        }
                    }

                    if (this.b)
                    {
                        for (var10 = var4 - 3 + var6; var10 <= var4 + var6; ++var10)
                        {
                            var11 = var10 - (var4 + var6);
                            var20 = 2 - var11 / 2;

                            for (var14 = var3 - var20; var14 <= var3 + var20; ++var14)
                            {
                                for (var15 = var5 - var20; var15 <= var5 + var20; ++var15)
                                {
                                    var21 = Block.byId[var1.getTypeId(var14, var10, var15)];

                                    if (var21 != null && var21.isLeaves(var1, var14, var10, var15))
                                    {
                                        if (var2.nextInt(4) == 0 && var1.getTypeId(var14 - 1, var10, var15) == 0)
                                        {
                                            this.b(var1, var14 - 1, var10, var15, 8);
                                        }

                                        if (var2.nextInt(4) == 0 && var1.getTypeId(var14 + 1, var10, var15) == 0)
                                        {
                                            this.b(var1, var14 + 1, var10, var15, 2);
                                        }

                                        if (var2.nextInt(4) == 0 && var1.getTypeId(var14, var10, var15 - 1) == 0)
                                        {
                                            this.b(var1, var14, var10, var15 - 1, 1);
                                        }

                                        if (var2.nextInt(4) == 0 && var1.getTypeId(var14, var10, var15 + 1) == 0)
                                        {
                                            this.b(var1, var14, var10, var15 + 1, 4);
                                        }
                                    }
                                }
                            }
                        }

                        if (var2.nextInt(5) == 0 && var6 > 5)
                        {
                            for (var10 = 0; var10 < 2; ++var10)
                            {
                                for (var11 = 0; var11 < 4; ++var11)
                                {
                                    if (var2.nextInt(4 - var10) == 0)
                                    {
                                        var20 = var2.nextInt(3);
                                        this.setTypeAndData(var1, var3 + Direction.a[Direction.f[var11]], var4 + var6 - 5 + var10, var5 + Direction.b[Direction.f[var11]], Block.COCOA.id, var20 << 2 | var11);
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
     * Grows vines downward from the given block for a given length. Args: World, x, starty, z, vine-length
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
