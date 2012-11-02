package net.minecraftforge.common;

import net.minecraft.server.WeightedRandomChoice;

public class DungeonHooks$DungeonMob extends WeightedRandomChoice
{
    public String type;

    public DungeonHooks$DungeonMob(int var1, String var2)
    {
        super(var1);
        this.type = var2;
    }

    public boolean equals(Object var1)
    {
        return var1 instanceof DungeonHooks$DungeonMob ? this.type.equals(((DungeonHooks$DungeonMob)var1).type) : false;
    }
}
