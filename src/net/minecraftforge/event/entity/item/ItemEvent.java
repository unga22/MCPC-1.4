package net.minecraftforge.event.entity.item;

import net.minecraft.server.EntityItem;
import net.minecraftforge.event.entity.EntityEvent;

public class ItemEvent extends EntityEvent
{
    public final EntityItem entityItem;

    public ItemEvent(EntityItem var1)
    {
        super(var1);
        this.entityItem = var1;
    }
}
