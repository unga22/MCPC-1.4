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

public class FMLRelaunchLog$ConsoleHandler extends ConsoleHandler 
{
	public static ConsoleReader reader = null;
	
	public synchronized void flush()
	{
		try {
			if (Main.useJline && reader != null) {
				reader.print("\r");
				reader.flush();
				super.flush();
				try {
					reader.drawLine();
				} catch (Throwable ex) {
					reader.getCursorBuffer().clear();
				}
				reader.flush();
			} else {
				super.flush();
			}
		} catch (IOException ex) {
			Logger.getLogger(TerminalConsoleHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}



