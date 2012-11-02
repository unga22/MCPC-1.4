package net.minecraftforge.event.entity;

import net.minecraft.server.Entity;
import net.minecraftforge.event.Event;

public class EntityEvent extends Event
{
    public final Entity entity;

    public EntityEvent(Entity var1)
    {
        this.entity = var1;
    }
}
