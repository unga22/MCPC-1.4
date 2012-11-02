package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.WeightedRandom;
import net.minecraftforge.common.DungeonHooks$DungeonLoot;
import net.minecraftforge.common.DungeonHooks$DungeonMob;

public class DungeonHooks
{
    private static int dungeonLootAttempts = 8;
    private static ArrayList dungeonMobs = new ArrayList();
    private static ArrayList dungeonLoot = new ArrayList();

    public static void setDungeonLootTries(int var0)
    {
        dungeonLootAttempts = var0;
    }

    public static int getDungeonLootTries()
    {
        return dungeonLootAttempts;
    }

    public static float addDungeonMob(String var0, int var1)
    {
        if (var1 <= 0)
        {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        }
        else
        {
            Iterator var2 = dungeonMobs.iterator();
            DungeonHooks$DungeonMob var3;

            do
            {
                if (!var2.hasNext())
                {
                    dungeonMobs.add(new DungeonHooks$DungeonMob(var1, var0));
                    return (float)var1;
                }

                var3 = (DungeonHooks$DungeonMob)var2.next();
            }
            while (!var0.equals(var3.type));

            return (float)(var3.a += var1);
        }
    }

    public static int removeDungeonMob(String var0)
    {
        Iterator var1 = dungeonMobs.iterator();
        DungeonHooks$DungeonMob var2;

        do
        {
            if (!var1.hasNext())
            {
                return 0;
            }

            var2 = (DungeonHooks$DungeonMob)var1.next();
        }
        while (!var0.equals(var2.type));

        dungeonMobs.remove(var2);
        return var2.a;
    }

    public static String getRandomDungeonMob(Random var0)
    {
        DungeonHooks$DungeonMob var1 = (DungeonHooks$DungeonMob)WeightedRandom.a(var0, dungeonMobs);
        return var1 == null ? "" : var1.type;
    }

    public static void addDungeonLoot(ItemStack var0, int var1)
    {
        addDungeonLoot(var0, var1, 1, 1);
    }

    public static float addDungeonLoot(ItemStack var0, int var1, int var2, int var3)
    {
        Iterator var4 = dungeonLoot.iterator();
        DungeonHooks$DungeonLoot var5;

        do
        {
            if (!var4.hasNext())
            {
                dungeonLoot.add(new DungeonHooks$DungeonLoot(var1, var0, var2, var3));
                return (float)var1;
            }

            var5 = (DungeonHooks$DungeonLoot)var4.next();
        }
        while (!var5.equals(var0, var2, var3));

        return (float)(var5.a += var1);
    }

    public static void removeDungeonLoot(ItemStack var0)
    {
        removeDungeonLoot(var0, -1, 0);
    }

    public static void removeDungeonLoot(ItemStack var0, int var1, int var2)
    {
        ArrayList var3 = (ArrayList)dungeonLoot.clone();
        Iterator var4;
        DungeonHooks$DungeonLoot var5;

        if (var1 < 0)
        {
            var4 = var3.iterator();

            while (var4.hasNext())
            {
                var5 = (DungeonHooks$DungeonLoot)var4.next();

                if (var5.equals(var0))
                {
                    dungeonLoot.remove(var5);
                }
            }
        }
        else
        {
            var4 = var3.iterator();

            while (var4.hasNext())
            {
                var5 = (DungeonHooks$DungeonLoot)var4.next();

                if (var5.equals(var0, var1, var2))
                {
                    dungeonLoot.remove(var5);
                }
            }
        }
    }

    public static ItemStack getRandomDungeonLoot(Random var0)
    {
        DungeonHooks$DungeonLoot var1 = (DungeonHooks$DungeonLoot)WeightedRandom.a(var0, dungeonLoot);
        return var1 != null ? var1.generateStack(var0) : null;
    }

    public void addDungeonLoot(DungeonHooks$DungeonLoot var1)
    {
        dungeonLoot.add(var1);
    }

    public boolean removeDungeonLoot(DungeonHooks$DungeonLoot var1)
    {
        return dungeonLoot.remove(var1);
    }

    static
    {
        addDungeonMob("Skeleton", 100);
        addDungeonMob("Zombie", 200);
        addDungeonMob("Spider", 100);
        addDungeonLoot(new ItemStack(Item.SADDLE), 100);
        addDungeonLoot(new ItemStack(Item.IRON_INGOT), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.BREAD), 100);
        addDungeonLoot(new ItemStack(Item.WHEAT), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.SULPHUR), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.STRING), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.BUCKET), 100);
        addDungeonLoot(new ItemStack(Item.GOLDEN_APPLE), 1);
        addDungeonLoot(new ItemStack(Item.REDSTONE), 40, 1, 4);
        addDungeonLoot(new ItemStack(Item.RECORD_1), 5);
        addDungeonLoot(new ItemStack(Item.RECORD_2), 5);
        addDungeonLoot(new ItemStack(Item.INK_SACK, 1, 3), 100);
    }
}
