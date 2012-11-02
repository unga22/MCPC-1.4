package net.minecraftforge.common;

import net.minecraft.server.World;

public interface IPlantable
{
    EnumPlantType getPlantType(World var1, int var2, int var3, int var4);

    int getPlantID(World var1, int var2, int var3, int var4);

    int getPlantMetadata(World var1, int var2, int var3, int var4);
}
