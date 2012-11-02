package net.minecraftforge.event.entity;

import net.minecraft.server.Entity;
import net.minecraft.server.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityJoinWorldEvent extends EntityEvent
{
    public final World world;

    public EntityJoinWorldEvent(Entity var1, World var2)
    {
        super(var1);
        this.world = var2;
    }
}
