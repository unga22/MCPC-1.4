package net.minecraftforge.event.entity;

import net.minecraft.server.Entity;

public class EntityEvent$EnteringChunk extends EntityEvent
{
    public int newChunkX;
    public int newChunkZ;
    public int oldChunkX;
    public int oldChunkZ;

    public EntityEvent$EnteringChunk(Entity var1, int var2, int var3, int var4, int var5)
    {
        super(var1);
        this.newChunkX = var2;
        this.newChunkZ = var3;
        this.oldChunkX = var4;
        this.oldChunkZ = var5;
    }
}
