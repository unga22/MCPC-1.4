package net.minecraft.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

public class NetServerHandler extends NetHandler
{
    /** The logging system. */
    public static Logger logger = Logger.getLogger("Minecraft");

    /** The underlying network manager for this server handler. */
    public INetworkManager networkManager;

    /** This is set to true whenever a player disconnects from the server. */
    public boolean disconnected = false;

    /** Reference to the MinecraftServer object. */
    private MinecraftServer minecraftServer;

    /** Reference to the EntityPlayerMP object. */
    private EntityPlayer player;

    /** incremented each tick */
    private int f;

    /** holds the amount of tick the player is floating */
    private int g;
    private boolean field_72584_h;
    private int i;
    private long j;

    /** The Java Random object. */
    private static Random k = new Random();
    private long l;
    private int m = 0;
    private int x = 0;

    /** The last known x position for this connection. */
    private double y;

    /** The last known y position for this connection. */
    private double z;

    /** The last known z position for this connection. */
    private double q;

    /** is true when the player has moved since his last movement packet */
    private boolean checkMovement = true;
    private IntHashMap field_72586_s = new IntHashMap();

    public NetServerHandler(MinecraftServer var1, INetworkManager var2, EntityPlayer var3)
    {
        this.minecraftServer = var1;
        this.networkManager = var2;
        var2.a(this);
        this.player = var3;
        var3.netServerHandler = this;
    }

    /**
     * handle all the packets for the connection
     */
    public void d()
    {
        this.field_72584_h = false;
        ++this.f;
        this.minecraftServer.methodProfiler.a("packetflow");
        this.networkManager.b();
        this.minecraftServer.methodProfiler.c("keepAlive");

        if ((long)this.f - this.l > 20L)
        {
            this.l = (long)this.f;
            this.j = System.nanoTime() / 1000000L;
            this.i = k.nextInt();
            this.sendPacket(new Packet0KeepAlive(this.i));
        }

        if (this.m > 0)
        {
            --this.m;
        }

        if (this.x > 0)
        {
            --this.x;
        }

        this.minecraftServer.methodProfiler.c("playerTick");

        if (!this.field_72584_h && !this.player.viewingCredits)
        {
            this.player.g();

            if (this.player.vehicle == null)
            {
                this.player.setPositionRotation(this.y, this.z, this.q, this.player.yaw, this.player.pitch);
            }
        }

        this.minecraftServer.methodProfiler.b();
    }

    /**
     * Kick the offending player and give a reason why
     */
    public void disconnect(String var1)
    {
        if (!this.disconnected)
        {
            this.player.l();
            this.sendPacket(new Packet255KickDisconnect(var1));
            this.networkManager.d();
            this.minecraftServer.getServerConfigurationManager().sendAll(new Packet3Chat("\u00a7e" + this.player.name + " left the game."));
            this.minecraftServer.getServerConfigurationManager().disconnect(this.player);
            this.disconnected = true;
        }
    }

