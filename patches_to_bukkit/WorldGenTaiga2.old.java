package net.minecraft.server;

import java.util.Random;
import org.bukkit.BlockChangeDelegate;

public class WorldGenTaiga2 extends WorldGenerator
  implements BlockSapling.TreeGenerator
{
  public WorldGenTaiga2(boolean flag)
  {
    super(flag);
  }

  public boolean a(World world, Random random, int i, int j, int k)
  {
    return generate((BlockChangeDelegate)world, random, i, j, k);
  }

  public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k)
  {
    int l = random.nextInt(4) + 6;
    int i1 = 1 + random.nextInt(2);
    int j1 = l - i1;
    int k1 = 2 + random.nextInt(2);
    boolean flag = true;

    if ((j >= 1) && (j + l + 1 <= 256))
    {
      for (int l1 = j; (l1 <= j + 1 + l) && (flag); l1++) {
        boolean flag1 = true;
        int k2;
        int k2;
        if (l1 - j < i1)
          k2 = 0;
        else {
          k2 = k1;
        }

        for (int i2 = i - k2; (i2 <= i + k2) && (flag); i2++) {
          for (int l2 = k - k2; (l2 <= k + k2) && (flag); l2++) {
            if ((l1 >= 0) && (l1 < 256)) {
              int j2 = world.getTypeId(i2, l1, l2);
              Block bl = Block.byId[j2];
              if ((j2 != 0) && (bl != null) && (!bl.isLeaves(world, i2, l1, l2)))
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
      l1 = world.getTypeId(i, j - 1, k);
      if (((l1 == Block.GRASS.id) || (l1 == Block.DIRT.id)) && (j < 256 - l - 1)) {
        setType(world, i, j - 1, k, Block.DIRT.id);
        int k2 = random.nextInt(2);
        int i2 = 1;
        byte b0 = 0;

        for (int j2 = 0; j2 <= j1; j2++) {
          int j3 = j + l - j2;

          for (int i3 = i - k2; i3 <= i + k2; i3++) {
            int k3 = i3 - i;

            for (int l3 = k - k2; l3 <= k + k2; l3++) {
              int i4 = l3 - k;
              Block bl = Block.byId[world.getTypeId(i3, j3, l3)];
              if (((Math.abs(k3) != k2) || (Math.abs(i4) != k2) || (k2 <= 0)) && ((bl == null) || (bl.canBeReplacedByLeaves(world, i3, j3, l3)))) {
                setTypeAndData(world, i3, j3, l3, Block.LEAVES.id, 1);
              }
            }
          }

          if (k2 >= i2) {
            k2 = b0;
            b0 = 1;
            i2++;
            if (i2 > k1)
              i2 = k1;
          }
          else {
            k2++;
          }
        }

        j2 = random.nextInt(3);

        for (int j3 = 0; j3 < l - j2; j3++) {
          int i3 = world.getTypeId(i, j + j3, k);
          Block bl = Block.byId[i3];
          if ((i3 == 0) || (bl == null) || (bl.isLeaves(world, i, j + j3, k))) {
            setTypeAndData(world, i, j + j3, k, Block.LOG.id, 1);
          }
        }

        return true;
      }
      return false;
    }

    return false;
  }
}

