package net.minecraftforge.event.entity.player;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityInteractEvent extends PlayerEvent
{
    public final Entity target;

    public EntityInteractEvent(EntityHuman var1, Entity var2)
    {
        super(var1);
        this.target = var2;
    }
}
