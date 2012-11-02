package net.minecraft.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.PortalType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.plugin.PluginManager;

public class EntityEnderDragon extends EntityComplex
{
  public double a;
  public double b;
  public double c;
  public double[][] d = new double[64][3];
  public int e = -1;
  public EntityComplexPart[] children;
  public EntityComplexPart g;
  public EntityComplexPart h;
  public EntityComplexPart i;
  public EntityComplexPart j;
  public EntityComplexPart k;
  public EntityComplexPart l;
  public EntityComplexPart m;
  public float n = 0.0F;
  public float o = 0.0F;
  public boolean p = false;
  public boolean q = false;
  private Entity u;
  public int r = 0;
  public EntityEnderCrystal s = null;

  public EntityEnderDragon(World world) {
    super(world);
    EntityComplexPart[] tmp57_54 = new EntityComplexPart[7];
    void tmp74_71 = new EntityComplexPart(this, "head", 6.0F, 6.0F); this.g = tmp74_71; tmp57_54[0] = tmp74_71;
    EntityComplexPart[] tmp79_57 = tmp57_54;
    void tmp96_93 = new EntityComplexPart(this, "body", 8.0F, 8.0F); this.h = tmp96_93; tmp79_57[1] = tmp96_93;
    EntityComplexPart[] tmp101_79 = tmp79_57;
    void tmp118_115 = new EntityComplexPart(this, "tail", 4.0F, 4.0F); this.i = tmp118_115; tmp101_79[2] = tmp118_115;
    EntityComplexPart[] tmp123_101 = tmp101_79;
    void tmp140_137 = new EntityComplexPart(this, "tail", 4.0F, 4.0F); this.j = tmp140_137; tmp123_101[3] = tmp140_137;
    EntityComplexPart[] tmp145_123 = tmp123_101;
    void tmp162_159 = new EntityComplexPart(this, "tail", 4.0F, 4.0F); this.k = tmp162_159; tmp145_123[4] = tmp162_159;
    EntityComplexPart[] tmp167_145 = tmp145_123;
    void tmp184_181 = new EntityComplexPart(this, "wing", 4.0F, 4.0F); this.l = tmp184_181; tmp167_145[5] = tmp184_181;
    EntityComplexPart[] tmp189_167 = tmp167_145;
    void tmp207_204 = new EntityComplexPart(this, "wing", 4.0F, 4.0F); this.m = tmp207_204; tmp189_167[6] = tmp207_204; this.children = tmp189_167;
    this.t = 200;
    setHealth(this.t);
    this.texture = "/mob/enderdragon/ender.png";
    b(16.0F, 8.0F);
    this.bQ = true;
    this.fireProof = true;
    this.b = 100.0D;
    this.cd = true;
  }

  protected void b() {
    super.b();
    this.datawatcher.a(16, new Integer(this.t));
  }

  public double[] a(int i, float f) {
    if (this.health <= 0) {
      f = 0.0F;
    }

    f = 1.0F - f;
    int j = this.e - i * 1 & 0x3F;
    int k = this.e - i * 1 - 1 & 0x3F;
    double[] adouble = new double[3];
    double d0 = this.d[j][0];

    for (double d1 = this.d[k][0] - d0; d1 < -180.0D; d1 += 360.0D);
    while (d1 >= 180.0D) {
      d1 -= 360.0D;
    }

    adouble[0] = (d0 + d1 * f);
    d0 = this.d[j][1];
    d1 = this.d[k][1] - d0;
    adouble[1] = (d0 + d1 * f);
    adouble[2] = (this.d[j][2] + (this.d[k][2] - this.d[j][2]) * f);
    return adouble;
  }

