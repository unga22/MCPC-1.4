package net.minecraft.server;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemSeeds extends Item
{
  private int id;
  private int b;

  public ItemSeeds(int i, int j, int k)
  {
    super(i);
    this.id = j;
    this.b = k;
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (l != 1)
      return false;
    if ((entityhuman.d(i, j, k)) && (entityhuman.d(i, j + 1, k))) {
      int i1 = world.getTypeId(i, j, k);

      if ((i1 == this.b) && (world.isEmpty(i, j + 1, k))) {
        CraftBlockState blockState = CraftBlockState.getBlockState(world, i, j + 1, k);

        world.setTypeId(i, j + 1, k, this.id);

        BlockPlaceEvent event = CraftEventFactory.callBlockPlaceEvent(world, entityhuman, blockState, i, j, k);

        if ((event.isCancelled()) || (!event.canBuild())) {
          event.getBlockPlaced().setTypeId(0);
          return false;
        }

        itemstack.count -= 1;
        return true;
      }
      return false;
    }

    return false;
  }
}

