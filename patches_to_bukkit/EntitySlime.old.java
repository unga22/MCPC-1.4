package net.minecraft.server;

import java.util.Random;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.plugin.PluginManager;

public class EntitySlime extends EntityLiving
  implements IMonster
{
  public float a;
  public float b;
  public float c;
  private int jumpDelay = 0;

  public EntitySlime(World world) {
    super(world);
    this.texture = "/mob/slime.png";
    int i = 1 << this.random.nextInt(3);

    this.height = 0.0F;
    this.jumpDelay = (this.random.nextInt(20) + 10);
    setSize(i);
  }

  protected void b() {
    super.b();
    this.datawatcher.a(16, new Byte((byte)1));
  }

  public void setSize(int i) {
    this.datawatcher.watch(16, new Byte((byte)i));
    b(0.6F * i, 0.6F * i);
    setPosition(this.locX, this.locY, this.locZ);
    setHealth(getMaxHealth());
    this.aA = i;
  }

  public int getMaxHealth() {
    int i = getSize();

    return i * i;
  }

  public int getSize() {
    return this.datawatcher.getByte(16);
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("Size", getSize() - 1);
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    setSize(nbttagcompound.getInt("Size") + 1);
  }

  protected String A() {
    return "slime";
  }

  protected String I() {
    return "mob.slime";
  }

  public void F_() {
    if ((!this.world.isStatic) && (this.world.difficulty == 0) && (getSize() > 0)) {
      this.dead = true;
    }

    this.b += (this.a - this.b) * 0.5F;
    this.c = this.b;
    boolean flag = this.onGround;

    super.F_();
    if ((this.onGround) && (!flag)) {
      int i = getSize();

      for (int j = 0; j < i * 8; j++) {
        float f = this.random.nextFloat() * 3.141593F * 2.0F;
        float f1 = this.random.nextFloat() * 0.5F + 0.5F;
        float f2 = MathHelper.sin(f) * i * 0.5F * f1;
        float f3 = MathHelper.cos(f) * i * 0.5F * f1;

        this.world.a(A(), this.locX + f2, this.boundingBox.b, this.locZ + f3, 0.0D, 0.0D, 0.0D);
      }

      if (K()) {
        this.world.makeSound(this, I(), p(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
      }

      this.a = -0.5F;
    }

    F();
  }

  protected void d_() {
    aG();
    EntityHuman entityhuman = this.world.findNearbyVulnerablePlayer(this, 16.0D);

    if (entityhuman != null) {
      a(entityhuman, 10.0F, 20.0F);
    }

    if ((this.onGround) && (this.jumpDelay-- <= 0)) {
      this.jumpDelay = E();
      if (entityhuman != null) {
        this.jumpDelay /= 3;
      }

      this.aZ = true;
      if (M()) {
        this.world.makeSound(this, I(), p(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

      this.a = 1.0F;
      this.aW = (1.0F - this.random.nextFloat() * 2.0F);
      this.aX = 1 * getSize();
    } else {
      this.aZ = false;
      if (this.onGround)
        this.aW = (this.aX = 0.0F);
    }
  }

  protected void F()
  {
    this.a *= 0.6F;
  }

  protected int E() {
    return this.random.nextInt(20) + 10;
  }

  protected EntitySlime C() {
    return new EntitySlime(this.world);
  }

  public void die() {
    int i = getSize();

    if ((!this.world.isStatic) && (i > 1) && (getHealth() <= 0)) {
      int j = 2 + this.random.nextInt(3);

      SlimeSplitEvent event = new SlimeSplitEvent((Slime)getBukkitEntity(), j);
      this.world.getServer().getPluginManager().callEvent(event);

      if ((!event.isCancelled()) && (event.getCount() > 0)) {
        j = event.getCount();
      } else {
        super.die();
        return;
      }

      for (int k = 0; k < j; k++) {
        float f = (k % 2 - 0.5F) * i / 4.0F;
        float f1 = (k / 2 - 0.5F) * i / 4.0F;
        EntitySlime entityslime = C();

        entityslime.setSize(i / 2);
        entityslime.setPositionRotation(this.locX + f, this.locY + 0.5D, this.locZ + f1, this.random.nextFloat() * 360.0F, 0.0F);
        this.world.addEntity(entityslime, CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);
      }
    }

    super.die();
  }

  public void a_(EntityHuman entityhuman) {
    if (G()) {
      int i = getSize();

      if ((h(entityhuman)) && (i(entityhuman) < 0.6D * i) && (entityhuman.damageEntity(DamageSource.mobAttack(this), H())))
        this.world.makeSound(this, "mob.slimeattack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }
  }

  protected boolean G()
  {
    return getSize() > 1;
  }

  protected int H() {
    return getSize();
  }

  protected String j() {
    return "mob.slime";
  }

  protected String k() {
    return "mob.slime";
  }

  protected int getLootId() {
    return getSize() == 1 ? Item.SLIME_BALL.id : 0;
  }

  public boolean canSpawn() {
    Chunk chunk = this.world.getChunkAtWorldCoords(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));

    return ((getSize() == 1) || (this.world.difficulty > 0)) && (this.random.nextInt(10) == 0) && (chunk.a(987234911L).nextInt(10) == 0) && (this.locY < 40.0D) ? super.canSpawn() : false;
  }

  protected float p() {
    return 0.4F * getSize();
  }

  public int D() {
    return 0;
  }

  protected boolean M() {
    return getSize() > 1;
  }

  protected boolean K() {
    return getSize() > 2;
  }
}

