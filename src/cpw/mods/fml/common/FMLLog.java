package cpw.mods.fml.common;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FMLLog
{
    private static FMLRelaunchLog coreLog = FMLRelaunchLog.log;

    public static void log(Level var0, String var1, Object ... var2)
    {
    	FMLRelaunchLog.log(var0, var1, var2);
    }

    public static void log(Level var0, Throwable var1, String var2, Object ... var3)
    {
        FMLRelaunchLog.log(var0, var1, var2, var3);
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

    public static Logger getLogger()
    {
        return coreLog.getLogger();
    }
}
