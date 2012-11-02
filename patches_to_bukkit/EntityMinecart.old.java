package net.minecraft.server;

import forge.ForgeHooks;
import forge.IMinecartCollisionHandler;
import forge.MinecraftForge;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class EntityMinecart extends Entity
  implements IInventory
{
  protected ItemStack[] items;
  protected int e;
  protected boolean f;
  public int type;
  public double b;
  public double c;
  protected static final int[][][] matrix = { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };
  protected int h;
  protected double i;
  protected double j;
  protected double k;
  protected double l;
  protected double m;
  protected double velocityX;
  protected double velocityY;
  protected double velocityZ;
  public static float defaultMaxSpeedRail = 0.4F;
  public static float defaultMaxSpeedGround = 0.4F;
  public static float defaultMaxSpeedAirLateral = 0.4F;
  public static float defaultMaxSpeedAirVertical = -1.0F;
  public static double defaultDragAir = 0.949999988079071D;
  protected boolean canUseRail = true;
  protected boolean canBePushed = true;
  private static IMinecartCollisionHandler collisionHandler = null;
  protected float maxSpeedRail;
  protected float maxSpeedGround;
  protected float maxSpeedAirLateral;
  protected float maxSpeedAirVertical;
  protected double dragAir;
  public boolean slowWhenEmpty = true;
  private double derailedX = 0.5D;
  private double derailedY = 0.5D;
  private double derailedZ = 0.5D;
  private double flyingX = 0.95D;
  private double flyingY = 0.95D;
  private double flyingZ = 0.95D;
  public double maxSpeed = 0.4D;
  public List<HumanEntity> transaction = new ArrayList();
  private int maxStack = 64;

  public ItemStack[] getContents() {
    return this.items;
  }

  public void onOpen(CraftHumanEntity who) {
    this.transaction.add(who);
  }

  public void onClose(CraftHumanEntity who) {
    this.transaction.remove(who);
  }

  public List<HumanEntity> getViewers() {
    return this.transaction;
  }

  public InventoryHolder getOwner() {
    org.bukkit.entity.Entity cart = getBukkitEntity();
    if ((cart instanceof InventoryHolder)) return (InventoryHolder)cart;
    return null;
  }

  public void setMaxStackSize(int size) {
    this.maxStack = size;
  }

  public EntityMinecart(World world)
  {
    super(world);
    this.items = new ItemStack[27];
    this.e = 0;
    this.f = false;
    this.bf = true;
    b(0.98F, 0.7F);
    this.height = (this.length / 2.0F);
    this.maxSpeedRail = defaultMaxSpeedRail;
    this.maxSpeedGround = defaultMaxSpeedGround;
    this.maxSpeedAirLateral = defaultMaxSpeedAirLateral;
    this.maxSpeedAirVertical = defaultMaxSpeedAirVertical;
    this.dragAir = defaultDragAir;
  }

  public EntityMinecart(World world, int type) {
    this(world);
    this.type = type;
  }

  protected boolean g_() {
    return false;
  }

  protected void b() {
    this.datawatcher.a(16, new Byte((byte)0));
    this.datawatcher.a(17, new Integer(0));
    this.datawatcher.a(18, new Integer(1));
    this.datawatcher.a(19, new Integer(0));
  }

  public AxisAlignedBB b_(Entity entity) {
    if (getCollisionHandler() != null)
    {
      return getCollisionHandler().getCollisionBox(this, entity);
    }
    return entity.boundingBox;
  }

  public AxisAlignedBB h() {
    if (getCollisionHandler() != null)
    {
      return getCollisionHandler().getBoundingBox(this);
    }
    return null;
  }

  public boolean e_() {
    return this.canBePushed;
  }

  public EntityMinecart(World world, double d0, double d1, double d2, int i) {
    this(world);
    setPosition(d0, d1 + this.height, d2);
    this.motX = 0.0D;
    this.motY = 0.0D;
    this.motZ = 0.0D;
    this.lastX = d0;
    this.lastY = d1;
    this.lastZ = d2;
    this.type = i;

    this.world.getServer().getPluginManager().callEvent(new VehicleCreateEvent((Vehicle)getBukkitEntity()));
  }

  public double x_() {
    return this.length * 0.0D - 0.300000011920929D;
  }

  public boolean damageEntity(DamageSource damagesource, int i) {
    if ((!this.world.isStatic) && (!this.dead))
    {
      Vehicle vehicle = (Vehicle)getBukkitEntity();
      org.bukkit.entity.Entity passenger = damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity();

      VehicleDamageEvent event = new VehicleDamageEvent(vehicle, passenger, i);
      this.world.getServer().getPluginManager().callEvent(event);

      if (event.isCancelled()) {
        return true;
      }

      i = event.getDamage();

      e(-n());
      d(10);
      aW();
      setDamage(getDamage() + i * 10);
      if (getDamage() > 40) {
        if (this.passenger != null) {
          this.passenger.mount(this);
        }

        VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, passenger);
        this.world.getServer().getPluginManager().callEvent(destroyEvent);

        if (destroyEvent.isCancelled()) {
          setDamage(40);
          return true;
        }

        die();
        dropCartAsItem();
      }
      return true;
    }
    return true;
  }

  public boolean o_()
  {
    return !this.dead;
  }

  public void die() {
    for (int i = 0; i < getSize(); i++) {
      ItemStack itemstack = getItem(i);

      if (itemstack != null) {
        float f = this.random.nextFloat() * 0.8F + 0.1F;
        float f1 = this.random.nextFloat() * 0.8F + 0.1F;
        float f2 = this.random.nextFloat() * 0.8F + 0.1F;

        while (itemstack.count > 0) {
          int j = this.random.nextInt(21) + 10;

          if (j > itemstack.count) {
            j = itemstack.count;
          }

          itemstack.count -= j;

          EntityItem entityitem = new EntityItem(this.world, this.locX + f, this.locY + f1, this.locZ + f2, new ItemStack(itemstack.id, j, itemstack.getData(), itemstack.getEnchantments()));
          float f3 = 0.05F;

          entityitem.motX = (float)this.random.nextGaussian() * f3;
          entityitem.motY = (float)this.random.nextGaussian() * f3 + 0.2F;
          entityitem.motZ = (float)this.random.nextGaussian() * f3;
          this.world.addEntity(entityitem);
        }
      }
    }

    super.die();
  }

  public void F_()
  {
    double prevX = this.locX;
    double prevY = this.locY;
    double prevZ = this.locZ;
    float prevYaw = this.yaw;
    float prevPitch = this.pitch;

    if (m() > 0) {
      d(m() - 1);
    }

    if (getDamage() > 0) {
      setDamage(getDamage() - 1);
    }

    if ((k()) && (this.random.nextInt(4) == 0) && (this.type == 2) && (getClass() == EntityMinecart.class)) {
      this.world.a("largesmoke", this.locX, this.locY + 0.8D, this.locZ, 0.0D, 0.0D, 0.0D);
    }

    if (this.world.isStatic) {
      if (this.h > 0) {
        double d0 = this.locX + (this.i - this.locX) / this.h;
        double d1 = this.locY + (this.j - this.locY) / this.h;
        double d2 = this.locZ + (this.k - this.locZ) / this.h;

        for (double d3 = this.l - this.yaw; d3 < -180.0D; d3 += 360.0D);
        while (d3 >= 180.0D) {
          d3 -= 360.0D;
        }

        this.yaw = (float)(this.yaw + d3 / this.h);
        this.pitch = (float)(this.pitch + (this.m - this.pitch) / this.h);
        this.h -= 1;
        setPosition(d0, d1, d2);
        c(this.yaw, this.pitch);
      } else {
        setPosition(this.locX, this.locY, this.locZ);
        c(this.yaw, this.pitch);
      }
    } else {
      this.lastX = this.locX;
      this.lastY = this.locY;
      this.lastZ = this.locZ;
      this.motY -= 0.03999999910593033D;
      int i = MathHelper.floor(this.locX);
      int j = MathHelper.floor(this.locY);
      int k = MathHelper.floor(this.locZ);

      if (BlockMinecartTrack.g(this.world, i, j - 1, k)) {
        j--;
      }

      double d4 = this.maxSpeed;
      double d5 = 0.0078125D;
      int l = this.world.getTypeId(i, j, k);

      if ((canUseRail()) && (BlockMinecartTrack.d(l))) {
        Vec3D vec3d = h(this.locX, this.locY, this.locZ);
        int i1 = ((BlockMinecartTrack)Block.byId[l]).getBasicRailMetadata(this.world, this, i, j, k);

        this.locY = j;
        boolean flag = false;
        boolean flag1 = false;

        if (l == Block.GOLDEN_RAIL.id) {
          flag = (this.world.getData(i, j, k) & 0x8) != 0;
          flag1 = !flag;
        }

        if ((i1 >= 2) && (i1 <= 5)) {
          this.locY = j + 1;
        }

        adjustSlopeVelocities(i1);
        int[][] aint = matrix[i1];
        double d6 = aint[1][0] - aint[0][0];
        double d7 = aint[1][2] - aint[0][2];
        double d8 = Math.sqrt(d6 * d6 + d7 * d7);
        double d9 = this.motX * d6 + this.motZ * d7;

        if (d9 < 0.0D) {
          d6 = -d6;
          d7 = -d7;
        }

        double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.motX = (d10 * d6 / d8);
        this.motZ = (d10 * d7 / d8);

        if ((flag1) && (shouldDoRailFunctions())) {
          double d11 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
          if (d11 < 0.03D) {
            this.motX *= 0.0D;
            this.motY *= 0.0D;
            this.motZ *= 0.0D;
          } else {
            this.motX *= 0.5D;
            this.motY *= 0.0D;
            this.motZ *= 0.5D;
          }
        }

        double d11 = 0.0D;
        double d12 = i + 0.5D + aint[0][0] * 0.5D;
        double d13 = k + 0.5D + aint[0][2] * 0.5D;
        double d14 = i + 0.5D + aint[1][0] * 0.5D;
        double d15 = k + 0.5D + aint[1][2] * 0.5D;

        d6 = d14 - d12;
        d7 = d15 - d13;

        if (d6 == 0.0D) {
          this.locX = (i + 0.5D);
          d11 = this.locZ - k;
        } else if (d7 == 0.0D) {
          this.locZ = (k + 0.5D);
          d11 = this.locX - i;
        } else {
          double d16 = this.locX - d12;
          double d18 = this.locZ - d13;
          double d17 = (d16 * d6 + d18 * d7) * 2.0D;
          d11 = d17;
        }

        this.locX = (d12 + d6 * d11);
        this.locZ = (d13 + d7 * d11);
        setPosition(this.locX, this.locY + this.height, this.locZ);
        moveMinecartOnRail(i, j, k);
        if ((aint[0][1] != 0) && (MathHelper.floor(this.locX) - i == aint[0][0]) && (MathHelper.floor(this.locZ) - k == aint[0][2]))
          setPosition(this.locX, this.locY + aint[0][1], this.locZ);
        else if ((aint[1][1] != 0) && (MathHelper.floor(this.locX) - i == aint[1][0]) && (MathHelper.floor(this.locZ) - k == aint[1][2])) {
          setPosition(this.locX, this.locY + aint[1][1], this.locZ);
        }
        applyDragAndPushForces();

        Vec3D vec3d1 = h(this.locX, this.locY, this.locZ);

        if ((vec3d1 != null) && (vec3d != null)) {
          double d20 = (vec3d.b - vec3d1.b) * 0.05D;

          d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
          if (d10 > 0.0D) {
            this.motX = (this.motX / d10 * (d10 + d20));
            this.motZ = (this.motZ / d10 * (d10 + d20));
          }

          setPosition(this.locX, vec3d1.b, this.locZ);
        }

        int j1 = MathHelper.floor(this.locX);
        int k1 = MathHelper.floor(this.locZ);

        if ((j1 != i) || (k1 != k)) {
          d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
          this.motX = (d10 * j1 - i);
          this.motZ = (d10 * k1 - k);
        }

        updatePushForces();
        if (shouldDoRailFunctions()) {
          ((BlockMinecartTrack)Block.byId[l]).onMinecartPass(this.world, this, i, j, k);
        }

        if ((flag) && (shouldDoRailFunctions())) {
          double d21 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
          if (d21 > 0.01D) {
            double d22 = 0.06D;

            this.motX += this.motX / d21 * d22;
            this.motZ += this.motZ / d21 * d22;
          } else if (i1 == 1) {
            if (this.world.e(i - 1, j, k))
              this.motX = 0.02D;
            else if (this.world.e(i + 1, j, k))
              this.motX = -0.02D;
          }
          else if (i1 == 0) {
            if (this.world.e(i, j, k - 1))
              this.motZ = 0.02D;
            else if (this.world.e(i, j, k + 1))
              this.motZ = -0.02D;
          }
        }
      }
      else {
        moveMinecartOffRail(i, j, k);
      }

      this.pitch = 0.0F;
      double d23 = this.lastX - this.locX;
      double d24 = this.lastZ - this.locZ;

      if (d23 * d23 + d24 * d24 > 0.001D) {
        this.yaw = (float)(Math.atan2(d24, d23) * 180.0D / 3.141592653589793D);
        if (this.f) {
          this.yaw += 180.0F;
        }

      }

      for (double d25 = this.yaw - this.lastYaw; d25 >= 180.0D; d25 -= 360.0D);
      while (d25 < -180.0D) {
        d25 += 360.0D;
      }

      if ((d25 < -170.0D) || (d25 >= 170.0D)) {
        this.yaw += 180.0F;
        this.f = (!this.f);
      }

      c(this.yaw, this.pitch);

      org.bukkit.World bworld = this.world.getWorld();
      Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
      Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
      Vehicle vehicle = (Vehicle)getBukkitEntity();

      this.world.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(vehicle));

      if (!from.equals(to)) {
        this.world.getServer().getPluginManager().callEvent(new VehicleMoveEvent(vehicle, from, to));
      }

      AxisAlignedBB box = null;
      if (getCollisionHandler() != null)
      {
        box = getCollisionHandler().getMinecartCollisionBox(this);
      }
      else
      {
        box = this.boundingBox.grow(0.2D, 0.0D, 0.2D);
      }
      List list = this.world.getEntities(this, box);

      if ((list != null) && (list.size() > 0)) {
        for (int l1 = 0; l1 < list.size(); l1++) {
          Entity entity = (Entity)list.get(l1);

          if ((entity != this.passenger) && (entity.e_()) && ((entity instanceof EntityMinecart))) {
            entity.collide(this);
          }
        }
      }

      if ((this.passenger != null) && (this.passenger.dead)) {
        if (this.passenger.vehicle == this) {
          this.passenger.vehicle = null;
        }

        this.passenger = null;
      }
      updateFuel();
      ForgeHooks.onMinecartUpdate(this, i, j, k);
    }
  }

  public Vec3D h(double d0, double d1, double d2) {
    int i = MathHelper.floor(d0);
    int j = MathHelper.floor(d1);
    int k = MathHelper.floor(d2);

    if (BlockMinecartTrack.g(this.world, i, j - 1, k)) {
      j--;
    }

    int l = this.world.getTypeId(i, j, k);

    if (BlockMinecartTrack.d(l)) {
      int i1 = ((BlockMinecartTrack)Block.byId[l]).getBasicRailMetadata(this.world, this, i, j, k);

      d1 = j;

      if ((i1 >= 2) && (i1 <= 5)) {
        d1 = j + 1;
      }

      int[][] aint = matrix[i1];
      double d3 = 0.0D;
      double d4 = i + 0.5D + aint[0][0] * 0.5D;
      double d5 = j + 0.5D + aint[0][1] * 0.5D;
      double d6 = k + 0.5D + aint[0][2] * 0.5D;
      double d7 = i + 0.5D + aint[1][0] * 0.5D;
      double d8 = j + 0.5D + aint[1][1] * 0.5D;
      double d9 = k + 0.5D + aint[1][2] * 0.5D;
      double d10 = d7 - d4;
      double d11 = (d8 - d5) * 2.0D;
      double d12 = d9 - d6;

      if (d10 == 0.0D) {
        d0 = i + 0.5D;
        d3 = d2 - k;
      } else if (d12 == 0.0D) {
        d2 = k + 0.5D;
        d3 = d0 - i;
      } else {
        double d13 = d0 - d4;
        double d14 = d2 - d6;
        double d15 = (d13 * d10 + d14 * d12) * 2.0D;

        d3 = d15;
      }

      d0 = d4 + d10 * d3;
      d1 = d5 + d11 * d3;
      d2 = d6 + d12 * d3;
      if (d11 < 0.0D) {
        d1 += 1.0D;
      }

      if (d11 > 0.0D) {
        d1 += 0.5D;
      }

      return Vec3D.create(d0, d1, d2);
    }
    return null;
  }

  protected void b(NBTTagCompound nbttagcompound)
  {
    nbttagcompound.setInt("Type", this.type);
    if (isPoweredCart()) {
      nbttagcompound.setDouble("PushX", this.b);
      nbttagcompound.setDouble("PushZ", this.c);
      nbttagcompound.setInt("Fuel", this.e);
    }
    if (getSize() > 0) {
      NBTTagList nbttaglist = new NBTTagList();

      for (int i = 0; i < this.items.length; i++) {
        if (this.items[i] != null) {
          NBTTagCompound nbttagcompound1 = new NBTTagCompound();

          nbttagcompound1.setByte("Slot", (byte)i);
          this.items[i].save(nbttagcompound1);
          nbttaglist.add(nbttagcompound1);
        }
      }

      nbttagcompound.set("Items", nbttaglist);
    }
  }

  protected void a(NBTTagCompound nbttagcompound) {
    this.type = nbttagcompound.getInt("Type");
    if (isPoweredCart()) {
      this.b = nbttagcompound.getDouble("PushX");
      this.c = nbttagcompound.getDouble("PushZ");
      try {
        this.e = nbttagcompound.getInt("Fuel");
      } catch (ClassCastException cce) {
        this.e = nbttagcompound.getShort("Fuel");
      }
    }
    if (getSize() > 0) {
      NBTTagList nbttaglist = nbttagcompound.getList("Items");

      this.items = new ItemStack[getSize()];

      for (int i = 0; i < nbttaglist.size(); i++) {
        NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.get(i);
        int j = nbttagcompound1.getByte("Slot") & 0xFF;

        if ((j >= 0) && (j < this.items.length))
          this.items[j] = ItemStack.a(nbttagcompound1);
      }
    }
  }

  public void collide(Entity entity)
  {
    ForgeHooks.onMinecartEntityCollision(this, entity);
    if (getCollisionHandler() != null)
    {
      getCollisionHandler().onEntityCollision(this, entity);
      return;
    }
    if ((!this.world.isStatic) && 
      (entity != this.passenger))
    {
      Vehicle vehicle = (Vehicle)getBukkitEntity();
      org.bukkit.entity.Entity hitEntity = entity == null ? null : entity.getBukkitEntity();

      VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, hitEntity);
      this.world.getServer().getPluginManager().callEvent(collisionEvent);

      if (collisionEvent.isCancelled()) {
        return;
      }

      if (((entity instanceof EntityLiving)) && (!(entity instanceof EntityHuman)) && (!(entity instanceof EntityIronGolem)) && (canBeRidden()) && (this.motX * this.motX + this.motZ * this.motZ > 0.01D) && (this.passenger == null) && (entity.vehicle == null)) {
        entity.mount(this);
      }

      double d0 = entity.locX - this.locX;
      double d1 = entity.locZ - this.locZ;
      double d2 = d0 * d0 + d1 * d1;

      if ((d2 >= 9.999999747378752E-005D) && (!collisionEvent.isCollisionCancelled())) {
        d2 = MathHelper.sqrt(d2);
        d0 /= d2;
        d1 /= d2;
        double d3 = 1.0D / d2;

        if (d3 > 1.0D) {
          d3 = 1.0D;
        }

        d0 *= d3;
        d1 *= d3;
        d0 *= 0.1000000014901161D;
        d1 *= 0.1000000014901161D;
        d0 *= 1.0F - this.bR;
        d1 *= 1.0F - this.bR;
        d0 *= 0.5D;
        d1 *= 0.5D;
        if ((entity instanceof EntityMinecart)) {
          double d4 = entity.locX - this.locX;
          double d5 = entity.locZ - this.locZ;
          Vec3D vec3d = Vec3D.create(d4, 0.0D, d5).b();
          Vec3D vec3d1 = Vec3D.create(MathHelper.cos(this.yaw * 3.141593F / 180.0F), 0.0D, MathHelper.sin(this.yaw * 3.141593F / 180.0F)).b();
          double d6 = Math.abs(vec3d.a(vec3d1));

          if (d6 < 0.800000011920929D) {
            return;
          }

          double d7 = entity.motX + this.motX;
          double d8 = entity.motZ + this.motZ;

          if ((((EntityMinecart)entity).isPoweredCart()) && (!isPoweredCart())) {
            this.motX *= 0.2000000029802322D;
            this.motZ *= 0.2000000029802322D;
            b_(entity.motX - d0, 0.0D, entity.motZ - d1);
            entity.motX *= 0.949999988079071D;
            entity.motZ *= 0.949999988079071D;
          } else if ((!((EntityMinecart)entity).isPoweredCart()) && (isPoweredCart())) {
            entity.motX *= 0.2000000029802322D;
            entity.motZ *= 0.2000000029802322D;
            entity.b_(this.motX + d0, 0.0D, this.motZ + d1);
            this.motX *= 0.949999988079071D;
            this.motZ *= 0.949999988079071D;
          } else {
            d7 /= 2.0D;
            d8 /= 2.0D;
            this.motX *= 0.2000000029802322D;
            this.motZ *= 0.2000000029802322D;
            b_(d7 - d0, 0.0D, d8 - d1);
            entity.motX *= 0.2000000029802322D;
            entity.motZ *= 0.2000000029802322D;
            entity.b_(d7 + d0, 0.0D, d8 + d1);
          }
        } else {
          b_(-d0, 0.0D, -d1);
          entity.b_(d0 / 4.0D, 0.0D, d1 / 4.0D);
        }
      }
    }
  }

  public int getSize()
  {
    return (this.type == 1) && (getClass() == EntityMinecart.class) ? 27 : 0;
  }

  public ItemStack getItem(int i) {
    return this.items[i];
  }

  public ItemStack splitStack(int i, int j) {
    if (this.items[i] != null)
    {
      if (this.items[i].count <= j) {
        ItemStack itemstack = this.items[i];
        this.items[i] = null;
        return itemstack;
      }
      ItemStack itemstack = this.items[i].a(j);
      if (this.items[i].count == 0) {
        this.items[i] = null;
      }

      return itemstack;
    }

    return null;
  }

  public ItemStack splitWithoutUpdate(int i)
  {
    if (this.items[i] != null) {
      ItemStack itemstack = this.items[i];

      this.items[i] = null;
      return itemstack;
    }
    return null;
  }

  public void setItem(int i, ItemStack itemstack)
  {
    this.items[i] = itemstack;
    if ((itemstack != null) && (itemstack.count > getMaxStackSize()))
      itemstack.count = getMaxStackSize();
  }

  public String getName()
  {
    return "container.minecart";
  }

  public int getMaxStackSize() {
    return this.maxStack;
  }
  public void update() {
  }

  public boolean b(EntityHuman entityhuman) {
    if (!ForgeHooks.onMinecartInteract(this, entityhuman))
    {
      return true;
    }
    if (canBeRidden()) {
      if ((this.passenger != null) && ((this.passenger instanceof EntityHuman)) && (this.passenger != entityhuman)) {
        return true;
      }

      if (!this.world.isStatic)
        entityhuman.mount(this);
    }
    else if (getSize() > 0) {
      if (!this.world.isStatic)
        entityhuman.openContainer(this);
    }
    else if ((this.type == 2) && (getClass() == EntityMinecart.class)) {
      ItemStack itemstack = entityhuman.inventory.getItemInHand();

      if ((itemstack != null) && (itemstack.id == Item.COAL.id)) {
        if (--itemstack.count == 0) {
          entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack)null);
        }

        this.e += 3600;
      }

      this.b = (this.locX - entityhuman.locX);
      this.c = (this.locZ - entityhuman.locZ);
    }

    return true;
  }

  public boolean a(EntityHuman entityhuman) {
    return !this.dead;
  }

  public boolean k() {
    return (this.datawatcher.getByte(16) & 0x1) != 0;
  }

  protected void a(boolean flag) {
    if (flag)
      this.datawatcher.watch(16, Byte.valueOf((byte)(this.datawatcher.getByte(16) | 0x1)));
    else
      this.datawatcher.watch(16, Byte.valueOf((byte)(this.datawatcher.getByte(16) & 0xFFFFFFFE))); 
  }

  public void f() {
  }

  public void g() {
  }

  public void setDamage(int i) { this.datawatcher.watch(19, Integer.valueOf(i)); }

  public int getDamage()
  {
    return this.datawatcher.getInt(19);
  }

  public void d(int i) {
    this.datawatcher.watch(17, Integer.valueOf(i));
  }

  public int m() {
    return this.datawatcher.getInt(17);
  }

  public void e(int i) {
    this.datawatcher.watch(18, Integer.valueOf(i));
  }

  public int n() {
    return this.datawatcher.getInt(18);
  }

  public Vector getFlyingVelocityMod()
  {
    return new Vector(this.flyingX, this.flyingY, this.flyingZ);
  }

  public void setFlyingVelocityMod(Vector flying) {
    this.flyingX = flying.getX();
    this.flyingY = flying.getY();
    this.flyingZ = flying.getZ();
  }

  public Vector getDerailedVelocityMod() {
    return new Vector(this.derailedX, this.derailedY, this.derailedZ);
  }

  public void setDerailedVelocityMod(Vector derailed) {
    this.derailedX = derailed.getX();
    this.derailedY = derailed.getY();
    this.derailedZ = derailed.getZ();
  }

  public void dropCartAsItem()
  {
    for (ItemStack item : getItemsDropped())
    {
      a(item, 0.0F);
    }
  }

  public List<ItemStack> getItemsDropped()
  {
    List items = new ArrayList();
    items.add(new ItemStack(Item.MINECART));

    switch (this.type)
    {
    case 1:
      items.add(new ItemStack(Block.CHEST));
      break;
    case 2:
      items.add(new ItemStack(Block.FURNACE));
    }

    return items;
  }

  public ItemStack getCartItem()
  {
    return MinecraftForge.getItemForCart(this);
  }

  public boolean isPoweredCart()
  {
    return (this.type == 2) && (getClass() == EntityMinecart.class);
  }

  public boolean isStorageCart()
  {
    return (this.type == 1) && (getClass() == EntityMinecart.class);
  }

  public boolean canBeRidden()
  {
    if ((this.type == 0) && (getClass() == EntityMinecart.class))
    {
      return true;
    }
    return false;
  }

  public boolean canUseRail()
  {
    return this.canUseRail;
  }

  public void setCanUseRail(boolean use)
  {
    this.canUseRail = use;
  }

  public boolean shouldDoRailFunctions()
  {
    return true;
  }

  public int getMinecartType()
  {
    return this.type;
  }

  public static IMinecartCollisionHandler getCollisionHandler()
  {
    return collisionHandler;
  }

  public static void setCollisionHandler(IMinecartCollisionHandler handler)
  {
    collisionHandler = handler;
  }

  protected double getDrag()
  {
    return this.passenger != null ? 1.0D : 0.96D;
  }

  protected void applyDragAndPushForces()
  {
    if (isPoweredCart())
    {
      double d27 = MathHelper.sqrt(this.b * this.b + this.c * this.c);
      if (d27 > 0.01D)
      {
        this.b /= d27;
        this.c /= d27;
        double d29 = 0.04D;
        this.motX *= 0.8D;
        this.motY *= 0.0D;
        this.motZ *= 0.8D;
        this.motX += this.b * d29;
        this.motZ += this.c * d29;
      }
      else {
        this.motX *= 0.9D;
        this.motY *= 0.0D;
        this.motZ *= 0.9D;
      }
    }
    this.motX *= getDrag();
    this.motY *= 0.0D;
    this.motZ *= getDrag();
  }

  protected void updatePushForces()
  {
    if (isPoweredCart())
    {
      double push = MathHelper.sqrt(this.b * this.b + this.c * this.c);
      if ((push > 0.01D) && (this.motX * this.motX + this.motZ * this.motZ > 0.001D))
      {
        this.b /= push;
        this.c /= push;
        if (this.b * this.b + this.c * this.c < 0.0D)
        {
          this.b = 0.0D;
          this.c = 0.0D;
        }
        else {
          this.b = this.motX;
          this.c = this.motZ;
        }
      }
    }
  }

  protected void moveMinecartOnRail(int i, int j, int k)
  {
    int id = this.world.getTypeId(i, j, k);
    if (!BlockMinecartTrack.d(id))
    {
      return;
    }
    float railMaxSpeed = ((BlockMinecartTrack)Block.byId[id]).getRailMaxSpeed(this.world, this, i, j, k);

    double maxSpeed = Math.min(railMaxSpeed, getMaxSpeedRail());
    double mX = this.motX;
    double mZ = this.motZ;
    if (this.passenger != null)
    {
      mX *= 0.75D;
      mZ *= 0.75D;
    }
    if (mX < -maxSpeed) mX = -maxSpeed;
    if (mX > maxSpeed) mX = maxSpeed;
    if (mZ < -maxSpeed) mZ = -maxSpeed;
    if (mZ > maxSpeed) mZ = maxSpeed;
    move(mX, 0.0D, mZ);
  }

  protected void moveMinecartOffRail(int i, int j, int k)
  {
    double d2 = getMaxSpeedGround();
    if (!this.onGround)
    {
      d2 = getMaxSpeedAirLateral();
    }
    if (this.motX < -d2) this.motX = (-d2);
    if (this.motX > d2) this.motX = d2;
    if (this.motZ < -d2) this.motZ = (-d2);
    if (this.motZ > d2) this.motZ = d2;
    double moveY = this.motY;
    if ((getMaxSpeedAirVertical() > 0.0F) && (this.motY > getMaxSpeedAirVertical()))
    {
      moveY = getMaxSpeedAirVertical();
      if ((Math.abs(this.motX) < 0.300000011920929D) && (Math.abs(this.motZ) < 0.300000011920929D))
      {
        moveY = 0.1500000059604645D;
        this.motY = moveY;
      }
    }
    if (this.onGround)
    {
      this.motX *= 0.5D;
      this.motY *= 0.5D;
      this.motZ *= 0.5D;
    }
    move(this.motX, moveY, this.motZ);
    if (!this.onGround)
    {
      this.motX *= getDragAir();
      this.motY *= getDragAir();
      this.motZ *= getDragAir();
    }
  }

  protected void updateFuel()
  {
    if (this.e > 0) this.e -= 1;
    if (this.e <= 0) this.b = (this.c = 0.0D);
    a(this.e > 0);
  }

  protected void adjustSlopeVelocities(int metadata)
  {
    double acceleration = 0.0078125D;
    if (metadata == 2)
    {
      this.motX -= acceleration;
    }
    else if (metadata == 3)
    {
      this.motX += acceleration;
    }
    else if (metadata == 4)
    {
      this.motZ += acceleration;
    }
    else if (metadata == 5)
    {
      this.motZ -= acceleration;
    }
  }

  public float getMaxSpeedRail()
  {
    return this.maxSpeedRail;
  }

  public void setMaxSpeedRail(float value)
  {
    this.maxSpeedRail = value;
  }

  public float getMaxSpeedGround()
  {
    return this.maxSpeedGround;
  }

  public void setMaxSpeedGround(float value)
  {
    this.maxSpeedGround = value;
  }

  public float getMaxSpeedAirLateral()
  {
    return this.maxSpeedAirLateral;
  }

  public void setMaxSpeedAirLateral(float value)
  {
    this.maxSpeedAirLateral = value;
  }

  public float getMaxSpeedAirVertical()
  {
    return this.maxSpeedAirVertical;
  }

  public void setMaxSpeedAirVertical(float value)
  {
    this.maxSpeedAirVertical = value;
  }

  public double getDragAir()
  {
    return this.dragAir;
  }

  public void setDragAir(double value)
  {
    this.dragAir = value;
  }
}

