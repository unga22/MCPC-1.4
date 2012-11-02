package cpw.mods.fml.common.network;

import net.minecraft.server.INetworkManager;
import net.minecraft.server.Packet250CustomPayload;

public interface IPacketHandler
{
    void onPacketData(INetworkManager var1, Packet250CustomPayload var2, Player var3);
}
