package net.minecraft.server;

import cpw.mods.fml.common.FMLLog;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleLogManager
{
    /** Reference to the logger. */
    public static Logger a = Logger.getLogger("Minecraft");

    /**
     * Initialises the console logger.
     */
    public static void init()
    {
        ConsoleLogFormatter var0 = new ConsoleLogFormatter();
        a.setParent(FMLLog.getLogger());

        try
        {
            FileHandler var1 = new FileHandler("server.log", true);
            var1.setFormatter(var0);
            a.addHandler(var1);
        }
        catch (Exception var2)
        {
            a.log(Level.WARNING, "Failed to log to server.log", var2);
        }
    }
}
