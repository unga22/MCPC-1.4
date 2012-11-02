package net.minecraft.server;

import forge.IShearable;
import java.util.ArrayList;
import java.util.Random;

public class ItemShears extends Item
{
  public ItemShears(int i)
  {
    super(i);
    e(1);
    setMaxDurability(238);
  }

  public boolean a(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving) {
    if ((i != Block.LEAVES.id) && (i != Block.WEB.id) && (i != Block.LONG_GRASS.id) && (i != Block.VINE.id) && (!(Block.byId[i] instanceof IShearable))) {
      return super.a(itemstack, i, j, k, l, entityliving);
    }
    return true;
  }

  public boolean canDestroySpecialBlock(Block block)
  {
    return block.id == Block.WEB.id;
  }

  public float getDestroySpeed(ItemStack itemstack, Block block) {
    return (block.id != Block.WEB.id) && (block.id != Block.LEAVES.id) ? super.getDestroySpeed(itemstack, block) : block.id == Block.WOOL.id ? 5.0F : 15.0F;
  }

  public void a(ItemStack itemstack, EntityLiving entity)
  {
    if (entity.world.isStatic)
    {
      return;
    }
    if ((entity instanceof IShearable))
    {
      IShearable target = (IShearable)entity;
      if (target.isShearable(itemstack, entity.world, (int)entity.locX, (int)entity.locY, (int)entity.locZ))
      {
        ArrayList drops = target.onSheared(itemstack, entity.world, (int)entity.locX, (int)entity.locY, (int)entity.locZ, EnchantmentManager.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS.id, itemstack));

        for (ItemStack stack : drops)
        {
          EntityItem ent = entity.a(stack, 1.0F);
          ent.motY += entity.random.nextFloat() * 0.05F;
          ent.motX += (entity.random.nextFloat() - entity.random.nextFloat()) * 0.1F;
          ent.motZ += (entity.random.nextFloat() - entity.random.nextFloat()) * 0.1F;
        }
        itemstack.damage(1, entity);
      }
    }
  }

  public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityHuman player)
  {
    if (player.world.isStatic)
    {
      return false;
    }
    int id = player.world.getTypeId(X, Y, Z);
    if ((Block.byId[id] != null) && ((Block.byId[id] instanceof IShearable)))
    {
      IShearable target = (IShearable)Block.byId[id];
      if (target.isShearable(itemstack, player.world, X, Y, Z))
      {
        ArrayList drops = target.onSheared(itemstack, player.world, X, Y, Z, EnchantmentManager.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS.id, itemstack));

        for (ItemStack stack : drops)
        {
          float f = 0.7F;
          double d = player.random.nextFloat() * f + 1.0F - f * 0.5D;
          double d1 = player.random.nextFloat() * f + 1.0F - f * 0.5D;
          double d2 = player.random.nextFloat() * f + 1.0F - f * 0.5D;
          EntityItem entityitem = new EntityItem(player.world, X + d, Y + d1, Z + d2, stack);
          entityitem.pickupDelay = 10;
          player.world.addEntity(entityitem);
        }
        itemstack.damage(1, player);
        player.a(StatisticList.C[id], 1);
      }
    }
    return false;
  }
}

