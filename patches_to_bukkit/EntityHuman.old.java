package net.minecraft.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.TickType;
import forge.ArmorProperties;
import forge.ForgeHooks;
import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;
import forge.packets.PacketOpenGUI;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.TrigMath;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.PluginManager;

public abstract class EntityHuman extends EntityLiving
{
  public PlayerInventory inventory = new PlayerInventory(this);
  public Container defaultContainer;
  public Container activeContainer;
  protected FoodMetaData foodData = new FoodMetaData();
  protected int o = 0;
  public byte p = 0;
  public int q = 0;
  public float r;
  public float s;
  public boolean t = false;
  public int u = 0;
  public String name;
  public int dimension;
  public int x = 0;
  public double y;
  public double z;
  public double A;
  public double B;
  public double C;
  public double D;
  public boolean sleeping;
  public boolean fauxSleeping;
  public String spawnWorld = "";
  public ChunkCoordinates F;
  public int sleepTicks;
  public float G;
  public float H;
  private ChunkCoordinates b;
  private ChunkCoordinates c;
  public int I = 20;
  protected boolean J = false;
  public float K;
  public PlayerAbilities abilities = new PlayerAbilities();
  public int oldLevel = -1;
  public int expLevel;
  public int expTotal;
  public float exp;
  private ItemStack d;
  private int e;
  protected float P = 0.1F;
  protected float Q = 0.02F;
  public EntityFishingHook hookedFish = null;

  public HumanEntity getBukkitEntity()
  {
    return (HumanEntity)super.getBukkitEntity();
  }

  public EntityHuman(World world)
  {
    super(world);
    this.defaultContainer = new ContainerPlayer(this.inventory, !world.isStatic);
    this.activeContainer = this.defaultContainer;
    this.height = 1.62F;
    ChunkCoordinates chunkcoordinates = world.getSpawn();

    setPositionRotation(chunkcoordinates.x + 0.5D, chunkcoordinates.y + 1, chunkcoordinates.z + 0.5D, 0.0F, 0.0F);
    this.ah = "humanoid";
    this.ag = 180.0F;
    this.maxFireTicks = 20;
    this.texture = "/mob/char.png";
  }

  public int getMaxHealth() {
    return 20;
  }

  protected void b() {
    super.b();
    this.datawatcher.a(16, Byte.valueOf((byte)0));
    this.datawatcher.a(17, Byte.valueOf((byte)0));
  }

  public boolean M() {
    return this.d != null;
  }

  public void N() {
    if (this.d != null) {
      this.d.b(this.world, this, this.e);
    }

    O();
  }

  public void O() {
    this.d = null;
    this.e = 0;
    if (!this.world.isStatic)
      i(false);
  }

  public boolean P()
  {
    return (M()) && (Item.byId[this.d.id].d(this.d) == EnumAnimation.d);
  }

  public void F_() {
    FMLCommonHandler.instance().tickStart(EnumSet.of(TickType.PLAYER), new Object[] { this, this.world });
    if (this.d != null) {
      ItemStack itemstack = this.inventory.getItemInHand();

      if (itemstack != this.d) {
        O();
      } else {
        this.d.getItem().onUsingItemTick(this.d, this, this.e);
        if ((this.e <= 25) && (this.e % 4 == 0)) {
          b(itemstack, 5);
        }

        if ((--this.e == 0) && (!this.world.isStatic)) {
          K();
        }
      }
    }

    if (this.x > 0) {
      this.x -= 1;
    }

    if (isSleeping()) {
      this.sleepTicks += 1;
      if (this.sleepTicks > 100) {
        this.sleepTicks = 100;
      }

      if (!this.world.isStatic) {
        if (!G())
          a(true, true, false);
        else if (this.world.e())
          a(false, true, true);
      }
    }
    else if (this.sleepTicks > 0) {
      this.sleepTicks += 1;
      if (this.sleepTicks >= 110) {
        this.sleepTicks = 0;
      }
    }

    super.F_();
    if ((!this.world.isStatic) && (this.activeContainer != null) && (!this.activeContainer.b(this))) {
      closeInventory();
      this.activeContainer = this.defaultContainer;
    }

    if (this.abilities.isFlying)
    {
      for (int i = 0; i < 8; i++);
    }

    if ((isBurning()) && (this.abilities.isInvulnerable)) {
      extinguish();
    }

    this.y = this.B;
    this.z = this.C;
    this.A = this.D;
    double d0 = this.locX - this.B;
    double d1 = this.locY - this.C;
    double d2 = this.locZ - this.D;
    double d3 = 10.0D;

    if (d0 > d3) {
      this.y = (this.B = this.locX);
    }

    if (d2 > d3) {
      this.A = (this.D = this.locZ);
    }

    if (d1 > d3) {
      this.z = (this.C = this.locY);
    }

    if (d0 < -d3) {
      this.y = (this.B = this.locX);
    }

    if (d2 < -d3) {
      this.A = (this.D = this.locZ);
    }

    if (d1 < -d3) {
      this.z = (this.C = this.locY);
    }

    this.B += d0 * 0.25D;
    this.D += d2 * 0.25D;
    this.C += d1 * 0.25D;
    a(StatisticList.k, 1);
    if (this.vehicle == null) {
      this.c = null;
    }

    if (!this.world.isStatic) {
      this.foodData.a(this);
    }
    FMLCommonHandler.instance().tickEnd(EnumSet.of(TickType.PLAYER), new Object[] { this, this.world });
  }

