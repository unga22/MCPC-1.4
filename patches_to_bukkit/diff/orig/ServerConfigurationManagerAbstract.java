package net.minecraft.server;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public abstract class ServerConfigurationManagerAbstract
{
    private static final SimpleDateFormat e = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    /** Reference to the logger. */
    public static final Logger a = Logger.getLogger("Minecraft");

    /** Reference to the MinecraftServer object. */
    private final MinecraftServer server;

    /** A list of player entities that exist on this server. */
    public final List players = new ArrayList();
    private final BanList banByName = new BanList(new File("banned-players.txt"));
    private final BanList banByIP = new BanList(new File("banned-ips.txt"));

    /** A set containing the OPs. */
    private Set operators = new HashSet();

    /** The Set of all whitelisted players. */
    private Set whitelist = new HashSet();

    /** Reference to the PlayerNBTManager object. */
    private PlayerFileData playerFileData;

    /**
     * Server setting to only allow OPs and whitelisted players to join the server.
     */
    private boolean hasWhitelist;

    /** The maximum number of players that can be connected at a time. */
    protected int maxPlayers;
    protected int d;
    private EnumGamemode m;

    /** True if all players are allowed to use commands (cheats). */
    private boolean n;

    /**
     * index into playerEntities of player to ping, updated every tick; currently hardcoded to max at 200 players
     */
    private int o = 0;

    public ServerConfigurationManagerAbstract(MinecraftServer var1)
    {
        this.server = var1;
        this.banByName.setEnabled(false);
        this.banByIP.setEnabled(false);
        this.maxPlayers = 8;
    }

    public void a(INetworkManager var1, EntityPlayer var2)
    {
        this.a(var2);
        var2.spawnIn(this.server.getWorldServer(var2.dimension));
        var2.itemInWorldManager.a((WorldServer)var2.world);
        String var3 = "local";

        if (var1.getSocketAddress() != null)
        {
            var3 = var1.getSocketAddress().toString();
        }

        a.info(var2.name + "[" + var3 + "] logged in with entity id " + var2.id + " at (" + var2.locX + ", " + var2.locY + ", " + var2.locZ + ")");
        WorldServer var4 = this.server.getWorldServer(var2.dimension);
        ChunkCoordinates var5 = var4.getSpawn();
        this.func_72381_a(var2, (EntityPlayer)null, var4);
        NetServerHandler var6 = new NetServerHandler(this.server, var1, var2);
        var6.sendPacket(new Packet1Login(var2.id, var4.getWorldData().getType(), var2.itemInWorldManager.getGameMode(), var4.getWorldData().isHardcore(), var4.worldProvider.dimension, var4.difficulty, var4.getHeight(), this.getMaxPlayers()));
        var6.sendPacket(new Packet6SpawnPosition(var5.x, var5.y, var5.z));
        var6.sendPacket(new Packet202Abilities(var2.abilities));
        this.b(var2, var4);
        this.sendAll(new Packet3Chat("\u00a7e" + var2.name + " joined the game."));
        this.c(var2);
        var6.a(var2.locX, var2.locY, var2.locZ, var2.yaw, var2.pitch);
        this.server.ae().a(var6);
        var6.sendPacket(new Packet4UpdateTime(var4.getTime(), var4.F()));

        if (this.server.getTexturePack().length() > 0)
        {
            var2.a(this.server.getTexturePack(), this.server.S());
        }

        Iterator var7 = var2.getEffects().iterator();

        while (var7.hasNext())
        {
            MobEffect var8 = (MobEffect)var7.next();
            var6.sendPacket(new Packet41MobEffect(var2.id, var8));
        }

        var2.syncInventory();
    }

    /**
     * Sets the NBT manager to the one for the WorldServer given.
     */
    public void setPlayerFileData(WorldServer[] var1)
    {
        this.playerFileData = var1[0].getDataManager().getPlayerFileData();
    }

    public void func_72375_a(EntityPlayer var1, WorldServer var2)
    {
        WorldServer var3 = var1.p();

        if (var2 != null)
        {
            var2.getPlayerManager().removePlayer(var1);
        }

        var3.getPlayerManager().addPlayer(var1);
        var3.chunkProviderServer.getChunkAt((int)var1.locX >> 4, (int)var1.locZ >> 4);
    }

    public int a()
    {
        return PlayerManager.func_72686_a(this.o());
    }

    /**
     * called during player login. reads the player information from disk.
     */
    public void a(EntityPlayer var1)
    {
        NBTTagCompound var2 = this.server.worldServer[0].getWorldData().i();

        if (var1.getName().equals(this.server.H()) && var2 != null)
        {
            var1.e(var2);
        }
        else
        {
            this.playerFileData.load(var1);
        }
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void b(EntityPlayer var1)
    {
        this.playerFileData.save(var1);
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    public void c(EntityPlayer var1)
    {
        this.sendAll(new Packet201PlayerInfo(var1.name, true, 1000));
        this.players.add(var1);
        WorldServer var2 = this.server.getWorldServer(var1.dimension);

        while (!var2.getCubes(var1, var1.boundingBox).isEmpty())
        {
            var1.setPosition(var1.locX, var1.locY + 1.0D, var1.locZ);
        }

        var2.addEntity(var1);
        this.func_72375_a(var1, (WorldServer)null);
        Iterator var3 = this.players.iterator();

        while (var3.hasNext())
        {
            EntityPlayer var4 = (EntityPlayer)var3.next();
            var1.netServerHandler.sendPacket(new Packet201PlayerInfo(var4.name, true, var4.ping));
        }
    }

    /**
     * using player's dimension, update their movement when in a vehicle (e.g. cart, boat)
     */
    public void d(EntityPlayer var1)
    {
        var1.p().getPlayerManager().movePlayer(var1);
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    public void disconnect(EntityPlayer var1)
    {
        this.b(var1);
        WorldServer var2 = var1.p();
        var2.kill(var1);
        var2.getPlayerManager().removePlayer(var1);
        this.players.remove(var1);
        this.sendAll(new Packet201PlayerInfo(var1.name, false, 9999));
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String attemptLogin(SocketAddress var1, String var2)
    {
        if (this.banByName.isBanned(var2))
        {
            BanEntry var6 = (BanEntry)this.banByName.getEntries().get(var2);
            String var7 = "You are banned from this server!\nReason: " + var6.getReason();

            if (var6.getExpires() != null)
            {
                var7 = var7 + "\nYour ban will be removed on " + e.format(var6.getExpires());
            }

            return var7;
        }
        else if (!this.isWhitelisted(var2))
        {
            return "You are not white-listed on this server!";
        }
        else
        {
            String var3 = var1.toString();
            var3 = var3.substring(var3.indexOf("/") + 1);
            var3 = var3.substring(0, var3.indexOf(":"));

            if (this.banByIP.isBanned(var3))
            {
                BanEntry var4 = (BanEntry)this.banByIP.getEntries().get(var3);
                String var5 = "Your IP address is banned from this server!\nReason: " + var4.getReason();

                if (var4.getExpires() != null)
                {
                    var5 = var5 + "\nYour ban will be removed on " + e.format(var4.getExpires());
                }

                return var5;
            }
            else
            {
                return this.players.size() >= this.maxPlayers ? "The server is full!" : null;
            }
        }
    }

    /**
     * also checks for multiple logins
     */
    public EntityPlayer processLogin(String var1)
    {
        ArrayList var2 = new ArrayList();
        Iterator var3 = this.players.iterator();
        EntityPlayer var4;

        while (var3.hasNext())
        {
            var4 = (EntityPlayer)var3.next();

            if (var4.name.equalsIgnoreCase(var1))
            {
                var2.add(var4);
            }
        }

        var3 = var2.iterator();

        while (var3.hasNext())
        {
            var4 = (EntityPlayer)var3.next();
            var4.netServerHandler.disconnect("You logged in from another location");
        }

        Object var5;

        if (this.server.M())
        {
            var5 = new DemoItemInWorldManager(this.server.getWorldServer(0));
        }
        else
        {
            var5 = new ItemInWorldManager(this.server.getWorldServer(0));
        }

        return new EntityPlayer(this.server, this.server.getWorldServer(0), var1, (ItemInWorldManager)var5);
    }

    /**
     * Called on respawn
     */
    public EntityPlayer moveToWorld(EntityPlayer var1, int var2, boolean var3)
    {
        var1.p().getTracker().untrackPlayer(var1);
        var1.p().getTracker().untrackEntity(var1);
        var1.p().getPlayerManager().removePlayer(var1);
        this.players.remove(var1);
        this.server.getWorldServer(var1.dimension).removeEntity(var1);
        ChunkCoordinates var4 = var1.getBed();
        boolean var5 = var1.func_82245_bX();
        var1.dimension = var2;
        Object var6;

        if (this.server.M())
        {
            var6 = new DemoItemInWorldManager(this.server.getWorldServer(var1.dimension));
        }
        else
        {
            var6 = new ItemInWorldManager(this.server.getWorldServer(var1.dimension));
        }

        EntityPlayer var7 = new EntityPlayer(this.server, this.server.getWorldServer(var1.dimension), var1.name, (ItemInWorldManager)var6);
        var7.netServerHandler = var1.netServerHandler;
        var7.copyTo(var1, var3);
        var7.id = var1.id;
        WorldServer var8 = this.server.getWorldServer(var1.dimension);
        this.func_72381_a(var7, var1, var8);
        ChunkCoordinates var9;

        if (var4 != null)
        {
            var9 = EntityHuman.getBed(this.server.getWorldServer(var1.dimension), var4, var5);

            if (var9 != null)
            {
                var7.setPositionRotation((double)((float)var9.x + 0.5F), (double)((float)var9.y + 0.1F), (double)((float)var9.z + 0.5F), 0.0F, 0.0F);
                var7.setRespawnPosition(var4, var5);
            }
            else
            {
                var7.netServerHandler.sendPacket(new Packet70Bed(0, 0));
            }
        }

        var8.chunkProviderServer.getChunkAt((int)var7.locX >> 4, (int)var7.locZ >> 4);

        while (!var8.getCubes(var7, var7.boundingBox).isEmpty())
        {
            var7.setPosition(var7.locX, var7.locY + 1.0D, var7.locZ);
        }

        var7.netServerHandler.sendPacket(new Packet9Respawn(var7.dimension, (byte)var7.world.difficulty, var7.world.getWorldData().getType(), var7.world.getHeight(), var7.itemInWorldManager.getGameMode()));
        var9 = var8.getSpawn();
        var7.netServerHandler.a(var7.locX, var7.locY, var7.locZ, var7.yaw, var7.pitch);
        var7.netServerHandler.sendPacket(new Packet6SpawnPosition(var9.x, var9.y, var9.z));
        var7.netServerHandler.sendPacket(new Packet43SetExperience(var7.exp, var7.expTotal, var7.expLevel));
        this.b(var7, var8);
        var8.getPlayerManager().addPlayer(var7);
        var8.addEntity(var7);
        this.players.add(var7);
        var7.syncInventory();
        return var7;
    }

    /**
     * moves provided player from overworld to nether or vice versa
     */
    public void changeDimension(EntityPlayer var1, int var2)
    {
        int var3 = var1.dimension;
        WorldServer var4 = this.server.getWorldServer(var1.dimension);
        var1.dimension = var2;
        WorldServer var5 = this.server.getWorldServer(var1.dimension);
        var1.netServerHandler.sendPacket(new Packet9Respawn(var1.dimension, (byte)var1.world.difficulty, var5.getWorldData().getType(), var5.getHeight(), var1.itemInWorldManager.getGameMode()));
        var4.removeEntity(var1);
        var1.dead = false;
        this.a(var1, var3, var4, var5);
        this.func_72375_a(var1, var4);
        var1.netServerHandler.a(var1.locX, var1.locY, var1.locZ, var1.yaw, var1.pitch);
        var1.itemInWorldManager.a(var5);
        this.b(var1, var5);
        this.updateClient(var1);
        Iterator var6 = var1.getEffects().iterator();

        while (var6.hasNext())
        {
            MobEffect var7 = (MobEffect)var6.next();
            var1.netServerHandler.sendPacket(new Packet41MobEffect(var1.id, var7));
        }
    }

    /**
     * Transfers an entity from a world to another world.
     */
    public void a(Entity var1, int var2, WorldServer var3, WorldServer var4)
    {
        double var5 = var1.locX;
        double var7 = var1.locZ;
        double var9 = 8.0D;
        double var11 = var1.locX;
        double var13 = var1.locY;
        double var15 = var1.locZ;
        float var17 = var1.yaw;

        if (var1.dimension == -1)
        {
            var5 /= var9;
            var7 /= var9;
            var1.setPositionRotation(var5, var1.locY, var7, var1.yaw, var1.pitch);

            if (var1.isAlive())
            {
                var3.entityJoinedWorld(var1, false);
            }
        }
        else if (var1.dimension == 0)
        {
            var5 *= var9;
            var7 *= var9;
            var1.setPositionRotation(var5, var1.locY, var7, var1.yaw, var1.pitch);

            if (var1.isAlive())
            {
                var3.entityJoinedWorld(var1, false);
            }
        }
        else
        {
            ChunkCoordinates var18;

            if (var2 == 1)
            {
                var18 = var4.getSpawn();
            }
            else
            {
                var18 = var4.getDimensionSpawn();
            }

            var5 = (double)var18.x;
            var1.locY = (double)var18.y;
            var7 = (double)var18.z;
            var1.setPositionRotation(var5, var1.locY, var7, 90.0F, 0.0F);

            if (var1.isAlive())
            {
                var3.entityJoinedWorld(var1, false);
            }
        }

        if (var2 != 1)
        {
            var5 = (double)MathHelper.a((int)var5, -29999872, 29999872);
            var7 = (double)MathHelper.a((int)var7, -29999872, 29999872);

            if (var1.isAlive())
            {
                var4.addEntity(var1);
                var1.setPositionRotation(var5, var1.locY, var7, var1.yaw, var1.pitch);
                var4.entityJoinedWorld(var1, false);
                (new PortalTravelAgent()).a(var4, var1, var11, var13, var15, var17);
            }
        }

        var1.spawnIn(var4);
    }

    /**
     * self explanitory
     */
    public void tick()
    {
        if (++this.o > 600)
        {
            this.o = 0;
        }

        if (this.o < this.players.size())
        {
            EntityPlayer var1 = (EntityPlayer)this.players.get(this.o);
            this.sendAll(new Packet201PlayerInfo(var1.name, true, var1.ping));
        }
    }

    /**
     * sends a packet to all players
     */
    public void sendAll(Packet var1)
    {
        for (int var2 = 0; var2 < this.players.size(); ++var2)
        {
            ((EntityPlayer)this.players.get(var2)).netServerHandler.sendPacket(var1);
        }
    }

    /**
     * Sends a packet to all players in the specified Dimension
     */
    public void a(Packet var1, int var2)
    {
        Iterator var3 = this.players.iterator();

        while (var3.hasNext())
        {
            EntityPlayer var4 = (EntityPlayer)var3.next();

            if (var4.dimension == var2)
            {
                var4.netServerHandler.sendPacket(var1);
            }
        }
    }

    /**
     * returns a string containing a comma-seperated list of player names
     */
    public String c()
    {
        String var1 = "";

        for (int var2 = 0; var2 < this.players.size(); ++var2)
        {
            if (var2 > 0)
            {
                var1 = var1 + ", ";
            }

            var1 = var1 + ((EntityPlayer)this.players.get(var2)).name;
        }

        return var1;
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] d()
    {
        String[] var1 = new String[this.players.size()];

        for (int var2 = 0; var2 < this.players.size(); ++var2)
        {
            var1[var2] = ((EntityPlayer)this.players.get(var2)).name;
        }

        return var1;
    }

    public BanList getNameBans()
    {
        return this.banByName;
    }

    public BanList getIPBans()
    {
        return this.banByIP;
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String var1)
    {
        this.operators.add(var1.toLowerCase());
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String var1)
    {
        this.operators.remove(var1.toLowerCase());
    }

    /**
     * Determine if the player is allowed to connect based on current server settings.
     */
    public boolean isWhitelisted(String var1)
    {
        var1 = var1.trim().toLowerCase();
        return !this.hasWhitelist || this.operators.contains(var1) || this.whitelist.contains(var1);
    }

    /**
     * Returns true if the specific player is allowed to use commands.
     */
    public boolean isOp(String var1)
    {
        return this.operators.contains(var1.trim().toLowerCase()) || this.server.I() && this.server.worldServer[0].getWorldData().allowCommands() && this.server.H().equalsIgnoreCase(var1) || this.n;
    }

    /**
     * gets the player entity for the player with the name specified
     */
    public EntityPlayer f(String var1)
    {
        Iterator var2 = this.players.iterator();
        EntityPlayer var3;

        do
        {
            if (!var2.hasNext())
            {
                return null;
            }

            var3 = (EntityPlayer)var2.next();
        }
        while (!var3.name.equalsIgnoreCase(var1));

        return var3;
    }

    /**
     * Find all players in a specified range and narrowing down by other parameters
     */
    public List a(ChunkCoordinates var1, int var2, int var3, int var4, int var5, int var6, int var7)
    {
        if (this.players.isEmpty())
        {
            return null;
        }
        else
        {
            Object var8 = new ArrayList();
            boolean var9 = var4 < 0;
            int var10 = var2 * var2;
            int var11 = var3 * var3;
            var4 = MathHelper.a(var4);

            for (int var12 = 0; var12 < this.players.size(); ++var12)
            {
                EntityPlayer var13 = (EntityPlayer)this.players.get(var12);

                if (var1 != null && (var2 > 0 || var3 > 0))
                {
                    float var14 = var1.e(var13.b());

                    if (var2 > 0 && var14 < (float)var10 || var3 > 0 && var14 > (float)var11)
                    {
                        continue;
                    }
                }

                if ((var5 == EnumGamemode.NOT_SET.a() || var5 == var13.itemInWorldManager.getGameMode().a()) && (var6 <= 0 || var13.expLevel >= var6) && var13.expLevel <= var7)
                {
                    ((List)var8).add(var13);
                }
            }

            if (var1 != null)
            {
                Collections.sort((List)var8, new PlayerDistanceComparator(var1));
            }

            if (var9)
            {
                Collections.reverse((List)var8);
            }

            if (var4 > 0)
            {
                var8 = ((List)var8).subList(0, Math.min(var4, ((List)var8).size()));
            }

            return (List)var8;
        }
    }

    /**
     * sends a packet to players within d3 of point (x,y,z)
     */
    public void sendPacketNearby(double var1, double var3, double var5, double var7, int var9, Packet var10)
    {
        this.sendPacketNearby((EntityHuman)null, var1, var3, var5, var7, var9, var10);
    }

    /**
     * params: srcPlayer,x,y,z,d,dimension. The packet is not sent to the srcPlayer, but all other players where
     * dx*dx+dy*dy+dz*dz<d*d
     */
    public void sendPacketNearby(EntityHuman var1, double var2, double var4, double var6, double var8, int var10, Packet var11)
    {
        Iterator var12 = this.players.iterator();

        while (var12.hasNext())
        {
            EntityPlayer var13 = (EntityPlayer)var12.next();

            if (var13 != var1 && var13.dimension == var10)
            {
                double var14 = var2 - var13.locX;
                double var16 = var4 - var13.locY;
                double var18 = var6 - var13.locZ;

                if (var14 * var14 + var16 * var16 + var18 * var18 < var8 * var8)
                {
                    var13.netServerHandler.sendPacket(var11);
                }
            }
        }
    }

    /**
     * Saves all of the players' current states.
     */
    public void savePlayers()
    {
        Iterator var1 = this.players.iterator();

        while (var1.hasNext())
        {
            EntityPlayer var2 = (EntityPlayer)var1.next();
            this.b(var2);
        }
    }

    /**
     * Add the specified player to the white list.
     */
    public void addWhitelist(String var1)
    {
        this.whitelist.add(var1);
    }

    /**
     * Remove the specified player from the whitelist.
     */
    public void removeWhitelist(String var1)
    {
        this.whitelist.remove(var1);
    }

    /**
     * Returns the whitelisted players.
     */
    public Set getWhitelisted()
    {
        return this.whitelist;
    }

    public Set getOPs()
    {
        return this.operators;
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void reloadWhitelist() {}

    /**
     * Updates the time and weather for the given player to those of the given world
     */
    public void b(EntityPlayer var1, WorldServer var2)
    {
        var1.netServerHandler.sendPacket(new Packet4UpdateTime(var2.getTime(), var2.F()));

        if (var2.M())
        {
            var1.netServerHandler.sendPacket(new Packet70Bed(1, 0));
        }
    }

    /**
     * sends the players inventory to himself
     */
    public void updateClient(EntityPlayer var1)
    {
        var1.updateInventory(var1.defaultContainer);
        var1.m();
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getPlayerCount()
    {
        return this.players.size();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getSeenPlayers()
    {
        return this.server.worldServer[0].getDataManager().getPlayerFileData().getSeenPlayers();
    }

    public boolean getHasWhitelist()
    {
        return this.hasWhitelist;
    }

    public void setHasWhitelist(boolean var1)
    {
        this.hasWhitelist = var1;
    }

    public List j(String var1)
    {
        ArrayList var2 = new ArrayList();
        Iterator var3 = this.players.iterator();

        while (var3.hasNext())
        {
            EntityPlayer var4 = (EntityPlayer)var3.next();

            if (var4.func_71114_r().equals(var1))
            {
                var2.add(var4);
            }
        }

        return var2;
    }

    /**
     * Gets the View Distance.
     */
    public int o()
    {
        return this.d;
    }

    public MinecraftServer getServer()
    {
        return this.server;
    }

    /**
     * gets the tags created in the last writePlayerData call
     */
    public NBTTagCompound q()
    {
        return null;
    }

    private void func_72381_a(EntityPlayer var1, EntityPlayer var2, World var3)
    {
        if (var2 != null)
        {
            var1.itemInWorldManager.setGameMode(var2.itemInWorldManager.getGameMode());
        }
        else if (this.m != null)
        {
            var1.itemInWorldManager.setGameMode(this.m);
        }

        var1.itemInWorldManager.b(var3.getWorldData().getGameType());
    }

    /**
     * Kicks everyone with "Server closed" as reason.
     */
    public void r()
    {
        while (!this.players.isEmpty())
        {
            ((EntityPlayer)this.players.get(0)).netServerHandler.disconnect("Server closed");
        }
    }
}
