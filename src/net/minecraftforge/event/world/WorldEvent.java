package net.minecraftforge.event.world;

import net.minecraft.server.World;
import net.minecraftforge.event.Event;

public class WorldEvent extends Event
{
    public final World world;

    public WorldEvent(World var1)
    {
        this.world = var1;
    }
}