  public void e() {
    this.n = this.o;
    if (!this.world.isStatic) {
      this.datawatcher.watch(16, Integer.valueOf(this.health));
    }

    if (this.health <= 0) {
      float f = (this.random.nextFloat() - 0.5F) * 8.0F;
      float d05 = (this.random.nextFloat() - 0.5F) * 4.0F;
      float f1 = (this.random.nextFloat() - 0.5F) * 8.0F;
      this.world.a("largeexplode", this.locX + f, this.locY + 2.0D + d05, this.locZ + f1, 0.0D, 0.0D, 0.0D);
    } else {
      A();
      float f = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 10.0F + 1.0F);
      f *= (float)Math.pow(2.0D, this.motY);
      if (this.q)
        this.o += f * 0.5F;
      else {
        this.o += f;
      }

      while (this.yaw >= 180.0F) {
        this.yaw -= 360.0F;
      }

      while (this.yaw < -180.0F) {
        this.yaw += 360.0F;
      }

      if (this.e < 0) {
        for (int i = 0; i < this.d.length; i++) {
          this.d[i][0] = this.yaw;
          this.d[i][1] = this.locY;
        }
      }

      if (++this.e == this.d.length) {
        this.e = 0;
      }

      this.d[this.e][0] = this.yaw;
      this.d[this.e][1] = this.locY;

      if (this.world.isStatic) {
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
        }
      } else {
        double d0 = this.a - this.locX;
        double d1 = this.b - this.locY;
        double d2 = this.c - this.locZ;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        if (this.u != null) {
          this.a = this.u.locX;
          this.c = this.u.locZ;
          double d4 = this.a - this.locX;
          double d5 = this.c - this.locZ;
          double d6 = Math.sqrt(d4 * d4 + d5 * d5);
          double d7 = 0.4000000059604645D + d6 / 80.0D - 1.0D;

          if (d7 > 10.0D) {
            d7 = 10.0D;
          }

          this.b = (this.u.boundingBox.b + d7);
        } else {
          this.a += this.random.nextGaussian() * 2.0D;
          this.c += this.random.nextGaussian() * 2.0D;
        }

        if ((this.p) || (d3 < 100.0D) || (d3 > 22500.0D) || (this.positionChanged) || (this.bz)) {
          E();
        }

        d1 /= MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f3 = 0.6F;
        if (d1 < -f3) {
          d1 = -f3;
        }

        if (d1 > f3) {
          d1 = f3;
        }

        for (this.motY += d1 * 0.1000000014901161D; this.yaw < -180.0F; this.yaw += 360.0F);
        while (this.yaw >= 180.0F) {
          this.yaw -= 360.0F;
        }

        double d8 = 180.0D - Math.atan2(d0, d2) * 180.0D / 3.141592741012573D;

        for (double d9 = d8 - this.yaw; d9 < -180.0D; d9 += 360.0D);
        while (d9 >= 180.0D) {
          d9 -= 360.0D;
        }

        if (d9 > 50.0D) {
          d9 = 50.0D;
        }

        if (d9 < -50.0D) {
          d9 = -50.0D;
        }

        Vec3D vec3d = Vec3D.create(this.a - this.locX, this.b - this.locY, this.c - this.locZ).b();
        Vec3D vec3d1 = Vec3D.create(MathHelper.sin(this.yaw * 3.141593F / 180.0F), this.motY, -MathHelper.cos(this.yaw * 3.141593F / 180.0F)).b();
        float f4 = (float)(vec3d1.a(vec3d) + 0.5D) / 1.5F;

        if (f4 < 0.0F) {
          f4 = 0.0F;
        }

        this.aY *= 0.8F;
        float f5 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0F + 1.0F;
        double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0D + 1.0D;

        if (d10 > 40.0D) {
          d10 = 40.0D;
        }

        this.aY = (float)(this.aY + d9 * (0.699999988079071D / d10 / f5));
        this.yaw += this.aY * 0.1F;
        float f6 = (float)(2.0D / (d10 + 1.0D));
        float f7 = 0.06F;

        a(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
        if (this.q)
          move(this.motX * 0.800000011920929D, this.motY * 0.800000011920929D, this.motZ * 0.800000011920929D);
        else {
          move(this.motX, this.motY, this.motZ);
        }

        Vec3D vec3d2 = Vec3D.create(this.motX, this.motY, this.motZ).b();
        float f8 = (float)(vec3d2.a(vec3d1) + 1.0D) / 2.0F;

        f8 = 0.8F + 0.15F * f8;
        this.motX *= f8;
        this.motZ *= f8;
        this.motY *= 0.910000026226044D;
      }

      this.V = this.yaw;
      this.g.width = (this.g.length = 3.0F);
      this.i.width = (this.i.length = 2.0F);
      this.j.width = (this.j.length = 2.0F);
      this.k.width = (this.k.length = 2.0F);
      this.h.length = 3.0F;
      this.h.width = 5.0F;
      this.l.length = 2.0F;
      this.l.width = 4.0F;
      this.m.length = 3.0F;
      this.m.width = 4.0F;
      float d05 = (float)(a(5, 1.0F)[1] - a(10, 1.0F)[1]) * 10.0F / 180.0F * 3.141593F;
      float f1 = MathHelper.cos(d05);
      float f9 = -MathHelper.sin(d05);
      float f10 = this.yaw * 3.141593F / 180.0F;
      float f11 = MathHelper.sin(f10);
      float f12 = MathHelper.cos(f10);

      this.h.F_();
      this.h.setPositionRotation(this.locX + f11 * 0.5F, this.locY, this.locZ - f12 * 0.5F, 0.0F, 0.0F);
      this.l.F_();
      this.l.setPositionRotation(this.locX + f12 * 4.5F, this.locY + 2.0D, this.locZ + f11 * 4.5F, 0.0F, 0.0F);
      this.m.F_();
      this.m.setPositionRotation(this.locX - f12 * 4.5F, this.locY + 2.0D, this.locZ - f11 * 4.5F, 0.0F, 0.0F);
      if (!this.world.isStatic) {
        C();
      }

      if ((!this.world.isStatic) && (this.hurtTicks == 0)) {
        a(this.world.getEntities(this, this.l.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
        a(this.world.getEntities(this, this.m.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
        b(this.world.getEntities(this, this.g.boundingBox.grow(1.0D, 1.0D, 1.0D)));
      }

      double[] adouble = a(5, 1.0F);
      double[] adouble1 = a(0, 1.0F);

      float f3 = MathHelper.sin(this.yaw * 3.141593F / 180.0F - this.aY * 0.01F);
      float f13 = MathHelper.cos(this.yaw * 3.141593F / 180.0F - this.aY * 0.01F);

      this.g.F_();
      this.g.setPositionRotation(this.locX + f3 * 5.5F * f1, this.locY + (adouble1[1] - adouble[1]) * 1.0D + f9 * 5.5F, this.locZ - f13 * 5.5F * f1, 0.0F, 0.0F);

      for (int j = 0; j < 3; j++) {
        EntityComplexPart entitycomplexpart = null;

        if (j == 0) {
          entitycomplexpart = this.i;
        }

        if (j == 1) {
          entitycomplexpart = this.j;
        }

        if (j == 2) {
          entitycomplexpart = this.k;
        }

        double[] adouble2 = a(12 + j * 2, 1.0F);
        float f14 = this.yaw * 3.141593F / 180.0F + a(adouble2[0] - adouble[0]) * 3.141593F / 180.0F * 1.0F;
        float f15 = MathHelper.sin(f14);
        float f16 = MathHelper.cos(f14);
        float f17 = 1.5F;
        float f18 = j + 1 * 2.0F;

        entitycomplexpart.F_();
        entitycomplexpart.setPositionRotation(this.locX - (f11 * f17 + f15 * f18) * f1, this.locY + (adouble2[1] - adouble[1]) * 1.0D - (f18 + f17) * f9 + 1.5D, this.locZ + (f12 * f17 + f16 * f18) * f1, 0.0F, 0.0F);
      }

      if (!this.world.isStatic)
        this.q = (a(this.g.boundingBox) | a(this.h.boundingBox));
    }
  }

  private void A()
  {
    if (this.s != null) {
      if (this.s.dead) {
        if (!this.world.isStatic) {
          a(this.g, DamageSource.EXPLOSION, 10);
        }

        this.s = null;
      } else if ((this.ticksLived % 10 == 0) && (this.health < this.t))
      {
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(getBukkitEntity(), 1, EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
        this.world.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
          this.health += event.getAmount();
        }
      }

    }

    if (this.random.nextInt(10) == 0) {
      float f = 32.0F;
      List list = this.world.a(EntityEnderCrystal.class, this.boundingBox.grow(f, f, f));
      EntityEnderCrystal entityendercrystal = null;
      double d0 = 1.7976931348623157E+308D;
      Iterator iterator = list.iterator();

      while (iterator.hasNext()) {
        Entity entity = (Entity)iterator.next();
        double d1 = entity.j(this);

        if (d1 < d0) {
          d0 = d1;
          entityendercrystal = (EntityEnderCrystal)entity;
        }
      }

      this.s = entityendercrystal;
    }
  }

  private void C() {
  }

  private void a(List list) { double d0 = (this.h.boundingBox.a + this.h.boundingBox.d) / 2.0D;
    double d1 = (this.h.boundingBox.c + this.h.boundingBox.f) / 2.0D;
    Iterator iterator = list.iterator();

    while (iterator.hasNext()) {
      Entity entity = (Entity)iterator.next();

      if ((entity instanceof EntityLiving)) {
        double d2 = entity.locX - d0;
        double d3 = entity.locZ - d1;
        double d4 = d2 * d2 + d3 * d3;

        entity.b_(d2 / d4 * 4.0D, 0.2000000029802322D, d3 / d4 * 4.0D);
      }
    } }

  private void b(List list)
  {
    for (int i = 0; i < list.size(); i++) {
      Entity entity = (Entity)list.get(i);

      if ((entity instanceof EntityLiving))
      {
        if (!(entity instanceof EntityHuman)) {
          EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, 10);
          Bukkit.getPluginManager().callEvent(damageEvent);

          if (!damageEvent.isCancelled()) {
            entity.getBukkitEntity().setLastDamageCause(damageEvent);
            entity.damageEntity(DamageSource.mobAttack(this), damageEvent.getDamage());
          }
        } else {
          entity.damageEntity(DamageSource.mobAttack(this), 10);
        }
      }
    }
  }

  private void E()
  {
    this.p = false;
    if ((this.random.nextInt(2) == 0) && (this.world.players.size() > 0)) {
      this.u = ((Entity)this.world.players.get(this.random.nextInt(this.world.players.size())));
    } else {
      boolean flag = false;
      do
      {
        this.a = 0.0D;
        this.b = 70.0F + this.random.nextFloat() * 50.0F;
        this.c = 0.0D;
        this.a += this.random.nextFloat() * 120.0F - 60.0F;
        this.c += this.random.nextFloat() * 120.0F - 60.0F;
        double d0 = this.locX - this.a;
        double d1 = this.locY - this.b;
        double d2 = this.locZ - this.c;

        flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
      }while (!flag);

      this.u = null;
    }
  }

  private float a(double d0) {
    while (d0 >= 180.0D) {
      d0 -= 360.0D;
    }

    while (d0 < -180.0D) {
      d0 += 360.0D;
    }

    return (float)d0;
  }

  private boolean a(AxisAlignedBB axisalignedbb) {
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.b);
    int k = MathHelper.floor(axisalignedbb.c);
    int l = MathHelper.floor(axisalignedbb.d);
    int i1 = MathHelper.floor(axisalignedbb.e);
    int j1 = MathHelper.floor(axisalignedbb.f);
    boolean flag = false;
    boolean flag1 = false;

    List destroyedBlocks = new ArrayList();
    CraftWorld craftWorld = this.world.getWorld();

    for (int k1 = i; k1 <= l; k1++) {
      for (int l1 = j; l1 <= i1; l1++) {
        for (int i2 = k; i2 <= j1; i2++) {
          int j2 = this.world.getTypeId(k1, l1, i2);

          if (j2 != 0) {
            if ((j2 != Block.OBSIDIAN.id) && (j2 != Block.WHITESTONE.id) && (j2 != Block.BEDROCK.id)) {
              flag1 = true;

              destroyedBlocks.add(craftWorld.getBlockAt(k1, l1, i2));
            }
            else {
              flag = true;
            }
          }
        }
      }
    }

    if (flag1)
    {
      org.bukkit.entity.Entity bukkitEntity = getBukkitEntity();
      EntityExplodeEvent event = new EntityExplodeEvent(bukkitEntity, bukkitEntity.getLocation(), destroyedBlocks, 0.0F);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled())
      {
        return flag;
      }
      for (org.bukkit.block.Block block : event.blockList()) {
        craftWorld.explodeBlock(block, event.getYield());
      }

      double d0 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * this.random.nextFloat();
      double d1 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * this.random.nextFloat();
      double d2 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * this.random.nextFloat();

      this.world.a("largeexplode", d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    return flag;
  }

  public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, int i) {
    if (entitycomplexpart != this.g) {
      i = i / 4 + 1;
    }

    float f = this.yaw * 3.141593F / 180.0F;
    float f1 = MathHelper.sin(f);
    float f2 = MathHelper.cos(f);

    this.a = (this.locX + f1 * 5.0F + (this.random.nextFloat() - 0.5F) * 2.0F);
    this.b = (this.locY + this.random.nextFloat() * 3.0F + 1.0D);
    this.c = (this.locZ - f2 * 5.0F + (this.random.nextFloat() - 0.5F) * 2.0F);
    this.u = null;
    if (((damagesource.getEntity() instanceof EntityHuman)) || (damagesource == DamageSource.EXPLOSION)) {
      dealDamage(damagesource, i);
    }

    return true;
  }

  protected void aB() {
    this.r += 1;
    if ((this.r >= 180) && (this.r <= 200)) {
      float f = (this.random.nextFloat() - 0.5F) * 8.0F;
      float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
      float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

      this.world.a("hugeexplosion", this.locX + f, this.locY + 2.0D + f1, this.locZ + f2, 0.0D, 0.0D, 0.0D);
    }

    if ((!this.world.isStatic) && (this.r > 150) && (this.r % 5 == 0)) {
      int i = this.expToDrop / 20;

      while (i > 0) {
        int j = EntityExperienceOrb.getOrbValue(i);
        i -= j;
        this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
      }
    }

    move(0.0D, 0.1000000014901161D, 0.0D);
    this.V = (this.yaw += 20.0F);
    if (this.r == 200) {
      int i = this.expToDrop - 10 * (this.expToDrop / 20);

      while (i > 0) {
        int j = EntityExperienceOrb.getOrbValue(i);
        i -= j;
        this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
      }

      a(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));
      aH();
      die();
    }
  }

