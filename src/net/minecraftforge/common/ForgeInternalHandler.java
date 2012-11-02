package net.minecraftforge.common;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityItem;
import net.minecraft.server.ItemStack;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent$Load;
import net.minecraftforge.event.world.WorldEvent$Save;

public class ForgeInternalHandler
{
    @ForgeSubscribe(
            priority = EventPriority.HIGHEST
    )
    public void onEntityJoinWorld(EntityJoinWorldEvent var1)
    {
        if (!var1.world.isStatic)
        {
            if (var1.entity.getPersistentID() == null)
            {
                var1.entity.generatePersistentID();
            }
            else
            {
                ForgeChunkManager.loadEntity(var1.entity);
            }
        }

        Entity var2 = var1.entity;

        if (var2.getClass().equals(EntityItem.class))
        {
            ItemStack var3 = ((EntityItem)var2).itemStack;

            if (var3 != null && var3.getItem().hasCustomEntity(var3))
            {
                Entity var4 = var3.getItem().createEntity(var1.world, var2, var3);

                if (var4 != null)
                {
                    var2.die();
                    var1.setCanceled(true);
                    var1.world.addEntity(var4);
                }
            }
        }
    }

    @ForgeSubscribe(
            priority = EventPriority.HIGHEST
    )
    public void onDimensionLoad(WorldEvent$Load var1)
    {
        ForgeChunkManager.loadWorld(var1.world);
    }

    @ForgeSubscribe(
            priority = EventPriority.HIGHEST
    )
    public void onDimensionSave(WorldEvent$Save var1)
    {
        ForgeChunkManager.saveWorld(var1.world);
    }
}
