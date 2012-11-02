package net.minecraft.server;

import java.util.Random;
import org.bukkit.BlockChangeDelegate;

public class WorldGenSwampTree extends WorldGenerator
  implements BlockSapling.TreeGenerator
{
  public boolean a(World world, Random random, int i, int j, int k)
  {
    return generate((BlockChangeDelegate)world, random, i, j, k);
  }

  public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k)
  {
    for (int l = random.nextInt(4) + 5; (world.getTypeId(i, j - 1, k) != 0) && (Block.byId[world.getTypeId(i, j - 1, k)].material == Material.WATER); j--);
    boolean flag = true;

    if ((j >= 1) && (j + l + 1 <= 128))
    {
      for (int i1 = j; i1 <= j + 1 + l; i1++) {
        byte b0 = 1;

        if (i1 == j) {
          b0 = 0;
        }

        if (i1 >= j + 1 + l - 2) {
          b0 = 3;
        }

        for (int j1 = i - b0; (j1 <= i + b0) && (flag); j1++) {
          for (int k1 = k - b0; (k1 <= k + b0) && (flag); k1++) {
            if ((i1 >= 0) && (i1 < 128)) {
              int l1 = world.getTypeId(j1, i1, k1);
              if ((l1 != 0) && (Block.byId[l1] != null) && (!Block.byId[l1].isLeaves(world, j1, i1, k1)))
                if ((l1 != Block.STATIONARY_WATER.id) && (l1 != Block.WATER.id))
                  flag = false;
                else if (i1 > j)
                  flag = false;
            }
            else
            {
              flag = false;
            }
          }
        }
      }

      if (!flag) {
        return false;
      }
      i1 = world.getTypeId(i, j - 1, k);
      if (((i1 == Block.GRASS.id) || (i1 == Block.DIRT.id)) && (j < 128 - l - 1)) {
        setType(world, i, j - 1, k, Block.DIRT.id);

        for (int j2 = j - 3 + l; j2 <= j + l; j2++) {
          int j1 = j2 - (j + l);
          int k1 = 2 - j1 / 2;

          for (int l1 = i - k1; l1 <= i + k1; l1++) {
            int i2 = l1 - i;

            for (int k2 = k - k1; k2 <= k + k1; k2++) {
              int l2 = k2 - k;
              Block bl = Block.byId[world.getTypeId(l1, j2, k2)];
              if (((Math.abs(i2) != k1) || (Math.abs(l2) != k1) || ((random.nextInt(2) != 0) && (j1 != 0))) && ((bl == null) || (bl.canBeReplacedByLeaves(world, l1, j2, k2))))
              {
                setType(world, l1, j2, k2, Block.LEAVES.id);
              }
            }
          }
        }

        for (j2 = 0; j2 < l; j2++) {
          int j1 = world.getTypeId(i, j + j2, k);
          Block bl = Block.byId[j1];
          if ((j1 == 0) || ((bl != null) && (bl.isLeaves(world, i, j + j2, k))) || (j1 == Block.WATER.id) || (j1 == Block.STATIONARY_WATER.id)) {
            setType(world, i, j + j2, k, Block.LOG.id);
          }
        }

        for (j2 = j - 3 + l; j2 <= j + l; j2++) {
          int j1 = j2 - (j + l);
          int k1 = 2 - j1 / 2;

          for (int l1 = i - k1; l1 <= i + k1; l1++) {
            for (int i2 = k - k1; i2 <= k + k1; i2++) {
              Block bl = Block.byId[world.getTypeId(l1, j2, i2)];
              if ((bl != null) && (bl.isLeaves(world, l1, j2, i2))) {
                if ((random.nextInt(4) == 0) && (world.getTypeId(l1 - 1, j2, i2) == 0)) {
                  b(world, l1 - 1, j2, i2, 8);
                }

                if ((random.nextInt(4) == 0) && (world.getTypeId(l1 + 1, j2, i2) == 0)) {
                  b(world, l1 + 1, j2, i2, 2);
                }

                if ((random.nextInt(4) == 0) && (world.getTypeId(l1, j2, i2 - 1) == 0)) {
                  b(world, l1, j2, i2 - 1, 1);
                }

                if ((random.nextInt(4) == 0) && (world.getTypeId(l1, j2, i2 + 1) == 0)) {
                  b(world, l1, j2, i2 + 1, 4);
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

  private void b(BlockChangeDelegate world, int i, int j, int k, int l)
  {
    setTypeAndData(world, i, j, k, Block.VINE.id, l);
    int i1 = 4;
    while (true)
    {
      j--;
      if ((world.getTypeId(i, j, k) != 0) || (i1 <= 0)) {
        return;
      }

      setTypeAndData(world, i, j, k, Block.VINE.id, l);
      i1--;
    }
  }
}

