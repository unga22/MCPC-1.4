package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class CommandHandler implements ICommandHandler
{
    /** Map of Strings to the ICommand objects they represent */
    private final Map a = new HashMap();

    /** The set of ICommand objects currently loaded. */
    private final Set b = new HashSet();

    public void a(ICommandListener var1, String var2)
    {
        if (var2.startsWith("/"))
        {
            var2 = var2.substring(1);
        }

        String[] var3 = var2.split(" ");
        String var4 = var3[0];
        var3 = a(var3);
        ICommand var5 = (ICommand)this.a.get(var4);
        int var6 = this.a(var5, var3);

        try
        {
            if (var5 == null)
            {
                throw new ExceptionUnknownCommand();
            }

            if (var5.b(var1))
            {
                if (var6 > -1)
                {
                    EntityPlayer[] var7 = PlayerSelector.func_82380_c(var1, var3[var6]);
                    String var8 = var3[var6];
                    EntityPlayer[] var9 = var7;
                    int var10 = var7.length;

                    for (int var11 = 0; var11 < var10; ++var11)
                    {
                        EntityPlayer var12 = var9[var11];
                        var3[var6] = var12.getLocalizedName();

                        try
                        {
                            var5.b(var1, var3);
                        }
                        catch (ExceptionPlayerNotFound var14)
                        {
                            var1.sendMessage("\u00a7c" + var1.a(var14.getMessage(), var14.a()));
                        }
                    }

                    var3[var6] = var8;
                }
                else
                {
                    var5.b(var1, var3);
                }
            }
            else
            {
                var1.sendMessage("\u00a7cYou do not have permission to use this command.");
            }
        }
        catch (ExceptionUsage var15)
        {
            var1.sendMessage("\u00a7c" + var1.a("commands.generic.usage", new Object[] {var1.a(var15.getMessage(), var15.a())}));
        }
        catch (CommandException var16)
        {
            var1.sendMessage("\u00a7c" + var1.a(var16.getMessage(), var16.a()));
        }
        catch (Throwable var17)
        {
            var1.sendMessage("\u00a7c" + var1.a("commands.generic.exception", new Object[0]));
            var17.printStackTrace();
        }
    }

    /**
     * adds the command and any aliases it has to the internal map of available commands
     */
    public ICommand a(ICommand var1)
    {
        List var2 = var1.b();
        this.a.put(var1.c(), var1);
        this.b.add(var1);

        if (var2 != null)
        {
            Iterator var3 = var2.iterator();

            while (var3.hasNext())
            {
                String var4 = (String)var3.next();
                ICommand var5 = (ICommand)this.a.get(var4);

                if (var5 == null || !var5.c().equals(var4))
                {
                    this.a.put(var4, var1);
                }
            }
        }

        return var1;
    }

    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] a(String[] var0)
    {
        String[] var1 = new String[var0.length - 1];

        for (int var2 = 1; var2 < var0.length; ++var2)
        {
            var1[var2 - 1] = var0[var2];
        }

        return var1;
    }

    /**
     * Performs a "begins with" string match on each token in par2. Only returns commands that par1 can use.
     */
    public List a(ICommandListener var1, String var2)
    {
        String[] var3 = var2.split(" ", -1);
        String var4 = var3[0];

        if (var3.length == 1)
        {
            ArrayList var8 = new ArrayList();
            Iterator var6 = this.a.entrySet().iterator();

            while (var6.hasNext())
            {
                Entry var7 = (Entry)var6.next();

                if (CommandAbstract.a(var4, (String)var7.getKey()) && ((ICommand)var7.getValue()).b(var1))
                {
                    var8.add(var7.getKey());
                }
            }

            return var8;
        }
        else
        {
            if (var3.length > 1)
            {
                ICommand var5 = (ICommand)this.a.get(var4);

                if (var5 != null)
                {
                    return var5.a(var1, a(var3));
                }
            }

            return null;
        }
    }

    /**
     * returns all commands that the commandSender can use
     */
    public List a(ICommandListener var1)
    {
        ArrayList var2 = new ArrayList();
        Iterator var3 = this.b.iterator();

        while (var3.hasNext())
        {
            ICommand var4 = (ICommand)var3.next();

            if (var4.b(var1))
            {
                var2.add(var4);
            }
        }

        return var2;
    }

    /**
     * returns a map of string to commads. All commands are returned, not just ones which someone has permission to use.
     */
    public Map a()
    {
        return this.a;
    }

    /**
     * Return a command's first parameter index containing a valid username.
     */
    private int a(ICommand var1, String[] var2)
    {
        if (var1 == null)
        {
            return -1;
        }
        else
        {
            for (int var3 = 0; var3 < var2.length; ++var3)
            {
                if (var1.a(var3) && PlayerSelector.func_82377_a(var2[var3]))
                {
                    return var3;
                }
            }

            return -1;
        }
    }
}
