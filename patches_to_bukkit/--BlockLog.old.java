package net.minecraft.server;

import java.util.Random;

public class BlockLog extends Block
{
  protected BlockLog(int i)
  {
    super(i, Material.WOOD);
    this.textureId = 20;
  }

  public int a(Random random) {
    return 1;
  }

  public int getDropType(int i, Random random, int j) {
    return Block.LOG.id;
  }

  public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
    super.a(world, entityhuman, i, j, k, l);
  }

  public void remove(World world, int i, int j, int k) {
    byte b0 = 4;
    int l = b0 + 1;

    if (world.a(i - l, j - l, k - l, i + l, j + l, k + l))
      for (int i1 = -b0; i1 <= b0; i1++)
        for (int j1 = -b0; j1 <= b0; j1++)
          for (int k1 = -b0; k1 <= b0; k1++) {
            int l1 = world.getTypeId(i + i1, j + j1, k + k1);

            if (Block.byId[l1] != null)
              Block.byId[l1].beginLeavesDecay(world, i + i1, j + j1, k + k1);
          }
  }

  public int a(int i, int j)
  {
    return j == 3 ? 153 : j == 2 ? 117 : j == 1 ? 116 : i == 0 ? 21 : i == 1 ? 21 : 20;
  }

  protected int getDropData(int i) {
    return i;
  }

  public boolean canSustainLeaves(World world, int x, int y, int z)
  {
    return true;
  }

  public boolean isWood(World world, int x, int y, int z)
  {
    return true;
  }
}

