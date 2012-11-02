package net.minecraft.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.IllegalFormatException;
import java.util.Properties;
import java.util.TreeMap;

public class LocaleLanguage
{
    /** Is the private singleton instance of StringTranslate. */
    private static LocaleLanguage a = new LocaleLanguage("en_US");

    /**
     * Contains all key/value pairs to be translated - is loaded from '/lang/en_US.lang' when the StringTranslate is
     * created.
     */
    private Properties b = new Properties();
    private TreeMap c;
    private String d;
    private boolean e;

    public LocaleLanguage(String var1)
    {
        this.e();
        this.a(var1);
    }

    /**
     * Return the StringTranslate singleton instance
     */
    public static LocaleLanguage a()
    {
        return a;
    }

    private void e()
    {
        TreeMap var1 = new TreeMap();

        try
        {
            BufferedReader var2 = new BufferedReader(new InputStreamReader(LocaleLanguage.class.getResourceAsStream("/lang/languages.txt"), "UTF-8"));

            for (String var3 = var2.readLine(); var3 != null; var3 = var2.readLine())
            {
                String[] var4 = var3.split("=");

                if (var4 != null && var4.length == 2)
                {
                    var1.put(var4[0], var4[1]);
                }
            }
        }
        catch (IOException var5)
        {
            var5.printStackTrace();
            return;
        }

        this.c = var1;
        this.c.put("en_US", "English (US)");
    }

    public TreeMap b()
    {
        return this.c;
    }

    private void a(Properties var1, String var2)
    {
        BufferedReader var3 = new BufferedReader(new InputStreamReader(LocaleLanguage.class.getResourceAsStream("/lang/" + var2 + ".lang"), "UTF-8"));

        for (String var4 = var3.readLine(); var4 != null; var4 = var3.readLine())
        {
            var4 = var4.trim();

            if (!var4.startsWith("#"))
            {
                String[] var5 = var4.split("=");

                if (var5 != null && var5.length == 2)
                {
                    var1.setProperty(var5[0], var5[1]);
                }
            }
        }
    }

    public void a(String var1)
    {
        if (!var1.equals(this.d))
        {
            Properties var2 = new Properties();

            try
            {
                this.a(var2, "en_US");
            }
            catch (IOException var8)
            {
                ;
            }

            this.e = false;

            if (!"en_US".equals(var1))
            {
                try
                {
                    this.a(var2, var1);
                    Enumeration var3 = var2.propertyNames();

                    while (var3.hasMoreElements() && !this.e)
                    {
                        Object var4 = var3.nextElement();
                        Object var5 = var2.get(var4);

                        if (var5 != null)
                        {
                            String var6 = var5.toString();

                            for (int var7 = 0; var7 < var6.length(); ++var7)
                            {
                                if (var6.charAt(var7) >= 256)
                                {
                                    this.e = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (IOException var9)
                {
                    var9.printStackTrace();
                    return;
                }
            }

            this.d = var1;
            this.b = var2;
        }
    }

    /**
     * Translate a key to current language.
     */
    public String b(String var1)
    {
        return this.b.getProperty(var1, var1);
    }

    /**
     * Translate a key to current language applying String.format()
     */
    public String a(String var1, Object ... var2)
    {
        String var3 = this.b.getProperty(var1, var1);

        try
        {
            return String.format(var3, var2);
        }
        catch (IllegalFormatException var5)
        {
            return "Format error: " + var3;
        }
    }

    public String func_74809_c(String var1)
    {
        return this.b.getProperty(var1 + ".name", "");
    }
}
