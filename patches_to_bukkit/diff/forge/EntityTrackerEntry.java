package net.minecraft.server;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EntityTrackerEntry
{
    /** The entity that this EntityTrackerEntry tracks. */
    public Entity tracker;
    public int b;

    /** check for sync when ticks % updateFrequency==0 */
    public int c;

    /** The encoded entity X position. */
    public int xLoc;

    /** The encoded entity Y position. */
    public int yLoc;

    /** The encoded entity Z position. */
    public int zLoc;

    /** The encoded entity yaw rotation. */
    public int yRot;

    /** The encoded entity pitch rotation. */
    public int xRot;
    public int i;
    public double j;
    public double k;
    public double l;
    public int m = 0;
    private double p;
    private double q;
    private double r;
    private boolean s = false;
    private boolean isMoving;

    /**
     * every 400 ticks a  full teleport packet is sent, rather than just a "move me +x" command, so that position
     * remains fully synced.
     */
    private int u = 0;
    private Entity v;
    public boolean n = false;
    public Set trackedPlayers = new HashSet();

    public EntityTrackerEntry(Entity var1, int var2, int var3, boolean var4)
    {
        this.tracker = var1;
        this.b = var2;
        this.c = var3;
        this.isMoving = var4;
        this.xLoc = MathHelper.floor(var1.locX * 32.0D);
        this.yLoc = MathHelper.floor(var1.locY * 32.0D);
        this.zLoc = MathHelper.floor(var1.locZ * 32.0D);
        this.yRot = MathHelper.d(var1.yaw * 256.0F / 360.0F);
        this.xRot = MathHelper.d(var1.pitch * 256.0F / 360.0F);
        this.i = MathHelper.d(var1.func_70079_am() * 256.0F / 360.0F);
    }

    public boolean equals(Object var1)
    {
        return var1 instanceof EntityTrackerEntry ? ((EntityTrackerEntry)var1).tracker.id == this.tracker.id : false;
    }

    public int hashCode()
    {
        return this.tracker.id;
    }

    public void track(List var1)
    {
        this.n = false;

        if (!this.s || this.tracker.e(this.p, this.q, this.r) > 16.0D)
        {
            this.p = this.tracker.locX;
            this.q = this.tracker.locY;
            this.r = this.tracker.locZ;
            this.s = true;
            this.n = true;
            this.scanPlayers(var1);
        }

        if (this.v != this.tracker.vehicle)
        {
            this.v = this.tracker.vehicle;
            this.broadcast(new Packet39AttachEntity(this.tracker, this.tracker.vehicle));
        }

        if (this.tracker instanceof EntityItemFrame && this.m % 10 == 0)
        {
            EntityItemFrame var23 = (EntityItemFrame)this.tracker;
            ItemStack var24 = var23.func_82335_i();

            if (var24 != null && var24.getItem() instanceof ItemWorldMap)
            {
                WorldMap var26 = Item.MAP.getSavedMap(var24, this.tracker.world);
                Iterator var27 = var1.iterator();

                while (var27.hasNext())
                {
                    EntityHuman var29 = (EntityHuman)var27.next();
                    EntityPlayer var30 = (EntityPlayer)var29;
                    var26.a(var30, var24);

                    if (var30.netServerHandler.lowPriorityCount() <= 5)
                    {
                        Packet var31 = Item.MAP.c(var24, this.tracker.world, var30);

                        if (var31 != null)
                        {
                            var30.netServerHandler.sendPacket(var31);
                        }
                    }
                }
            }

            DataWatcher var28 = this.tracker.getDataWatcher();

            if (var28.a())
            {
                this.broadcastIncludingSelf(new Packet40EntityMetadata(this.tracker.id, var28, false));
            }
        }
        else if (this.m++ % this.c == 0 || this.tracker.am)
        {
            int var2;
            int var3;

            if (this.tracker.vehicle == null)
            {
                ++this.u;
                var2 = this.tracker.ar.a(this.tracker.locX);
                var3 = MathHelper.floor(this.tracker.locY * 32.0D);
                int var4 = this.tracker.ar.a(this.tracker.locZ);
                int var5 = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                int var6 = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                int var7 = var2 - this.xLoc;
                int var8 = var3 - this.yLoc;
                int var9 = var4 - this.zLoc;
                Object var10 = null;
                boolean var11 = Math.abs(var7) >= 4 || Math.abs(var8) >= 4 || Math.abs(var9) >= 4 || this.m % 60 == 0;
                boolean var12 = Math.abs(var5 - this.yRot) >= 4 || Math.abs(var6 - this.xRot) >= 4;

                if (var7 >= -128 && var7 < 128 && var8 >= -128 && var8 < 128 && var9 >= -128 && var9 < 128 && this.u <= 400)
                {
                    if (var11 && var12)
                    {
                        var10 = new Packet33RelEntityMoveLook(this.tracker.id, (byte)var7, (byte)var8, (byte)var9, (byte)var5, (byte)var6);
                    }
                    else if (var11)
                    {
                        var10 = new Packet31RelEntityMove(this.tracker.id, (byte)var7, (byte)var8, (byte)var9);
                    }
                    else if (var12)
                    {
                        var10 = new Packet32EntityLook(this.tracker.id, (byte)var5, (byte)var6);
                    }
                }
                else
                {
                    this.u = 0;
                    var10 = new Packet34EntityTeleport(this.tracker.id, var2, var3, var4, (byte)var5, (byte)var6);
                }

                if (this.isMoving)
                {
                    double var13 = this.tracker.motX - this.j;
                    double var15 = this.tracker.motY - this.k;
                    double var17 = this.tracker.motZ - this.l;
                    double var19 = 0.02D;
                    double var21 = var13 * var13 + var15 * var15 + var17 * var17;

                    if (var21 > var19 * var19 || var21 > 0.0D && this.tracker.motX == 0.0D && this.tracker.motY == 0.0D && this.tracker.motZ == 0.0D)
                    {
                        this.j = this.tracker.motX;
                        this.k = this.tracker.motY;
                        this.l = this.tracker.motZ;
                        this.broadcast(new Packet28EntityVelocity(this.tracker.id, this.j, this.k, this.l));
                    }
                }

                if (var10 != null)
                {
                    this.broadcast((Packet)var10);
                }

                DataWatcher var32 = this.tracker.getDataWatcher();

                if (var32.a())
                {
                    this.broadcastIncludingSelf(new Packet40EntityMetadata(this.tracker.id, var32, false));
                }

                if (var11)
                {
                    this.xLoc = var2;
                    this.yLoc = var3;
                    this.zLoc = var4;
                }

                if (var12)
                {
                    this.yRot = var5;
                    this.xRot = var6;
                }
            }
            else
            {
                var2 = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                var3 = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                boolean var25 = Math.abs(var2 - this.yRot) >= 4 || Math.abs(var3 - this.xRot) >= 4;

                if (var25)
                {
                    this.broadcast(new Packet32EntityLook(this.tracker.id, (byte)var2, (byte)var3));
                    this.yRot = var2;
                    this.xRot = var3;
                }

                this.xLoc = this.tracker.ar.a(this.tracker.locX);
                this.yLoc = MathHelper.floor(this.tracker.locY * 32.0D);
                this.zLoc = this.tracker.ar.a(this.tracker.locZ);
            }

            var2 = MathHelper.d(this.tracker.func_70079_am() * 256.0F / 360.0F);

            if (Math.abs(var2 - this.i) >= 4)
            {
                this.broadcast(new Packet35EntityHeadRotation(this.tracker.id, (byte)var2));
                this.i = var2;
            }

            this.tracker.am = false;
        }

        if (this.tracker.velocityChanged)
        {
            this.broadcastIncludingSelf(new Packet28EntityVelocity(this.tracker));
            this.tracker.velocityChanged = false;
        }
    }

    public void broadcast(Packet var1)
    {
        Iterator var2 = this.trackedPlayers.iterator();

        while (var2.hasNext())
        {
            EntityPlayer var3 = (EntityPlayer)var2.next();
            var3.netServerHandler.sendPacket(var1);
        }
    }

    public void broadcastIncludingSelf(Packet var1)
    {
        this.broadcast(var1);

        if (this.tracker instanceof EntityPlayer)
        {
            ((EntityPlayer)this.tracker).netServerHandler.sendPacket(var1);
        }
    }

    public void a()
    {
        Iterator var1 = this.trackedPlayers.iterator();

        while (var1.hasNext())
        {
            EntityPlayer var2 = (EntityPlayer)var1.next();
            var2.removeQueue.add(Integer.valueOf(this.tracker.id));
        }
    }

    public void a(EntityPlayer var1)
    {
        if (this.trackedPlayers.contains(var1))
        {
            var1.removeQueue.add(Integer.valueOf(this.tracker.id));
            this.trackedPlayers.remove(var1);
        }
    }

    public void updatePlayer(EntityPlayer var1)
    {
        if (var1 != this.tracker)
        {
            double var2 = var1.locX - (double)(this.xLoc / 32);
            double var4 = var1.locZ - (double)(this.zLoc / 32);

            if (var2 >= (double)(-this.b) && var2 <= (double)this.b && var4 >= (double)(-this.b) && var4 <= (double)this.b)
            {
                if (!this.trackedPlayers.contains(var1) && this.d(var1))
                {
                    this.trackedPlayers.add(var1);
                    Packet var6 = this.b();
                    var1.netServerHandler.sendPacket(var6);

                    if (this.tracker instanceof EntityItemFrame)
                    {
                        var1.netServerHandler.sendPacket(new Packet40EntityMetadata(this.tracker.id, this.tracker.getDataWatcher(), true));
                    }

                    this.j = this.tracker.motX;
                    this.k = this.tracker.motY;
                    this.l = this.tracker.motZ;
                    int var7 = MathHelper.floor(this.tracker.locX * 32.0D);
                    int var8 = MathHelper.floor(this.tracker.locY * 32.0D);
                    int var9 = MathHelper.floor(this.tracker.locZ * 32.0D);

                    if (var7 != this.xLoc || var8 != this.yLoc || var9 != this.zLoc)
                    {
                        FMLNetworkHandler.makeEntitySpawnAdjustment(this.tracker.id, var1, this.xLoc, this.yLoc, this.zLoc);
                    }

                    if (this.isMoving && !(var6 instanceof Packet24MobSpawn))
                    {
                        var1.netServerHandler.sendPacket(new Packet28EntityVelocity(this.tracker.id, this.tracker.motX, this.tracker.motY, this.tracker.motZ));
                    }

                    if (this.tracker.vehicle != null)
                    {
                        var1.netServerHandler.sendPacket(new Packet39AttachEntity(this.tracker, this.tracker.vehicle));
                    }

                    if (this.tracker instanceof EntityLiving)
                    {
                        for (int var10 = 0; var10 < 5; ++var10)
                        {
                            ItemStack var11 = ((EntityLiving)this.tracker).getEquipment(var10);

                            if (var11 != null)
                            {
                                var1.netServerHandler.sendPacket(new Packet5EntityEquipment(this.tracker.id, var10, var11));
                            }
                        }
                    }

                    if (this.tracker instanceof EntityHuman)
                    {
                        EntityHuman var15 = (EntityHuman)this.tracker;

                        if (var15.isSleeping())
                        {
                            var1.netServerHandler.sendPacket(new Packet17EntityLocationAction(this.tracker, 0, MathHelper.floor(this.tracker.locX), MathHelper.floor(this.tracker.locY), MathHelper.floor(this.tracker.locZ)));
                        }
                    }

                    if (this.tracker instanceof EntityLiving)
                    {
                        EntityLiving var13 = (EntityLiving)this.tracker;
                        Iterator var14 = var13.getEffects().iterator();

                        while (var14.hasNext())
                        {
                            MobEffect var12 = (MobEffect)var14.next();
                            var1.netServerHandler.sendPacket(new Packet41MobEffect(this.tracker.id, var12));
                        }
                    }
                }
            }
            else if (this.trackedPlayers.contains(var1))
            {
                this.trackedPlayers.remove(var1);
                var1.removeQueue.add(Integer.valueOf(this.tracker.id));
            }
        }
    }

    private boolean d(EntityPlayer var1)
    {
        return var1.p().getPlayerManager().a(var1, this.tracker.ai, this.tracker.ak);
    }

    public void scanPlayers(List var1)
    {
        Iterator var2 = var1.iterator();

        while (var2.hasNext())
        {
            EntityHuman var3 = (EntityHuman)var2.next();
            this.updatePlayer((EntityPlayer)var3);
        }
    }

    private Packet b()
    {
        if (this.tracker.dead)
        {
            System.out.println("Fetching addPacket for removed entity");
        }

        Packet var1 = FMLNetworkHandler.getEntitySpawningPacket(this.tracker);

        if (var1 != null)
        {
            return var1;
        }
        else if (this.tracker instanceof EntityItem)
        {
            EntityItem var11 = (EntityItem)this.tracker;
            Packet21PickupSpawn var8 = new Packet21PickupSpawn(var11);
            var11.locX = (double)var8.b / 32.0D;
            var11.locY = (double)var8.c / 32.0D;
            var11.locZ = (double)var8.d / 32.0D;
            return var8;
        }
        else if (this.tracker instanceof EntityPlayer)
        {
            return new Packet20NamedEntitySpawn((EntityHuman)this.tracker);
        }
        else
        {
            EntityMinecart var2;

            if (this.tracker instanceof EntityMinecart)
            {
                var2 = (EntityMinecart)this.tracker;

                if (var2.type == 0)
                {
                    return new Packet23VehicleSpawn(this.tracker, 10);
                }

                if (var2.type == 1)
                {
                    return new Packet23VehicleSpawn(this.tracker, 11);
                }

                if (var2.type == 2)
                {
                    return new Packet23VehicleSpawn(this.tracker, 12);
                }
            }

            if (this.tracker instanceof EntityBoat)
            {
                return new Packet23VehicleSpawn(this.tracker, 1);
            }
            else if (!(this.tracker instanceof IAnimal) && !(this.tracker instanceof EntityEnderDragon))
            {
                if (this.tracker instanceof EntityFishingHook)
                {
                    EntityHuman var10 = ((EntityFishingHook)this.tracker).owner;
                    return new Packet23VehicleSpawn(this.tracker, 90, var10 != null ? var10.id : this.tracker.id);
                }
                else if (this.tracker instanceof EntityArrow)
                {
                    Entity var9 = ((EntityArrow)this.tracker).shooter;
                    return new Packet23VehicleSpawn(this.tracker, 60, var9 != null ? var9.id : this.tracker.id);
                }
                else if (this.tracker instanceof EntitySnowball)
                {
                    return new Packet23VehicleSpawn(this.tracker, 61);
                }
                else if (this.tracker instanceof EntityPotion)
                {
                    return new Packet23VehicleSpawn(this.tracker, 73, ((EntityPotion)this.tracker).getPotionValue());
                }
                else if (this.tracker instanceof EntityThrownExpBottle)
                {
                    return new Packet23VehicleSpawn(this.tracker, 75);
                }
                else if (this.tracker instanceof EntityEnderPearl)
                {
                    return new Packet23VehicleSpawn(this.tracker, 65);
                }
                else if (this.tracker instanceof EntityEnderSignal)
                {
                    return new Packet23VehicleSpawn(this.tracker, 72);
                }
                else
                {
                    Packet23VehicleSpawn var5;

                    if (this.tracker instanceof EntityFireball)
                    {
                        EntityFireball var7 = (EntityFireball)this.tracker;
                        var2 = null;
                        byte var4 = 63;

                        if (this.tracker instanceof EntitySmallFireball)
                        {
                            var4 = 64;
                        }
                        else if (this.tracker instanceof EntityWitherSkull)
                        {
                            var4 = 66;
                        }

                        if (var7.shooter != null)
                        {
                            var5 = new Packet23VehicleSpawn(this.tracker, var4, ((EntityFireball)this.tracker).shooter.id);
                        }
                        else
                        {
                            var5 = new Packet23VehicleSpawn(this.tracker, var4, 0);
                        }

                        var5.e = (int)(var7.dirX * 8000.0D);
                        var5.f = (int)(var7.dirY * 8000.0D);
                        var5.g = (int)(var7.dirZ * 8000.0D);
                        return var5;
                    }
                    else if (this.tracker instanceof EntityEgg)
                    {
                        return new Packet23VehicleSpawn(this.tracker, 62);
                    }
                    else if (this.tracker instanceof EntityTNTPrimed)
                    {
                        return new Packet23VehicleSpawn(this.tracker, 50);
                    }
                    else if (this.tracker instanceof EntityEnderCrystal)
                    {
                        return new Packet23VehicleSpawn(this.tracker, 51);
                    }
                    else if (this.tracker instanceof EntityFallingBlock)
                    {
                        EntityFallingBlock var6 = (EntityFallingBlock)this.tracker;
                        return new Packet23VehicleSpawn(this.tracker, 70, var6.id | var6.field_70285_b << 16);
                    }
                    else if (this.tracker instanceof EntityPainting)
                    {
                        return new Packet25EntityPainting((EntityPainting)this.tracker);
                    }
                    else if (this.tracker instanceof EntityItemFrame)
                    {
                        EntityItemFrame var3 = (EntityItemFrame)this.tracker;
                        var5 = new Packet23VehicleSpawn(this.tracker, 71, var3.field_82332_a);
                        var5.b = MathHelper.d((float)(var3.x * 32));
                        var5.c = MathHelper.d((float)(var3.y * 32));
                        var5.d = MathHelper.d((float)(var3.z * 32));
                        return var5;
                    }
                    else if (this.tracker instanceof EntityExperienceOrb)
                    {
                        return new Packet26AddExpOrb((EntityExperienceOrb)this.tracker);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Don\'t know how to add " + this.tracker.getClass() + "!");
                    }
                }
            }
            else
            {
                this.i = MathHelper.d(this.tracker.func_70079_am() * 256.0F / 360.0F);
                return new Packet24MobSpawn((EntityLiving)this.tracker);
            }
        }
    }

    /**
     * Remove a tracked player from our list and tell the tracked player to destroy us from their world.
     */
    public void clear(EntityPlayer var1)
    {
        if (this.trackedPlayers.contains(var1))
        {
            this.trackedPlayers.remove(var1);
            var1.removeQueue.add(Integer.valueOf(this.tracker.id));
        }
    }
}
