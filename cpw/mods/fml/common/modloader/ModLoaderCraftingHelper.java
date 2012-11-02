package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public class ModLoaderCraftingHelper implements ICraftingHandler
{
    private BaseModProxy mod;

    public ModLoaderCraftingHelper(BaseModProxy var1)
    {
        this.mod = var1;
    }

    public void onCrafting(EntityHuman var1, ItemStack var2, IInventory var3)
    {
        this.mod.takenFromCrafting(var1, var2, var3);
    }

    public void onSmelting(EntityHuman var1, ItemStack var2)
    {
        this.mod.takenFromFurnace(var1, var2);
    }
}
