package net.minecraft.server;

import java.util.Random;

public class WorldGenDeadBush extends WorldGenerator
{
  private int a;

  public WorldGenDeadBush(int i)
  {
    this.a = i;
  }

  public boolean a(World world, Random random, int i, int j, int k)
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

    for (int i1 = 0; i1 < 4; i1++) {
      int j1 = i + random.nextInt(8) - random.nextInt(8);
      int k1 = j + random.nextInt(4) - random.nextInt(4);
      int l1 = k + random.nextInt(8) - random.nextInt(8);

      if ((world.isEmpty(j1, k1, l1)) && (((BlockFlower)Block.byId[this.a]).f(world, j1, k1, l1))) {
        world.setRawTypeId(j1, k1, l1, this.a);
      }
    }

    return true;
  }
}

