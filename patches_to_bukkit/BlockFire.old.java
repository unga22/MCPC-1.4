package net.minecraft.server;

import java.util.Random;
import org.bukkit.Server;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;

public class BlockFire extends Block
{
  private int[] a = new int[256];
  private int[] b = new int[256];

  protected BlockFire(int i, int j) {
    super(i, j, Material.FIRE);
    a(true);
  }

  public void k() {
    this.a = Block.blockFireSpreadSpeed;
    this.b = Block.blockFlammability;
    a(Block.WOOD.id, 5, 20);
    a(Block.FENCE.id, 5, 20);
    a(Block.WOOD_STAIRS.id, 5, 20);
    a(Block.LOG.id, 5, 5);
    a(Block.LEAVES.id, 30, 60);
    a(Block.BOOKSHELF.id, 30, 20);
    a(Block.TNT.id, 15, 100);
    a(Block.LONG_GRASS.id, 60, 100);
    a(Block.WOOL.id, 30, 60);
    a(Block.VINE.id, 15, 100);
  }

  private void a(int i, int j, int k)
  {
    Block.setBurnProperties(i, j, k);
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    return null;
  }

  public boolean a() {
    return false;
  }

  public boolean b() {
    return false;
  }

  public int c() {
    return 3;
  }

  public int a(Random random) {
    return 0;
  }

  public int d() {
    return 30;
  }

  public void a(World world, int i, int j, int k, Random random)
  {
    Block base = Block.byId[world.getTypeId(i, j - 1, k)];
    boolean flag = (base != null) && (base.isFireSource(world, i, j - 1, k, world.getData(i, j - 1, k), 0));

    if (((world.worldProvider instanceof WorldProviderTheEnd)) && (world.getTypeId(i, j - 1, k) == Block.BEDROCK.id)) {
      flag = true;
    }

    if (!canPlace(world, i, j, k)) {
      fireExtinguished(world, i, j, k);
    }

    if ((!flag) && (world.x()) && ((world.y(i, j, k)) || (world.y(i - 1, j, k)) || (world.y(i + 1, j, k)) || (world.y(i, j, k - 1)) || (world.y(i, j, k + 1)))) {
      fireExtinguished(world, i, j, k);
    } else {
      int l = world.getData(i, j, k);

      if (l < 15) {
        world.setRawData(i, j, k, l + random.nextInt(3) / 2);
      }

      world.c(i, j, k, this.id, d() + random.nextInt(10));
      if ((!flag) && (!g(world, i, j, k))) {
        if ((!world.isBlockSolidOnSide(i, j - 1, k, 1)) || (l > 3))
          fireExtinguished(world, i, j, k);
      }
      else if ((!flag) && (!canBlockCatchFire(world, i, j - 1, k, 1)) && (l == 15) && (random.nextInt(4) == 0)) {
        fireExtinguished(world, i, j, k);
      } else {
        boolean flag1 = world.z(i, j, k);
        byte b0 = 0;

        if (flag1) {
          b0 = -50;
        }

        tryToCatchBlockOnFire(world, i + 1, j, k, 300 + b0, random, l, 4);
        tryToCatchBlockOnFire(world, i - 1, j, k, 300 + b0, random, l, 5);
        tryToCatchBlockOnFire(world, i, j - 1, k, 250 + b0, random, l, 1);
        tryToCatchBlockOnFire(world, i, j + 1, k, 250 + b0, random, l, 0);
        tryToCatchBlockOnFire(world, i, j, k - 1, 300 + b0, random, l, 3);
        tryToCatchBlockOnFire(world, i, j, k + 1, 300 + b0, random, l, 2);

        Server server = world.getServer();
        org.bukkit.World bworld = world.getWorld();

        BlockIgniteEvent.IgniteCause igniteCause = BlockIgniteEvent.IgniteCause.SPREAD;
        org.bukkit.block.Block fromBlock = bworld.getBlockAt(i, j, k);

        for (int i1 = i - 1; i1 <= i + 1; i1++)
          for (int j1 = k - 1; j1 <= k + 1; j1++)
            for (int k1 = j - 1; k1 <= j + 4; k1++)
              if ((i1 != i) || (k1 != j) || (j1 != k)) {
                int l1 = 100;

                if (k1 > j + 1) {
                  l1 += (k1 - (j + 1)) * 100;
                }

                int i2 = h(world, i1, k1, j1);

                if (i2 > 0) {
                  int j2 = (i2 + 40) / (l + 30);

                  if (flag1) {
                    j2 /= 2;
                  }

                  if ((j2 > 0) && (random.nextInt(l1) <= j2) && ((!world.x()) || (!world.y(i1, k1, j1))) && (!world.y(i1 - 1, k1, k)) && (!world.y(i1 + 1, k1, j1)) && (!world.y(i1, k1, j1 - 1)) && (!world.y(i1, k1, j1 + 1))) {
                    int k2 = l + random.nextInt(5) / 4;

                    if (k2 > 15) {
                      k2 = 15;
                    }

                    org.bukkit.block.Block block = bworld.getBlockAt(i1, k1, j1);

                    if (block.getTypeId() != Block.FIRE.id) {
                      BlockIgniteEvent event = new BlockIgniteEvent(block, igniteCause, null);
                      server.getPluginManager().callEvent(event);

                      if (!event.isCancelled())
                      {
                        BlockState blockState = bworld.getBlockAt(i1, k1, j1).getState();
                        blockState.setTypeId(this.id);
                        blockState.setData(new MaterialData(this.id, (byte)k2));

                        BlockSpreadEvent spreadEvent = new BlockSpreadEvent(blockState.getBlock(), fromBlock, blockState);
                        server.getPluginManager().callEvent(spreadEvent);

                        if (!spreadEvent.isCancelled())
                          blockState.update(true);
                      }
                    }
                  }
                }
              }
      }
    }
  }

