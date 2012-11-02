package net.minecraftforge.event.entity.item;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ItemTossEvent extends ItemEvent
{
    public final EntityHuman player;

    public ItemTossEvent(EntityItem var1, EntityHuman var2)
    {
        super(var1);
        this.player = var2;
    }
}
