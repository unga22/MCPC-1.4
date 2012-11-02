package net.minecraft.server;

import forge.IShearable;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.plugin.PluginManager;

public class BlockLeaves extends BlockTransparant
  implements IShearable
{
  private int c;
  int[] a;

  protected BlockLeaves(int i, int j)
  {
    super(i, j, Material.LEAVES, false);
    this.c = j;
    a(true);
  }

  public void remove(World world, int i, int j, int k) {
    byte b0 = 1;
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

  public void a(World world, int i, int j, int k, Random random)
  {
    if (!world.isStatic) {
      int l = world.getData(i, j, k);

      if (((l & 0x8) != 0) && ((l & 0x4) == 0)) {
        byte b0 = 4;
        int i1 = b0 + 1;
        byte b1 = 32;
        int j1 = b1 * b1;
        int k1 = b1 / 2;

        if (this.a == null) {
          this.a = new int[b1 * b1 * b1];
        }

        if (world.a(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1))
        {
          for (int l1 = -b0; l1 <= b0; l1++) {
            for (int i2 = -b0; i2 <= b0; i2++) {
              for (int j2 = -b0; j2 <= b0; j2++) {
                int k2 = world.getTypeId(i + l1, j + i2, k + j2);

                Block block = Block.byId[k2];

                if ((block != null) && (block.canSustainLeaves(world, i + l1, j + i2, k + j2)))
                  this.a[((l1 + k1) * j1 + (i2 + k1) * b1 + j2 + k1)] = 0;
                else if ((block != null) && (block.isLeaves(world, i + l1, j + i2, k + j2)))
                  this.a[((l1 + k1) * j1 + (i2 + k1) * b1 + j2 + k1)] = -2;
                else {
                  this.a[((l1 + k1) * j1 + (i2 + k1) * b1 + j2 + k1)] = -1;
                }
              }
            }
          }

          for (l1 = 1; l1 <= 4; l1++) {
            for (int i2 = -b0; i2 <= b0; i2++) {
              for (int j2 = -b0; j2 <= b0; j2++) {
                for (int k2 = -b0; k2 <= b0; k2++) {
                  if (this.a[((i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1)] == l1 - 1) {
                    if (this.a[((i2 + k1 - 1) * j1 + (j2 + k1) * b1 + k2 + k1)] == -2) {
                      this.a[((i2 + k1 - 1) * j1 + (j2 + k1) * b1 + k2 + k1)] = l1;
                    }

                    if (this.a[((i2 + k1 + 1) * j1 + (j2 + k1) * b1 + k2 + k1)] == -2) {
                      this.a[((i2 + k1 + 1) * j1 + (j2 + k1) * b1 + k2 + k1)] = l1;
                    }

                    if (this.a[((i2 + k1) * j1 + (j2 + k1 - 1) * b1 + k2 + k1)] == -2) {
                      this.a[((i2 + k1) * j1 + (j2 + k1 - 1) * b1 + k2 + k1)] = l1;
                    }

                    if (this.a[((i2 + k1) * j1 + (j2 + k1 + 1) * b1 + k2 + k1)] == -2) {
                      this.a[((i2 + k1) * j1 + (j2 + k1 + 1) * b1 + k2 + k1)] = l1;
                    }

                    if (this.a[((i2 + k1) * j1 + (j2 + k1) * b1 + (k2 + k1 - 1))] == -2) {
                      this.a[((i2 + k1) * j1 + (j2 + k1) * b1 + (k2 + k1 - 1))] = l1;
                    }

                    if (this.a[((i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1 + 1)] == -2) {
                      this.a[((i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1 + 1)] = l1;
                    }
                  }
                }
              }
            }
          }
        }

        int l1 = this.a[(k1 * j1 + k1 * b1 + k1)];
        if (l1 >= 0)
          world.setRawData(i, j, k, l & 0xFFFFFFF7);
        else
          g(world, i, j, k);
      }
    }
  }

  private void g(World world, int i, int j, int k)
  {
    LeavesDecayEvent event = new LeavesDecayEvent(world.getWorld().getBlockAt(i, j, k));
    world.getServer().getPluginManager().callEvent(event);

    if (event.isCancelled()) return;

    b(world, i, j, k, world.getData(i, j, k), 0);
    world.setTypeId(i, j, k, 0);
  }

  public int a(Random random) {
    return random.nextInt(20) == 0 ? 1 : 0;
  }

  public int getDropType(int i, Random random, int j) {
    return Block.SAPLING.id;
  }

  public void dropNaturally(World world, int i, int j, int k, int l, float f, int i1) {
    if (!world.isStatic) {
      byte b0 = 20;

      if ((l & 0x3) == 3) {
        b0 = 40;
      }

      if (world.random.nextInt(b0) == 0) {
        int j1 = getDropType(l, world.random, i1);

        a(world, i, j, k, new ItemStack(j1, 1, getDropData(l)));
      }

      if (((l & 0x3) == 0) && (world.random.nextInt(200) == 0))
        a(world, i, j, k, new ItemStack(Item.APPLE, 1, 0));
    }
  }

  public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l)
  {
    super.a(world, entityhuman, i, j, k, l);
  }

  protected int getDropData(int i) {
    return i & 0x3;
  }

  public boolean a() {
    return !this.b;
  }

  public int a(int i, int j) {
    return (j & 0x3) == 3 ? this.textureId + 144 : (j & 0x3) == 1 ? this.textureId + 80 : this.textureId;
  }

  public void b(World world, int i, int j, int k, Entity entity) {
    super.b(world, i, j, k, entity);
  }

  public boolean isShearable(ItemStack item, World world, int x, int y, int z)
  {
    return true;
  }

  public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune)
  {
    ArrayList ret = new ArrayList();
    ret.add(new ItemStack(this, 1, world.getData(x, y, z) & 0x3));
    return ret;
  }

  public void beginLeavesDecay(World world, int x, int y, int z)
  {
    world.setData(x, y, z, world.getData(x, y, z) | 0x8);
  }

  public boolean isLeaves(World world, int x, int y, int z)
  {
    return true;
  }
}

