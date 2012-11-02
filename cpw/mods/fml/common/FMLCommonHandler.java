package cpw.mods.fml.common;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry$EntityRegistration;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.server.FMLServerHandler;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.server.CrashReport;
import net.minecraft.server.DedicatedServer;
import net.minecraft.server.DedicatedServerConnectionThread;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet131ItemData;
import net.minecraft.server.ThreadServerApplication;
import net.minecraft.server.World;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldNBTStorage;

public class FMLCommonHandler
{
    private static final FMLCommonHandler INSTANCE = new FMLCommonHandler();
    private IFMLSidedHandler sidedDelegate;
    private List scheduledClientTicks = Lists.newArrayList();
    private List scheduledServerTicks = Lists.newArrayList();
    private Class forge;
    private boolean noForge;
    private List brandings;
    private List crashCallables = Lists.newArrayList(new ICrashCallable[] {Loader.instance().getCallableCrashInformation()});
    private Set<WorldNBTStorage> handlerSet = new HashSet<WorldNBTStorage>();

    public void beginLoading(IFMLSidedHandler var1)
    {
        this.sidedDelegate = var1;
        FMLLog.info("Attempting early MinecraftForge initialization", new Object[0]);
        this.callForgeMethod("initialize");
        this.callForgeMethod("registerCrashCallable");
        FMLLog.info("Completed early MinecraftForge initialization", new Object[0]);
    }

    public void rescheduleTicks(Side var1)
    {
        TickRegistry.updateTickQueue(var1.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks, var1);
    }

    public void tickStart(EnumSet var1, Side var2, Object ... var3)
    {
        List var4 = var2.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks;

        if (var4.size() != 0)
        {
            Iterator var5 = var4.iterator();

            while (var5.hasNext())
            {
                IScheduledTickHandler var6 = (IScheduledTickHandler)var5.next();
                EnumSet var7 = EnumSet.copyOf((EnumSet)Objects.firstNonNull(var6.ticks(), EnumSet.noneOf(TickType.class)));
                var7.removeAll(EnumSet.complementOf(var1));

                if (!var7.isEmpty())
                {
                    var6.tickStart(var7, var3);
                }
            }
        }
    }

    public void tickEnd(EnumSet var1, Side var2, Object ... var3)
    {
        List var4 = var2.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks;

        if (var4.size() != 0)
        {
            Iterator var5 = var4.iterator();

            while (var5.hasNext())
            {
                IScheduledTickHandler var6 = (IScheduledTickHandler)var5.next();
                EnumSet var7 = EnumSet.copyOf((EnumSet)Objects.firstNonNull(var6.ticks(), EnumSet.noneOf(TickType.class)));
                var7.removeAll(EnumSet.complementOf(var1));

                if (!var7.isEmpty())
                {
                    var6.tickEnd(var7, var3);
                }
            }
        }
    }

    public static FMLCommonHandler instance()
    {
        return INSTANCE;
    }

    public ModContainer findContainerFor(Object var1)
    {
        return (ModContainer)Loader.instance().getReversedModObjectList().get(var1);
    }

    public Logger getFMLLogger()
    {
        return FMLLog.getLogger();
    }

    public Side getSide()
    {
        return this.sidedDelegate.getSide();
    }

    public Side getEffectiveSide()
    {
        Thread var1 = Thread.currentThread();
        return !(var1 instanceof ThreadServerApplication) && !(var1 instanceof DedicatedServerConnectionThread) ? Side.CLIENT : Side.SERVER;
    }

    public void raiseException(Throwable var1, String var2, boolean var3)
    {
        instance().getFMLLogger().throwing("FMLHandler", "raiseException", var1);

        if (var3)
        {
            this.getSidedDelegate().haltGame(var2, var1);
        }
    }

    private Class findMinecraftForge()
    {
        if (this.forge == null && !this.noForge)
        {
            try
            {
                this.forge = Class.forName("net.minecraftforge.common.MinecraftForge");
            }
            catch (Exception var2)
            {
                this.noForge = true;
            }
        }

        return this.forge;
    }

    private Object callForgeMethod(String var1)
    {
        if (this.noForge)
        {
            return null;
        }
        else
        {
            try
            {
                return this.findMinecraftForge().getMethod(var1, new Class[0]).invoke((Object)null, new Object[0]);
            }
            catch (Exception var3)
            {
                return null;
            }
        }
    }

    public void computeBranding()
    {
        if (this.brandings == null)
        {
            Builder var1 = ImmutableList.builder();
            var1.add(Loader.instance().getMCVersionString());
            var1.add(Loader.instance().getMCPVersionString());
            var1.add("FML v" + Loader.instance().getFMLVersionString());
            String var2 = (String)this.callForgeMethod("getBrandingVersion");

            if (!Strings.isNullOrEmpty(var2))
            {
                var1.add(var2);
            }

            var1.addAll(this.sidedDelegate.getAdditionalBrandingInformation());

            try
            {
                Properties var3 = new Properties();
                var3.load(this.getClass().getClassLoader().getResourceAsStream("fmlbranding.properties"));
                var1.add(var3.getProperty("fmlbranding"));
            }
            catch (Exception var5)
            {
                ;
            }

            int var6 = Loader.instance().getModList().size();
            int var4 = Loader.instance().getActiveModList().size();
            var1.add(String.format("%d mod%s loaded, %d mod%s active", new Object[] {Integer.valueOf(var6), var6 != 1 ? "s" : "", Integer.valueOf(var4), var4 != 1 ? "s" : ""}));
            this.brandings = var1.build();
        }
    }

