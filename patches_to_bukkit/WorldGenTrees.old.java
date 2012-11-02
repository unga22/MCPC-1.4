package net.minecraft.server;

import java.util.Random;
import org.bukkit.BlockChangeDelegate;

public class WorldGenTrees extends WorldGenerator
  implements BlockSapling.TreeGenerator
{
  private final int a;
  private final boolean b;
  private final int c;
  private final int d;

  public WorldGenTrees(boolean flag)
  {
    this(flag, 4, 0, 0, false);
  }

  public WorldGenTrees(boolean flag, int i, int j, int k, boolean flag1) {
    super(flag);
    this.a = i;
    this.c = j;
    this.d = k;
    this.b = flag1;
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
        byte b0 = 1;
        if (i1 == j) {
          b0 = 0;
        }

        if (i1 >= j + 1 + l - 2) {
          b0 = 2;
        }

        for (int l1 = i - b0; (l1 <= i + b0) && (flag); l1++) {
          for (int j1 = k - b0; (j1 <= k + b0) && (flag); j1++) {
            if ((i1 >= 0) && (i1 < 256)) {
              int k1 = world.getTypeId(l1, i1, j1);
              Block bl = Block.byId[k1];
              if ((k1 != 0) && (bl != null) && (!bl.isLeaves(world, l1, i1, j1)) && (k1 != Block.GRASS.id) && (k1 != Block.DIRT.id) && (bl != null) && (!bl.isWood(world, l1, i1, j1)))
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
        setType(world, i, j - 1, k, Block.DIRT.id);
        byte b0 = 3;
        byte b1 = 0;

        for (int j1 = j - b0 + l; j1 <= j + l; j1++) {
          int k1 = j1 - (j + l);
          int i2 = b1 + 1 - k1 / 2;

          for (int j2 = i - i2; j2 <= i + i2; j2++) {
            int k2 = j2 - i;

            for (int l2 = k - i2; l2 <= k + i2; l2++) {
              int i3 = l2 - k;
              Block bl = Block.byId[world.getTypeId(j2, j1, l2)];
              if (((Math.abs(k2) != i2) || (Math.abs(i3) != i2) || ((random.nextInt(2) != 0) && (k1 != 0))) && ((bl == null) || (bl.canBeReplacedByLeaves(world, j2, j1, l2))))
              {
                setTypeAndData(world, j2, j1, l2, Block.LEAVES.id, this.d);
              }
            }
          }
        }

        for (j1 = 0; j1 < l; j1++) {
          int k1 = world.getTypeId(i, j + j1, k);
          Block bl = Block.byId[k1];

          if ((k1 == 0) || (bl == null) || (bl.isLeaves(world, i, j + j1, k))) {
            setTypeAndData(world, i, j + j1, k, Block.LOG.id, this.c);
            if ((this.b) && (j1 > 0)) {
              if ((random.nextInt(3) > 0) && (world.isEmpty(i - 1, j + j1, k))) {
                setTypeAndData(world, i - 1, j + j1, k, Block.VINE.id, 8);
              }

              if ((random.nextInt(3) > 0) && (world.isEmpty(i + 1, j + j1, k))) {
                setTypeAndData(world, i + 1, j + j1, k, Block.VINE.id, 2);
              }

              if ((random.nextInt(3) > 0) && (world.isEmpty(i, j + j1, k - 1))) {
                setTypeAndData(world, i, j + j1, k - 1, Block.VINE.id, 1);
              }

              if ((random.nextInt(3) > 0) && (world.isEmpty(i, j + j1, k + 1))) {
                setTypeAndData(world, i, j + j1, k + 1, Block.VINE.id, 4);
              }
            }
          }
        }

        if (this.b) {
          for (j1 = j - 3 + l; j1 <= j + l; j1++) {
            int k1 = j1 - (j + l);
            int i2 = 2 - k1 / 2;

            for (int j2 = i - i2; j2 <= i + i2; j2++) {
              for (int k2 = k - i2; k2 <= k + i2; k2++) {
                Block bl = Block.byId[world.getTypeId(j2, j1, k2)];
                if ((bl != null) && (bl.isLeaves(world, j2, j1, k2))) {
                  if ((random.nextInt(4) == 0) && (world.getTypeId(j2 - 1, j1, k2) == 0)) {
                    b(world, j2 - 1, j1, k2, 8);
                  }

                  if ((random.nextInt(4) == 0) && (world.getTypeId(j2 + 1, j1, k2) == 0)) {
                    b(world, j2 + 1, j1, k2, 2);
                  }

                  if ((random.nextInt(4) == 0) && (world.getTypeId(j2, j1, k2 - 1) == 0)) {
                    b(world, j2, j1, k2 - 1, 1);
                  }

                  if ((random.nextInt(4) == 0) && (world.getTypeId(j2, j1, k2 + 1) == 0)) {
                    b(world, j2, j1, k2 + 1, 4);
                  }
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

