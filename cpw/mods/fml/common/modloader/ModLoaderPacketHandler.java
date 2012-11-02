package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.Packet250CustomPayload;

public class ModLoaderPacketHandler implements IPacketHandler
{
    private BaseModProxy mod;

    public ModLoaderPacketHandler(BaseModProxy var1)
    {
        this.mod = var1;
    }

    public void onPacketData(INetworkManager var1, Packet250CustomPayload var2, Player var3)
    {
        if (var3 instanceof EntityPlayer)
        {
            this.mod.serverCustomPayload(((EntityPlayer)var3).netServerHandler, var2);
        }
        else
        {
            ModLoaderHelper.sidedHelper.sendClientPacket(this.mod, var2);
        }
    }
}
