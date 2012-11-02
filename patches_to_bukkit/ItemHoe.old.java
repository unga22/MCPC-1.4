package net.minecraft.server;

import forge.ForgeHooks;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemHoe extends Item
{
  public ItemHoe(int i, EnumToolMaterial enumtoolmaterial)
  {
    super(i);
    this.maxStackSize = 1;
    setMaxDurability(enumtoolmaterial.a());
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (!entityhuman.d(i, j, k)) {
      return false;
    }
    if (ForgeHooks.onUseHoe(itemstack, entityhuman, world, i, j, k))
    {
      itemstack.damage(1, entityhuman);
      return true;
    }
    int i1 = world.getTypeId(i, j, k);
    int j1 = world.getTypeId(i, j + 1, k);

    if (((l == 0) || (j1 != 0) || (i1 != Block.GRASS.id)) && (i1 != Block.DIRT.id)) {
      return false;
    }
    Block block = Block.SOIL;

    world.makeSound(i + 0.5F, j + 0.5F, k + 0.5F, block.stepSound.getName(), (block.stepSound.getVolume1() + 1.0F) / 2.0F, block.stepSound.getVolume2() * 0.8F);
    if (world.isStatic) {
      return true;
    }
    CraftBlockState blockState = CraftBlockState.getBlockState(world, i, j, k);

    world.setTypeId(i, j, k, block.id);

    BlockPlaceEvent event = CraftEventFactory.callBlockPlaceEvent(world, entityhuman, blockState, i, j, k);

    if ((event.isCancelled()) || (!event.canBuild())) {
      event.getBlockPlaced().setTypeId(blockState.getTypeId());
      return false;
    }

    itemstack.damage(1, entityhuman);
    return true;
  }
}

