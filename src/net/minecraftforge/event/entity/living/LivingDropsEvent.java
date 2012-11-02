package net.minecraftforge.event.entity.living;

import java.util.ArrayList;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingDropsEvent extends LivingEvent
{
    public final DamageSource source;
    public final ArrayList drops;
    public final int lootingLevel;
    public final boolean recentlyHit;
    public final int specialDropValue;

    public LivingDropsEvent(EntityLiving var1, DamageSource var2, ArrayList var3, int var4, boolean var5, int var6)
    {
        super(var1);
        this.source = var2;
        this.drops = var3;
        this.lootingLevel = var4;
        this.recentlyHit = var5;
        this.specialDropValue = var6;
    }
}
