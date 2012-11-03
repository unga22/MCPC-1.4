package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor$ArmorProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
//CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
//CraftBukkit end
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;

public abstract class EntityHuman extends EntityLiving implements ICommandListener {

    public PlayerInventory inventory = new PlayerInventory(this);
    private InventoryEnderChest enderChest = new InventoryEnderChest();
    public Container defaultContainer;
    public Container activeContainer;
    protected FoodMetaData foodData = new FoodMetaData();
    protected int bO = 0;
    public byte bP = 0;
    public int bQ = 0;
    public float bR;
    public float bS;
    public String name;
    public int bU = 0;
    public double bV;
    public double bW;
    public double bX;
    public double bY;
    public double bZ;
    public double ca;
    // CraftBukkit start
    public boolean sleeping; // protected -> public
    public boolean fauxSleeping;
    public String spawnWorld = "";

    public HumanEntity getBukkitEntity() {
        return (HumanEntity) super.getBukkitEntity();
    }
    // CraftBukkit end

    public ChunkCoordinates cc;
    public int sleepTicks; // CraftBukkit - private -> public
    public float cd;
    public float ce;
    private ChunkCoordinates c;
    private boolean d;
    private ChunkCoordinates e;
    public PlayerAbilities abilities = new PlayerAbilities();
    public int oldLevel = -1; // CraftBukkit
    public int expLevel;
    public int expTotal;
    public float exp;
    private ItemStack f;
    private int g;
    protected float cj = 0.1F;
    protected float ck = 0.02F;
    private int h = 0;
    public EntityFishingHook hookedFish = null;

    public EntityHuman(World world) {
        super(world);
        this.defaultContainer = new ContainerPlayer(this.inventory, !world.isStatic, this);
        this.activeContainer = this.defaultContainer;
        this.height = 1.62F;
        ChunkCoordinates chunkcoordinates = world.getSpawn();

        this.setPositionRotation((double) chunkcoordinates.x + 0.5D, (double) (chunkcoordinates.y + 1), (double) chunkcoordinates.z + 0.5D, 0.0F, 0.0F);
        this.aI = "humanoid";
        this.aH = 180.0F;
        this.maxFireTicks = 20;
        this.texture = "/mob/char.png";
    }

    public int getMaxHealth() {
        return 20;
    }

    protected void a() {
        super.a();
        this.datawatcher.a(16, Byte.valueOf((byte) 0));
        this.datawatcher.a(17, Byte.valueOf((byte) 0));
    }

    public boolean bI() {
        return this.f != null;
    }

    public void bK() {
        if (this.f != null) {
            this.f.b(this.world, this, this.g);
        }

        this.bL();
    }

    public void bL() {
        this.f = null;
        this.g = 0;
        if (!this.world.isStatic) {
            this.d(false);
        }
    }

    public boolean be() {
        return this.bI() && Item.byId[this.f.id].d_(this.f) == EnumAnimation.d;
    }

    public void j_() {
    	FMLCommonHandler.instance().onPlayerPreTick(this);
    	
        if (this.f != null) {
            ItemStack itemstack = this.inventory.getItemInHand();

            if (itemstack == this.f) {
            	 this.f.getItem().onUsingItemTick(this.f, this, this.g);
            	 
                if (this.g <= 25 && this.g % 4 == 0) {
                    this.c(itemstack, 5);
                }

                if (--this.g == 0 && !this.world.isStatic) {
                    this.n();
                }
            } else {
                this.bL();
            }
        }

        if (this.bU > 0) {
            --this.bU;
        }

        if (this.isSleeping()) {
            ++this.sleepTicks;
            if (this.sleepTicks > 100) {
                this.sleepTicks = 100;
            }

            if (!this.world.isStatic) {
                if (!this.j()) {
                    this.a(true, true, false);
                } else if (this.world.t()) {
                    this.a(false, true, true);
                }
            }
        } else if (this.sleepTicks > 0) {
            ++this.sleepTicks;
            if (this.sleepTicks >= 110) {
                this.sleepTicks = 0;
            }
        }

        super.j_();
        if (!this.world.isStatic && this.activeContainer != null && !this.activeContainer.c(this)) {
            this.closeInventory();
            this.activeContainer = this.defaultContainer;
        }

        if (this.isBurning() && this.abilities.isInvulnerable) {
            this.extinguish();
        }

        this.bV = this.bY;
        this.bW = this.bZ;
        this.bX = this.ca;
        double d0 = this.locX - this.bY;
        double d1 = this.locY - this.bZ;
        double d2 = this.locZ - this.ca;
        double d3 = 10.0D;

        if (d0 > d3) {
            this.bV = this.bY = this.locX;
        }

        if (d2 > d3) {
            this.bX = this.ca = this.locZ;
        }

        if (d1 > d3) {
            this.bW = this.bZ = this.locY;
        }

        if (d0 < -d3) {
            this.bV = this.bY = this.locX;
        }

        if (d2 < -d3) {
            this.bX = this.ca = this.locZ;
        }

        if (d1 < -d3) {
            this.bW = this.bZ = this.locY;
        }

        this.bY += d0 * 0.25D;
        this.ca += d2 * 0.25D;
        this.bZ += d1 * 0.25D;
        this.a(StatisticList.k, 1);
        if (this.vehicle == null) {
            this.e = null;
        }

        if (!this.world.isStatic) {
            this.foodData.a(this);
        }
        
        FMLCommonHandler.instance().onPlayerPostTick(this);
    }

