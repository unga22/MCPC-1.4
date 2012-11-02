package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

public class PlayerDestroyItemEvent extends PlayerEvent
{
    public final ItemStack original;

    public PlayerDestroyItemEvent(EntityHuman var1, ItemStack var2)
    {
        super(var1);
        this.original = var2;
    }
}
