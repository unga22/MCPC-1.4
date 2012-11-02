package net.minecraft.server;

import java.util.Random;

public class StructurePieceTreasure extends WeightedRandomChoice
{
    /** The Item/Block ID to generate in the Chest. */
    private int b;

    /** The Item damage/metadata. */
    private int c;

    /** The minimum chance of item generating. */
    private int d;

    /** The maximum chance of item generating. */
    private int e;

    public StructurePieceTreasure(int var1, int var2, int var3, int var4, int var5)
    {
        super(var5);
        this.b = var1;
        this.c = var2;
        this.d = var3;
        this.e = var4;
    }

    /**
     * Generates the Chest contents.
     */
    public static void a(Random var0, StructurePieceTreasure[] var1, TileEntityChest var2, int var3)
    {
        for (int var4 = 0; var4 < var3; ++var4)
        {
            StructurePieceTreasure var5 = (StructurePieceTreasure)WeightedRandom.a(var0, var1);
            int var6 = var5.d + var0.nextInt(var5.e - var5.d + 1);

            if (Item.byId[var5.b].getMaxStackSize() >= var6)
            {
                var2.setItem(var0.nextInt(var2.getSize()), new ItemStack(var5.b, var6, var5.c));
            }
            else
            {
                for (int var7 = 0; var7 < var6; ++var7)
                {
                    var2.setItem(var0.nextInt(var2.getSize()), new ItemStack(var5.b, 1, var5.c));
                }
            }
        }
    }

    /**
     * Generates the Dispenser contents.
     */
    public static void a(Random var0, StructurePieceTreasure[] var1, TileEntityDispenser var2, int var3)
    {
        for (int var4 = 0; var4 < var3; ++var4)
        {
            StructurePieceTreasure var5 = (StructurePieceTreasure)WeightedRandom.a(var0, var1);
            int var6 = var5.d + var0.nextInt(var5.e - var5.d + 1);

            if (Item.byId[var5.b].getMaxStackSize() >= var6)
            {
                var2.setItem(var0.nextInt(var2.getSize()), new ItemStack(var5.b, var6, var5.c));
            }
            else
            {
                for (int var7 = 0; var7 < var6; ++var7)
                {
                    var2.setItem(var0.nextInt(var2.getSize()), new ItemStack(var5.b, 1, var5.c));
                }
            }
        }
    }
}
