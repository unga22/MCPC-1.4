package net.minecraftforge.common;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.World.Environment;

import net.minecraft.server.ExceptionWorldConflict;
import net.minecraft.server.IDataManager;
import net.minecraft.server.IProgressUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.SecondaryWorldServer;
import net.minecraft.server.World;
import net.minecraft.server.WorldManager;
import net.minecraft.server.WorldProvider;
import net.minecraft.server.WorldProviderHell;
import net.minecraft.server.WorldProviderNormal;
import net.minecraft.server.WorldProviderTheEnd;
import net.minecraft.server.WorldServer;
import net.minecraft.server.WorldSettings;
import net.minecraftforge.event.world.WorldEvent$Load;
import net.minecraftforge.event.world.WorldEvent$Unload;

public class DimensionManager
{
    private static Hashtable providers = new Hashtable();
    private static Hashtable spawnSettings = new Hashtable();
    private static Hashtable worlds = new Hashtable();
    private static boolean hasInit = false;
    private static Hashtable dimensions = new Hashtable();
    private static Map persistentChunkStore = Maps.newHashMap();
    private static ArrayList unloadQueue = new ArrayList();
    private static BitSet dimensionMap = new BitSet(1024);

    public static boolean registerProviderType(int var0, Class var1, boolean var2)
    {
        if (providers.containsValue(Integer.valueOf(var0)))
        {
            return false;
        }
        else
        {
            providers.put(Integer.valueOf(var0), var1);
            spawnSettings.put(Integer.valueOf(var0), Boolean.valueOf(var2));
            return true;
        }
    }

    public static void init()
    {
        if (!hasInit)
        {
            registerProviderType(0, WorldProviderNormal.class, true);
            registerProviderType(-1, WorldProviderHell.class, true);
            registerProviderType(1, WorldProviderTheEnd.class, false);
            registerDimension(0, 0);
            registerDimension(-1, -1);
            registerDimension(1, 1);
        }
    }

    public static void registerDimension(int var0, int var1)
    {
        if (!providers.containsKey(Integer.valueOf(var1)))
        {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, provider type %d does not exist", new Object[] {Integer.valueOf(var0), Integer.valueOf(var1)}));
        }
        else if (dimensions.containsKey(Integer.valueOf(var0)))
        {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, One is already registered", new Object[] {Integer.valueOf(var0)}));
        }
        else
        {
            dimensions.put(Integer.valueOf(var0), Integer.valueOf(var1));

            if (var0 >= 0)
            {
                dimensionMap.set(var0);
            }
        }
    }

    public static void unregisterDimension(int var0)
    {
        if (!dimensions.containsKey(Integer.valueOf(var0)))
        {
            throw new IllegalArgumentException(String.format("Failed to unregister dimension for id %d; No provider registered", new Object[] {Integer.valueOf(var0)}));
        }
        else
        {
            dimensions.remove(Integer.valueOf(var0));
        }
    }

    public static int getProviderType(int var0)
    {
        if (!dimensions.containsKey(Integer.valueOf(var0)))
        {
            throw new IllegalArgumentException(String.format("Could not get provider type for dimension %d, does not exist", new Object[] {Integer.valueOf(var0)}));
        }
        else
        {
            return ((Integer)dimensions.get(Integer.valueOf(var0))).intValue();
        }
    }

    public static WorldProvider getProvider(int var0)
    {
        return getWorld(var0).worldProvider;
    }

    public static Integer[] getIDs()
    {
        return (Integer[])worlds.keySet().toArray(new Integer[worlds.size()]);
    }

    public static void setWorld(int var0, WorldServer var1)
    {
        if (var1 != null)
        {
            worlds.put(Integer.valueOf(var0), var1);
            MinecraftServer.getServer().worldTickTimes.put(Integer.valueOf(var0), new long[100]);
            FMLLog.info("Loading dimension %d (%s) (%s)", new Object[] {Integer.valueOf(var0), var1.getWorldData().getName(), var1.getMinecraftServer()});
        }
        else
        {
            worlds.remove(Integer.valueOf(var0));
            MinecraftServer.getServer().worldTickTimes.remove(Integer.valueOf(var0));
            FMLLog.info("Unloading dimension %d", new Object[] {Integer.valueOf(var0)});
        }

        ArrayList var2 = new ArrayList();

        if (worlds.get(Integer.valueOf(0)) != null)
        {
            var2.add(worlds.get(Integer.valueOf(0)));
        }

        if (worlds.get(Integer.valueOf(-1)) != null)
        {
            var2.add(worlds.get(Integer.valueOf(-1)));
        }

        if (worlds.get(Integer.valueOf(1)) != null)
        {
            var2.add(worlds.get(Integer.valueOf(1)));
        }

        Iterator var3 = worlds.entrySet().iterator();

        while (var3.hasNext())
        {
            Entry var4 = (Entry)var3.next();
            int var5 = ((Integer)var4.getKey()).intValue();

            if (var5 < -1 || var5 > 1)
            {
                var2.add(var4.getValue());
            }
        }

        MinecraftServer.getServer().worlds = var2;
    }

