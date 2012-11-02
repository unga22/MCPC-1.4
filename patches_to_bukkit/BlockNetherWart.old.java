package net.minecraft.server;

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.craftbukkit.event.CraftEventFactory;

public class BlockNetherWart extends BlockFlower
{
  protected BlockNetherWart(int i)
  {
    super(i, 226);
    a(true);
    float f = 0.5F;

    a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
  }

  protected boolean d(int i) {
    return i == Block.SOUL_SAND.id;
  }

  public boolean f(World world, int i, int j, int k) {
    return d(world.getTypeId(i, j - 1, k));
  }

  public void a(World world, int i, int j, int k, Random random) {
    int l = world.getData(i, j, k);

    if (l < 3) {
      BiomeBase biomebase = world.getBiome(i, k);

      if (((biomebase instanceof BiomeHell)) && (random.nextInt(10) == 0)) {
        CraftEventFactory.handleBlockGrowEvent(world, i, j, k, this.id, ++l);
      }
    }

    super.a(world, i, j, k, random);
  }

  public int a(int i, int j) {
    return j > 0 ? this.textureId + 1 : j >= 3 ? this.textureId + 2 : this.textureId;
  }

  public int c() {
    return 6;
  }

  public ArrayList<ItemStack> getBlockDropped(World world, int i, int j, int k, int metadata, int fortune)
  {
    ArrayList ret = new ArrayList();
    int n = 1;
    if (metadata >= 3) {
      n = 2 + world.random.nextInt(3) + (fortune > 0 ? world.random.nextInt(fortune + 1) : 0);
    }

    for (int m = 0; m < n; m++) {
      ret.add(new ItemStack(Item.NETHER_STALK));
    }
    return ret;
  }

  public int getDropType(int i, Random random, int j) {
    return 0;
  }

  public int a(Random random) {
    return 0;
  }
}

