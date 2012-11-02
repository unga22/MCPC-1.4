package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event$HasResult;

@Cancelable
@Event$HasResult
public class BonemealEvent extends PlayerEvent
{
    public final World world;
    public final int ID;
    public final int X;
    public final int Y;
    public final int Z;

    public BonemealEvent(EntityHuman var1, World var2, int var3, int var4, int var5, int var6)
    {
        super(var1);
        this.world = var2;
        this.ID = var3;
        this.X = var4;
        this.Y = var5;
        this.Z = var6;
    }
}
