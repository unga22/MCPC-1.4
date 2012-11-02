package cpw.mods.fml.common.network;

import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet3Chat;

public interface IChatListener
{
    Packet3Chat serverChat(NetHandler var1, Packet3Chat var2);

    Packet3Chat clientChat(NetHandler var1, Packet3Chat var2);
}
