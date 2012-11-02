package net.minecraftforge.event.entity.player;

import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerEvent$BreakSpeed extends PlayerEvent
{
    public final Block block;
    public final int metadata;
    public final float originalSpeed;
    public float newSpeed = 0.0F;

    public PlayerEvent$BreakSpeed(EntityHuman var1, Block var2, int var3, float var4)
    {
        super(var1);
        this.block = var2;
        this.metadata = var3;
        this.originalSpeed = var4;
        this.newSpeed = var4;
    }
}
