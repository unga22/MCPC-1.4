package net.minecraft.server;

import java.util.Random;
import org.bukkit.BlockChangeDelegate;

public class WorldGenForest extends WorldGenerator
  implements BlockSapling.TreeGenerator
{
  public WorldGenForest(boolean flag)
  {
    super(flag);
  }

  public boolean a(World world, Random random, int i, int j, int k)
  {
    return generate((BlockChangeDelegate)world, random, i, j, k);
  }

  public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k)
  {
    int l = random.nextInt(3) + 5;
    boolean flag = true;

    if ((j >= 1) && (j + l + 1 <= 256))
    {
      for (int i1 = j; i1 <= j + 1 + l; i1++) {
        byte b0 = 1;

        if (i1 == j) {
          b0 = 0;
        }

        if (i1 >= j + 1 + l - 2) {
          b0 = 2;
        }

        for (int j1 = i - b0; (j1 <= i + b0) && (flag); j1++) {
          for (int k1 = k - b0; (k1 <= k + b0) && (flag); k1++) {
            if ((i1 >= 0) && (i1 < 256)) {
              int l1 = world.getTypeId(j1, i1, k1);
              Block block = Block.byId[l1];
              if ((l1 != 0) && (block != null) && (block.canBeReplacedByLeaves(world, j1, i1, k1)))
                flag = false;
            }
            else {
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
        setType(world, i, j - 1, k, Block.DIRT.id);

        for (int i2 = j - 3 + l; i2 <= j + l; i2++) {
          int j1 = i2 - (j + l);
          int k1 = 1 - j1 / 2;

          for (int l1 = i - k1; l1 <= i + k1; l1++) {
            int j2 = l1 - i;

            for (int k2 = k - k1; k2 <= k + k1; k2++) {
              int l2 = k2 - k;

              Block block = Block.byId[world.getTypeId(l1, i2, k2)];
              if (((Math.abs(j2) != k1) || (Math.abs(l2) != k1) || ((random.nextInt(2) != 0) && (j1 != 0))) && ((block == null) || (block.canBeReplacedByLeaves(world, l1, i2, k2))))
              {
                setTypeAndData(world, l1, i2, k2, Block.LEAVES.id, 2);
              }
            }
          }
        }

        for (i2 = 0; i2 < l; i2++) {
          int j1 = world.getTypeId(i, j + i2, k);
          Block block = Block.byId[j1];

          if ((j1 == 0) || ((block != null) && (block.isLeaves(world, i, j + i2, k)))) {
            setTypeAndData(world, i, j + i2, k, Block.LOG.id, 2);
          }
        }

        return true;
      }
      return false;
    }

    return false;
  }
}

