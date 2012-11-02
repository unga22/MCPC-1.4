package net.minecraft.server;

import java.util.Random;
import org.bukkit.BlockChangeDelegate;

public class WorldGenMegaTree extends WorldGenerator
  implements BlockSapling.TreeGenerator
{
  private final int a;
  private final int b;
  private final int c;

  public WorldGenMegaTree(boolean flag, int i, int j, int k)
  {
    super(flag);
    this.a = i;
    this.b = j;
    this.c = k;
  }

  public boolean a(World world, Random random, int i, int j, int k)
  {
    return generate((BlockChangeDelegate)world, random, i, j, k);
  }

  public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k)
  {
    int l = random.nextInt(3) + this.a;
    boolean flag = true;

    if ((j >= 1) && (j + l + 1 <= 256))
    {
      for (int i1 = j; i1 <= j + 1 + l; i1++) {
        byte b0 = 2;

        if (i1 == j) {
          b0 = 1;
        }

        if (i1 >= j + 1 + l - 2) {
          b0 = 2;
        }

        for (int j1 = i - b0; (j1 <= i + b0) && (flag); j1++) {
          for (int k1 = k - b0; (k1 <= k + b0) && (flag); k1++) {
            if ((i1 >= 0) && (i1 < 256)) {
              int l1 = world.getTypeId(j1, i1, k1);
              if ((l1 != 0) && (Block.byId[l1] != null) && (!Block.byId[l1].isLeaves(world, j1, i1, k1)) && (l1 != Block.GRASS.id) && (l1 != Block.DIRT.id) && (Block.byId[l1] != null) && (!Block.byId[l1].isWood(world, j1, i1, k1)) && (l1 != Block.SAPLING.id))
              {
                flag = false;
              }
            } else {
              flag = false;
            }
          }
        }
      }

      if (!flag) {
        return false;
      }
      i1 = world.getTypeId(i, j - 1, k);
      if (((i1 == Block.GRASS.id) || (i1 == Block.DIRT.id)) && (j < 256 - l - 1)) {
        world.setRawTypeId(i, j - 1, k, Block.DIRT.id);
        world.setRawTypeId(i + 1, j - 1, k, Block.DIRT.id);
        world.setRawTypeId(i, j - 1, k + 1, Block.DIRT.id);
        world.setRawTypeId(i + 1, j - 1, k + 1, Block.DIRT.id);
        a(world, i, k, j + l, 2, random);

        for (int i2 = j + l - 2 - random.nextInt(4); i2 > j + l / 2; i2 -= 2 + random.nextInt(4)) {
          float f = random.nextFloat() * 3.141593F * 2.0F;

          int k1 = i + (int)(0.5F + MathHelper.cos(f) * 4.0F);
          int l1 = k + (int)(0.5F + MathHelper.sin(f) * 4.0F);
          a(world, k1, l1, i2, 0, random);

          for (int j2 = 0; j2 < 5; j2++) {
            k1 = i + (int)(1.5F + MathHelper.cos(f) * j2);
            l1 = k + (int)(1.5F + MathHelper.sin(f) * j2);
            setTypeAndData(world, k1, i2 - 3 + j2 / 2, l1, Block.LOG.id, this.b);
          }
        }

        for (int j1 = 0; j1 < l; j1++) {
          int k1 = world.getTypeId(i, j + j1, k);
          if ((k1 == 0) || (Block.byId[k1] == null) || (Block.byId[k1].isLeaves(world, i, j + j1, k))) {
            setTypeAndData(world, i, j + j1, k, Block.LOG.id, this.b);
            if (j1 > 0) {
              if ((random.nextInt(3) > 0) && (world.isEmpty(i - 1, j + j1, k))) {
                setTypeAndData(world, i - 1, j + j1, k, Block.VINE.id, 8);
              }

              if ((random.nextInt(3) > 0) && (world.isEmpty(i, j + j1, k - 1))) {
                setTypeAndData(world, i, j + j1, k - 1, Block.VINE.id, 1);
              }
            }
          }

          if (j1 < l - 1) {
            k1 = world.getTypeId(i + 1, j + j1, k);
            if ((k1 == 0) || (Block.byId[k1] == null) || (Block.byId[k1].isLeaves(world, i + 1, j + j1, k))) {
              setTypeAndData(world, i + 1, j + j1, k, Block.LOG.id, this.b);
              if (j1 > 0) {
                if ((random.nextInt(3) > 0) && (world.isEmpty(i + 2, j + j1, k))) {
                  setTypeAndData(world, i + 2, j + j1, k, Block.VINE.id, 2);
                }

                if ((random.nextInt(3) > 0) && (world.isEmpty(i + 1, j + j1, k - 1))) {
                  setTypeAndData(world, i + 1, j + j1, k - 1, Block.VINE.id, 1);
                }
              }
            }

            k1 = world.getTypeId(i + 1, j + j1, k + 1);
            if ((k1 == 0) || (Block.byId[k1] == null) || (Block.byId[k1].isLeaves(world, i + 1, j + j1, k + 1))) {
              setTypeAndData(world, i + 1, j + j1, k + 1, Block.LOG.id, this.b);
              if (j1 > 0) {
                if ((random.nextInt(3) > 0) && (world.isEmpty(i + 2, j + j1, k + 1))) {
                  setTypeAndData(world, i + 2, j + j1, k + 1, Block.VINE.id, 2);
                }

                if ((random.nextInt(3) > 0) && (world.isEmpty(i + 1, j + j1, k + 2))) {
                  setTypeAndData(world, i + 1, j + j1, k + 2, Block.VINE.id, 4);
                }
              }
            }

            k1 = world.getTypeId(i, j + j1, k + 1);
            if ((k1 == 0) || (Block.byId[k1] == null) || (Block.byId[k1].isLeaves(world, i, j + j1, k + 1))) {
              setTypeAndData(world, i, j + j1, k + 1, Block.LOG.id, this.b);
              if (j1 > 0) {
                if ((random.nextInt(3) > 0) && (world.isEmpty(i - 1, j + j1, k + 1))) {
                  setTypeAndData(world, i - 1, j + j1, k + 1, Block.VINE.id, 8);
                }

                if ((random.nextInt(3) > 0) && (world.isEmpty(i, j + j1, k + 2))) {
                  setTypeAndData(world, i, j + j1, k + 2, Block.VINE.id, 4);
                }
              }
            }
          }
        }

        return true;
      }
      return false;
    }

    return false;
  }

  private void a(BlockChangeDelegate world, int i, int j, int k, int l, Random random)
  {
    byte b0 = 2;

    for (int i1 = k - b0; i1 <= k; i1++) {
      int j1 = i1 - k;
      int k1 = l + 1 - j1;

      for (int l1 = i - k1; l1 <= i + k1 + 1; l1++) {
        int i2 = l1 - i;

        for (int j2 = j - k1; j2 <= j + k1 + 1; j2++) {
          int k2 = j2 - j;

          Block bl = Block.byId[world.getTypeId(l1, i1, j2)];
          if (((i2 >= 0) || (k2 >= 0) || (i2 * i2 + k2 * k2 <= k1 * k1)) && (((i2 <= 0) && (k2 <= 0)) || ((i2 * i2 + k2 * k2 <= (k1 + 1) * (k1 + 1)) && ((random.nextInt(4) != 0) || (i2 * i2 + k2 * k2 <= (k1 - 1) * (k1 - 1))) && ((bl == null) || (bl.canBeReplacedByLeaves(world, l1, i1, j2))))))
          {
            setTypeAndData(world, l1, i1, j2, Block.LEAVES.id, this.c);
          }
        }
      }
    }
  }
}

