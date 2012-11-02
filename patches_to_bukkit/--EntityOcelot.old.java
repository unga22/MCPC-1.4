package net.minecraft.server;

public class EntityOcelot extends EntityTameableAnimal
{
    /**
     * The tempt AI task for this mob, used to prevent taming while it is fleeing.
     */
    private PathfinderGoalTempt e;

    public EntityOcelot(World var1)
    {
        super(var1);
        this.texture = "/mob/ozelot.png";
        this.a(0.6F, 0.8F);
        this.getNavigation().a(true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.d);
        this.goalSelector.a(3, this.e = new PathfinderGoalTempt(this, 0.18F, Item.RAW_FISH.id, true));
        this.goalSelector.a(4, new PathfinderGoalAvoidPlayer(this, EntityHuman.class, 16.0F, 0.23F, 0.4F));
        this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 0.3F, 10.0F, 5.0F));
        this.goalSelector.a(6, new PathfinderGoalJumpOnBlock(this, 0.4F));
        this.goalSelector.a(7, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.a(8, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.a(9, new PathfinderGoalBreed(this, 0.23F));
        this.goalSelector.a(10, new PathfinderGoalRandomStroll(this, 0.23F));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed(this, EntityChicken.class, 14.0F, 750, false));
    }

    protected void a()
    {
        super.a();
        this.datawatcher.a(18, Byte.valueOf((byte)0));
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    public void bj()
    {
        if (this.getControllerMove().func_75640_a())
        {
            float var1 = this.getControllerMove().b();

            if (var1 == 0.18F)
            {
                this.setSneaking(true);
                this.setSprinting(false);
            }
            else if (var1 == 0.4F)
            {
                this.setSneaking(false);
                this.setSprinting(true);
            }
            else
            {
                this.setSneaking(false);
                this.setSprinting(false);
            }
        }
        else
        {
            this.setSneaking(false);
            this.setSprinting(false);
        }
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean bg()
    {
        return !this.isTamed();
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean bb()
    {
        return true;
    }

    public int getMaxHealth()
    {
        return 10;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void a(float var1) {}

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.setInt("CatType", this.getCatType());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        this.setCatType(var1.getInt("CatType"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String aW()
    {
        return this.isTamed() ? (this.r() ? "mob.cat.purr" : (this.random.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow")) : "";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String aX()
    {
        return "mob.cat.hitt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String aY()
    {
        return "mob.cat.hitt";
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float aV()
    {
        return 0.4F;
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getLootId()
    {
        return Item.LEATHER.id;
    }

    public boolean l(Entity var1)
    {
        return var1.damageEntity(DamageSource.mobAttack(this), 3);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean damageEntity(DamageSource var1, int var2)
    {
        this.d.a(false);
        return super.damageEntity(var1, var2);
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropDeathLoot(boolean var1, int var2) {}

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean c(EntityHuman var1)
    {
        ItemStack var2 = var1.inventory.getItemInHand();

        if (this.isTamed())
        {
            if (var1.name.equalsIgnoreCase(this.getOwnerName()) && !this.world.isStatic && !this.c(var2))
            {
                this.d.a(!this.isSitting());
            }
        }
        else if (this.e.func_75277_f() && var2 != null && var2.id == Item.RAW_FISH.id && var1.e(this) < 9.0D)
        {
            if (!var1.abilities.canInstantlyBuild)
            {
                --var2.count;
            }

            if (var2.count <= 0)
            {
                var1.inventory.setItem(var1.inventory.itemInHandIndex, (ItemStack)null);
            }

            if (!this.world.isStatic)
            {
                if (this.random.nextInt(3) == 0)
                {
                    this.setTamed(true);
                    this.setCatType(1 + this.world.random.nextInt(3));
                    this.setOwnerName(var1.name);
                    this.f(true);
                    this.d.a(true);
                    this.world.broadcastEntityEffect(this, (byte)7);
                }
                else
                {
                    this.f(false);
                    this.world.broadcastEntityEffect(this, (byte)6);
                }
            }

            return true;
        }

        return super.c(var1);
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public EntityAnimal createChild(EntityAnimal var1)
    {
        EntityOcelot var2 = new EntityOcelot(this.world);

        if (this.isTamed())
        {
            var2.setOwnerName(this.getOwnerName());
            var2.setTamed(true);
            var2.setCatType(this.getCatType());
        }

        return var2;
    }

    /**
     * Checks if the parameter is an wheat item.
     */
    public boolean c(ItemStack var1)
    {
        return var1 != null && var1.id == Item.RAW_FISH.id;
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean mate(EntityAnimal var1)
    {
        if (var1 == this)
        {
            return false;
        }
        else if (!this.isTamed())
        {
            return false;
        }
        else if (!(var1 instanceof EntityOcelot))
        {
            return false;
        }
        else
        {
            EntityOcelot var2 = (EntityOcelot)var1;
            return !var2.isTamed() ? false : this.r() && var2.r();
        }
    }

    public int getCatType()
    {
        return this.datawatcher.getByte(18);
    }

    public void setCatType(int var1)
    {
        this.datawatcher.watch(18, Byte.valueOf((byte)var1));
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean canSpawn()
    {
        if (this.world.random.nextInt(3) == 0)
        {
            return false;
        }
        else
        {
            if (this.world.b(this.boundingBox) && this.world.getCubes(this, this.boundingBox).isEmpty() && !this.world.containsLiquid(this.boundingBox))
            {
                int var1 = MathHelper.floor(this.locX);
                int var2 = MathHelper.floor(this.boundingBox.b);
                int var3 = MathHelper.floor(this.locZ);

                if (var2 < 63)
                {
                    return false;
                }

                int var4 = this.world.getTypeId(var1, var2 - 1, var3);

                if (var4 == Block.GRASS.id || var4 == Block.LEAVES.id)
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Gets the username of the entity.
     */
    public String getLocalizedName()
    {
        return this.isTamed() ? "entity.Cat.name" : super.getLocalizedName();
    }

    /**
     * Initialize this creature.
     */
    public void bD()
    {
        if (this.world.random.nextInt(7) == 0)
        {
            for (int var1 = 0; var1 < 2; ++var1)
            {
                EntityOcelot var2 = new EntityOcelot(this.world);
                var2.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
                var2.setAge(-24000);
                this.world.addEntity(var2);
            }
        }
    }
}
