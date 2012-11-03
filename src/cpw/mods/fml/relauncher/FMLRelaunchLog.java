package cpw.mods.fml.relauncher;

import cpw.mods.fml.relauncher.FMLRelaunchLog$1;
import cpw.mods.fml.relauncher.FMLRelaunchLog$ConsoleLogThread;
import cpw.mods.fml.relauncher.FMLRelaunchLog$ConsoleLogWrapper;
import cpw.mods.fml.relauncher.FMLRelaunchLog$LoggingOutStream;
import java.io.File;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FMLRelaunchLog
{
    public static FMLRelaunchLog log = new FMLRelaunchLog();
    static File minecraftHome;
    private static boolean configured;
    private static Thread consoleLogThread;
    private static PrintStream errCache;
    private Logger myLog;

    private static void configureLogging()
    {
        LogManager.getLogManager().reset();
        Logger var0 = Logger.getLogger("global");
        var0.setLevel(Level.OFF);
        log.myLog = Logger.getLogger("ForgeModLoader");
        Logger var1 = Logger.getLogger("STDOUT");
        var1.setParent(log.myLog);
        Logger var2 = Logger.getLogger("STDERR");
        var2.setParent(log.myLog);
        FMLLogFormatter var3 = new FMLLogFormatter();
        log.myLog.setUseParentHandlers(false);
        log.myLog.addHandler(new FMLRelaunchLog$ConsoleLogWrapper((FMLRelaunchLog$1)null));
        consoleLogThread = new Thread(new FMLRelaunchLog$ConsoleLogThread((FMLRelaunchLog$1)null));
        consoleLogThread.start();
        FMLRelaunchLog$ConsoleLogThread.wrappedHandler.setLevel(Level.parse(System.getProperty("fml.log.level", "INFO")));
        FMLRelaunchLog$ConsoleLogThread.wrappedHandler.setFormatter(var3);
        log.myLog.setLevel(Level.ALL);

        try
        {
            File var4 = new File(minecraftHome, FMLRelauncher.logFileNamePattern);
            FileHandler var5 = new FileHandler(var4.getPath(), 0, 3);
            var5.setFormatter(var3);
            var5.setLevel(Level.ALL);
            log.myLog.addHandler(var5);
        }
        catch (Exception var6)
        {
            ;
        }

        errCache = System.err;
        System.setOut(new PrintStream(new FMLRelaunchLog$LoggingOutStream(var1), true));
        System.setErr(new PrintStream(new FMLRelaunchLog$LoggingOutStream(var2), true));
        configured = true;
    }

    public static void log(Level var0, String var1, Object ... var2)
    {
        if (!configured)
        {
            configureLogging();
        }

        log.myLog.log(var0, String.format(var1, var2));
    }

    public static void log(Level var0, Throwable var1, String var2, Object ... var3)
    {
        if (!configured)
        {
            configureLogging();
        }

        log.myLog.log(var0, String.format(var2, var3), var1);
    }

    public static void severe(String var0, Object ... var1)
    {
        log(Level.SEVERE, var0, var1);
    }

    public static void warning(String var0, Object ... var1)
    {
        log(Level.WARNING, var0, var1);
    }

    public static void info(String var0, Object ... var1)
    {
        log(Level.INFO, var0, var1);
    }

    public static void fine(String var0, Object ... var1)
    {
        log(Level.FINE, var0, var1);
    }

    public static void finer(String var0, Object ... var1)
    {
        log(Level.FINER, var0, var1);
    }

    public static void finest(String var0, Object ... var1)
    {
        log(Level.FINEST, var0, var1);
    }

    public Logger getLogger()
    {
    	if (this.myLog == null)
    		configureLogging();
    	
        return this.myLog;
    }

    static PrintStream access$000()
    {
        return errCache;
    }
}
