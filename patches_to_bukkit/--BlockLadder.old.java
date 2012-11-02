package net.minecraft.server;

import java.util.Random;

public class BlockLadder extends Block
{
  protected BlockLadder(int i, int j)
  {
    super(i, j, Material.ORIENTABLE);
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    int l = world.getData(i, j, k);
    float f = 0.125F;

    if (l == 2) {
      a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
    }

    if (l == 3) {
      a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
    }

    if (l == 4) {
      a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    if (l == 5) {
      a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
    }

    return super.e(world, i, j, k);
  }

  public boolean a() {
    return false;
  }

  public boolean b() {
    return false;
  }

  public int c() {
    return 8;
  }

  public boolean canPlace(World par1World, int par2, int par3, int par4)
  {
    return (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, 5)) || (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, 4)) || (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, 3)) || (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, 2));
  }

  public void postPlace(World world, int i, int j, int k, int l)
  {
    int i1 = world.getData(i, j, k);

    if (((i1 == 0) || (l == 2)) && (world.isBlockSolidOnSide(i, j, k + 1, 2))) {
      i1 = 2;
    }

    if (((i1 == 0) || (l == 3)) && (world.isBlockSolidOnSide(i, j, k - 1, 3))) {
      i1 = 3;
    }

    if (((i1 == 0) || (l == 4)) && (world.isBlockSolidOnSide(i + 1, j, k, 4))) {
      i1 = 4;
    }

    if (((i1 == 0) || (l == 5)) && (world.isBlockSolidOnSide(i - 1, j, k, 5))) {
      i1 = 5;
    }

    world.setData(i, j, k, i1);
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    int i1 = world.getData(i, j, k);
    boolean flag = false;

    if ((i1 == 2) && (world.isBlockSolidOnSide(i, j, k + 1, 2))) {
      flag = true;
    }

    if ((i1 == 3) && (world.isBlockSolidOnSide(i, j, k - 1, 3))) {
      flag = true;
    }

    if ((i1 == 4) && (world.isBlockSolidOnSide(i + 1, j, k, 4))) {
      flag = true;
    }

    if ((i1 == 5) && (world.isBlockSolidOnSide(i - 1, j, k, 5))) {
      flag = true;
    }

    if (!flag) {
      b(world, i, j, k, i1, 0);
      world.setTypeId(i, j, k, 0);
    }

    super.doPhysics(world, i, j, k, l);
  }

  public int a(Random random) {
    return 1;
  }

  public boolean isLadder(World world, int x, int y, int z)
  {
    return true;
  }
}

