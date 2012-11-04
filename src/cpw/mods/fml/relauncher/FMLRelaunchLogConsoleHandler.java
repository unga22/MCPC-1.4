package cpw.mods.fml.relauncher;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.DedicatedServer;
import net.minecraft.server.MinecraftServer;

import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.libs.jline.console.ConsoleReader;
import org.bukkit.craftbukkit.util.TerminalConsoleHandler;

public class FMLRelaunchLogConsoleHandler extends ConsoleHandler 
{
	public synchronized void flush()
	{
		super.flush();
	}
}



