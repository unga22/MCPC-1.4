package net.minecraftforge.event.entity.minecart;

import net.minecraft.server.EntityMinecart;

public class MinecartUpdateEvent extends MinecartEvent
{
    public final float x;
    public final float y;
    public final float z;

    public MinecartUpdateEvent(EntityMinecart var1, float var2, float var3, float var4)
    {
        super(var1);
        this.x = var2;
        this.y = var3;
        this.z = var4;
    }
}
