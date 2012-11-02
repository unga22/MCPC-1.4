package cpw.mods.fml.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;

public interface IPickupNotifier
{
    void notifyPickup(EntityItem var1, EntityHuman var2);
}
