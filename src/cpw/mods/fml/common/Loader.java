package cpw.mods.fml.common;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Sets.SetView;
import cpw.mods.fml.common.Loader$1;
import cpw.mods.fml.common.Loader$2;
import cpw.mods.fml.common.Loader$3;
import cpw.mods.fml.common.Loader$ModIdComparator;
import cpw.mods.fml.common.LoaderState$ModState;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import cpw.mods.fml.common.event.FMLInterModComms$IMCEvent;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.functions.ModIdFunction;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.RelaunchClassLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.server.CrashReport;
import net.minecraft.server.CrashReportVersion;

public class Loader
{
    private static final Splitter DEPENDENCYPARTSPLITTER = Splitter.on(":").omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCYSPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();
    private static Loader instance;
    private static String major;
    private static String minor;
    private static String rev;
    private static String build;
    private static String mccversion;
    private static String mcpversion;
    private ModClassLoader modClassLoader = new ModClassLoader(this.getClass().getClassLoader());
    private List mods;
    private Map namedMods;
    private File canonicalConfigDir;
    private File canonicalMinecraftDir;
    private Exception capturedError;
    private File canonicalModsDir;
    private LoadController modController;
    private MinecraftDummyContainer minecraft;
    private MCPDummyContainer mcp;
    private static File minecraftDir;
    private static List injectedContainers;

    public static Loader instance()
    {
        if (instance == null)
        {
            instance = new Loader();
        }

        return instance;
    }

    public static void injectData(Object ... var0)
    {
        major = (String)var0[0];
        minor = (String)var0[1];
        rev = (String)var0[2];
        build = (String)var0[3];
        mccversion = (String)var0[4];
        mcpversion = (String)var0[5];
        minecraftDir = (File)var0[6];
        injectedContainers = (List)var0[7];
    }

    private Loader()
    {
        String var1 = (new CrashReportVersion((CrashReport)null)).a();

        if (!mccversion.equals(var1))
        {
            FMLLog.severe("This version of FML is built for Minecraft %s, we have detected Minecraft %s in your minecraft jar file", new Object[] {mccversion, var1});
            throw new LoaderException();
        }
        else
        {
            this.minecraft = new MinecraftDummyContainer(var1);
            this.mcp = new MCPDummyContainer(MetadataCollection.from(this.getClass().getResourceAsStream("/mcpmod.info"), "MCP").getMetadataForId("mcp", (Map)null));
        }
    }

