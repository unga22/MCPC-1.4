package net.minecraft.server;

import java.util.Random;

public class BlockLeaves extends BlockTransparant
{
    /**
     * The base index in terrain.png corresponding to the fancy version of the leaf texture. This is stored so we can
     * switch the displayed version between fancy and fast graphics (fast is this index + 1).
     */
    private int cD;
    public static final String[] a = new String[] {"oak", "spruce", "birch", "jungle"};
    int[] b;

    protected BlockLeaves(int var1, int var2)
    {
        super(var1, var2, Material.LEAVES, false);
        this.cD = var2;
        this.b(true);
        this.a(CreativeModeTab.c);
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void remove(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        byte var7 = 1;
        int var8 = var7 + 1;

        if (var1.d(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8))
        {
            for (int var9 = -var7; var9 <= var7; ++var9)
            {
                for (int var10 = -var7; var10 <= var7; ++var10)
                {
                    for (int var11 = -var7; var11 <= var7; ++var11)
                    {
                        int var12 = var1.getTypeId(var2 + var9, var3 + var10, var4 + var11);

                        if (var12 == Block.LEAVES.id)
                        {
                            int var13 = var1.getData(var2 + var9, var3 + var10, var4 + var11);
                            var1.setRawData(var2 + var9, var3 + var10, var4 + var11, var13 | 8);
                        }
                    }
                }
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (!var1.isStatic)
        {
            int var6 = var1.getData(var2, var3, var4);

            if ((var6 & 8) != 0 && (var6 & 4) == 0)
            {
                byte var7 = 4;
                int var8 = var7 + 1;
                byte var9 = 32;
                int var10 = var9 * var9;
                int var11 = var9 / 2;

                if (this.b == null)
                {
                    this.b = new int[var9 * var9 * var9];
                }

                int var12;

                if (var1.d(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8))
                {
                    int var13;
                    int var14;
                    int var15;

                    for (var12 = -var7; var12 <= var7; ++var12)
                    {
                        for (var13 = -var7; var13 <= var7; ++var13)
                        {
                            for (var14 = -var7; var14 <= var7; ++var14)
                            {
                                var15 = var1.getTypeId(var2 + var12, var3 + var13, var4 + var14);

                                if (var15 == Block.LOG.id)
                                {
                                    this.b[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = 0;
                                }
                                else if (var15 == Block.LEAVES.id)
                                {
                                    this.b[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -2;
                                }
                                else
                                {
                                    this.b[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -1;
                                }
                            }
                        }
                    }

                    for (var12 = 1; var12 <= 4; ++var12)
                    {
                        for (var13 = -var7; var13 <= var7; ++var13)
                        {
                            for (var14 = -var7; var14 <= var7; ++var14)
                            {
                                for (var15 = -var7; var15 <= var7; ++var15)
                                {
                                    if (this.b[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11] == var12 - 1)
                                    {
                                        if (this.b[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2)
                                        {
                                            this.b[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;
                                        }

                                        if (this.b[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2)
                                        {
                                            this.b[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;
                                        }

                                        if (this.b[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] == -2)
                                        {
                                            this.b[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] = var12;
                                        }

                                        if (this.b[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] == -2)
                                        {
                                            this.b[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] = var12;
                                        }

                                        if (this.b[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] == -2)
                                        {
                                            this.b[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] = var12;
                                        }

                                        if (this.b[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] == -2)
                                        {
                                            this.b[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] = var12;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                var12 = this.b[var11 * var10 + var11 * var9 + var11];

                if (var12 >= 0)
                {
                    var1.setRawData(var2, var3, var4, var6 & -9);
                }
                else
                {
                    this.l(var1, var2, var3, var4);
                }
            }
        }
    }

    private void l(World var1, int var2, int var3, int var4)
    {
        this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
        var1.setTypeId(var2, var3, var4, 0);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int a(Random var1)
    {
        return var1.nextInt(20) == 0 ? 1 : 0;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return Block.SAPLING.id;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropNaturally(World var1, int var2, int var3, int var4, int var5, float var6, int var7)
    {
        if (!var1.isStatic)
        {
            byte var8 = 20;

            if ((var5 & 3) == 3)
            {
                var8 = 40;
            }

            if (var1.random.nextInt(var8) == 0)
            {
                int var9 = this.getDropType(var5, var1.random, var7);
                this.a(var1, var2, var3, var4, new ItemStack(var9, 1, this.getDropData(var5)));
            }

            if ((var5 & 3) == 0 && var1.random.nextInt(200) == 0)
            {
                this.a(var1, var2, var3, var4, new ItemStack(Item.APPLE, 1, 0));
            }
        }
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void a(World var1, EntityHuman var2, int var3, int var4, int var5, int var6)
    {
        if (!var1.isStatic && var2.bP() != null && var2.bP().id == Item.SHEARS.id)
        {
            var2.a(StatisticList.C[this.id], 1);
            this.a(var1, var3, var4, var5, new ItemStack(Block.LEAVES.id, 1, var6 & 3));
        }
        else
        {
            super.a(var1, var2, var3, var4, var5, var6);
        }
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int getDropData(int var1)
    {
        return var1 & 3;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean c()
    {
        return !this.c;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        return (var2 & 3) == 1 ? this.textureId + 80 : ((var2 & 3) == 3 ? this.textureId + 144 : this.textureId);
    }
}
