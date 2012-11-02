package net.minecraftforge.common;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldNBTStorage;

public class ForgeDummyContainer extends DummyModContainer implements WorldAccessContainer
{
    public ForgeDummyContainer()
    {
        super(new ModMetadata());
        ModMetadata var1 = this.getMetadata();
        var1.modId = "Forge";
        var1.name = "Minecraft Forge";
        var1.version = String.format("%d.%d.%d.%d", new Object[] {Integer.valueOf(6), Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(341)});
        var1.credits = "Made possible with help from many people";
        var1.authorList = Arrays.asList(new String[] {"LexManos", "Eloraam", "Spacetoad"});
        var1.description = "Minecraft Forge is a common open source API allowing a broad range of mods to work cooperatively together. It allows many mods to be created without them editing the main Minecraft code.";
        var1.url = "http://MinecraftForge.net";
        var1.updateUrl = "http://MinecraftForge.net/forum/index.php/topic,5.0.html";
        var1.screenshots = new String[0];
        var1.logoFile = "/forge_logo.png";
    }

    public boolean registerBus(EventBus var1, LoadController var2)
    {
        var1.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent var1)
    {
        ForgeChunkManager.captureConfig(var1.getModConfigurationDirectory());
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent var1)
    {
        ForgeChunkManager.loadConfiguration();
    }

    public NBTTagCompound getDataForWriting(WorldNBTStorage var1, WorldData var2)
    {
        NBTTagCompound var3 = new NBTTagCompound();
        NBTTagCompound var4 = DimensionManager.saveDimensionDataMap();
        var3.setCompound("DimensionData", var4);
        return var3;
    }

    public void readData(WorldNBTStorage var1, WorldData var2, Map var3, NBTTagCompound var4)
    {
        if (var4.hasKey("DimensionData"))
        {
            DimensionManager.loadDimensionDataMap(var4.hasKey("DimensionData") ? var4.getCompound("DimensionData") : null);
        }
    }
}
