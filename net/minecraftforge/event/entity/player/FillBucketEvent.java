package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event$HasResult;

@Cancelable
@Event$HasResult
public class FillBucketEvent extends PlayerEvent
{
    public final ItemStack current;
    public final World world;
    public final MovingObjectPosition target;
    public ItemStack result;

    public FillBucketEvent(EntityHuman var1, ItemStack var2, World var3, MovingObjectPosition var4)
    {
        super(var1);
        this.current = var2;
        this.world = var3;
        this.target = var4;
    }
}
