package net.minecraftforge.event.entity;

import net.minecraft.server.Entity;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlaySoundAtEntityEvent extends EntityEvent
{
    public String name;
    public final float volume;
    public final float pitch;

    public PlaySoundAtEntityEvent(Entity var1, String var2, float var3, float var4)
    {
        super(var1);
        this.name = var2;
        this.volume = var3;
        this.pitch = var4;
    }
}
