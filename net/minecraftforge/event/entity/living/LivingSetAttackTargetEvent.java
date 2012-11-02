package net.minecraftforge.event.entity.living;

import net.minecraft.server.EntityLiving;

public class LivingSetAttackTargetEvent extends LivingEvent
{
    public final EntityLiving target;

    public LivingSetAttackTargetEvent(EntityLiving var1, EntityLiving var2)
    {
        super(var1);
        this.target = var2;
    }
}
