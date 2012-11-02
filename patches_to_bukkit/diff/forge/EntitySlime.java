package net.minecraft.server;

public class EntitySlime extends EntityLiving implements IMonster
{
    public float field_70813_a;
    public float field_70811_b;
    public float field_70812_c;

    /** ticks until this slime jumps again */
    private int jumpDelay = 0;

    public EntitySlime(World var1)
    {
        super(var1);
        this.texture = "/mob/slime.png";
        int var2 = 1 << this.random.nextInt(3);
        this.height = 0.0F;
        this.jumpDelay = this.random.nextInt(20) + 10;
        this.setSize(var2);
    }

    protected void a()
    {
        super.a();
        this.datawatcher.a(16, new Byte((byte)1));
    }

    public void setSize(int var1)
    {
        this.datawatcher.watch(16, new Byte((byte)var1));
        this.a(0.6F * (float)var1, 0.6F * (float)var1);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.setHealth(this.getMaxHealth());
        this.bc = var1;
    }

    public int getMaxHealth()
    {
        int var1 = this.getSize();
        return var1 * var1;
    }

    /**
     * Returns the size of the slime.
     */
    public int getSize()
    {
        return this.datawatcher.getByte(16);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.setInt("Size", this.getSize() - 1);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        this.setSize(var1.getInt("Size") + 1);
    }

    /**
     * Returns the name of a particle effect that may be randomly created by EntitySlime.onUpdate()
     */
    protected String h()
    {
        return "slime";
    }

    /**
     * Returns the name of the sound played when the slime jumps.
     */
    protected String n()
    {
        return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void j_()
    {
        if (!this.world.isStatic && this.world.difficulty == 0 && this.getSize() > 0)
        {
            this.dead = true;
        }

        this.field_70811_b += (this.field_70813_a - this.field_70811_b) * 0.5F;
        this.field_70812_c = this.field_70811_b;
        boolean var1 = this.onGround;
        super.j_();

        if (this.onGround && !var1)
        {
            int var2 = this.getSize();

            for (int var3 = 0; var3 < var2 * 8; ++var3)
            {
                float var4 = this.random.nextFloat() * (float)Math.PI * 2.0F;
                float var5 = this.random.nextFloat() * 0.5F + 0.5F;
                float var6 = MathHelper.sin(var4) * (float)var2 * 0.5F * var5;
                float var7 = MathHelper.cos(var4) * (float)var2 * 0.5F * var5;
                this.world.addParticle(this.h(), this.locX + (double)var6, this.boundingBox.b, this.locZ + (double)var7, 0.0D, 0.0D, 0.0D);
            }

            if (this.o())
            {
                this.world.makeSound(this, this.n(), this.aV(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.field_70813_a = -0.5F;
        }
        else if (!this.onGround && var1)
        {
            this.field_70813_a = 1.0F;
        }

        this.func_70808_l();
    }

    protected void bk()
    {
        this.bh();
        EntityHuman var1 = this.world.findNearbyVulnerablePlayer(this, 16.0D);

        if (var1 != null)
        {
            this.a(var1, 10.0F, 20.0F);
        }

        if (this.onGround && this.jumpDelay-- <= 0)
        {
            this.jumpDelay = this.j();

            if (var1 != null)
            {
                this.jumpDelay /= 3;
            }

            this.bG = true;

            if (this.q())
            {
                this.world.makeSound(this, this.n(), this.aV(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.bD = 1.0F - this.random.nextFloat() * 2.0F;
            this.bE = (float)(1 * this.getSize());
        }
        else
        {
            this.bG = false;

            if (this.onGround)
            {
                this.bD = this.bE = 0.0F;
            }
        }
    }

    protected void func_70808_l()
    {
        this.field_70813_a *= 0.6F;
    }

    /**
     * Gets the amount of time the slime needs to wait between jumps.
     */
    protected int j()
    {
        return this.random.nextInt(20) + 10;
    }

    protected EntitySlime i()
    {
        return new EntitySlime(this.world);
    }

    /**
     * Will get destroyed next tick.
     */
    public void die()
    {
        int var1 = this.getSize();

        if (!this.world.isStatic && var1 > 1 && this.getHealth() <= 0)
        {
            int var2 = 2 + this.random.nextInt(3);

            for (int var3 = 0; var3 < var2; ++var3)
            {
                float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 4.0F;
                float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 4.0F;
                EntitySlime var6 = this.i();
                var6.setSize(var1 / 2);
                var6.setPositionRotation(this.locX + (double)var4, this.locY + 0.5D, this.locZ + (double)var5, this.random.nextFloat() * 360.0F, 0.0F);
                this.world.addEntity(var6);
            }
        }

        super.die();
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void b_(EntityHuman var1)
    {
        if (this.l())
        {
            int var2 = this.getSize();

            if (this.m(var1) && this.e(var1) < 0.6D * (double)var2 * 0.6D * (double)var2 && var1.damageEntity(DamageSource.mobAttack(this), this.m()))
            {
                this.world.makeSound(this, "mob.attack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }

    /**
     * Indicates weather the slime is able to damage the player (based upon the slime's size)
     */
    protected boolean l()
    {
        return this.getSize() > 1;
    }

    /**
     * Gets the amount of damage dealt to the player when "attacked" by the slime.
     */
    protected int m()
    {
        return this.getSize();
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String aX()
    {
        return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String aY()
    {
        return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getLootId()
    {
        return this.getSize() == 1 ? Item.SLIME_BALL.id : 0;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean canSpawn()
    {
        Chunk var1 = this.world.getChunkAtWorldCoords(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));

        if (this.world.getWorldData().getType().handleSlimeSpawnReduction(this.random, this.world))
        {
            return false;
        }
        else
        {
            if (this.getSize() == 1 || this.world.difficulty > 0)
            {
                if (this.world.getBiome(MathHelper.floor(this.locX), MathHelper.floor(this.locZ)) == BiomeBase.SWAMPLAND && this.locY > 50.0D && this.locY < 70.0D && this.world.getLightLevel(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) <= this.random.nextInt(8))
                {
                    return super.canSpawn();
                }

                if (this.random.nextInt(10) == 0 && var1.a(987234911L).nextInt(10) == 0 && this.locY < 40.0D)
                {
                    return super.canSpawn();
                }
            }

            return false;
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float aV()
    {
        return 0.4F * (float)this.getSize();
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int bm()
    {
        return 0;
    }

    /**
     * Returns true if the slime makes a sound when it jumps (based upon the slime's size)
     */
    protected boolean q()
    {
        return this.getSize() > 0;
    }

    /**
     * Returns true if the slime makes a sound when it lands after a jump (based upon the slime's size)
     */
    protected boolean o()
    {
        return this.getSize() > 2;
    }
}
