package net.minecraft.server;

import cpw.mods.fml.server.FMLBukkitHandler;
import forge.ForgeHooks;
import forge.ForgeHooksServer;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.server.ServerListPingEvent;

public class NetLoginHandler extends NetHandler
{
  public static Logger logger = Logger.getLogger("Minecraft");
  private static Random random = new Random();
  public NetworkManager networkManager;
  public boolean c = false;
  private MinecraftServer server;
  private int f = 0;
  private String g = null;
  private Packet1Login h = null;
  private String loginKey = Long.toString(random.nextLong(), 16);
  public String hostname = "";

  public NetLoginHandler(MinecraftServer minecraftserver, Socket socket, String s) {
    this.server = minecraftserver;
    this.networkManager = new NetworkManager(socket, s, this);
    this.networkManager.f = 0;
    ForgeHooks.onConnect(this.networkManager);
  }

  public Socket getSocket()
  {
    return this.networkManager.socket;
  }

  public void a()
  {
    if (this.h != null) {
      b(this.h);
      this.h = null;
    }

    if (this.f++ == 600)
      disconnect("Took too long to log in");
    else
      this.networkManager.b();
  }

  public void disconnect(String s)
  {
    try {
      logger.info("Disconnecting " + getName() + ": " + s);
      this.networkManager.queue(new Packet255KickDisconnect(s));
      this.networkManager.d();
      this.c = true;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void a(Packet2Handshake packet2handshake)
  {
    if (packet2handshake.a == null) {
      disconnect(this.server.server.getOutdatedServerMessage());
      return;
    }

    int i = packet2handshake.a.indexOf(';');
    if (i == -1)
      this.hostname = "";
    else this.hostname = packet2handshake.a.substring(i + 1);

    if (this.server.onlineMode) {
      this.loginKey = Long.toString(random.nextLong(), 16);
      this.networkManager.queue(new Packet2Handshake(this.loginKey));
    } else {
      this.networkManager.queue(new Packet2Handshake("-"));
    }
  }

  public void a(Packet1Login packet1login) {
    this.g = packet1login.name;
    if (packet1login.a != 29) {
      if (packet1login.a > 29)
        disconnect(this.server.server.getOutdatedServerMessage());
      else {
        disconnect(this.server.server.getOutdatedClientMessage());
      }
    }
    else if (!this.server.onlineMode)
    {
      if (!packet1login.name.equals(ChatColor.stripColor(packet1login.name))) {
        disconnect("Colourful names are not permitted!");
        return;
      }

      b(packet1login);
    } else {
      new ThreadLoginVerifier(this, packet1login, this.server.server).start();
    }
  }

  public void b(Packet1Login packet1login)
  {
    EntityPlayer entityplayer = this.server.serverConfigurationManager.attemptLogin(this, packet1login.name, this.hostname);

    if (entityplayer != null)
    {
      entityplayer.itemInWorldManager.a((WorldServer)entityplayer.world);

      logger.info(getName() + " logged in with entity id " + entityplayer.id + " at ([" + entityplayer.world.worldData.name + "] " + entityplayer.locX + ", " + entityplayer.locY + ", " + entityplayer.locZ + ")");
      WorldServer worldserver = (WorldServer)entityplayer.world;
      ChunkCoordinates chunkcoordinates = worldserver.getSpawn();

      entityplayer.itemInWorldManager.b(worldserver.getWorldData().getGameType());
      NetServerHandler netserverhandler = new NetServerHandler(this.server, this.networkManager, entityplayer);

      this.server.networkListenThread.a(netserverhandler);

      ForgeHooksServer.handleLoginPacket(packet1login, netserverhandler, this.networkManager);
      FMLBukkitHandler.instance().handleLogin(packet1login, this.networkManager);
    }

    this.c = true;
  }

  public void a(String s, Object[] aobject) {
    logger.info(getName() + " lost connection");
    this.c = true;
  }

  public void a(Packet254GetInfo packet254getinfo) {
    if (this.networkManager.getSocket() == null) return;
    try
    {
      ServerListPingEvent pingEvent = CraftEventFactory.callServerListPingEvent(this.server.server, getSocket().getInetAddress(), this.server.motd, this.server.serverConfigurationManager.getPlayerCount(), this.server.serverConfigurationManager.getMaxPlayers());
      String s = pingEvent.getMotd() + "§" + this.server.serverConfigurationManager.getPlayerCount() + "§" + pingEvent.getMaxPlayers();

      this.server.networkListenThread.a(this.networkManager.getSocket());
      this.networkManager.queue(new Packet255KickDisconnect(s));
      this.networkManager.d();

      this.c = true;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void onUnhandledPacket(Packet packet) {
    disconnect("Protocol error");
  }

  public String getName() {
    return this.g != null ? this.g + " [" + this.networkManager.getSocketAddress().toString() + "]" : this.networkManager.getSocketAddress().toString();
  }

  public boolean c() {
    return true;
  }

  static String a(NetLoginHandler netloginhandler) {
    return netloginhandler.loginKey;
  }

  static Packet1Login a(NetLoginHandler netloginhandler, Packet1Login packet1login) {
    return netloginhandler.h = packet1login;
  }
}

