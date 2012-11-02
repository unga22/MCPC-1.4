package net.minecraftforge.event.entity.living;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingDeathEvent extends LivingEvent
{
    public final DamageSource source;

    public LivingDeathEvent(EntityLiving var1, DamageSource var2)
    {
        super(var1);
        this.source = var2;
    }
}
