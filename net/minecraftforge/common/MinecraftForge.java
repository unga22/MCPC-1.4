package net.minecraftforge.common;

import cpw.mods.fml.common.FMLLog;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.Block;
import net.minecraft.server.EntityEnderman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraftforge.common.ForgeHooks$GrassEntry;
import net.minecraftforge.common.ForgeHooks$SeedEntry;
import net.minecraftforge.event.EventBus;

public class MinecraftForge
{
    public static final EventBus EVENT_BUS = new EventBus();
    public static boolean SPAWNER_ALLOW_ON_INVERTED = false;
    private static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();

    public static void addGrassPlant(Block var0, int var1, int var2)
    {
        ForgeHooks.grassList.add(new ForgeHooks$GrassEntry(var0, var1, var2));
    }

    public static void addGrassSeed(ItemStack var0, int var1)
    {
        ForgeHooks.seedList.add(new ForgeHooks$SeedEntry(var0, var1));
    }

    public static void setToolClass(Item var0, String var1, int var2)
    {
        ForgeHooks.toolClasses.put(var0, Arrays.asList(new Serializable[] {var1, Integer.valueOf(var2)}));
    }

    public static void setBlockHarvestLevel(Block var0, int var1, String var2, int var3)
    {
        List var4 = Arrays.asList(new Object[] {var0, Integer.valueOf(var1), var2});
        ForgeHooks.toolHarvestLevels.put(var4, Integer.valueOf(var3));
        ForgeHooks.toolEffectiveness.add(var4);
    }

    public static void removeBlockEffectiveness(Block var0, int var1, String var2)
    {
        List var3 = Arrays.asList(new Object[] {var0, Integer.valueOf(var1), var2});
        ForgeHooks.toolEffectiveness.remove(var3);
    }

    public static void setBlockHarvestLevel(Block var0, String var1, int var2)
    {
        for (int var3 = 0; var3 < 16; ++var3)
        {
            List var4 = Arrays.asList(new Object[] {var0, Integer.valueOf(var3), var1});
            ForgeHooks.toolHarvestLevels.put(var4, Integer.valueOf(var2));
            ForgeHooks.toolEffectiveness.add(var4);
        }
    }

    public static int getBlockHarvestLevel(Block var0, int var1, String var2)
    {
        ForgeHooks.initTools();
        List var3 = Arrays.asList(new Object[] {var0, Integer.valueOf(var1), var2});
        Integer var4 = (Integer)ForgeHooks.toolHarvestLevels.get(var3);
        return var4 == null ? -1 : var4.intValue();
    }

    public static void removeBlockEffectiveness(Block var0, String var1)
    {
        for (int var2 = 0; var2 < 16; ++var2)
        {
            List var3 = Arrays.asList(new Object[] {var0, Integer.valueOf(var2), var1});
            ForgeHooks.toolEffectiveness.remove(var3);
        }
    }

    public static void initialize()
    {
        System.out.printf("MinecraftForge v%s Initialized\n", new Object[] {ForgeVersion.getVersion()});
        FMLLog.info("MinecraftForge v%s Initialized", new Object[] {ForgeVersion.getVersion()});
        Block var0 = new Block(0, Material.AIR);
        Block.byId[0] = null;
        Block.q[0] = false;
        Block.lightBlock[0] = 0;

        for (int var1 = 256; var1 < 4096; ++var1)
        {
            if (Item.byId[var1] != null)
            {
                Block.byId[var1] = var0;
            }
        }

        boolean[] var3 = new boolean[4096];

        for (int var2 = 0; var2 < EntityEnderman.d.length; ++var2)
        {
            var3[var2] = EntityEnderman.d[var2];
        }

        EntityEnderman.d = var3;
        EVENT_BUS.register(INTERNAL_HANDLER);
    }

    public static String getBrandingVersion()
    {
        return "Minecraft Forge " + ForgeVersion.getVersion();
    }
}
