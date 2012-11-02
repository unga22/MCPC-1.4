package net.minecraftforge.common;

import java.util.ArrayList;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public interface IShearable
{
    boolean isShearable(ItemStack var1, World var2, int var3, int var4, int var5);

    ArrayList onSheared(ItemStack var1, World var2, int var3, int var4, int var5, int var6);
}
