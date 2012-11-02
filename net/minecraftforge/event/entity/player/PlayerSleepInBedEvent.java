package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumBedResult;

public class PlayerSleepInBedEvent extends PlayerEvent
{
    public EnumBedResult result = null;
    public final int x;
    public final int y;
    public final int z;

    public PlayerSleepInBedEvent(EntityHuman var1, int var2, int var3, int var4)
    {
        super(var1);
        this.x = var2;
        this.y = var3;
        this.z = var4;
    }
}
