package net.minecraftforge.common;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.ItemStack;
import net.minecraftforge.common.ISpecialArmor$ArmorProperties;

public interface ISpecialArmor
{
    ISpecialArmor$ArmorProperties getProperties(EntityLiving var1, ItemStack var2, DamageSource var3, double var4, int var6);

    int getArmorDisplay(EntityHuman var1, ItemStack var2, int var3);

    void damageArmor(EntityLiving var1, ItemStack var2, DamageSource var3, int var4, int var5);
}
