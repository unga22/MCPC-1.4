package cpw.mods.fml.common.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.network.FMLPacket$Type;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry$EntityRegistration;
import cpw.mods.fml.common.registry.GameRegistry;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EnumGamemode;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet131ItemData;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.Packet250CustomPayload;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.ServerConfigurationManagerAbstract;
import net.minecraft.server.World;
import net.minecraft.server.WorldType;

public class FMLNetworkHandler
{
    private static final int FML_HASH = Hashing.murmur3_32().hashString("FML").asInt();
    private static final int PROTOCOL_VERSION = 1;
    private static final FMLNetworkHandler INSTANCE = new FMLNetworkHandler();
    static final int LOGIN_RECEIVED = 1;
    static final int CONNECTION_VALID = 2;
    static final int FML_OUT_OF_DATE = -1;
    static final int MISSING_MODS_OR_VERSIONS = -2;
    private Map loginStates = Maps.newHashMap();
    private Map networkModHandlers = Maps.newHashMap();
    private Map networkIdLookup = Maps.newHashMap();

    public static void handlePacket250Packet(Packet250CustomPayload var0, INetworkManager var1, NetHandler var2)
    {
        String var3 = var0.tag;

        if (var3.startsWith("MC|"))
        {
            var2.handleVanilla250Packet(var0);
        }

        if (var3.equals("FML"))
        {
            instance().handleFMLPacket(var0, var1, var2);
        }
        else
        {
            NetworkRegistry.instance().handleCustomPacket(var0, var1, var2);
        }
    }

    public static void onConnectionEstablishedToServer(NetHandler var0, INetworkManager var1, Packet1Login var2)
    {
        NetworkRegistry.instance().clientLoggedIn(var0, var1, var2);
    }

    private void handleFMLPacket(Packet250CustomPayload var1, INetworkManager var2, NetHandler var3)
    {
        FMLPacket var4 = FMLPacket.readPacket(var1.data);
        String var5 = "";

        if (var3 instanceof NetLoginHandler)
        {
            var5 = ((NetLoginHandler)var3).h;
        }
        else
        {
            EntityHuman var6 = var3.getPlayerH();

            if (var6 != null)
            {
                var5 = var6.getName();
            }
        }

        var4.execute(var2, this, var3, var5);
    }

    public static void onConnectionReceivedFromClient(NetLoginHandler var0, MinecraftServer var1, SocketAddress var2, String var3)
    {
        instance().handleClientConnection(var0, var1, var2, var3);
    }

    private void handleClientConnection(NetLoginHandler var1, MinecraftServer var2, SocketAddress var3, String var4)
    {
        if (!this.loginStates.containsKey(var1))
        {
            if (this.handleVanillaLoginKick(var1, var2, var3, var4))
            {
                FMLLog.fine("Connection from %s rejected - no FML packet received from client", new Object[] {var4});
                var1.completeConnection("You don\'t have FML installed, you cannot connect to this server");
            }
            else
            {
                FMLLog.fine("Connection from %s was closed by vanilla minecraft", new Object[] {var4});
            }
        }
        else
        {
            switch (((Integer)this.loginStates.get(var1)).intValue())
            {
                case -2:
                    var1.completeConnection("The server requires mods that are absent or out of date on your client");
                    this.loginStates.remove(var1);
                    break;

                case -1:
                    var1.completeConnection("Your client is not running a new enough version of FML to connect to this server");
                    this.loginStates.remove(var1);
                    break;

                case 0:
                default:
                    var1.completeConnection("There was a problem during FML negotiation");
                    this.loginStates.remove(var1);
                    break;

                case 1:
                    String var5 = NetworkRegistry.instance().connectionReceived(var1, var1.networkManager);

                    if (var5 != null)
                    {
                        var1.completeConnection(var5);
                        this.loginStates.remove(var1);
                        return;
                    }

                    if (!this.handleVanillaLoginKick(var1, var2, var3, var4))
                    {
                        this.loginStates.remove(var1);
                        return;
                    }

                    NetLoginHandler.a(var1, false);
                    var1.networkManager.queue(this.getModListRequestPacket());
                    this.loginStates.put(var1, Integer.valueOf(2));
                    break;

                case 2:
                    var1.completeConnection((String)null);
                    this.loginStates.remove(var1);
            }
        }
    }

