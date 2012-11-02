package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.IPickupNotifier;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;

public class ModLoaderPickupNotifier implements IPickupNotifier
{
    private BaseModProxy mod;

    public ModLoaderPickupNotifier(BaseModProxy var1)
    {
        this.mod = var1;
    }

    public void notifyPickup(EntityItem var1, EntityHuman var2)
    {
        this.mod.onItemPickup(var2, var1.itemStack);
    }
}