    private void sortModList()
    {
        FMLLog.fine("Verifying mod requirements are satisfied", new Object[0]);
        boolean var15 = false;

        try
        {
            var15 = true;
            HashBiMap var1 = HashBiMap.create();
            Iterator var2 = this.getActiveModList().iterator();
            label186:

            while (true)
            {
                ModContainer var3;

                if (!var2.hasNext())
                {
                    var2 = this.getActiveModList().iterator();
                    HashSet var5;

                    do
                    {
                        if (!var2.hasNext())
                        {
                            FMLLog.fine("All mod requirements are satisfied", new Object[0]);
                            ModSorter var19 = new ModSorter(this.getActiveModList(), this.namedMods);

                            try
                            {
                                FMLLog.fine("Sorting mods into an ordered list", new Object[0]);
                                List var21 = var19.sort();
                                this.modController.getActiveModList().clear();
                                this.modController.getActiveModList().addAll(var21);
                                this.mods.removeAll(var21);
                                var21.addAll(this.mods);
                                this.mods = var21;
                                FMLLog.fine("Mod sorting completed successfully", new Object[0]);
                                var15 = false;
                                break label186;
                            }
                            catch (ModSortingException var16)
                            {
                                FMLLog.severe("A dependency cycle was detected in the input mod set so an ordering cannot be determined", new Object[0]);
                                FMLLog.severe("The visited mod list is %s", new Object[] {var16.getExceptionData().getVisitedNodes()});
                                FMLLog.severe("The first mod in the cycle is %s", new Object[] {var16.getExceptionData().getFirstBadNode()});
                                FMLLog.log(Level.SEVERE, var16, "The full error", new Object[0]);
                                throw new LoaderException(var16);
                            }
                        }

                        var3 = (ModContainer)var2.next();

                        if (!var3.acceptableMinecraftVersionRange().containsVersion(this.minecraft.getProcessedVersion()))
                        {
                            FMLLog.severe("The mod %s does not wish to run in Minecraft version %s. You will have to remove it to play.", new Object[] {var3.getModId(), this.getMCVersionString()});
                            throw new WrongMinecraftVersionException(var3);
                        }

                        ImmutableMap var4 = Maps.uniqueIndex(var3.getRequirements(), new Loader$1(this));
                        var5 = Sets.newHashSet();
                        SetView var6 = Sets.difference(var4.keySet(), var1.keySet());

                        if (!var6.isEmpty())
                        {
                            FMLLog.severe("The mod %s (%s) requires mods %s to be available", new Object[] {var3.getModId(), var3.getName(), var6});
                            Iterator var22 = var6.iterator();

                            while (var22.hasNext())
                            {
                                String var23 = (String)var22.next();
                                var5.add(var4.get(var23));
                            }

                            throw new MissingModsException(var5);
                        }

                        ImmutableList var7 = ImmutableList.builder().addAll(var3.getDependants()).addAll(var3.getDependencies()).build();
                        Iterator var8 = var7.iterator();

                        while (var8.hasNext())
                        {
                            ArtifactVersion var9 = (ArtifactVersion)var8.next();

                            if (var1.containsKey(var9.getLabel()) && !var9.containsVersion((ArtifactVersion)var1.get(var9.getLabel())))
                            {
                                var5.add(var9);
                            }
                        }
                    }
                    while (var5.isEmpty());

                    FMLLog.severe("The mod %s (%s) requires mod versions %s to be available", new Object[] {var3.getModId(), var3.getName(), var5});
                    throw new MissingModsException(var5);
                }

                var3 = (ModContainer)var2.next();
                var1.put(var3.getModId(), var3.getProcessedVersion());
            }
        }
        finally
        {
            if (var15)
            {
                FMLLog.fine("Mod sorting data:", new Object[0]);
                Iterator var11 = this.getActiveModList().iterator();

                while (var11.hasNext())
                {
                    ModContainer var12 = (ModContainer)var11.next();

                    if (!var12.isImmutable())
                    {
                        FMLLog.fine("\t%s(%s:%s): %s (%s)", new Object[] {var12.getModId(), var12.getName(), var12.getVersion(), var12.getSource().getName(), var12.getSortingRules()});
                    }
                }

                if (this.mods.size() == 0)
                {
                    FMLLog.fine("No mods found to sort", new Object[0]);
                }
            }
        }

        FMLLog.fine("Mod sorting data:", new Object[0]);
        Iterator var18 = this.getActiveModList().iterator();

        while (var18.hasNext())
        {
            ModContainer var20 = (ModContainer)var18.next();

            if (!var20.isImmutable())
            {
                FMLLog.fine("\t%s(%s:%s): %s (%s)", new Object[] {var20.getModId(), var20.getName(), var20.getVersion(), var20.getSource().getName(), var20.getSortingRules()});
            }
        }

        if (this.mods.size() == 0)
        {
            FMLLog.fine("No mods found to sort", new Object[0]);
        }
    }

    private ModDiscoverer identifyMods()
    {
        FMLLog.fine("Building injected Mod Containers %s", new Object[] {injectedContainers});
        this.mods.add(new InjectedModContainer(this.mcp, new File("minecraft.jar")));
        File var1 = new File(minecraftDir, "coremods");
        ModContainer var4;

        for (Iterator var2 = injectedContainers.iterator(); var2.hasNext(); this.mods.add(new InjectedModContainer(var4, var1)))
        {
            String var3 = (String)var2.next();

            try
            {
                var4 = (ModContainer)Class.forName(var3, true, this.modClassLoader).newInstance();
            }
            catch (Exception var6)
            {
                FMLLog.log(Level.SEVERE, var6, "A problem occured instantiating the injected mod container %s", new Object[] {var3});
                throw new LoaderException(var6);
            }
        }

        ModDiscoverer var7 = new ModDiscoverer();
        FMLLog.fine("Attempting to load mods contained in the minecraft jar file and associated classes", new Object[0]);
        var7.findClasspathMods(this.modClassLoader);
        FMLLog.fine("Minecraft jar mods loaded successfully", new Object[0]);
        FMLLog.info("Searching %s for mods", new Object[] {this.canonicalModsDir.getAbsolutePath()});
        var7.findModDirMods(this.canonicalModsDir);
        this.mods.addAll(var7.identifyMods());
        this.identifyDuplicates(this.mods);
        this.namedMods = Maps.uniqueIndex(this.mods, new ModIdFunction());
        FMLLog.info("Forge Mod Loader has identified %d mod%s to load", new Object[] {Integer.valueOf(this.mods.size()), this.mods.size() != 1 ? "s" : ""});
        return var7;
    }

