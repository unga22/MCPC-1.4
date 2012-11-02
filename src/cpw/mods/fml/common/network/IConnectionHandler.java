package cpw.mods.fml.common.network;

import net.minecraft.server.INetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;

public interface IConnectionHandler
{
    void playerLoggedIn(Player var1, NetHandler var2, INetworkManager var3);

    String connectionReceived(NetLoginHandler var1, INetworkManager var2);

    void connectionOpened(NetHandler var1, String var2, int var3, INetworkManager var4);

    void connectionOpened(NetHandler var1, MinecraftServer var2, INetworkManager var3);

    void connectionClosed(INetworkManager var1);

    void clientLoggedIn(NetHandler var1, INetworkManager var2, Packet1Login var3);
}
