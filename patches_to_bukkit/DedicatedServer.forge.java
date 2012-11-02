package net.minecraft.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class DedicatedServer extends MinecraftServer implements IMinecraftServer
{
    private final List l = Collections.synchronizedList(new ArrayList());
    private RemoteStatusListener m;
    private RemoteControlListener n;
    private PropertyManager propertyManager;
    private boolean generateStructures;

    /** The Game Type. */
    private EnumGamemode q;
    private ServerConnection r;
    private boolean s = false;

    public DedicatedServer(File var1)
    {
        super(var1);
        new ThreadSleepForever(this);
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean init()
    {
        ThreadCommandReader var1 = new ThreadCommandReader(this);
        var1.setDaemon(true);
        var1.start();
        ConsoleLogManager.init();
        log.info("Starting minecraft server version 1.4.2");

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            log.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        log.info("Loading properties");
        this.propertyManager = new PropertyManager(new File("server.properties"));

        if (this.I())
        {
            this.d("127.0.0.1");
        }
        else
        {
            this.setOnlineMode(this.propertyManager.getBoolean("online-mode", true));
            this.d(this.propertyManager.getString("server-ip", ""));
        }

        this.setSpawnAnimals(this.propertyManager.getBoolean("spawn-animals", true));
        this.setSpawnNPCs(this.propertyManager.getBoolean("spawn-npcs", true));
        this.setPvP(this.propertyManager.getBoolean("pvp", true));
        this.setAllowFlight(this.propertyManager.getBoolean("allow-flight", false));
        this.setTexturePack(this.propertyManager.getString("texture-pack", ""));
        this.setMotd(this.propertyManager.getString("motd", "A Minecraft Server"));
        this.generateStructures = this.propertyManager.getBoolean("generate-structures", true);
        int var2 = this.propertyManager.getInt("gamemode", EnumGamemode.SURVIVAL.a());
        this.q = WorldSettings.a(var2);
        log.info("Default game type: " + this.q);
        InetAddress var3 = null;

        if (this.getServerIp().length() > 0)
        {
            var3 = InetAddress.getByName(this.getServerIp());
        }

        if (this.G() < 0)
        {
            this.setPort(this.propertyManager.getInt("server-port", 25565));
        }

        log.info("Generating keypair");
        this.a(MinecraftEncryption.b());
        log.info("Starting Minecraft server on " + (this.getServerIp().length() == 0 ? "*" : this.getServerIp()) + ":" + this.G());

        try
        {
            this.r = new DedicatedServerConnection(this, var3, this.G());
        }
        catch (IOException var16)
        {
            log.warning("**** FAILED TO BIND TO PORT!");
            log.log(Level.WARNING, "The exception was: " + var16.toString());
            log.warning("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.getOnlineMode())
        {
            log.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            log.warning("The server will make no attempt to authenticate usernames. Beware.");
            log.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            log.warning("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        this.a(new ServerConfigurationManager(this));
        long var4 = System.nanoTime();

        if (this.J() == null)
        {
            this.l(this.propertyManager.getString("level-name", "world"));
        }

        String var6 = this.propertyManager.getString("level-seed", "");
        String var7 = this.propertyManager.getString("level-type", "DEFAULT");
        String var8 = this.propertyManager.getString("generator-settings", "");
        long var9 = (new Random()).nextLong();

        if (var6.length() > 0)
        {
            try
            {
                long var11 = Long.parseLong(var6);

                if (var11 != 0L)
                {
                    var9 = var11;
                }
            }
            catch (NumberFormatException var15)
            {
                var9 = (long)var6.hashCode();
            }
        }

        WorldType var17 = WorldType.getType(var7);

        if (var17 == null)
        {
            var17 = WorldType.NORMAL;
        }

        this.d(this.propertyManager.getInt("max-build-height", 256));
        this.d((this.getMaxBuildHeight() + 8) / 16 * 16);
        this.d(MathHelper.a(this.getMaxBuildHeight(), 64, 256));
        this.propertyManager.a("max-build-height", Integer.valueOf(this.getMaxBuildHeight()));
        log.info("Preparing level \"" + this.J() + "\"");
        this.a(this.J(), this.J(), var9, var17, var8);
        long var12 = System.nanoTime() - var4;
        String var14 = String.format("%.3fs", new Object[] {Double.valueOf((double)var12 / 1.0E9D)});
        log.info("Done (" + var14 + ")! For help, type \"help\" or \"?\"");

        if (this.propertyManager.getBoolean("enable-query", false))
        {
            log.info("Starting GS4 status listener");
            this.m = new RemoteStatusListener(this);
            this.m.a();
        }

        if (this.propertyManager.getBoolean("enable-rcon", false))
        {
            log.info("Starting remote control listener");
            this.n = new RemoteControlListener(this);
            this.n.a();
        }

        return true;
    }

    public boolean getGenerateStructures()
    {
        return this.generateStructures;
    }

    public EnumGamemode getGamemode()
    {
        return this.q;
    }

    /**
     * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
     */
    public int getDifficulty()
    {
        return this.propertyManager.getInt("difficulty", 1);
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.propertyManager.getBoolean("hardcore", false);
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void a(CrashReport var1)
    {
        while (this.isRunning())
        {
            this.al();

            try
            {
                Thread.sleep(10L);
            }
            catch (InterruptedException var3)
            {
                var3.printStackTrace();
            }
        }
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport b(CrashReport var1)
    {
        var1 = super.b(var1);
        var1.a("Type", new CrashReportType(this));
        return var1;
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    protected void p()
    {
        System.exit(0);
    }

    protected void r()
    {
        super.r();
        this.al();
    }

    public boolean getAllowNether()
    {
        return this.propertyManager.getBoolean("allow-nether", true);
    }

    public boolean getSpawnMonsters()
    {
        return this.propertyManager.getBoolean("spawn-monsters", true);
    }

    public void a(MojangStatisticsGenerator var1)
    {
        var1.a("whitelist_enabled", Boolean.valueOf(this.am().getHasWhitelist()));
        var1.a("whitelist_count", Integer.valueOf(this.am().getWhitelisted().size()));
        super.a(var1);
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean getSnooperEnabled()
    {
        return this.propertyManager.getBoolean("snooper-enabled", true);
    }

    public void issueCommand(String var1, ICommandListener var2)
    {
        this.l.add(new ServerCommand(var1, var2));
    }

    public void al()
    {
        while (!this.l.isEmpty())
        {
            ServerCommand var1 = (ServerCommand)this.l.remove(0);
            this.getCommandHandler().a(var1.source, var1.command);
        }
    }

    public boolean T()
    {
        return true;
    }

    public ServerConfigurationManager am()
    {
        return (ServerConfigurationManager)super.getServerConfigurationManager();
    }

    public ServerConnection ae()
    {
        return this.r;
    }

    /**
     * Gets an integer property. If it does not exist, set it to the specified value.
     */
    public int a(String var1, int var2)
    {
        return this.propertyManager.getInt(var1, var2);
    }

    /**
     * Gets a string property. If it does not exist, set it to the specified value.
     */
    public String a(String var1, String var2)
    {
        return this.propertyManager.getString(var1, var2);
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified value.
     */
    public boolean a(String var1, boolean var2)
    {
        return this.propertyManager.getBoolean(var1, var2);
    }

    /**
     * Saves an Object with the given property name.
     */
    public void a(String var1, Object var2)
    {
        this.propertyManager.a(var1, var2);
    }

    /**
     * Saves all of the server properties to the properties file.
     */
    public void a()
    {
        this.propertyManager.savePropertiesFile();
    }

    /**
     * Returns the filename where server properties are stored
     */
    public String b_()
    {
        File var1 = this.propertyManager.c();
        return var1 != null ? var1.getAbsolutePath() : "No settings file";
    }

    public void func_82011_an()
    {
        ServerGUI.a(this);
        this.s = true;
    }

    public boolean ag()
    {
        return this.s;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String a(EnumGamemode var1, boolean var2)
    {
        return "";
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean getEnableCommandBlock()
    {
        return this.propertyManager.getBoolean("enable-command-block", false);
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtection()
    {
        return this.propertyManager.getInt("spawn-protection", super.getSpawnProtection());
    }

    public ServerConfigurationManagerAbstract getServerConfigurationManager()
    {
        return this.am();
    }
}
