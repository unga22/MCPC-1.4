package net.minecraft.server;

import java.util.Random;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockFadeEvent;

public class BlockSnow extends Block
{
  protected BlockSnow(int i, int j)
  {
    super(i, j, Material.SNOW_LAYER);
    a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    a(true);
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    int l = world.getData(i, j, k) & 0x7;

    return l >= 3 ? AxisAlignedBB.b(i + this.minX, j + this.minY, k + this.minZ, i + this.maxX, j + 0.5F, k + this.maxZ) : null;
  }

  public boolean a() {
    return false;
  }

  public boolean b() {
    return false;
  }

  public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
    int l = iblockaccess.getData(i, j, k) & 0x7;
    float f = 2 * (1 + l) / 16.0F;

    a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
  }

  public boolean canPlace(World world, int i, int j, int k) {
    int l = world.getTypeId(i, j - 1, k);
    Block block = Block.byId[l];
    return (l != 0) && (((block != null) && (block.isLeaves(world, i, j - 1, k))) || (Block.byId[l].a())) ? world.getMaterial(i, j - 1, k).isSolid() : false;
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    g(world, i, j, k);
  }

  private boolean g(World world, int i, int j, int k) {
    if (!canPlace(world, i, j, k))
    {
      world.setRawTypeId(i, j, k, 0);
      world.notify(i, j, k);
      return false;
    }
    return true;
  }

  public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l)
  {
    b(world, i, j, k, l, 0);
    entityhuman.a(StatisticList.C[this.id], 1);
  }

  public int getDropType(int i, Random random, int j) {
    return Item.SNOW_BALL.id;
  }

  public int a(Random random) {
    return 1;
  }

  public void a(World world, int i, int j, int k, Random random) {
    if (world.a(EnumSkyBlock.BLOCK, i, j, k) > 11)
    {
      if (CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(i, j, k), 0).isCancelled()) {
        return;
      }

      world.setTypeId(i, j, k, 0);
    }
  }
}

