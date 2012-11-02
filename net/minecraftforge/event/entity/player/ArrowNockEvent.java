package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowNockEvent extends PlayerEvent
{
    public ItemStack result;

    public ArrowNockEvent(EntityHuman var1, ItemStack var2)
    {
        super(var1);
        this.result = var2;
    }
}
