package net.minecraft.server;

import forge.ForgeHooks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.TrigMath;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.plugin.PluginManager;

public abstract class EntityLiving extends Entity
  implements net.minecraft.src.EntityLiving
{
  public int maxNoDamageTicks = 20;
  public float T;
  public float U;
  public float V = 0.0F;
  public float W = 0.0F;
  public float X = 0.0F;
  public float Y = 0.0F;
  protected float Z;
  protected float aa;
  protected float ab;
  protected float ac;
  protected boolean ad = true;
  protected String texture = "/mob/char.png";
  protected boolean af = true;
  protected float ag = 0.0F;
  protected String ah = null;
  protected float ai = 1.0F;
  protected int aj = 0;
  protected float ak = 0.0F;
  public float al = 0.1F;
  public float am = 0.02F;
  public float an;
  public float ao;
  protected int health = getMaxHealth();
  public int aq;
  protected int ar;
  private int a;
  public int hurtTicks;
  public int at;
  public float au = 0.0F;
  public int deathTicks = 0;
  public int attackTicks = 0;
  public float ax;
  public float ay;
  protected boolean az = false;
  protected int aA;
  public int aB = -1;
  public float aC = (float)(Math.random() * 0.8999999761581421D + 0.1000000014901161D);
  public float aD;
  public float aE;
  public float aF;
  public EntityHuman killer = null;
  protected int lastDamageByPlayerTime = 0;
  public EntityLiving lastDamager = null;
  private int c = 0;
  private EntityLiving d = null;
  public int aI = 0;
  public int aJ = 0;
  public HashMap effects = new HashMap();
  public boolean e = true;
  private int f;
  private ControllerLook lookController;
  private ControllerMove moveController;
  private ControllerJump jumpController;
  private EntityAIBodyControl senses;
  private Navigation navigation;
  protected PathfinderGoalSelector goalSelector = new PathfinderGoalSelector();
  protected PathfinderGoalSelector targetSelector = new PathfinderGoalSelector();
  private EntityLiving l;
  private EntitySenses m;
  private float n;
  private ChunkCoordinates o = new ChunkCoordinates(0, 0, 0);
  private float p = -1.0F;
  protected int aN;
  protected double aO;
  protected double aP;
  protected double aQ;
  protected double aR;
  protected double aS;
  float aT = 0.0F;
  public int lastDamage = 0;
  protected int aV = 0;
  protected float aW;
  protected float aX;
  protected float aY;
  protected boolean aZ = false;
  protected float ba = 0.0F;
  protected float bb = 0.7F;
  private int q = 0;
  private Entity r;
  protected int bc = 0;
  public int expToDrop = 0;
  public int maxAirTicks = 300;

  public EntityLiving(World world) {
    super(world);
    this.bf = true;
    this.lookController = new ControllerLook(this);
    this.moveController = new ControllerMove(this);
    this.jumpController = new ControllerJump(this);
    this.senses = new EntityAIBodyControl(this);
    this.navigation = new Navigation(this, world, 16.0F);
    this.m = new EntitySenses(this);
    this.U = ((float)(Math.random() + 1.0D) * 0.01F);
    setPosition(this.locX, this.locY, this.locZ);
    this.T = ((float)Math.random() * 12398.0F);
    this.yaw = (float)(Math.random() * 3.141592741012573D * 2.0D);
    this.X = this.yaw;
    this.bP = 0.5F;
  }

  public ControllerLook getControllerLook() {
    return this.lookController;
  }

  public ControllerMove getControllerMove() {
    return this.moveController;
  }

  public ControllerJump getControllerJump() {
    return this.jumpController;
  }

  public Navigation al() {
    return this.navigation;
  }

  public EntitySenses am() {
    return this.m;
  }

  public Random an() {
    return this.random;
  }

  public EntityLiving ao() {
    return this.lastDamager;
  }

  public EntityLiving ap() {
    return this.d;
  }

  public void g(Entity entity) {
    if ((entity instanceof EntityLiving))
      this.d = ((EntityLiving)entity);
  }

  public int aq()
  {
    return this.aV;
  }

  public float ar() {
    return this.X;
  }

  public float as() {
    return this.n;
  }

  public void d(float f) {
    this.n = f;
    e(f);
  }

  public boolean a(Entity entity) {
    g(entity);
    return false;
  }

  public EntityLiving at() {
    return this.l;
  }

  public void b(EntityLiving entityliving) {
    this.l = entityliving;
    ForgeHooks.onEntityLivingSetAttackTarget(this, entityliving);
  }

  public boolean a(Class oclass) {
    return (EntityCreeper.class != oclass) && (EntityGhast.class != oclass);
  }
  public void z() {
  }

  public boolean au() {
    return e(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
  }

  public boolean e(int i, int j, int k) {
    return this.p == -1.0F;
  }

  public void b(int i, int j, int k, int l) {
    this.o.a(i, j, k);
    this.p = l;
  }

  public ChunkCoordinates av() {
    return this.o;
  }

  public float aw() {
    return this.p;
  }

  public void ax() {
    this.p = -1.0F;
  }

  public boolean ay() {
    return this.p != -1.0F;
  }

  public void a(EntityLiving entityliving) {
    this.lastDamager = entityliving;
    this.c = (this.lastDamager != null ? 60 : 0);
    ForgeHooks.onEntityLivingSetAttackTarget(this, entityliving);
  }

  protected void b() {
    this.datawatcher.a(8, Integer.valueOf(this.f));
  }

  public boolean h(Entity entity) {
    return this.world.a(Vec3D.create(this.locX, this.locY + getHeadHeight(), this.locZ), Vec3D.create(entity.locX, entity.locY + entity.getHeadHeight(), entity.locZ)) == null;
  }

  public boolean o_() {
    return !this.dead;
  }

  public boolean e_() {
    return !this.dead;
  }

  public float getHeadHeight() {
    return this.length * 0.85F;
  }

  public int m() {
    return 80;
  }

  public void az() {
    String s = i();

    if (s != null)
      this.world.makeSound(this, s, p(), A());
  }

  public void aA()
  {
    this.an = this.ao;
    super.aA();

    if ((isAlive()) && (this.random.nextInt(1000) < this.a++)) {
      this.a = (-m());
      az();
    }

    if ((isAlive()) && (inBlock()) && (!(this instanceof EntityEnderDragon))) {
      EntityDamageEvent event = new EntityDamageEvent(getBukkitEntity(), EntityDamageEvent.DamageCause.SUFFOCATION, 1);
      this.world.getServer().getPluginManager().callEvent(event);

      if (!event.isCancelled()) {
        event.getEntity().setLastDamageCause(event);
        damageEntity(DamageSource.STUCK, event.getDamage());
      }

    }

    if ((isFireproof()) || (this.world.isStatic)) {
      extinguish();
    }

    if ((isAlive()) && (a(Material.WATER)) && (!f_()) && (!this.effects.containsKey(Integer.valueOf(MobEffectList.WATER_BREATHING.id)))) {
      setAirTicks(b_(getAirTicks()));
      if (getAirTicks() == -20) {
        setAirTicks(0);

        for (int i = 0; i < 8; i++) {
          float f = this.random.nextFloat() - this.random.nextFloat();
          float f1 = this.random.nextFloat() - this.random.nextFloat();
          float f2 = this.random.nextFloat() - this.random.nextFloat();

          this.world.a("bubble", this.locX + f, this.locY + f1, this.locZ + f2, this.motX, this.motY, this.motZ);
        }

        EntityDamageEvent event = new EntityDamageEvent(getBukkitEntity(), EntityDamageEvent.DamageCause.DROWNING, 2);
        this.world.getServer().getPluginManager().callEvent(event);

        if ((!event.isCancelled()) && (event.getDamage() != 0)) {
          event.getEntity().setLastDamageCause(event);
          damageEntity(DamageSource.DROWN, event.getDamage());
        }

      }

      extinguish();
    }
    else if (getAirTicks() != 300) {
      setAirTicks(this.maxAirTicks);
    }

    this.ax = this.ay;
    if (this.attackTicks > 0) {
      this.attackTicks -= 1;
    }

    if (this.hurtTicks > 0) {
      this.hurtTicks -= 1;
    }

    if (this.noDamageTicks > 0) {
      this.noDamageTicks -= 1;
    }

    if (this.health <= 0) {
      aB();
    }

    if (this.lastDamageByPlayerTime > 0)
      this.lastDamageByPlayerTime -= 1;
    else {
      this.killer = null;
    }

    if ((this.d != null) && (!this.d.isAlive())) {
      this.d = null;
    }

    if (this.lastDamager != null) {
      if (!this.lastDamager.isAlive())
        a((EntityLiving)null);
      else if (this.c > 0)
        this.c -= 1;
      else {
        a((EntityLiving)null);
      }
    }

    aK();
    this.ac = this.ab;
    this.W = this.V;
    this.Y = this.X;
    this.lastYaw = this.yaw;
    this.lastPitch = this.pitch;
  }

  public int getExpReward()
  {
    int exp = getExpValue(this.killer);

    if ((!this.world.isStatic) && ((this.lastDamageByPlayerTime > 0) || (alwaysGivesExp())) && (!isBaby())) {
      return exp;
    }
    return 0;
  }

  protected void aB()
  {
    this.deathTicks += 1;
    if ((this.deathTicks >= 20) && (!this.dead))
    {
      int i = this.expToDrop;
      while (i > 0) {
        int j = EntityExperienceOrb.getOrbValue(i);

        i -= j;
        this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
      }

      aH();
      die();

      for (i = 0; i < 20; i++) {
        double d0 = this.random.nextGaussian() * 0.02D;
        double d1 = this.random.nextGaussian() * 0.02D;
        double d2 = this.random.nextGaussian() * 0.02D;

        this.world.a("explode", this.locX + this.random.nextFloat() * this.width * 2.0F - this.width, this.locY + this.random.nextFloat() * this.length, this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width, d0, d1, d2);
      }
    }
  }

  protected int b_(int i) {
    return i - 1;
  }

  protected int getExpValue(EntityHuman entityhuman) {
    return this.aA;
  }

  protected boolean alwaysGivesExp() {
    return false;
  }

  public void aC() {
    for (int i = 0; i < 20; i++) {
      double d0 = this.random.nextGaussian() * 0.02D;
      double d1 = this.random.nextGaussian() * 0.02D;
      double d2 = this.random.nextGaussian() * 0.02D;
      double d3 = 10.0D;

      this.world.a("explode", this.locX + this.random.nextFloat() * this.width * 2.0F - this.width - d0 * d3, this.locY + this.random.nextFloat() * this.length - d1 * d3, this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width - d2 * d3, d0, d1, d2);
    }
  }

  public void R() {
    super.R();
    this.Z = this.aa;
    this.aa = 0.0F;
    this.fallDistance = 0.0F;
  }

  public void F_() {
    if (ForgeHooks.onEntityLivingUpdate(this))
    {
      return;
    }

    super.F_();
    if (this.aI > 0) {
      if (this.aJ <= 0) {
        this.aJ = 60;
      }

      this.aJ -= 1;
      if (this.aJ <= 0) {
        this.aI -= 1;
      }
    }

    e();
    double d0 = this.locX - this.lastX;
    double d1 = this.locZ - this.lastZ;
    float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
    float f1 = this.V;
    float f2 = 0.0F;

    this.Z = this.aa;
    float f3 = 0.0F;

    if (f > 0.05F) {
      f3 = 1.0F;
      f2 = f * 3.0F;

      f1 = (float)TrigMath.atan2(d1, d0) * 180.0F / 3.141593F - 90.0F;
    }

    if (this.ao > 0.0F) {
      f1 = this.yaw;
    }

    if (!this.onGround) {
      f3 = 0.0F;
    }

    this.aa += (f3 - this.aa) * 0.3F;
    if (c_()) {
      this.senses.a();
    }
    else
    {
      for (float f4 = f1 - this.V; f4 < -180.0F; f4 += 360.0F);
      while (f4 >= 180.0F) {
        f4 -= 360.0F;
      }

      this.V += f4 * 0.3F;

      for (float f5 = this.yaw - this.V; f5 < -180.0F; f5 += 360.0F);
      while (f5 >= 180.0F) {
        f5 -= 360.0F;
      }

      boolean flag = (f5 < -90.0F) || (f5 >= 90.0F);

      if (f5 < -75.0F) {
        f5 = -75.0F;
      }

      if (f5 >= 75.0F) {
        f5 = 75.0F;
      }

      this.V = (this.yaw - f5);
      if (f5 * f5 > 2500.0F) {
        this.V += f5 * 0.2F;
      }

      if (flag) {
        f2 *= -1.0F;
      }
    }

    while (this.yaw - this.lastYaw < -180.0F) {
      this.lastYaw -= 360.0F;
    }

    while (this.yaw - this.lastYaw >= 180.0F) {
      this.lastYaw += 360.0F;
    }

    while (this.V - this.W < -180.0F) {
      this.W -= 360.0F;
    }

    while (this.V - this.W >= 180.0F) {
      this.W += 360.0F;
    }

    while (this.pitch - this.lastPitch < -180.0F) {
      this.lastPitch -= 360.0F;
    }

    while (this.pitch - this.lastPitch >= 180.0F) {
      this.lastPitch += 360.0F;
    }

    while (this.X - this.Y < -180.0F) {
      this.Y -= 360.0F;
    }

    while (this.X - this.Y >= 180.0F) {
      this.Y += 360.0F;
    }

    this.ab += f2;
  }

  protected void b(float f, float f1) {
    super.b(f, f1);
  }

  public void heal(int i)
  {
    heal(i, EntityRegainHealthEvent.RegainReason.CUSTOM);
  }

  public void heal(int i, EntityRegainHealthEvent.RegainReason regainReason) {
    if (this.health > 0) {
      EntityRegainHealthEvent event = new EntityRegainHealthEvent(getBukkitEntity(), i, regainReason);
      this.world.getServer().getPluginManager().callEvent(event);

      if (!event.isCancelled()) {
        this.health += event.getAmount();
      }

      if (this.health > getMaxHealth()) {
        this.health = getMaxHealth();
      }

      this.noDamageTicks = (this.maxNoDamageTicks / 2);
    }
  }

  public abstract int getMaxHealth();

  public int getHealth() {
    return this.health;
  }

  public void setHealth(int i) {
    this.health = i;
    if (i > getMaxHealth())
      i = getMaxHealth();
  }

  public boolean damageEntity(DamageSource damagesource, int i)
  {
    if (ForgeHooks.onEntityLivingAttacked(this, damagesource, i))
    {
      return false;
    }

    if (this.world.isStatic) {
      return false;
    }
    this.aV = 0;
    if (this.health <= 0)
      return false;
    if ((damagesource.k()) && (hasEffect(MobEffectList.FIRE_RESISTANCE))) {
      return false;
    }
    this.aE = 1.5F;
    boolean flag = true;

    if ((damagesource instanceof EntityDamageSource)) {
      EntityDamageEvent event = CraftEventFactory.handleEntityDamageEvent(this, damagesource, i);
      if (event.isCancelled()) {
        return false;
      }
      i = event.getDamage();
    }

    if (this.noDamageTicks > this.maxNoDamageTicks / 2.0F) {
      if (i <= this.lastDamage) {
        return false;
      }

      c(damagesource, i - this.lastDamage);
      this.lastDamage = i;
      flag = false;
    } else {
      this.lastDamage = i;
      this.aq = this.health;
      this.noDamageTicks = this.maxNoDamageTicks;
      c(damagesource, i);
      this.hurtTicks = (this.at = 10);
    }

    this.au = 0.0F;
    Entity entity = damagesource.getEntity();

    if (entity != null) {
      if ((entity instanceof EntityLiving)) {
        a((EntityLiving)entity);
      }

      if ((entity instanceof EntityHuman)) {
        this.lastDamageByPlayerTime = 60;
        this.killer = ((EntityHuman)entity);
      } else if ((entity instanceof EntityWolf)) {
        EntityWolf entitywolf = (EntityWolf)entity;

        if (entitywolf.isTamed()) {
          this.lastDamageByPlayerTime = 60;
          this.killer = null;
        }
      }
    }

    if (flag) {
      this.world.broadcastEntityEffect(this, (byte)2);
      aW();
      if (entity != null) {
        double d0 = entity.locX - this.locX;

        for (double d1 = entity.locZ - this.locZ; d0 * d0 + d1 * d1 < 0.0001D; d1 = (Math.random() - Math.random()) * 0.01D) {
          d0 = (Math.random() - Math.random()) * 0.01D;
        }

        this.au = ((float)(Math.atan2(d1, d0) * 180.0D / 3.141592741012573D) - this.yaw);
        a(entity, i, d0, d1);
      } else {
        this.au = (int)(Math.random() * 2.0D) * 180;
      }
    }

    if (this.health <= 0) {
      if (flag) {
        this.world.makeSound(this, k(), p(), A());
      }

      die(damagesource);
    } else if (flag) {
      this.world.makeSound(this, j(), p(), A());
    }

    return true;
  }

  private float A()
  {
    return isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
  }

  public int T() {
    return 0;
  }
  protected void f(int i) {
  }

  protected int d(DamageSource damagesource, int i) {
    if (!damagesource.ignoresArmor()) {
      int j = 25 - T();
      int k = i * j + this.ar;

      f(i);
      i = k / 25;
      this.ar = (k % 25);
    }

    return i;
  }

  protected int b(DamageSource damagesource, int i) {
    if (hasEffect(MobEffectList.RESISTANCE)) {
      int j = (getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
      int k = 25 - j;
      int l = i * k + this.ar;

      i = l / 25;
      this.ar = (l % 25);
    }

    return i;
  }

  protected void c(DamageSource damagesource, int i) {
    i = ForgeHooks.onEntityLivingHurt(this, damagesource, i);
    if (i == 0)
    {
      return;
    }

    i = d(damagesource, i);
    i = b(damagesource, i);
    this.health -= i;
  }

  protected float p() {
    return 1.0F;
  }

  protected String i() {
    return null;
  }

  protected String j() {
    return "damage.hurtflesh";
  }

  protected String k() {
    return "damage.hurtflesh";
  }

  public void a(Entity entity, int i, double d0, double d1) {
    this.ce = true;
    float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
    float f1 = 0.4F;

    this.motX /= 2.0D;
    this.motY /= 2.0D;
    this.motZ /= 2.0D;
    this.motX -= d0 / f * f1;
    this.motY += f1;
    this.motZ -= d1 / f * f1;
    if (this.motY > 0.4000000059604645D)
      this.motY = 0.4000000059604645D;
  }

  public void die(DamageSource damagesource)
  {
    if (ForgeHooks.onEntityLivingDeath(this, damagesource))
    {
      return;
    }

    Entity entity = damagesource.getEntity();

    if ((this.aj >= 0) && (entity != null)) {
      entity.b(this, this.aj);
    }

    if (entity != null) {
      entity.c(this);
    }

    this.az = true;
    if (!this.world.isStatic) {
      int i = 0;

      if ((entity instanceof EntityHuman)) {
        i = EnchantmentManager.getBonusMonsterLootEnchantmentLevel(((EntityHuman)entity).inventory);
      }

      this.captureDrops = true;
      this.capturedDrops.clear();
      if (!isBaby()) {
        dropDeathLoot(this.lastDamageByPlayerTime > 0, i);
      }
      else
      {
        CraftEventFactory.callEntityDeathEvent(this);
      }

      this.captureDrops = false;
      ForgeHooks.onEntityLivingDrops(this, damagesource, this.capturedDrops, i, this.lastDamageByPlayerTime > 0, 0);
      for (EntityItem item : this.capturedDrops)
      {
        this.world.addEntity(item);
      }
    }

    this.world.broadcastEntityEffect(this, (byte)3);
  }

  protected ItemStack b(int i)
  {
    return null;
  }

  protected void dropDeathLoot(boolean flag, int i)
  {
    int j = getLootId();

    List loot = new ArrayList();

    if (j > 0) {
      int k = this.random.nextInt(3);

      if (i > 0) {
        k += this.random.nextInt(i + 1);
      }

      if (k > 0) {
        loot.add(new org.bukkit.inventory.ItemStack(j, k));
      }

    }

    if (this.lastDamageByPlayerTime > 0) {
      int k = this.random.nextInt(200) - i;

      if (k < 5) {
        ItemStack itemstack = b(k <= 0 ? 1 : 0);
        if (itemstack != null) {
          loot.add(new CraftItemStack(itemstack));
        }
      }
    }

    CraftEventFactory.callEntityDeathEvent(this, loot);
  }

  protected int getLootId()
  {
    return 0;
  }

  protected void a(float f) {
    if (ForgeHooks.onEntityLivingFall(this, f))
    {
      return;
    }

    super.a(f);
    int i = (int)Math.ceil(f - 3.0F);

    if (i > 0)
    {
      EntityDamageEvent event = new EntityDamageEvent(getBukkitEntity(), EntityDamageEvent.DamageCause.FALL, i);
      this.world.getServer().getPluginManager().callEvent(event);

      if ((!event.isCancelled()) && (event.getDamage() != 0)) {
        i = event.getDamage();

        if (i > 4)
          this.world.makeSound(this, "damage.fallbig", 1.0F, 1.0F);
        else {
          this.world.makeSound(this, "damage.fallsmall", 1.0F, 1.0F);
        }
        getBukkitEntity().setLastDamageCause(event);
        damageEntity(DamageSource.FALL, i);
      }

      int j = this.world.getTypeId(MathHelper.floor(this.locX), MathHelper.floor(this.locY - 0.2000000029802322D - this.height), MathHelper.floor(this.locZ));

      if (j > 0) {
        StepSound stepsound = Block.byId[j].stepSound;

        this.world.makeSound(this, stepsound.getName(), stepsound.getVolume1() * 0.5F, stepsound.getVolume2() * 0.75F);
      }
    }
  }

  public void a(float f, float f1)
  {
    if (aU()) {
      double d0 = this.locY;
      a(f, f1, c_() ? 0.04F : 0.02F);
      move(this.motX, this.motY, this.motZ);
      this.motX *= 0.800000011920929D;
      this.motY *= 0.800000011920929D;
      this.motZ *= 0.800000011920929D;
      this.motY -= 0.02D;
      if ((this.positionChanged) && (d(this.motX, this.motY + 0.6000000238418579D - this.locY + d0, this.motZ)))
        this.motY = 0.300000011920929D;
    }
    else if (aV()) {
      double d0 = this.locY;
      a(f, f1, 0.02F);
      move(this.motX, this.motY, this.motZ);
      this.motX *= 0.5D;
      this.motY *= 0.5D;
      this.motZ *= 0.5D;
      this.motY -= 0.02D;
      if ((this.positionChanged) && (d(this.motX, this.motY + 0.6000000238418579D - this.locY + d0, this.motZ)))
        this.motY = 0.300000011920929D;
    }
    else {
      float f2 = 0.91F;

      if (this.onGround) {
        f2 = 0.5460001F;
        int i = this.world.getTypeId(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));

        if (i > 0) {
          f2 = Block.byId[i].frictionFactor * 0.91F;
        }
      }

      float f3 = 0.1627714F / (f2 * f2 * f2);
      float f4;
      if (this.onGround)
      {
        float f4;
        float f4;
        if (c_())
          f4 = as();
        else {
          f4 = this.al;
        }

        f4 *= f3;
      } else {
        f4 = this.am;
      }

      a(f, f1, f4);
      f2 = 0.91F;
      if (this.onGround) {
        f2 = 0.5460001F;
        int j = this.world.getTypeId(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));

        if (j > 0) {
          f2 = Block.byId[j].frictionFactor * 0.91F;
        }
      }

      if (t()) {
        float f5 = 0.15F;

        if (this.motX < -f5) {
          this.motX = -f5;
        }

        if (this.motX > f5) {
          this.motX = f5;
        }

        if (this.motZ < -f5) {
          this.motZ = -f5;
        }

        if (this.motZ > f5) {
          this.motZ = f5;
        }

        this.fallDistance = 0.0F;
        if (this.motY < -0.15D) {
          this.motY = -0.15D;
        }

        boolean flag = (isSneaking()) && ((this instanceof EntityHuman));

        if ((flag) && (this.motY < 0.0D)) {
          this.motY = 0.0D;
        }
      }

      move(this.motX, this.motY, this.motZ);
      if ((this.positionChanged) && (t())) {
        this.motY = 0.2D;
      }

      this.motY -= 0.08D;
      this.motY *= 0.9800000190734863D;
      this.motX *= f2;
      this.motZ *= f2;
    }

    this.aD = this.aE;
    double d0 = this.locX - this.lastX;
    double d1 = this.locZ - this.lastZ;
    float f6 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

    if (f6 > 1.0F) {
      f6 = 1.0F;
    }

    this.aE += (f6 - this.aE) * 0.4F;
    this.aF += this.aE;
  }

  public boolean t() {
    int i = MathHelper.floor(this.locX);
    int j = MathHelper.floor(this.boundingBox.b);
    int k = MathHelper.floor(this.locZ);
    int l = this.world.getTypeId(i, j, k);

    return (l == Block.LADDER.id) || (l == Block.VINE.id);
  }

  public void b(NBTTagCompound nbttagcompound) {
    nbttagcompound.setShort("Health", (short)this.health);
    nbttagcompound.setShort("HurtTime", (short)this.hurtTicks);
    nbttagcompound.setShort("DeathTime", (short)this.deathTicks);
    nbttagcompound.setShort("AttackTime", (short)this.attackTicks);
    if (!this.effects.isEmpty()) {
      NBTTagList nbttaglist = new NBTTagList();
      Iterator iterator = this.effects.values().iterator();

      while (iterator.hasNext()) {
        MobEffect mobeffect = (MobEffect)iterator.next();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.setByte("Id", (byte)mobeffect.getEffectId());
        nbttagcompound1.setByte("Amplifier", (byte)mobeffect.getAmplifier());
        nbttagcompound1.setInt("Duration", mobeffect.getDuration());
        nbttaglist.add(nbttagcompound1);
      }

      nbttagcompound.set("ActiveEffects", nbttaglist);
    }
  }

  public void a(NBTTagCompound nbttagcompound) {
    if (this.health < -32768) {
      this.health = -32768;
    }

    this.health = nbttagcompound.getShort("Health");
    if (!nbttagcompound.hasKey("Health")) {
      this.health = getMaxHealth();
    }

    this.hurtTicks = nbttagcompound.getShort("HurtTime");
    this.deathTicks = nbttagcompound.getShort("DeathTime");
    this.attackTicks = nbttagcompound.getShort("AttackTime");
    if (nbttagcompound.hasKey("ActiveEffects")) {
      NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects");

      for (int i = 0; i < nbttaglist.size(); i++) {
        NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.get(i);
        byte b0 = nbttagcompound1.getByte("Id");
        byte b1 = nbttagcompound1.getByte("Amplifier");
        int j = nbttagcompound1.getInt("Duration");

        this.effects.put(Integer.valueOf(b0), new MobEffect(b0, j, b1));
      }
    }
  }

  public boolean isAlive() {
    return (!this.dead) && (this.health > 0);
  }

  public boolean f_() {
    return false;
  }

  public void e(float f) {
    this.aX = f;
  }

  public void f(boolean flag) {
    this.aZ = flag;
  }

  public void e() {
    if (this.q > 0) {
      this.q -= 1;
    }

    if (this.aN > 0) {
      double d0 = this.locX + (this.aO - this.locX) / this.aN;
      double d1 = this.locY + (this.aP - this.locY) / this.aN;
      double d2 = this.locZ + (this.aQ - this.locZ) / this.aN;

      for (double d3 = this.aR - this.yaw; d3 < -180.0D; d3 += 360.0D);
      while (d3 >= 180.0D) {
        d3 -= 360.0D;
      }

      this.yaw = (float)(this.yaw + d3 / this.aN);
      this.pitch = (float)(this.pitch + (this.aS - this.pitch) / this.aN);
      this.aN -= 1;
      setPosition(d0, d1, d2);
      c(this.yaw, this.pitch);

      if (this.world.getTypeId(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)) != 0) {
        d1 += 1.0D;
        setPosition(d0, d1, d2);
      }

    }

    if (Q()) {
      this.aZ = false;
      this.aW = 0.0F;
      this.aX = 0.0F;
      this.aY = 0.0F;
    } else if (aF()) {
      if (c_())
      {
        z_();
      }
      else
      {
        d_();

        this.X = this.yaw;
      }

    }

    boolean flag = aU();
    boolean flag1 = aV();

    if (this.aZ) {
      if (flag) {
        this.motY += 0.03999999910593033D;
      } else if (flag1) {
        this.motY += 0.03999999910593033D;
      } else if ((this.onGround) && (this.q == 0)) {
        ac();
        this.q = 10;
      }
    }
    else this.q = 0;

    this.aW *= 0.98F;
    this.aX *= 0.98F;
    this.aY *= 0.9F;
    float f = this.al;

    this.al *= J();
    a(this.aW, this.aX);
    this.al = f;

    List list1 = this.world.getEntities(this, this.boundingBox.grow(0.2000000029802322D, 0.0D, 0.2000000029802322D));

    if ((list1 != null) && (list1.size() > 0))
      for (int j = 0; j < list1.size(); j++) {
        Entity entity = (Entity)list1.get(j);

        if (entity.e_())
          entity.collide(this);
      }
  }

  protected boolean c_()
  {
    return false;
  }

  protected boolean aF() {
    return !this.world.isStatic;
  }

  protected boolean Q() {
    return this.health <= 0;
  }

  public boolean P() {
    return false;
  }

  protected void ac() {
    this.motY = 0.4199999868869782D;
    if (hasEffect(MobEffectList.JUMP)) {
      this.motY += getEffect(MobEffectList.JUMP).getAmplifier() + 1 * 0.1F;
    }

    if (isSprinting()) {
      float f = this.yaw * 0.01745329F;

      this.motX -= MathHelper.sin(f) * 0.2F;
      this.motZ += MathHelper.cos(f) * 0.2F;
    }

    this.ce = true;

    ForgeHooks.onEntityLivingJump(this);
  }

  protected boolean n() {
    return true;
  }

  protected void aG() {
    EntityHuman entityhuman = this.world.findNearbyPlayer(this, -1.0D);

    if (entityhuman != null) {
      double d0 = entityhuman.locX - this.locX;
      double d1 = entityhuman.locY - this.locY;
      double d2 = entityhuman.locZ - this.locZ;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;

      if ((n()) && (d3 > 16384.0D)) {
        die();
      }

      if ((this.aV > 600) && (this.random.nextInt(800) == 0) && (d3 > 1024.0D) && (n()))
        die();
      else if (d3 < 1024.0D)
        this.aV = 0;
    }
  }

  protected void z_()
  {
    this.aV += 1;

    aG();

    this.m.a();

    this.targetSelector.a();

    this.goalSelector.a();

    this.navigation.d();

    g();

    this.moveController.c();
    this.lookController.a();
    this.jumpController.b();
  }

  protected void g() {
  }

  protected void d_() {
    this.aV += 1;
    aG();
    this.aW = 0.0F;
    this.aX = 0.0F;
    float f = 8.0F;

    if (this.random.nextFloat() < 0.02F) {
      EntityHuman entityhuman = this.world.findNearbyPlayer(this, f);

      if (entityhuman != null) {
        this.r = entityhuman;
        this.bc = (10 + this.random.nextInt(20));
      } else {
        this.aY = ((this.random.nextFloat() - 0.5F) * 20.0F);
      }
    }

    if (this.r != null) {
      a(this.r, 10.0F, D());
      if ((this.bc-- <= 0) || (this.r.dead) || (this.r.j(this) > f * f))
        this.r = null;
    }
    else {
      if (this.random.nextFloat() < 0.05F) {
        this.aY = ((this.random.nextFloat() - 0.5F) * 20.0F);
      }

      this.yaw += this.aY;
      this.pitch = this.ba;
    }

    boolean flag = aU();
    boolean flag1 = aV();

    if ((flag) || (flag1))
      this.aZ = (this.random.nextFloat() < 0.8F);
  }

  public int D()
  {
    return 40;
  }

  public void a(Entity entity, float f, float f1) {
    double d0 = entity.locX - this.locX;
    double d1 = entity.locZ - this.locZ;
    double d2;
    double d2;
    if ((entity instanceof EntityLiving)) {
      EntityLiving entityliving = (EntityLiving)entity;

      d2 = this.locY + getHeadHeight() - (entityliving.locY + entityliving.getHeadHeight());
    } else {
      d2 = (entity.boundingBox.b + entity.boundingBox.e) / 2.0D - (this.locY + getHeadHeight());
    }

    double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1);
    float f2 = (float)(Math.atan2(d1, d0) * 180.0D / 3.141592741012573D) - 90.0F;
    float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / 3.141592741012573D));

    this.pitch = (-b(this.pitch, f3, f1));
    this.yaw = b(this.yaw, f2, f);
  }

  private float b(float f, float f1, float f2)
  {
    for (float f3 = f1 - f; f3 < -180.0F; f3 += 360.0F);
    while (f3 >= 180.0F) {
      f3 -= 360.0F;
    }

    if (f3 > f2) {
      f3 = f2;
    }

    if (f3 < -f2) {
      f3 = -f2;
    }

    return f + f3;
  }
  public void aH() {
  }

  public boolean canSpawn() {
    return (this.world.containsEntity(this.boundingBox)) && (this.world.getCubes(this, this.boundingBox).size() == 0) && (!this.world.containsLiquid(this.boundingBox));
  }

  protected void aI()
  {
    EntityDamageByBlockEvent event = new EntityDamageByBlockEvent(null, getBukkitEntity(), EntityDamageEvent.DamageCause.VOID, 4);
    this.world.getServer().getPluginManager().callEvent(event);

    if ((event.isCancelled()) || (event.getDamage() == 0)) {
      return;
    }

    event.getEntity().setLastDamageCause(event);
    damageEntity(DamageSource.OUT_OF_WORLD, event.getDamage());
  }

  public Vec3D aJ()
  {
    return f(1.0F);
  }

  public Vec3D f(float f)
  {
    if (f == 1.0F) {
      float f1 = MathHelper.cos(-this.yaw * 0.01745329F - 3.141593F);
      float f2 = MathHelper.sin(-this.yaw * 0.01745329F - 3.141593F);
      float f3 = -MathHelper.cos(-this.pitch * 0.01745329F);
      float f4 = MathHelper.sin(-this.pitch * 0.01745329F);
      return Vec3D.create(f2 * f3, f4, f1 * f3);
    }
    float f1 = this.lastPitch + (this.pitch - this.lastPitch) * f;
    float f2 = this.lastYaw + (this.yaw - this.lastYaw) * f;
    float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
    float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
    float f5 = -MathHelper.cos(-f1 * 0.01745329F);
    float f6 = MathHelper.sin(-f1 * 0.01745329F);

    return Vec3D.create(f4 * f5, f6, f3 * f5);
  }

  public int q()
  {
    return 4;
  }

  public boolean isSleeping() {
    return false;
  }

  protected void aK() {
    Iterator iterator = this.effects.keySet().iterator();

    while (iterator.hasNext()) {
      Integer integer = (Integer)iterator.next();
      MobEffect mobeffect = (MobEffect)this.effects.get(integer);

      if ((!mobeffect.tick(this)) && (!this.world.isStatic)) {
        iterator.remove();
        d(mobeffect);
      }

    }

    if (this.e) {
      if (!this.world.isStatic) {
        if (!this.effects.isEmpty()) {
          int i = PotionBrewer.a(this.effects.values());
          this.datawatcher.watch(8, Integer.valueOf(i));
        } else {
          this.datawatcher.watch(8, Integer.valueOf(0));
        }
      }

      this.e = false;
    }

    if (this.random.nextBoolean()) {
      int i = this.datawatcher.getInt(8);
      if (i > 0) {
        double d0 = i >> 16 & 0xFF / 255.0D;
        double d1 = i >> 8 & 0xFF / 255.0D;
        double d2 = i >> 0 & 0xFF / 255.0D;

        this.world.a("mobSpell", this.locX + (this.random.nextDouble() - 0.5D) * this.width, this.locY + this.random.nextDouble() * this.length - this.height, this.locZ + (this.random.nextDouble() - 0.5D) * this.width, d0, d1, d2);
      }
    }
  }

  public void aL() {
    Iterator iterator = this.effects.keySet().iterator();

    while (iterator.hasNext()) {
      Integer integer = (Integer)iterator.next();
      MobEffect mobeffect = (MobEffect)this.effects.get(integer);

      if (!this.world.isStatic) {
        iterator.remove();
        d(mobeffect);
      }
    }
  }

  public Collection getEffects() {
    return this.effects.values();
  }

  public boolean hasEffect(MobEffectList mobeffectlist) {
    return this.effects.containsKey(Integer.valueOf(mobeffectlist.id));
  }

  public MobEffect getEffect(MobEffectList mobeffectlist) {
    return (MobEffect)this.effects.get(Integer.valueOf(mobeffectlist.id));
  }

  public void addEffect(MobEffect mobeffect) {
    if (a(mobeffect))
      if (this.effects.containsKey(Integer.valueOf(mobeffect.getEffectId()))) {
        ((MobEffect)this.effects.get(Integer.valueOf(mobeffect.getEffectId()))).a(mobeffect);
        c((MobEffect)this.effects.get(Integer.valueOf(mobeffect.getEffectId())));
      } else {
        this.effects.put(Integer.valueOf(mobeffect.getEffectId()), mobeffect);
        b(mobeffect);
      }
  }

  public boolean a(MobEffect mobeffect)
  {
    if (getMonsterType() == MonsterType.UNDEAD) {
      int i = mobeffect.getEffectId();

      if ((i == MobEffectList.REGENERATION.id) || (i == MobEffectList.POISON.id)) {
        return false;
      }
    }

    return true;
  }

  public boolean aN() {
    return getMonsterType() == MonsterType.UNDEAD;
  }

  protected void b(MobEffect mobeffect) {
    this.e = true;
  }

  protected void c(MobEffect mobeffect) {
    this.e = true;
  }

  protected void d(MobEffect mobeffect) {
    this.e = true;
  }

  protected float J() {
    float f = 1.0F;

    if (hasEffect(MobEffectList.FASTER_MOVEMENT)) {
      f *= (1.0F + 0.2F * getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier() + 1);
    }

    if (hasEffect(MobEffectList.SLOWER_MOVEMENT)) {
      f *= (1.0F - 0.15F * getEffect(MobEffectList.SLOWER_MOVEMENT).getAmplifier() + 1);
    }

    return f;
  }

  public void enderTeleportTo(double d0, double d1, double d2) {
    setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
  }

  public boolean isBaby() {
    return false;
  }

  public MonsterType getMonsterType() {
    return MonsterType.UNDEFINED;
  }

  public void c(ItemStack itemstack) {
    this.world.makeSound(this, "random.break", 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);

    for (int i = 0; i < 5; i++) {
      Vec3D vec3d = Vec3D.create((this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

      vec3d.a(-this.pitch * 3.141593F / 180.0F);
      vec3d.b(-this.yaw * 3.141593F / 180.0F);
      Vec3D vec3d1 = Vec3D.create((this.random.nextFloat() - 0.5D) * 0.3D, -this.random.nextFloat() * 0.6D - 0.3D, 0.6D);

      vec3d1.a(-this.pitch * 3.141593F / 180.0F);
      vec3d1.b(-this.yaw * 3.141593F / 180.0F);
      vec3d1 = vec3d1.add(this.locX, this.locY + getHeadHeight(), this.locZ);
      this.world.a("iconcrack_" + itemstack.getItem().id, vec3d1.a, vec3d1.b, vec3d1.c, vec3d.a, vec3d.b + 0.05D, vec3d.c);
    }
  }
}