    private boolean handleVanillaLoginKick(NetLoginHandler var1, MinecraftServer var2, SocketAddress var3, String var4)
    {
        ServerConfigurationManagerAbstract var5 = var2.getServerConfigurationManager();
        EntityPlayer var6 = var5.attemptLogin(var1, var1.h, var1.hostname);

        var1.completeConnection(var6);

        return var6 != null;
    }

    public static void handleLoginPacketOnServer(NetLoginHandler var0, Packet1Login var1)
    {
        if (var1.a == FML_HASH)
        {
            if (var1.e == 1)
            {
                FMLLog.finest("Received valid FML login packet from %s", new Object[] {var0.networkManager.getSocketAddress()});
                instance().loginStates.put(var0, Integer.valueOf(1));
            }
            else if (var1.e != 1)
            {
                FMLLog.finest("Received incorrect FML (%x) login packet from %s", new Object[] {Integer.valueOf(var1.e), var0.networkManager.getSocketAddress()});
                instance().loginStates.put(var0, Integer.valueOf(-1));
            }
        }
        else
        {
            FMLLog.fine("Received invalid login packet (%x, %x) from %s", new Object[] {Integer.valueOf(var1.a), Integer.valueOf(var1.e), var0.networkManager.getSocketAddress()});
        }
    }

    static void setHandlerState(NetLoginHandler var0, int var1)
    {
        instance().loginStates.put(var0, Integer.valueOf(var1));
    }

    public static FMLNetworkHandler instance()
    {
        return INSTANCE;
    }

    public static Packet1Login getFMLFakeLoginPacket()
    {
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel((byte)0);
        Packet1Login var0 = new Packet1Login();
        var0.a = FML_HASH;
        var0.e = 1;
        var0.d = EnumGamemode.NONE;
        var0.b = WorldType.types[0];
        return var0;
    }

    public Packet250CustomPayload getModListRequestPacket()
    {
        return PacketDispatcher.getPacket("FML", FMLPacket.makePacket(FMLPacket$Type.MOD_LIST_REQUEST, new Object[0]));
    }

    public void registerNetworkMod(NetworkModHandler var1)
    {
        this.networkModHandlers.put(var1.getContainer(), var1);
        this.networkIdLookup.put(Integer.valueOf(var1.getNetworkId()), var1);
    }

    public boolean registerNetworkMod(ModContainer var1, Class var2, ASMDataTable var3)
    {
        NetworkModHandler var4 = new NetworkModHandler(var1, var2, var3);

        if (var4.isNetworkMod())
        {
            this.registerNetworkMod(var4);
        }

        return var4.isNetworkMod();
    }

    public NetworkModHandler findNetworkModHandler(Object var1)
    {
        return var1 instanceof ModContainer ? (NetworkModHandler)this.networkModHandlers.get(var1) : (var1 instanceof Integer ? (NetworkModHandler)this.networkIdLookup.get(var1) : (NetworkModHandler)this.networkModHandlers.get(FMLCommonHandler.instance().findContainerFor(var1)));
    }

    public Set getNetworkModList()
    {
        return this.networkModHandlers.keySet();
    }

    public static void handlePlayerLogin(EntityPlayer var0, NetServerHandler var1, INetworkManager var2)
    {
        NetworkRegistry.instance().playerLoggedIn(var0, var1, var2);
        GameRegistry.onPlayerLogin(var0);
    }

    public Map getNetworkIdMap()
    {
        return this.networkIdLookup;
    }

    public void bindNetworkId(String var1, Integer var2)
    {
        Map var3 = Loader.instance().getIndexedModList();
        NetworkModHandler var4 = this.findNetworkModHandler(var3.get(var1));

        if (var4 != null)
        {
            var4.setNetworkId(var2.intValue());
            this.networkIdLookup.put(var2, var4);
        }
    }

