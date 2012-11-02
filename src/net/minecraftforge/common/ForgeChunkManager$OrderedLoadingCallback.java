package net.minecraftforge.common;

import java.util.List;
import net.minecraft.server.World;
import net.minecraftforge.common.ForgeChunkManager$LoadingCallback;

public interface ForgeChunkManager$OrderedLoadingCallback extends ForgeChunkManager$LoadingCallback
{
    List ticketsLoaded(List var1, World var2, int var3);
}
