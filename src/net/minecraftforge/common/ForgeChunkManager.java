package net.minecraftforge.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.MathHelper;
import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;
import net.minecraftforge.common.ForgeChunkManager$LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager$OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager$Ticket;
import net.minecraftforge.common.ForgeChunkManager$Type;
import net.minecraftforge.common.Property$Type;

public class ForgeChunkManager
{
    private static int defaultMaxCount;
    private static int defaultMaxChunks;
    private static boolean overridesEnabled;
    private static Map tickets = (new MapMaker()).weakKeys().makeMap();
    private static Map ticketConstraints = Maps.newHashMap();
    private static Map chunkConstraints = Maps.newHashMap();
    private static SetMultimap playerTickets = HashMultimap.create();
    private static Map callbacks = Maps.newHashMap();
    private static Map forcedChunks = (new MapMaker()).weakKeys().makeMap();
    private static BiMap pendingEntities = HashBiMap.create();
    private static Map dormantChunkCache = (new MapMaker()).weakKeys().makeMap();
    private static File cfgFile;
    private static Configuration config;
    private static int playerTicketLength;
    private static int dormantChunkCacheSize;

    static void loadWorld(World var0)
    {
        ArrayListMultimap var1 = ArrayListMultimap.create();
        tickets.put(var0, var1);
        forcedChunks.put(var0, ImmutableSetMultimap.of());

        if (var0 instanceof WorldServer)
        {
            dormantChunkCache.put(var0, CacheBuilder.newBuilder().maximumSize((long)dormantChunkCacheSize).build());
            WorldServer var2 = (WorldServer)var0;
            File var3 = var2.getChunkSaveLocation();
            File var4 = new File(var3, "forcedchunks.dat");

            if (var4.exists() && var4.isFile())
            {
                ArrayListMultimap var5 = ArrayListMultimap.create();
                ArrayListMultimap var6 = ArrayListMultimap.create();
                NBTTagCompound var7;

                try
                {
                    var7 = NBTCompressedStreamTools.a(var4);
                }
                catch (IOException var20)
                {
                    FMLLog.log(Level.WARNING, var20, "Unable to read forced chunk data at %s - it will be ignored", new Object[] {var4.getAbsolutePath()});
                    return;
                }

                NBTTagList var8 = var7.getList("TicketList");

                for (int var9 = 0; var9 < var8.size(); ++var9)
                {
                    NBTTagCompound var10 = (NBTTagCompound)var8.get(var9);
                    String var11 = var10.getString("Owner");
                    boolean var12 = "Forge".equals(var11);

                    if (!var12 && !Loader.isModLoaded(var11))
                    {
                        FMLLog.warning("Found chunkloading data for mod %s which is currently not available or active - it will be removed from the world save", new Object[] {var11});
                    }
                    else if (!var12 && !callbacks.containsKey(var11))
                    {
                        FMLLog.warning("The mod %s has registered persistent chunkloading data but doesn\'t seem to want to be called back with it - it will be removed from the world save", new Object[] {var11});
                    }
                    else
                    {
                        NBTTagList var13 = var10.getList("Tickets");

                        for (int var14 = 0; var14 < var13.size(); ++var14)
                        {
                            NBTTagCompound var15 = (NBTTagCompound)var13.get(var14);
                            var11 = var15.hasKey("ModId") ? var15.getString("ModId") : var11;
                            ForgeChunkManager$Type var16 = ForgeChunkManager$Type.values()[var15.getByte("Type")];
                            byte var17 = var15.getByte("ChunkListDepth");
                            ForgeChunkManager$Ticket var18 = new ForgeChunkManager$Ticket(var11, var16, var0);

                            if (var15.hasKey("ModData"))
                            {
                                ForgeChunkManager$Ticket.access$102(var18, var15.getCompound("ModData"));
                            }

                            if (var15.hasKey("Player"))
                            {
                                ForgeChunkManager$Ticket.access$202(var18, var15.getString("Player"));
                                var6.put(ForgeChunkManager$Ticket.access$300(var18), var18);
                                playerTickets.put(ForgeChunkManager$Ticket.access$200(var18), var18);
                            }
                            else
                            {
                                var5.put(var11, var18);
                            }

                            if (var16 == ForgeChunkManager$Type.ENTITY)
                            {
                                ForgeChunkManager$Ticket.access$402(var18, var15.getInt("chunkX"));
                                ForgeChunkManager$Ticket.access$502(var18, var15.getInt("chunkZ"));
                                UUID var19 = new UUID(var15.getLong("PersistentIDMSB"), var15.getLong("PersistentIDLSB"));
                                pendingEntities.put(var19, var18);
                            }
                        }
                    }
                }

                Iterator var21 = ImmutableSet.copyOf(pendingEntities.values()).iterator();
                ForgeChunkManager$Ticket var23;

                while (var21.hasNext())
                {
                    var23 = (ForgeChunkManager$Ticket)var21.next();

                    if (ForgeChunkManager$Ticket.access$600(var23) == ForgeChunkManager$Type.ENTITY && ForgeChunkManager$Ticket.access$700(var23) == null)
                    {
                        var0.getChunkAt(ForgeChunkManager$Ticket.access$400(var23), ForgeChunkManager$Ticket.access$500(var23));
                    }
                }

                var21 = ImmutableSet.copyOf(pendingEntities.values()).iterator();

                while (var21.hasNext())
                {
                    var23 = (ForgeChunkManager$Ticket)var21.next();

                    if (ForgeChunkManager$Ticket.access$600(var23) == ForgeChunkManager$Type.ENTITY && ForgeChunkManager$Ticket.access$700(var23) == null)
                    {
                        FMLLog.warning("Failed to load persistent chunkloading entity %s from store.", new Object[] {pendingEntities.inverse().get(var23)});
                        var5.remove(ForgeChunkManager$Ticket.access$300(var23), var23);
                    }
                }

                pendingEntities.clear();
                var21 = var5.keySet().iterator();
                String var22;
                ForgeChunkManager$LoadingCallback var26;

                while (var21.hasNext())
                {
                    var22 = (String)var21.next();
                    var26 = (ForgeChunkManager$LoadingCallback)callbacks.get(var22);
                    int var24 = getMaxTicketLengthFor(var22);
                    List var27 = var5.get(var22);

                    if (var26 instanceof ForgeChunkManager$OrderedLoadingCallback)
                    {
                        ForgeChunkManager$OrderedLoadingCallback var28 = (ForgeChunkManager$OrderedLoadingCallback)var26;
                        var27 = var28.ticketsLoaded(ImmutableList.copyOf(var27), var0, var24);
                    }

                    if (var27.size() > var24)
                    {
                        FMLLog.warning("The mod %s has too many open chunkloading tickets %d. Excess will be dropped", new Object[] {var22, Integer.valueOf(var27.size())});
                        var27.subList(var24, var27.size()).clear();
                    }

                    ((Multimap)tickets.get(var0)).putAll(var22, var27);
                    var26.ticketsLoaded(ImmutableList.copyOf(var27), var0);
                }

                var21 = var6.keySet().iterator();

                while (var21.hasNext())
                {
                    var22 = (String)var21.next();
                    var26 = (ForgeChunkManager$LoadingCallback)callbacks.get(var22);
                    List var25 = var6.get(var22);
                    ((Multimap)tickets.get(var0)).putAll("Forge", var25);
                    var26.ticketsLoaded(ImmutableList.copyOf(var25), var0);
                }
            }
        }
    }

