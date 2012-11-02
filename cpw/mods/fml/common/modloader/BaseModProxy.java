package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.TickType;
import java.util.Random;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet250CustomPayload;
import net.minecraft.server.World;

public interface BaseModProxy
{
    void modsLoaded();

    void load();

    String getName();

    String getPriorities();

    String getVersion();

    boolean doTickInGUI(TickType var1, boolean var2, Object ... var3);

    boolean doTickInGame(TickType var1, boolean var2, Object ... var3);

    void generateSurface(World var1, Random var2, int var3, int var4);

    void generateNether(World var1, Random var2, int var3, int var4);

    int addFuel(int var1, int var2);

    void takenFromCrafting(EntityHuman var1, ItemStack var2, IInventory var3);

    void takenFromFurnace(EntityHuman var1, ItemStack var2);

    void onClientLogout(INetworkManager var1);

    void onClientLogin(EntityHuman var1);

    void serverDisconnect();

    void serverConnect(NetHandler var1);

    void receiveCustomPacket(Packet250CustomPayload var1);

    void clientChat(String var1);

    void onItemPickup(EntityHuman var1, ItemStack var2);

    int dispenseEntity(World var1, ItemStack var2, Random var3, int var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13);

    void serverCustomPayload(NetServerHandler var1, Packet250CustomPayload var2);

    void serverChat(NetServerHandler var1, String var2);
}
