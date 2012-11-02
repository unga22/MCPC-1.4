package net.minecraft.server;

import java.util.Random;
import org.bukkit.BlockChangeDelegate;

public class WorldGenGroundBush extends WorldGenerator
  implements BlockSapling.TreeGenerator
{
  private int a;
  private int b;

  public WorldGenGroundBush(int i, int j)
  {
    this.b = i;
    this.a = j;
  }

  public boolean a(World world, Random random, int i, int j, int k)
  {
    return generate((BlockChangeDelegate)world, random, i, j, k);
  }

  public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k)
  {
    Block block = null;
    do
    {
      block = Block.byId[world.getTypeId(i, j, k)];
      if ((block != null) && (!block.isLeaves(world, i, j, k)))
      {
        break;
      }
      j--;
    }
    while (j > 0);

    int i1 = world.getTypeId(i, j, k);

    if ((i1 == Block.DIRT.id) || (i1 == Block.GRASS.id)) {
      j++;
      setTypeAndData(world, i, j, k, Block.LOG.id, this.b);

      for (int j1 = j; j1 <= j + 2; j1++) {
        int k1 = j1 - j;
        int l1 = 2 - k1;

        for (int i2 = i - l1; i2 <= i + l1; i2++) {
          int j2 = i2 - i;

          for (int k2 = k - l1; k2 <= k + l1; k2++) {
            int l2 = k2 - k;

            Block bl = Block.byId[world.getTypeId(i2, j1, k2)];

            if (((Math.abs(j2) != l1) || (Math.abs(l2) != l1) || (random.nextInt(2) != 0)) && ((bl == null) || (bl.canBeReplacedByLeaves(world, i2, j1, k2))))
              setTypeAndData(world, i2, j1, k2, Block.LEAVES.id, this.a);
          }
        }
      }
    }
    else
    {
      return false;
    }

    return true;
  }
}

