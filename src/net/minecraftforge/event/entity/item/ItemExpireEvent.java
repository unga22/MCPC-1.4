package net.minecraftforge.event.entity.item;

import net.minecraft.server.EntityItem;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ItemExpireEvent extends ItemEvent
{
    public int extraLife;

    public ItemExpireEvent(EntityItem var1, int var2)
    {
        super(var1);
        this.extraLife = var2;
    }
}
