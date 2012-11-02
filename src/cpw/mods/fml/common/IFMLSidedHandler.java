package cpw.mods.fml.common;

import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.EntityRegistry$EntityRegistration;
import java.util.List;
import net.minecraft.server.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet131ItemData;

public interface IFMLSidedHandler
{
    List getAdditionalBrandingInformation();

    Side getSide();

    void haltGame(String var1, Throwable var2);

    void showGuiScreen(Object var1);

    Entity spawnEntityIntoClientWorld(EntityRegistry$EntityRegistration var1, EntitySpawnPacket var2);

    void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket var1);

    void beginServerLoading(MinecraftServer var1);

    void finishServerLoading();

    MinecraftServer getServer();

    void sendPacket(Packet var1);

    void displayMissingMods(ModMissingPacket var1);

    void handleTinyPacket(NetHandler var1, Packet131ItemData var2);

    void setClientCompatibilityLevel(byte var1);

    byte getClientCompatibilityLevel();
}