    public void a(Packet10Flying var1)
    {
        WorldServer var2 = this.minecraftServer.getWorldServer(this.player.dimension);
        this.field_72584_h = true;

        if (!this.player.viewingCredits)
        {
            double var3;

            if (!this.checkMovement)
            {
                var3 = var1.y - this.z;

                if (var1.x == this.y && var3 * var3 < 0.01D && var1.z == this.q)
                {
                    this.checkMovement = true;
                }
            }

            if (this.checkMovement)
            {
                double var5;
                double var7;
                double var9;
                double var13;

                if (this.player.vehicle != null)
                {
                    float var34 = this.player.yaw;
                    float var4 = this.player.pitch;
                    this.player.vehicle.V();
                    var5 = this.player.locX;
                    var7 = this.player.locY;
                    var9 = this.player.locZ;
                    double var35 = 0.0D;
                    var13 = 0.0D;

                    if (var1.hasLook)
                    {
                        var34 = var1.yaw;
                        var4 = var1.pitch;
                    }

                    if (var1.hasPos && var1.y == -999.0D && var1.stance == -999.0D)
                    {
                        if (Math.abs(var1.x) > 1.0D || Math.abs(var1.z) > 1.0D)
                        {
                            System.err.println(this.player.name + " was caught trying to crash the server with an invalid position.");
                            this.disconnect("Nope!");
                            return;
                        }

                        var35 = var1.x;
                        var13 = var1.z;
                    }

                    this.player.onGround = var1.g;
                    this.player.g();
                    this.player.move(var35, 0.0D, var13);
                    this.player.setLocation(var5, var7, var9, var34, var4);
                    this.player.motX = var35;
                    this.player.motZ = var13;

                    if (this.player.vehicle != null)
                    {
                        var2.vehicleEnteredWorld(this.player.vehicle, true);
                    }

                    if (this.player.vehicle != null)
                    {
                        this.player.vehicle.V();
                    }

                    this.minecraftServer.getServerConfigurationManager().d(this.player);
                    this.y = this.player.locX;
                    this.z = this.player.locY;
                    this.q = this.player.locZ;
                    var2.playerJoinedWorld(this.player);
                    return;
                }

                if (this.player.isSleeping())
                {
                    this.player.g();
                    this.player.setLocation(this.y, this.z, this.q, this.player.yaw, this.player.pitch);
                    var2.playerJoinedWorld(this.player);
                    return;
                }

                var3 = this.player.locY;
                this.y = this.player.locX;
                this.z = this.player.locY;
                this.q = this.player.locZ;
                var5 = this.player.locX;
                var7 = this.player.locY;
                var9 = this.player.locZ;
                float var11 = this.player.yaw;
                float var12 = this.player.pitch;

                if (var1.hasPos && var1.y == -999.0D && var1.stance == -999.0D)
                {
                    var1.hasPos = false;
                }

                if (var1.hasPos)
                {
                    var5 = var1.x;
                    var7 = var1.y;
                    var9 = var1.z;
                    var13 = var1.stance - var1.y;

                    if (!this.player.isSleeping() && (var13 > 1.65D || var13 < 0.1D))
                    {
                        this.disconnect("Illegal stance");
                        logger.warning(this.player.name + " had an illegal stance: " + var13);
                        return;
                    }

                    if (Math.abs(var1.x) > 3.2E7D || Math.abs(var1.z) > 3.2E7D)
                    {
                        this.disconnect("Illegal position");
                        return;
                    }
                }

                if (var1.hasLook)
                {
                    var11 = var1.yaw;
                    var12 = var1.pitch;
                }

                this.player.g();
                this.player.W = 0.0F;
                this.player.setLocation(this.y, this.z, this.q, var11, var12);

                if (!this.checkMovement)
                {
                    return;
                }

                var13 = var5 - this.player.locX;
                double var15 = var7 - this.player.locY;
                double var17 = var9 - this.player.locZ;
                double var19 = Math.min(Math.abs(var13), Math.abs(this.player.motX));
                double var21 = Math.min(Math.abs(var15), Math.abs(this.player.motY));
                double var23 = Math.min(Math.abs(var17), Math.abs(this.player.motZ));
                double var25 = var19 * var19 + var21 * var21 + var23 * var23;

                if (var25 > 100.0D && (!this.minecraftServer.I() || !this.minecraftServer.H().equals(this.player.name)))
                {
                    logger.warning(this.player.name + " moved too quickly! " + var13 + "," + var15 + "," + var17 + " (" + var19 + ", " + var21 + ", " + var23 + ")");
                    this.a(this.y, this.z, this.q, this.player.yaw, this.player.pitch);
                    return;
                }

                float var27 = 0.0625F;
                boolean var28 = var2.getCubes(this.player, this.player.boundingBox.clone().shrink((double)var27, (double)var27, (double)var27)).isEmpty();

                if (this.player.onGround && !var1.g && var15 > 0.0D)
                {
                    this.player.j(0.2F);
                }

                this.player.move(var13, var15, var17);
                this.player.onGround = var1.g;
                this.player.checkMovement(var13, var15, var17);
                double var29 = var15;
                var13 = var5 - this.player.locX;
                var15 = var7 - this.player.locY;

                if (var15 > -0.5D || var15 < 0.5D)
                {
                    var15 = 0.0D;
                }

                var17 = var9 - this.player.locZ;
                var25 = var13 * var13 + var15 * var15 + var17 * var17;
                boolean var31 = false;

                if (var25 > 0.0625D && !this.player.isSleeping() && !this.player.itemInWorldManager.isCreative())
                {
                    var31 = true;
                    logger.warning(this.player.name + " moved wrongly!");
                }

                this.player.setLocation(var5, var7, var9, var11, var12);
                boolean var32 = var2.getCubes(this.player, this.player.boundingBox.clone().shrink((double)var27, (double)var27, (double)var27)).isEmpty();

                if (var28 && (var31 || !var32) && !this.player.isSleeping())
                {
                    this.a(this.y, this.z, this.q, var11, var12);
                    return;
                }

                AxisAlignedBB var33 = this.player.boundingBox.clone().grow((double)var27, (double)var27, (double)var27).a(0.0D, -0.55D, 0.0D);

                if (!this.minecraftServer.getAllowFlight() && !this.player.itemInWorldManager.isCreative() && !var2.c(var33))
                {
                    if (var29 >= -0.03125D)
                    {
                        ++this.g;

                        if (this.g > 80)
                        {
                            logger.warning(this.player.name + " was kicked for floating too long!");
                            this.disconnect("Flying is not enabled on this server");
                            return;
                        }
                    }
                }
                else
                {
                    this.g = 0;
                }

                this.player.onGround = var1.g;
                this.minecraftServer.getServerConfigurationManager().d(this.player);
                this.player.b(this.player.locY - var3, var1.g);
            }
        }
    }

