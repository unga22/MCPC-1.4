package cpw.mods.fml.common.network;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.World;

public interface IGuiHandler
{
    Object getServerGuiElement(int var1, EntityHuman var2, World var3, int var4, int var5, int var6);

    Object getClientGuiElement(int var1, EntityHuman var2, World var3, int var4, int var5, int var6);
}