    public static void setForcedChunkLoadingCallback(Object var0, ForgeChunkManager$LoadingCallback var1)
    {
        ModContainer var2 = getContainer(var0);

        if (var2 == null)
        {
            FMLLog.warning("Unable to register a callback for an unknown mod %s (%s : %x)", new Object[] {var0, var0.getClass().getName(), Integer.valueOf(System.identityHashCode(var0))});
        }
        else
        {
            callbacks.put(var2.getModId(), var1);
        }
    }

    public static int ticketCountAvailableFor(Object var0, World var1)
    {
        ModContainer var2 = getContainer(var0);

        if (var2 != null)
        {
            String var3 = var2.getModId();
            int var4 = getMaxTicketLengthFor(var3);
            return var4 - ((Multimap)tickets.get(var1)).get(var3).size();
        }
        else
        {
            return 0;
        }
    }

    private static ModContainer getContainer(Object var0)
    {
        ModContainer var1 = (ModContainer)Loader.instance().getModObjectList().inverse().get(var0);
        return var1;
    }

    private static int getMaxTicketLengthFor(String var0)
    {
        int var1 = ticketConstraints.containsKey(var0) && overridesEnabled ? ((Integer)ticketConstraints.get(var0)).intValue() : defaultMaxCount;
        return var1;
    }

