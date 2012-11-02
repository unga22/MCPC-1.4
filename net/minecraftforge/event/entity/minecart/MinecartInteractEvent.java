package net.minecraftforge.event.entity.minecart;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMinecart;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class MinecartInteractEvent extends MinecartEvent
{
    public final EntityHuman player;

    public MinecartInteractEvent(EntityMinecart var1, EntityHuman var2)
    {
        super(var1);
        this.player = var2;
    }
}
