package cpw.mods.fml.common.registry;

import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IDispenserHandler;
import java.util.Random;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

final class GameRegistry$1 implements IDispenserHandler
{
    final IDispenseHandler val$handler;

    GameRegistry$1(IDispenseHandler var1)
    {
        this.val$handler = var1;
    }

    public int dispense(int var1, int var2, int var3, int var4, int var5, World var6, ItemStack var7, Random var8, double var9, double var11, double var13)
    {
        return this.val$handler.dispense((double)var1, (double)var2, (double)var3, var4, var5, var6, var7, var8, var9, var11, var13);
    }
}
