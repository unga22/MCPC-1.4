package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event$HasResult;

@Cancelable
@Event$HasResult
public class EntityItemPickupEvent extends PlayerEvent
{
    public final EntityItem item;
    private boolean handled = false;

    public EntityItemPickupEvent(EntityHuman var1, EntityItem var2)
    {
        super(var1);
        this.item = var2;
    }
}
