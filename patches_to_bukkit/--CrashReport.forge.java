package net.minecraft.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrashReport
{
    /** Description of the crash report. */
    private final String a;

    /** The Throwable that is the "cause" for this crash and Crash Report. */
    private final Throwable b;

    /** Holds the keys and values of all crash report sections. */
    private final Map c = new LinkedHashMap();

    /** File of crash report. */
    private File d = null;

    public CrashReport(String var1, Throwable var2)
    {
        this.a = var1;
        this.b = var2;
        this.func_71504_g();
    }

    private void func_71504_g()
    {
        this.a("Minecraft Version", new CrashReportVersion(this));
        this.a("Operating System", new CrashReportOperatingSystem(this));
        this.a("Java Version", new CrashReportJavaVersion(this));
        this.a("Java VM Version", new CrashReportJavaVMVersion(this));
        this.a("Memory", new CrashReportMemory(this));
        this.a("JVM Flags", new CrashReportJVMFlags(this));
        this.a("AABB Pool Size", new CrashReportAABBPoolSize(this));
    }

    /**
     * Adds a Crashreport section with the given name with the value set to the result of the given Callable;
     */
    public void a(String var1, Callable var2)
    {
        try
        {
            this.a(var1, var2.call());
        }
        catch (Throwable var4)
        {
            this.a(var1, var4);
        }
    }

    /**
     * Adds a Crashreport section with the given name with the given value (convered .toString())
     */
    public void a(String var1, Object var2)
    {
        this.c.put(var1, var2 == null ? "null" : var2.toString());
    }

    /**
     * Adds a Crashreport section with the given name with the given Throwable
     */
    public void a(String var1, Throwable var2)
    {
        this.a(var1, "~ERROR~ " + var2.getClass().getSimpleName() + ": " + var2.getMessage());
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String a()
    {
        return this.a;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash Report.
     */
    public Throwable b()
    {
        return this.b;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
    public void a(StringBuilder var1)
    {
        boolean var2 = true;

        for (Iterator var3 = this.c.entrySet().iterator(); var3.hasNext(); var2 = false)
        {
            Entry var4 = (Entry)var3.next();

            if (!var2)
            {
                var1.append("\n");
            }

            var1.append("- ");
            var1.append((String)var4.getKey());
            var1.append(": ");
            var1.append((String)var4.getValue());
        }
    }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
     */
    public String d()
    {
        StringWriter var1 = null;
        PrintWriter var2 = null;
        String var3 = this.b.toString();

        try
        {
            var1 = new StringWriter();
            var2 = new PrintWriter(var1);
            this.b.printStackTrace(var2);
            var3 = var1.toString();
        }
        finally
        {
            try
            {
                if (var1 != null)
                {
                    var1.close();
                }

                if (var2 != null)
                {
                    var2.close();
                }
            }
            catch (IOException var10)
            {
                ;
            }
        }

        return var3;
    }

    /**
     * Gets the complete report with headers, stack trace, and different sections as a string.
     */
    public String e()
    {
        StringBuilder var1 = new StringBuilder();
        var1.append("---- Minecraft Crash Report ----\n");
        var1.append("// ");
        var1.append(h());
        var1.append("\n\n");
        var1.append("Time: ");
        var1.append((new SimpleDateFormat()).format(new Date()));
        var1.append("\n");
        var1.append("Description: ");
        var1.append(this.a);
        var1.append("\n\n");
        var1.append(this.d());
        var1.append("\n");
        var1.append("Relevant Details:");
        var1.append("\n");
        this.a(var1);
        return var1.toString();
    }

    /**
     * Saves the complete crash report to the given File.
     */
    public boolean a(File var1)
    {
        if (this.d != null)
        {
            return false;
        }
        else
        {
            if (var1.getParentFile() != null)
            {
                var1.getParentFile().mkdirs();
            }

            try
            {
                FileWriter var2 = new FileWriter(var1);
                var2.write(this.e());
                var2.close();
                this.d = var1;
                return true;
            }
            catch (Throwable var3)
            {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "Could not save crash report to " + var1, var3);
                return false;
            }
        }
    }

    /**
     * Gets a random witty comment for inclusion in this CrashReport
     */
    private static String h()
    {
        String[] var0 = new String[] {"Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :("};

        try
        {
            return var0[(int)(System.nanoTime() % (long)var0.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }
}
