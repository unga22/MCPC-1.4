package cpw.mods.fml.common;

import java.util.Random;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public interface IDispenseHandler
{
    @Deprecated
    int dispense(double var1, double var3, double var5, int var7, int var8, World var9, ItemStack var10, Random var11, double var12, double var14, double var16);
}