    private static int getMaxChunkDepthFor(String var0)
    {
        int var1 = chunkConstraints.containsKey(var0) && overridesEnabled ? ((Integer)chunkConstraints.get(var0)).intValue() : defaultMaxChunks;
        return var1;
    }

    public static ForgeChunkManager$Ticket requestPlayerTicket(Object var0, EntityHuman var1, World var2, ForgeChunkManager$Type var3)
    {
        ModContainer var4 = getContainer(var0);

        if (var4 == null)
        {
            FMLLog.log(Level.SEVERE, "Failed to locate the container for mod instance %s (%s : %x)", new Object[] {var0, var0.getClass().getName(), Integer.valueOf(System.identityHashCode(var0))});
            return null;
        }
        else if (playerTickets.get(var1.getLocalizedName()).size() > playerTicketLength)
        {
            FMLLog.warning("Unable to assign further chunkloading tickets to player %s (on behalf of mod %s)", new Object[] {var1.getLocalizedName(), var4.getModId()});
            return null;
        }
        else
        {
            ForgeChunkManager$Ticket var5 = new ForgeChunkManager$Ticket(var4.getModId(), var3, var2, var1);
            playerTickets.put(var1.getLocalizedName(), var5);
            ((Multimap)tickets.get(var2)).put("Forge", var5);
            return var5;
        }
    }

    public static ForgeChunkManager$Ticket requestTicket(Object var0, World var1, ForgeChunkManager$Type var2)
    {
        ModContainer var3 = getContainer(var0);

        if (var3 == null)
        {
            FMLLog.log(Level.SEVERE, "Failed to locate the container for mod instance %s (%s : %x)", new Object[] {var0, var0.getClass().getName(), Integer.valueOf(System.identityHashCode(var0))});
            return null;
        }
        else
        {
            String var4 = var3.getModId();

            if (!callbacks.containsKey(var4))
            {
                FMLLog.severe("The mod %s has attempted to request a ticket without a listener in place", new Object[] {var4});
                throw new RuntimeException("Invalid ticket request");
            }
            else
            {
                int var5 = ticketConstraints.containsKey(var4) ? ((Integer)ticketConstraints.get(var4)).intValue() : defaultMaxCount;

                if (((Multimap)tickets.get(var1)).get(var4).size() >= var5)
                {
                    FMLLog.info("The mod %s has attempted to allocate a chunkloading ticket beyond it\'s currently allocated maximum : %d", new Object[] {var4, Integer.valueOf(var5)});
                    return null;
                }
                else
                {
                    ForgeChunkManager$Ticket var6 = new ForgeChunkManager$Ticket(var4, var2, var1);
                    ((Multimap)tickets.get(var1)).put(var4, var6);
                    return var6;
                }
            }
        }
    }

