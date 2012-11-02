package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class Entity
{
    private static int entityCount = 0;
    public int id;
    public double l;

    /**
     * Blocks entities from spawning when they do their AABB check to make sure the spot is clear of entities that can
     * prevent spawning.
     */
    public boolean m;

    /** The entity that is riding this entity */
    public Entity passenger;

    /** The entity we are currently riding */
    public Entity vehicle;

    /** Reference to the World object. */
    public World world;
    public double lastX;
    public double lastY;
    public double lastZ;

    /** Entity position X */
    public double locX;

    /** Entity position Y */
    public double locY;

    /** Entity position Z */
    public double locZ;

    /** Entity motion X */
    public double motX;

    /** Entity motion Y */
    public double motY;

    /** Entity motion Z */
    public double motZ;

    /** Entity rotation Yaw */
    public float yaw;

    /** Entity rotation Pitch */
    public float pitch;
    public float lastYaw;
    public float lastPitch;

    /** Axis aligned bounding box. */
    public final AxisAlignedBB boundingBox;
    public boolean onGround;

    /**
     * True if after a move this entity has collided with something on X- or Z-axis
     */
    public boolean positionChanged;

    /**
     * True if after a move this entity has collided with something on Y-axis
     */
    public boolean G;

    /**
     * True if after a move this entity has collided with something either vertically or horizontally
     */
    public boolean H;
    public boolean velocityChanged;
    protected boolean J;
    public boolean field_70135_K;

    /**
     * gets set by setEntityDead, so this must be the flag whether an Entity is dead (inactive may be better term)
     */
    public boolean dead;
    public float height;

    /** How wide this entity is considered to be */
    public float width;

    /** How high this entity is considered to be */
    public float length;

    /** The previous ticks distance walked multiplied by 0.6 */
    public float P;

    /** The distance walked multiplied by 0.6 */
    public float Q;
    public float field_82151_R;
    public float fallDistance;

    /**
     * The distance that has to be exceeded in order to triger a new step sound and an onEntityWalking event on a block
     */
    private int c;

    /**
     * The entity's X coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double T;

    /**
     * The entity's Y coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double U;

    /**
     * The entity's Z coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double V;
    public float W;

    /**
     * How high this entity can step up when running into a block to try to get over it (currently make note the entity
     * will always step up this amount and not just the amount needed)
     */
    public float X;

    /**
     * Whether this entity won't clip with collision or not (make note it won't disable gravity)
     */
    public boolean Y;

    /**
     * Reduces the velocity applied by entity collisions by the specified percent.
     */
    public float Z;
    protected Random random;

    /** How many ticks has this entity had ran since being alive */
    public int ticksLived;

    /**
     * The amount of ticks you have to stand inside of fire before be set on fire
     */
    public int maxFireTicks;
    private int fireTicks;

    /**
     * Whether this entity is currently inside of water (if it handles water movement that is)
     */
    protected boolean ad;

    /**
     * Remaining time an entity will be "immune" to further damage after being hurt.
     */
    public int noDamageTicks;
    private boolean justCreated;
    protected boolean fireProof;
    protected DataWatcher datawatcher;
    private double f;
    private double g;

    /** Has this entity been added to the chunk its within */
    public boolean ah;
    public int ai;
    public int aj;
    public int ak;

    /**
     * Render entity even if it is outside the camera frustum. Only true in EntityFish for now. Used in RenderGlobal:
     * render if ignoreFrustumCheck or in frustum.
     */
    public boolean al;
    public boolean am;
    public int an;

    /** Whether the entity is inside a Portal */
    protected boolean ao;
    private int field_82153_h;

    /** Which dimension the player is in (-1 = the Nether, 0 = normal world) */
    public int dimension;
    protected int field_82152_aq;
    public EnumEntitySize ar;

    public Entity(World var1)
    {
        this.id = entityCount++;
        this.l = 1.0D;
        this.m = false;
        this.boundingBox = AxisAlignedBB.a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        this.onGround = false;
        this.H = false;
        this.velocityChanged = false;
        this.field_70135_K = true;
        this.dead = false;
        this.height = 0.0F;
        this.width = 0.6F;
        this.length = 1.8F;
        this.P = 0.0F;
        this.Q = 0.0F;
        this.field_82151_R = 0.0F;
        this.fallDistance = 0.0F;
        this.c = 1;
        this.W = 0.0F;
        this.X = 0.0F;
        this.Y = false;
        this.Z = 0.0F;
        this.random = new Random();
        this.ticksLived = 0;
        this.maxFireTicks = 1;
        this.fireTicks = 0;
        this.ad = false;
        this.noDamageTicks = 0;
        this.justCreated = true;
        this.fireProof = false;
        this.datawatcher = new DataWatcher();
        this.ah = false;
        this.field_82152_aq = 0;
        this.ar = EnumEntitySize.SIZE_2;
        this.world = var1;
        this.setPosition(0.0D, 0.0D, 0.0D);

        if (var1 != null)
        {
            this.dimension = var1.worldProvider.dimension;
        }

        this.datawatcher.a(0, Byte.valueOf((byte)0));
        this.datawatcher.a(1, Short.valueOf((short)300));
        this.a();
    }

    protected abstract void a();

    public DataWatcher getDataWatcher()
    {
        return this.datawatcher;
    }

    public boolean equals(Object var1)
    {
        return var1 instanceof Entity ? ((Entity)var1).id == this.id : false;
    }

    public int hashCode()
    {
        return this.id;
    }

    /**
     * Will get destroyed next tick.
     */
    public void die()
    {
        this.dead = true;
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected void a(float var1, float var2)
    {
        this.width = var1;
        this.length = var2;
        float var3 = var1 % 2.0F;

        if ((double)var3 < 0.375D)
        {
            this.ar = EnumEntitySize.SIZE_1;
        }
        else if ((double)var3 < 0.75D)
        {
            this.ar = EnumEntitySize.SIZE_2;
        }
        else if ((double)var3 < 1.0D)
        {
            this.ar = EnumEntitySize.SIZE_3;
        }
        else if ((double)var3 < 1.375D)
        {
            this.ar = EnumEntitySize.SIZE_4;
        }
        else if ((double)var3 < 1.75D)
        {
            this.ar = EnumEntitySize.SIZE_5;
        }
        else
        {
            this.ar = EnumEntitySize.SIZE_6;
        }
    }

    /**
     * Sets the rotation of the entity. Args: yaw, pitch (both in degrees)
     */
    protected void b(float var1, float var2)
    {
        this.yaw = var1 % 360.0F;
        this.pitch = var2 % 360.0F;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double var1, double var3, double var5)
    {
        this.locX = var1;
        this.locY = var3;
        this.locZ = var5;
        float var7 = this.width / 2.0F;
        float var8 = this.length;
        this.boundingBox.b(var1 - (double)var7, var3 - (double)this.height + (double)this.W, var5 - (double)var7, var1 + (double)var7, var3 - (double)this.height + (double)this.W + (double)var8, var5 + (double)var7);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void j_()
    {
        this.y();
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void y()
    {
        this.world.methodProfiler.a("entityBaseTick");

        if (this.vehicle != null && this.vehicle.dead)
        {
            this.vehicle = null;
        }

        ++this.ticksLived;
        this.P = this.Q;
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
        int var2;

        if (!this.world.isStatic && this.world instanceof WorldServer)
        {
            MinecraftServer var1 = ((WorldServer)this.world).getMinecraftServer();
            var2 = this.z();

            if (this.ao)
            {
                if (var1.getAllowNether())
                {
                    if (this.vehicle == null && this.field_82153_h++ >= var2)
                    {
                        this.field_82153_h = var2;
                        this.an = this.ab();
                        byte var3;

                        if (this.world.worldProvider.dimension == -1)
                        {
                            var3 = 0;
                        }
                        else
                        {
                            var3 = -1;
                        }

                        this.b(var3);
                    }

                    this.ao = false;
                }
            }
            else
            {
                if (this.field_82153_h > 0)
                {
                    this.field_82153_h -= 4;
                }

                if (this.field_82153_h < 0)
                {
                    this.field_82153_h = 0;
                }
            }

            if (this.an > 0)
            {
                --this.an;
            }
        }

        int var9;

        if (this.isSprinting() && !this.H())
        {
            int var6 = MathHelper.floor(this.locX);
            var2 = MathHelper.floor(this.locY - 0.20000000298023224D - (double)this.height);
            var9 = MathHelper.floor(this.locZ);
            int var4 = this.world.getTypeId(var6, var2, var9);

            if (var4 > 0)
            {
                this.world.addParticle("tilecrack_" + var4, this.locX + ((double)this.random.nextFloat() - 0.5D) * (double)this.width, this.boundingBox.b + 0.1D, this.locZ + ((double)this.random.nextFloat() - 0.5D) * (double)this.width, -this.motX * 4.0D, 1.5D, -this.motZ * 4.0D);
            }
        }

        if (this.I())
        {
            if (!this.ad && !this.justCreated)
            {
                float var7 = MathHelper.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.2F;

                if (var7 > 1.0F)
                {
                    var7 = 1.0F;
                }

                this.world.makeSound(this, "liquid.splash", var7, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                float var8 = (float)MathHelper.floor(this.boundingBox.b);
                float var5;
                float var10;

                for (var9 = 0; (float)var9 < 1.0F + this.width * 20.0F; ++var9)
                {
                    var10 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
                    var5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.world.addParticle("bubble", this.locX + (double)var10, (double)(var8 + 1.0F), this.locZ + (double)var5, this.motX, this.motY - (double)(this.random.nextFloat() * 0.2F), this.motZ);
                }

                for (var9 = 0; (float)var9 < 1.0F + this.width * 20.0F; ++var9)
                {
                    var10 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
                    var5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.world.addParticle("splash", this.locX + (double)var10, (double)(var8 + 1.0F), this.locZ + (double)var5, this.motX, this.motY, this.motZ);
                }
            }

            this.fallDistance = 0.0F;
            this.ad = true;
            this.fireTicks = 0;
        }
        else
        {
            this.ad = false;
        }

        if (this.world.isStatic)
        {
            this.fireTicks = 0;
        }
        else if (this.fireTicks > 0)
        {
            if (this.fireProof)
            {
                this.fireTicks -= 4;

                if (this.fireTicks < 0)
                {
                    this.fireTicks = 0;
                }
            }
            else
            {
                if (this.fireTicks % 20 == 0)
                {
                    this.damageEntity(DamageSource.BURN, 1);
                }

                --this.fireTicks;
            }
        }

        if (this.J())
        {
            this.A();
            this.fallDistance *= 0.5F;
        }

        if (this.locY < -64.0D)
        {
            this.C();
        }

        if (!this.world.isStatic)
        {
            this.a(0, this.fireTicks > 0);
            this.a(2, this.vehicle != null);
        }

        this.justCreated = false;
        this.world.methodProfiler.b();
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int z()
    {
        return 0;
    }

    /**
     * Called whenever the entity is walking inside of lava.
     */
    protected void A()
    {
        if (!this.fireProof)
        {
            this.damageEntity(DamageSource.LAVA, 4);
            this.setOnFire(15);
        }
    }

    /**
     * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
     */
    public void setOnFire(int var1)
    {
        int var2 = var1 * 20;

        if (this.fireTicks < var2)
        {
            this.fireTicks = var2;
        }
    }

    /**
     * Removes fire from entity.
     */
    public void extinguish()
    {
        this.fireTicks = 0;
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void C()
    {
        this.die();
    }

    /**
     * Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     */
    public boolean c(double var1, double var3, double var5)
    {
        AxisAlignedBB var7 = this.boundingBox.c(var1, var3, var5);
        List var8 = this.world.getCubes(this, var7);
        return !var8.isEmpty() ? false : !this.world.containsLiquid(var7);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void move(double var1, double var3, double var5)
    {
        if (this.Y)
        {
            this.boundingBox.d(var1, var3, var5);
            this.locX = (this.boundingBox.a + this.boundingBox.d) / 2.0D;
            this.locY = this.boundingBox.b + (double)this.height - (double)this.W;
            this.locZ = (this.boundingBox.c + this.boundingBox.f) / 2.0D;
        }
        else
        {
            this.world.methodProfiler.a("move");
            this.W *= 0.4F;
            double var7 = this.locX;
            double var9 = this.locY;
            double var11 = this.locZ;

            if (this.J)
            {
                this.J = false;
                var1 *= 0.25D;
                var3 *= 0.05000000074505806D;
                var5 *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double var13 = var1;
            double var15 = var3;
            double var17 = var5;
            AxisAlignedBB var19 = this.boundingBox.clone();
            boolean var20 = this.onGround && this.isSneaking() && this instanceof EntityHuman;

            if (var20)
            {
                double var21;

                for (var21 = 0.05D; var1 != 0.0D && this.world.getCubes(this, this.boundingBox.c(var1, -1.0D, 0.0D)).isEmpty(); var13 = var1)
                {
                    if (var1 < var21 && var1 >= -var21)
                    {
                        var1 = 0.0D;
                    }
                    else if (var1 > 0.0D)
                    {
                        var1 -= var21;
                    }
                    else
                    {
                        var1 += var21;
                    }
                }

                for (; var5 != 0.0D && this.world.getCubes(this, this.boundingBox.c(0.0D, -1.0D, var5)).isEmpty(); var17 = var5)
                {
                    if (var5 < var21 && var5 >= -var21)
                    {
                        var5 = 0.0D;
                    }
                    else if (var5 > 0.0D)
                    {
                        var5 -= var21;
                    }
                    else
                    {
                        var5 += var21;
                    }
                }

                while (var1 != 0.0D && var5 != 0.0D && this.world.getCubes(this, this.boundingBox.c(var1, -1.0D, var5)).isEmpty())
                {
                    if (var1 < var21 && var1 >= -var21)
                    {
                        var1 = 0.0D;
                    }
                    else if (var1 > 0.0D)
                    {
                        var1 -= var21;
                    }
                    else
                    {
                        var1 += var21;
                    }

                    if (var5 < var21 && var5 >= -var21)
                    {
                        var5 = 0.0D;
                    }
                    else if (var5 > 0.0D)
                    {
                        var5 -= var21;
                    }
                    else
                    {
                        var5 += var21;
                    }

                    var13 = var1;
                    var17 = var5;
                }
            }

            List var36 = this.world.getCubes(this, this.boundingBox.a(var1, var3, var5));
            AxisAlignedBB var23;

            for (Iterator var22 = var36.iterator(); var22.hasNext(); var3 = var23.b(this.boundingBox, var3))
            {
                var23 = (AxisAlignedBB)var22.next();
            }

            this.boundingBox.d(0.0D, var3, 0.0D);

            if (!this.field_70135_K && var15 != var3)
            {
                var5 = 0.0D;
                var3 = 0.0D;
                var1 = 0.0D;
            }

            boolean var34 = this.onGround || var15 != var3 && var15 < 0.0D;
            AxisAlignedBB var24;
            Iterator var35;

            for (var35 = var36.iterator(); var35.hasNext(); var1 = var24.a(this.boundingBox, var1))
            {
                var24 = (AxisAlignedBB)var35.next();
            }

            this.boundingBox.d(var1, 0.0D, 0.0D);

            if (!this.field_70135_K && var13 != var1)
            {
                var5 = 0.0D;
                var3 = 0.0D;
                var1 = 0.0D;
            }

            for (var35 = var36.iterator(); var35.hasNext(); var5 = var24.c(this.boundingBox, var5))
            {
                var24 = (AxisAlignedBB)var35.next();
            }

            this.boundingBox.d(0.0D, 0.0D, var5);

            if (!this.field_70135_K && var17 != var5)
            {
                var5 = 0.0D;
                var3 = 0.0D;
                var1 = 0.0D;
            }

            double var25;
            double var27;
            double var37;

            if (this.X > 0.0F && var34 && (var20 || this.W < 0.05F) && (var13 != var1 || var17 != var5))
            {
                var37 = var1;
                var25 = var3;
                var27 = var5;
                var1 = var13;
                var3 = (double)this.X;
                var5 = var17;
                AxisAlignedBB var29 = this.boundingBox.clone();
                this.boundingBox.c(var19);
                var36 = this.world.getCubes(this, this.boundingBox.a(var13, var3, var17));
                AxisAlignedBB var31;
                Iterator var30;

                for (var30 = var36.iterator(); var30.hasNext(); var3 = var31.b(this.boundingBox, var3))
                {
                    var31 = (AxisAlignedBB)var30.next();
                }

                this.boundingBox.d(0.0D, var3, 0.0D);

                if (!this.field_70135_K && var15 != var3)
                {
                    var5 = 0.0D;
                    var3 = 0.0D;
                    var1 = 0.0D;
                }

                for (var30 = var36.iterator(); var30.hasNext(); var1 = var31.a(this.boundingBox, var1))
                {
                    var31 = (AxisAlignedBB)var30.next();
                }

                this.boundingBox.d(var1, 0.0D, 0.0D);

                if (!this.field_70135_K && var13 != var1)
                {
                    var5 = 0.0D;
                    var3 = 0.0D;
                    var1 = 0.0D;
                }

                for (var30 = var36.iterator(); var30.hasNext(); var5 = var31.c(this.boundingBox, var5))
                {
                    var31 = (AxisAlignedBB)var30.next();
                }

                this.boundingBox.d(0.0D, 0.0D, var5);

                if (!this.field_70135_K && var17 != var5)
                {
                    var5 = 0.0D;
                    var3 = 0.0D;
                    var1 = 0.0D;
                }

                if (!this.field_70135_K && var15 != var3)
                {
                    var5 = 0.0D;
                    var3 = 0.0D;
                    var1 = 0.0D;
                }
                else
                {
                    var3 = (double)(-this.X);

                    for (var30 = var36.iterator(); var30.hasNext(); var3 = var31.b(this.boundingBox, var3))
                    {
                        var31 = (AxisAlignedBB)var30.next();
                    }

                    this.boundingBox.d(0.0D, var3, 0.0D);
                }

                if (var37 * var37 + var27 * var27 >= var1 * var1 + var5 * var5)
                {
                    var1 = var37;
                    var3 = var25;
                    var5 = var27;
                    this.boundingBox.c(var29);
                }
                else
                {
                    double var38 = this.boundingBox.b - (double)((int)this.boundingBox.b);

                    if (var38 > 0.0D)
                    {
                        this.W = (float)((double)this.W + var38 + 0.01D);
                    }
                }
            }

            this.world.methodProfiler.b();
            this.world.methodProfiler.a("rest");
            this.locX = (this.boundingBox.a + this.boundingBox.d) / 2.0D;
            this.locY = this.boundingBox.b + (double)this.height - (double)this.W;
            this.locZ = (this.boundingBox.c + this.boundingBox.f) / 2.0D;
            this.positionChanged = var13 != var1 || var17 != var5;
            this.G = var15 != var3;
            this.onGround = var15 != var3 && var15 < 0.0D;
            this.H = this.positionChanged || this.G;
            this.a(var3, this.onGround);

            if (var13 != var1)
            {
                this.motX = 0.0D;
            }

            if (var15 != var3)
            {
                this.motY = 0.0D;
            }

            if (var17 != var5)
            {
                this.motZ = 0.0D;
            }

            var37 = this.locX - var7;
            var25 = this.locY - var9;
            var27 = this.locZ - var11;

            if (this.f_() && !var20 && this.vehicle == null)
            {
                int var39 = MathHelper.floor(this.locX);
                int var42 = MathHelper.floor(this.locY - 0.20000000298023224D - (double)this.height);
                int var41 = MathHelper.floor(this.locZ);
                int var32 = this.world.getTypeId(var39, var42, var41);

                if (var32 == 0 && this.world.getTypeId(var39, var42 - 1, var41) == Block.FENCE.id)
                {
                    var32 = this.world.getTypeId(var39, var42 - 1, var41);
                }

                if (var32 != Block.LADDER.id)
                {
                    var25 = 0.0D;
                }

                this.Q = (float)((double)this.Q + (double)MathHelper.sqrt(var37 * var37 + var27 * var27) * 0.6D);
                this.field_82151_R = (float)((double)this.field_82151_R + (double)MathHelper.sqrt(var37 * var37 + var25 * var25 + var27 * var27) * 0.6D);

                if (this.field_82151_R > (float)this.c && var32 > 0)
                {
                    this.c = (int)this.field_82151_R + 1;

                    if (this.H())
                    {
                        float var33 = MathHelper.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.35F;

                        if (var33 > 1.0F)
                        {
                            var33 = 1.0F;
                        }

                        this.world.makeSound(this, "liquid.swim", var33, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    }

                    this.a(var39, var42, var41, var32);
                    Block.byId[var32].b(this.world, var39, var42, var41, this);
                }
            }

            this.D();
            boolean var40 = this.G();

            if (this.world.e(this.boundingBox.shrink(0.001D, 0.001D, 0.001D)))
            {
                this.burn(1);

                if (!var40)
                {
                    ++this.fireTicks;

                    if (this.fireTicks == 0)
                    {
                        this.setOnFire(8);
                    }
                }
            }
            else if (this.fireTicks <= 0)
            {
                this.fireTicks = -this.maxFireTicks;
            }

            if (var40 && this.fireTicks > 0)
            {
                this.world.makeSound(this, "random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.fireTicks = -this.maxFireTicks;
            }

            this.world.methodProfiler.b();
        }
    }

    /**
     * Checks for block collisions, and calls the associated onBlockCollided method for the collided block.
     */
    protected void D()
    {
        int var1 = MathHelper.floor(this.boundingBox.a + 0.001D);
        int var2 = MathHelper.floor(this.boundingBox.b + 0.001D);
        int var3 = MathHelper.floor(this.boundingBox.c + 0.001D);
        int var4 = MathHelper.floor(this.boundingBox.d - 0.001D);
        int var5 = MathHelper.floor(this.boundingBox.e - 0.001D);
        int var6 = MathHelper.floor(this.boundingBox.f - 0.001D);

        if (this.world.d(var1, var2, var3, var4, var5, var6))
        {
            for (int var7 = var1; var7 <= var4; ++var7)
            {
                for (int var8 = var2; var8 <= var5; ++var8)
                {
                    for (int var9 = var3; var9 <= var6; ++var9)
                    {
                        int var10 = this.world.getTypeId(var7, var8, var9);

                        if (var10 > 0)
                        {
                            Block.byId[var10].a(this.world, var7, var8, var9, this);
                        }
                    }
                }
            }
        }
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void a(int var1, int var2, int var3, int var4)
    {
        StepSound var5 = Block.byId[var4].stepSound;

        if (this.world.getTypeId(var1, var2 + 1, var3) == Block.SNOW.id)
        {
            var5 = Block.SNOW.stepSound;
            this.world.makeSound(this, var5.getName(), var5.getVolume1() * 0.15F, var5.getVolume2());
        }
        else if (!Block.byId[var4].material.isLiquid())
        {
            this.world.makeSound(this, var5.getName(), var5.getVolume1() * 0.15F, var5.getVolume2());
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean f_()
    {
        return true;
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void a(double var1, boolean var3)
    {
        if (var3)
        {
            if (this.fallDistance > 0.0F)
            {
                this.a(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        }
        else if (var1 < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - var1);
        }
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB E()
    {
        return null;
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void burn(int var1)
    {
        if (!this.fireProof)
        {
            this.damageEntity(DamageSource.FIRE, var1);
        }
    }

    public final boolean isFireproof()
    {
        return this.fireProof;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void a(float var1)
    {
        if (this.passenger != null)
        {
            this.passenger.a(var1);
        }
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    public boolean G()
    {
        return this.ad || this.world.B(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    public boolean H()
    {
        return this.ad;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean I()
    {
        return this.world.a(this.boundingBox.grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D, 0.001D, 0.001D), Material.WATER, this);
    }

    /**
     * Checks if the current block the entity is within of the specified material type
     */
    public boolean a(Material var1)
    {
        double var2 = this.locY + (double)this.getHeadHeight();
        int var4 = MathHelper.floor(this.locX);
        int var5 = MathHelper.d((float)MathHelper.floor(var2));
        int var6 = MathHelper.floor(this.locZ);
        int var7 = this.world.getTypeId(var4, var5, var6);

        if (var7 != 0 && Block.byId[var7].material == var1)
        {
            float var8 = BlockFluids.d(this.world.getData(var4, var5, var6)) - 0.11111111F;
            float var9 = (float)(var5 + 1) - var8;
            return var2 < (double)var9;
        }
        else
        {
            return false;
        }
    }

    public float getHeadHeight()
    {
        return 0.0F;
    }

    /**
     * Whether or not the current entity is in lava
     */
    public boolean J()
    {
        return this.world.a(this.boundingBox.grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.LAVA);
    }

    /**
     * Used in both water and by flying objects
     */
    public void a(float var1, float var2, float var3)
    {
        float var4 = var1 * var1 + var2 * var2;

        if (var4 >= 1.0E-4F)
        {
            var4 = MathHelper.c(var4);

            if (var4 < 1.0F)
            {
                var4 = 1.0F;
            }

            var4 = var3 / var4;
            var1 *= var4;
            var2 *= var4;
            float var5 = MathHelper.sin(this.yaw * (float)Math.PI / 180.0F);
            float var6 = MathHelper.cos(this.yaw * (float)Math.PI / 180.0F);
            this.motX += (double)(var1 * var6 - var2 * var5);
            this.motZ += (double)(var2 * var6 + var1 * var5);
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float c(float var1)
    {
        int var2 = MathHelper.floor(this.locX);
        int var3 = MathHelper.floor(this.locZ);

        if (this.world.isLoaded(var2, 0, var3))
        {
            double var4 = (this.boundingBox.e - this.boundingBox.b) * 0.66D;
            int var6 = MathHelper.floor(this.locY - (double)this.height + var4);
            return this.world.o(var2, var6, var3);
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Sets the reference to the World object.
     */
    public void spawnIn(World var1)
    {
        this.world = var1;
    }

    /**
     * Sets the entity's position and rotation. Args: posX, posY, posZ, yaw, pitch
     */
    public void setLocation(double var1, double var3, double var5, float var7, float var8)
    {
        this.lastX = this.locX = var1;
        this.lastY = this.locY = var3;
        this.lastZ = this.locZ = var5;
        this.lastYaw = this.yaw = var7;
        this.lastPitch = this.pitch = var8;
        this.W = 0.0F;
        double var9 = (double)(this.lastYaw - var7);

        if (var9 < -180.0D)
        {
            this.lastYaw += 360.0F;
        }

        if (var9 >= 180.0D)
        {
            this.lastYaw -= 360.0F;
        }

        this.setPosition(this.locX, this.locY, this.locZ);
        this.b(var7, var8);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setPositionRotation(double var1, double var3, double var5, float var7, float var8)
    {
        this.T = this.lastX = this.locX = var1;
        this.U = this.lastY = this.locY = var3 + (double)this.height;
        this.V = this.lastZ = this.locZ = var5;
        this.yaw = var7;
        this.pitch = var8;
        this.setPosition(this.locX, this.locY, this.locZ);
    }

    /**
     * Returns the distance to the entity. Args: entity
     */
    public float d(Entity var1)
    {
        float var2 = (float)(this.locX - var1.locX);
        float var3 = (float)(this.locY - var1.locY);
        float var4 = (float)(this.locZ - var1.locZ);
        return MathHelper.c(var2 * var2 + var3 * var3 + var4 * var4);
    }

    /**
     * Gets the squared distance to the position. Args: x, y, z
     */
    public double e(double var1, double var3, double var5)
    {
        double var7 = this.locX - var1;
        double var9 = this.locY - var3;
        double var11 = this.locZ - var5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    /**
     * Gets the distance to the position. Args: x, y, z
     */
    public double f(double var1, double var3, double var5)
    {
        double var7 = this.locX - var1;
        double var9 = this.locY - var3;
        double var11 = this.locZ - var5;
        return (double)MathHelper.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
    }

    /**
     * Returns the squared distance to the entity. Args: entity
     */
    public double e(Entity var1)
    {
        double var2 = this.locX - var1.locX;
        double var4 = this.locY - var1.locY;
        double var6 = this.locZ - var1.locZ;
        return var2 * var2 + var4 * var4 + var6 * var6;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void b_(EntityHuman var1) {}

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void collide(Entity var1)
    {
        if (var1.passenger != this && var1.vehicle != this)
        {
            double var2 = var1.locX - this.locX;
            double var4 = var1.locZ - this.locZ;
            double var6 = MathHelper.a(var2, var4);

            if (var6 >= 0.009999999776482582D)
            {
                var6 = (double)MathHelper.sqrt(var6);
                var2 /= var6;
                var4 /= var6;
                double var8 = 1.0D / var6;

                if (var8 > 1.0D)
                {
                    var8 = 1.0D;
                }

                var2 *= var8;
                var4 *= var8;
                var2 *= 0.05000000074505806D;
                var4 *= 0.05000000074505806D;
                var2 *= (double)(1.0F - this.Z);
                var4 *= (double)(1.0F - this.Z);
                this.g(-var2, 0.0D, -var4);
                var1.g(var2, 0.0D, var4);
            }
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void g(double var1, double var3, double var5)
    {
        this.motX += var1;
        this.motY += var3;
        this.motZ += var5;
        this.am = true;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void K()
    {
        this.velocityChanged = true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean damageEntity(DamageSource var1, int var2)
    {
        this.K();
        return false;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean L()
    {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean M()
    {
        return false;
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void c(Entity var1, int var2) {}

    /**
     * adds the ID of this entity to the NBT given
     */
    public boolean c(NBTTagCompound var1)
    {
        String var2 = this.Q();

        if (!this.dead && var2 != null)
        {
            var1.setString("id", var2);
            this.d(var1);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Save the entity to NBT (calls an abstract helper method to write extra data)
     */
    public void d(NBTTagCompound var1)
    {
        var1.set("Pos", this.a(new double[] {this.locX, this.locY + (double)this.W, this.locZ}));
        var1.set("Motion", this.a(new double[] {this.motX, this.motY, this.motZ}));
        var1.set("Rotation", this.a(new float[] {this.yaw, this.pitch}));
        var1.setFloat("FallDistance", this.fallDistance);
        var1.setShort("Fire", (short)this.fireTicks);
        var1.setShort("Air", (short)this.getAirTicks());
        var1.setBoolean("OnGround", this.onGround);
        var1.setInt("Dimension", this.dimension);
        this.b(var1);
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    public void e(NBTTagCompound var1)
    {
        NBTTagList var2 = var1.getList("Pos");
        NBTTagList var3 = var1.getList("Motion");
        NBTTagList var4 = var1.getList("Rotation");
        this.motX = ((NBTTagDouble)var3.get(0)).data;
        this.motY = ((NBTTagDouble)var3.get(1)).data;
        this.motZ = ((NBTTagDouble)var3.get(2)).data;

        if (Math.abs(this.motX) > 10.0D)
        {
            this.motX = 0.0D;
        }

        if (Math.abs(this.motY) > 10.0D)
        {
            this.motY = 0.0D;
        }

        if (Math.abs(this.motZ) > 10.0D)
        {
            this.motZ = 0.0D;
        }

        this.lastX = this.T = this.locX = ((NBTTagDouble)var2.get(0)).data;
        this.lastY = this.U = this.locY = ((NBTTagDouble)var2.get(1)).data;
        this.lastZ = this.V = this.locZ = ((NBTTagDouble)var2.get(2)).data;
        this.lastYaw = this.yaw = ((NBTTagFloat)var4.get(0)).data;
        this.lastPitch = this.pitch = ((NBTTagFloat)var4.get(1)).data;
        this.fallDistance = var1.getFloat("FallDistance");
        this.fireTicks = var1.getShort("Fire");
        this.setAirTicks(var1.getShort("Air"));
        this.onGround = var1.getBoolean("OnGround");
        this.dimension = var1.getInt("Dimension");
        this.setPosition(this.locX, this.locY, this.locZ);
        this.b(this.yaw, this.pitch);
        this.a(var1);
    }

    /**
     * Returns the string that identifies this Entity's class
     */
    protected final String Q()
    {
        return EntityTypes.b(this);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected abstract void a(NBTTagCompound var1);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected abstract void b(NBTTagCompound var1);

    /**
     * creates a NBT list from the array of doubles passed to this function
     */
    protected NBTTagList a(double ... var1)
    {
        NBTTagList var2 = new NBTTagList();
        double[] var3 = var1;
        int var4 = var1.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            double var6 = var3[var5];
            var2.add(new NBTTagDouble((String)null, var6));
        }

        return var2;
    }

    /**
     * Returns a new NBTTagList filled with the specified floats
     */
    protected NBTTagList a(float ... var1)
    {
        NBTTagList var2 = new NBTTagList();
        float[] var3 = var1;
        int var4 = var1.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            float var6 = var3[var5];
            var2.add(new NBTTagFloat((String)null, var6));
        }

        return var2;
    }

    /**
     * Drops an item stack at the entity's position. Args: itemID, count
     */
    public EntityItem b(int var1, int var2)
    {
        return this.a(var1, var2, 0.0F);
    }

    /**
     * Drops an item stack with a specified y offset. Args: itemID, count, yOffset
     */
    public EntityItem a(int var1, int var2, float var3)
    {
        return this.a(new ItemStack(var1, var2, 0), var3);
    }

    /**
     * Drops an item at the position of the entity.
     */
    public EntityItem a(ItemStack var1, float var2)
    {
        EntityItem var3 = new EntityItem(this.world, this.locX, this.locY + (double)var2, this.locZ, var1);
        var3.pickupDelay = 10;
        this.world.addEntity(var3);
        return var3;
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isAlive()
    {
        return !this.dead;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean inBlock()
    {
        for (int var1 = 0; var1 < 8; ++var1)
        {
            float var2 = ((float)((var1 >> 0) % 2) - 0.5F) * this.width * 0.8F;
            float var3 = ((float)((var1 >> 1) % 2) - 0.5F) * 0.1F;
            float var4 = ((float)((var1 >> 2) % 2) - 0.5F) * this.width * 0.8F;
            int var5 = MathHelper.floor(this.locX + (double)var2);
            int var6 = MathHelper.floor(this.locY + (double)this.getHeadHeight() + (double)var3);
            int var7 = MathHelper.floor(this.locZ + (double)var4);

            if (this.world.s(var5, var6, var7))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean c(EntityHuman var1)
    {
        return false;
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB g(Entity var1)
    {
        return null;
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void U()
    {
        if (this.vehicle.dead)
        {
            this.vehicle = null;
        }
        else
        {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
            this.j_();

            if (this.vehicle != null)
            {
                this.vehicle.V();
                this.g += (double)(this.vehicle.yaw - this.vehicle.lastYaw);

                for (this.f += (double)(this.vehicle.pitch - this.vehicle.lastPitch); this.g >= 180.0D; this.g -= 360.0D)
                {
                    ;
                }

                while (this.g < -180.0D)
                {
                    this.g += 360.0D;
                }

                while (this.f >= 180.0D)
                {
                    this.f -= 360.0D;
                }

                while (this.f < -180.0D)
                {
                    this.f += 360.0D;
                }

                double var1 = this.g * 0.5D;
                double var3 = this.f * 0.5D;
                float var5 = 10.0F;

                if (var1 > (double)var5)
                {
                    var1 = (double)var5;
                }

                if (var1 < (double)(-var5))
                {
                    var1 = (double)(-var5);
                }

                if (var3 > (double)var5)
                {
                    var3 = (double)var5;
                }

                if (var3 < (double)(-var5))
                {
                    var3 = (double)(-var5);
                }

                this.g -= var1;
                this.f -= var3;
                this.yaw = (float)((double)this.yaw + var1);
                this.pitch = (float)((double)this.pitch + var3);
            }
        }
    }

    public void V()
    {
        if (!(this.passenger instanceof EntityHuman) || !((EntityHuman)this.passenger).func_71066_bF())
        {
            this.passenger.T = this.T;
            this.passenger.U = this.U + this.X() + this.passenger.W();
            this.passenger.V = this.V;
        }

        this.passenger.setPosition(this.locX, this.locY + this.X() + this.passenger.W(), this.locZ);
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double W()
    {
        return (double)this.height;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double X()
    {
        return (double)this.length * 0.75D;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mount(Entity var1)
    {
        this.f = 0.0D;
        this.g = 0.0D;

        if (var1 == null)
        {
            if (this.vehicle != null)
            {
                this.setPositionRotation(this.vehicle.locX, this.vehicle.boundingBox.b + (double)this.vehicle.length, this.vehicle.locZ, this.yaw, this.pitch);
                this.vehicle.passenger = null;
            }

            this.vehicle = null;
        }
        else if (this.vehicle == var1)
        {
            this.h(var1);
            this.vehicle.passenger = null;
            this.vehicle = null;
        }
        else
        {
            if (this.vehicle != null)
            {
                this.vehicle.passenger = null;
            }

            if (var1.passenger != null)
            {
                var1.passenger.vehicle = null;
            }

            this.vehicle = var1;
            var1.passenger = this;
        }
    }

    /**
     * Called when a player unounts an entity.
     */
    public void h(Entity var1)
    {
        double var3 = var1.locX;
        double var5 = var1.boundingBox.b + (double)var1.length;
        double var7 = var1.locZ;

        for (double var9 = -1.5D; var9 < 2.0D; ++var9)
        {
            for (double var11 = -1.5D; var11 < 2.0D; ++var11)
            {
                if (var9 != 0.0D || var11 != 0.0D)
                {
                    int var13 = (int)(this.locX + var9);
                    int var14 = (int)(this.locZ + var11);
                    AxisAlignedBB var2 = this.boundingBox.c(var9, 1.0D, var11);

                    if (this.world.a(var2).isEmpty())
                    {
                        if (this.world.t(var13, (int)this.locY, var14))
                        {
                            this.setPositionRotation(this.locX + var9, this.locY + 1.0D, this.locZ + var11, this.yaw, this.pitch);
                            return;
                        }

                        if (this.world.t(var13, (int)this.locY - 1, var14) || this.world.getMaterial(var13, (int)this.locY - 1, var14) == Material.WATER)
                        {
                            var3 = this.locX + var9;
                            var5 = this.locY + 1.0D;
                            var7 = this.locZ + var11;
                        }
                    }
                }
            }
        }

        this.setPositionRotation(var3, var5, var7, this.yaw, this.pitch);
    }

    public float Y()
    {
        return 0.1F;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3D Z()
    {
        return null;
    }

    /**
     * Called by portal blocks when an entity is within it.
     */
    public void aa()
    {
        if (this.an > 0)
        {
            this.an = this.ab();
        }
        else
        {
            double var1 = this.lastX - this.locX;
            double var3 = this.lastZ - this.locZ;

            if (!this.world.isStatic && !this.ao)
            {
                this.field_82152_aq = Direction.func_82372_a(var1, var3);
            }

            this.ao = true;
        }
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int ab()
    {
        return 500;
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it seems)
     */
    public ItemStack[] getEquipment()
    {
        return null;
    }

    public void func_70062_b(int var1, ItemStack var2) {}

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return this.fireTicks > 0 || this.e(0);
    }

    public boolean func_70115_ae()
    {
        return this.vehicle != null || this.e(2);
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        return this.e(1);
    }

    /**
     * Sets the sneaking flag.
     */
    public void setSneaking(boolean var1)
    {
        this.a(1, var1);
    }

    /**
     * Get if the Entity is sprinting.
     */
    public boolean isSprinting()
    {
        return this.e(3);
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean var1)
    {
        this.a(3, var1);
    }

    public boolean func_82150_aj()
    {
        return this.e(5);
    }

    public void func_82142_c(boolean var1)
    {
        this.a(5, var1);
    }

    public void d(boolean var1)
    {
        this.a(4, var1);
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0) is burning; 1) is sneaking; 2) is riding
     * something; 3) is sprinting; 4) is eating
     */
    protected boolean e(int var1)
    {
        return (this.datawatcher.getByte(0) & 1 << var1) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void a(int var1, boolean var2)
    {
        byte var3 = this.datawatcher.getByte(0);

        if (var2)
        {
            this.datawatcher.watch(0, Byte.valueOf((byte)(var3 | 1 << var1)));
        }
        else
        {
            this.datawatcher.watch(0, Byte.valueOf((byte)(var3 & ~(1 << var1))));
        }
    }

    public int getAirTicks()
    {
        return this.datawatcher.getShort(1);
    }

    public void setAirTicks(int var1)
    {
        this.datawatcher.watch(1, Short.valueOf((short)var1));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void a(EntityLightning var1)
    {
        this.burn(5);
        ++this.fireTicks;

        if (this.fireTicks == 0)
        {
            this.setOnFire(8);
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void a(EntityLiving var1) {}

    /**
     * Adds velocity to push the entity out of blocks at the specified x, y, z position Args: x, y, z
     */
    protected boolean i(double var1, double var3, double var5)
    {
        int var7 = MathHelper.floor(var1);
        int var8 = MathHelper.floor(var3);
        int var9 = MathHelper.floor(var5);
        double var10 = var1 - (double)var7;
        double var12 = var3 - (double)var8;
        double var14 = var5 - (double)var9;

        if (this.world.s(var7, var8, var9))
        {
            boolean var16 = !this.world.s(var7 - 1, var8, var9);
            boolean var17 = !this.world.s(var7 + 1, var8, var9);
            boolean var18 = !this.world.s(var7, var8 - 1, var9);
            boolean var19 = !this.world.s(var7, var8 + 1, var9);
            boolean var20 = !this.world.s(var7, var8, var9 - 1);
            boolean var21 = !this.world.s(var7, var8, var9 + 1);
            byte var22 = -1;
            double var23 = 9999.0D;

            if (var16 && var10 < var23)
            {
                var23 = var10;
                var22 = 0;
            }

            if (var17 && 1.0D - var10 < var23)
            {
                var23 = 1.0D - var10;
                var22 = 1;
            }

            if (var18 && var12 < var23)
            {
                var23 = var12;
                var22 = 2;
            }

            if (var19 && 1.0D - var12 < var23)
            {
                var23 = 1.0D - var12;
                var22 = 3;
            }

            if (var20 && var14 < var23)
            {
                var23 = var14;
                var22 = 4;
            }

            if (var21 && 1.0D - var14 < var23)
            {
                var23 = 1.0D - var14;
                var22 = 5;
            }

            float var25 = this.random.nextFloat() * 0.2F + 0.1F;

            if (var22 == 0)
            {
                this.motX = (double)(-var25);
            }

            if (var22 == 1)
            {
                this.motX = (double)var25;
            }

            if (var22 == 2)
            {
                this.motY = (double)(-var25);
            }

            if (var22 == 3)
            {
                this.motY = (double)var25;
            }

            if (var22 == 4)
            {
                this.motZ = (double)(-var25);
            }

            if (var22 == 5)
            {
                this.motZ = (double)var25;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void am()
    {
        this.J = true;
        this.fallDistance = 0.0F;
    }

    /**
     * Gets the username of the entity.
     */
    public String getLocalizedName()
    {
        String var1 = EntityTypes.b(this);

        if (var1 == null)
        {
            var1 = "generic";
        }

        return LocaleI18n.get("entity." + var1 + ".name");
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] ao()
    {
        return null;
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean i(Entity var1)
    {
        return this == var1;
    }

    public float func_70079_am()
    {
        return 0.0F;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean aq()
    {
        return true;
    }

    public String toString()
    {
        return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[] {this.getClass().getSimpleName(), this.getLocalizedName(), Integer.valueOf(this.id), this.world == null ? "~NULL~" : this.world.getWorldData().getName(), Double.valueOf(this.locX), Double.valueOf(this.locY), Double.valueOf(this.locZ)});
    }

    public void func_82149_j(Entity var1)
    {
        this.setPositionRotation(var1.locX, var1.locY, var1.locZ, var1.yaw, var1.pitch);
    }

    public void func_82141_a(Entity var1, boolean var2)
    {
        NBTTagCompound var3 = new NBTTagCompound();
        var1.d(var3);
        this.e(var3);
        this.an = var1.an;
        this.field_82152_aq = var1.field_82152_aq;
    }

    public void b(int var1)
    {
        if (!this.world.isStatic && !this.dead)
        {
            MinecraftServer var2 = MinecraftServer.getServer();
            int var3 = this.dimension;
            WorldServer var4 = var2.getWorldServer(var3);
            WorldServer var5 = var2.getWorldServer(var1);
            this.dimension = var1;
            this.world.kill(this);
            this.dead = false;
            var2.getServerConfigurationManager().a(this, var3, var4, var5);
            Entity var6 = EntityTypes.createEntityByName(EntityTypes.b(this), var5);

            if (var6 != null)
            {
                var6.func_82141_a(this, true);
                var5.addEntity(var6);
            }

            this.dead = true;
            var4.func_82742_i();
            var5.func_82742_i();
        }
    }

    public float func_82146_a(Explosion var1, Block var2, int var3, int var4, int var5)
    {
        return var2.a(this);
    }

    public int func_82143_as()
    {
        return 3;
    }

    public int func_82148_at()
    {
        return this.field_82152_aq;
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    public boolean au()
    {
        return false;
    }
}
