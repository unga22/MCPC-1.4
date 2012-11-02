package net.minecraft.server;

import forge.ForgeHooks;
import forge.IShearable;
import java.util.ArrayList;
import java.util.Random;

public class BlockLongGrass extends BlockFlower
  implements IShearable
{
  protected BlockLongGrass(int i, int j)
  {
    super(i, j, Material.REPLACEABLE_PLANT);
    float f = 0.4F;

    a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
  }

  public int a(int i, int j) {
    return j == 0 ? this.textureId + 16 : j == 2 ? this.textureId + 16 + 1 : j == 1 ? this.textureId : this.textureId;
  }

  public int getDropType(int i, Random random, int j)
  {
    return -1;
  }

  public int getDropCount(int i, Random random) {
    return 1 + random.nextInt(i * 2 + 1);
  }

  public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
    super.a(world, entityhuman, i, j, k, l);
  }

  public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
  {
    ArrayList ret = new ArrayList();
    if (world.random.nextInt(8) != 0) {
      return ret;
    }
    ItemStack item = ForgeHooks.getGrassSeed(world);
    if (item != null) {
      ret.add(item);
    }
    return ret;
  }

  public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
    return true;
  }

  public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
    ArrayList ret = new ArrayList();
    ret.add(new ItemStack(this, 1, world.getData(x, y, z)));
    return ret;
  }
}

