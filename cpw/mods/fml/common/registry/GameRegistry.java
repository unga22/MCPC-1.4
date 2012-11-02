package cpw.mods.fml.common.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IDispenserHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.Mod$Block;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry$1;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.Block;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.IChunkProvider;
import net.minecraft.server.IInventory;
import net.minecraft.server.IRecipe;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;
import net.minecraft.server.RecipesFurnace;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.WorldType;

public class GameRegistry
{
    private static Multimap blockRegistry = ArrayListMultimap.create();
    private static Multimap itemRegistry = ArrayListMultimap.create();
    private static Set worldGenerators = Sets.newHashSet();
    private static List fuelHandlers = Lists.newArrayList();
    private static List craftingHandlers = Lists.newArrayList();
    private static List dispenserHandlers = Lists.newArrayList();
    private static List pickupHandlers = Lists.newArrayList();
    private static List playerTrackers = Lists.newArrayList();

    public static void registerWorldGenerator(IWorldGenerator var0)
    {
        worldGenerators.add(var0);
    }

    public static void generateWorld(int var0, int var1, World var2, IChunkProvider var3, IChunkProvider var4)
    {
        long var5 = var2.getSeed();
        Random var7 = new Random(var5);
        long var8 = var7.nextLong() >> 3;
        long var10 = var7.nextLong() >> 3;
        var7.setSeed(var8 * (long)var0 + var10 * (long)var1 ^ var5);
        Iterator var12 = worldGenerators.iterator();

        while (var12.hasNext())
        {
            IWorldGenerator var13 = (IWorldGenerator)var12.next();
            var13.generate(var7, var0, var1, var2, var3, var4);
        }
    }

    public static void registerDispenserHandler(IDispenserHandler var0)
    {
        dispenserHandlers.add(var0);
    }

    @Deprecated
    public static void registerDispenserHandler(IDispenseHandler var0)
    {
        registerDispenserHandler((IDispenserHandler)(new GameRegistry$1(var0)));
    }

    public static int tryDispense(World var0, int var1, int var2, int var3, int var4, int var5, ItemStack var6, Random var7, double var8, double var10, double var12)
    {
        Iterator var14 = dispenserHandlers.iterator();
        int var16;

        do
        {
            if (!var14.hasNext())
            {
                return -1;
            }

            IDispenserHandler var15 = (IDispenserHandler)var14.next();
            var16 = var15.dispense(var1, var2, var3, var4, var5, var0, var6, var7, var8, var10, var12);
        }
        while (var16 <= -1);

        return var16;
    }

    public static Object buildBlock(ModContainer var0, Class var1, Mod$Block var2) throws Exception
    {
        Object var3 = var1.getConstructor(new Class[] {Integer.TYPE}).newInstance(new Object[] {Integer.valueOf(findSpareBlockId())});
        registerBlock((Block)var3);
        return var3;
    }

    private static int findSpareBlockId()
    {
        return BlockTracker.nextBlockId();
    }

    public static void registerBlock(Block var0)
    {
        registerBlock(var0, ItemBlock.class);
    }

    public static void registerBlock(Block var0, Class var1)
    {
        if (Loader.instance().isInState(LoaderState.CONSTRUCTING))
        {
            FMLLog.warning("The mod %s is attempting to register a block whilst it it being constructed. This is bad modding practice - please use a proper mod lifecycle event.", new Object[] {Loader.instance().activeModContainer()});
        }

        try
        {
            assert var0 != null : "registerBlock: block cannot be null";
            assert var1 != null : "registerBlock: itemclass cannot be null";
            int var2 = var0.id - 256;
            var1.getConstructor(new Class[] {Integer.TYPE}).newInstance(new Object[] {Integer.valueOf(var2)});
        }
        catch (Exception var3)
        {
            FMLLog.log(Level.SEVERE, var3, "Caught an exception during block registration", new Object[0]);
            throw new LoaderException(var3);
        }

        blockRegistry.put(Loader.instance().activeModContainer(), (BlockProxy)var0);
    }

