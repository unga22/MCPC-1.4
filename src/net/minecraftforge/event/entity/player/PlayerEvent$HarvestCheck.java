package net.minecraftforge.event.entity.player;

import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;

public class PlayerEvent$HarvestCheck extends PlayerEvent
{
    public final Block block;
    public boolean success;

    public PlayerEvent$HarvestCheck(EntityHuman var1, Block var2, boolean var3)
    {
        super(var1);
        this.block = var2;
        this.success = var3;
    }
}
