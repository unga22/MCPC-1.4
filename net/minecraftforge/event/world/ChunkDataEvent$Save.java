package net.minecraftforge.event.world;

import net.minecraft.server.Chunk;
import net.minecraft.server.NBTTagCompound;

public class ChunkDataEvent$Save extends ChunkDataEvent
{
    public ChunkDataEvent$Save(Chunk var1, NBTTagCompound var2)
    {
        super(var1, var2);
    }
}