    public static void addRecipe(ItemStack var0, Object ... var1)
    {
        CraftingManager.getInstance().registerShapedRecipe(var0, var1);
    }

    public static void addShapelessRecipe(ItemStack var0, Object ... var1)
    {
        CraftingManager.getInstance().registerShapelessRecipe(var0, var1);
    }

    public static void addRecipe(IRecipe var0)
    {
        CraftingManager.getInstance().getRecipes().add(var0);
    }

    public static void addSmelting(int var0, ItemStack var1, float var2)
    {
        RecipesFurnace.getInstance().registerRecipe(var0, var1, var2);
    }

    public static void registerTileEntity(Class var0, String var1)
    {
        TileEntity.a(var0, var1);
    }

    public static void addBiome(BiomeBase var0)
    {
        WorldType.NORMAL.addNewBiome(var0);
    }

    public static void removeBiome(BiomeBase var0)
    {
        WorldType.NORMAL.removeBiome(var0);
    }

    public static void registerFuelHandler(IFuelHandler var0)
    {
        fuelHandlers.add(var0);
    }

    public static int getFuelValue(ItemStack var0)
    {
        int var1 = 0;
        IFuelHandler var3;

        for (Iterator var2 = fuelHandlers.iterator(); var2.hasNext(); var1 = Math.max(var1, var3.getBurnTime(var0)))
        {
            var3 = (IFuelHandler)var2.next();
        }

        return var1;
    }

    public static void registerCraftingHandler(ICraftingHandler var0)
    {
        craftingHandlers.add(var0);
    }

    public static void onItemCrafted(EntityHuman var0, ItemStack var1, IInventory var2)
    {
        Iterator var3 = craftingHandlers.iterator();

        while (var3.hasNext())
        {
            ICraftingHandler var4 = (ICraftingHandler)var3.next();
            var4.onCrafting(var0, var1, var2);
        }
    }

    public static void onItemSmelted(EntityHuman var0, ItemStack var1)
    {
        Iterator var2 = craftingHandlers.iterator();

        while (var2.hasNext())
        {
            ICraftingHandler var3 = (ICraftingHandler)var2.next();
            var3.onSmelting(var0, var1);
        }
    }

    public static void registerPickupHandler(IPickupNotifier var0)
    {
        pickupHandlers.add(var0);
    }

    public static void onPickupNotification(EntityHuman var0, EntityItem var1)
    {
        Iterator var2 = pickupHandlers.iterator();

        while (var2.hasNext())
        {
            IPickupNotifier var3 = (IPickupNotifier)var2.next();
            var3.notifyPickup(var1, var0);
        }
    }

    public static void registerPlayerTracker(IPlayerTracker var0)
    {
        playerTrackers.add(var0);
    }

    public static void onPlayerLogin(EntityHuman var0)
    {
        Iterator var1 = playerTrackers.iterator();

        while (var1.hasNext())
        {
            IPlayerTracker var2 = (IPlayerTracker)var1.next();
            var2.onPlayerLogin(var0);
        }
    }

    public static void onPlayerLogout(EntityHuman var0)
    {
        Iterator var1 = playerTrackers.iterator();

        while (var1.hasNext())
        {
            IPlayerTracker var2 = (IPlayerTracker)var1.next();
            var2.onPlayerLogout(var0);
        }
    }

    public static void onPlayerChangedDimension(EntityHuman var0)
    {
        Iterator var1 = playerTrackers.iterator();

        while (var1.hasNext())
        {
            IPlayerTracker var2 = (IPlayerTracker)var1.next();
            var2.onPlayerChangedDimension(var0);
        }
    }

    public static void onPlayerRespawn(EntityHuman var0)
    {
        Iterator var1 = playerTrackers.iterator();

        while (var1.hasNext())
        {
            IPlayerTracker var2 = (IPlayerTracker)var1.next();
            var2.onPlayerRespawn(var0);
        }
    }
}