    private void identifyDuplicates(List var1)
    {
        TreeMultimap var2 = TreeMultimap.create(new Loader$ModIdComparator(this, (Loader$1)null), Ordering.arbitrary());
        Iterator var3 = var1.iterator();

        while (var3.hasNext())
        {
            ModContainer var4 = (ModContainer)var3.next();

            if (var4.getSource() != null)
            {
                var2.put(var4, var4.getSource());
            }
        }

        ImmutableMultiset var7 = Multisets.copyHighestCountFirst(var2.keys());
        LinkedHashMultimap var8 = LinkedHashMultimap.create();
        Iterator var5 = var7.entrySet().iterator();

        while (var5.hasNext())
        {
            Entry var6 = (Entry)var5.next();

            if (var6.getCount() > 1)
            {
                FMLLog.severe("Found a duplicate mod %s at %s", new Object[] {((ModContainer)var6.getElement()).getModId(), var2.get(var6.getElement())});
                var8.putAll(var6.getElement(), var2.get(var6.getElement()));
            }
        }

        if (!var8.isEmpty())
        {
            throw new DuplicateModsFoundException(var8);
        }
    }

    private void initializeLoader()
    {
        File var1 = new File(minecraftDir, "mods");
        File var2 = new File(minecraftDir, "config");
        String var3;
        String var4;

        try
        {
            this.canonicalMinecraftDir = minecraftDir.getCanonicalFile();
            var3 = var1.getCanonicalPath();
            var4 = var2.getCanonicalPath();
            this.canonicalConfigDir = var2.getCanonicalFile();
            this.canonicalModsDir = var1.getCanonicalFile();
        }
        catch (IOException var6)
        {
            FMLLog.log(Level.SEVERE, var6, "Failed to resolve loader directories: mods : %s ; config %s", new Object[] {this.canonicalModsDir.getAbsolutePath(), var2.getAbsolutePath()});
            throw new LoaderException(var6);
        }

        boolean var5;

        if (!this.canonicalModsDir.exists())
        {
            FMLLog.info("No mod directory found, creating one: %s", new Object[] {var3});
            var5 = this.canonicalModsDir.mkdir();

            if (!var5)
            {
                FMLLog.severe("Unable to create the mod directory %s", new Object[] {var3});
                throw new LoaderException();
            }

            FMLLog.info("Mod directory created successfully", new Object[0]);
        }

        if (!this.canonicalConfigDir.exists())
        {
            FMLLog.fine("No config directory found, creating one: %s", new Object[] {var4});
            var5 = this.canonicalConfigDir.mkdir();

            if (!var5)
            {
                FMLLog.severe("Unable to create the config directory %s", new Object[] {var4});
                throw new LoaderException();
            }

            FMLLog.info("Config directory created successfully", new Object[0]);
        }

        if (!this.canonicalModsDir.isDirectory())
        {
            FMLLog.severe("Attempting to load mods from %s, which is not a directory", new Object[] {var3});
            throw new LoaderException();
        }
        else if (!var2.isDirectory())
        {
            FMLLog.severe("Attempting to load configuration from %s, which is not a directory", new Object[] {var4});
            throw new LoaderException();
        }
    }

    public List getModList()
    {
        return instance().mods != null ? ImmutableList.copyOf(instance().mods) : ImmutableList.of();
    }

