package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Iterator;
import java.util.List;

public class EntityEnderDragon extends EntityLiving implements ou, IComplex
{
    public double a;
    public double b;
    public double c;

    /**
     * Ring buffer array for the last 64 Y-positions and yaw rotations. Used to calculate offsets for the animations.
     */
    public double[][] d = new double[64][3];

    /**
     * Index into the ring buffer. Incremented once per tick and restarts at 0 once it reaches the end of the buffer.
     */
    public int e = -1;

    /** An array containing all body parts of this dragon */
    public EntityComplexPart[] children;

    /** The head bounding box of a dragon */
    public EntityComplexPart g;

    /** The body bounding box of a dragon */
    public EntityComplexPart h;
    public EntityComplexPart i;
    public EntityComplexPart j;
    public EntityComplexPart bK;
    public EntityComplexPart bL;
    public EntityComplexPart bM;

    /** Animation time at previous tick. */
    public float bN = 0.0F;

    /**
     * Animation time, used to control the speed of the animation cycles (wings flapping, jaw opening, etc.)
     */
    public float bO = 0.0F;

    /** Force selecting a new flight target at next tick if set to true. */
    public boolean bP = false;

    /**
     * Activated if the dragon is flying though obsidian, white stone or bedrock. Slows movement and animation speed.
     */
    public boolean bQ = false;
    private Entity bT;
    public int bR = 0;

    /** The current endercrystal that is healing this dragon */
    public EntityEnderCrystal bS = null;

    public EntityEnderDragon(World var1)
    {
        super(var1);
        this.children = new EntityComplexPart[] {this.g = new EntityComplexPart(this, "head", 6.0F, 6.0F), this.h = new EntityComplexPart(this, "body", 8.0F, 8.0F), this.i = new EntityComplexPart(this, "tail", 4.0F, 4.0F), this.j = new EntityComplexPart(this, "tail", 4.0F, 4.0F), this.bK = new EntityComplexPart(this, "tail", 4.0F, 4.0F), this.bL = new EntityComplexPart(this, "wing", 4.0F, 4.0F), this.bM = new EntityComplexPart(this, "wing", 4.0F, 4.0F)};
        this.setHealth(this.getMaxHealth());
        this.texture = "/mob/enderdragon/ender.png";
        this.a(16.0F, 8.0F);
        this.Y = true;
        this.fireProof = true;
        this.b = 100.0D;
        this.al = true;
    }

    public int getMaxHealth()
    {
        return 200;
    }

    protected void a()
    {
        super.a();
        this.datawatcher.a(16, new Integer(this.getMaxHealth()));
    }

