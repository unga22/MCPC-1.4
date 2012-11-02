package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry$EntityRegistration;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet250CustomPayload;

public interface IModLoaderSidedHelper
{
    void finishModLoading(ModLoaderModContainer var1);

    Object getClientGui(BaseModProxy var1, EntityHuman var2, int var3, int var4, int var5, int var6);

    Entity spawnEntity(BaseModProxy var1, EntitySpawnPacket var2, EntityRegistry$EntityRegistration var3);

    void sendClientPacket(BaseModProxy var1, Packet250CustomPayload var2);

    void clientConnectionOpened(NetHandler var1, INetworkManager var2, BaseModProxy var3);

    boolean clientConnectionClosed(INetworkManager var1, BaseModProxy var2);
}
