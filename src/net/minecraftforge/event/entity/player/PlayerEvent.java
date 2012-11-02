package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerEvent extends LivingEvent
{
    public final EntityHuman entityPlayer;

    public PlayerEvent(EntityHuman var1)
    {
        super(var1);
        this.entityPlayer = var1;
    }
}
