package cpw.mods.fml.common.registry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.LocaleLanguage;

public class LanguageRegistry
{
    private static final LanguageRegistry INSTANCE = new LanguageRegistry();
    private Map modLanguageData = new HashMap();

    public static LanguageRegistry instance()
    {
        return INSTANCE;
    }

    public String getStringLocalization(String var1)
    {
        return this.getStringLocalization(var1, LocaleLanguage.a().c());
    }

    public String getStringLocalization(String var1, String var2)
    {
        String var3 = "";
        Properties var4 = (Properties)this.modLanguageData.get(var2);

        if (var4 != null && var4.getProperty(var1) != null)
        {
            var3 = var4.getProperty(var1);
        }

        return var3;
    }

    public void addStringLocalization(String var1, String var2)
    {
        this.addStringLocalization(var1, "en_US", var2);
    }

    public void addStringLocalization(String var1, String var2, String var3)
    {
        Properties var4 = (Properties)this.modLanguageData.get(var2);

        if (var4 == null)
        {
            var4 = new Properties();
            this.modLanguageData.put(var2, var4);
        }

        var4.put(var1, var3);
    }

    public void addStringLocalization(Properties var1)
    {
        this.addStringLocalization(var1, "en_US");
    }

    public void addStringLocalization(Properties var1, String var2)
    {
        Properties var3 = (Properties)this.modLanguageData.get(var2);

        if (var3 == null)
        {
            var3 = new Properties();
            this.modLanguageData.put(var2, var3);
        }

        if (var1 != null)
        {
            var3.putAll(var1);
        }
    }

    public static void reloadLanguageTable()
    {
        String var0 = LocaleLanguage.a().c();
        LocaleLanguage.a().d = null;
        LocaleLanguage.a().a(var0);
    }

    public void addNameForObject(Object var1, String var2, String var3)
    {
        String var4;

        if (var1 instanceof Item)
        {
            var4 = ((Item)var1).getName();
        }
        else if (var1 instanceof Block)
        {
            var4 = ((Block)var1).a();
        }
        else
        {
            if (!(var1 instanceof ItemStack))
            {
                throw new IllegalArgumentException(String.format("Illegal object for naming %s", new Object[] {var1}));
            }

            var4 = ((ItemStack)var1).getItem().c_((ItemStack)var1);
        }

        var4 = var4 + ".name";
        this.addStringLocalization(var4, var2, var3);
    }

    public static void addName(Object var0, String var1)
    {
        instance().addNameForObject(var0, "en_US", var1);
    }

    public void loadLanguageTable(Properties var1, String var2)
    {
        Properties var3 = (Properties)this.modLanguageData.get("en_US");

        if (var3 != null)
        {
            var1.putAll(var3);
        }

        Properties var4 = (Properties)this.modLanguageData.get(var2);

        if (var4 != null)
        {
            var1.putAll(var4);
        }
    }

    public void loadLocalization(String var1, String var2, boolean var3)
    {
        this.loadLocalization(this.getClass().getResource(var1), var2, var3);
    }

    public void loadLocalization(URL var1, String var2, boolean var3)
    {
        InputStream var4 = null;
        Properties var5 = new Properties();

        try
        {
            var4 = var1.openStream();

            if (var3)
            {
                var5.loadFromXML(var4);
            }
            else
            {
                var5.load(var4);
            }

            this.addStringLocalization(var5, var2);
        }
        catch (IOException var15)
        {
            var15.printStackTrace();
        }
        finally
        {
            try
            {
                if (var4 != null)
                {
                    var4.close();
                }
            }
            catch (IOException var14)
            {
                var14.printStackTrace();
            }
        }
    }
}