    /**
     * Only used for sub-world loading.
     */
    public static void initDimension(int dim) 
    {
    	WorldServer overworld = getWorld(0);
    	if (overworld == null) {
    		throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
    	}
    	try {
    		DimensionManager.getProviderType(dim);
    	} catch (Exception e) {
    		System.err.println("Cannot Hotload Dim: " + e.getMessage());
    		return; //If a provider hasn't been registered then we can't hotload the dim
    	}
    	MinecraftServer mcServer = overworld.getMinecraftServer();
    	IDataManager savehandler = overworld.getDataManager();
    	
    	WorldSettings worldSettings = new WorldSettings(overworld.getWorldData());

    	WorldServer world = (dim == 0 ? overworld : new SecondaryWorldServer(mcServer, savehandler, overworld.getWorldData().getName(), dim, worldSettings, overworld, mcServer.methodProfiler, Environment.getEnvironment(0), null));
    	world.addIWorldAccess(new WorldManager(mcServer, world));
    	world.worldProvider.setDimension(dim);
    	
    	mcServer.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldInitEvent(world.getWorld()));
    	MinecraftForge.EVENT_BUS.post(new WorldEvent$Load(world));
    	
    	if (!mcServer.I())
    		world.getWorldData().setGameType(mcServer.getGamemode());

    	mcServer.c(mcServer.getDifficulty());
    	
    	mcServer.worlds.add(world);
    }


    public static WorldServer getWorld(int var0)
    {
        return (WorldServer)worlds.get(Integer.valueOf(var0));
    }

    public static WorldServer[] getWorlds()
    {
        return (WorldServer[])worlds.values().toArray(new WorldServer[0]);
    }

    public static boolean shouldLoadSpawn(int var0)
    {
        int var1 = getProviderType(var0);
        return spawnSettings.contains(Integer.valueOf(var1)) && ((Boolean)spawnSettings.get(Integer.valueOf(var1))).booleanValue();
    }

    public static Integer[] getStaticDimensionIDs()
    {
        return (Integer[])dimensions.keySet().toArray(new Integer[dimensions.keySet().size()]);
    }

    public static WorldProvider createProviderFor(int var0)
    {
        try
        {
            if (dimensions.containsKey(Integer.valueOf(var0)))
            {
                WorldProvider var1 = (WorldProvider)((Class)providers.get(Integer.valueOf(getProviderType(var0)))).newInstance();
                var1.setDimension(var0);
                return var1;
            }
            else
            {
                throw new RuntimeException(String.format("No WorldProvider bound for dimension %d", new Object[] {Integer.valueOf(var0)}));
            }
        }
        catch (Exception var2)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("An error occured trying to create an instance of WorldProvider %d (%s)", new Object[] {Integer.valueOf(var0), ((Class)providers.get(Integer.valueOf(getProviderType(var0)))).getSimpleName()}), var2);
            throw new RuntimeException(var2);
        }
    }

    public static void unloadWorld(int var0)
    {
        unloadQueue.add(Integer.valueOf(var0));
    }

    public static void unloadWorlds(Hashtable var0)
    {
        Iterator var1 = unloadQueue.iterator();

        while (var1.hasNext())
        {
            int var2 = ((Integer)var1.next()).intValue();

            try
            {
                ((WorldServer)worlds.get(Integer.valueOf(var2))).save(true, (IProgressUpdate)null);
            }
            catch (ExceptionWorldConflict var4)
            {
                var4.printStackTrace();
            }

            MinecraftForge.EVENT_BUS.post(new WorldEvent$Unload((World)worlds.get(Integer.valueOf(var2))));
            ((WorldServer)worlds.get(Integer.valueOf(var2))).saveLevel();
            setWorld(var2, (WorldServer)null);
        }

        unloadQueue.clear();
    }

    public static int getNextFreeDimId()
    {
        int var0 = 0;

        while (true)
        {
            var0 = dimensionMap.nextClearBit(var0);

            if (!dimensions.containsKey(Integer.valueOf(var0)))
            {
                return var0;
            }

            dimensionMap.set(var0);
        }
    }

    public static NBTTagCompound saveDimensionDataMap()
    {
        int[] var0 = new int[(dimensionMap.length() + 32 - 1) / 32];
        NBTTagCompound var1 = new NBTTagCompound();

        for (int var2 = 0; var2 < var0.length; ++var2)
        {
            int var3 = 0;

            for (int var4 = 0; var4 < 32; ++var4)
            {
                var3 |= dimensionMap.get(var2 * 32 + var4) ? 1 << var4 : 0;
            }

            var0[var2] = var3;
        }

        var1.setIntArray("DimensionArray", var0);
        return var1;
    }

    public static void loadDimensionDataMap(NBTTagCompound var0)
    {
        if (var0 == null)
        {
            dimensionMap.clear();
            Iterator var1 = dimensions.keySet().iterator();

            while (var1.hasNext())
            {
                Integer var2 = (Integer)var1.next();

                if (var2.intValue() >= 0)
                {
                    dimensionMap.set(var2.intValue());
                }
            }
        }
        else
        {
            int[] var4 = var0.getIntArray("DimensionArray");

            for (int var5 = 0; var5 < var4.length; ++var5)
            {
                for (int var3 = 0; var3 < 32; ++var3)
                {
                    dimensionMap.set(var5 * 32 + var3, (var4[var5] & 1 << var3) != 0);
                }
            }
        }
    }

    static
    {
        init();
    }
}
