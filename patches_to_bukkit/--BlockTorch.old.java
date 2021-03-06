package net.minecraft.server;

import java.util.Random;

public class BlockTorch extends Block
{
  protected BlockTorch(int i, int j)
  {
    super(i, j, Material.ORIENTABLE);
    a(true);
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
    return 2;
  }

  private boolean g(World world, int i, int j, int k) {
    if (world.isBlockSolidOnSide(i, j, k, 1)) {
      return true;
    }
    int l = world.getTypeId(i, j, k);

    if ((l != Block.FENCE.id) && (l != Block.NETHER_FENCE.id) && (l != Block.GLASS.id)) {
      if ((Block.byId[l] != null) && ((Block.byId[l] instanceof BlockStairs))) {
        int i1 = world.getData(i, j, k);

        if ((0x4 & i1) != 0) {
          return true;
        }
      }

      return false;
    }
    return true;
  }

  public boolean canPlace(World par1World, int par2, int par3, int par4)
  {
    return (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, 5)) || (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, 4)) || (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, 3)) || (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, 2)) || (g(par1World, par2, par3 - 1, par4));
  }

  public void postPlace(World world, int i, int j, int k, int l)
  {
    int i1 = world.getData(i, j, k);

    if ((l == 1) && (g(world, i, j - 1, k))) {
      i1 = 5;
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

    world.setData(i, j, k, i1);
  }

  public void a(World world, int i, int j, int k, Random random) {
    super.a(world, i, j, k, random);
    if (world.getData(i, j, k) == 0)
      onPlace(world, i, j, k);
  }

  public void onPlace(World world, int i, int j, int k)
  {
    if (world.isBlockSolidOnSide(i - 1, j, k, 5))
      world.setData(i, j, k, 1);
    else if (world.isBlockSolidOnSide(i + 1, j, k, 4))
      world.setData(i, j, k, 2);
    else if (world.isBlockSolidOnSide(i, j, k - 1, 3))
      world.setData(i, j, k, 3);
    else if (world.isBlockSolidOnSide(i, j, k + 1, 2))
      world.setData(i, j, k, 4);
    else if (g(world, i, j - 1, k)) {
      world.setData(i, j, k, 5);
    }

    h(world, i, j, k);
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    if (h(world, i, j, k)) {
      int i1 = world.getData(i, j, k);
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

      if ((!g(world, i, j - 1, k)) && (i1 == 5)) {
        flag = true;
      }

      if (flag) {
        b(world, i, j, k, world.getData(i, j, k), 0);
        world.setTypeId(i, j, k, 0);
      }
    }
  }

  private boolean h(World world, int i, int j, int k) {
    if (!canPlace(world, i, j, k)) {
      if (world.getTypeId(i, j, k) == this.id) {
        b(world, i, j, k, world.getData(i, j, k), 0);
        world.setTypeId(i, j, k, 0);
      }

      return false;
    }
    return true;
  }

  public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1)
  {
    int l = world.getData(i, j, k) & 0x7;
    float f = 0.15F;

    if (l == 1) {
      a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
    } else if (l == 2) {
      a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
    } else if (l == 3) {
      a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
    } else if (l == 4) {
      a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
    } else {
      f = 0.1F;
      a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
    }

    return super.a(world, i, j, k, vec3d, vec3d1);
  }
}