    public void loadMods()
    {
        this.initializeLoader();
        this.mods = Lists.newArrayList();
        this.namedMods = Maps.newHashMap();
        this.modController = new LoadController(this);
        this.modController.transition(LoaderState.LOADING);
        ModDiscoverer var1 = this.identifyMods();
        this.disableRequestedMods();
        this.modController.distributeStateMessage(FMLLoadEvent.class);
        this.sortModList();
        this.mods = ImmutableList.copyOf(this.mods);
        Iterator var2 = var1.getNonModLibs().iterator();

        while (var2.hasNext())
        {
            File var3 = (File)var2.next();

            if (var3.isFile())
            {
                FMLLog.info("FML has found a non-mod file %s in your mods directory. It will now be injected into your classpath. This could severe stability issues, it should be removed if possible.", new Object[] {var3.getName()});

                try
                {
                    this.modClassLoader.addFile(var3);
                }
                catch (MalformedURLException var5)
                {
                    FMLLog.log(Level.SEVERE, var5, "Encountered a weird problem with non-mod file injection : %s", new Object[] {var3.getName()});
                }
            }
        }

        this.modController.transition(LoaderState.CONSTRUCTING);
        this.modController.distributeStateMessage(LoaderState.CONSTRUCTING, new Object[] {this.modClassLoader, var1.getASMTable()});
        this.modController.transition(LoaderState.PREINITIALIZATION);
        this.modController.distributeStateMessage(LoaderState.PREINITIALIZATION, new Object[] {var1.getASMTable(), this.canonicalConfigDir});
        this.modController.transition(LoaderState.INITIALIZATION);
    }

    private void disableRequestedMods()
    {
        String var1 = System.getProperty("fml.modStates", "");
        FMLLog.fine("Received a system property request \'%s\'", new Object[] {var1});
        Map var2 = Splitter.on(CharMatcher.anyOf(";:")).omitEmptyStrings().trimResults().withKeyValueSeparator("=").split(var1);
        FMLLog.fine("System property request managing the state of %d mods", new Object[] {Integer.valueOf(var2.size())});
        HashMap var3 = Maps.newHashMap();
        File var4 = new File(this.canonicalConfigDir, "fmlModState.properties");
        Properties var5 = new Properties();

        if (var4.exists() && var4.isFile())
        {
            FMLLog.fine("Found a mod state file %s", new Object[] {var4.getName()});

            try
            {
                var5.load(new FileReader(var4));
                FMLLog.fine("Loaded states for %d mods from file", new Object[] {Integer.valueOf(var5.size())});
            }
            catch (Exception var9)
            {
                FMLLog.log(Level.INFO, var9, "An error occurred reading the fmlModState.properties file", new Object[0]);
            }
        }

        var3.putAll(Maps.fromProperties(var5));
        var3.putAll(var2);
        FMLLog.fine("After merging, found state information for %d mods", new Object[] {Integer.valueOf(var3.size())});
        Map var6 = Maps.transformValues(var3, new Loader$2(this));
        Iterator var7 = var6.entrySet().iterator();

        while (var7.hasNext())
        {
            java.util.Map.Entry var8 = (java.util.Map.Entry)var7.next();

            if (this.namedMods.containsKey(var8.getKey()))
            {
                FMLLog.info("Setting mod %s to enabled state %b", new Object[] {var8.getKey(), var8.getValue()});
                ((ModContainer)this.namedMods.get(var8.getKey())).setEnabledState(((Boolean)var8.getValue()).booleanValue());
            }
        }
    }

    public static boolean isModLoaded(String var0)
    {
        return instance().namedMods.containsKey(var0) && instance().modController.getModState((ModContainer)instance.namedMods.get(var0)) != LoaderState$ModState.DISABLED;
    }

    public File getConfigDir()
    {
        return this.canonicalConfigDir;
    }

    public String getCrashInformation()
    {
        StringBuilder var1 = new StringBuilder();
        List var2 = FMLCommonHandler.instance().getBrandings();
        Joiner.on(' ').skipNulls().appendTo(var1, var2.subList(1, var2.size()));

        if (this.modController != null)
        {
            this.modController.printModStates(var1);
        }

        return var1.toString();
    }

    public String getFMLVersionString()
    {
        return String.format("%s.%s.%s.%s", new Object[] {major, minor, rev, build});
    }

    public ClassLoader getModClassLoader()
    {
        return this.modClassLoader;
    }

