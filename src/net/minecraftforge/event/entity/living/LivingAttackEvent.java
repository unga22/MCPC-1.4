package net.minecraftforge.event.entity.living;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingAttackEvent extends LivingEvent
{
    public final DamageSource source;
    public final int ammount;

    public LivingAttackEvent(EntityLiving var1, DamageSource var2, int var3)
    {
        super(var1);
        this.source = var2;
        this.ammount = var3;
    }
}
