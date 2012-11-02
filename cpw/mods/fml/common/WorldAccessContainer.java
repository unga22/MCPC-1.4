package cpw.mods.fml.common;

import java.util.Map;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldNBTStorage;

public interface WorldAccessContainer
{
    NBTTagCompound getDataForWriting(WorldNBTStorage var1, WorldData var2);

    void readData(WorldNBTStorage var1, WorldData var2, Map var3, NBTTagCompound var4);
}
