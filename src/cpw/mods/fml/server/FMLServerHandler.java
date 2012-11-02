package cpw.mods.fml.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.EntityRegistry$EntityRegistration;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.util.List;
import net.minecraft.server.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet131ItemData;
import net.minecraft.server.World;

public class FMLServerHandler implements IFMLSidedHandler
{
    private static final FMLServerHandler INSTANCE = new FMLServerHandler();
    private MinecraftServer server;

    private FMLServerHandler()
    {
        FMLCommonHandler.instance().beginLoading(this);
    }

    public void beginServerLoading(MinecraftServer var1)
    {
        this.server = var1;
        ObfuscationReflectionHelper.detectObfuscation(World.class);
        Loader.instance().loadMods();
    }

    public void finishServerLoading()
    {
        Loader.instance().initializeMods();
        LanguageRegistry.reloadLanguageTable();
    }

    public void haltGame(String var1, Throwable var2)
    {
        throw new RuntimeException(var1, var2);
    }

    public MinecraftServer getServer()
    {
        return this.server;
    }

    public static FMLServerHandler instance()
    {
        return INSTANCE;
    }

    public List getAdditionalBrandingInformation()
    {
        return null;
    }

    public Side getSide()
    {
        return Side.SERVER;
    }

    public void showGuiScreen(Object var1) {}

    public Entity spawnEntityIntoClientWorld(EntityRegistry$EntityRegistration var1, EntitySpawnPacket var2)
    {
        return null;
    }

    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket var1) {}

    public void sendPacket(Packet var1)
    {
        throw new RuntimeException("You cannot send a bare packet without a target on the server!");
    }

    public void displayMissingMods(ModMissingPacket var1) {}

    public void handleTinyPacket(NetHandler var1, Packet131ItemData var2) {}

    public void setClientCompatibilityLevel(byte var1) {}

    public byte getClientCompatibilityLevel()
    {
        return (byte)0;
    }
}
