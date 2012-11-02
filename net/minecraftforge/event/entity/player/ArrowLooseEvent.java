package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowLooseEvent extends PlayerEvent
{
    public final ItemStack bow;
    public int charge;

    public ArrowLooseEvent(EntityHuman var1, ItemStack var2, int var3)
    {
        super(var1);
        this.bow = var2;
        this.charge = var3;
    }
}
