package cpw.mods.fml.common;

import java.util.Random;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public interface IDispenserHandler
{
    int dispense(int var1, int var2, int var3, int var4, int var5, World var6, ItemStack var7, Random var8, double var9, double var11, double var13);
}
