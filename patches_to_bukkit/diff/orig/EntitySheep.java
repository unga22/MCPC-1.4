package net.minecraft.server;

import java.util.Random;

public class EntitySheep extends EntityAnimal
{
    /**
     * Holds the RGB table of the sheep colors - in OpenGL glColor3f values - used to render the sheep colored fleece.
     */
    public static final float[][] d = new float[][] {{1.0F, 1.0F, 1.0F}, {0.95F, 0.7F, 0.2F}, {0.9F, 0.5F, 0.85F}, {0.6F, 0.7F, 0.95F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.7F, 0.8F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.6F, 0.7F}, {0.7F, 0.4F, 0.9F}, {0.2F, 0.4F, 0.8F}, {0.5F, 0.4F, 0.3F}, {0.4F, 0.5F, 0.2F}, {0.8F, 0.3F, 0.3F}, {0.1F, 0.1F, 0.1F}};

    /**
     * Used to control movement as well as wool regrowth. Set to 40 on handleHealthUpdate and counts down with each
     * tick.
     */
    private int e;

    /** The eat grass AI task for this mob. */
    private PathfinderGoalEatTile f = new PathfinderGoalEatTile(this);

    public EntitySheep(World var1)
    {
        super(var1);
        this.texture = "/mob/sheep.png";
        this.a(0.9F, 1.3F);
        float var2 = 0.23F;
        this.getNavigation().a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 0.38F));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, var2));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 0.25F, Item.WHEAT.id, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 0.25F));
        this.goalSelector.a(5, this.f);
        this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, var2));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean bb()
    {
        return true;
    }

    protected void bi()
    {
        this.e = this.f.func_75362_f();
        super.bi();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void c()
    {
        if (this.world.isStatic)
        {
            this.e = Math.max(0, this.e - 1);
        }

        super.c();
    }

    public int getMaxHealth()
    {
        return 8;
    }

    protected void a()
    {
        super.a();
        this.datawatcher.a(16, new Byte((byte)0));
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropDeathLoot(boolean var1, int var2)
    {
        if (!this.isSheared())
        {
            this.a(new ItemStack(Block.WOOL.id, 1, this.getColor()), 0.0F);
        }
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getLootId()
    {
        return Block.WOOL.id;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean c(EntityHuman var1)
    {
        ItemStack var2 = var1.inventory.getItemInHand();

        if (var2 != null && var2.id == Item.SHEARS.id && !this.isSheared() && !this.isBaby())
        {
            if (!this.world.isStatic)
            {
                this.setSheared(true);
                int var3 = 1 + this.random.nextInt(3);

                for (int var4 = 0; var4 < var3; ++var4)
                {
                    EntityItem var5 = this.a(new ItemStack(Block.WOOL.id, 1, this.getColor()), 1.0F);
                    var5.motY += (double)(this.random.nextFloat() * 0.05F);
                    var5.motX += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    var5.motZ += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                }
            }

            var2.damage(1, var1);
            this.world.makeSound(this, "mob.sheep.shear", 1.0F, 1.0F);
        }

        return super.c(var1);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.setBoolean("Sheared", this.isSheared());
        var1.setByte("Color", (byte)this.getColor());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        this.setSheared(var1.getBoolean("Sheared"));
        this.setColor(var1.getByte("Color"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String aW()
    {
        return "mob.sheep.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String aX()
    {
        return "mob.sheep.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String aY()
    {
        return "mob.sheep.say";
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void a(int var1, int var2, int var3, int var4)
    {
        this.world.makeSound(this, "mob.sheep.step", 0.15F, 1.0F);
    }

    public int getColor()
    {
        return this.datawatcher.getByte(16) & 15;
    }

    public void setColor(int var1)
    {
        byte var2 = this.datawatcher.getByte(16);
        this.datawatcher.watch(16, Byte.valueOf((byte)(var2 & 240 | var1 & 15)));
    }

    /**
     * returns true if a sheeps wool has been sheared
     */
    public boolean isSheared()
    {
        return (this.datawatcher.getByte(16) & 16) != 0;
    }

    /**
     * make a sheep sheared if set to true
     */
    public void setSheared(boolean var1)
    {
        byte var2 = this.datawatcher.getByte(16);

        if (var1)
        {
            this.datawatcher.watch(16, Byte.valueOf((byte)(var2 | 16)));
        }
        else
        {
            this.datawatcher.watch(16, Byte.valueOf((byte)(var2 & -17)));
        }
    }

    /**
     * This method is called when a sheep spawns in the world to select the color of sheep fleece.
     */
    public static int a(Random var0)
    {
        int var1 = var0.nextInt(100);
        return var1 < 5 ? 15 : (var1 < 10 ? 7 : (var1 < 15 ? 8 : (var1 < 18 ? 12 : (var0.nextInt(500) == 0 ? 6 : 0))));
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public EntityAnimal createChild(EntityAnimal var1)
    {
        EntitySheep var2 = (EntitySheep)var1;
        EntitySheep var3 = new EntitySheep(this.world);

        if (this.random.nextBoolean())
        {
            var3.setColor(this.getColor());
        }
        else
        {
            var3.setColor(var2.getColor());
        }

        return var3;
    }

    /**
     * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This
     * function is used in the AIEatGrass)
     */
    public void aG()
    {
        this.setSheared(false);

        if (this.isBaby())
        {
            int var1 = this.getAge() + 1200;

            if (var1 > 0)
            {
                var1 = 0;
            }

            this.setAge(var1);
        }
    }

    /**
     * Initialize this creature.
     */
    public void bD()
    {
        this.setColor(a(this.world.random));
    }
}