  protected void b(ItemStack itemstack, int i) {
    if (itemstack.m() == EnumAnimation.c) {
      this.world.makeSound(this, "random.drink", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }

    if (itemstack.m() == EnumAnimation.b) {
      for (int j = 0; j < i; j++) {
        Vec3D vec3d = Vec3D.create((this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

        vec3d.a(-this.pitch * 3.141593F / 180.0F);
        vec3d.b(-this.yaw * 3.141593F / 180.0F);
        Vec3D vec3d1 = Vec3D.create((this.random.nextFloat() - 0.5D) * 0.3D, -this.random.nextFloat() * 0.6D - 0.3D, 0.6D);

        vec3d1.a(-this.pitch * 3.141593F / 180.0F);
        vec3d1.b(-this.yaw * 3.141593F / 180.0F);
        vec3d1 = vec3d1.add(this.locX, this.locY + getHeadHeight(), this.locZ);
        this.world.a("iconcrack_" + itemstack.getItem().id, vec3d1.a, vec3d1.b, vec3d1.c, vec3d.a, vec3d.b + 0.05D, vec3d.c);
      }

      this.world.makeSound(this, "random.eat", 0.5F + 0.5F * this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }
  }

  protected void K() {
    if (this.d != null) {
      b(this.d, 16);
      int i = this.d.count;
      ItemStack itemstack = this.d.b(this.world, this);

      if ((itemstack != this.d) || ((itemstack != null) && (itemstack.count != i))) {
        this.inventory.items[this.inventory.itemInHandIndex] = itemstack;
        if (itemstack.count == 0) {
          this.inventory.items[this.inventory.itemInHandIndex] = null;
        }
      }

      O();
    }
  }

  protected boolean Q() {
    return (getHealth() <= 0) || (isSleeping());
  }

  public void closeInventory()
  {
    this.activeContainer = this.defaultContainer;
  }

  public void R() {
    double d0 = this.locX;
    double d1 = this.locY;
    double d2 = this.locZ;

    super.R();
    this.r = this.s;
    this.s = 0.0F;
    h(this.locX - d0, this.locY - d1, this.locZ - d2);
  }

  private int E() {
    return hasEffect(MobEffectList.SLOWER_DIG) ? 6 + (1 + getEffect(MobEffectList.SLOWER_DIG).getAmplifier()) * 2 : hasEffect(MobEffectList.FASTER_DIG) ? 6 - (1 + getEffect(MobEffectList.FASTER_DIG).getAmplifier()) * 1 : 6;
  }

  protected void d_() {
    int i = E();

    if (this.t) {
      this.u += 1;
      if (this.u >= i) {
        this.u = 0;
        this.t = false;
      }
    } else {
      this.u = 0;
    }

    this.ao = (this.u / i);
  }

  public void e() {
    if (this.o > 0) {
      this.o -= 1;
    }

    if ((this.world.difficulty == 0) && (getHealth() < getMaxHealth()) && (this.ticksLived % 20 * 12 == 0))
    {
      heal(1, EntityRegainHealthEvent.RegainReason.REGEN);
    }

    this.inventory.i();
    this.r = this.s;
    super.e();
    this.al = this.P;
    this.am = this.Q;
    if (isSprinting()) {
      this.al = (float)(this.al + this.P * 0.3D);
      this.am = (float)(this.am + this.Q * 0.3D);
    }

    float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

    float f1 = (float)TrigMath.atan(-this.motY * 0.2000000029802322D) * 15.0F;

    if (f > 0.1F) {
      f = 0.1F;
    }

    if ((!this.onGround) || (getHealth() <= 0)) {
      f = 0.0F;
    }

    if ((this.onGround) || (getHealth() <= 0)) {
      f1 = 0.0F;
    }

    this.s += (f - this.s) * 0.4F;
    this.ay += (f1 - this.ay) * 0.8F;
    if (getHealth() > 0) {
      List list = this.world.getEntities(this, this.boundingBox.grow(1.0D, 0.0D, 1.0D));

      if (list != null)
        for (int i = 0; i < list.size(); i++) {
          Entity entity = (Entity)list.get(i);

          if (!entity.dead)
            l(entity);
        }
    }
  }

  private void l(Entity entity)
  {
    entity.a_(this);
  }

  public void die(DamageSource damagesource) {
    super.die(damagesource);
    b(0.2F, 0.2F);
    setPosition(this.locX, this.locY, this.locZ);
    this.motY = 0.1000000014901161D;
    if (this.name.equals("Notch")) {
      a(new ItemStack(Item.APPLE, 1), true);
    }

    this.inventory.k();
    if (damagesource != null) {
      this.motX = -MathHelper.cos((this.au + this.yaw) * 3.141593F / 180.0F) * 0.1F;
      this.motZ = -MathHelper.sin((this.au + this.yaw) * 3.141593F / 180.0F) * 0.1F;
    } else {
      this.motX = (this.motZ = 0.0D);
    }

    this.height = 0.1F;
    a(StatisticList.y, 1);
  }

  public void b(Entity entity, int i) {
    this.q += i;
    if ((entity instanceof EntityHuman))
      a(StatisticList.A, 1);
    else
      a(StatisticList.z, 1);
  }

  protected int b_(int i)
  {
    int j = EnchantmentManager.getOxygenEnchantmentLevel(this.inventory);

    return (j > 0) && (this.random.nextInt(j + 1) > 0) ? i : super.b_(i);
  }

  public EntityItem S() {
    ItemStack stack = this.inventory.getItemInHand();
    if (stack == null) {
      return null;
    }
    if (stack.getItem().onDroppedByPlayer(stack, this)) {
      return a(this.inventory.splitStack(this.inventory.itemInHandIndex, 1), false);
    }
    return null;
  }

  public EntityItem drop(ItemStack itemstack) {
    return a(itemstack, false);
  }

  public EntityItem a(ItemStack itemstack, boolean flag) {
    if (itemstack == null) {
      return null;
    }
    EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY - 0.300000011920929D + getHeadHeight(), this.locZ, itemstack);

    entityitem.pickupDelay = 40;
    float f = 0.1F;

    if (flag) {
      float f1 = this.random.nextFloat() * 0.5F;
      float f2 = this.random.nextFloat() * 3.141593F * 2.0F;

      entityitem.motX = -MathHelper.sin(f2) * f1;
      entityitem.motZ = MathHelper.cos(f2) * f1;
      entityitem.motY = 0.2000000029802322D;
    } else {
      f = 0.3F;
      entityitem.motX = -MathHelper.sin(this.yaw / 180.0F * 3.141593F) * MathHelper.cos(this.pitch / 180.0F * 3.141593F) * f;
      entityitem.motZ = MathHelper.cos(this.yaw / 180.0F * 3.141593F) * MathHelper.cos(this.pitch / 180.0F * 3.141593F) * f;
      entityitem.motY = -MathHelper.sin(this.pitch / 180.0F * 3.141593F) * f + 0.1F;
      f = 0.02F;
      float f1 = this.random.nextFloat() * 3.141593F * 2.0F;
      f *= this.random.nextFloat();
      entityitem.motX += Math.cos(f1) * f;
      entityitem.motY += (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
      entityitem.motZ += Math.sin(f1) * f;
    }

    Player player = (Player)getBukkitEntity();
    CraftItem drop = new CraftItem(this.world.getServer(), entityitem);

    PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
    this.world.getServer().getPluginManager().callEvent(event);

    if (event.isCancelled()) {
      player.getInventory().addItem(new org.bukkit.inventory.ItemStack[] { drop.getItemStack() });
      return null;
    }

    a(entityitem);
    a(StatisticList.v, 1);
    return entityitem;
  }

  protected void a(EntityItem entityitem)
  {
    this.world.addEntity(entityitem);
  }

  @Deprecated
  public float a(Block block) {
    return getCurrentPlayerStrVsBlock(block, 0);
  }

  public float getCurrentPlayerStrVsBlock(Block block, int meta) {
    ItemStack stack = this.inventory.getItemInHand();
    float f = stack == null ? 1.0F : stack.getItem().getStrVsBlock(stack, block, meta);

    float f1 = f;
    int i = EnchantmentManager.getDigSpeedEnchantmentLevel(this.inventory);

    if ((i > 0) && (this.inventory.b(block))) {
      f1 = f + i * i + 1;
    }

    if (hasEffect(MobEffectList.FASTER_DIG)) {
      f1 *= (1.0F + getEffect(MobEffectList.FASTER_DIG).getAmplifier() + 1 * 0.2F);
    }

    if (hasEffect(MobEffectList.SLOWER_DIG)) {
      f1 *= (1.0F - getEffect(MobEffectList.SLOWER_DIG).getAmplifier() + 1 * 0.2F);
    }

    if ((a(Material.WATER)) && (!EnchantmentManager.hasWaterWorkerEnchantment(this.inventory))) {
      f1 /= 5.0F;
    }

    if (!this.onGround) {
      f1 /= 5.0F;
    }

    return f1;
  }

  public boolean b(Block block) {
    return this.inventory.b(block);
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    NBTTagList nbttaglist = nbttagcompound.getList("Inventory");

    this.inventory.b(nbttaglist);
    this.dimension = nbttagcompound.getInt("Dimension");
    this.sleeping = nbttagcompound.getBoolean("Sleeping");
    this.sleepTicks = nbttagcompound.getShort("SleepTimer");
    this.exp = nbttagcompound.getFloat("XpP");
    this.expLevel = nbttagcompound.getInt("XpLevel");
    this.expTotal = nbttagcompound.getInt("XpTotal");
    if (this.sleeping) {
      this.F = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
      a(true, true, false);
    }

    this.spawnWorld = nbttagcompound.getString("SpawnWorld");
    if ("".equals(this.spawnWorld)) {
      this.spawnWorld = ((org.bukkit.World)this.world.getServer().getWorlds().get(0)).getName();
    }

    if ((nbttagcompound.hasKey("SpawnX")) && (nbttagcompound.hasKey("SpawnY")) && (nbttagcompound.hasKey("SpawnZ"))) {
      this.b = new ChunkCoordinates(nbttagcompound.getInt("SpawnX"), nbttagcompound.getInt("SpawnY"), nbttagcompound.getInt("SpawnZ"));
    }

    this.foodData.a(nbttagcompound);
    this.abilities.b(nbttagcompound);
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.set("Inventory", this.inventory.a(new NBTTagList()));
    nbttagcompound.setInt("Dimension", this.dimension);
    nbttagcompound.setBoolean("Sleeping", this.sleeping);
    nbttagcompound.setShort("SleepTimer", (short)this.sleepTicks);
    nbttagcompound.setFloat("XpP", this.exp);
    nbttagcompound.setInt("XpLevel", this.expLevel);
    nbttagcompound.setInt("XpTotal", this.expTotal);
    if (this.b != null) {
      nbttagcompound.setInt("SpawnX", this.b.x);
      nbttagcompound.setInt("SpawnY", this.b.y);
      nbttagcompound.setInt("SpawnZ", this.b.z);
      nbttagcompound.setString("SpawnWorld", this.spawnWorld);
    }

    this.foodData.b(nbttagcompound);
    this.abilities.a(nbttagcompound);
  }
  public void openContainer(IInventory iinventory) {
  }
  public void startEnchanting(int i, int j, int k) {
  }
  public void startCrafting(int i, int j, int k) {
  }
  public void receive(Entity entity, int i) {
  }

  public float getHeadHeight() {
    return 0.12F;
  }

  protected void A() {
    this.height = 1.62F;
  }

  public boolean damageEntity(DamageSource damagesource, int i) {
    if ((this.abilities.isInvulnerable) && (!damagesource.ignoresInvulnerability())) {
      return false;
    }
    this.aV = 0;
    if (getHealth() <= 0) {
      return false;
    }
    if ((isSleeping()) && (!this.world.isStatic)) {
      a(true, true, false);
    }

    Entity entity = damagesource.getEntity();

    if (((entity instanceof EntityMonster)) || ((entity instanceof EntityArrow))) {
      if (this.world.difficulty == 0) {
        return false;
      }

      if (this.world.difficulty == 1) {
        i = i / 2 + 1;
      }

      if (this.world.difficulty == 3) {
        i = i * 3 / 2;
      }

    }

    Entity entity1 = entity;

    if (((entity instanceof EntityArrow)) && (((EntityArrow)entity).shooter != null)) {
      entity1 = ((EntityArrow)entity).shooter;
    }

    if ((entity1 instanceof EntityLiving)) {
      a((EntityLiving)entity1, false);
    }

    a(StatisticList.x, i);
    return super.damageEntity(damagesource, i);
  }

  protected int b(DamageSource damagesource, int i)
  {
    int j = super.b(damagesource, i);

    if (j <= 0) {
      return 0;
    }
    int k = EnchantmentManager.a(this.inventory, damagesource);

    if (k > 20) {
      k = 20;
    }

    if ((k > 0) && (k <= 20)) {
      int l = 25 - k;
      int i1 = j * l + this.ar;

      j = i1 / 25;
      this.ar = (i1 % 25);
    }

    return j;
  }

  protected boolean C()
  {
    return false;
  }

  protected void a(EntityLiving entityliving, boolean flag) {
    if ((!(entityliving instanceof EntityCreeper)) && (!(entityliving instanceof EntityGhast))) {
      if ((entityliving instanceof EntityWolf)) {
        EntityWolf entitywolf = (EntityWolf)entityliving;

        if ((entitywolf.isTamed()) && (this.name.equals(entitywolf.getOwnerName()))) {
          return;
        }
      }

      if ((!(entityliving instanceof EntityHuman)) || (C())) {
        List list = this.world.a(EntityWolf.class, AxisAlignedBB.b(this.locX, this.locY, this.locZ, this.locX + 1.0D, this.locY + 1.0D, this.locZ + 1.0D).grow(16.0D, 4.0D, 16.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
          Entity entity = (Entity)iterator.next();
          EntityWolf entitywolf1 = (EntityWolf)entity;

          if ((entitywolf1.isTamed()) && (entitywolf1.I() == null) && (this.name.equals(entitywolf1.getOwnerName())) && ((!flag) || (!entitywolf1.isSitting()))) {
            entitywolf1.setSitting(false);
            entitywolf1.setTarget(entityliving);
          }
        }
      }
    }
  }

  protected void f(int i) {
    this.inventory.e(i);
  }

  public int T() {
    return this.inventory.j();
  }

  protected void c(DamageSource damagesource, int i) {
    if ((!damagesource.ignoresArmor()) && (P())) {
      i = 1 + i >> 1;
    }

    i = ArmorProperties.ApplyArmor(this, this.inventory.armor, damagesource, i);
    if (i <= 0) {
      return;
    }

    i = b(damagesource, i);
    c(damagesource.f());
    this.health -= i;
  }
  public void openFurnace(TileEntityFurnace tileentityfurnace) {
  }
  public void openDispenser(TileEntityDispenser tileentitydispenser) {
  }
  public void a(TileEntitySign tileentitysign) {
  }
  public void openBrewingStand(TileEntityBrewingStand tileentitybrewingstand) {
  }

  public void e(Entity entity) {
    if (!ForgeHooks.onEntityInteract(this, entity, false))
    {
      return;
    }
    if (!entity.b(this)) {
      ItemStack itemstack = U();

      if ((itemstack != null) && ((entity instanceof EntityLiving))) {
        if (this.abilities.canInstantlyBuild) itemstack = itemstack.cloneItemStack();
        itemstack.a((EntityLiving)entity);

        if ((itemstack.count == 0) && (!this.abilities.canInstantlyBuild)) {
          itemstack.a(this);
          V();
        }
      }
    }
  }

  public ItemStack U() {
    return this.inventory.getItemInHand();
  }

  public void V() {
    ItemStack orig = this.inventory.getItemInHand();
    this.inventory.setItem(this.inventory.itemInHandIndex, (ItemStack)null);
    ForgeHooks.onDestroyCurrentItem(this, orig);
  }

  public double W() {
    return this.height - 0.5F;
  }

  public void C_() {
    if ((!this.t) || (this.u >= E() / 2) || (this.u < 0)) {
      this.u = -1;
      this.t = true;
    }
  }

  public void attack(Entity entity) {
    if (!ForgeHooks.onEntityInteract(this, entity, true))
    {
      return;
    }
    ItemStack stack = this.inventory.getItemInHand();
    if ((stack != null) && (stack.getItem().onLeftClickEntity(stack, this, entity))) {
      return;
    }

    if (entity.k_()) {
      int i = this.inventory.a(entity);

      if (hasEffect(MobEffectList.INCREASE_DAMAGE)) {
        i += (3 << getEffect(MobEffectList.INCREASE_DAMAGE).getAmplifier());
      }

      if (hasEffect(MobEffectList.WEAKNESS)) {
        i -= (2 << getEffect(MobEffectList.WEAKNESS).getAmplifier());
      }

      int j = 0;
      int k = 0;

      if ((entity instanceof EntityLiving)) {
        k = EnchantmentManager.a(this.inventory, (EntityLiving)entity);
        j += EnchantmentManager.getKnockbackEnchantmentLevel(this.inventory, (EntityLiving)entity);
      }

      if (isSprinting()) {
        j++;
      }

      if ((i > 0) || (k > 0)) {
        boolean flag = (this.fallDistance > 0.0F) && (!this.onGround) && (!t()) && (!aU()) && (!hasEffect(MobEffectList.BLINDNESS)) && (this.vehicle == null) && ((entity instanceof EntityLiving));

        if (flag) {
          i += this.random.nextInt(i / 2 + 2);
        }

        i += k;
        boolean flag1 = entity.damageEntity(DamageSource.playerAttack(this), i);

        if (!flag1) {
          return;
        }

        if (flag1) {
          if (j > 0) {
            entity.b_(-MathHelper.sin(this.yaw * 3.141593F / 180.0F) * j * 0.5F, 0.1D, MathHelper.cos(this.yaw * 3.141593F / 180.0F) * j * 0.5F);
            this.motX *= 0.6D;
            this.motZ *= 0.6D;
            setSprinting(false);
          }

          if (flag) {
            c(entity);
          }

          if (k > 0) {
            d(entity);
          }

          if (i >= 18) {
            a(AchievementList.E);
          }

          g(entity);
        }

        ItemStack itemstack = U();

        if ((itemstack != null) && ((entity instanceof EntityLiving))) {
          itemstack.a((EntityLiving)entity, this);

          if (itemstack.count == 0) {
            itemstack.a(this);
            V();
          }
        }

        if ((entity instanceof EntityLiving)) {
          if (entity.isAlive()) {
            a((EntityLiving)entity, true);
          }

          a(StatisticList.w, i);
          int l = EnchantmentManager.getFireAspectEnchantmentLevel(this.inventory, (EntityLiving)entity);

          if (l > 0)
          {
            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), l * 4);
            Bukkit.getPluginManager().callEvent(combustEvent);

            if (!combustEvent.isCancelled()) {
              entity.setOnFire(combustEvent.getDuration());
            }
          }

        }

        c(0.3F);
      }
    }
  }
  public void c(Entity entity) {
  }

  public void d(Entity entity) {
  }

  public void carriedChanged(ItemStack itemstack) {  }


  public void die() { super.die();
    this.defaultContainer.a(this);
    if (this.activeContainer != null)
      this.activeContainer.a(this);
  }

  public boolean inBlock()
  {
    return (!this.sleeping) && (super.inBlock());
  }

  public EnumBedResult a(int i, int j, int k) {
    EnumBedResult customSleep = ForgeHooks.sleepInBedAt(this, i, j, k);
    if (customSleep != null) {
      return customSleep;
    }

    if (!this.world.isStatic) {
      if ((isSleeping()) || (!isAlive())) {
        return EnumBedResult.OTHER_PROBLEM;
      }

      if (!this.world.worldProvider.d()) {
        return EnumBedResult.NOT_POSSIBLE_HERE;
      }

      if (this.world.e()) {
        return EnumBedResult.NOT_POSSIBLE_NOW;
      }

      if ((Math.abs(this.locX - i) > 3.0D) || (Math.abs(this.locY - j) > 2.0D) || (Math.abs(this.locZ - k) > 3.0D)) {
        return EnumBedResult.TOO_FAR_AWAY;
      }

      double d0 = 8.0D;
      double d1 = 5.0D;
      List list = this.world.a(EntityMonster.class, AxisAlignedBB.b(i - d0, j - d1, k - d0, i + d0, j + d1, k + d0));

      if (!list.isEmpty()) {
        return EnumBedResult.NOT_SAFE;
      }

    }

    if ((getBukkitEntity() instanceof Player)) {
      Player player = (Player)getBukkitEntity();
      org.bukkit.block.Block bed = this.world.getWorld().getBlockAt(i, j, k);

      PlayerBedEnterEvent event = new PlayerBedEnterEvent(player, bed);
      this.world.getServer().getPluginManager().callEvent(event);

      if (event.isCancelled()) {
        return EnumBedResult.OTHER_PROBLEM;
      }

    }

    b(0.2F, 0.2F);
    this.height = 0.2F;
    if (this.world.isLoaded(i, j, k)) {
      int l = this.world.getData(i, j, k);
      int i1 = BlockBed.b(l);
      Block block = Block.byId[this.world.getTypeId(i, j, k)];
      if (block != null)
      {
        i1 = block.getBedDirection(this.world, i, j, k);
      }
      float f = 0.5F;
      float f1 = 0.5F;

      switch (i1) {
      case 0:
        f1 = 0.9F;
        break;
      case 1:
        f = 0.1F;
        break;
      case 2:
        f1 = 0.1F;
        break;
      case 3:
        f = 0.9F;
      }

      c(i1);
      setPosition(i + f, j + 0.9375F, k + f1);
    } else {
      setPosition(i + 0.5F, j + 0.9375F, k + 0.5F);
    }

    this.sleeping = true;
    this.sleepTicks = 0;
    this.F = new ChunkCoordinates(i, j, k);
    this.motX = (this.motZ = this.motY = 0.0D);
    if (!this.world.isStatic) {
      this.world.everyoneSleeping();
    }

    return EnumBedResult.OK;
  }

  private void c(int i) {
    this.G = 0.0F;
    this.H = 0.0F;
    switch (i) {
    case 0:
      this.H = -1.8F;
      break;
    case 1:
      this.G = 1.8F;
      break;
    case 2:
      this.H = 1.8F;
      break;
    case 3:
      this.G = -1.8F;
    }
  }

  public void a(boolean flag, boolean flag1, boolean flag2) {
    if ((this.fauxSleeping) && (!this.sleeping)) return;

    b(0.6F, 1.8F);
    A();
    ChunkCoordinates chunkcoordinates = this.F;
    ChunkCoordinates chunkcoordinates1 = this.F;

    Block block = chunkcoordinates == null ? null : Block.byId[this.world.getTypeId(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z)];
    if ((chunkcoordinates != null) && (block != null) && (block.isBed(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, this))) {
      block.setBedOccupied(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, this, false);
      chunkcoordinates1 = block.getBedSpawnPosition(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, this);
      if (chunkcoordinates1 == null) {
        chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.x, chunkcoordinates.y + 1, chunkcoordinates.z);
      }

      setPosition(chunkcoordinates1.x + 0.5F, chunkcoordinates1.y + this.height + 0.1F, chunkcoordinates1.z + 0.5F);
    }

    this.sleeping = false;
    if ((!this.world.isStatic) && (flag1)) {
      this.world.everyoneSleeping();
    }

    if ((getBukkitEntity() instanceof Player)) {
      Player player = (Player)getBukkitEntity();
      org.bukkit.block.Block bed;
      org.bukkit.block.Block bed;
      if (chunkcoordinates != null)
        bed = this.world.getWorld().getBlockAt(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z);
      else {
        bed = this.world.getWorld().getBlockAt(player.getLocation());
      }

      PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed);
      this.world.getServer().getPluginManager().callEvent(event);
    }

    if (flag)
      this.sleepTicks = 0;
    else {
      this.sleepTicks = 100;
    }

    if (flag2)
      setRespawnPosition(this.F);
  }

  private boolean G()
  {
    ChunkCoordinates c = this.F;
    int blockID = this.world.getTypeId(c.x, c.y, c.z);
    return (Block.byId[blockID] != null) && (Block.byId[blockID].isBed(this.world, c.x, c.y, c.z, this));
  }

  public static ChunkCoordinates getBed(World world, ChunkCoordinates chunkcoordinates) {
    IChunkProvider ichunkprovider = world.q();

    ichunkprovider.getChunkAt(chunkcoordinates.x - 3 >> 4, chunkcoordinates.z - 3 >> 4);
    ichunkprovider.getChunkAt(chunkcoordinates.x + 3 >> 4, chunkcoordinates.z - 3 >> 4);
    ichunkprovider.getChunkAt(chunkcoordinates.x - 3 >> 4, chunkcoordinates.z + 3 >> 4);
    ichunkprovider.getChunkAt(chunkcoordinates.x + 3 >> 4, chunkcoordinates.z + 3 >> 4);
    ChunkCoordinates c = chunkcoordinates;
    Block block = Block.byId[world.getTypeId(c.x, c.y, c.z)];
    if ((block == null) || (!block.isBed(world, c.x, c.y, c.z, null))) {
      return null;
    }
    ChunkCoordinates chunkcoordinates1 = block.getBedSpawnPosition(world, c.x, c.y, c.z, null);
    return chunkcoordinates1;
  }

  public boolean isSleeping()
  {
    return this.sleeping;
  }

  public boolean isDeeplySleeping() {
    return (this.sleeping) && (this.sleepTicks >= 100);
  }
  public void a(String s) {
  }

  public ChunkCoordinates getBed() {
    return this.b;
  }

  public void setRespawnPosition(ChunkCoordinates chunkcoordinates) {
    if (chunkcoordinates != null) {
      this.b = new ChunkCoordinates(chunkcoordinates);
      this.spawnWorld = this.world.worldData.name;
    } else {
      this.b = null;
    }
  }

  public void a(Statistic statistic) {
    a(statistic, 1);
  }
  public void a(Statistic statistic, int i) {
  }

  protected void ac() {
    super.ac();
    a(StatisticList.u, 1);
    if (isSprinting())
      c(0.8F);
    else
      c(0.2F);
  }

  public void a(float f, float f1)
  {
    double d0 = this.locX;
    double d1 = this.locY;
    double d2 = this.locZ;

    if (this.abilities.isFlying) {
      double d3 = this.motY;
      float f2 = this.am;

      this.am = 0.05F;
      super.a(f, f1);
      this.motY = (d3 * 0.6D);
      this.am = f2;
    } else {
      super.a(f, f1);
    }

    checkMovement(this.locX - d0, this.locY - d1, this.locZ - d2);
  }

  public void checkMovement(double d0, double d1, double d2) {
    if (this.vehicle == null)
    {
      if (a(Material.WATER)) {
        int i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
        if (i > 0) {
          a(StatisticList.q, i);
          c(0.015F * i * 0.01F);
        }
      } else if (aU()) {
        int i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
        if (i > 0) {
          a(StatisticList.m, i);
          c(0.015F * i * 0.01F);
        }
      } else if (t()) {
        if (d1 > 0.0D)
          a(StatisticList.o, (int)Math.round(d1 * 100.0D));
      }
      else if (this.onGround) {
        int i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
        if (i > 0) {
          a(StatisticList.l, i);
          if (isSprinting())
            c(0.09999999F * i * 0.01F);
          else
            c(0.01F * i * 0.01F);
        }
      }
      else {
        int i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
        if (i > 25)
          a(StatisticList.p, i);
      }
    }
  }

  private void h(double d0, double d1, double d2)
  {
    if (this.vehicle != null) {
      int i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

      if (i > 0)
        if ((this.vehicle instanceof EntityMinecart)) {
          a(StatisticList.r, i);
          if (this.c == null)
            this.c = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
          else if (this.c.b(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) >= 1000.0D)
            a(AchievementList.q, 1);
        }
        else if ((this.vehicle instanceof EntityBoat)) {
          a(StatisticList.s, i);
        } else if ((this.vehicle instanceof EntityPig)) {
          a(StatisticList.t, i);
        }
    }
  }

  protected void a(float f)
  {
    if (!this.abilities.canFly) {
      if (f >= 2.0F) {
        a(StatisticList.n, (int)Math.round(f * 100.0D));
      }

      super.a(f);
    }
  }

  public void c(EntityLiving entityliving) {
    if ((entityliving instanceof EntityMonster))
      a(AchievementList.s);
  }

  public void ad()
  {
    if (this.I > 0)
      this.I = 10;
    else
      this.J = true;
  }

  public void giveExp(int i)
  {
    this.q += i;
    int j = 2147483647 - this.expTotal;

    if (i > j) {
      i = j;
    }

    this.exp += i / getExpToLevel();

    for (this.expTotal += i; this.exp >= 1.0F; this.exp /= getExpToLevel()) {
      this.exp = ((this.exp - 1.0F) * getExpToLevel());
      levelUp();
    }
  }

  public void levelDown(int i) {
    this.expLevel -= i;
    if (this.expLevel < 0)
      this.expLevel = 0;
  }

  public int getExpToLevel()
  {
    return 7 + (this.expLevel * 7 >> 1);
  }

  private void levelUp() {
    this.expLevel += 1;
  }

  public void c(float f) {
    if ((!this.abilities.isInvulnerable) && 
      (!this.world.isStatic))
      this.foodData.a(f);
  }

  public FoodMetaData getFoodData()
  {
    return this.foodData;
  }

  public boolean b(boolean flag) {
    return ((flag) || (this.foodData.b())) && (!this.abilities.isInvulnerable);
  }

  public boolean ag() {
    return (getHealth() > 0) && (getHealth() < getMaxHealth());
  }

  public void a(ItemStack itemstack, int i) {
    if (itemstack != this.d) {
      this.d = itemstack;
      this.e = i;
      if (!this.world.isStatic)
        i(true);
    }
  }

  public boolean d(int i, int j, int k)
  {
    return true;
  }

  protected int getExpValue(EntityHuman entityhuman) {
    int i = this.expLevel * 7;

    return i > 100 ? 100 : i;
  }

  protected boolean alwaysGivesExp() {
    return true;
  }

  public String getLocalizedName() {
    return this.name;
  }
  public void e(int i) {
  }

  public void copyTo(EntityHuman entityhuman) {
    this.inventory.a(entityhuman.inventory);
    this.health = entityhuman.health;
    this.foodData = entityhuman.foodData;
    this.expLevel = entityhuman.expLevel;
    this.expTotal = entityhuman.expTotal;
    this.exp = entityhuman.exp;
    this.q = entityhuman.q;
  }

  protected boolean g_() {
    return !this.abilities.isFlying;
  }

  public void updateAbilities()
  {
  }

  public void openGui(BaseMod mod, int ID, World world, int x, int y, int z)
  {
    if (!(this instanceof EntityPlayer))
    {
      return;
    }
    EntityPlayer player = (EntityPlayer)this;
    if (!(mod instanceof NetworkMod))
    {
      return;
    }
    IGuiHandler handler = MinecraftForge.getGuiHandler(mod);
    if (handler != null)
    {
      Container container = (Container)handler.getGuiElement(ID, player, world, x, y, z);
      if (container != null)
      {
        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if (container != null)
        {
          player.realGetNextWidowId();
          player.H();
          PacketOpenGUI pkt = new PacketOpenGUI(player.getCurrentWindowIdField(), MinecraftForge.getModID((NetworkMod)mod), ID, x, y, z);
          player.netServerHandler.sendPacket(pkt.getPacket());
          this.activeContainer = container;
          this.activeContainer.windowId = player.getCurrentWindowIdField();
          this.activeContainer.addSlotListener(player);
        }
      }
    }
  }
}