    /**
     * Moves the player to the specified destination and rotation
     */
    public void a(double var1, double var3, double var5, float var7, float var8)
    {
        this.checkMovement = false;
        this.y = var1;
        this.z = var3;
        this.q = var5;
        this.player.setLocation(var1, var3, var5, var7, var8);
        this.player.netServerHandler.sendPacket(new Packet13PlayerLookMove(var1, var3 + 1.6200000047683716D, var3, var5, var7, var8, false));
    }

    public void a(Packet14BlockDig var1)
    {
        WorldServer var2 = this.minecraftServer.getWorldServer(this.player.dimension);

        if (var1.e == 4)
        {
            this.player.bN();
        }
        else if (var1.e == 5)
        {
            this.player.bK();
        }
        else
        {
            boolean var3 = var2.worldProvider.dimension != 0 || this.minecraftServer.getServerConfigurationManager().getOPs().isEmpty() || this.minecraftServer.getServerConfigurationManager().isOp(this.player.name) || this.minecraftServer.I();
            boolean var4 = false;

            if (var1.e == 0)
            {
                var4 = true;
            }

            if (var1.e == 2)
            {
                var4 = true;
            }

            int var5 = var1.a;
            int var6 = var1.b;
            int var7 = var1.c;

            if (var4)
            {
                double var8 = this.player.locX - ((double)var5 + 0.5D);
                double var10 = this.player.locY - ((double)var6 + 0.5D) + 1.5D;
                double var12 = this.player.locZ - ((double)var7 + 0.5D);
                double var14 = var8 * var8 + var10 * var10 + var12 * var12;

                if (var14 > 36.0D)
                {
                    return;
                }

                if (var6 >= this.minecraftServer.getMaxBuildHeight())
                {
                    return;
                }
            }

            ChunkCoordinates var19 = var2.getSpawn();
            int var9 = MathHelper.a(var5 - var19.x);
            int var20 = MathHelper.a(var7 - var19.z);

            if (var9 > var20)
            {
                var20 = var9;
            }

            if (var1.e == 0)
            {
                if (var20 <= this.minecraftServer.getSpawnProtection() && !var3)
                {
                    this.player.netServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
                }
                else
                {
                    this.player.itemInWorldManager.dig(var5, var6, var7, var1.face);
                }
            }
            else if (var1.e == 2)
            {
                this.player.itemInWorldManager.a(var5, var6, var7);

                if (var2.getTypeId(var5, var6, var7) != 0)
                {
                    this.player.netServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
                }
            }
            else if (var1.e == 1)
            {
                this.player.itemInWorldManager.c(var5, var6, var7);

                if (var2.getTypeId(var5, var6, var7) != 0)
                {
                    this.player.netServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
                }
            }
            else if (var1.e == 3)
            {
                double var11 = this.player.locX - ((double)var5 + 0.5D);
                double var13 = this.player.locY - ((double)var6 + 0.5D);
                double var15 = this.player.locZ - ((double)var7 + 0.5D);
                double var17 = var11 * var11 + var13 * var13 + var15 * var15;

                if (var17 < 256.0D)
                {
                    this.player.netServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
                }
            }
        }
    }

