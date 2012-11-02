package net.minecraft.server;

import forge.ForgeHooks;
import java.util.Random;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.plugin.PluginManager;

public class ItemDye extends Item
{
  public static final String[] a = { "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white" };
  public static final int[] b = { 1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 2651799, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320 };

  public ItemDye(int i) {
    super(i);
    a(true);
    setMaxDurability(0);
  }

  public String a(ItemStack itemstack) {
    int i = MathHelper.a(itemstack.getData(), 0, 15);

    return super.getName() + "." + a[i];
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if ((entityhuman != null) && (!entityhuman.d(i, j, k))) {
      return false;
    }
    if (itemstack.getData() == 15) {
      int i1 = world.getTypeId(i, j, k);
      if (ForgeHooks.onUseBonemeal(world, i1, i, j, k))
      {
        if (!world.isStatic)
        {
          itemstack.count -= 1;
        }
        return true;
      }

      if (i1 == Block.SAPLING.id) {
        if (!world.isStatic)
        {
          Player player = (entityhuman instanceof EntityPlayer) ? (Player)entityhuman.getBukkitEntity() : null;
          ((BlockSapling)Block.SAPLING).grow(world, i, j, k, world.random, true, player, itemstack);
        }

        return true;
      }

      if ((i1 == Block.BROWN_MUSHROOM.id) || (i1 == Block.RED_MUSHROOM.id))
      {
        if (!world.isStatic) {
          Player player = (entityhuman instanceof EntityPlayer) ? (Player)entityhuman.getBukkitEntity() : null;
          ((BlockMushroom)Block.byId[i1]).grow(world, i, j, k, world.random, true, player, itemstack);
        }

        return true;
      }

      if ((i1 == Block.MELON_STEM.id) || (i1 == Block.PUMPKIN_STEM.id)) {
        if (!world.isStatic) {
          ((BlockStem)Block.byId[i1]).g(world, i, j, k);
          itemstack.count -= 1;
        }

        return true;
      }

      if (i1 == Block.CROPS.id) {
        if (!world.isStatic) {
          ((BlockCrops)Block.CROPS).g(world, i, j, k);
          itemstack.count -= 1;
        }

        return true;
      }

      if (i1 == Block.GRASS.id) {
        if (!world.isStatic) {
          itemstack.count -= 1;

          label564: for (int j1 = 0; j1 < 128; j1++) {
            int k1 = i;
            int l1 = j + 1;
            int i2 = k;

            for (int j2 = 0; j2 < j1 / 16; j2++) {
              k1 += c.nextInt(3) - 1;
              l1 += (c.nextInt(3) - 1) * c.nextInt(3) / 2;
              i2 += c.nextInt(3) - 1;
              if ((world.getTypeId(k1, l1 - 1, i2) != Block.GRASS.id) || (world.e(k1, l1, i2)))
              {
                break label564;
              }
            }
            if (world.getTypeId(k1, l1, i2) == 0) {
              if (c.nextInt(10) != 0) {
                if ((!mod_MinecraftForge.DISABLE_DARK_ROOMS) || (Block.LONG_GRASS.f(world, k1, l1, i2)))
                {
                  world.setTypeIdAndData(k1, l1, i2, Block.LONG_GRASS.id, 1);
                }
              } else ForgeHooks.plantGrassPlant(world, k1, l1, i2);
            }
          }

        }

        return true;
      }
    }

    return false;
  }

  public void a(ItemStack itemstack, EntityLiving entityliving)
  {
    if ((entityliving instanceof EntitySheep)) {
      EntitySheep entitysheep = (EntitySheep)entityliving;
      int i = BlockCloth.d(itemstack.getData());

      if ((!entitysheep.isSheared()) && (entitysheep.getColor() != i))
      {
        byte bColor = new Integer(i).byteValue();
        SheepDyeWoolEvent event = new SheepDyeWoolEvent((Sheep)entitysheep.getBukkitEntity(), DyeColor.getByData(bColor));
        entitysheep.world.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
          return;
        }

        i = event.getColor().getData();

        entitysheep.setColor(i);
        itemstack.count -= 1;
      }
    }
  }
}

