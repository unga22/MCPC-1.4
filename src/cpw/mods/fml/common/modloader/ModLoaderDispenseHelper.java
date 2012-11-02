package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.IDispenserHandler;
import java.util.Random;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ModLoaderDispenseHelper implements IDispenserHandler
{
    private BaseModProxy mod;

    public ModLoaderDispenseHelper(BaseModProxy var1)
    {
        this.mod = var1;
    }

    public int dispense(int var1, int var2, int var3, int var4, int var5, World var6, ItemStack var7, Random var8, double var9, double var11, double var13)
    {
        int var15 = this.mod.dispenseEntity(var6, var7, var8, var1, var2, var3, var4, var5, var9, var11, var13);
        return var15 == 0 ? -1 : var15;
    }
}