    public List getBrandings()
    {
        if (this.brandings == null)
        {
            this.computeBranding();
        }

        return ImmutableList.copyOf(this.brandings);
    }

    public IFMLSidedHandler getSidedDelegate()
    {
        return this.sidedDelegate;
    }

    public void onPostServerTick()
    {
        this.tickEnd(EnumSet.of(TickType.SERVER), Side.SERVER, new Object[0]);
    }

    public void onPostWorldTick(Object var1)
    {
        this.tickEnd(EnumSet.of(TickType.WORLD), Side.SERVER, new Object[] {var1});
    }

    public void onPreServerTick()
    {
        this.tickStart(EnumSet.of(TickType.SERVER), Side.SERVER, new Object[0]);
    }

    public void onPreWorldTick(Object var1)
    {
        this.tickStart(EnumSet.of(TickType.WORLD), Side.SERVER, new Object[] {var1});
    }

    public void onWorldLoadTick(World[] var1)
    {
        this.rescheduleTicks(Side.SERVER);
        World[] var2 = var1;
        int var3 = var1.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            World var5 = var2[var4];
            this.tickStart(EnumSet.of(TickType.WORLDLOAD), Side.SERVER, new Object[] {var5});
        }
    }

    public void handleServerStarting(MinecraftServer var1)
    {
        Loader.instance().serverStarting(var1);
    }

    public void handleServerStarted()
    {
        Loader.instance().serverStarted();
    }

    public void handleServerStopping()
    {
        Loader.instance().serverStopping();
    }

    public MinecraftServer getMinecraftServerInstance()
    {
        return this.sidedDelegate.getServer();
    }

    public void showGuiScreen(Object var1)
    {
        this.sidedDelegate.showGuiScreen(var1);
    }

    public Entity spawnEntityIntoClientWorld(EntityRegistry$EntityRegistration var1, EntitySpawnPacket var2)
    {
        return this.sidedDelegate.spawnEntityIntoClientWorld(var1, var2);
    }

    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket var1)
    {
        this.sidedDelegate.adjustEntityLocationOnClient(var1);
    }

    public void onServerStart(DedicatedServer var1)
    {
        FMLServerHandler.instance();
        this.sidedDelegate.beginServerLoading(var1);
    }

    public void onServerStarted()
    {
        this.sidedDelegate.finishServerLoading();
    }

    public void onPreClientTick()
    {
        this.tickStart(EnumSet.of(TickType.CLIENT), Side.CLIENT, new Object[0]);
    }

    public void onPostClientTick()
    {
        this.tickEnd(EnumSet.of(TickType.CLIENT), Side.CLIENT, new Object[0]);
    }

    public void onRenderTickStart(float var1)
    {
        this.tickStart(EnumSet.of(TickType.RENDER), Side.CLIENT, new Object[] {Float.valueOf(var1)});
    }

    public void onRenderTickEnd(float var1)
    {
        this.tickEnd(EnumSet.of(TickType.RENDER), Side.CLIENT, new Object[] {Float.valueOf(var1)});
    }

    public void onPlayerPreTick(EntityHuman var1)
    {
        Side var2 = var1 instanceof EntityPlayer ? Side.SERVER : Side.CLIENT;
        this.tickStart(EnumSet.of(TickType.PLAYER), var2, new Object[] {var1});
    }

    public void onPlayerPostTick(EntityHuman var1)
    {
        Side var2 = var1 instanceof EntityPlayer ? Side.SERVER : Side.CLIENT;
        this.tickEnd(EnumSet.of(TickType.PLAYER), var2, new Object[] {var1});
    }

    public void registerCrashCallable(ICrashCallable var1)
    {
        this.crashCallables.add(var1);
    }

    public void enhanceCrashReport(CrashReport var1)
    {
        Iterator var2 = this.crashCallables.iterator();

        while (var2.hasNext())
        {
            ICrashCallable var3 = (ICrashCallable)var2.next();
            var1.a(var3.getLabel(), var3);
        }
    }

    public void handleTinyPacket(NetHandler var1, Packet131ItemData var2)
    {
        this.sidedDelegate.handleTinyPacket(var1, var2);
    }

    public void handleWorldDataSave(WorldNBTStorage var1, WorldData var2, NBTTagCompound var3)
    {
        Iterator var4 = Loader.instance().getModList().iterator();

        while (var4.hasNext())
        {
            ModContainer var5 = (ModContainer)var4.next();

            if (var5 instanceof InjectedModContainer)
            {
                WorldAccessContainer var6 = ((InjectedModContainer)var5).getWrappedWorldAccessContainer();

                if (var6 != null)
                {
                    NBTTagCompound var7 = var6.getDataForWriting(var1, var2);
                    var3.setCompound(var5.getModId(), var7);
                }
            }
        }
    }

    public void handleWorldDataLoad(WorldNBTStorage var1, WorldData var2, NBTTagCompound var3)
    {
        if (this.getEffectiveSide() == Side.SERVER)
        {
            if (!this.handlerSet.contains(var1))
            {
                this.handlerSet.add(var1);
                HashMap var4 = Maps.newHashMap();
                var2.setAdditionalProperties(var4);
                Iterator var5 = Loader.instance().getModList().iterator();

                while (var5.hasNext())
                {
                    ModContainer var6 = (ModContainer)var5.next();

                    if (var6 instanceof InjectedModContainer)
                    {
                        WorldAccessContainer var7 = ((InjectedModContainer)var6).getWrappedWorldAccessContainer();

                        if (var7 != null)
                        {
                            var7.readData(var1, var2, var4, var3.getCompound(var6.getModId()));
                        }
                    }
                }
            }
        }
    }
}
