package net.minecraftforge.event.entity.player;

import java.util.ArrayList;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EnchantmentManager;
import net.minecraft.server.EntityHuman;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

@Cancelable
public class PlayerDropsEvent extends LivingDropsEvent
{
    public final EntityHuman entityPlayer;

    public PlayerDropsEvent(EntityHuman var1, DamageSource var2, ArrayList var3, boolean var4)
    {
        super(var1, var2, var3, var2.getEntity() instanceof EntityHuman ? EnchantmentManager.getBonusMonsterLootEnchantmentLevel((EntityHuman)var2.getEntity()) : 0, var4, 0);
        this.entityPlayer = var1;
    }
}
