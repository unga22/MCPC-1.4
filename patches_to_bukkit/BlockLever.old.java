package net.minecraft.server;

import java.util.Random;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.PluginManager;

public class BlockLever extends Block
{
  protected BlockLever(int i, int j)
  {
    super(i, j, Material.ORIENTABLE);
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    return null;
  }

  public boolean a() {
    return false;
  }

  public boolean b() {
    return false;
  }

  public int c() {
    return 12;
  }

  public boolean canPlace(World par1World, int par2, int par3, int par4, int par5) {
    return ((par5 == 1) && (par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1))) || ((par5 == 2) && (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, 2))) || ((par5 == 3) && (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, 3))) || ((par5 == 4) && (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, 4))) || ((par5 == 5) && (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, 5)));
  }

  public boolean canPlace(World par1World, int par2, int par3, int par4)
  {
    return (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, 5)) || (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, 4)) || (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, 3)) || (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, 2)) || (par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1));
  }

  public void postPlace(World world, int i, int j, int k, int l)
  {
    int i1 = world.getData(i, j, k);
    int j1 = i1 & 0x8;

    i1 &= 7;
    i1 = -1;
    if ((l == 1) && (world.isBlockSolidOnSide(i, j - 1, k, 1))) {
      i1 = 5 + world.random.nextInt(2);
    }

    if ((l == 2) && (world.isBlockSolidOnSide(i, j, k + 1, 2))) {
      i1 = 4;
    }

    if ((l == 3) && (world.isBlockSolidOnSide(i, j, k - 1, 3))) {
      i1 = 3;
    }

    if ((l == 4) && (world.isBlockSolidOnSide(i + 1, j, k, 4))) {
      i1 = 2;
    }

    if ((l == 5) && (world.isBlockSolidOnSide(i - 1, j, k, 5))) {
      i1 = 1;
    }

    if (i1 == -1) {
      b(world, i, j, k, world.getData(i, j, k), 0);
      world.setTypeId(i, j, k, 0);
    } else {
      world.setData(i, j, k, i1 + j1);
    }
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    if (g(world, i, j, k)) {
      int i1 = world.getData(i, j, k) & 0x7;
      boolean flag = false;

      if ((!world.isBlockSolidOnSide(i - 1, j, k, 5)) && (i1 == 1)) {
        flag = true;
      }

      if ((!world.isBlockSolidOnSide(i + 1, j, k, 4)) && (i1 == 2)) {
        flag = true;
      }

      if ((!world.isBlockSolidOnSide(i, j, k - 1, 3)) && (i1 == 3)) {
        flag = true;
      }

      if ((!world.isBlockSolidOnSide(i, j, k + 1, 2)) && (i1 == 4)) {
        flag = true;
      }

      if ((!world.isBlockSolidOnSide(i, j - 1, k, 1)) && (i1 == 5)) {
        flag = true;
      }

      if ((!world.isBlockSolidOnSide(i, j - 1, k, 1)) && (i1 == 6)) {
        flag = true;
      }

      if (flag) {
        b(world, i, j, k, world.getData(i, j, k), 0);
        world.setTypeId(i, j, k, 0);
      }
    }
  }

  private boolean g(World world, int i, int j, int k) {
    if (!canPlace(world, i, j, k)) {
      b(world, i, j, k, world.getData(i, j, k), 0);
      world.setTypeId(i, j, k, 0);
      return false;
    }
    return true;
  }

  public void updateShape(IBlockAccess iblockaccess, int i, int j, int k)
  {
    int l = iblockaccess.getData(i, j, k) & 0x7;
    float f = 0.1875F;

    if (l == 1) {
      a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
    } else if (l == 2) {
      a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
    } else if (l == 3) {
      a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
    } else if (l == 4) {
      a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
    } else {
      f = 0.25F;
      a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
    }
  }

  public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
    interact(world, i, j, k, entityhuman);
  }

  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (world.isStatic) {
      return true;
    }
    int l = world.getData(i, j, k);
    int i1 = l & 0x7;
    int j1 = 8 - (l & 0x8);

    org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);
    int old = j1 != 8 ? 1 : 0;
    int current = j1 == 8 ? 1 : 0;

    BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
    world.getServer().getPluginManager().callEvent(eventRedstone);

    if ((eventRedstone.getNewCurrent() > 0 ? 1 : 0) != (j1 == 8 ? 1 : 0)) {
      return true;
    }

    world.setData(i, j, k, i1 + j1);
    world.b(i, j, k, i, j, k);
    world.makeSound(i + 0.5D, j + 0.5D, k + 0.5D, "random.click", 0.3F, j1 > 0 ? 0.6F : 0.5F);
    world.applyPhysics(i, j, k, this.id);
    if (i1 == 1)
      world.applyPhysics(i - 1, j, k, this.id);
    else if (i1 == 2)
      world.applyPhysics(i + 1, j, k, this.id);
    else if (i1 == 3)
      world.applyPhysics(i, j, k - 1, this.id);
    else if (i1 == 4)
      world.applyPhysics(i, j, k + 1, this.id);
    else {
      world.applyPhysics(i, j - 1, k, this.id);
    }

    return true;
  }

  public void remove(World world, int i, int j, int k)
  {
    int l = world.getData(i, j, k);

    if ((l & 0x8) > 0) {
      world.applyPhysics(i, j, k, this.id);
      int i1 = l & 0x7;

      if (i1 == 1)
        world.applyPhysics(i - 1, j, k, this.id);
      else if (i1 == 2)
        world.applyPhysics(i + 1, j, k, this.id);
      else if (i1 == 3)
        world.applyPhysics(i, j, k - 1, this.id);
      else if (i1 == 4)
        world.applyPhysics(i, j, k + 1, this.id);
      else {
        world.applyPhysics(i, j - 1, k, this.id);
      }
    }

    super.remove(world, i, j, k);
  }

  public boolean a(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    return (iblockaccess.getData(i, j, k) & 0x8) > 0;
  }

  public boolean d(World world, int i, int j, int k, int l) {
    int i1 = world.getData(i, j, k);

    if ((i1 & 0x8) == 0) {
      return false;
    }
    int j1 = i1 & 0x7;

    return (j1 == 6) && (l == 1);
  }

  public boolean isPowerSource()
  {
    return true;
  }
}