    public static void releaseTicket(ForgeChunkManager$Ticket var0)
    {
        if (var0 != null)
        {
            if (var0.isPlayerTicket())
            {
                if (!playerTickets.containsValue(var0))
                {
                    return;
                }
            }
            else if (!((Multimap)tickets.get(ForgeChunkManager$Ticket.access$800(var0))).containsEntry(ForgeChunkManager$Ticket.access$300(var0), var0))
            {
                return;
            }

            if (ForgeChunkManager$Ticket.access$900(var0) != null)
            {
                Iterator var1 = ImmutableSet.copyOf(ForgeChunkManager$Ticket.access$900(var0)).iterator();

                while (var1.hasNext())
                {
                    ChunkCoordIntPair var2 = (ChunkCoordIntPair)var1.next();
                    unforceChunk(var0, var2);
                }
            }

            if (var0.isPlayerTicket())
            {
                playerTickets.remove(ForgeChunkManager$Ticket.access$200(var0), var0);
                ((Multimap)tickets.get(ForgeChunkManager$Ticket.access$800(var0))).remove("Forge", var0);
            }
            else
            {
                ((Multimap)tickets.get(ForgeChunkManager$Ticket.access$800(var0))).remove(ForgeChunkManager$Ticket.access$300(var0), var0);
            }
        }
    }

    public static void forceChunk(ForgeChunkManager$Ticket var0, ChunkCoordIntPair var1)
    {
        if (var0 != null && var1 != null)
        {
            if (ForgeChunkManager$Ticket.access$600(var0) == ForgeChunkManager$Type.ENTITY && ForgeChunkManager$Ticket.access$700(var0) == null)
            {
                throw new RuntimeException("Attempted to use an entity ticket to force a chunk, without an entity");
            }
            else
            {
                label29:
                {
                    if (var0.isPlayerTicket())
                    {
                        if (playerTickets.containsValue(var0))
                        {
                            break label29;
                        }
                    }
                    else if (((Multimap)tickets.get(ForgeChunkManager$Ticket.access$800(var0))).containsEntry(ForgeChunkManager$Ticket.access$300(var0), var0))
                    {
                        break label29;
                    }

                    FMLLog.severe("The mod %s attempted to force load a chunk with an invalid ticket. This is not permitted.", new Object[] {ForgeChunkManager$Ticket.access$300(var0)});
                    return;
                }
                ForgeChunkManager$Ticket.access$900(var0).add(var1);
                ImmutableSetMultimap var2 = ImmutableSetMultimap.builder().putAll((Multimap)forcedChunks.get(ForgeChunkManager$Ticket.access$800(var0))).put(var1, var0).build();
                forcedChunks.put(ForgeChunkManager$Ticket.access$800(var0), var2);

                if (ForgeChunkManager$Ticket.access$1000(var0) > 0 && ForgeChunkManager$Ticket.access$900(var0).size() > ForgeChunkManager$Ticket.access$1000(var0))
                {
                    ChunkCoordIntPair var3 = (ChunkCoordIntPair)ForgeChunkManager$Ticket.access$900(var0).iterator().next();
                    unforceChunk(var0, var3);
                }
            }
        }
    }

    public static void reorderChunk(ForgeChunkManager$Ticket var0, ChunkCoordIntPair var1)
    {
        if (var0 != null && var1 != null && ForgeChunkManager$Ticket.access$900(var0).contains(var1))
        {
            ForgeChunkManager$Ticket.access$900(var0).remove(var1);
            ForgeChunkManager$Ticket.access$900(var0).add(var1);
        }
    }

    public static void unforceChunk(ForgeChunkManager$Ticket var0, ChunkCoordIntPair var1)
    {
        if (var0 != null && var1 != null)
        {
            ForgeChunkManager$Ticket.access$900(var0).remove(var1);
            LinkedHashMultimap var2 = LinkedHashMultimap.create((Multimap)forcedChunks.get(ForgeChunkManager$Ticket.access$800(var0)));
            var2.remove(var1, var0);
            ImmutableSetMultimap var3 = ImmutableSetMultimap.copyOf(var2);
            forcedChunks.put(ForgeChunkManager$Ticket.access$800(var0), var3);
        }
    }

    static void loadConfiguration()
    {
        Iterator var0 = config.categories.keySet().iterator();

        while (var0.hasNext())
        {
            String var1 = (String)var0.next();

            if (!var1.equals("Forge") && !var1.equals("defaults"))
            {
                Property var2 = config.get(var1, "maximumTicketCount", 200);
                Property var3 = config.get(var1, "maximumChunksPerTicket", 25);
                ticketConstraints.put(var1, Integer.valueOf(var2.getInt(200)));
                chunkConstraints.put(var1, Integer.valueOf(var3.getInt(25)));
            }
        }

        config.save();
    }

