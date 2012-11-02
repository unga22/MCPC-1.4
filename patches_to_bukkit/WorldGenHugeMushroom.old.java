package net.minecraft.server;

import java.util.List;
import java.util.Random;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;

public class WorldGenHugeMushroom extends WorldGenerator
  implements BlockSapling.TreeGenerator
{
  private int a = -1;

  public WorldGenHugeMushroom(int i) {
    super(true);
    this.a = i;
  }

  public WorldGenHugeMushroom() {
    super(false);
  }

  public boolean a(World world, Random random, int i, int j, int k)
  {
    return grow((BlockChangeDelegate)world, random, i, j, k, null, null, null);
  }

  public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k) {
    return grow(world, random, i, j, k, null, null, null);
  }

  public boolean grow(BlockChangeDelegate world, Random random, int i, int j, int k, StructureGrowEvent event, ItemStack itemstack, CraftWorld bukkitWorld)
  {
    int l = random.nextInt(2);

    if (this.a >= 0) {
      l = this.a;
    }

    int i1 = random.nextInt(3) + 4;
    boolean flag = true;

    if ((j >= 1) && (j + i1 + 1 < 256))
    {
      for (int j1 = j; j1 <= j + 1 + i1; j1++) {
        byte b0 = 3;

        if (j1 == j) {
          b0 = 0;
        }

        for (int k1 = i - b0; (k1 <= i + b0) && (flag); k1++) {
          for (int l1 = k - b0; (l1 <= k + b0) && (flag); l1++) {
            if ((j1 >= 0) && (j1 < 256)) {
              int i2 = world.getTypeId(k1, j1, l1);
              Block block = Block.byId[i2];

              if ((i2 != 0) && (block != null) && (!block.isLeaves(world, k1, j1, l1)))
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
      j1 = world.getTypeId(i, j - 1, k);
      if ((j1 != Block.DIRT.id) && (j1 != Block.GRASS.id) && (j1 != Block.MYCEL.id)) {
        return false;
      }
      if (((world.getTypeId(i, j, k) != 0) && (!Block.byId[world.getTypeId(i, j, k)].material.isReplacable())) || (((world instanceof World)) && (!Block.BROWN_MUSHROOM.canPlace((World)world, i, j, k)))) {
        return false;
      }

      if (event == null) {
        setTypeAndData(world, i, j - 1, k, Block.DIRT.id, 0);
      } else {
        BlockState dirtState = bukkitWorld.getBlockAt(i, j - 1, k).getState();
        dirtState.setTypeId(Block.DIRT.id);
        event.getBlocks().add(dirtState);
      }

      int j2 = j + i1;

      if (l == 1) {
        j2 = j + i1 - 3;
      }

      for (int k1 = j2; k1 <= j + i1; k1++) {
        int l1 = 1;
        if (k1 < j + i1) {
          l1++;
        }

        if (l == 0) {
          l1 = 3;
        }

        for (int i2 = i - l1; i2 <= i + l1; i2++) {
          for (int k2 = k - l1; k2 <= k + l1; k2++) {
            int l2 = 5;

            if (i2 == i - l1) {
              l2--;
            }

            if (i2 == i + l1) {
              l2++;
            }

            if (k2 == k - l1) {
              l2 -= 3;
            }

            if (k2 == k + l1) {
              l2 += 3;
            }

            if ((l == 0) || (k1 < j + i1)) {
              if (((i2 == i - l1) || (i2 == i + l1)) && ((k2 == k - l1) || (k2 == k + l1)))
              {
                continue;
              }
              if ((i2 == i - (l1 - 1)) && (k2 == k - l1)) {
                l2 = 1;
              }

              if ((i2 == i - l1) && (k2 == k - (l1 - 1))) {
                l2 = 1;
              }

              if ((i2 == i + (l1 - 1)) && (k2 == k - l1)) {
                l2 = 3;
              }

              if ((i2 == i + l1) && (k2 == k - (l1 - 1))) {
                l2 = 3;
              }

              if ((i2 == i - (l1 - 1)) && (k2 == k + l1)) {
                l2 = 7;
              }

              if ((i2 == i - l1) && (k2 == k + (l1 - 1))) {
                l2 = 7;
              }

              if ((i2 == i + (l1 - 1)) && (k2 == k + l1)) {
                l2 = 9;
              }

              if ((i2 == i + l1) && (k2 == k + (l1 - 1))) {
                l2 = 9;
              }
            }

            if ((l2 == 5) && (k1 < j + i1)) {
              l2 = 0;
            }

            Block bl = Block.byId[world.getTypeId(i2, k1, k2)];

            if (((l2 != 0) || (j >= j + i1 - 1)) && ((bl == null) || (bl.canBeReplacedByLeaves(world, i2, k1, k2))))
            {
              if (event == null) {
                setTypeAndData(world, i2, k1, k2, Block.BIG_MUSHROOM_1.id + l, l2);
              } else {
                BlockState state = bukkitWorld.getBlockAt(i2, k1, k2).getState();
                state.setTypeId(Block.BIG_MUSHROOM_1.id + l);
                state.setData(new MaterialData(Block.BIG_MUSHROOM_1.id + l, (byte)l2));
                event.getBlocks().add(state);
              }
            }
          }
        }

      }

      for (k1 = 0; k1 < i1; k1++)
      {
        int l1;
        Block bl = Block.byId[(l1 = world.getTypeId(i, j + k1, k))];
        if ((bl == null) || (bl.canBeReplacedByLeaves(world, i, j + k1, k)))
        {
          if (event == null) {
            setTypeAndData(world, i, j + k1, k, Block.BIG_MUSHROOM_1.id + l, 10);
          } else {
            BlockState state = bukkitWorld.getBlockAt(i, j + k1, k).getState();
            state.setTypeId(Block.BIG_MUSHROOM_1.id + l);
            state.setData(new MaterialData(Block.BIG_MUSHROOM_1.id + l, (byte)10));
            event.getBlocks().add(state);
          }
        }

      }

      if (event != null) {
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
          for (BlockState state : event.getBlocks()) {
            state.update(true);
          }
        }

      }

      return true;
    }

    return false;
  }
}

