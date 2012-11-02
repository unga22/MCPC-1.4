package net.minecraft.server;

import java.util.Random;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.PluginManager;

public class BlockDoor extends Block
{
  protected BlockDoor(int i, Material material)
  {
    super(i, material);
    this.textureId = 97;
    if (material == Material.ORE) {
      this.textureId += 1;
    }

    float f = 0.5F;
    float f1 = 1.0F;

    a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
  }

  public boolean a() {
    return false;
  }

  public boolean b(IBlockAccess iblockaccess, int i, int j, int k) {
    int l = e(iblockaccess, i, j, k);

    return (l & 0x4) != 0;
  }

  public boolean b() {
    return false;
  }

  public int c() {
    return 7;
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    updateShape(world, i, j, k);
    return super.e(world, i, j, k);
  }

  public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
    d(e(iblockaccess, i, j, k));
  }

  public int c(IBlockAccess iblockaccess, int i, int j, int k) {
    return e(iblockaccess, i, j, k) & 0x3;
  }

  public boolean d(IBlockAccess iblockaccess, int i, int j, int k) {
    return (e(iblockaccess, i, j, k) & 0x4) != 0;
  }

  private void d(int i) {
    float f = 0.1875F;

    a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
    int j = i & 0x3;
    boolean flag = (i & 0x4) != 0;
    boolean flag1 = (i & 0x10) != 0;

    if (j == 0) {
      if (!flag)
        a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
      else if (!flag1)
        a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
      else
        a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
    }
    else if (j == 1) {
      if (!flag)
        a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
      else if (!flag1)
        a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      else
        a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
    }
    else if (j == 2) {
      if (!flag)
        a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      else if (!flag1)
        a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
      else
        a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
    }
    else if (j == 3)
      if (!flag)
        a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
      else if (!flag1)
        a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
      else
        a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
  }

  public void attack(World world, int i, int j, int k, EntityHuman entityhuman)
  {
    interact(world, i, j, k, entityhuman);
  }

  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (this.material == Material.ORE) {
      return true;
    }
    int l = e(world, i, j, k);
    int i1 = l & 0x7;

    i1 ^= 4;
    if ((l & 0x8) != 0) {
      world.setData(i, j - 1, k, i1);
      world.b(i, j - 1, k, i, j, k);
    } else {
      world.setData(i, j, k, i1);
      world.b(i, j, k, i, j, k);
    }

    world.a(entityhuman, 1003, i, j, k, 0);
    return true;
  }

  public void setDoor(World world, int i, int j, int k, boolean flag)
  {
    int l = e(world, i, j, k);
    boolean flag1 = (l & 0x4) != 0;

    if (flag1 != flag) {
      int i1 = l & 0x7;

      i1 ^= 4;
      if ((l & 0x8) != 0) {
        world.setData(i, j - 1, k, i1);
        world.b(i, j - 1, k, i, j, k);
      } else {
        world.setData(i, j, k, i1);
        world.b(i, j, k, i, j, k);
      }

      world.a((EntityHuman)null, 1003, i, j, k, 0);
    }
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    int i1 = world.getData(i, j, k);

    if ((i1 & 0x8) != 0) {
      if (world.getTypeId(i, j - 1, k) != this.id) {
        world.setTypeId(i, j, k, 0);
      }
      else if ((l > 0) && (l != this.id))
        doPhysics(world, i, j - 1, k, l);
    }
    else {
      boolean flag = false;

      if (world.getTypeId(i, j + 1, k) != this.id) {
        world.setTypeId(i, j, k, 0);
        flag = true;
      }

      if (!world.isBlockSolidOnSide(i, j - 1, k, 1)) {
        world.setTypeId(i, j, k, 0);
        flag = true;
        if (world.getTypeId(i, j + 1, k) == this.id) {
          world.setTypeId(i, j + 1, k, 0);
        }
      }

      if (flag) {
        if (!world.isStatic) {
          b(world, i, j, k, i1, 0);
        }
      }
      else if ((l > 0) && (Block.byId[l].isPowerSource())) {
        org.bukkit.World bworld = world.getWorld();
        org.bukkit.block.Block block = bworld.getBlockAt(i, j, k);
        org.bukkit.block.Block blockTop = bworld.getBlockAt(i, j + 1, k);

        int power = block.getBlockPower();
        int powerTop = blockTop.getBlockPower();
        if (powerTop > power) power = powerTop;
        int oldPower = (world.getData(i, j, k) & 0x4) > 0 ? 15 : 0;

        if (((oldPower == 0 ? 1 : 0) ^ (power == 0 ? 1 : 0)) != 0) {
          BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, oldPower, power);
          world.getServer().getPluginManager().callEvent(eventRedstone);

          setDoor(world, i, j, k, eventRedstone.getNewCurrent() > 0);
        }
      }
    }
  }

  public int getDropType(int i, Random random, int j)
  {
    return this.material == Material.ORE ? Item.IRON_DOOR.id : (i & 0x8) != 0 ? 0 : Item.WOOD_DOOR.id;
  }

  public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
    updateShape(world, i, j, k);
    return super.a(world, i, j, k, vec3d, vec3d1);
  }

  public boolean canPlace(World world, int i, int j, int k)
  {
    return j < 255;
  }

  public int g() {
    return 1;
  }

  public int e(IBlockAccess iblockaccess, int i, int j, int k) {
    int l = iblockaccess.getData(i, j, k);
    boolean flag = (l & 0x8) != 0;
    int j1;
    int i1;
    int j1;
    if (flag) {
      int i1 = iblockaccess.getData(i, j - 1, k);
      j1 = l;
    } else {
      i1 = l;
      j1 = iblockaccess.getData(i, j + 1, k);
    }

    boolean flag1 = (j1 & 0x1) != 0;
    int k1 = i1 & 0x7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);

    return k1;
  }
}