    public static void onClientConnectionToRemoteServer(NetHandler var0, String var1, int var2, INetworkManager var3)
    {
        NetworkRegistry.instance().connectionOpened(var0, var1, var2, var3);
    }

    public static void onClientConnectionToIntegratedServer(NetHandler var0, MinecraftServer var1, INetworkManager var2)
    {
        NetworkRegistry.instance().connectionOpened(var0, var1, var2);
    }

    public static void onConnectionClosed(INetworkManager var0, EntityHuman var1)
    {
        NetworkRegistry.instance().connectionClosed(var0, var1);
    }

    public static void openGui(EntityHuman var0, Object var1, int var2, World var3, int var4, int var5, int var6)
    {
        ModContainer var7 = FMLCommonHandler.instance().findContainerFor(var1);

        if (var7 == null)
        {
            NetworkModHandler var8 = instance().findNetworkModHandler(var1);

            if (var8 == null)
            {
                FMLLog.warning("A mod tried to open a gui on the server without being a NetworkMod", new Object[0]);
                return;
            }

            var7 = var8.getContainer();
        }

        if (var0 instanceof EntityPlayer)
        {
            NetworkRegistry.instance().openRemoteGui(var7, (EntityPlayer)var0, var2, var3, var4, var5, var6);
        }
        else
        {
            NetworkRegistry.instance().openLocalGui(var7, var0, var2, var3, var4, var5, var6);
        }
    }

    public static Packet getEntitySpawningPacket(Entity var0)
    {
        EntityRegistry$EntityRegistration var1 = EntityRegistry.instance().lookupModSpawn(var0.getClass(), false);
        return var1 == null ? null : (var1.usesVanillaSpawning() ? null : PacketDispatcher.getPacket("FML", FMLPacket.makePacket(FMLPacket$Type.ENTITYSPAWN, new Object[] {var1, var0, instance().findNetworkModHandler(var1.getContainer())})));
    }

    public static void makeEntitySpawnAdjustment(int var0, EntityPlayer var1, int var2, int var3, int var4)
    {
        Packet250CustomPayload var5 = PacketDispatcher.getPacket("FML", FMLPacket.makePacket(FMLPacket$Type.ENTITYSPAWNADJUSTMENT, new Object[] {Integer.valueOf(var0), Integer.valueOf(var2), Integer.valueOf(var3), Integer.valueOf(var4)}));
        var1.netServerHandler.sendPacket(var5);
    }

    public static InetAddress computeLocalHost() throws IOException
    {
        InetAddress var0 = null;
        ArrayList var1 = Lists.newArrayList();
        InetAddress var2 = InetAddress.getLocalHost();
        Iterator var3 = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();

        while (var3.hasNext())
        {
            NetworkInterface var4 = (NetworkInterface)var3.next();

            if (!var4.isLoopback() && var4.isUp())
            {
                var1.addAll(Collections.list(var4.getInetAddresses()));

                if (var1.contains(var2))
                {
                    var0 = var2;
                    break;
                }
            }
        }

        if (var0 == null && !var1.isEmpty())
        {
            var3 = var1.iterator();

            while (var3.hasNext())
            {
                InetAddress var5 = (InetAddress)var3.next();

                if (var5.getAddress().length == 4)
                {
                    var0 = var5;
                    break;
                }
            }
        }

        if (var0 == null)
        {
            var0 = var2;
        }

        return var0;
    }

    public static Packet3Chat handleChatMessage(NetHandler var0, Packet3Chat var1)
    {
        return NetworkRegistry.instance().handleChat(var0, var1);
    }

    public static void handlePacket131Packet(NetHandler var0, Packet131ItemData var1)
    {
        if (!(var0 instanceof NetServerHandler) && var1.a == Item.MAP.id)
        {
            FMLCommonHandler.instance().handleTinyPacket(var0, var1);
        }
        else
        {
            NetworkRegistry.instance().handleTinyPacket(var0, var1);
        }
    }

    public static int getCompatibilityLevel()
    {
        return 1;
    }

    public static boolean vanillaLoginPacketCompatibility()
    {
        return FMLCommonHandler.instance().getSidedDelegate().getClientCompatibilityLevel() == 0;
    }
}
