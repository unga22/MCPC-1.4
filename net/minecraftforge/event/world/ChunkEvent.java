package net.minecraftforge.event.world;

import net.minecraft.server.Chunk;

public class ChunkEvent extends WorldEvent
{
    private final Chunk chunk;

    public ChunkEvent(Chunk var1)
    {
        super(var1.world);
        this.chunk = var1;
    }

    public Chunk getChunk()
    {
        return this.chunk;
    }
}
