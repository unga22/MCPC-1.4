package net.minecraftforge.common;

import java.util.Random;
import net.minecraft.server.ItemStack;
import net.minecraft.server.WeightedRandomChoice;

public class DungeonHooks$DungeonLoot extends WeightedRandomChoice
{
    private ItemStack itemStack;
    private int minCount = 1;
    private int maxCount = 1;

    public DungeonHooks$DungeonLoot(int var1, ItemStack var2, int var3, int var4)
    {
        super(var1);
        this.itemStack = var2;
        this.minCount = var3;
        this.maxCount = var4;
    }

    public ItemStack generateStack(Random var1)
    {
        ItemStack var2 = this.itemStack.cloneItemStack();
        var2.count = this.minCount + var1.nextInt(this.maxCount - this.minCount + 1);
        return var2;
    }

    public boolean equals(ItemStack var1, int var2, int var3)
    {
        return var2 == this.minCount && var3 == this.maxCount && var1.doMaterialsMatch(this.itemStack);
    }

    public boolean equals(ItemStack var1)
    {
        return var1.doMaterialsMatch(this.itemStack);
    }
}
