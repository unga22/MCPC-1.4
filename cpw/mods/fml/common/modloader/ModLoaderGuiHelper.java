package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.World;

public class ModLoaderGuiHelper implements IGuiHandler
{
    private BaseModProxy mod;
    private int id;
    private Container container;

    ModLoaderGuiHelper(BaseModProxy var1, int var2)
    {
        this.mod = var1;
        this.id = var2;
    }

    public Object getServerGuiElement(int var1, EntityHuman var2, World var3, int var4, int var5, int var6)
    {
        return this.container;
    }

    public Object getClientGuiElement(int var1, EntityHuman var2, World var3, int var4, int var5, int var6)
    {
        return ModLoaderHelper.getClientSideGui(this.mod, var2, var1, var4, var5, var6);
    }

    public void injectContainer(Container var1)
    {
        this.container = var1;
    }

    public Object getMod()
    {
        return this.mod;
    }
}