    public void a(Packet15Place var1)
    {
        WorldServer var2 = this.minecraftServer.getWorldServer(this.player.dimension);
        ItemStack var3 = this.player.inventory.getItemInHand();
        boolean var4 = false;
        int var5 = var1.d();
        int var6 = var1.f();
        int var7 = var1.g();
        int var8 = var1.getFace();
        boolean var9 = var2.worldProvider.dimension != 0 || this.minecraftServer.getServerConfigurationManager().getOPs().isEmpty() || this.minecraftServer.getServerConfigurationManager().isOp(this.player.name) || this.minecraftServer.I();

        if (var1.getFace() == 255)
        {
            if (var3 == null)
            {
                return;
            }

            this.player.itemInWorldManager.useItem(this.player, var2, var3);
        }
        else if (var1.f() >= this.minecraftServer.getMaxBuildHeight() - 1 && (var1.getFace() == 1 || var1.f() >= this.minecraftServer.getMaxBuildHeight()))
        {
            this.player.netServerHandler.sendPacket(new Packet3Chat("\u00a77Height limit for building is " + this.minecraftServer.getMaxBuildHeight()));
            var4 = true;
        }
        else
        {
            ChunkCoordinates var10 = var2.getSpawn();
            int var11 = MathHelper.a(var5 - var10.x);
            int var12 = MathHelper.a(var7 - var10.z);

            if (var11 > var12)
            {
                var12 = var11;
            }

            if (this.checkMovement && this.player.e((double)var5 + 0.5D, (double)var6 + 0.5D, (double)var7 + 0.5D) < 64.0D && (var12 > this.minecraftServer.getSpawnProtection() || var9))
            {
                this.player.itemInWorldManager.interact(this.player, var2, var3, var5, var6, var7, var8, var1.j(), var1.l(), var1.m());
            }

            var4 = true;
        }

        if (var4)
        {
            this.player.netServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));

            if (var8 == 0)
            {
                --var6;
            }

            if (var8 == 1)
            {
                ++var6;
            }

            if (var8 == 2)
            {
                --var7;
            }

            if (var8 == 3)
            {
                ++var7;
            }

            if (var8 == 4)
            {
                --var5;
            }

            if (var8 == 5)
            {
                ++var5;
            }