    public static ImmutableSetMultimap getPersistentChunksFor(World var0)
    {
        return forcedChunks.containsKey(var0) ? (ImmutableSetMultimap)forcedChunks.get(var0) : ImmutableSetMultimap.of();
    }

    static void saveWorld(World var0)
    {
        if (var0 instanceof WorldServer)
        {
            WorldServer var1 = (WorldServer)var0;
            File var2 = var1.getChunkSaveLocation();
            File var3 = new File(var2, "forcedchunks.dat");
            NBTTagCompound var4 = new NBTTagCompound();
            NBTTagList var5 = new NBTTagList();
            var4.set("TicketList", var5);
            Multimap var6 = (Multimap)tickets.get(var1);
            Iterator var7 = var6.keySet().iterator();

            while (var7.hasNext())
            {
                String var8 = (String)var7.next();
                NBTTagCompound var9 = new NBTTagCompound();
                var5.add(var9);
                var9.setString("Owner", var8);
                NBTTagList var10 = new NBTTagList();
                var9.set("Tickets", var10);
                Iterator var11 = var6.get(var8).iterator();

                while (var11.hasNext())
                {
                    ForgeChunkManager$Ticket var12 = (ForgeChunkManager$Ticket)var11.next();
                    NBTTagCompound var13 = new NBTTagCompound();
                    var13.setByte("Type", (byte)ForgeChunkManager$Ticket.access$600(var12).ordinal());
                    var13.setByte("ChunkListDepth", (byte)ForgeChunkManager$Ticket.access$1000(var12));

                    if (var12.isPlayerTicket())
                    {
                        var13.setString("ModId", ForgeChunkManager$Ticket.access$300(var12));
                        var13.setString("Player", ForgeChunkManager$Ticket.access$200(var12));
                    }

                    if (ForgeChunkManager$Ticket.access$100(var12) != null)
                    {
                        var13.setCompound("ModData", ForgeChunkManager$Ticket.access$100(var12));
                    }

                    if (ForgeChunkManager$Ticket.access$600(var12) == ForgeChunkManager$Type.ENTITY && ForgeChunkManager$Ticket.access$700(var12) != null)
                    {
                        var13.setInt("chunkX", MathHelper.floor((double)ForgeChunkManager$Ticket.access$700(var12).ai));
                        var13.setInt("chunkZ", MathHelper.floor((double)ForgeChunkManager$Ticket.access$700(var12).ak));
                        var13.setLong("PersistentIDMSB", ForgeChunkManager$Ticket.access$700(var12).getPersistentID().getMostSignificantBits());
                        var13.setLong("PersistentIDLSB", ForgeChunkManager$Ticket.access$700(var12).getPersistentID().getLeastSignificantBits());
                        var10.add(var13);
                    }
                    else if (ForgeChunkManager$Ticket.access$600(var12) != ForgeChunkManager$Type.ENTITY)
                    {
                        var10.add(var13);
                    }
                }
            }

            try
            {
                NBTCompressedStreamTools.b(var4, var3);
            }
            catch (IOException var14)
            {
                FMLLog.log(Level.WARNING, var14, "Unable to write forced chunk data to %s - chunkloading won\'t work", new Object[] {var3.getAbsolutePath()});
            }
        }
    }

    static void loadEntity(Entity var0)
    {
        UUID var1 = var0.getPersistentID();
        ForgeChunkManager$Ticket var2 = (ForgeChunkManager$Ticket)pendingEntities.get(var1);

        if (var2 != null)
        {
            var2.bindEntity(var0);
            pendingEntities.remove(var1);
        }
    }

    public static void putDormantChunk(long var0, Chunk var2)
    {
        Cache var3 = (Cache)dormantChunkCache.get(var2.world);

        if (var3 != null)
        {
            var3.put(Long.valueOf(var0), var2);
        }
    }