  private void a(int i, int j) {
    byte b0 = 64;

    BlockEnderPortal.a = true;
    byte b1 = 4;

    BlockStateListPopulator world = new BlockStateListPopulator(this.world.getWorld());

    for (int k = b0 - 1; k <= b0 + 32; k++) {
      for (int l = i - b1; l <= i + b1; l++) {
        for (int i1 = j - b1; i1 <= j + b1; i1++) {
          double d0 = l - i;
          double d1 = i1 - j;
          double d2 = MathHelper.sqrt(d0 * d0 + d1 * d1);

          if (d2 <= b1 - 0.5D) {
            if (k < b0) {
              if (d2 <= b1 - 1 - 0.5D)
                world.setTypeId(l, k, i1, Block.BEDROCK.id);
            }
            else if (k > b0)
              world.setTypeId(l, k, i1, 0);
            else if (d2 > b1 - 1 - 0.5D)
              world.setTypeId(l, k, i1, Block.BEDROCK.id);
            else {
              world.setTypeId(l, k, i1, Block.ENDER_PORTAL.id);
            }
          }
        }
      }
    }

    world.setTypeId(i, b0 + 0, j, Block.BEDROCK.id);
    world.setTypeId(i, b0 + 1, j, Block.BEDROCK.id);
    world.setTypeId(i, b0 + 2, j, Block.BEDROCK.id);
    world.setTypeId(i - 1, b0 + 2, j, Block.TORCH.id);
    world.setTypeId(i + 1, b0 + 2, j, Block.TORCH.id);
    world.setTypeId(i, b0 + 2, j - 1, Block.TORCH.id);
    world.setTypeId(i, b0 + 2, j + 1, Block.TORCH.id);
    world.setTypeId(i, b0 + 3, j, Block.BEDROCK.id);
    world.setTypeId(i, b0 + 4, j, Block.DRAGON_EGG.id);

    EntityCreatePortalEvent event = new EntityCreatePortalEvent((LivingEntity)getBukkitEntity(), Collections.unmodifiableList(world.getList()), PortalType.ENDER);
    this.world.getServer().getPluginManager().callEvent(event);

    if (!event.isCancelled()) {
      for (BlockState state : event.getBlocks())
        state.update(true);
    }
    else
      for (BlockState state : event.getBlocks()) {
        packet = new Packet53BlockChange(state.getX(), state.getY(), state.getZ(), this.world);
        for (it = this.world.players.iterator(); it.hasNext(); ) {
          EntityHuman entity = (EntityHuman)it.next();
          if ((entity instanceof EntityPlayer))
            ((EntityPlayer)entity).netServerHandler.sendPacket(packet);
        }
      }
    Packet53BlockChange packet;
    Iterator it;
    BlockEnderPortal.a = false;
  }
  protected void aG() {
  }

  public Entity[] bb() {
    return this.children;
  }

  public boolean o_() {
    return false;
  }

  public int getExpReward()
  {
    return 20000;
  }
}

