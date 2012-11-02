package net.minecraft.server;

import java.util.Random;

public class WorldGenGrass extends WorldGenerator
{
  private int a;
  private int b;

  public WorldGenGrass(int i, int j)
  {
    this.a = i;
    this.b = j;
  }

  public boolean a(World world, Random random, int i, int j, int k)
  {
    Block bl = Block.byId[world.getTypeId(i, j, k)];
    do {
      if ((bl != null) && (!bl.isLeaves(world, i, j, k)))
      {
        break;
      }
      j--;
    }
    while (j > 0);

    for (int i1 = 0; i1 < 128; i1++) {
      int j1 = i + random.nextInt(8) - random.nextInt(8);
      int k1 = j + random.nextInt(4) - random.nextInt(4);
      int l1 = k + random.nextInt(8) - random.nextInt(8);

      if ((world.isEmpty(j1, k1, l1)) && (((BlockFlower)Block.byId[this.a]).f(world, j1, k1, l1))) {
        world.setRawTypeIdAndData(j1, k1, l1, this.a, this.b);
      }
    }

    return true;
  }
}