    public static Chunk fetchDormantChunk(long var0, World var2)
    {
        Cache var3 = (Cache)dormantChunkCache.get(var2);
        return var3 == null ? null : (Chunk)var3.getIfPresent(Long.valueOf(var0));
    }

    static void captureConfig(File var0)
    {
        cfgFile = new File(var0, "forgeChunkLoading.cfg");
        config = new Configuration(cfgFile, true);
        config.categories.clear();

        try
        {
            config.load();
        }
        catch (Exception var11)
        {
            File var2 = new File(cfgFile.getParentFile(), "forgeChunkLoading.cfg.bak");

            if (var2.exists())
            {
                var2.delete();
            }

            cfgFile.renameTo(var2);
            FMLLog.log(Level.SEVERE, var11, "A critical error occured reading the forgeChunkLoading.cfg file, defaults will be used - the invalid file is backed up at forgeChunkLoading.cfg.bak", new Object[0]);
        }

        config.addCustomCategoryComment("defaults", "Default configuration for forge chunk loading control");
        Property var1 = config.get("defaults", "maximumTicketCount", 200);
        var1.comment = "The default maximum ticket count for a mod which does not have an override\nin this file. This is the number of chunk loading requests a mod is allowed to make.";
        defaultMaxCount = var1.getInt(200);
        Property var12 = config.get("defaults", "maximumChunksPerTicket", 25);
        var12.comment = "The default maximum number of chunks a mod can force, per ticket, \nfor a mod without an override. This is the maximum number of chunks a single ticket can force.";
        defaultMaxChunks = var12.getInt(25);
        Property var3 = config.get("defaults", "playetTicketCount", 500);
        var3.comment = "The number of tickets a player can be assigned instead of a mod. This is shared across all mods and it is up to the mods to use it.";
        playerTicketLength = var3.getInt(500);
        Property var4 = config.get("defaults", "dormantChunkCacheSize", 0);
        var4.comment = "Unloaded chunks can first be kept in a dormant cache for quicker\nloading times. Specify the size of that cache here";
        dormantChunkCacheSize = var4.getInt(0);
        FMLLog.info("Configured a dormant chunk cache size of %d", new Object[] {Integer.valueOf(var4.getInt(0))});
        Property var5 = config.get("defaults", "enabled", true);
        var5.comment = "Are mod overrides enabled?";
        overridesEnabled = var5.getBoolean(true);
        config.addCustomCategoryComment("Forge", "Sample mod specific control section.\nCopy this section and rename the with the modid for the mod you wish to override.\nA value of zero in either entry effectively disables any chunkloading capabilities\nfor that mod");
        Property var6 = config.get("Forge", "maximumTicketCount", 200);
        var6.comment = "Maximum ticket count for the mod. Zero disables chunkloading capabilities.";
        var6 = config.get("Forge", "maximumChunksPerTicket", 25);
        var6.comment = "Maximum chunks per ticket for the mod.";
        Iterator var7 = config.categories.keySet().iterator();

        while (var7.hasNext())
        {
            String var8 = (String)var7.next();

            if (!var8.equals("Forge") && !var8.equals("defaults"))
            {
                Property var9 = config.get(var8, "maximumTicketCount", 200);
                Property var10 = config.get(var8, "maximumChunksPerTicket", 25);
            }
        }
    }

    public static Map getConfigMapFor(Object var0)
    {
        ModContainer var1 = getContainer(var0);

        if (var1 != null)
        {
            Object var2 = (Map)config.categories.get(var1.getModId());

            if (var2 == null)
            {
                var2 = Maps.newHashMap();
                config.categories.put(var1.getModId(), var2);
            }

            return (Map)var2;
        }
        else
        {
            return null;
        }
    }

    public static void addConfigProperty(Object var0, String var1, String var2, Property$Type var3)
    {
        ModContainer var4 = getContainer(var0);

        if (var4 != null)
        {
            Map var5 = (Map)config.categories.get(var4.getModId());
            var5.put(var1, new Property(var1, var2, var3));
        }
    }

    static int access$000(String var0)
    {
        return getMaxChunkDepthFor(var0);
    }
}
