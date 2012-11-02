package net.minecraftforge.event.world;

import net.minecraft.server.Chunk;
import net.minecraft.server.NBTTagCompound;

public class ChunkDataEvent extends ChunkEvent
{
    private final NBTTagCompound data;

    public ChunkDataEvent(Chunk var1, NBTTagCompound var2)
    {
        super(var1);
        this.data = var2;
    }

    public NBTTagCompound getData()
    {
        return this.data;
    }
}
