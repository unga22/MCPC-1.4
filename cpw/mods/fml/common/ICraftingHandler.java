package cpw.mods.fml.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public interface ICraftingHandler
{
    void onCrafting(EntityHuman var1, ItemStack var2, IInventory var3);

    void onSmelting(EntityHuman var1, ItemStack var2);
}