  @Deprecated
  private void a(World world, int i, int j, int k, int l, Random random, int i1)
  {
    tryToCatchBlockOnFire(world, i, j, k, l, random, i1, 0);
  }

  private void tryToCatchBlockOnFire(World world, int i, int j, int k, int l, Random random, int i1, int face)
  {
    int j1 = 0;
    Block block = Block.byId[world.getTypeId(i, j, k)];
    if (block != null)
    {
      j1 = block.getFlammability(world, i, j, k, world.getData(i, j, k), face);
    }

    if (random.nextInt(l) < j1) {
      boolean flag = world.getTypeId(i, j, k) == Block.TNT.id;

      org.bukkit.block.Block theBlock = world.getWorld().getBlockAt(i, j, k);

      BlockBurnEvent event = new BlockBurnEvent(theBlock);
      world.getServer().getPluginManager().callEvent(event);

      if (event.isCancelled()) {
        return;
      }

      if ((random.nextInt(i1 + 10) < 5) && (!world.y(i, j, k))) {
        int k1 = i1 + random.nextInt(5) / 4;

        if (k1 > 15) {
          k1 = 15;
        }

        world.setTypeIdAndData(i, j, k, this.id, k1);
      } else {
        world.setTypeId(i, j, k, 0);
      }

      if (flag)
        Block.TNT.postBreak(world, i, j, k, 1);
    }
  }

  private boolean g(World par1World, int par2, int par3, int par4)
  {
    return (canBlockCatchFire(par1World, par2 + 1, par3, par4, 4)) || (canBlockCatchFire(par1World, par2 - 1, par3, par4, 5)) || (canBlockCatchFire(par1World, par2, par3 - 1, par4, 1)) || (canBlockCatchFire(par1World, par2, par3 + 1, par4, 0)) || (canBlockCatchFire(par1World, par2, par3, par4 - 1, 3)) || (canBlockCatchFire(par1World, par2, par3, par4 + 1, 2));
  }

  private int h(World world, int i, int j, int k)
  {
    byte b0 = 0;

    if (!world.isEmpty(i, j, k)) {
      return 0;
    }

    int var6 = getChanceToEncourageFire(world, i + 1, j, k, b0, 4);
    var6 = getChanceToEncourageFire(world, i - 1, j, k, var6, 5);
    var6 = getChanceToEncourageFire(world, i, j - 1, k, var6, 1);
    var6 = getChanceToEncourageFire(world, i, j + 1, k, var6, 0);
    var6 = getChanceToEncourageFire(world, i, j, k - 1, var6, 3);
    var6 = getChanceToEncourageFire(world, i, j, k + 1, var6, 2);

    return var6;
  }

  public boolean E_()
  {
    return false;
  }

  @Deprecated
  public boolean c(IBlockAccess iblockaccess, int i, int j, int k)
  {
    return canBlockCatchFire(iblockaccess, i, j, k, 0);
  }

  @Deprecated
  public int f(World world, int i, int j, int k, int l)
  {
    return getChanceToEncourageFire(world, i, j, k, l, 0);
  }

  public boolean canPlace(World world, int i, int j, int k) {
    return (world.isBlockSolidOnSide(i, j - 1, k, 1)) || (g(world, i, j, k));
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    if ((!world.isBlockSolidOnSide(i, j - 1, k, 1)) && (!g(world, i, j, k)))
      fireExtinguished(world, i, j, k);
  }

  public void onPlace(World world, int i, int j, int k)
  {
    if ((world.worldProvider.dimension == 1) || (world.getTypeId(i, j - 1, k) != Block.OBSIDIAN.id) || (!Block.PORTAL.b_(world, i, j, k)))
      if ((!world.isBlockSolidOnSide(i, j - 1, k, 1)) && (!g(world, i, j, k)))
        fireExtinguished(world, i, j, k);
      else
        world.c(i, j, k, this.id, d() + world.random.nextInt(10));
  }

  private void fireExtinguished(World world, int x, int y, int z)
  {
    if (!CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(x, y, z), 0).isCancelled())
      world.setTypeId(x, y, z, 0);
  }

  public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z, int face)
  {
    Block block = Block.byId[world.getTypeId(x, y, z)];
    if (block != null)
    {
      return block.isFlammable(world, x, y, z, world.getData(x, y, z), face);
    }
    return false;
  }

  public int getChanceToEncourageFire(World world, int x, int y, int z, int oldChance, int face)
  {
    int newChance = 0;
    Block block = Block.byId[world.getTypeId(x, y, z)];
    if (block != null)
    {
      newChance = block.getFireSpreadSpeed(world, x, y, z, world.getData(x, y, z), face);
    }
    return newChance > oldChance ? newChance : oldChance;
  }
}

