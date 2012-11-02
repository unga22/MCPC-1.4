package net.minecraft.server;

import forge.MinecraftForge;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class ItemBucket extends Item
{
  private int a;

  public ItemBucket(int i, int j)
  {
    super(i);
    this.maxStackSize = 1;
    this.a = j;
  }

  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    float f = 1.0F;
    double d0 = entityhuman.lastX + (entityhuman.locX - entityhuman.lastX) * f;
    double d1 = entityhuman.lastY + (entityhuman.locY - entityhuman.lastY) * f + 1.62D - entityhuman.height;
    double d2 = entityhuman.lastZ + (entityhuman.locZ - entityhuman.lastZ) * f;
    boolean flag = this.a == 0;
    MovingObjectPosition movingobjectposition = a(world, entityhuman, flag);

    if (movingobjectposition == null) {
      return itemstack;
    }
    if (movingobjectposition.type == EnumMovingObjectType.TILE) {
      int i = movingobjectposition.b;
      int j = movingobjectposition.c;
      int k = movingobjectposition.d;

      if (!world.a(entityhuman, i, j, k)) {
        return itemstack;
      }

      if (this.a == 0) {
        if ((entityhuman != null) && (!entityhuman.d(i, j, k))) {
          return itemstack;
        }
        ItemStack stack = MinecraftForge.fillCustomBucket(world, i, j, k);
        if (stack != null)
        {
          return stack;
        }

        if ((world.getMaterial(i, j, k) == Material.WATER) && (world.getData(i, j, k) == 0))
        {
          PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman, i, j, k, -1, itemstack, Item.WATER_BUCKET);

          if (event.isCancelled()) {
            return itemstack;
          }
          world.setTypeId(i, j, k, 0);
          if (entityhuman.abilities.canInstantlyBuild) {
            return itemstack;
          }

          return CraftItemStack.createNMSItemStack(event.getItemStack());
        }

        if ((world.getMaterial(i, j, k) == Material.LAVA) && (world.getData(i, j, k) == 0))
        {
          PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman, i, j, k, -1, itemstack, Item.LAVA_BUCKET);

          if (event.isCancelled()) {
            return itemstack;
          }
          world.setTypeId(i, j, k, 0);
          if (entityhuman.abilities.canInstantlyBuild) {
            return itemstack;
          }

          return CraftItemStack.createNMSItemStack(event.getItemStack());
        }
      } else {
        if (this.a < 0)
        {
          PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent(entityhuman, i, j, k, movingobjectposition.face, itemstack);

          if (event.isCancelled()) {
            return itemstack;
          }

          return CraftItemStack.createNMSItemStack(event.getItemStack());
        }

        int clickedX = i; int clickedY = j; int clickedZ = k;

        if (movingobjectposition.face == 0) {
          j--;
        }

        if (movingobjectposition.face == 1) {
          j++;
        }

        if (movingobjectposition.face == 2) {
          k--;
        }

        if (movingobjectposition.face == 3) {
          k++;
        }

        if (movingobjectposition.face == 4) {
          i--;
        }

        if (movingobjectposition.face == 5) {
          i++;
        }

        if (!entityhuman.d(i, j, k)) {
          return itemstack;
        }

        if ((world.isEmpty(i, j, k)) || (!world.getMaterial(i, j, k).isBuildable()))
        {
          PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent(entityhuman, clickedX, clickedY, clickedZ, movingobjectposition.face, itemstack);

          if (event.isCancelled()) {
            return itemstack;
          }

          if ((world.worldProvider.d) && (this.a == Block.WATER.id)) {
            world.makeSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

            for (int l = 0; l < 8; l++)
              world.a("largesmoke", i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
          }
          else {
            world.setTypeIdAndData(i, j, k, this.a, 0);
          }

          if (entityhuman.abilities.canInstantlyBuild) {
            return itemstack;
          }

          return CraftItemStack.createNMSItemStack(event.getItemStack());
        }
      }
    }
    else if ((this.a == 0) && ((movingobjectposition.entity instanceof EntityCow)))
    {
      Location loc = movingobjectposition.entity.getBukkitEntity().getLocation();
      PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), -1, itemstack, Item.MILK_BUCKET);

      if (event.isCancelled()) {
        return itemstack;
      }

      return CraftItemStack.createNMSItemStack(event.getItemStack());
    }

    return itemstack;
  }
}

