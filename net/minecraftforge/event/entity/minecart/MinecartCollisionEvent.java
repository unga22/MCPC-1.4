package net.minecraftforge.event.entity.minecart;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityMinecart;

public class MinecartCollisionEvent extends MinecartEvent
{
    public final Entity collider;

    public MinecartCollisionEvent(EntityMinecart var1, Entity var2)
    {
        super(var1);
        this.collider = var2;
    }
}
