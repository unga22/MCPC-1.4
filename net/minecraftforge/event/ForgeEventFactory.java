package net.minecraftforge.event;

import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent$HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent$Action;

public class ForgeEventFactory
{
    public static boolean doPlayerHarvestCheck(EntityHuman var0, Block var1, boolean var2)
    {
        PlayerEvent$HarvestCheck var3 = new PlayerEvent$HarvestCheck(var0, var1, var2);
        MinecraftForge.EVENT_BUS.post(var3);
        return var3.success;
    }

    public static float getBreakSpeed(EntityHuman var0, Block var1, int var2, float var3)
    {
        PlayerEvent$BreakSpeed var4 = new PlayerEvent$BreakSpeed(var0, var1, var2, var3);
        return MinecraftForge.EVENT_BUS.post(var4) ? -1.0F : var4.newSpeed;
    }

    public static PlayerInteractEvent onPlayerInteract(EntityHuman var0, PlayerInteractEvent$Action var1, int var2, int var3, int var4, int var5)
    {
        PlayerInteractEvent var6 = new PlayerInteractEvent(var0, var1, var2, var3, var4, var5);
        MinecraftForge.EVENT_BUS.post(var6);
        return var6;
    }

    public static void onPlayerDestroyItem(EntityHuman var0, ItemStack var1)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(var0, var1));
    }
}
