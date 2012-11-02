package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.server.ItemStack;
import net.minecraft.server.StructurePieceTreasure;
import net.minecraft.server.WorldGenJungleTemple;
import net.minecraft.server.WorldGenMineshaftPieces;
import net.minecraft.server.WorldGenPyramidPiece;
import net.minecraft.server.WorldGenStrongholdChestCorridor;
import net.minecraft.server.WorldGenStrongholdLibrary;
import net.minecraft.server.WorldGenStrongholdRoomCrossing;
import net.minecraft.server.WorldGenVillageBlacksmith;
import net.minecraft.server.WorldServer;

public class ChestGenHooks
{
    public static final String MINESHAFT_CORRIDOR = "mineshaftCorridor";
    public static final String PYRAMID_DESERT_CHEST = "pyramidDesertyChest";
    public static final String PYRAMID_JUNGLE_CHEST = "pyramidJungleChest";
    public static final String PYRAMID_JUNGLE_DISPENSER = "pyramidJungleDispenser";
    public static final String STRONGHOLD_CORRIDOR = "strongholdCorridor";
    public static final String STRONGHOLD_LIBRARY = "strongholdLibrary";
    public static final String STRONGHOLD_CROSSING = "strongholdCrossing";
    public static final String VILLAGE_BLACKSMITH = "villageBlacksmith";
    public static final String BONUS_CHEST = "bonusChest";
    private static final HashMap chestInfo = new HashMap();
    private static boolean hasInit = false;
    private String category;
    private int countMin;
    private int countMax;
    private ArrayList contents;

    private static void init()
    {
        if (!hasInit)
        {
            addInfo("mineshaftCorridor", WorldGenMineshaftPieces.a, 3, 7);
            addInfo("pyramidDesertyChest", WorldGenPyramidPiece.i, 2, 7);
            addInfo("pyramidJungleChest", WorldGenJungleTemple.l, 2, 7);
            addInfo("pyramidJungleDispenser", WorldGenJungleTemple.m, 2, 2);
            addInfo("strongholdCorridor", WorldGenStrongholdChestCorridor.a, 2, 4);
            addInfo("strongholdLibrary", WorldGenStrongholdLibrary.b, 1, 5);
            addInfo("strongholdCrossing", WorldGenStrongholdRoomCrossing.c, 1, 5);
            addInfo("villageBlacksmith", WorldGenVillageBlacksmith.a, 3, 9);
            addInfo("bonusChest", WorldServer.S, 10, 10);
        }
    }

    private static void addInfo(String var0, StructurePieceTreasure[] var1, int var2, int var3)
    {
        chestInfo.put(var0, new ChestGenHooks(var0, var1, var2, var3));
    }

    public static ChestGenHooks getInfo(String var0)
    {
        if (!chestInfo.containsKey(var0))
        {
            chestInfo.put(var0, new ChestGenHooks(var0));
        }

        return (ChestGenHooks)chestInfo.get(var0);
    }

    public static ItemStack[] generateStacks(Random var0, ItemStack var1, int var2, int var3)
    {
        int var4 = var2 + var0.nextInt(var3 - var2 + 1);
        ItemStack[] var5;

        if (var1.getItem() == null)
        {
            var5 = new ItemStack[0];
        }
        else if (var4 > var1.getItem().getMaxStackSize())
        {
            var5 = new ItemStack[var4];

            for (int var6 = 0; var6 < var4; ++var6)
            {
                var5[var6] = var1.cloneItemStack();
                var5[var6].count = 1;
            }
        }
        else
        {
            var5 = new ItemStack[] {var1.cloneItemStack()};
            var5[0].count = var4;
        }

        return var5;
    }

    public static StructurePieceTreasure[] getItems(String var0)
    {
        return getInfo(var0).getItems();
    }

    public static int getCount(String var0, Random var1)
    {
        return getInfo(var0).getCount(var1);
    }

    public static void addItem(String var0, StructurePieceTreasure var1)
    {
        getInfo(var0).addItem(var1);
    }

    public static void removeItem(String var0, ItemStack var1)
    {
        getInfo(var0).removeItem(var1);
    }

    public ChestGenHooks(String var1)
    {
        this.countMin = 0;
        this.countMax = 0;
        this.contents = new ArrayList();
        this.category = var1;
    }

    public ChestGenHooks(String var1, StructurePieceTreasure[] var2, int var3, int var4)
    {
        this(var1);
        StructurePieceTreasure[] var5 = var2;
        int var6 = var2.length;

        for (int var7 = 0; var7 < var6; ++var7)
        {
            StructurePieceTreasure var8 = var5[var7];
            this.contents.add(var8);
        }

        this.countMin = var3;
        this.countMax = var4;
    }

    public void addItem(StructurePieceTreasure var1)
    {
        this.contents.add(var1);
    }

    public void removeItem(ItemStack var1)
    {
        Iterator var2 = this.contents.iterator();

        while (var2.hasNext())
        {
            StructurePieceTreasure var3 = (StructurePieceTreasure)var2.next();

            if (var1.doMaterialsMatch(var3.itemStack) || var1.getData() == -1 && var1.id == var3.itemStack.id)
            {
                var2.remove();
            }
        }
    }

    public StructurePieceTreasure[] getItems()
    {
        return (StructurePieceTreasure[])this.contents.toArray(new StructurePieceTreasure[this.contents.size()]);
    }

    public int getCount(Random var1)
    {
        return this.countMin < this.countMax ? this.countMin + var1.nextInt(this.countMax - this.countMin) : this.countMin;
    }

    public int getMin()
    {
        return this.countMin;
    }

    public int getMax()
    {
        return this.countMax;
    }

    public void setMin(int var1)
    {
        this.countMin = var1;
    }

    public void setMax(int var1)
    {
        this.countMax = var1;
    }

    static
    {
        init();
    }
}
