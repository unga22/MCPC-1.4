package net.minecraftforge.event.entity.player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event$HasResult;

@Cancelable
@Event$HasResult
public class UseHoeEvent extends PlayerEvent
{
    public final ItemStack current;
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    private boolean handeled = false;

    public UseHoeEvent(EntityHuman var1, ItemStack var2, World var3, int var4, int var5, int var6)
    {
        super(var1);
        this.current = var2;
        this.world = var3;
        this.x = var4;
        this.y = var5;
        this.z = var6;
    }
}