    public void computeDependencies(String var1, Set var2, List var3, List var4)
    {
        if (var1 != null && var1.length() != 0)
        {
            boolean var5 = false;
            Iterator var6 = DEPENDENCYSPLITTER.split(var1).iterator();

            while (var6.hasNext())
            {
                String var7 = (String)var6.next();
                ArrayList var8 = Lists.newArrayList(DEPENDENCYPARTSPLITTER.split(var7));

                if (var8.size() != 2)
                {
                    var5 = true;
                }
                else
                {
                    String var9 = (String)var8.get(0);
                    String var10 = (String)var8.get(1);
                    boolean var11 = var10.startsWith("*");

                    if (var11 && var10.length() > 1)
                    {
                        var5 = true;
                    }
                    else
                    {
                        if ("required-before".equals(var9) || "required-after".equals(var9))
                        {
                            if (var11)
                            {
                                var5 = true;
                                continue;
                            }

                            var2.add(VersionParser.parseVersionReference(var10));
                        }

                        if (var11 && var10.indexOf(64) > -1)
                        {
                            var5 = true;
                        }
                        else if (!"required-before".equals(var9) && !"before".equals(var9))
                        {
                            if (!"required-after".equals(var9) && !"after".equals(var9))
                            {
                                var5 = true;
                            }
                            else
                            {
                                var3.add(VersionParser.parseVersionReference(var10));
                            }
                        }
                        else
                        {
                            var4.add(VersionParser.parseVersionReference(var10));
                        }
                    }
                }
            }

            if (var5)
            {
                FMLLog.log(Level.WARNING, "Unable to parse dependency string %s", new Object[] {var1});
                throw new LoaderException();
            }
        }
    }

    public Map getIndexedModList()
    {
        return ImmutableMap.copyOf(this.namedMods);
    }

    public void initializeMods()
    {
        this.modController.distributeStateMessage(LoaderState.INITIALIZATION, new Object[0]);
        this.modController.transition(LoaderState.POSTINITIALIZATION);
        this.modController.distributeStateMessage(FMLInterModComms$IMCEvent.class);
        this.modController.distributeStateMessage(LoaderState.POSTINITIALIZATION, new Object[0]);
        this.modController.transition(LoaderState.AVAILABLE);
        this.modController.distributeStateMessage(LoaderState.AVAILABLE, new Object[0]);
        FMLLog.info("Forge Mod Loader has successfully loaded %d mod%s", new Object[] {Integer.valueOf(this.mods.size()), this.mods.size() == 1 ? "" : "s"});
    }

    public ICrashCallable getCallableCrashInformation()
    {
        return new Loader$3(this);
    }

    public List getActiveModList()
    {
        return (List)(this.modController != null ? this.modController.getActiveModList() : ImmutableList.of());
    }

    public LoaderState$ModState getModState(ModContainer var1)
    {
        return this.modController.getModState(var1);
    }

    public String getMCVersionString()
    {
        return "Minecraft " + mccversion;
    }

    public void serverStarting(Object var1)
    {
        this.modController.distributeStateMessage(LoaderState.SERVER_STARTING, new Object[] {var1});
        this.modController.transition(LoaderState.SERVER_STARTING);
    }

    public void serverStarted()
    {
        this.modController.distributeStateMessage(LoaderState.SERVER_STARTED, new Object[0]);
        this.modController.transition(LoaderState.SERVER_STARTED);
    }

    public void serverStopping()
    {
        this.modController.distributeStateMessage(LoaderState.SERVER_STOPPING, new Object[0]);
        this.modController.transition(LoaderState.SERVER_STOPPING);
        this.modController.transition(LoaderState.AVAILABLE);
    }

    public BiMap getModObjectList()
    {
        return this.modController.getModObjectList();
    }

    public BiMap getReversedModObjectList()
    {
        return this.getModObjectList().inverse();
    }

    public ModContainer activeModContainer()
    {
        return this.modController.activeContainer();
    }

    public boolean isInState(LoaderState var1)
    {
        return this.modController.isInState(var1);
    }

    public MinecraftDummyContainer getMinecraftModContainer()
    {
        return this.minecraft;
    }

    public boolean hasReachedState(LoaderState var1)
    {
        return this.modController.hasReachedState(var1);
    }

    public String getMCPVersionString()
    {
        return String.format("MCP v%s", new Object[] {mcpversion});
    }
}
