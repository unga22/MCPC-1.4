package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public abstract class EntityHuman extends EntityLiving implements ICommandListener
{
    /** Inventory of the player */
    public PlayerInventory inventory = new PlayerInventory(this);
    private InventoryEnderChest enderChest = new InventoryEnderChest();

    /** the crafting inventory in you get when opening your inventory */
    public Container defaultContainer;

    /** the crafting inventory you are currently using */
    public Container activeContainer;

    /** The food object of the player, the general hunger logic. */
    protected FoodMetaData foodData = new FoodMetaData();

    /**
     * Used to tell if the player pressed jump twice. If this is at 0 and it's pressed (And they are allowed to fly, as
     * defined in the player's movementInput) it sets this to 7. If it's pressed and it's greater than 0 enable fly.
     */
    protected int bO = 0;
    public byte field_71098_bD = 0;
    public int bQ = 0;
    public float bR;
    public float bS;
    public String name;

    /**
     * Used by EntityPlayer to prevent too many xp orbs from getting absorbed at once.
     */
    public int bU = 0;
    public double field_71091_bM;
    public double field_71096_bN;
    public double field_71097_bO;
    public double field_71094_bP;
    public double field_71095_bQ;
    public double field_71085_bR;

    /** Boolean value indicating weather a player is sleeping or not */
    protected boolean sleeping;

    /** the current location of the player */
    public ChunkCoordinates cc;
    private int sleepTicks;
    public float field_71079_bU;
    public float field_71089_bV;

    /** holds the spawn chunk of the player */
    private ChunkCoordinates c;

    /**
     * Whether this player's spawn point is forced, preventing execution of bed checks.
     */
    private boolean d;

    /** Holds the coordinate of the player when enter a minecraft to ride. */
    private ChunkCoordinates e;

    /** The player's capabilities. (See class PlayerCapabilities) */
    public PlayerAbilities abilities = new PlayerAbilities();

    /** The current experience level the player is on. */
    public int expLevel;

    /**
     * The total amount of experience the player has. This also includes the amount of experience within their
     * Experience Bar.
     */
    public int expTotal;

    /**
     * The current amount of experience the player has within their Experience Bar.
     */
    public float exp;

    /**
     * This is the item that is in use when the player is holding down the useItemButton (e.g., bow, food, sword)
     */
    private ItemStack f;

    /**
     * This field starts off equal to getMaxItemUseDuration and is decremented on each tick
     */
    private int g;
    protected float cj = 0.1F;
    protected float ck = 0.02F;
    private int field_82249_h = 0;

    /**
     * An instance of a fishing rod's hook. If this isn't null, the icon image of the fishing rod is slightly different
     */
    public EntityFishingHook hookedFish = null;

    public EntityHuman(World var1)
    {
        super(var1);
        this.defaultContainer = new ContainerPlayer(this.inventory, !var1.isStatic, this);
        this.activeContainer = this.defaultContainer;
        this.height = 1.62F;
        ChunkCoordinates var2 = var1.getSpawn();
        this.setPositionRotation((double)var2.x + 0.5D, (double)(var2.y + 1), (double)var2.z + 0.5D, 0.0F, 0.0F);
        this.aI = "humanoid";
        this.field_70741_aB = 180.0F;
        this.maxFireTicks = 20;
        this.texture = "/mob/char.png";
    }

    public int getMaxHealth()
    {
        return 20;
    }

    protected void a()
    {
        super.a();
        this.datawatcher.a(16, Byte.valueOf((byte)0));
        this.datawatcher.a(17, Byte.valueOf((byte)0));
    }

    /**
     * Checks if the entity is currently using an item (e.g., bow, food, sword) by holding down the useItemButton
     */
    public boolean bI()
    {
        return this.f != null;
    }

    public void bK()
    {
        if (this.f != null)
        {
            this.f.b(this.world, this, this.g);
        }

        this.bL();
    }

    public void bL()
    {
        this.f = null;
        this.g = 0;

        if (!this.world.isStatic)
        {
            this.d(false);
        }
    }

    public boolean be()
    {
        return this.bI() && Item.byId[this.f.id].d_(this.f) == EnumAnimation.block;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void j_()
    {
        if (this.f != null)
        {
            ItemStack var1 = this.inventory.getItemInHand();

            if (var1 == this.f)
            {
                if (this.g <= 25 && this.g % 4 == 0)
                {
                    this.c(var1, 5);
                }

                if (--this.g == 0 && !this.world.isStatic)
                {
                    this.n();
                }
            }
            else
            {
                this.bL();
            }
        }

        if (this.bU > 0)
        {
            --this.bU;
        }

        if (this.isSleeping())
        {
            ++this.sleepTicks;

            if (this.sleepTicks > 100)
            {
                this.sleepTicks = 100;
            }

            if (!this.world.isStatic)
            {
                if (!this.j())
                {
                    this.a(true, true, false);
                }
                else if (this.world.t())
                {
                    this.a(false, true, true);
                }
            }
        }
        else if (this.sleepTicks > 0)
        {
            ++this.sleepTicks;

            if (this.sleepTicks >= 110)
            {
                this.sleepTicks = 0;
            }
        }

        super.j_();

        if (!this.world.isStatic && this.activeContainer != null && !this.activeContainer.c(this))
        {
            this.closeInventory();
            this.activeContainer = this.defaultContainer;
        }

        if (this.isBurning() && this.abilities.isInvulnerable)
        {
            this.extinguish();
        }

        this.field_71091_bM = this.field_71094_bP;
        this.field_71096_bN = this.field_71095_bQ;
        this.field_71097_bO = this.field_71085_bR;
        double var9 = this.locX - this.field_71094_bP;
        double var3 = this.locY - this.field_71095_bQ;
        double var5 = this.locZ - this.field_71085_bR;
        double var7 = 10.0D;

        if (var9 > var7)
        {
            this.field_71091_bM = this.field_71094_bP = this.locX;
        }

        if (var5 > var7)
        {
            this.field_71097_bO = this.field_71085_bR = this.locZ;
        }

        if (var3 > var7)
        {
            this.field_71096_bN = this.field_71095_bQ = this.locY;
        }

        if (var9 < -var7)
        {
            this.field_71091_bM = this.field_71094_bP = this.locX;
        }

        if (var5 < -var7)
        {
            this.field_71097_bO = this.field_71085_bR = this.locZ;
        }

        if (var3 < -var7)
        {
            this.field_71096_bN = this.field_71095_bQ = this.locY;
        }

        this.field_71094_bP += var9 * 0.25D;
        this.field_71085_bR += var5 * 0.25D;
        this.field_71095_bQ += var3 * 0.25D;
        this.a(StatisticList.k, 1);

        if (this.vehicle == null)
        {
            this.e = null;
        }

        if (!this.world.isStatic)
        {
            this.foodData.a(this);
        }
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int z()
    {
        return this.abilities.isInvulnerable ? 0 : 80;
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int ab()
    {
        return 10;
    }

    /**
     * Plays sounds and makes particles for item in use state
     */
    protected void c(ItemStack var1, int var2)
    {
        if (var1.n() == EnumAnimation.drink)
        {
            this.world.makeSound(this, "random.drink", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }

        if (var1.n() == EnumAnimation.eat)
        {
            for (int var3 = 0; var3 < var2; ++var3)
            {
                Vec3D var4 = this.world.getVec3DPool().create(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                var4.a(-this.pitch * (float)Math.PI / 180.0F);
                var4.b(-this.yaw * (float)Math.PI / 180.0F);
                Vec3D var5 = this.world.getVec3DPool().create(((double)this.random.nextFloat() - 0.5D) * 0.3D, (double)(-this.random.nextFloat()) * 0.6D - 0.3D, 0.6D);
                var5.a(-this.pitch * (float)Math.PI / 180.0F);
                var5.b(-this.yaw * (float)Math.PI / 180.0F);
                var5 = var5.add(this.locX, this.locY + (double)this.getHeadHeight(), this.locZ);
                this.world.addParticle("iconcrack_" + var1.getItem().id, var5.c, var5.d, var5.e, var4.c, var4.d + 0.05D, var4.e);
            }

            this.world.makeSound(this, "random.eat", 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void n()
    {
        if (this.f != null)
        {
            this.c(this.f, 16);
            int var1 = this.f.count;
            ItemStack var2 = this.f.b(this.world, this);

            if (var2 != this.f || var2 != null && var2.count != var1)
            {
                this.inventory.items[this.inventory.itemInHandIndex] = var2;

                if (var2.count == 0)
                {
                    this.inventory.items[this.inventory.itemInHandIndex] = null;
                }
            }

            this.bL();
        }
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean bd()
    {
        return this.getHealth() <= 0 || this.isSleeping();
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    protected void closeInventory()
    {
        this.activeContainer = this.defaultContainer;
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void U()
    {
        double var1 = this.locX;
        double var3 = this.locY;
        double var5 = this.locZ;
        float var7 = this.yaw;
        float var8 = this.pitch;
        super.U();
        this.bR = this.bS;
        this.bS = 0.0F;
        this.k(this.locX - var1, this.locY - var3, this.locZ - var5);

        if (this.vehicle instanceof EntityPig)
        {
            this.pitch = var8;
            this.yaw = var7;
            this.aw = ((EntityPig)this.vehicle).aw;
        }
    }

    protected void bk()
    {
        this.func_82168_bl();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void c()
    {
        if (this.bO > 0)
        {
            --this.bO;
        }

        if (this.world.difficulty == 0 && this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 * 12 == 0)
        {
            this.heal(1);
        }

        this.inventory.j();
        this.bR = this.bS;
        super.c();
        this.aM = this.abilities.b();
        this.aN = this.ck;

        if (this.isSprinting())
        {
            this.aM = (float)((double)this.aM + (double)this.abilities.b() * 0.3D);
            this.aN = (float)((double)this.aN + (double)this.ck * 0.3D);
        }

        float var1 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
        float var2 = (float)Math.atan(-this.motY * 0.20000000298023224D) * 15.0F;

        if (var1 > 0.1F)
        {
            var1 = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0)
        {
            var1 = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0)
        {
            var2 = 0.0F;
        }

        this.bS += (var1 - this.bS) * 0.4F;
        this.ba += (var2 - this.ba) * 0.8F;

        if (this.getHealth() > 0)
        {
            List var3 = this.world.getEntities(this, this.boundingBox.grow(1.0D, 0.0D, 1.0D));

            if (var3 != null)
            {
                Iterator var4 = var3.iterator();

                while (var4.hasNext())
                {
                    Entity var5 = (Entity)var4.next();

                    if (!var5.dead)
                    {
                        this.q(var5);
                    }
                }
            }
        }
    }

    private void q(Entity var1)
    {
        var1.b_(this);
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void die(DamageSource var1)
    {
        super.die(var1);
        this.a(0.2F, 0.2F);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.motY = 0.10000000149011612D;

        if (this.name.equals("Notch"))
        {
            this.a(new ItemStack(Item.APPLE, 1), true);
        }

        if (!this.world.getGameRules().getBoolean("keepInventory"))
        {
            this.inventory.l();
        }

        if (var1 != null)
        {
            this.motX = (double)(-MathHelper.cos((this.aW + this.yaw) * (float)Math.PI / 180.0F) * 0.1F);
            this.motZ = (double)(-MathHelper.sin((this.aW + this.yaw) * (float)Math.PI / 180.0F) * 0.1F);
        }
        else
        {
            this.motX = this.motZ = 0.0D;
        }

        this.height = 0.1F;
        this.a(StatisticList.y, 1);
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void c(Entity var1, int var2)
    {
        this.bQ += var2;

        if (var1 instanceof EntityHuman)
        {
            this.a(StatisticList.A, 1);
        }
        else
        {
            this.a(StatisticList.z, 1);
        }
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem bN()
    {
        return this.a(this.inventory.splitStack(this.inventory.itemInHandIndex, 1), false);
    }

    /**
     * Args: itemstack - called when player drops an item stack that's not in his inventory (like items still placed in
     * a workbench while the workbench'es GUI gets closed)
     */
    public EntityItem drop(ItemStack var1)
    {
        return this.a(var1, false);
    }

    /**
     * Args: itemstack, flag
     */
    public EntityItem a(ItemStack var1, boolean var2)
    {
        if (var1 == null)
        {
            return null;
        }
        else
        {
            EntityItem var3 = new EntityItem(this.world, this.locX, this.locY - 0.30000001192092896D + (double)this.getHeadHeight(), this.locZ, var1);
            var3.pickupDelay = 40;
            float var4 = 0.1F;
            float var5;

            if (var2)
            {
                var5 = this.random.nextFloat() * 0.5F;
                float var6 = this.random.nextFloat() * (float)Math.PI * 2.0F;
                var3.motX = (double)(-MathHelper.sin(var6) * var5);
                var3.motZ = (double)(MathHelper.cos(var6) * var5);
                var3.motY = 0.20000000298023224D;
            }
            else
            {
                var4 = 0.3F;
                var3.motX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float)Math.PI) * var4);
                var3.motZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float)Math.PI) * var4);
                var3.motY = (double)(-MathHelper.sin(this.pitch / 180.0F * (float)Math.PI) * var4 + 0.1F);
                var4 = 0.02F;
                var5 = this.random.nextFloat() * (float)Math.PI * 2.0F;
                var4 *= this.random.nextFloat();
                var3.motX += Math.cos((double)var5) * (double)var4;
                var3.motY += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                var3.motZ += Math.sin((double)var5) * (double)var4;
            }

            this.a(var3);
            this.a(StatisticList.v, 1);
            return var3;
        }
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    protected void a(EntityItem var1)
    {
        this.world.addEntity(var1);
    }

    /**
     * Returns how strong the player is against the specified block at this moment
     */
    public float a(Block var1)
    {
        float var2 = this.inventory.a(var1);
        int var3 = EnchantmentManager.getDigSpeedEnchantmentLevel(this);

        if (var3 > 0 && this.inventory.b(var1))
        {
            var2 += (float)(var3 * var3 + 1);
        }

        if (this.hasEffect(MobEffectList.FASTER_DIG))
        {
            var2 *= 1.0F + (float)(this.getEffect(MobEffectList.FASTER_DIG).getAmplifier() + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffectList.SLOWER_DIG))
        {
            var2 *= 1.0F - (float)(this.getEffect(MobEffectList.SLOWER_DIG).getAmplifier() + 1) * 0.2F;
        }

        if (this.a(Material.WATER) && !EnchantmentManager.hasWaterWorkerEnchantment(this))
        {
            var2 /= 5.0F;
        }

        if (!this.onGround)
        {
            var2 /= 5.0F;
        }

        return var2;
    }

    /**
     * Checks if the player has the ability to harvest a block (checks current inventory item for a tool if necessary)
     */
    public boolean b(Block var1)
    {
        return this.inventory.b(var1);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        NBTTagList var2 = var1.getList("Inventory");
        this.inventory.b(var2);
        this.sleeping = var1.getBoolean("Sleeping");
        this.sleepTicks = var1.getShort("SleepTimer");
        this.exp = var1.getFloat("XpP");
        this.expLevel = var1.getInt("XpLevel");
        this.expTotal = var1.getInt("XpTotal");

        if (this.sleeping)
        {
            this.cc = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
            this.a(true, true, false);
        }

        if (var1.hasKey("SpawnX") && var1.hasKey("SpawnY") && var1.hasKey("SpawnZ"))
        {
            this.c = new ChunkCoordinates(var1.getInt("SpawnX"), var1.getInt("SpawnY"), var1.getInt("SpawnZ"));
            this.d = var1.getBoolean("SpawnForced");
        }

        this.foodData.a(var1);
        this.abilities.b(var1);

        if (var1.hasKey("EnderItems"))
        {
            NBTTagList var3 = var1.getList("EnderItems");
            this.enderChest.a(var3);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.set("Inventory", this.inventory.a(new NBTTagList()));
        var1.setBoolean("Sleeping", this.sleeping);
        var1.setShort("SleepTimer", (short)this.sleepTicks);
        var1.setFloat("XpP", this.exp);
        var1.setInt("XpLevel", this.expLevel);
        var1.setInt("XpTotal", this.expTotal);

        if (this.c != null)
        {
            var1.setInt("SpawnX", this.c.x);
            var1.setInt("SpawnY", this.c.y);
            var1.setInt("SpawnZ", this.c.z);
            var1.setBoolean("SpawnForced", this.d);
        }

        this.foodData.b(var1);
        this.abilities.a(var1);
        var1.set("EnderItems", this.enderChest.g());
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void openContainer(IInventory var1) {}

    public void startEnchanting(int var1, int var2, int var3) {}

    /**
     * Displays the GUI for interacting with an anvil.
     */
    public void openAnvil(int var1, int var2, int var3) {}

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void startCrafting(int var1, int var2, int var3) {}

    public float getHeadHeight()
    {
        return 0.12F;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void e_()
    {
        this.height = 1.62F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean damageEntity(DamageSource var1, int var2)
    {
        if (this.abilities.isInvulnerable && !var1.ignoresInvulnerability())
        {
            return false;
        }
        else
        {
            this.bC = 0;

            if (this.getHealth() <= 0)
            {
                return false;
            }
            else
            {
                if (this.isSleeping() && !this.world.isStatic)
                {
                    this.a(true, true, false);
                }

                if (var1.func_76350_n())
                {
                    if (this.world.difficulty == 0)
                    {
                        var2 = 0;
                    }

                    if (this.world.difficulty == 1)
                    {
                        var2 = var2 / 2 + 1;
                    }

                    if (this.world.difficulty == 3)
                    {
                        var2 = var2 * 3 / 2;
                    }
                }

                if (var2 == 0)
                {
                    return false;
                }
                else
                {
                    Entity var3 = var1.getEntity();

                    if (var3 instanceof EntityArrow && ((EntityArrow)var3).shooter != null)
                    {
                        var3 = ((EntityArrow)var3).shooter;
                    }

                    if (var3 instanceof EntityLiving)
                    {
                        this.a((EntityLiving)var3, false);
                    }

                    this.a(StatisticList.x, var2);
                    return super.damageEntity(var1, var2);
                }
            }
        }
    }

    /**
     * Reduces damage, depending on potions
     */
    protected int c(DamageSource var1, int var2)
    {
        int var3 = super.c(var1, var2);

        if (var3 <= 0)
        {
            return 0;
        }
        else
        {
            int var4 = EnchantmentManager.a(this.inventory.armor, var1);

            if (var4 > 20)
            {
                var4 = 20;
            }

            if (var4 > 0 && var4 <= 20)
            {
                int var5 = 25 - var4;
                int var6 = var3 * var5 + this.aS;
                var3 = var6 / 25;
                this.aS = var6 % 25;
            }

            return var3;
        }
    }

    /**
     * returns if pvp is enabled or not
     */
    protected boolean h()
    {
        return false;
    }

    /**
     * Called when the player attack or gets attacked, it's alert all wolves in the area that are owned by the player to
     * join the attack or defend the player.
     */
    protected void a(EntityLiving var1, boolean var2)
    {
        if (!(var1 instanceof EntityCreeper) && !(var1 instanceof EntityGhast))
        {
            if (var1 instanceof EntityWolf)
            {
                EntityWolf var3 = (EntityWolf)var1;

                if (var3.isTamed() && this.name.equals(var3.getOwnerName()))
                {
                    return;
                }
            }

            if (!(var1 instanceof EntityHuman) || this.h())
            {
                List var6 = this.world.a(EntityWolf.class, AxisAlignedBB.a().a(this.locX, this.locY, this.locZ, this.locX + 1.0D, this.locY + 1.0D, this.locZ + 1.0D).grow(16.0D, 4.0D, 16.0D));
                Iterator var4 = var6.iterator();

                while (var4.hasNext())
                {
                    EntityWolf var5 = (EntityWolf)var4.next();

                    if (var5.isTamed() && var5.l() == null && this.name.equals(var5.getOwnerName()) && (!var2 || !var5.isSitting()))
                    {
                        var5.setSitting(false);
                        var5.setTarget(var1);
                    }
                }
            }
        }
    }

    protected void k(int var1)
    {
        this.inventory.g(var1);
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int aU()
    {
        return this.inventory.k();
    }

    public float func_82243_bO()
    {
        int var1 = 0;
        ItemStack[] var2 = this.inventory.armor;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];

            if (var5 != null)
            {
                ++var1;
            }
        }

        return (float)var1 / (float)this.inventory.armor.length;
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    protected void d(DamageSource var1, int var2)
    {
        if (!this.field_83001_bt)
        {
            if (!var1.ignoresArmor() && this.be())
            {
                var2 = 1 + var2 >> 1;
            }

            var2 = this.b(var1, var2);
            var2 = this.c(var1, var2);
            this.j(var1.d());
            this.health -= var2;
        }
    }

    /**
     * Displays the furnace GUI for the passed in furnace entity. Args: tileEntityFurnace
     */
    public void openFurnace(TileEntityFurnace var1) {}

    /**
     * Displays the dipsenser GUI for the passed in dispenser entity. Args: TileEntityDispenser
     */
    public void openDispenser(TileEntityDispenser var1) {}

    /**
     * Displays the GUI for editing a sign. Args: tileEntitySign
     */
    public void a(TileEntity var1) {}

    /**
     * Displays the GUI for interacting with a brewing stand.
     */
    public void openBrewingStand(TileEntityBrewingStand var1) {}

    /**
     * Displays the GUI for interacting with a beacon.
     */
    public void openBeacon(TileEntityBeacon var1) {}

    public void openTrade(IMerchant var1) {}

    /**
     * Displays the GUI for interacting with a book.
     */
    public void d(ItemStack var1) {}

    public boolean o(Entity var1)
    {
        if (var1.c(this))
        {
            return true;
        }
        else
        {
            ItemStack var2 = this.bP();

            if (var2 != null && var1 instanceof EntityLiving)
            {
                if (this.abilities.canInstantlyBuild)
                {
                    var2 = var2.cloneItemStack();
                }

                if (var2.a((EntityLiving)var1))
                {
                    if (var2.count <= 0 && !this.abilities.canInstantlyBuild)
                    {
                        this.bQ();
                    }

                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Returns the currently being used item by the player.
     */
    public ItemStack bP()
    {
        return this.inventory.getItemInHand();
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void bQ()
    {
        this.inventory.setItem(this.inventory.itemInHandIndex, (ItemStack)null);
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double W()
    {
        return (double)(this.height - 0.5F);
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.  The equipped item has hitEntity
     * called on it. Args: targetEntity
     */
    public void attack(Entity var1)
    {
        if (var1.aq())
        {
            int var2 = this.inventory.a(var1);

            if (this.hasEffect(MobEffectList.INCREASE_DAMAGE))
            {
                var2 += 3 << this.getEffect(MobEffectList.INCREASE_DAMAGE).getAmplifier();
            }

            if (this.hasEffect(MobEffectList.WEAKNESS))
            {
                var2 -= 2 << this.getEffect(MobEffectList.WEAKNESS).getAmplifier();
            }

            int var3 = 0;
            int var4 = 0;

            if (var1 instanceof EntityLiving)
            {
                var4 = EnchantmentManager.a(this, (EntityLiving)var1);
                var3 += EnchantmentManager.getKnockbackEnchantmentLevel(this, (EntityLiving)var1);
            }

            if (this.isSprinting())
            {
                ++var3;
            }

            if (var2 > 0 || var4 > 0)
            {
                boolean var5 = this.fallDistance > 0.0F && !this.onGround && !this.g_() && !this.H() && !this.hasEffect(MobEffectList.BLINDNESS) && this.vehicle == null && var1 instanceof EntityLiving;

                if (var5)
                {
                    var2 += this.random.nextInt(var2 / 2 + 2);
                }

                var2 += var4;
                boolean var6 = var1.damageEntity(DamageSource.playerAttack(this), var2);

                if (var6)
                {
                    if (var3 > 0)
                    {
                        var1.g((double)(-MathHelper.sin(this.yaw * (float)Math.PI / 180.0F) * (float)var3 * 0.5F), 0.1D, (double)(MathHelper.cos(this.yaw * (float)Math.PI / 180.0F) * (float)var3 * 0.5F));
                        this.motX *= 0.6D;
                        this.motZ *= 0.6D;
                        this.setSprinting(false);
                    }

                    if (var5)
                    {
                        this.b(var1);
                    }

                    if (var4 > 0)
                    {
                        this.c(var1);
                    }

                    if (var2 >= 18)
                    {
                        this.a(AchievementList.E);
                    }

                    this.k(var1);
                }

                ItemStack var7 = this.bP();

                if (var7 != null && var1 instanceof EntityLiving)
                {
                    var7.a((EntityLiving)var1, this);

                    if (var7.count <= 0)
                    {
                        this.bQ();
                    }
                }

                if (var1 instanceof EntityLiving)
                {
                    if (var1.isAlive())
                    {
                        this.a((EntityLiving)var1, true);
                    }

                    this.a(StatisticList.w, var2);
                    int var8 = EnchantmentManager.getFireAspectEnchantmentLevel(this, (EntityLiving)var1);

                    if (var8 > 0 && var6)
                    {
                        var1.setOnFire(var8 * 4);
                    }
                }

                this.j(0.3F);
            }
        }
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void b(Entity var1) {}

    public void c(Entity var1) {}

    /**
     * Will get destroyed next tick.
     */
    public void die()
    {
        super.die();
        this.defaultContainer.a(this);

        if (this.activeContainer != null)
        {
            this.activeContainer.a(this);
        }
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean inBlock()
    {
        return !this.sleeping && super.inBlock();
    }

    public boolean func_71066_bF()
    {
        return false;
    }

    /**
     * puts player to sleep on specified bed if possible
     */
    public EnumBedResult a(int var1, int var2, int var3)
    {
        if (!this.world.isStatic)
        {
            if (this.isSleeping() || !this.isAlive())
            {
                return EnumBedResult.OTHER_PROBLEM;
            }

            if (!this.world.worldProvider.d())
            {
                return EnumBedResult.NOT_POSSIBLE_HERE;
            }

            if (this.world.t())
            {
                return EnumBedResult.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(this.locX - (double)var1) > 3.0D || Math.abs(this.locY - (double)var2) > 2.0D || Math.abs(this.locZ - (double)var3) > 3.0D)
            {
                return EnumBedResult.TOO_FAR_AWAY;
            }

            double var4 = 8.0D;
            double var6 = 5.0D;
            List var8 = this.world.a(EntityMonster.class, AxisAlignedBB.a().a((double)var1 - var4, (double)var2 - var6, (double)var3 - var4, (double)var1 + var4, (double)var2 + var6, (double)var3 + var4));

            if (!var8.isEmpty())
            {
                return EnumBedResult.NOT_SAFE;
            }
        }

        this.a(0.2F, 0.2F);
        this.height = 0.2F;

        if (this.world.isLoaded(var1, var2, var3))
        {
            int var9 = this.world.getData(var1, var2, var3);
            int var5 = BlockBed.e(var9);
            float var10 = 0.5F;
            float var7 = 0.5F;

            switch (var5)
            {
                case 0:
                    var7 = 0.9F;
                    break;

                case 1:
                    var10 = 0.1F;
                    break;

                case 2:
                    var7 = 0.1F;
                    break;

                case 3:
                    var10 = 0.9F;
            }

            this.func_71013_b(var5);
            this.setPosition((double)((float)var1 + var10), (double)((float)var2 + 0.9375F), (double)((float)var3 + var7));
        }
        else
        {
            this.setPosition((double)((float)var1 + 0.5F), (double)((float)var2 + 0.9375F), (double)((float)var3 + 0.5F));
        }

        this.sleeping = true;
        this.sleepTicks = 0;
        this.cc = new ChunkCoordinates(var1, var2, var3);
        this.motX = this.motZ = this.motY = 0.0D;

        if (!this.world.isStatic)
        {
            this.world.everyoneSleeping();
        }

        return EnumBedResult.OK;
    }

    private void func_71013_b(int var1)
    {
        this.field_71079_bU = 0.0F;
        this.field_71089_bV = 0.0F;

        switch (var1)
        {
            case 0:
                this.field_71089_bV = -1.8F;
                break;

            case 1:
                this.field_71079_bU = 1.8F;
                break;

            case 2:
                this.field_71089_bV = 1.8F;
                break;

            case 3:
                this.field_71079_bU = -1.8F;
        }
    }

    /**
     * Wake up the player if they're sleeping.
     */
    public void a(boolean var1, boolean var2, boolean var3)
    {
        this.a(0.6F, 1.8F);
        this.e_();
        ChunkCoordinates var4 = this.cc;
        ChunkCoordinates var5 = this.cc;

        if (var4 != null && this.world.getTypeId(var4.x, var4.y, var4.z) == Block.BED.id)
        {
            BlockBed.a(this.world, var4.x, var4.y, var4.z, false);
            var5 = BlockBed.b(this.world, var4.x, var4.y, var4.z, 0);

            if (var5 == null)
            {
                var5 = new ChunkCoordinates(var4.x, var4.y + 1, var4.z);
            }

            this.setPosition((double)((float)var5.x + 0.5F), (double)((float)var5.y + this.height + 0.1F), (double)((float)var5.z + 0.5F));
        }

        this.sleeping = false;

        if (!this.world.isStatic && var2)
        {
            this.world.everyoneSleeping();
        }

        if (var1)
        {
            this.sleepTicks = 0;
        }
        else
        {
            this.sleepTicks = 100;
        }

        if (var3)
        {
            this.setRespawnPosition(this.cc, false);
        }
    }

    /**
     * Checks if the player is currently in a bed
     */
    private boolean j()
    {
        return this.world.getTypeId(this.cc.x, this.cc.y, this.cc.z) == Block.BED.id;
    }

    /**
     * Ensure that a block enabling respawning exists at the specified coordinates and find an empty space nearby to
     * spawn.
     */
    public static ChunkCoordinates getBed(World var0, ChunkCoordinates var1, boolean var2)
    {
        IChunkProvider var3 = var0.H();
        var3.getChunkAt(var1.x - 3 >> 4, var1.z - 3 >> 4);
        var3.getChunkAt(var1.x + 3 >> 4, var1.z - 3 >> 4);
        var3.getChunkAt(var1.x - 3 >> 4, var1.z + 3 >> 4);
        var3.getChunkAt(var1.x + 3 >> 4, var1.z + 3 >> 4);

        if (var0.getTypeId(var1.x, var1.y, var1.z) != Block.BED.id)
        {
            return var2 && var0.isEmpty(var1.x, var1.y, var1.z) && var0.isEmpty(var1.x, var1.y + 1, var1.z) ? var1 : null;
        }
        else
        {
            ChunkCoordinates var4 = BlockBed.b(var0, var1.x, var1.y, var1.z, 0);
            return var4;
        }
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isSleeping()
    {
        return this.sleeping;
    }

    /**
     * Returns whether or not the player is asleep and the screen has fully faded.
     */
    public boolean isDeeplySleeping()
    {
        return this.sleeping && this.sleepTicks >= 100;
    }

    protected void func_82239_b(int var1, boolean var2)
    {
        byte var3 = this.datawatcher.getByte(16);

        if (var2)
        {
            this.datawatcher.watch(16, Byte.valueOf((byte)(var3 | 1 << var1)));
        }
        else
        {
            this.datawatcher.watch(16, Byte.valueOf((byte)(var3 & ~(1 << var1))));
        }
    }

    /**
     * Add a chat message to the player
     */
    public void b(String var1) {}

    /**
     * Returns the coordinates to respawn the player based on last bed that the player sleep.
     */
    public ChunkCoordinates getBed()
    {
        return this.c;
    }

    public boolean func_82245_bX()
    {
        return this.d;
    }

    /**
     * Defines a spawn coordinate to player spawn. Used by bed after the player sleep on it.
     */
    public void setRespawnPosition(ChunkCoordinates var1, boolean var2)
    {
        if (var1 != null)
        {
            this.c = new ChunkCoordinates(var1);
            this.d = var2;
        }
        else
        {
            this.c = null;
            this.d = false;
        }
    }

    /**
     * Will trigger the specified trigger.
     */
    public void a(Statistic var1)
    {
        this.a(var1, 1);
    }

    /**
     * Adds a value to a statistic field.
     */
    public void a(Statistic var1, int var2) {}

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void bf()
    {
        super.bf();
        this.a(StatisticList.u, 1);

        if (this.isSprinting())
        {
            this.j(0.8F);
        }
        else
        {
            this.j(0.2F);
        }
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void e(float var1, float var2)
    {
        double var3 = this.locX;
        double var5 = this.locY;
        double var7 = this.locZ;

        if (this.abilities.isFlying && this.vehicle == null)
        {
            double var9 = this.motY;
            float var11 = this.aN;
            this.aN = this.abilities.a();
            super.e(var1, var2);
            this.motY = var9 * 0.6D;
            this.aN = var11;
        }
        else
        {
            super.e(var1, var2);
        }

        this.checkMovement(this.locX - var3, this.locY - var5, this.locZ - var7);
    }

    /**
     * Adds a value to a movement statistic field - like run, walk, swin or climb.
     */
    public void checkMovement(double var1, double var3, double var5)
    {
        if (this.vehicle == null)
        {
            int var7;

            if (this.a(Material.WATER))
            {
                var7 = Math.round(MathHelper.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);

                if (var7 > 0)
                {
                    this.a(StatisticList.q, var7);
                    this.j(0.015F * (float)var7 * 0.01F);
                }
            }
            else if (this.H())
            {
                var7 = Math.round(MathHelper.sqrt(var1 * var1 + var5 * var5) * 100.0F);

                if (var7 > 0)
                {
                    this.a(StatisticList.m, var7);
                    this.j(0.015F * (float)var7 * 0.01F);
                }
            }
            else if (this.g_())
            {
                if (var3 > 0.0D)
                {
                    this.a(StatisticList.o, (int)Math.round(var3 * 100.0D));
                }
            }
            else if (this.onGround)
            {
                var7 = Math.round(MathHelper.sqrt(var1 * var1 + var5 * var5) * 100.0F);

                if (var7 > 0)
                {
                    this.a(StatisticList.l, var7);

                    if (this.isSprinting())
                    {
                        this.j(0.099999994F * (float)var7 * 0.01F);
                    }
                    else
                    {
                        this.j(0.01F * (float)var7 * 0.01F);
                    }
                }
            }
            else
            {
                var7 = Math.round(MathHelper.sqrt(var1 * var1 + var5 * var5) * 100.0F);

                if (var7 > 25)
                {
                    this.a(StatisticList.p, var7);
                }
            }
        }
    }

    /**
     * Adds a value to a mounted movement statistic field - by minecart, boat, or pig.
     */
    private void k(double var1, double var3, double var5)
    {
        if (this.vehicle != null)
        {
            int var7 = Math.round(MathHelper.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);

            if (var7 > 0)
            {
                if (this.vehicle instanceof EntityMinecart)
                {
                    this.a(StatisticList.r, var7);

                    if (this.e == null)
                    {
                        this.e = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
                    }
                    else if ((double)this.e.e(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) >= 1000000.0D)
                    {
                        this.a(AchievementList.q, 1);
                    }
                }
                else if (this.vehicle instanceof EntityBoat)
                {
                    this.a(StatisticList.s, var7);
                }
                else if (this.vehicle instanceof EntityPig)
                {
                    this.a(StatisticList.t, var7);
                }
            }
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void a(float var1)
    {
        if (!this.abilities.canFly)
        {
            if (var1 >= 2.0F)
            {
                this.a(StatisticList.n, (int)Math.round((double)var1 * 100.0D));
            }

            super.a(var1);
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void a(EntityLiving var1)
    {
        if (var1 instanceof IMonster)
        {
            this.a(AchievementList.s);
        }
    }

    public ItemStack q(int var1)
    {
        return this.inventory.func_70440_f(var1);
    }

    protected void func_82164_bB() {}

    protected void func_82162_bC() {}

    /**
     * Add experience points to player.
     */
    public void giveExp(int var1)
    {
        this.bQ += var1;
        int var2 = Integer.MAX_VALUE - this.expTotal;

        if (var1 > var2)
        {
            var1 = var2;
        }

        this.exp += (float)var1 / (float)this.getExpToLevel();

        for (this.expTotal += var1; this.exp >= 1.0F; this.exp /= (float)this.getExpToLevel())
        {
            this.exp = (this.exp - 1.0F) * (float)this.getExpToLevel();
            this.levelDown(1);
        }
    }

    /**
     * Add experience levels to this player.
     */
    public void levelDown(int var1)
    {
        this.expLevel += var1;

        if (this.expLevel < 0)
        {
            this.expLevel = 0;
        }

        if (var1 > 0 && this.expLevel % 5 == 0 && (float)this.field_82249_h < (float)this.ticksLived - 100.0F)
        {
            float var2 = this.expLevel > 30 ? 1.0F : (float)this.expLevel / 30.0F;
            this.world.makeSound(this, "random.levelup", var2 * 0.75F, 1.0F);
            this.field_82249_h = this.ticksLived;
        }
    }

    /**
     * This method returns the cap amount of experience that the experience bar can hold. With each level, the
     * experience cap on the player's experience bar is raised by 10.
     */
    public int getExpToLevel()
    {
        return this.expLevel >= 30 ? 62 + (this.expLevel - 30) * 7 : (this.expLevel >= 15 ? 17 + (this.expLevel - 15) * 3 : 17);
    }

    /**
     * increases exhaustion level by supplied amount
     */
    public void j(float var1)
    {
        if (!this.abilities.isInvulnerable)
        {
            if (!this.world.isStatic)
            {
                this.foodData.a(var1);
            }
        }
    }

    /**
     * Returns the player's FoodStats object.
     */
    public FoodMetaData getFoodData()
    {
        return this.foodData;
    }

    public boolean f(boolean var1)
    {
        return (var1 || this.foodData.c()) && !this.abilities.isInvulnerable;
    }

    /**
     * Checks if the player's health is not full and not zero.
     */
    public boolean ca()
    {
        return this.getHealth() > 0 && this.getHealth() < this.getMaxHealth();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int maxItemUseDuration
     */
    public void a(ItemStack var1, int var2)
    {
        if (var1 != this.f)
        {
            this.f = var1;
            this.g = var2;

            if (!this.world.isStatic)
            {
                this.d(true);
            }
        }
    }

    public boolean func_82246_f(int var1, int var2, int var3)
    {
        if (this.abilities.mayBuild)
        {
            return true;
        }
        else
        {
            int var4 = this.world.getTypeId(var1, var2, var3);

            if (var4 > 0 && this.bP() != null)
            {
                Block var5 = Block.byId[var4];
                ItemStack var6 = this.bP();

                if (var6.b(var5) || var6.a(var5) > 1.0F)
                {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean func_82247_a(int var1, int var2, int var3, int var4, ItemStack var5)
    {
        return this.abilities.mayBuild ? true : (var5 != null ? var5.func_82835_x() : false);
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExpValue(EntityHuman var1)
    {
        if (this.world.getGameRules().getBoolean("keepInventory"))
        {
            return 0;
        }
        else
        {
            int var2 = this.expLevel * 7;
            return var2 > 100 ? 100 : var2;
        }
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean alwaysGivesExp()
    {
        return true;
    }

    /**
     * Gets the username of the entity.
     */
    public String getLocalizedName()
    {
        return this.name;
    }

    /**
     * Copies the values from the given player into this player if boolean par2 is true. Always clones Ender Chest
     * Inventory.
     */
    public void copyTo(EntityHuman var1, boolean var2)
    {
        if (var2)
        {
            this.inventory.b(var1.inventory);
            this.health = var1.health;
            this.foodData = var1.foodData;
            this.expLevel = var1.expLevel;
            this.expTotal = var1.expTotal;
            this.exp = var1.exp;
            this.bQ = var1.bQ;
            this.field_82152_aq = var1.field_82152_aq;
        }
        else if (this.world.getGameRules().getBoolean("keepInventory"))
        {
            this.inventory.b(var1.inventory);
            this.expLevel = var1.expLevel;
            this.expTotal = var1.expTotal;
            this.exp = var1.exp;
            this.bQ = var1.bQ;
        }

        this.enderChest = var1.enderChest;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean f_()
    {
        return !this.abilities.isFlying;
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void updateAbilities() {}

    /**
     * Sets the player's game type
     */
    public void a(EnumGamemode var1) {}

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getName()
    {
        return this.name;
    }

    public LocaleLanguage getLocale()
    {
        return LocaleLanguage.a();
    }

    /**
     * Translates and formats the given string key with the given arguments.
     */
    public String a(String var1, Object ... var2)
    {
        return this.getLocale().a(var1, var2);
    }

    /**
     * Returns the InventoryEnderChest of this player.
     */
    public InventoryEnderChest getEnderChest()
    {
        return this.enderChest;
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public ItemStack getEquipment(int var1)
    {
        return var1 == 0 ? this.inventory.getItemInHand() : this.inventory.armor[var1 - 1];
    }

    public ItemStack func_70694_bm()
    {
        return this.inventory.getItemInHand();
    }

    public void func_70062_b(int var1, ItemStack var2)
    {
        this.inventory.armor[var1] = var2;
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it seems)
     */
    public ItemStack[] getEquipment()
    {
        return this.inventory.armor;
    }
}
