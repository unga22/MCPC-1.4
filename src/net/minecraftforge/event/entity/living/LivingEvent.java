package net.minecraftforge.event.entity.living;

import net.minecraft.server.EntityLiving;
import net.minecraftforge.event.entity.EntityEvent;

public class LivingEvent extends EntityEvent
{
    public final EntityLiving entityLiving;

    public LivingEvent(EntityLiving var1)
    {
        super(var1);
        this.entityLiving = var1;
    }
}
