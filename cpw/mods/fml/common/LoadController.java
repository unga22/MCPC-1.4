package cpw.mods.fml.common;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.LoaderState$ModState;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class LoadController
{
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap eventChannels;
    private LoaderState state;
    private Multimap modStates = ArrayListMultimap.create();
    private Multimap errors = ArrayListMultimap.create();
    private Map modList;
    private List activeModList = Lists.newArrayList();
    private ModContainer activeContainer;
    private BiMap modObjectList;

    public LoadController(Loader var1)
    {
        this.loader = var1;
        this.masterChannel = new EventBus("FMLMainChannel");
        this.masterChannel.register(this);
        this.state = LoaderState.NOINIT;
    }

    @Subscribe
    public void buildModList(FMLLoadEvent var1)
    {
        this.modList = this.loader.getIndexedModList();
        Builder var2 = ImmutableMap.builder();
        Iterator var3 = this.loader.getModList().iterator();

        while (var3.hasNext())
        {
            ModContainer var4 = (ModContainer)var3.next();
            EventBus var5 = new EventBus(var4.getModId());
            boolean var6 = var4.registerBus(var5, this);

            if (var6)
            {
                FMLLog.fine("Activating mod %s", new Object[] {var4.getModId()});
                this.activeModList.add(var4);
                this.modStates.put(var4.getModId(), LoaderState$ModState.UNLOADED);
                var2.put(var4.getModId(), var5);
            }
            else
            {
                FMLLog.warning("Mod %s has been disabled through configuration", new Object[] {var4.getModId()});
                this.modStates.put(var4.getModId(), LoaderState$ModState.UNLOADED);
                this.modStates.put(var4.getModId(), LoaderState$ModState.DISABLED);
            }
        }

        this.eventChannels = var2.build();
    }

    public void distributeStateMessage(LoaderState var1, Object ... var2)
    {
        if (var1.hasEvent())
        {
            this.masterChannel.post(var1.getEvent(var2));
        }
    }

    public void transition(LoaderState var1)
    {
        LoaderState var2 = this.state;
        this.state = this.state.transition(!this.errors.isEmpty());

        if (this.state != var1)
        {
            Throwable var3 = null;
            FMLLog.severe("Fatal errors were detected during the transition from %s to %s. Loading cannot continue", new Object[] {var2, var1});
            StringBuilder var4 = new StringBuilder();
            this.printModStates(var4);
            FMLLog.severe(var4.toString(), new Object[0]);
            FMLLog.severe("The following problems were captured during this phase", new Object[0]);
            Iterator var5 = this.errors.entries().iterator();

            while (var5.hasNext())
            {
                Entry var6 = (Entry)var5.next();
                FMLLog.log(Level.SEVERE, (Throwable)var6.getValue(), "Caught exception from %s", new Object[] {var6.getKey()});

                if (var6.getValue() instanceof IFMLHandledException)
                {
                    var3 = (Throwable)var6.getValue();
                }
                else if (var3 == null)
                {
                    var3 = (Throwable)var6.getValue();
                }
            }

            if (var3 != null && var3 instanceof RuntimeException)
            {
                throw(RuntimeException)var3;
            }
            else
            {
                throw new LoaderException(var3);
            }
        }
    }

    public ModContainer activeContainer()
    {
        return this.activeContainer;
    }

    @Subscribe
    public void propogateStateMessage(FMLEvent var1)
    {
        if (var1 instanceof FMLPreInitializationEvent)
        {
            this.modObjectList = this.buildModObjectList();
        }

        Iterator var2 = this.activeModList.iterator();

        while (var2.hasNext())
        {
            ModContainer var3 = (ModContainer)var2.next();
            this.activeContainer = var3;
            String var4 = var3.getModId();
            var1.applyModContainer(this.activeContainer());
            FMLLog.finer("Sending event %s to mod %s", new Object[] {var1.getEventType(), var4});
            ((EventBus)this.eventChannels.get(var4)).post(var1);
            FMLLog.finer("Sent event %s to mod %s", new Object[] {var1.getEventType(), var4});
            this.activeContainer = null;

            if (var1 instanceof FMLStateEvent)
            {
                if (!this.errors.containsKey(var4))
                {
                    this.modStates.put(var4, ((FMLStateEvent)var1).getModState());
                }
                else
                {
                    this.modStates.put(var4, LoaderState$ModState.ERRORED);
                }
            }
        }
    }

    public ImmutableBiMap buildModObjectList()
    {
        com.google.common.collect.ImmutableBiMap.Builder var1 = ImmutableBiMap.builder();
        Iterator var2 = this.activeModList.iterator();

        while (var2.hasNext())
        {
            ModContainer var3 = (ModContainer)var2.next();

            if (!var3.isImmutable() && var3.getMod() != null)
            {
                var1.put(var3, var3.getMod());
            }

            if (var3.getMod() == null && !var3.isImmutable() && this.state != LoaderState.CONSTRUCTING)
            {
                FMLLog.severe("There is a severe problem with %s - it appears not to have constructed correctly", new Object[] {var3.getModId()});

                if (this.state != LoaderState.CONSTRUCTING)
                {
                    this.errorOccurred(var3, new RuntimeException());
                }
            }
        }

        return var1.build();
    }

    public void errorOccurred(ModContainer var1, Throwable var2)
    {
        if (var2 instanceof InvocationTargetException)
        {
            this.errors.put(var1.getModId(), ((InvocationTargetException)var2).getCause());
        }
        else
        {
            this.errors.put(var1.getModId(), var2);
        }
    }

    public void printModStates(StringBuilder var1)
    {
        Iterator var2 = this.loader.getModList().iterator();

        while (var2.hasNext())
        {
            ModContainer var3 = (ModContainer)var2.next();
            var1.append("\n\t").append(var3.getModId()).append(" [").append(var3.getName()).append("] (").append(var3.getSource().getName()).append(") ");
            Joiner.on("->").appendTo(var1, this.modStates.get(var3.getModId()));
        }
    }

    public List getActiveModList()
    {
        return this.activeModList;
    }

    public LoaderState$ModState getModState(ModContainer var1)
    {
        return (LoaderState$ModState)Iterables.getLast(this.modStates.get(var1.getModId()), LoaderState$ModState.AVAILABLE);
    }

    public void distributeStateMessage(Class var1)
    {
        try
        {
            this.masterChannel.post(var1.newInstance());
        }
        catch (Exception var3)
        {
            FMLLog.log(Level.SEVERE, var3, "An unexpected exception", new Object[0]);
            throw new LoaderException(var3);
        }
    }

    public BiMap getModObjectList()
    {
        if (this.modObjectList == null)
        {
            FMLLog.severe("Detected an attempt by a mod %s to perform game activity during mod construction. This is a serious programming error.", new Object[] {this.activeContainer});
            return this.buildModObjectList();
        }
        else
        {
            return ImmutableBiMap.copyOf(this.modObjectList);
        }
    }

    public boolean isInState(LoaderState var1)
    {
        return this.state == var1;
    }

    boolean hasReachedState(LoaderState var1)
    {
        return this.state.ordinal() >= var1.ordinal() && this.state != LoaderState.ERRORED;
    }
}
