package net.minecraftforge.event.world;

import net.minecraft.server.Chunk;
import net.minecraft.server.NBTTagCompound;

public class ChunkDataEvent$Load extends ChunkDataEvent
{
    public ChunkDataEvent$Load(Chunk var1, NBTTagCompound var2)
    {
        super(var1, var2);
    }
}
