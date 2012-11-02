package cpw.mods.fml.common;

import com.google.common.eventbus.EventBus;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldNBTStorage;

public class FMLDummyContainer extends DummyModContainer implements WorldAccessContainer
{
    public FMLDummyContainer()
    {
        super(new ModMetadata());
        ModMetadata var1 = this.getMetadata();
        var1.modId = "FML";
        var1.name = "Forge Mod Loader";
        var1.version = Loader.instance().getFMLVersionString();
        var1.credits = "Made possible with help from many people";
        var1.authorList = Arrays.asList(new String[] {"cpw, LexManos"});
        var1.description = "The Forge Mod Loader provides the ability for systems to load mods from the file system. It also provides key capabilities for mods to be able to cooperate and provide a good modding environment. The mod loading system is compatible with ModLoader, all your ModLoader mods should work.";
        var1.url = "https://github.com/cpw/FML/wiki";
        var1.updateUrl = "https://github.com/cpw/FML/wiki";
        var1.screenshots = new String[0];
        var1.logoFile = "";
    }

    public boolean registerBus(EventBus var1, LoadController var2)
    {
        return true;
    }

    public NBTTagCompound getDataForWriting(WorldNBTStorage var1, WorldData var2)
    {
        NBTTagCompound var3 = new NBTTagCompound();
        NBTTagList var4 = new NBTTagList();
        Iterator var5 = Loader.instance().getActiveModList().iterator();

        while (var5.hasNext())
        {
            ModContainer var6 = (ModContainer)var5.next();
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setString("ModId", var6.getModId());
            var7.setString("ModVersion", var6.getVersion());
            var4.add(var7);
        }

        var3.set("ModList", var4);
        return var3;
    }

    public void readData(WorldNBTStorage var1, WorldData var2, Map var3, NBTTagCompound var4)
    {
        if (var4.hasKey("ModList"))
        {
            NBTTagList var5 = var4.getList("ModList");

            for (int var6 = 0; var6 < var5.size(); ++var6)
            {
                NBTTagCompound var7 = (NBTTagCompound)var5.get(var6);
                String var8 = var7.getString("ModId");
                String var9 = var7.getString("ModVersion");
                ModContainer var10 = (ModContainer)Loader.instance().getIndexedModList().get(var8);

                if (var10 == null)
                {
                    FMLLog.severe("This world was saved with mod %s which appears to be missing, things may not work well", new Object[] {var8});
                }
                else if (!var9.equals(var10.getVersion()))
                {
                    FMLLog.info("This world was saved with mod %s version %s and it is now at version %s, things may not work well", new Object[] {var8, var9, var10.getVersion()});
                }
            }
        }
    }
}
