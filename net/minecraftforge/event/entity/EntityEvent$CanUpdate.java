package net.minecraftforge.event.entity;

import net.minecraft.server.Entity;

public class EntityEvent$CanUpdate extends EntityEvent
{
    public boolean canUpdate = false;

    public EntityEvent$CanUpdate(Entity var1)
    {
        super(var1);
    }
}
