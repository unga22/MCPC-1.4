package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event$Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent$Action;

@Cancelable
public class PlayerInteractEvent extends PlayerEvent
{
    public final PlayerInteractEvent$Action action;
    public final int x;
    public final int y;
    public final int z;
    public final int face;
    public Event$Result useBlock;
    public Event$Result useItem;

    public PlayerInteractEvent(EntityHuman var1, PlayerInteractEvent$Action var2, int var3, int var4, int var5, int var6)
    {
        super(var1);
        this.useBlock = Event$Result.DEFAULT;
        this.useItem = Event$Result.DEFAULT;
        this.action = var2;
        this.x = var3;
        this.y = var4;
        this.z = var5;
        this.face = var6;

        if (var6 == -1)
        {
            this.useBlock = Event$Result.DENY;
        }
    }

    public void setCanceled(boolean var1)
    {
        this.useBlock = var1 ? Event$Result.DENY : (this.useBlock == Event$Result.DENY ? Event$Result.DEFAULT : this.useBlock);
        this.useItem = var1 ? Event$Result.DENY : (this.useItem == Event$Result.DENY ? Event$Result.DEFAULT : this.useItem);
    }
}
