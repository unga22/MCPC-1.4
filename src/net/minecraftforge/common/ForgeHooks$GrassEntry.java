package net.minecraftforge.common;

import net.minecraft.server.Block;
import net.minecraft.server.WeightedRandomChoice;

class ForgeHooks$GrassEntry extends WeightedRandomChoice
{
    public final Block block;
    public final int metadata;

    public ForgeHooks$GrassEntry(Block var1, int var2, int var3)
    {
        super(var3);
        this.block = var1;
        this.metadata = var2;
    }
}
