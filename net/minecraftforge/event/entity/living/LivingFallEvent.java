package net.minecraftforge.event.entity.living;

import net.minecraft.server.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingFallEvent extends LivingEvent
{
    public float distance;

    public LivingFallEvent(EntityLiving var1, float var2)
    {
        super(var1);
        this.distance = var2;
    }
}
