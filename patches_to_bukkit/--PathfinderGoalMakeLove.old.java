package net.minecraft.server;

import java.util.Random;

public class PathfinderGoalMakeLove extends PathfinderGoal
{
  private EntityVillager b;
  private EntityVillager c;
  private World d;
  private int e = 0;
  Village a;

  public PathfinderGoalMakeLove(EntityVillager paramEntityVillager)
  {
    this.b = paramEntityVillager;
    this.d = paramEntityVillager.world;
    a(3);
  }

  public boolean a() {
    if (this.b.getAge() != 0) return false;
    if (this.b.an().nextInt(500) != 0) return false;

    this.a = this.d.villages.getClosestVillage(MathHelper.floor(this.b.locX), MathHelper.floor(this.b.locY), MathHelper.floor(this.b.locZ), 0);
    if (this.a == null) return false;
    if (!f()) return false;

    Entity localEntity = this.d.a(EntityVillager.class, this.b.boundingBox.grow(8.0D, 3.0D, 8.0D), this.b);
    if (localEntity == null) return false;

    this.c = ((EntityVillager)localEntity);
    if (this.c.getAge() != 0) return false;

    return true;
  }

  public void c() {
    this.e = 300;
    this.b.a(true);
  }

  public void d() {
    this.a = null;
    this.c = null;
    this.b.a(false);
  }

  public boolean b() {
    return (this.e >= 0) && (f()) && (this.b.getAge() == 0);
  }

  public void e() {
    this.e -= 1;
    this.b.getControllerLook().a(this.c, 10.0F, 30.0F);

    if (this.b.j(this.c) > 2.25D) {
      this.b.al().a(this.c, 0.25F);
    }
    else if ((this.e == 0) && (this.c.A())) i();

    if (this.b.an().nextInt(35) == 0) a(this.b);
  }

  private boolean f()
  {
    int i = (int)(this.a.getDoorCount() * 0.35D);

    return this.a.getPopulationCount() < i;
  }

  private void i() {
    EntityVillager localEntityVillager = new EntityVillager(this.d);
    this.c.setAge(6000);
    this.b.setAge(6000);
    localEntityVillager.setAge(-24000);
    localEntityVillager.setProfession(this.b.an().nextInt(5));
    localEntityVillager.setPositionRotation(this.b.locX, this.b.locY, this.b.locZ, 0.0F, 0.0F);
    this.d.addEntity(localEntityVillager);
    a(localEntityVillager);
  }

  private void a(EntityLiving paramEntityLiving) {
    Random localRandom = paramEntityLiving.an();
    for (int i = 0; i < 5; i++) {
      double d1 = localRandom.nextGaussian() * 0.02D;
      double d2 = localRandom.nextGaussian() * 0.02D;
      double d3 = localRandom.nextGaussian() * 0.02D;
      this.d.a("heart", paramEntityLiving.locX + localRandom.nextFloat() * paramEntityLiving.width * 2.0F - paramEntityLiving.width, paramEntityLiving.locY + 1.0D + localRandom.nextFloat() * paramEntityLiving.length, paramEntityLiving.locZ + localRandom.nextFloat() * paramEntityLiving.width * 2.0F - paramEntityLiving.width, d1, d2, d3);
    }
  }
}

