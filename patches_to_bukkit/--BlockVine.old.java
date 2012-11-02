package net.minecraft.server;

import forge.IShearable;
import java.util.ArrayList;
import java.util.Random;

public class BlockVine extends Block
  implements IShearable
{
  public BlockVine(int i)
  {
    super(i, 143, Material.REPLACEABLE_PLANT);
    a(true);
  }

  public void f() {
    a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
  }

  public int c() {
    return 20;
  }

  public boolean a() {
    return false;
  }

  public boolean b() {
    return false;
  }

  public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
    int l = iblockaccess.getData(i, j, k);
    float f = 1.0F;
    float f1 = 1.0F;
    float f2 = 1.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    boolean flag = l > 0;

    if ((l & 0x2) != 0) {
      f3 = Math.max(f3, 0.0625F);
      f = 0.0F;
      f1 = 0.0F;
      f4 = 1.0F;
      f2 = 0.0F;
      f5 = 1.0F;
      flag = true;
    }

    if ((l & 0x8) != 0) {
      f = Math.min(f, 0.9375F);
      f3 = 1.0F;
      f1 = 0.0F;
      f4 = 1.0F;
      f2 = 0.0F;
      f5 = 1.0F;
      flag = true;
    }

    if ((l & 0x4) != 0) {
      f5 = Math.max(f5, 0.0625F);
      f2 = 0.0F;
      f = 0.0F;
      f3 = 1.0F;
      f1 = 0.0F;
      f4 = 1.0F;
      flag = true;
    }

    if ((l & 0x1) != 0) {
      f2 = Math.min(f2, 0.9375F);
      f5 = 1.0F;
      f = 0.0F;
      f3 = 1.0F;
      f1 = 0.0F;
      f4 = 1.0F;
      flag = true;
    }

    if ((!flag) && (d(iblockaccess.getTypeId(i, j + 1, k)))) {
      f1 = Math.min(f1, 0.9375F);
      f4 = 1.0F;
      f = 0.0F;
      f3 = 1.0F;
      f2 = 0.0F;
      f5 = 1.0F;
    }

    a(f, f1, f2, f3, f4, f5);
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    return null;
  }

  public boolean canPlace(World world, int i, int j, int k, int l) {
    switch (l) {
    case 1:
      return d(world.getTypeId(i, j + 1, k));
    case 2:
      return d(world.getTypeId(i, j, k + 1));
    case 3:
      return d(world.getTypeId(i, j, k - 1));
    case 4:
      return d(world.getTypeId(i + 1, j, k));
    case 5:
      return d(world.getTypeId(i - 1, j, k));
    }

    return false;
  }

  private boolean d(int i)
  {
    if (i == 0) {
      return false;
    }
    Block block = Block.byId[i];

    return (block.b()) && (block.material.isSolid());
  }

  private boolean g(World world, int i, int j, int k)
  {
    int l = world.getData(i, j, k);
    int i1 = l;

    if (l > 0) {
      for (int j1 = 0; j1 <= 3; j1++) {
        int k1 = 1 << j1;

        if (((l & k1) != 0) && (!d(world.getTypeId(i + Direction.a[j1], j, k + Direction.b[j1]))) && ((world.getTypeId(i, j + 1, k) != this.id) || ((world.getData(i, j + 1, k) & k1) == 0))) {
          i1 &= (k1 ^ 0xFFFFFFFF);
        }
      }
    }

    if ((i1 == 0) && (!d(world.getTypeId(i, j + 1, k)))) {
      return false;
    }
    if (i1 != l) {
      world.setData(i, j, k, i1);
    }

    return true;
  }

  public void doPhysics(World world, int i, int j, int k, int l)
  {
    if ((!world.isStatic) && (!g(world, i, j, k))) {
      b(world, i, j, k, world.getData(i, j, k), 0);
      world.setTypeId(i, j, k, 0);
    }
  }

  public void a(World world, int i, int j, int k, Random random) {
    if ((!world.isStatic) && (world.random.nextInt(4) == 0)) {
      byte b0 = 4;
      int l = 5;
      boolean flag = false;

      for (int i1 = i - b0; i1 <= i + b0; i1++) {
        for (int j1 = k - b0; j1 <= k + b0; j1++) {
          for (int k1 = j - 1; k1 <= j + 1; k1++) {
            if (world.getTypeId(i1, k1, j1) == this.id) {
              l--;
              if (l <= 0) {
                flag = true;
                break label121;
              }
            }
          }
        }
      }

      label121: i1 = world.getData(i, j, k);
      int j1 = world.random.nextInt(6);
      int k1 = Direction.d[j1];

      if ((j1 == 1) && (j < 255) && (world.isEmpty(i, j + 1, k))) {
        if (flag) {
          return;
        }

        int l1 = world.random.nextInt(16) & i1;
        if (l1 > 0) {
          for (int i2 = 0; i2 <= 3; i2++) {
            if (!d(world.getTypeId(i + Direction.a[i2], j + 1, k + Direction.b[i2]))) {
              l1 &= (1 << i2 ^ 0xFFFFFFFF);
            }
          }

          if (l1 > 0) {
            world.setTypeIdAndData(i, j + 1, k, this.id, l1);
          }

        }

      }
      else if ((j1 >= 2) && (j1 <= 5) && ((i1 & 1 << k1) == 0)) {
        if (flag) {
          return;
        }

        int l1 = world.getTypeId(i + Direction.a[k1], j, k + Direction.b[k1]);
        if ((l1 != 0) && (Block.byId[l1] != null)) {
          if ((Block.byId[l1].material.j()) && (Block.byId[l1].b()))
            world.setData(i, j, k, i1 | 1 << k1);
        }
        else {
          int i2 = k1 + 1 & 0x3;
          int j2 = k1 + 3 & 0x3;
          if (((i1 & 1 << i2) != 0) && (d(world.getTypeId(i + Direction.a[k1] + Direction.a[i2], j, k + Direction.b[k1] + Direction.b[i2]))))
            world.setTypeIdAndData(i + Direction.a[k1], j, k + Direction.b[k1], this.id, 1 << i2);
          else if (((i1 & 1 << j2) != 0) && (d(world.getTypeId(i + Direction.a[k1] + Direction.a[j2], j, k + Direction.b[k1] + Direction.b[j2]))))
            world.setTypeIdAndData(i + Direction.a[k1], j, k + Direction.b[k1], this.id, 1 << j2);
          else if (((i1 & 1 << i2) != 0) && (world.isEmpty(i + Direction.a[k1] + Direction.a[i2], j, k + Direction.b[k1] + Direction.b[i2])) && (d(world.getTypeId(i + Direction.a[i2], j, k + Direction.b[i2]))))
            world.setTypeIdAndData(i + Direction.a[k1] + Direction.a[i2], j, k + Direction.b[k1] + Direction.b[i2], this.id, 1 << (k1 + 2 & 0x3));
          else if (((i1 & 1 << j2) != 0) && (world.isEmpty(i + Direction.a[k1] + Direction.a[j2], j, k + Direction.b[k1] + Direction.b[j2])) && (d(world.getTypeId(i + Direction.a[j2], j, k + Direction.b[j2]))))
            world.setTypeIdAndData(i + Direction.a[k1] + Direction.a[j2], j, k + Direction.b[k1] + Direction.b[j2], this.id, 1 << (k1 + 2 & 0x3));
          else if (d(world.getTypeId(i + Direction.a[k1], j + 1, k + Direction.b[k1])))
            world.setTypeIdAndData(i + Direction.a[k1], j, k + Direction.b[k1], this.id, 0);
        }
      }
      else if (j > 1) {
        int l1 = world.getTypeId(i, j - 1, k);
        if (l1 == 0) {
          int i2 = world.random.nextInt(16) & i1;
          if (i2 > 0)
            world.setTypeIdAndData(i, j - 1, k, this.id, i2);
        }
        else if (l1 == this.id) {
          int i2 = world.random.nextInt(16) & i1;
          int j2 = world.getData(i, j - 1, k);
          if (j2 != (j2 | i2))
            world.setData(i, j - 1, k, j2 | i2);
        }
      }
    }
  }

  public void postPlace(World world, int i, int j, int k, int l)
  {
    byte b0 = 0;

    switch (l) {
    case 2:
      b0 = 1;
      break;
    case 3:
      b0 = 4;
      break;
    case 4:
      b0 = 8;
      break;
    case 5:
      b0 = 2;
    }

    if (b0 != 0)
      world.setData(i, j, k, b0);
  }

  public int getDropType(int i, Random random, int j)
  {
    return 0;
  }

  public int a(Random random) {
    return 0;
  }

  public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
    super.a(world, entityhuman, i, j, k, l);
  }

  public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
    return true;
  }

  public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
    ArrayList ret = new ArrayList();
    ret.add(new ItemStack(this, 1, 0));
    return ret;
  }

  public boolean isLadder(World world, int x, int y, int z) {
    return true;
  }
}

