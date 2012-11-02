package net.minecraftforge.common;

import net.minecraft.server.ItemStack;
import net.minecraft.server.WeightedRandomChoice;

class ForgeHooks$SeedEntry extends WeightedRandomChoice
{
    public final ItemStack seed;

    public ForgeHooks$SeedEntry(ItemStack var1, int var2)
    {
        super(var2);
        this.seed = var1;
    }
}
