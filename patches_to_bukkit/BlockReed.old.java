package net.minecraft.server;

import java.util.Random;
import org.bukkit.craftbukkit.event.CraftEventFactory;

public class BlockReed extends Block
{
  protected BlockReed(int i, int j)
  {
    super(i, Material.PLANT);
    this.textureId = j;
    float f = 0.375F;

    a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
    a(true);
  }

  public void a(World world, int i, int j, int k, Random random) {
    if (world.isEmpty(i, j + 1, k))
    {
      for (int l = 1; world.getTypeId(i, j - l, k) == this.id; l++);
      if (l < 3) {
        int i1 = world.getData(i, j, k);

        if (i1 == 15) {
          CraftEventFactory.handleBlockGrowEvent(world, i, j + 1, k, this.id, 0);
          world.setData(i, j, k, 0);
        } else {
          world.setData(i, j, k, i1 + 1);
        }
      }
    }
  }

  public boolean canPlace(World world, int i, int j, int k) {
    int l = world.getTypeId(i, j - 1, k);

    return l == this.id;
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    g(world, i, j, k);
  }

  protected final void g(World world, int i, int j, int k) {
    if (!f(world, i, j, k)) {
      b(world, i, j, k, world.getData(i, j, k), 0);
      world.setTypeId(i, j, k, 0);
    }
  }

  public boolean f(World world, int i, int j, int k) {
    return canPlace(world, i, j, k);
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    return null;
  }

  public int getDropType(int i, Random random, int j) {
    return Item.SUGAR_CANE.id;
  }

  public boolean a() {
    return false;
  }

  public boolean b() {
    return false;
  }

  public int c() {
    return 1;
  }
}