            this.player.netServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
        }

        var3 = this.player.inventory.getItemInHand();

        if (var3 != null && var3.count == 0)
        {
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = null;
            var3 = null;
        }

        if (var3 == null || var3.m() == 0)
        {
            this.player.h = true;
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = ItemStack.b(this.player.inventory.items[this.player.inventory.itemInHandIndex]);
            Slot var13 = this.player.activeContainer.a(this.player.inventory, this.player.inventory.itemInHandIndex);
            this.player.activeContainer.b();
            this.player.h = false;

            if (!ItemStack.matches(this.player.inventory.getItemInHand(), var1.getItemStack()))
            {
                this.sendPacket(new Packet103SetSlot(this.player.activeContainer.windowId, var13.g, this.player.inventory.getItemInHand()));
            }
        }
    }

    public void a(String var1, Object[] var2)
    {
        logger.info(this.player.name + " lost connection: " + var1);
        this.minecraftServer.getServerConfigurationManager().sendAll(new Packet3Chat("\u00a7e" + this.player.name + " left the game."));
        this.minecraftServer.getServerConfigurationManager().disconnect(this.player);
        this.disconnected = true;

        if (this.minecraftServer.I() && this.player.name.equals(this.minecraftServer.H()))
        {
            logger.info("Stopping singleplayer server as player logged out");
            this.minecraftServer.safeShutdown();
        }
    }

    /**
     * Default handler called for packets that don't have their own handlers in NetServerHandler; kicks player from the
     * server.
     */
    public void onUnhandledPacket(Packet var1)
    {
        logger.warning(this.getClass() + " wasn\'t prepared to deal with a " + var1.getClass());
        this.disconnect("Protocol error, unexpected packet");
    }

    /**
     * Adds the packet to the underlying network manager's send queue.
     */
    public void sendPacket(Packet var1)
    {
        if (var1 instanceof Packet3Chat)
        {
            Packet3Chat var2 = (Packet3Chat)var1;
            int var3 = this.player.getChatFlags();

            if (var3 == 2)
            {
                return;
            }

            if (var3 == 1 && !var2.func_73475_d())
            {
                return;
            }
        }

        this.networkManager.queue(var1);
    }

    public void a(Packet16BlockItemSwitch var1)
    {
        if (var1.itemInHandIndex >= 0 && var1.itemInHandIndex < PlayerInventory.func_70451_h())
        {
            this.player.inventory.itemInHandIndex = var1.itemInHandIndex;
        }
        else
        {
            logger.warning(this.player.name + " tried to set an invalid carried item");
        }
    }

    public void a(Packet3Chat var1)
    {
        if (this.player.getChatFlags() == 2)
        {
            this.sendPacket(new Packet3Chat("Cannot send chat message."));
        }
        else
        {
            String var2 = var1.message;

            if (var2.length() > 100)
            {
                this.disconnect("Chat message too long");
            }
            else
            {
                var2 = var2.trim();

                for (int var3 = 0; var3 < var2.length(); ++var3)
                {
                    if (!SharedConstants.isAllowedChatCharacter(var2.charAt(var3)))
                    {
                        this.disconnect("Illegal characters in chat");
                        return;
                    }
                }

                if (var2.startsWith("/"))
                {
                    this.handleCommand(var2);
                }
                else
                {
                    if (this.player.getChatFlags() == 1)
                    {
                        this.sendPacket(new Packet3Chat("Cannot send chat message."));
                        return;
                    }

                    var2 = "<" + this.player.name + "> " + var2;
                    logger.info(var2);
                    this.minecraftServer.getServerConfigurationManager().sendAll(new Packet3Chat(var2, false));
                }

                this.m += 20;

                if (this.m > 200 && !this.minecraftServer.getServerConfigurationManager().isOp(this.player.name))
                {
                    this.disconnect("disconnect.spam");
                }
            }
        }
    }

    /**
     * Processes a / command
     */
    private void handleCommand(String var1)
    {
        this.minecraftServer.getCommandHandler().a(this.player, var1);
    }

    public void a(Packet18ArmAnimation var1)
    {
        if (var1.b == 1)
        {
            this.player.bE();
        }
    }

    /**
     * runs registerPacket on the given Packet19EntityAction
     */
    public void a(Packet19EntityAction var1)
    {
        if (var1.animation == 1)
        {
            this.player.setSneaking(true);
        }
        else if (var1.animation == 2)
        {
            this.player.setSneaking(false);
        }
        else if (var1.animation == 4)
        {
            this.player.setSprinting(true);
        }
        else if (var1.animation == 5)
        {
            this.player.setSprinting(false);
        }
        else if (var1.animation == 3)
        {
            this.player.a(false, true, true);
            this.checkMovement = false;
        }
    }

    public void a(Packet255KickDisconnect var1)
    {
        this.networkManager.a("disconnect.quitting", new Object[0]);
    }

    /**
     * return the number of chuckDataPackets from the netManager
     */
    public int lowPriorityCount()
    {
        return this.networkManager.e();
    }

    public void a(Packet7UseEntity var1)
    {
        WorldServer var2 = this.minecraftServer.getWorldServer(this.player.dimension);
        Entity var3 = var2.getEntity(var1.target);

        if (var3 != null)
        {
            boolean var4 = this.player.m(var3);
            double var5 = 36.0D;

            if (!var4)
            {
                var5 = 9.0D;
            }

            if (this.player.e(var3) < var5)
            {
                if (var1.action == 0)
                {
                    this.player.o(var3);
                }
                else if (var1.action == 1)
                {
                    this.player.attack(var3);
                }
            }
        }
    }

    public void a(Packet205ClientCommand var1)
    {
        if (var1.a == 1)
        {
            if (this.player.viewingCredits)
            {
                this.player = this.minecraftServer.getServerConfigurationManager().moveToWorld(this.player, 0, true);
            }
            else if (this.player.p().getWorldData().isHardcore())
            {
                if (this.minecraftServer.I() && this.player.name.equals(this.minecraftServer.H()))
                {
                    this.player.netServerHandler.disconnect("You have died. Game over, man, it\'s game over!");
                    this.minecraftServer.P();
                }
                else
                {
                    BanEntry var2 = new BanEntry(this.player.name);
                    var2.setReason("Death in Hardcore");
                    this.minecraftServer.getServerConfigurationManager().getNameBans().add(var2);
                    this.player.netServerHandler.disconnect("You have died. Game over, man, it\'s game over!");
                }
            }
            else
            {
                if (this.player.getHealth() > 0)
                {
                    return;
                }

                this.player = this.minecraftServer.getServerConfigurationManager().moveToWorld(this.player, 0, false);
            }
        }
    }

    /**
     * packet.processPacket is only called if this returns true
     */
    public boolean b()
    {
        return true;
    }

    /**
     * respawns the player
     */
    public void a(Packet9Respawn var1) {}

    public void handleContainerClose(Packet101CloseWindow var1)
    {
        this.player.k();
    }

    public void a(Packet102WindowClick var1)
    {
        if (this.player.activeContainer.windowId == var1.a && this.player.activeContainer.b(this.player))
        {
            ItemStack var2 = this.player.activeContainer.clickItem(var1.slot, var1.button, var1.shift, this.player);

            if (ItemStack.matches(var1.item, var2))
            {
                this.player.netServerHandler.sendPacket(new Packet106Transaction(var1.a, var1.d, true));
                this.player.h = true;
                this.player.activeContainer.b();
                this.player.broadcastCarriedItem();
                this.player.h = false;
            }
            else
            {
                this.field_72586_s.a(this.player.activeContainer.windowId, Short.valueOf(var1.d));
                this.player.netServerHandler.sendPacket(new Packet106Transaction(var1.a, var1.d, false));
                this.player.activeContainer.a(this.player, false);
                ArrayList var3 = new ArrayList();

                for (int var4 = 0; var4 < this.player.activeContainer.b.size(); ++var4)
                {
                    var3.add(((Slot)this.player.activeContainer.b.get(var4)).getItem());
                }

                this.player.a(this.player.activeContainer, var3);
            }
        }
    }

    public void a(Packet108ButtonClick var1)
    {
        if (this.player.activeContainer.windowId == var1.a && this.player.activeContainer.b(this.player))
        {
            this.player.activeContainer.a(this.player, var1.b);
            this.player.activeContainer.b();
        }
    }

    /**
     * Handle a creative slot packet.
     */
    public void a(Packet107SetCreativeSlot var1)
    {
        if (this.player.itemInWorldManager.isCreative())
        {
            boolean var2 = var1.slot < 0;
            ItemStack var3 = var1.b;
            boolean var4 = var1.slot >= 1 && var1.slot < 36 + PlayerInventory.func_70451_h();
            boolean var5 = var3 == null || var3.id < Item.byId.length && var3.id >= 0 && Item.byId[var3.id] != null;
            boolean var6 = var3 == null || var3.getData() >= 0 && var3.getData() >= 0 && var3.count <= 64 && var3.count > 0;

            if (var4 && var5 && var6)
            {
                if (var3 == null)
                {
                    this.player.defaultContainer.setItem(var1.slot, (ItemStack)null);
                }
                else
                {
                    this.player.defaultContainer.setItem(var1.slot, var3);
                }

                this.player.defaultContainer.a(this.player, true);
            }
            else if (var2 && var5 && var6 && this.x < 200)
            {
                this.x += 20;
                EntityItem var7 = this.player.drop(var3);

                if (var7 != null)
                {
                    var7.func_70288_d();
                }
            }
        }
    }

    public void a(Packet106Transaction var1)
    {
        Short var2 = (Short)this.field_72586_s.get(this.player.activeContainer.windowId);

        if (var2 != null && var1.b == var2.shortValue() && this.player.activeContainer.windowId == var1.a && !this.player.activeContainer.b(this.player))
        {
            this.player.activeContainer.a(this.player, true);
        }
    }

    /**
     * Updates Client side signs
     */
    public void a(Packet130UpdateSign var1)
    {
        WorldServer var2 = this.minecraftServer.getWorldServer(this.player.dimension);

        if (var2.isLoaded(var1.x, var1.y, var1.z))
        {
            TileEntity var3 = var2.getTileEntity(var1.x, var1.y, var1.z);

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (!var4.a())
                {
                    this.minecraftServer.warning("Player " + this.player.name + " just tried to change non-editable sign");
                    return;
                }
            }

            int var6;
            int var8;

            for (var8 = 0; var8 < 4; ++var8)
            {
                boolean var5 = true;

                if (var1.lines[var8].length() > 15)
                {
                    var5 = false;
                }
                else
                {
                    for (var6 = 0; var6 < var1.lines[var8].length(); ++var6)
                    {
                        if (SharedConstants.allowedCharacters.indexOf(var1.lines[var8].charAt(var6)) < 0)
                        {
                            var5 = false;
                        }
                    }
                }

                if (!var5)
                {
                    var1.lines[var8] = "!?";
                }
            }

            if (var3 instanceof TileEntitySign)
            {
                var8 = var1.x;
                int var9 = var1.y;
                var6 = var1.z;
                TileEntitySign var7 = (TileEntitySign)var3;
                System.arraycopy(var1.lines, 0, var7.lines, 0, 4);
                var7.update();
                var2.notify(var8, var9, var6);
            }
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void a(Packet0KeepAlive var1)
    {
        if (var1.a == this.i)
        {
            int var2 = (int)(System.nanoTime() / 1000000L - this.j);
            this.player.ping = (this.player.ping * 3 + var2) / 4;
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean a()
    {
        return true;
    }

    /**
     * Handle a player abilities packet.
     */
    public void a(Packet202Abilities var1)
    {
        this.player.abilities.isFlying = var1.f() && this.player.abilities.canFly;
    }

    public void a(Packet203TabComplete var1)
    {
        StringBuilder var2 = new StringBuilder();
        String var4;

        for (Iterator var3 = this.minecraftServer.a(this.player, var1.d()).iterator(); var3.hasNext(); var2.append(var4))
        {
            var4 = (String)var3.next();

            if (var2.length() > 0)
            {
                var2.append("\u0000");
            }
        }

        this.player.netServerHandler.sendPacket(new Packet203TabComplete(var2.toString()));
    }

    public void a(Packet204LocaleAndViewDistance var1)
    {
        this.player.a(var1);
    }

    public void a(Packet250CustomPayload var1)
    {
        DataInputStream var2;
        ItemStack var3;
        ItemStack var4;

        if ("MC|BEdit".equals(var1.tag))
        {
            try
            {
                var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
                var3 = Packet.c(var2);

                if (!ItemBookAndQuill.a(var3.getTag()))
                {
                    throw new IOException("Invalid book tag!");
                }

                var4 = this.player.inventory.getItemInHand();

                if (var3 != null && var3.id == Item.BOOK_AND_QUILL.id && var3.id == var4.id)
                {
                    var4.setTag(var3.getTag());
                }
            }
            catch (Exception var12)
            {
                var12.printStackTrace();
            }
        }
        else if ("MC|BSign".equals(var1.tag))
        {
            try
            {
                var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
                var3 = Packet.c(var2);

                if (!ItemWrittenBook.a(var3.getTag()))
                {
                    throw new IOException("Invalid book tag!");
                }

                var4 = this.player.inventory.getItemInHand();

                if (var3 != null && var3.id == Item.WRITTEN_BOOK.id && var4.id == Item.BOOK_AND_QUILL.id)
                {
                    var4.setTag(var3.getTag());
                    var4.id = Item.WRITTEN_BOOK.id;
                }
            }
            catch (Exception var11)
            {
                var11.printStackTrace();
            }
        }
        else
        {
            int var14;

            if ("MC|TrSel".equals(var1.tag))
            {
                try
                {
                    var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
                    var14 = var2.readInt();
                    Container var16 = this.player.activeContainer;

                    if (var16 instanceof ContainerMerchant)
                    {
                        ((ContainerMerchant)var16).b(var14);
                    }
                }
                catch (Exception var10)
                {
                    var10.printStackTrace();
                }
            }
            else
            {
                int var18;

                if ("MC|AdvCdm".equals(var1.tag))
                {
                    if (!this.minecraftServer.getEnableCommandBlock())
                    {
                        this.player.sendMessage(this.player.a("advMode.notEnabled", new Object[0]));
                    }
                    else if (this.player.a(2, "") && this.player.abilities.canInstantlyBuild)
                    {
                        try
                        {
                            var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
                            var14 = var2.readInt();
                            var18 = var2.readInt();
                            int var5 = var2.readInt();
                            String var6 = Packet.a(var2, 256);
                            TileEntity var7 = this.player.world.getTileEntity(var14, var18, var5);

                            if (var7 != null && var7 instanceof TileEntityCommand)
                            {
                                ((TileEntityCommand)var7).func_82352_b(var6);
                                this.player.world.notify(var14, var18, var5);
                                this.player.sendMessage("Command set: " + var6);
                            }
                        }
                        catch (Exception var9)
                        {
                            var9.printStackTrace();
                        }
                    }
                    else
                    {
                        this.player.sendMessage(this.player.a("advMode.notAllowed", new Object[0]));
                    }
                }
                else if ("MC|Beacon".equals(var1.tag))
                {
                    if (this.player.activeContainer instanceof ContainerBeacon)
                    {
                        try
                        {
                            var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
                            var14 = var2.readInt();
                            var18 = var2.readInt();
                            ContainerBeacon var17 = (ContainerBeacon)this.player.activeContainer;
                            Slot var19 = var17.getSlot(0);

                            if (var19.d())
                            {
                                var19.a(1);
                                TileEntityBeacon var20 = var17.func_82863_d();
                                var20.func_82128_d(var14);
                                var20.func_82127_e(var18);
                                var20.update();
                            }
                        }
                        catch (Exception var8)
                        {
                            var8.printStackTrace();
                        }
                    }
                }
                else if ("MC|ItemName".equals(var1.tag) && this.player.activeContainer instanceof ContainerAnvil)
                {
                    ContainerAnvil var13 = (ContainerAnvil)this.player.activeContainer;

                    if (var1.data != null && var1.data.length >= 1)
                    {
                        String var15 = SharedConstants.func_71565_a(new String(var1.data));

                        if (var15.length() <= 30)
                        {
                            var13.func_82850_a(var15);
                        }
                    }
                    else
                    {
                        var13.func_82850_a("");
                    }
                }
            }
        }
    }
}
