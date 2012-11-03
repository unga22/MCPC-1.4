package org.bukkit.craftbukkit;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.libs.jline.UnsupportedTerminal;
import org.bukkit.craftbukkit.libs.joptsimple.ArgumentAcceptingOptionSpec;
import org.bukkit.craftbukkit.libs.joptsimple.OptionException;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSpecBuilder;

public class Main
{
	public static boolean useJline = true;
	public static boolean useConsole = true;
	
	public static void main(String[] args)
	{
		MinecraftServer.main(args);
	}
	
	public static OptionSet loadOptions(String[] args)
	{
		OptionParser parser = new OptionParser()
		{
		};
		OptionSet options = null;
		try
		{
			options = parser.parse(args);
		} catch (OptionException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
		}

		if ((options == null) || (options.has("?")))
			try {
				parser.printHelpOn(System.out);
			} catch (IOException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}
		else if (options.has("v"))
			System.out.println(CraftServer.class.getPackage().getImplementationVersion());
		else
			try
		{
				String jline_UnsupportedTerminal = new String(new char[] { 'j', 'l', 'i', 'n', 'e', '.', 'U', 'n', 's', 'u', 'p', 'p', 'o', 'r', 't', 'e', 'd', 'T', 'e', 'r', 'm', 'i', 'n', 'a', 'l' });
				String jline_terminal = new String(new char[] { 'j', 'l', 'i', 'n', 'e', '.', 't', 'e', 'r', 'm', 'i', 'n', 'a', 'l' });

				useJline = !jline_UnsupportedTerminal.equals(System.getProperty(jline_terminal));

				if (options.has("nojline")) {
					System.setProperty("user.language", "en");
					useJline = false;
				}

				if (!useJline)
				{
					System.setProperty("org.bukkit.craftbukkit.libs.jline.terminal", UnsupportedTerminal.class.getName());
				}

				if (options.has("noconsole")) {
					useConsole = false;
				}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return options;
	}
	
	// disabled, cant link objects over app domains thru the new FML
	/*
	public static void main(String[] args)
	{
		OptionParser parser = new OptionParser()
		{
		};
		OptionSet options = null;
		try
		{
			options = parser.parse(args);
		} catch (OptionException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
		}

		if ((options == null) || (options.has("?")))
			try {
				parser.printHelpOn(System.out);
			} catch (IOException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}
		else if (options.has("v"))
			System.out.println(CraftServer.class.getPackage().getImplementationVersion());
		else
			try
		{
				String jline_UnsupportedTerminal = new String(new char[] { 'j', 'l', 'i', 'n', 'e', '.', 'U', 'n', 's', 'u', 'p', 'p', 'o', 'r', 't', 'e', 'd', 'T', 'e', 'r', 'm', 'i', 'n', 'a', 'l' });
				String jline_terminal = new String(new char[] { 'j', 'l', 'i', 'n', 'e', '.', 't', 'e', 'r', 'm', 'i', 'n', 'a', 'l' });

				useJline = !jline_UnsupportedTerminal.equals(System.getProperty(jline_terminal));

				if (options.has("nojline")) {
					System.setProperty("user.language", "en");
					useJline = false;
				}

				if (!useJline)
				{
					System.setProperty("org.bukkit.craftbukkit.libs.jline.terminal", UnsupportedTerminal.class.getName());
				}

				if (options.has("noconsole")) {
					useConsole = false;
				}

				MinecraftServer.main(options); - disabled
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}*/

	private static List<String> asList(String[] params)
	{
		return Arrays.asList(params);
	}
}