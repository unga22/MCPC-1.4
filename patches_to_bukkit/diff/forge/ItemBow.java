package net.minecraft.server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemBow extends Item
{
    public ItemBow(int var1)
    {
        super(var1);
        this.maxStackSize = 1;
        this.setMaxDurability(384);
        this.a(CreativeModeTab.j);
    }

    /**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */
    public void a(ItemStack var1, World var2, EntityHuman var3, int var4)
    {
        int var5 = this.a(var1) - var4;
        ArrowLooseEvent var6 = new ArrowLooseEvent(var3, var1, var5);
        MinecraftForge.EVENT_BUS.post(var6);

        if (!var6.isCanceled())
        {
            var5 = var6.charge;
            boolean var7 = var3.abilities.canInstantlyBuild || EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_INFINITE.id, var1) > 0;

            if (var7 || var3.inventory.e(Item.ARROW.id))
            {
                float var8 = (float)var5 / 20.0F;
                var8 = (var8 * var8 + var8 * 2.0F) / 3.0F;

                if ((double)var8 < 0.1D)
                {
                    return;
                }

                if (var8 > 1.0F)
                {
                    var8 = 1.0F;
                }

                EntityArrow var9 = new EntityArrow(var2, var3, var8 * 2.0F);

                if (var8 == 1.0F)
                {
                    var9.e(true);
                }

                int var10 = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_DAMAGE.id, var1);

                if (var10 > 0)
                {
                    var9.b(var9.c() + (double)var10 * 0.5D + 0.5D);
                }

                int var11 = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK.id, var1);

                if (var11 > 0)
                {
                    var9.a(var11);
                }

                if (EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_FIRE.id, var1) > 0)
                {
                    var9.setOnFire(100);
                }

                var1.damage(1, var3);
                var2.makeSound(var3, "random.bow", 1.0F, 1.0F / (d.nextFloat() * 0.4F + 1.2F) + var8 * 0.5F);

                if (var7)
                {
                    var9.fromPlayer = 2;
                }
                else
                {
                    var3.inventory.d(Item.ARROW.id);
                }

                if (!var2.isStatic)
                {
                    var2.addEntity(var9);
                }
            }
        }
    }

    public ItemStack b(ItemStack var1, World var2, EntityHuman var3)
    {
        return var1;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int a(ItemStack var1)
    {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAnimation d_(ItemStack var1)
    {
        return EnumAnimation.bow;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack a(ItemStack var1, World var2, EntityHuman var3)
    {
        ArrowNockEvent var4 = new ArrowNockEvent(var3, var1);
        MinecraftForge.EVENT_BUS.post(var4);

        if (var4.isCanceled())
        {
            return var4.result;
        }
        else
        {
            if (var3.abilities.canInstantlyBuild || var3.inventory.e(Item.ARROW.id))
            {
                var3.a(var1, this.a(var1));
            }

            return var1;
        }
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int c()
    {
        return 1;
    }
}
