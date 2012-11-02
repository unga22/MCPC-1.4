package net.minecraftforge.event.entity.living;

import net.minecraft.server.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingEvent$LivingUpdateEvent extends LivingEvent
{
    public LivingEvent$LivingUpdateEvent(EntityLiving var1)
    {
        super(var1);
    }
}