    public int z() {
        return this.abilities.isInvulnerable ? 0 : 80;
    }

    public int ab() {
        return 10;
    }

    protected void c(ItemStack itemstack, int i) {
        if (itemstack.n() == EnumAnimation.c) {
            this.world.makeSound(this, "random.drink", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }

        if (itemstack.n() == EnumAnimation.b) {
            for (int j = 0; j < i; ++j) {
                Vec3D vec3d = this.world.getVec3DPool().create(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

                vec3d.a(-this.pitch * 3.1415927F / 180.0F);
                vec3d.b(-this.yaw * 3.1415927F / 180.0F);
                Vec3D vec3d1 = this.world.getVec3DPool().create(((double) this.random.nextFloat() - 0.5D) * 0.3D, (double) (-this.random.nextFloat()) * 0.6D - 0.3D, 0.6D);

                vec3d1.a(-this.pitch * 3.1415927F / 180.0F);
                vec3d1.b(-this.yaw * 3.1415927F / 180.0F);
                vec3d1 = vec3d1.add(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
                this.world.addParticle("iconcrack_" + itemstack.getItem().id, vec3d1.c, vec3d1.d, vec3d1.e, vec3d.c, vec3d.d + 0.05D, vec3d.e);
            }

            this.world.makeSound(this, "random.eat", 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    protected void n() {
        if (this.f != null) {
            this.c(this.f, 16);
            int i = this.f.count;
            ItemStack itemstack = this.f.b(this.world, this);

            if (itemstack != this.f || itemstack != null && itemstack.count != i) {
                this.inventory.items[this.inventory.itemInHandIndex] = itemstack;
                if (itemstack.count == 0) {
                    this.inventory.items[this.inventory.itemInHandIndex] = null;
                }
            }

            this.bL();
        }
    }

    protected boolean bd() {
        return this.getHealth() <= 0 || this.isSleeping();
    }

    // CraftBukkit - protected -> public
    public void closeInventory() {
        this.activeContainer = this.defaultContainer;
    }

    public void U() {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;
        float f = this.yaw;
        float f1 = this.pitch;

        super.U();
        this.bR = this.bS;
        this.bS = 0.0F;
        this.k(this.locX - d0, this.locY - d1, this.locZ - d2);
        if (this.vehicle instanceof EntityPig) {
            this.pitch = f1;
            this.yaw = f;
            this.aw = ((EntityPig) this.vehicle).aw;
        }
    }

    protected void bk() {
        this.bl();
    }

    public void c() {
        if (this.bO > 0) {
            --this.bO;
        }

        if (this.world.difficulty == 0 && this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 * 12 == 0) {
            // CraftBukkit - added regain reason of "REGEN" for filtering purposes.
            this.heal(1, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.REGEN);
        }

        this.inventory.j();
        this.bR = this.bS;
        super.c();
        this.aM = this.abilities.b();
        this.aN = this.ck;
        if (this.isSprinting()) {
            this.aM = (float) ((double) this.aM + (double) this.abilities.b() * 0.3D);
            this.aN = (float) ((double) this.aN + (double) this.ck * 0.3D);
        }

        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
        // CraftBukkit - Math -> TrigMath
        float f1 = (float) org.bukkit.craftbukkit.TrigMath.atan(-this.motY * 0.20000000298023224D) * 15.0F;

        if (f > 0.1F) {
            f = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0) {
            f = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0) {
            f1 = 0.0F;
        }

        this.bS += (f - this.bS) * 0.4F;
        this.ba += (f1 - this.ba) * 0.8F;
        if (this.getHealth() > 0) {
            List list = this.world.getEntities(this, this.boundingBox.grow(1.0D, 0.0D, 1.0D));

            if (list != null) {
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    if (!entity.dead) {
                        this.q(entity);
                    }
                }
            }
        }
    }

    private void q(Entity entity) {
        entity.b_(this);
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.a(0.2F, 0.2F);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.motY = 0.10000000149011612D;
        this.captureDrops = true;
        this.capturedDrops.clear();
        
        if (this.name.equals("Notch")) {
            this.a(new ItemStack(Item.APPLE, 1), true);
        }

        if (!this.world.getGameRules().getBoolean("keepInventory")) {
            this.inventory.l();
        }
        
        this.captureDrops = false;

        if (!this.world.isStatic)
        {
            PlayerDropsEvent var2 = new PlayerDropsEvent(this, damagesource, this.capturedDrops, this.lastDamageByPlayerTime > 0);

            if (!MinecraftForge.EVENT_BUS.post(var2))
            {
                Iterator var3 = this.capturedDrops.iterator();

                while (var3.hasNext())
                {
                    EntityItem var4 = (EntityItem)var3.next();
                    this.a(var4);
                }
            }
        }

        if (damagesource != null) {
            this.motX = (double) (-MathHelper.cos((this.aW + this.yaw) * 3.1415927F / 180.0F) * 0.1F);
            this.motZ = (double) (-MathHelper.sin((this.aW + this.yaw) * 3.1415927F / 180.0F) * 0.1F);
        } else {
            this.motX = this.motZ = 0.0D;
        }

        this.height = 0.1F;
        this.a(StatisticList.y, 1);
    }

    public void c(Entity entity, int i) {
        this.bQ += i;
        if (entity instanceof EntityHuman) {
            this.a(StatisticList.A, 1);
        } else {
            this.a(StatisticList.z, 1);
        }
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem bN()
    {
        ItemStack var1 = this.inventory.getItemInHand();
        return var1 == null ? null : (var1.getItem().onDroppedByPlayer(var1, this) ? ForgeHooks.onPlayerTossEvent(this, this.inventory.splitStack(this.inventory.itemInHandIndex, 1)) : null);
    }

    /**
     * Args: itemstack - called when player drops an item stack that's not in his inventory (like items still placed in
     * a workbench while the workbench'es GUI gets closed)
     */
    public EntityItem drop(ItemStack var1)
    {
        return ForgeHooks.onPlayerTossEvent(this, var1);
    }

    public EntityItem a(ItemStack itemstack, boolean flag) {
        if (itemstack == null) {
            return null;
        } else {
            EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY - 0.30000001192092896D + (double) this.getHeadHeight(), this.locZ, itemstack);

            entityitem.pickupDelay = 40;
            float f = 0.1F;
            float f1;

            if (flag) {
                f1 = this.random.nextFloat() * 0.5F;
                float f2 = this.random.nextFloat() * 3.1415927F * 2.0F;

                entityitem.motX = (double) (-MathHelper.sin(f2) * f1);
                entityitem.motZ = (double) (MathHelper.cos(f2) * f1);
                entityitem.motY = 0.20000000298023224D;
            } else {
                f = 0.3F;
                entityitem.motX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
                entityitem.motZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
                entityitem.motY = (double) (-MathHelper.sin(this.pitch / 180.0F * 3.1415927F) * f + 0.1F);
                f = 0.02F;
                f1 = this.random.nextFloat() * 3.1415927F * 2.0F;
                f *= this.random.nextFloat();
                entityitem.motX += Math.cos((double) f1) * (double) f;
                entityitem.motY += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                entityitem.motZ += Math.sin((double) f1) * (double) f;
            }

            // CraftBukkit start
            Player player = (Player) this.getBukkitEntity();
            CraftItem drop = new CraftItem(this.world.getServer(), entityitem);

            PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                player.getInventory().addItem(drop.getItemStack());
                return null;
            }
            // CraftBukkit end

            this.a(entityitem);
            this.a(StatisticList.v, 1);
            return entityitem;
        }
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    public void a(EntityItem var1)
    {
        if (this.captureDrops)
        {
            this.capturedDrops.add(var1);
        }
        else
        {
            this.world.addEntity(var1);
        }
    }

    @Deprecated

    /**
     * Returns how strong the player is against the specified block at this moment
     */
    public float a(Block var1)
    {
        return this.getCurrentPlayerStrVsBlock(var1, 0);
    }

    public float getCurrentPlayerStrVsBlock(Block var1, int var2)
    {
        ItemStack var3 = this.inventory.getItemInHand();
        float var4 = var3 == null ? 1.0F : var3.getItem().getStrVsBlock(var3, var1, var2);
        int var5 = EnchantmentManager.getDigSpeedEnchantmentLevel(this);

        if (var5 > 0 && ForgeHooks.canHarvestBlock(var1, this, var2))
        {
            var4 += (float)(var5 * var5 + 1);
        }

        if (this.hasEffect(MobEffectList.FASTER_DIG))
        {
            var4 *= 1.0F + (float)(this.getEffect(MobEffectList.FASTER_DIG).getAmplifier() + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffectList.SLOWER_DIG))
        {
            var4 *= 1.0F - (float)(this.getEffect(MobEffectList.SLOWER_DIG).getAmplifier() + 1) * 0.2F;
        }

        if (this.a(Material.WATER) && !EnchantmentManager.hasWaterWorkerEnchantment(this))
        {
            var4 /= 5.0F;
        }

        if (!this.onGround)
        {
            var4 /= 5.0F;
        }

        var4 = ForgeEventFactory.getBreakSpeed(this, var1, var2, var4);
        return var4 < 0.0F ? 0.0F : var4;
    }

    /**
     * Checks if the player has the ability to harvest a block (checks current inventory item for a tool if necessary)
     */
    public boolean b(Block var1)
    {
        return ForgeEventFactory.doPlayerHarvestCheck(this, var1, this.inventory.b(var1));
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Inventory");

        this.inventory.b(nbttaglist);
        this.sleeping = nbttagcompound.getBoolean("Sleeping");
        this.sleepTicks = nbttagcompound.getShort("SleepTimer");
        this.exp = nbttagcompound.getFloat("XpP");
        this.expLevel = nbttagcompound.getInt("XpLevel");
        this.expTotal = nbttagcompound.getInt("XpTotal");
        if (this.sleeping) {
            this.cc = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
            this.a(true, true, false);
        }

        // CraftBukkit start
        this.spawnWorld = nbttagcompound.getString("SpawnWorld");
        if ("".equals(spawnWorld)) {
            this.spawnWorld = this.world.getServer().getWorlds().get(0).getName();
        }
        // CraftBukkit end

        if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
            this.c = new ChunkCoordinates(nbttagcompound.getInt("SpawnX"), nbttagcompound.getInt("SpawnY"), nbttagcompound.getInt("SpawnZ"));
            this.d = nbttagcompound.getBoolean("SpawnForced");
        }

        this.foodData.a(nbttagcompound);
        this.abilities.b(nbttagcompound);
        if (nbttagcompound.hasKey("EnderItems")) {
            NBTTagList nbttaglist1 = nbttagcompound.getList("EnderItems");

            this.enderChest.a(nbttaglist1);
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.set("Inventory", this.inventory.a(new NBTTagList()));
        nbttagcompound.setBoolean("Sleeping", this.sleeping);
        nbttagcompound.setShort("SleepTimer", (short) this.sleepTicks);
        nbttagcompound.setFloat("XpP", this.exp);
        nbttagcompound.setInt("XpLevel", this.expLevel);
        nbttagcompound.setInt("XpTotal", this.expTotal);
        if (this.c != null) {
            nbttagcompound.setInt("SpawnX", this.c.x);
            nbttagcompound.setInt("SpawnY", this.c.y);
            nbttagcompound.setInt("SpawnZ", this.c.z);
            nbttagcompound.setBoolean("SpawnForced", this.d);
            nbttagcompound.setString("SpawnWorld", spawnWorld); // CraftBukkit - fixes bed spawns for multiworld worlds
        }

        this.foodData.b(nbttagcompound);
        this.abilities.a(nbttagcompound);
        nbttagcompound.set("EnderItems", this.enderChest.g());
    }

    public void openContainer(IInventory iinventory) {}

    public void startEnchanting(int i, int j, int k) {}

    public void openAnvil(int i, int j, int k) {}

    public void startCrafting(int i, int j, int k) {}

    public float getHeadHeight() {
        return 0.12F;
    }

    protected void e_() {
        this.height = 1.62F;
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        if (this.abilities.isInvulnerable && !damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            this.bC = 0;
            if (this.getHealth() <= 0) {
                return false;
            } else {
                if (this.isSleeping() && !this.world.isStatic) {
                    this.a(true, true, false);
                }

                if (damagesource.n()) {
                    if (this.world.difficulty == 0) {
                        return false; // CraftBukkit - i = 0 -> return false
                    }

                    if (this.world.difficulty == 1) {
                        i = i / 2 + 1;
                    }

                    if (this.world.difficulty == 3) {
                        i = i * 3 / 2;
                    }
                }

                if (false && i == 0) { // CraftBukkit - Don't filter out 0 damage
                    return false;
                } else {
                    Entity entity = damagesource.getEntity();

                    if (entity instanceof EntityArrow && ((EntityArrow) entity).shooter != null) {
                        entity = ((EntityArrow) entity).shooter;
                    }

                    if (entity instanceof EntityLiving) {
                        this.a((EntityLiving) entity, false);
                    }

                    this.a(StatisticList.x, i);
                    return super.damageEntity(damagesource, i);
                }
            }
        }
    }

    protected int c(DamageSource damagesource, int i) {
        int j = super.c(damagesource, i);

        if (j <= 0) {
            return 0;
        } else {
            int k = EnchantmentManager.a(this.inventory.armor, damagesource);

            if (k > 20) {
                k = 20;
            }

            if (k > 0 && k <= 20) {
                int l = 25 - k;
                int i1 = j * l + this.aS;

                j = i1 / 25;
                this.aS = i1 % 25;
            }

            return j;
        }
    }

    protected boolean h() {
        return false;
    }

    protected void a(EntityLiving entityliving, boolean flag) {
        if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
            if (entityliving instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entityliving;

                if (entitywolf.isTamed() && this.name.equals(entitywolf.getOwnerName())) {
                    return;
                }
            }

            if (!(entityliving instanceof EntityHuman) || this.h()) {
                List list = this.world.a(EntityWolf.class, AxisAlignedBB.a().a(this.locX, this.locY, this.locZ, this.locX + 1.0D, this.locY + 1.0D, this.locZ + 1.0D).grow(16.0D, 4.0D, 16.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityWolf entitywolf1 = (EntityWolf) iterator.next();

                    if (entitywolf1.isTamed() && entitywolf1.l() == null && this.name.equals(entitywolf1.getOwnerName()) && (!flag || !entitywolf1.isSitting())) {
                        entitywolf1.setSitting(false);
                        entitywolf1.setTarget(entityliving);
                    }
                }
            }
        }
    }

    protected void k(int i) {
        this.inventory.g(i);
    }

    public int aU() {
        return this.inventory.k();
    }

    public float bO() {
        int i = 0;
        ItemStack[] aitemstack = this.inventory.armor;
        int j = aitemstack.length;

        for (int k = 0; k < j; ++k) {
            ItemStack itemstack = aitemstack[k];

            if (itemstack != null) {
                ++i;
            }
        }

        return (float) i / (float) this.inventory.armor.length;
    }

    protected void d(DamageSource damagesource, int i) 
    {
        if (!this.invulnerable) 
        {
            i = ForgeHooks.onLivingHurt(this, damagesource, i);
            if (i <= 0)
                return;
            
            if (!damagesource.ignoresArmor() && this.be()) {
                i = 1 + i >> 1;
            }

            i = ISpecialArmor$ArmorProperties.ApplyArmor(this, this.inventory.armor, damagesource, (double)i);
            if (i <= 0)
                return;
            
            i = this.c(damagesource, i);
            this.j(damagesource.d());
            this.health -= i;
        }
    }

    public void openFurnace(TileEntityFurnace tileentityfurnace) {}

    public void openDispenser(TileEntityDispenser tileentitydispenser) {}

    public void a(TileEntity tileentity) {}

    public void openBrewingStand(TileEntityBrewingStand tileentitybrewingstand) {}

    public void openBeacon(TileEntityBeacon tileentitybeacon) {}

    public void openTrade(IMerchant imerchant) {}

    public void d(ItemStack itemstack) {}

    public boolean o(Entity entity) {
    	if (MinecraftForge.EVENT_BUS.post(new EntityInteractEvent(this, entity)))
        {
            return false;
        }
        else if (entity.c(this)) {
            return true;
        } else {
            ItemStack itemstack = this.bP();

            if (itemstack != null && entity instanceof EntityLiving) {
                if (this.abilities.canInstantlyBuild) {
                    itemstack = itemstack.cloneItemStack();
                }

                if (itemstack.a((EntityLiving) entity)) {
                    // CraftBukkit - bypass infinite items; <= 0 -> == 0
                    if (itemstack.count == 0 && !this.abilities.canInstantlyBuild) {
                        this.bQ();
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public ItemStack bP() {
        return this.inventory.getItemInHand();
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void bQ()
    {
        ItemStack var1 = this.bP();
        this.inventory.setItem(this.inventory.itemInHandIndex, (ItemStack)null);
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this, var1));
    }

    public double W() {
        return (double) (this.height - 0.5F);
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.  The equipped item has hitEntity
     * called on it. Args: targetEntity
     */
    public void attack(Entity var1)
    {
        if (!MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(this, var1)))
        {
            ItemStack var2 = this.bP();

            if (var2 == null || !var2.getItem().onLeftClickEntity(var2, this, var1))
            {
                if (var1.aq())
                {
                    int var3 = this.inventory.a(var1);

                    if (this.hasEffect(MobEffectList.INCREASE_DAMAGE))
                    {
                        var3 += 3 << this.getEffect(MobEffectList.INCREASE_DAMAGE).getAmplifier();
                    }

                    if (this.hasEffect(MobEffectList.WEAKNESS))
                    {
                        var3 -= 2 << this.getEffect(MobEffectList.WEAKNESS).getAmplifier();
                    }

                    int var4 = 0;
                    int var5 = 0;

                    if (var1 instanceof EntityLiving)
                    {
                        var5 = EnchantmentManager.a(this, (EntityLiving)var1);
                        var4 += EnchantmentManager.getKnockbackEnchantmentLevel(this, (EntityLiving)var1);
                    }

                    if (this.isSprinting())
                    {
                        ++var4;
                    }

                    if (var3 > 0 || var5 > 0)
                    {
                        boolean var6 = this.fallDistance > 0.0F && !this.onGround && !this.g_() && !this.H() && !this.hasEffect(MobEffectList.BLINDNESS) && this.vehicle == null && var1 instanceof EntityLiving;

                        if (var6)
                        {
                            var3 += this.random.nextInt(var3 / 2 + 2);
                        }

                        var3 += var5;
                        boolean var7 = var1.damageEntity(DamageSource.playerAttack(this), var3);
                        
                        // CraftBukkit start - Return when the damage fails so that the item will not lose durability
                        if (!var7) {
                            return;
                        }
                        // CraftBukkit end

                        if (var7)
                        {
                            if (var4 > 0)
                            {
                                var1.g((double)(-MathHelper.sin(this.yaw * (float)Math.PI / 180.0F) * (float)var4 * 0.5F), 0.1D, (double)(MathHelper.cos(this.yaw * (float)Math.PI / 180.0F) * (float)var4 * 0.5F));
                                this.motX *= 0.6D;
                                this.motZ *= 0.6D;
                                this.setSprinting(false);
                            }

                            if (var6)
                            {
                                this.b(var1);
                            }

                            if (var5 > 0)
                            {
                                this.c(var1);
                            }

                            if (var3 >= 18)
                            {
                                this.a(AchievementList.E);
                            }

                            this.k(var1);
                        }

                        ItemStack var8 = this.bP();

                        if (var8 != null && var1 instanceof EntityLiving)
                        {
                            var8.a((EntityLiving)var1, this);

                            // CraftBukkit - bypass infinite items; <= 0 -> == 0
                            if (var8.count == 0) {
                                this.bQ();
                            }
                        }

                        if (var1 instanceof EntityLiving)
                        {
                            if (var1.isAlive())
                            {
                                this.a((EntityLiving)var1, true);
                            }

                            this.a(StatisticList.w, var3);
                            int l = EnchantmentManager.getFireAspectEnchantmentLevel(this, (EntityLiving)var1);

                            if (l > 0 && var7) {
                                // CraftBukkit start - raise a combust event when somebody hits with a fire enchanted item
                                EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), var1.getBukkitEntity(), l * 4);
                                org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                                if (!combustEvent.isCancelled()) {
                                	var1.setOnFire(combustEvent.getDuration());
                                }
                                // CraftBukkit end
                            }
                        }

                        this.j(0.3F);
                    }
                }
            }
        }
    }

    public void b(Entity entity) {}

    public void c(Entity entity) {}

    public void die() {
        super.die();
        this.defaultContainer.a(this);
        if (this.activeContainer != null) {
            this.activeContainer.a(this);
        }
    }

    public boolean inBlock() {
        return !this.sleeping && super.inBlock();
    }

    public boolean bS() {
        return false;
    }

    /**
     * puts player to sleep on specified bed if possible
     */
    public EnumBedResult a(int var1, int var2, int var3)
    {
        PlayerSleepInBedEvent var4 = new PlayerSleepInBedEvent(this, var1, var2, var3);
        MinecraftForge.EVENT_BUS.post(var4);

        if (var4.result != null)
        {
            return var4.result;
        }
        else
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

                double var5 = 8.0D;
                double var7 = 5.0D;
                List var9 = this.world.a(EntityMonster.class, AxisAlignedBB.a().a((double)var1 - var5, (double)var2 - var7, (double)var3 - var5, (double)var1 + var5, (double)var2 + var7, (double)var3 + var5));

                if (!var9.isEmpty())
                {
                    return EnumBedResult.NOT_SAFE;
                }
            }
            
            // CraftBukkit start
            if (this.getBukkitEntity() instanceof Player) {
                Player player = (Player) this.getBukkitEntity();
                org.bukkit.block.Block bed = this.world.getWorld().getBlockAt(var1, var2, var3);

                PlayerBedEnterEvent event = new PlayerBedEnterEvent(player, bed);
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return EnumBedResult.OTHER_PROBLEM;
                }
            }
            // CraftBukkit end

            this.a(0.2F, 0.2F);
            this.height = 0.2F;

            if (this.world.isLoaded(var1, var2, var3))
            {
                int var10 = this.world.getData(var1, var2, var3);
                int var6 = BlockBed.e(var10);
                Block var11 = Block.byId[this.world.getTypeId(var1, var2, var3)];

                if (var11 != null)
                {
                    var6 = var11.getBedDirection(this.world, var1, var2, var3);
                }

                float var8 = 0.5F;
                float var12 = 0.5F;

                switch (var6)
                {
                    case 0:
                        var12 = 0.9F;
                        break;

                    case 1:
                        var8 = 0.1F;
                        break;

                    case 2:
                        var12 = 0.1F;
                        break;

                    case 3:
                        var8 = 0.9F;
                }

                this.u(var6);
                this.setPosition((double)((float)var1 + var8), (double)((float)var2 + 0.9375F), (double)((float)var3 + var12));
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
    }

    private void u(int i) {
        this.cd = 0.0F;
        this.ce = 0.0F;
        switch (i) {
        case 0:
            this.ce = -1.8F;
            break;

        case 1:
            this.cd = 1.8F;
            break;

        case 2:
            this.ce = 1.8F;
            break;

        case 3:
            this.cd = -1.8F;
        }
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        this.a(0.6F, 1.8F);
        this.e_();
        ChunkCoordinates chunkcoordinates = this.cc;
        ChunkCoordinates chunkcoordinates1 = this.cc;

        Block var6 = chunkcoordinates == null ? null : Block.byId[this.world.getTypeId(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z)];

        if (chunkcoordinates != null && var6 != null && var6.isBed(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, this))
        {
            var6.setBedOccupied(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, this, false);
            chunkcoordinates1 = var6.getBedSpawnPosition(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, this);
            
            if (chunkcoordinates1 == null) {
                chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.x, chunkcoordinates.y + 1, chunkcoordinates.z);
            }

            this.setPosition((double) ((float) chunkcoordinates1.x + 0.5F), (double) ((float) chunkcoordinates1.y + this.height + 0.1F), (double) ((float) chunkcoordinates1.z + 0.5F));
        }

        this.sleeping = false;
        if (!this.world.isStatic && flag1) {
            this.world.everyoneSleeping();
        }

        // CraftBukkit start
        if (this.getBukkitEntity() instanceof Player) {
            Player player = (Player) this.getBukkitEntity();

            org.bukkit.block.Block bed;
            if (chunkcoordinates != null) {
                bed = this.world.getWorld().getBlockAt(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z);
            } else {
                bed = this.world.getWorld().getBlockAt(player.getLocation());
            }

            PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed);
            this.world.getServer().getPluginManager().callEvent(event);
        }
        // CraftBukkit end

        if (flag) {
            this.sleepTicks = 0;
        } else {
            this.sleepTicks = 100;
        }

        if (flag2) {
            this.setRespawnPosition(this.cc, false);
        }
    }

    /**
     * Checks if the player is currently in a bed
     */
    private boolean j()
    {
        ChunkCoordinates var1 = this.cc;
        int var2 = this.world.getTypeId(var1.x, var1.y, var1.z);
        return Block.byId[var2] != null && Block.byId[var2].isBed(this.world, var1.x, var1.y, var1.z, this);
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
        Block var5 = Block.byId[var0.getTypeId(var1.x, var1.y, var1.z)];

        if (var5 != null && var5.isBed(var0, var1.x, var1.y, var1.z, (EntityLiving)null))
        {
            ChunkCoordinates var6 = var5.getBedSpawnPosition(var0, var1.x, var1.y, var1.z, (EntityHuman)null);
            return var6;
        }
        else
        {
            return var2 && var0.isEmpty(var1.x, var1.y, var1.z) && var0.isEmpty(var1.x, var1.y + 1, var1.z) ? var1 : null;
        }
    }

    public boolean isSleeping() {
        return this.sleeping;
    }

    public boolean isDeeplySleeping() {
        return this.sleeping && this.sleepTicks >= 100;
    }

    protected void b(int i, boolean flag) {
        byte b0 = this.datawatcher.getByte(16);

        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 | 1 << i)));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & ~(1 << i))));
        }
    }

    public void b(String s) {}

    public ChunkCoordinates getBed() {
        return this.c;
    }

    public boolean isRespawnForced() {
        return this.d;
    }

    public void setRespawnPosition(ChunkCoordinates chunkcoordinates, boolean flag) {
        if (chunkcoordinates != null) {
            this.c = new ChunkCoordinates(chunkcoordinates);
            this.d = flag;
            this.spawnWorld = this.world.worldData.getName(); // CraftBukkit
        } else {
            this.c = null;
            this.d = false;
        }
    }

    public void a(Statistic statistic) {
        this.a(statistic, 1);
    }

    public void a(Statistic statistic, int i) {}

    protected void bf() {
        super.bf();
        this.a(StatisticList.u, 1);
        if (this.isSprinting()) {
            this.j(0.8F);
        } else {
            this.j(0.2F);
        }
    }

    public void e(float f, float f1) {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;

        if (this.abilities.isFlying && this.vehicle == null) {
            double d3 = this.motY;
            float f2 = this.aN;

            this.aN = this.abilities.a();
            super.e(f, f1);
            this.motY = d3 * 0.6D;
            this.aN = f2;
        } else {
            super.e(f, f1);
        }

        this.checkMovement(this.locX - d0, this.locY - d1, this.locZ - d2);
    }

    public void checkMovement(double d0, double d1, double d2) {
        if (this.vehicle == null) {
            int i;

            if (this.a(Material.WATER)) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.q, i);
                    this.j(0.015F * (float) i * 0.01F);
                }
            } else if (this.H()) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.m, i);
                    this.j(0.015F * (float) i * 0.01F);
                }
            } else if (this.g_()) {
                if (d1 > 0.0D) {
                    this.a(StatisticList.o, (int) Math.round(d1 * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.l, i);
                    if (this.isSprinting()) {
                        this.j(0.099999994F * (float) i * 0.01F);
                    } else {
                        this.j(0.01F * (float) i * 0.01F);
                    }
                }
            } else {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 25) {
                    this.a(StatisticList.p, i);
                }
            }
        }
    }

    private void k(double d0, double d1, double d2) {
        if (this.vehicle != null) {
            int i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

            if (i > 0) {
                if (this.vehicle instanceof EntityMinecart) {
                    this.a(StatisticList.r, i);
                    if (this.e == null) {
                        this.e = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
                    } else if ((double) this.e.e(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) >= 1000000.0D) {
                        this.a((Statistic) AchievementList.q, 1);
                    }
                } else if (this.vehicle instanceof EntityBoat) {
                    this.a(StatisticList.s, i);
                } else if (this.vehicle instanceof EntityPig) {
                    this.a(StatisticList.t, i);
                }
            }
        }
    }

    protected void a(float f) {
        if (!this.abilities.canFly) {
            if (f >= 2.0F) {
                this.a(StatisticList.n, (int) Math.round((double) f * 100.0D));
            }

            super.a(f);
        }
    }

    public void a(EntityLiving entityliving) {
        if (entityliving instanceof IMonster) {
            this.a((Statistic) AchievementList.s);
        }
    }

    public ItemStack q(int i) {
        return this.inventory.f(i);
    }

    protected void bB() {}

    protected void bC() {}

    public void giveExp(int i) {
        this.bQ += i;
        int j = Integer.MAX_VALUE - this.expTotal;

        if (i > j) {
            i = j;
        }

        this.exp += (float) i / (float) this.getExpToLevel();

        for (this.expTotal += i; this.exp >= 1.0F; this.exp /= (float) this.getExpToLevel()) {
            this.exp = (this.exp - 1.0F) * (float) this.getExpToLevel();
            this.levelDown(1);
        }
    }

    public void levelDown(int i) {
        this.expLevel += i;
        if (this.expLevel < 0) {
            this.expLevel = 0;
        }

        if (i > 0 && this.expLevel % 5 == 0 && (float) this.h < (float) this.ticksLived - 100.0F) {
            float f = this.expLevel > 30 ? 1.0F : (float) this.expLevel / 30.0F;

            this.world.makeSound(this, "random.levelup", f * 0.75F, 1.0F);
            this.h = this.ticksLived;
        }
    }

    public int getExpToLevel() {
        return this.expLevel >= 30 ? 62 + (this.expLevel - 30) * 7 : (this.expLevel >= 15 ? 17 + (this.expLevel - 15) * 3 : 17);
    }

    /**
     * increases exhaustion level by supplied amount
     */
    public void j(float var1)
    {
        if (!this.abilities.isInvulnerable && !this.world.isStatic)
        {
            this.foodData.a(var1);
        }
    }

    public FoodMetaData getFoodData() {
        return this.foodData;
    }

    public boolean f(boolean flag) {
        return (flag || this.foodData.c()) && !this.abilities.isInvulnerable;
    }

    public boolean ca() {
        return this.getHealth() > 0 && this.getHealth() < this.getMaxHealth();
    }

    public void a(ItemStack itemstack, int i) {
        if (itemstack != this.f) {
            this.f = itemstack;
            this.g = i;
            if (!this.world.isStatic) {
                this.d(true);
            }
        }
    }

    public boolean f(int i, int j, int k) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            int l = this.world.getTypeId(i, j, k);

            if (l > 0 && this.bP() != null) {
                Block block = Block.byId[l];
                ItemStack itemstack = this.bP();

                if (itemstack.b(block) || itemstack.a(block) > 1.0F) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean a(int i, int j, int k, int l, ItemStack itemstack) {
        return this.abilities.mayBuild ? true : (itemstack != null ? itemstack.x() : false);
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (this.world.getGameRules().getBoolean("keepInventory")) {
            return 0;
        } else {
            int i = this.expLevel * 7;

            return i > 100 ? 100 : i;
        }
    }

    protected boolean alwaysGivesExp() {
        return true;
    }

    public String getLocalizedName() {
        return this.name;
    }

    public void copyTo(EntityHuman entityhuman, boolean flag) {
        if (flag) {
            this.inventory.b(entityhuman.inventory);
            this.health = entityhuman.health;
            this.foodData = entityhuman.foodData;
            this.expLevel = entityhuman.expLevel;
            this.expTotal = entityhuman.expTotal;
            this.exp = entityhuman.exp;
            this.bQ = entityhuman.bQ;
            this.aq = entityhuman.aq;
        } else if (this.world.getGameRules().getBoolean("keepInventory")) {
            this.inventory.b(entityhuman.inventory);
            this.expLevel = entityhuman.expLevel;
            this.expTotal = entityhuman.expTotal;
            this.exp = entityhuman.exp;
            this.bQ = entityhuman.bQ;
        }

        this.enderChest = entityhuman.enderChest;
    }

    protected boolean f_() {
        return !this.abilities.isFlying;
    }

    public void updateAbilities() {}

    public void a(EnumGamemode enumgamemode) {}

    public String getName() {
        return this.name;
    }

    public LocaleLanguage getLocale() {
        return LocaleLanguage.a();
    }

    public String a(String s, Object... aobject) {
        return this.getLocale().a(s, aobject);
    }

    public InventoryEnderChest getEnderChest() {
        return this.enderChest;
    }

    public ItemStack getEquipment(int i) {
        return i == 0 ? this.inventory.getItemInHand() : this.inventory.armor[i - 1];
    }

    public ItemStack bA() {
        return this.inventory.getItemInHand();
    }

    public void setEquipment(int i, ItemStack itemstack) {
        this.inventory.armor[i] = itemstack;
    }

    public ItemStack[] getEquipment() {
        return this.inventory.armor;
    }

    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z)
    {
    	FMLNetworkHandler.openGui(this, mod, modGuiId, world, x, y, z);
    }
}