    /**
     * Returns a double[3] array with movement offsets, used to calculate trailing tail/neck positions. [0] = yaw
     * offset, [1] = y offset, [2] = unused, always 0. Parameters: buffer index offset, partial ticks.
     */
    public double[] a(int var1, float var2)
    {
        if (this.health <= 0)
        {
            var2 = 0.0F;
        }

        var2 = 1.0F - var2;
        int var3 = this.e - var1 * 1 & 63;
        int var4 = this.e - var1 * 1 - 1 & 63;
        double[] var5 = new double[3];
        double var6 = this.d[var3][0];
        double var8 = MathHelper.g(this.d[var4][0] - var6);
        var5[0] = var6 + var8 * (double)var2;
        var6 = this.d[var3][1];
        var8 = this.d[var4][1] - var6;
        var5[1] = var6 + var8 * (double)var2;
        var5[2] = this.d[var3][2] + (this.d[var4][2] - this.d[var3][2]) * (double)var2;
        return var5;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void c()
    {
        float var1;
        float var2;

        if (!this.world.isStatic)
        {
            this.datawatcher.watch(16, Integer.valueOf(this.health));
        }
        else
        {
            var1 = MathHelper.cos(this.bO * (float)Math.PI * 2.0F);
            var2 = MathHelper.cos(this.bN * (float)Math.PI * 2.0F);

            if (var2 <= -0.3F && var1 >= -0.3F)
            {
                this.world.func_72980_b(this.locX, this.locY, this.locZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.random.nextFloat() * 0.3F);
            }
        }

        this.bN = this.bO;
        float var3;

        if (this.health <= 0)
        {
            var1 = (this.random.nextFloat() - 0.5F) * 8.0F;
            var2 = (this.random.nextFloat() - 0.5F) * 4.0F;
            var3 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle("largeexplode", this.locX + (double)var1, this.locY + 2.0D + (double)var2, this.locZ + (double)var3, 0.0D, 0.0D, 0.0D);
        }
        else
        {
            this.h();
            var1 = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 10.0F + 1.0F);
            var1 *= (float)Math.pow(2.0D, this.motY);

            if (this.bQ)
            {
                this.bO += var1 * 0.5F;
            }
            else
            {
                this.bO += var1;
            }

            this.yaw = MathHelper.g(this.yaw);

            if (this.e < 0)
            {
                for (int var4 = 0; var4 < this.d.length; ++var4)
                {
                    this.d[var4][0] = (double)this.yaw;
                    this.d[var4][1] = this.locY;
                }
            }

            if (++this.e == this.d.length)
            {
                this.e = 0;
            }

            this.d[this.e][0] = (double)this.yaw;
            this.d[this.e][1] = this.locY;
            double var6;
            double var8;
            double var10;
            float var12;
            float var23;
            float var24;
            float var26;
            double var28;
            float var35;

            if (this.world.isStatic)
            {
                if (this.bu > 0)
                {
                    var10 = this.locX + (this.bv - this.locX) / (double)this.bu;
                    var28 = this.locY + (this.bw - this.locY) / (double)this.bu;
                    var6 = this.locZ + (this.bx - this.locZ) / (double)this.bu;
                    var8 = MathHelper.g(this.by - (double)this.yaw);
                    this.yaw = (float)((double)this.yaw + var8 / (double)this.bu);
                    this.pitch = (float)((double)this.pitch + (this.bz - (double)this.pitch) / (double)this.bu);
                    --this.bu;
                    this.setPosition(var10, var28, var6);
                    this.b(this.yaw, this.pitch);
                }
            }
            else
            {
                var10 = this.a - this.locX;
                var28 = this.b - this.locY;
                var6 = this.c - this.locZ;
                var8 = var10 * var10 + var28 * var28 + var6 * var6;
                double var13;
                double var15;

                if (this.bT != null)
                {
                    this.a = this.bT.locX;
                    this.c = this.bT.locZ;
                    var13 = this.a - this.locX;
                    var15 = this.c - this.locZ;
                    double var17 = Math.sqrt(var13 * var13 + var15 * var15);
                    double var19 = 0.4000000059604645D + var17 / 80.0D - 1.0D;

                    if (var19 > 10.0D)
                    {
                        var19 = 10.0D;
                    }

                    this.b = this.bT.boundingBox.b + var19;
                }
                else
                {
                    this.a += this.random.nextGaussian() * 2.0D;
                    this.c += this.random.nextGaussian() * 2.0D;
                }

                if (this.bP || var8 < 100.0D || var8 > 22500.0D || this.positionChanged || this.G)
                {
                    this.i();
                }

                var28 /= (double)MathHelper.sqrt(var10 * var10 + var6 * var6);
                var12 = 0.6F;

                if (var28 < (double)(-var12))
                {
                    var28 = (double)(-var12);
                }

                if (var28 > (double)var12)
                {
                    var28 = (double)var12;
                }

                this.motY += var28 * 0.10000000149011612D;
                this.yaw = MathHelper.g(this.yaw);
                var13 = 180.0D - Math.atan2(var10, var6) * 180.0D / Math.PI;
                var15 = MathHelper.g(var13 - (double)this.yaw);

                if (var15 > 50.0D)
                {
                    var15 = 50.0D;
                }

                if (var15 < -50.0D)
                {
                    var15 = -50.0D;
                }

                Vec3D var31 = this.world.getVec3DPool().create(this.a - this.locX, this.b - this.locY, this.c - this.locZ).a();
                Vec3D var18 = this.world.getVec3DPool().create((double)MathHelper.sin(this.yaw * (float)Math.PI / 180.0F), this.motY, (double)(-MathHelper.cos(this.yaw * (float)Math.PI / 180.0F))).a();
                var35 = (float)(var18.b(var31) + 0.5D) / 1.5F;

                if (var35 < 0.0F)
                {
                    var35 = 0.0F;
                }

                this.bF *= 0.8F;
                float var20 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0F + 1.0F;
                double var21 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0D + 1.0D;

                if (var21 > 40.0D)
                {
                    var21 = 40.0D;
                }

                this.bF = (float)((double)this.bF + var15 * (0.699999988079071D / var21 / (double)var20));
                this.yaw += this.bF * 0.1F;
                var23 = (float)(2.0D / (var21 + 1.0D));
                var24 = 0.06F;
                this.a(0.0F, -1.0F, var24 * (var35 * var23 + (1.0F - var23)));

                if (this.bQ)
                {
                    this.move(this.motX * 0.800000011920929D, this.motY * 0.800000011920929D, this.motZ * 0.800000011920929D);
                }
                else
                {
                    this.move(this.motX, this.motY, this.motZ);
                }

                Vec3D var25 = this.world.getVec3DPool().create(this.motX, this.motY, this.motZ).a();
                var26 = (float)(var25.b(var18) + 1.0D) / 2.0F;
                var26 = 0.8F + 0.15F * var26;
                this.motX *= (double)var26;
                this.motZ *= (double)var26;
                this.motY *= 0.9100000262260437D;
            }

            this.aw = this.yaw;
            this.g.width = this.g.length = 3.0F;
            this.i.width = this.i.length = 2.0F;
            this.j.width = this.j.length = 2.0F;
            this.bK.width = this.bK.length = 2.0F;
            this.h.length = 3.0F;
            this.h.width = 5.0F;
            this.bL.length = 2.0F;
            this.bL.width = 4.0F;
            this.bM.length = 3.0F;
            this.bM.width = 4.0F;
            var2 = (float)(this.a(5, 1.0F)[1] - this.a(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
            var3 = MathHelper.cos(var2);
            float var29 = -MathHelper.sin(var2);
            float var14 = this.yaw * (float)Math.PI / 180.0F;
            float var32 = MathHelper.sin(var14);
            float var16 = MathHelper.cos(var14);
            this.h.j_();
            this.h.setPositionRotation(this.locX + (double)(var32 * 0.5F), this.locY, this.locZ - (double)(var16 * 0.5F), 0.0F, 0.0F);
            this.bL.j_();
            this.bL.setPositionRotation(this.locX + (double)(var16 * 4.5F), this.locY + 2.0D, this.locZ + (double)(var32 * 4.5F), 0.0F, 0.0F);
            this.bM.j_();
            this.bM.setPositionRotation(this.locX - (double)(var16 * 4.5F), this.locY + 2.0D, this.locZ - (double)(var32 * 4.5F), 0.0F, 0.0F);

            if (!this.world.isStatic && this.hurtTicks == 0)
            {
                this.a(this.world.getEntities(this, this.bL.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                this.a(this.world.getEntities(this, this.bM.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                this.b(this.world.getEntities(this, this.g.boundingBox.grow(1.0D, 1.0D, 1.0D)));
            }

            double[] var30 = this.a(5, 1.0F);
            double[] var33 = this.a(0, 1.0F);
            var12 = MathHelper.sin(this.yaw * (float)Math.PI / 180.0F - this.bF * 0.01F);
            var35 = MathHelper.cos(this.yaw * (float)Math.PI / 180.0F - this.bF * 0.01F);
            this.g.j_();
            this.g.setPositionRotation(this.locX + (double)(var12 * 5.5F * var3), this.locY + (var33[1] - var30[1]) * 1.0D + (double)(var29 * 5.5F), this.locZ - (double)(var35 * 5.5F * var3), 0.0F, 0.0F);

            for (int var34 = 0; var34 < 3; ++var34)
            {
                EntityComplexPart var36 = null;

                if (var34 == 0)
                {
                    var36 = this.i;
                }

                if (var34 == 1)
                {
                    var36 = this.j;
                }

                if (var34 == 2)
                {
                    var36 = this.bK;
                }

                double[] var22 = this.a(12 + var34 * 2, 1.0F);
                var23 = this.yaw * (float)Math.PI / 180.0F + this.b(var22[0] - var30[0]) * (float)Math.PI / 180.0F * 1.0F;
                var24 = MathHelper.sin(var23);
                float var37 = MathHelper.cos(var23);
                var26 = 1.5F;
                float var27 = (float)(var34 + 1) * 2.0F;
                var36.j_();
                var36.setPositionRotation(this.locX - (double)((var32 * var26 + var24 * var27) * var3), this.locY + (var22[1] - var30[1]) * 1.0D - (double)((var27 + var26) * var29) + 1.5D, this.locZ + (double)((var16 * var26 + var37 * var27) * var3), 0.0F, 0.0F);
            }

            if (!this.world.isStatic)
            {
                this.bQ = this.a(this.g.boundingBox) | this.a(this.h.boundingBox);
            }
        }
    }

    /**
     * Updates the state of the enderdragon's current endercrystal.
     */
    private void h()
    {
        if (this.bS != null)
        {
            if (this.bS.dead)
            {
                if (!this.world.isStatic)
                {
                    this.a(this.g, DamageSource.EXPLOSION, 10);
                }

                this.bS = null;
            }
            else if (this.ticksLived % 10 == 0 && this.health < this.getMaxHealth())
            {
                ++this.health;
            }
        }

        if (this.random.nextInt(10) == 0)
        {
            float var1 = 32.0F;
            List var2 = this.world.a(EntityEnderCrystal.class, this.boundingBox.grow((double)var1, (double)var1, (double)var1));
            EntityEnderCrystal var3 = null;
            double var4 = Double.MAX_VALUE;
            Iterator var6 = var2.iterator();

            while (var6.hasNext())
            {
                EntityEnderCrystal var7 = (EntityEnderCrystal)var6.next();
                double var8 = var7.e(this);

                if (var8 < var4)
                {
                    var4 = var8;
                    var3 = var7;
                }
            }

            this.bS = var3;
        }
    }

    /**
     * Pushes all entities inside the list away from the enderdragon.
     */
    private void a(List var1)
    {
        double var2 = (this.h.boundingBox.a + this.h.boundingBox.d) / 2.0D;
        double var4 = (this.h.boundingBox.c + this.h.boundingBox.f) / 2.0D;
        Iterator var6 = var1.iterator();

        while (var6.hasNext())
        {
            Entity var7 = (Entity)var6.next();

            if (var7 instanceof EntityLiving)
            {
                double var8 = var7.locX - var2;
                double var10 = var7.locZ - var4;
                double var12 = var8 * var8 + var10 * var10;
                var7.g(var8 / var12 * 4.0D, 0.20000000298023224D, var10 / var12 * 4.0D);
            }
        }
    }

    /**
     * Attacks all entities inside this list, dealing 5 hearts of damage.
     */
    private void b(List var1)
    {
        Iterator var2 = var1.iterator();

        while (var2.hasNext())
        {
            Entity var3 = (Entity)var2.next();

            if (var3 instanceof EntityLiving)
            {
                var3.damageEntity(DamageSource.mobAttack(this), 10);
            }
        }
    }

    /**
     * Sets a new target for the flight AI. It can be a random coordinate or a nearby player.
     */
    private void i()
    {
        this.bP = false;

        if (this.random.nextInt(2) == 0 && !this.world.players.isEmpty())
        {
            this.bT = (Entity)this.world.players.get(this.random.nextInt(this.world.players.size()));
        }
        else
        {
            boolean var1 = false;

            do
            {
                this.a = 0.0D;
                this.b = (double)(70.0F + this.random.nextFloat() * 50.0F);
                this.c = 0.0D;
                this.a += (double)(this.random.nextFloat() * 120.0F - 60.0F);
                this.c += (double)(this.random.nextFloat() * 120.0F - 60.0F);
                double var2 = this.locX - this.a;
                double var4 = this.locY - this.b;
                double var6 = this.locZ - this.c;
                var1 = var2 * var2 + var4 * var4 + var6 * var6 > 100.0D;
            }
            while (!var1);

            this.bT = null;
        }
    }

    /**
     * Simplifies the value of a number by adding/subtracting 180 to the point that the number is between -180 and 180.
     */
    private float b(double var1)
    {
        return (float)MathHelper.g(var1);
    }

    /**
     * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
     */
    private boolean a(AxisAlignedBB var1)
    {
        int var2 = MathHelper.floor(var1.a);
        int var3 = MathHelper.floor(var1.b);
        int var4 = MathHelper.floor(var1.c);
        int var5 = MathHelper.floor(var1.d);
        int var6 = MathHelper.floor(var1.e);
        int var7 = MathHelper.floor(var1.f);
        boolean var8 = false;
        boolean var9 = false;

        for (int var10 = var2; var10 <= var5; ++var10)
        {
            for (int var11 = var3; var11 <= var6; ++var11)
            {
                for (int var12 = var4; var12 <= var7; ++var12)
                {
                    int var13 = this.world.getTypeId(var10, var11, var12);
                    Block var14 = Block.byId[var13];

                    if (var14 != null)
                    {
                        if (var14.canDragonDestroy(this.world, var10, var11, var12))
                        {
                            var9 = true;
                            this.world.setTypeId(var10, var11, var12, 0);
                        }
                        else
                        {
                            var8 = true;
                        }
                    }
                }
            }
        }

        if (var9)
        {
            double var16 = var1.a + (var1.d - var1.a) * (double)this.random.nextFloat();
            double var17 = var1.b + (var1.e - var1.b) * (double)this.random.nextFloat();
            double var18 = var1.c + (var1.f - var1.c) * (double)this.random.nextFloat();
            this.world.addParticle("largeexplode", var16, var17, var18, 0.0D, 0.0D, 0.0D);
        }

        return var8;
    }

    public boolean a(EntityComplexPart var1, DamageSource var2, int var3)
    {
        if (var1 != this.g)
        {
            var3 = var3 / 4 + 1;
        }

        float var4 = this.yaw * (float)Math.PI / 180.0F;
        float var5 = MathHelper.sin(var4);
        float var6 = MathHelper.cos(var4);
        this.a = this.locX + (double)(var5 * 5.0F) + (double)((this.random.nextFloat() - 0.5F) * 2.0F);
        this.b = this.locY + (double)(this.random.nextFloat() * 3.0F) + 1.0D;
        this.c = this.locZ - (double)(var6 * 5.0F) + (double)((this.random.nextFloat() - 0.5F) * 2.0F);
        this.bT = null;

        if (var2.getEntity() instanceof EntityHuman || var2 == DamageSource.EXPLOSION)
        {
            this.func_82195_e(var2, var3);
        }

        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean damageEntity(DamageSource var1, int var2)
    {
        return false;
    }

    protected boolean func_82195_e(DamageSource var1, int var2)
    {
        return super.damageEntity(var1, var2);
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void aO()
    {
        ++this.bR;

        if (this.bR >= 180 && this.bR <= 200)
        {
            float var1 = (this.random.nextFloat() - 0.5F) * 8.0F;
            float var2 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float var3 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle("hugeexplosion", this.locX + (double)var1, this.locY + 2.0D + (double)var2, this.locZ + (double)var3, 0.0D, 0.0D, 0.0D);
        }

        int var4;
        int var5;

        if (!this.world.isStatic)
        {
            if (this.bR > 150 && this.bR % 5 == 0)
            {
                var4 = 1000;

                while (var4 > 0)
                {
                    var5 = EntityExperienceOrb.getOrbValue(var4);
                    var4 -= var5;
                    this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, var5));
                }
            }

            if (this.bR == 1)
            {
                this.world.func_82739_e(1018, (int)this.locX, (int)this.locY, (int)this.locZ, 0);
            }
        }

        this.move(0.0D, 0.10000000149011612D, 0.0D);
        this.aw = this.yaw += 20.0F;

        if (this.bR == 200 && !this.world.isStatic)
        {
            var4 = 2000;

            while (var4 > 0)
            {
                var5 = EntityExperienceOrb.getOrbValue(var4);
                var4 -= var5;
                this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, var5));
            }

            this.c(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));
            this.die();
        }
    }

    /**
     * Creates the ender portal leading back to the normal world after defeating the enderdragon.
     */
    private void c(int var1, int var2)
    {
        byte var3 = 64;
        BlockEnderPortal.a = true;
        byte var4 = 4;

        for (int var5 = var3 - 1; var5 <= var3 + 32; ++var5)
        {
            for (int var6 = var1 - var4; var6 <= var1 + var4; ++var6)
            {
                for (int var7 = var2 - var4; var7 <= var2 + var4; ++var7)
                {
                    double var8 = (double)(var6 - var1);
                    double var10 = (double)(var7 - var2);
                    double var12 = var8 * var8 + var10 * var10;

                    if (var12 <= ((double)var4 - 0.5D) * ((double)var4 - 0.5D))
                    {
                        if (var5 < var3)
                        {
                            if (var12 <= ((double)(var4 - 1) - 0.5D) * ((double)(var4 - 1) - 0.5D))
                            {
                                this.world.setTypeId(var6, var5, var7, Block.BEDROCK.id);
                            }
                        }
                        else if (var5 > var3)
                        {
                            this.world.setTypeId(var6, var5, var7, 0);
                        }
                        else if (var12 > ((double)(var4 - 1) - 0.5D) * ((double)(var4 - 1) - 0.5D))
                        {
                            this.world.setTypeId(var6, var5, var7, Block.BEDROCK.id);
                        }
                        else
                        {
                            this.world.setTypeId(var6, var5, var7, Block.ENDER_PORTAL.id);
                        }
                    }
                }
            }
        }

        this.world.setTypeId(var1, var3 + 0, var2, Block.BEDROCK.id);
        this.world.setTypeId(var1, var3 + 1, var2, Block.BEDROCK.id);
        this.world.setTypeId(var1, var3 + 2, var2, Block.BEDROCK.id);
        this.world.setTypeId(var1 - 1, var3 + 2, var2, Block.TORCH.id);
        this.world.setTypeId(var1 + 1, var3 + 2, var2, Block.TORCH.id);
        this.world.setTypeId(var1, var3 + 2, var2 - 1, Block.TORCH.id);
        this.world.setTypeId(var1, var3 + 2, var2 + 1, Block.TORCH.id);
        this.world.setTypeId(var1, var3 + 3, var2, Block.BEDROCK.id);
        this.world.setTypeId(var1, var3 + 4, var2, Block.DRAGON_EGG.id);
        BlockEnderPortal.a = false;
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void bh() {}

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] ao()
    {
        return this.children;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean L()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public int func_70968_i()
    {
        return this.datawatcher.getInt(16);
    }

    public World func_82194_d()
    {
        return this.world;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String aW()
    {
        return "mob.enderdragon.growl";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String aX()
    {
        return "mob.enderdragon.hit";
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float aV()
    {
        return 5.0F;
    }
}
