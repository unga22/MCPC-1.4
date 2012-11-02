package net.minecraftforge.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraftforge.common.MinecartRegistry$MinecartKey;

public class MinecartRegistry
{
    private static Map itemForMinecart = new HashMap();
    private static Map minecartForItem = new HashMap();

    public static void registerMinecart(Class var0, ItemStack var1)
    {
        registerMinecart(var0, 0, var1);
    }

    public static void registerMinecart(Class var0, int var1, ItemStack var2)
    {
        MinecartRegistry$MinecartKey var3 = new MinecartRegistry$MinecartKey(var0, var1);
        itemForMinecart.put(var3, var2);
        minecartForItem.put(var2, var3);
    }

    public static void removeMinecart(Class var0, int var1)
    {
        MinecartRegistry$MinecartKey var2 = new MinecartRegistry$MinecartKey(var0, var1);
        ItemStack var3 = (ItemStack)itemForMinecart.remove(var2);

        if (var3 != null)
        {
            minecartForItem.remove(var3);
        }
    }

    public static ItemStack getItemForCart(Class var0)
    {
        return getItemForCart(var0, 0);
    }

    public static ItemStack getItemForCart(Class var0, int var1)
    {
        ItemStack var2 = (ItemStack)itemForMinecart.get(new MinecartRegistry$MinecartKey(var0, var1));
        return var2 == null ? null : var2.cloneItemStack();
    }

    public static ItemStack getItemForCart(EntityMinecart var0)
    {
        return getItemForCart(var0.getClass(), var0.getMinecartType());
    }

    public static Class getCartClassForItem(ItemStack var0)
    {
        MinecartRegistry$MinecartKey var1 = null;
        Iterator var2 = minecartForItem.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();

            if (((ItemStack)var3.getKey()).doMaterialsMatch(var0))
            {
                var1 = (MinecartRegistry$MinecartKey)var3.getValue();
                break;
            }
        }

        return var1 != null ? var1.minecart : null;
    }

    public static int getCartTypeForItem(ItemStack var0)
    {
        MinecartRegistry$MinecartKey var1 = null;
        Iterator var2 = minecartForItem.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();

            if (((ItemStack)var3.getKey()).doMaterialsMatch(var0))
            {
                var1 = (MinecartRegistry$MinecartKey)var3.getValue();
                break;
            }
        }

        return var1 != null ? var1.type : -1;
    }

    public static Set getAllCartItems()
    {
        HashSet var0 = new HashSet();
        Iterator var1 = minecartForItem.keySet().iterator();

        while (var1.hasNext())
        {
            ItemStack var2 = (ItemStack)var1.next();
            var0.add(var2.cloneItemStack());
        }

        return var0;
    }

    static
    {
        registerMinecart(EntityMinecart.class, 0, new ItemStack(Item.MINECART));
        registerMinecart(EntityMinecart.class, 1, new ItemStack(Item.STORAGE_MINECART));
        registerMinecart(EntityMinecart.class, 2, new ItemStack(Item.POWERED_MINECART));
    }
}
