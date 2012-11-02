package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MinecraftServer implements Runnable, IMojangStatistics, ICommandListener
{
    /** The logging system. */
    public static Logger log = Logger.getLogger("Minecraft");

    /** Instance of Minecraft Server. */
    private static MinecraftServer l = null;
    private final Convertable convertable;

    /** The PlayerUsageSnooper instance. */
    private final MojangStatisticsGenerator n = new MojangStatisticsGenerator("server", this);
    private final File universe;

    /** List of names of players who are online. */
    private final List p = new ArrayList();
    private final ICommandHandler q;
    public final MethodProfiler methodProfiler = new MethodProfiler();

    /** The server's hostname. */
    private String serverIp;

    /** The server's port. */
    private int s = -1;

    /** The server world instances. */
    public WorldServer[] worldServer;

    /** The ServerConfigurationManager instance. */
    private ServerConfigurationManagerAbstract t;

    /**
     * Indicates whether the server is running or not. Set to false to initiate a shutdown.
     */
    private boolean isRunning = true;

    /** Indicates to other classes that the server is safely stopped. */
    private boolean isStopped = false;

    /** Incremented every tick. */
    private int ticks = 0;

    /**
     * The task the server is currently working on(and will output on outputPercentRemaining).
     */
    public String d;

    /** The percentage of the current task finished so far. */
    public int e;

    /** True if the server is in online mode. */
    private boolean onlineMode;

    /** True if the server has animals turned on. */
    private boolean spawnAnimals;
    private boolean spawnNPCs;

    /** Indicates whether PvP is active on the server or not. */
    private boolean pvpMode;

    /** Determines if flight is allowed or not. */
    private boolean allowFlight;

    /** The server MOTD string. */
    private String motd;

    /** Maximum build height. */
    private int D;
    private long E;
    private long F;
    private long G;
    private long H;
    public final long[] f = new long[100];
    public final long[] g = new long[100];
    public final long[] h = new long[100];
    public final long[] i = new long[100];
    public final long[] j = new long[100];

    /** Stats are [dimension][tick%100] system.nanoTime is stored. */
    public long[][] k;
    private KeyPair I;

    /** Username of the server owner (for integrated servers) */
    private String J;
    private String K;
    private boolean demoMode;
    private boolean N;

    /**
     * If true, there is no need to save chunks or stop the server, because that is already being done.
     */
    private boolean O;
    private String P = "";
    private boolean Q = false;

    /**
     * Set when warned for "Can't keep up", which triggers again after 15 seconds.
     */
    private long R;
    private String S;
    private boolean T;

    public MinecraftServer(File par1File)
    {
        l = this;
        this.universe = par1File;
        this.q = new CommandDispatcher();
        this.convertable = new WorldLoaderServer(par1File);
        this.al();
    }

    /**
     * Register all dispense behaviors.
     */
    private void al()
    {
        BlockDispenser.a.a(Item.ARROW, new DispenseBehaviorArrow(this));
        BlockDispenser.a.a(Item.EGG, new DispenseBehaviorEgg(this));
        BlockDispenser.a.a(Item.SNOW_BALL, new DispenseBehaviorSnowBall(this));
        BlockDispenser.a.a(Item.EXP_BOTTLE, new DispenseBehaviorExpBottle(this));
        BlockDispenser.a.a(Item.POTION, new DispenseBehaviorPotion(this));
        BlockDispenser.a.a(Item.MONSTER_EGG, new DispenseBehaviorMonsterEgg(this));
        BlockDispenser.a.a(Item.FIREBALL, new DispenseBehaviorFireball(this));
        DispenseBehaviorMinecart var1 = new DispenseBehaviorMinecart(this);
        BlockDispenser.a.a(Item.MINECART, var1);
        BlockDispenser.a.a(Item.STORAGE_MINECART, var1);
        BlockDispenser.a.a(Item.POWERED_MINECART, var1);
        BlockDispenser.a.a(Item.BOAT, new DispenseBehaviorBoat(this));
        DispenseBehaviorFilledBucket var2 = new DispenseBehaviorFilledBucket(this);
        BlockDispenser.a.a(Item.LAVA_BUCKET, var2);
        BlockDispenser.a.a(Item.WATER_BUCKET, var2);
        BlockDispenser.a.a(Item.BUCKET, new DispenseBehaviorEmptyBucket(this));
    }

    /**
     * Initialises the server and starts it.
     */
    protected abstract boolean init() throws IOException;

    protected void b(String par1Str)
    {
        if (this.getConvertable().isConvertable(par1Str))
        {
            log.info("Converting map!");
            this.c("menu.convertingLevel");
            this.getConvertable().convert(par1Str, new ConvertProgressUpdater(this));
        }
    }

    /**
     * Typically "menu.convertingLevel", "menu.loadingLevel" or others.
     */
    protected synchronized void c(String par1Str)
    {
        this.S = par1Str;
    }

    protected void a(String var1, String var2, long var3, WorldType var5, String var6)
    {
        this.b(var1);
        this.c("menu.loadingLevel");
        this.worldServer = new WorldServer[3];
        this.k = new long[this.worldServer.length][100];
        IDataManager var7 = this.convertable.a(var1, true);
        WorldData var9 = var7.getWorldData();
        WorldSettings var8;

        if (var9 == null)
        {
            var8 = new WorldSettings(var3, this.getGamemode(), this.getGenerateStructures(), this.isHardcore(), var5);
            var8.func_82750_a(var6);
        }
        else
        {
            var8 = new WorldSettings(var9);
        }

        if (this.N)
        {
            var8.a();
        }

        for (int var10 = 0; var10 < this.worldServer.length; ++var10)
        {
            byte var11 = 0;

            if (var10 == 1)
            {
                var11 = -1;
            }

            if (var10 == 2)
            {
                var11 = 1;
            }

            if (var10 == 0)
            {
                if (this.M())
                {
                    this.worldServer[var10] = new DemoWorldServer(this, var7, var2, var11, this.methodProfiler);
                }
                else
                {
                    this.worldServer[var10] = new WorldServer(this, var7, var2, var11, var8, this.methodProfiler);
                }
            }
            else
            {
                this.worldServer[var10] = new SecondaryWorldServer(this, var7, var2, var11, var8, this.worldServer[0], this.methodProfiler);
            }

            this.worldServer[var10].addIWorldAccess(new WorldManager(this, this.worldServer[var10]));

            if (!this.I())
            {
                this.worldServer[var10].getWorldData().setGameType(this.getGamemode());
            }

            this.t.setPlayerFileData(this.worldServer);
        }

        this.c(this.getDifficulty());
        this.e();
    }

    protected void e()
    {
        int var5 = 0;
        this.c("menu.generatingTerrain");
        byte var6 = 0;
        log.info("Preparing start region for level " + var6);
        WorldServer var7 = this.worldServer[var6];
        ChunkCoordinates var8 = var7.getSpawn();
        long var9 = System.currentTimeMillis();

        for (int var11 = -192; var11 <= 192 && this.isRunning(); var11 += 16)
        {
            for (int var12 = -192; var12 <= 192 && this.isRunning(); var12 += 16)
            {
                long var13 = System.currentTimeMillis();

                if (var13 - var9 > 1000L)
                {
                    this.a_("Preparing spawn area", var5 * 100 / 625);
                    var9 = var13;
                }

                ++var5;
                var7.chunkProviderServer.getChunkAt(var8.x + var11 >> 4, var8.z + var12 >> 4);
            }
        }

        this.j();
    }

    public abstract boolean getGenerateStructures();

    public abstract EnumGamemode getGamemode();

    /**
     * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
     */
    public abstract int getDifficulty();

    /**
     * Defaults to false.
     */
    public abstract boolean isHardcore();

    /**
     * Used to display a percent remaining given text and the percentage.
     */
    protected void a_(String par1Str, int par2)
    {
        this.d = par1Str;
        this.e = par2;
        log.info(par1Str + ": " + par2 + "%");
    }

    /**
     * Set current task to null and set its percentage to 0.
     */
    protected void j()
    {
        this.d = null;
        this.e = 0;
    }

    /**
     * par1 indicates if a log message should be output.
     */
    protected void saveChunks(boolean par1)
    {
        if (!this.O)
        {
            WorldServer[] var2 = this.worldServer;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4)
            {
                WorldServer var5 = var2[var4];

                if (var5 != null)
                {
                    if (!par1)
                    {
                        log.info("Saving chunks for level \'" + var5.getWorldData().getName() + "\'/" + var5.worldProvider.getName());
                    }

                    try
                    {
                        var5.save(true, (IProgressUpdate)null);
                    }
                    catch (ExceptionWorldConflict var7)
                    {
                        log.warning(var7.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stop()
    {
        if (!this.O)
        {
            log.info("Stopping server");

            if (this.ae() != null)
            {
                this.ae().a();
            }

            if (this.t != null)
            {
                log.info("Saving players");
                this.t.savePlayers();
                this.t.r();
            }

            log.info("Saving worlds");
            this.saveChunks(false);
            WorldServer[] var1 = this.worldServer;
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3)
            {
                WorldServer var4 = var1[var3];
                var4.saveLevel();
            }

            if (this.n != null && this.n.d())
            {
                this.n.e();
            }
        }
    }

    /**
     * "getHostname" is already taken, but both return the hostname.
     */
    public String getServerIp()
    {
        return this.serverIp;
    }

    public void d(String par1Str)
    {
        this.serverIp = par1Str;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void safeShutdown()
    {
        this.isRunning = false;
    }

    public void run()
    {
        try
        {
            if (this.init())
            {
                long var1 = System.currentTimeMillis();

                for (long var50 = 0L; this.isRunning; this.Q = true)
                {
                    long var5 = System.currentTimeMillis();
                    long var7 = var5 - var1;

                    if (var7 > 2000L && var1 - this.R >= 15000L)
                    {
                        log.warning("Can\'t keep up! Did the system time change, or is the server overloaded?");
                        var7 = 2000L;
                        this.R = var1;
                    }

                    if (var7 < 0L)
                    {
                        log.warning("Time ran backwards! Did the system time change?");
                        var7 = 0L;
                    }

                    var50 += var7;
                    var1 = var5;

                    if (this.worldServer[0].everyoneDeeplySleeping())
                    {
                        this.q();
                        var50 = 0L;
                    }
                    else
                    {
                        while (var50 > 50L)
                        {
                            var50 -= 50L;
                            this.q();
                        }
                    }

                    Thread.sleep(1L);
                }
            }
            else
            {
                this.a((CrashReport)null);
            }
        }
        catch (Throwable var48)
        {
            var48.printStackTrace();
            log.log(Level.SEVERE, "Encountered an unexpected exception " + var48.getClass().getSimpleName(), var48);
            CrashReport var2 = null;

            if (var48 instanceof ReportedException)
            {
                var2 = this.b(((ReportedException)var48).a());
            }
            else
            {
                var2 = this.b(new CrashReport("Exception in server tick loop", var48));
            }

            File var3 = new File(new File(this.o(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (var2.a(var3))
            {
                log.severe("This crash report has been saved to: " + var3.getAbsolutePath());
            }
            else
            {
                log.severe("We were unable to save this crash report to disk.");
            }

            this.a(var2);
        }
        finally
        {
            try
            {
                this.stop();
                this.isStopped = true;
            }
            catch (Throwable var46)
            {
                var46.printStackTrace();
            }
            finally
            {
                this.p();
            }
        }
    }

    protected File o()
    {
        return new File(".");
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void a(CrashReport var1) {}

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    protected void p() {}

    /**
     * Main function called by run() every loop.
     */
    protected void q()
    {
        long var1 = System.nanoTime();
        AxisAlignedBB.a().a();
        ++this.ticks;

        if (this.T)
        {
            this.T = false;
            this.methodProfiler.a = true;
            this.methodProfiler.a();
        }

        this.methodProfiler.a("root");
        this.r();

        if (this.ticks % 900 == 0)
        {
            this.methodProfiler.a("save");
            this.t.savePlayers();
            this.saveChunks(true);
            this.methodProfiler.b();
        }

        this.methodProfiler.a("tallying");
        this.j[this.ticks % 100] = System.nanoTime() - var1;
        this.f[this.ticks % 100] = Packet.p - this.E;
        this.E = Packet.p;
        this.g[this.ticks % 100] = Packet.q - this.F;
        this.F = Packet.q;
        this.h[this.ticks % 100] = Packet.n - this.G;
        this.G = Packet.n;
        this.i[this.ticks % 100] = Packet.o - this.H;
        this.H = Packet.o;
        this.methodProfiler.b();
        this.methodProfiler.a("snooper");

        if (!this.n.d() && this.ticks > 100)
        {
            this.n.a();
        }

        if (this.ticks % 6000 == 0)
        {
            this.n.b();
        }

        this.methodProfiler.b();
        this.methodProfiler.b();
    }

    public void r()
    {
        this.methodProfiler.a("levels");

        for (int var1 = 0; var1 < this.worldServer.length; ++var1)
        {
            long var2 = System.nanoTime();

            if (var1 == 0 || this.getAllowNether())
            {
                WorldServer var4 = this.worldServer[var1];
                this.methodProfiler.a(var4.getWorldData().getName());
                this.methodProfiler.a("pools");
                var4.getVec3DPool().a();
                this.methodProfiler.b();

                if (this.ticks % 20 == 0)
                {
                    this.methodProfiler.a("timeSync");
                    this.t.a(new Packet4UpdateTime(var4.getTime(), var4.F()), var4.worldProvider.dimension);
                    this.methodProfiler.b();
                }

                this.methodProfiler.a("tick");
                var4.doTick();
                var4.tickEntities();
                this.methodProfiler.b();
                this.methodProfiler.a("tracker");
                var4.getTracker().updatePlayers();
                this.methodProfiler.b();
                this.methodProfiler.b();
            }

            this.k[var1][this.ticks % 100] = System.nanoTime() - var2;
        }

        this.methodProfiler.c("connection");
        this.ae().b();
        this.methodProfiler.c("players");
        this.t.tick();
        this.methodProfiler.c("tickables");
        Iterator var5 = this.p.iterator();

        while (var5.hasNext())
        {
            IUpdatePlayerListBox var6 = (IUpdatePlayerListBox)var5.next();
            var6.a();
        }

        this.methodProfiler.b();
    }

    public boolean getAllowNether()
    {
        return true;
    }

    public void func_82010_a(IUpdatePlayerListBox var1)
    {
        this.p.add(var1);
    }

    public static void main(String[] par0ArrayOfStr)
    {
        StatisticList.func_75919_a();

        try
        {
            boolean var1 = !GraphicsEnvironment.isHeadless();
            String var2 = null;
            String var3 = ".";
            String var4 = null;
            boolean var5 = false;
            boolean var6 = false;
            int var7 = -1;

            for (int var8 = 0; var8 < par0ArrayOfStr.length; ++var8)
            {
                String var9 = par0ArrayOfStr[var8];
                String var10 = var8 == par0ArrayOfStr.length - 1 ? null : par0ArrayOfStr[var8 + 1];
                boolean var11 = false;

                if (!var9.equals("nogui") && !var9.equals("--nogui"))
                {
                    if (var9.equals("--port") && var10 != null)
                    {
                        var11 = true;

                        try
                        {
                            var7 = Integer.parseInt(var10);
                        }
                        catch (NumberFormatException var13)
                        {
                            ;
                        }
                    }
                    else if (var9.equals("--singleplayer") && var10 != null)
                    {
                        var11 = true;
                        var2 = var10;
                    }
                    else if (var9.equals("--universe") && var10 != null)
                    {
                        var11 = true;
                        var3 = var10;
                    }
                    else if (var9.equals("--world") && var10 != null)
                    {
                        var11 = true;
                        var4 = var10;
                    }
                    else if (var9.equals("--demo"))
                    {
                        var5 = true;
                    }
                    else if (var9.equals("--bonusChest"))
                    {
                        var6 = true;
                    }
                }
                else
                {
                    var1 = false;
                }

                if (var11)
                {
                    ++var8;
                }
            }

            DedicatedServer var15 = new DedicatedServer(new File(var3));

            if (var2 != null)
            {
                var15.k(var2);
            }

            if (var4 != null)
            {
                var15.l(var4);
            }

            if (var7 >= 0)
            {
                var15.setPort(var7);
            }

            if (var5)
            {
                var15.b(true);
            }

            if (var6)
            {
                var15.c(true);
            }

            if (var1)
            {
                var15.func_82011_an();
            }

            var15.t();
            Runtime.getRuntime().addShutdownHook(new ThreadShutdown(var15));
        }
        catch (Exception var14)
        {
            log.log(Level.SEVERE, "Failed to start the minecraft server", var14);
        }
    }

    public void t()
    {
        (new ThreadServerApplication(this, "Server thread")).start();
    }

    /**
     * Returns a File object from the specified string.
     */
    public File e(String par1Str)
    {
        return new File(this.o(), par1Str);
    }

    /**
     * Logs the message with a level of INFO.
     */
    public void info(String par1Str)
    {
        log.info(par1Str);
    }

    /**
     * Logs the message with a level of WARN.
     */
    public void warning(String par1Str)
    {
        log.warning(par1Str);
    }

    /**
     * Gets the worldServer by the given dimension.
     */
    public WorldServer getWorldServer(int var1)
    {
        return var1 == -1 ? this.worldServer[1] : (var1 == 1 ? this.worldServer[2] : this.worldServer[0]);
    }

    /**
     * Returns the server's hostname.
     */
    public String u()
    {
        return this.serverIp;
    }

    /**
     * Never used, but "getServerPort" is already taken.
     */
    public int v()
    {
        return this.s;
    }

    /**
     * Returns the server message of the day
     */
    public String w()
    {
        return this.motd;
    }

    /**
     * Returns the server's Minecraft version as string.
     */
    public String getVersion()
    {
        return "1.4.2";
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int y()
    {
        return this.t.getPlayerCount();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int z()
    {
        return this.t.getMaxPlayers();
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getPlayers()
    {
        return this.t.d();
    }

    /**
     * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    public String getPlugins()
    {
        return "";
    }

    /**
     * Handle a command received by an RCon instance
     */
    public String h(String par1Str)
    {
        RemoteControlCommandListener.instance.c();
        this.q.a(RemoteControlCommandListener.instance, par1Str);
        return RemoteControlCommandListener.instance.d();
    }

    /**
     * Returns true if debugging is enabled, false otherwise.
     */
    public boolean isDebugging()
    {
        return false;
    }

    /**
     * Logs the error message with a level of SEVERE.
     */
    public void i(String par1Str)
    {
        log.log(Level.SEVERE, par1Str);
    }

    /**
     * If isDebuggingEnabled(), logs the message with a level of INFO.
     */
    public void j(String par1Str)
    {
        if (this.isDebugging())
        {
            log.log(Level.INFO, par1Str);
        }
    }

    public String getServerModName()
    {
        return "vanilla";
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport b(CrashReport var1)
    {
        var1.a("Is Modded", new CrashReportModded(this));
        var1.a("Profiler Position", new CrashReportProfilerPosition(this));

        if (this.worldServer != null && this.worldServer.length > 0 && this.worldServer[0] != null)
        {
            var1.a("Vec3 Pool Size", new CrashReportVec3DPoolSize(this));
        }

        if (this.t != null)
        {
            var1.a("Player Count", new CrashReportPlayers(this));
        }

        if (this.worldServer != null)
        {
            WorldServer[] var2 = this.worldServer;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4)
            {
                WorldServer var5 = var2[var4];

                if (var5 != null)
                {
                    var5.a(var1);
                }
            }
        }

        return var1;
    }

    /**
     * If par2Str begins with /, then it searches for commands, otherwise it returns players.
     */
    public List a(ICommandListener var1, String var2)
    {
        ArrayList var3 = new ArrayList();

        if (var2.startsWith("/"))
        {
            var2 = var2.substring(1);
            boolean var10 = !var2.contains(" ");
            List var11 = this.q.a(var1, var2);

            if (var11 != null)
            {
                Iterator var12 = var11.iterator();

                while (var12.hasNext())
                {
                    String var13 = (String)var12.next();

                    if (var10)
                    {
                        var3.add("/" + var13);
                    }
                    else
                    {
                        var3.add(var13);
                    }
                }
            }

            return var3;
        }
        else
        {
            String[] var4 = var2.split(" ", -1);
            String var5 = var4[var4.length - 1];
            String[] var6 = this.t.d();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8)
            {
                String var9 = var6[var8];

                if (CommandAbstract.a(var5, var9))
                {
                    var3.add(var9);
                }
            }

            return var3;
        }
    }

    /**
     * Gets mcServer.
     */
    public static MinecraftServer getServer()
    {
        return l;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getName()
    {
        return "Server";
    }

    public void sendMessage(String par1Str)
    {
        log.info(StripColor.a(par1Str));
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean a(int par1, String par2Str)
    {
        return true;
    }

    /**
     * Translates and formats the given string key with the given arguments.
     */
    public String a(String par1Str, Object ... par2ArrayOfObj)
    {
        return LocaleLanguage.a().a(par1Str, par2ArrayOfObj);
    }

    public ICommandHandler getCommandHandler()
    {
        return this.q;
    }

    /**
     * Gets KeyPair instanced in MinecraftServer.
     */
    public KeyPair F()
    {
        return this.I;
    }

    /**
     * Gets serverPort.
     */
    public int G()
    {
        return this.s;
    }

    public void setPort(int par1)
    {
        this.s = par1;
    }

    /**
     * Returns the username of the server owner (for integrated servers)
     */
    public String H()
    {
        return this.J;
    }

    /**
     * Sets the username of the owner of this server (in the case of an integrated server)
     */
    public void k(String par1Str)
    {
        this.J = par1Str;
    }

    public boolean I()
    {
        return this.J != null;
    }

    public String J()
    {
        return this.K;
    }

    public void l(String par1Str)
    {
        this.K = par1Str;
    }

    public void a(KeyPair par1KeyPair)
    {
        this.I = par1KeyPair;
    }

    public void c(int par1)
    {
        for (int var2 = 0; var2 < this.worldServer.length; ++var2)
        {
            WorldServer var3 = this.worldServer[var2];

            if (var3 != null)
            {
                if (var3.getWorldData().isHardcore())
                {
                    var3.difficulty = 3;
                    var3.setSpawnFlags(true, true);
                }
                else if (this.I())
                {
                    var3.difficulty = par1;
                    var3.setSpawnFlags(var3.difficulty > 0, true);
                }
                else
                {
                    var3.difficulty = par1;
                    var3.setSpawnFlags(this.getSpawnMonsters(), this.spawnAnimals);
                }
            }
        }
    }

    protected boolean getSpawnMonsters()
    {
        return true;
    }

    /**
     * Gets whether this is a demo or not.
     */
    public boolean M()
    {
        return this.demoMode;
    }

    /**
     * Sets whether this is a demo or not.
     */
    public void b(boolean par1)
    {
        this.demoMode = par1;
    }

    public void c(boolean par1)
    {
        this.N = par1;
    }

    public Convertable getConvertable()
    {
        return this.convertable;
    }

    /**
     * WARNING : directly calls
     * getActiveAnvilConverter().deleteWorldDirectory(theWorldServer[0].getSaveHandler().getSaveDirectoryName());
     */
    public void P()
    {
        this.O = true;
        this.getConvertable().d();

        for (int var1 = 0; var1 < this.worldServer.length; ++var1)
        {
            WorldServer var2 = this.worldServer[var1];

            if (var2 != null)
            {
                var2.saveLevel();
            }
        }

        this.getConvertable().e(this.worldServer[0].getDataManager().g());
        this.safeShutdown();
    }

    public String getTexturePack()
    {
        return this.P;
    }

    public void setTexturePack(String par1Str)
    {
        this.P = par1Str;
    }

    public void a(MojangStatisticsGenerator var1)
    {
        var1.a("whitelist_enabled", Boolean.valueOf(false));
        var1.a("whitelist_count", Integer.valueOf(0));
        var1.a("players_current", Integer.valueOf(this.y()));
        var1.a("players_max", Integer.valueOf(this.z()));
        var1.a("players_seen", Integer.valueOf(this.t.getSeenPlayers().length));
        var1.a("uses_auth", Boolean.valueOf(this.onlineMode));
        var1.a("gui_state", this.ag() ? "enabled" : "disabled");
        var1.a("avg_tick_ms", Integer.valueOf((int)(MathHelper.a(this.j) * 1.0E-6D)));
        var1.a("avg_sent_packet_count", Integer.valueOf((int)MathHelper.a(this.f)));
        var1.a("avg_sent_packet_size", Integer.valueOf((int)MathHelper.a(this.g)));
        var1.a("avg_rec_packet_count", Integer.valueOf((int)MathHelper.a(this.h)));
        var1.a("avg_rec_packet_size", Integer.valueOf((int)MathHelper.a(this.i)));
        int var2 = 0;

        for (int var3 = 0; var3 < this.worldServer.length; ++var3)
        {
            if (this.worldServer[var3] != null)
            {
                WorldServer var4 = this.worldServer[var3];
                WorldData var5 = var4.getWorldData();
                var1.a("world[" + var2 + "][dimension]", Integer.valueOf(var4.worldProvider.dimension));
                var1.a("world[" + var2 + "][mode]", var5.getGameType());
                var1.a("world[" + var2 + "][difficulty]", Integer.valueOf(var4.difficulty));
                var1.a("world[" + var2 + "][hardcore]", Boolean.valueOf(var5.isHardcore()));
                var1.a("world[" + var2 + "][generator_name]", var5.getType().name());
                var1.a("world[" + var2 + "][generator_version]", Integer.valueOf(var5.getType().getVersion()));
                var1.a("world[" + var2 + "][height]", Integer.valueOf(this.D));
                var1.a("world[" + var2 + "][chunks_loaded]", Integer.valueOf(var4.H().getLoadedChunks()));
                ++var2;
            }
        }

        var1.a("worlds", Integer.valueOf(var2));
    }

    public void b(MojangStatisticsGenerator var1)
    {
        var1.a("singleplayer", Boolean.valueOf(this.I()));
        var1.a("server_brand", this.getServerModName());
        var1.a("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
        var1.a("dedicated", Boolean.valueOf(this.T()));
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean getSnooperEnabled()
    {
        return true;
    }

    /**
     * This is checked to be 16 upon receiving the packet, otherwise the packet is ignored.
     */
    public int S()
    {
        return 16;
    }

    public abstract boolean T();

    public boolean getOnlineMode()
    {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean par1)
    {
        this.onlineMode = par1;
    }

    public boolean getSpawnAnimals()
    {
        return this.spawnAnimals;
    }

    public void setSpawnAnimals(boolean par1)
    {
        this.spawnAnimals = par1;
    }

    public boolean getSpawnNPCs()
    {
        return this.spawnNPCs;
    }

    public void setSpawnNPCs(boolean par1)
    {
        this.spawnNPCs = par1;
    }

    public boolean getPvP()
    {
        return this.pvpMode;
    }

    public void setPvP(boolean par1)
    {
        this.pvpMode = par1;
    }

    public boolean getAllowFlight()
    {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean par1)
    {
        this.allowFlight = par1;
    }

    /**
     * Return whether command blocks are enabled.
     */
    public abstract boolean getEnableCommandBlock();

    public String getMotd()
    {
        return this.motd;
    }

    public void setMotd(String par1Str)
    {
        this.motd = par1Str;
    }

    public int getMaxBuildHeight()
    {
        return this.D;
    }

    public void d(int par1)
    {
        this.D = par1;
    }

    public boolean isStopped()
    {
        return this.isStopped;
    }

    public ServerConfigurationManagerAbstract getServerConfigurationManager()
    {
        return this.t;
    }

    public void a(ServerConfigurationManagerAbstract var1)
    {
        this.t = var1;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void a(EnumGamemode var1)
    {
        for (int var2 = 0; var2 < this.worldServer.length; ++var2)
        {
            getServer().worldServer[var2].getWorldData().setGameType(var1);
        }
    }

    public abstract ServerConnection ae();

    public boolean ag()
    {
        return false;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public abstract String a(EnumGamemode var1, boolean var2);

    public int ah()
    {
        return this.ticks;
    }

    public void ai()
    {
        this.T = true;
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates b()
    {
        return new ChunkCoordinates(0, 0, 0);
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtection()
    {
        return 16;
    }

    /**
     * Gets the current player count, maximum player count, and player entity list.
     */
    public static ServerConfigurationManagerAbstract a(MinecraftServer var0)
    {
        return var0.t;
    }
}
