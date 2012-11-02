package net.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class PlayerInstance
{
    /** the list of all players in this instance (chunk) */
    private final List b;

    /** the chunk the player currently resides in */
    private final ChunkCoordIntPair location;

    /** array of blocks to update this tick */
    private short[] dirtyBlocks;

    /** the number of blocks that need to be updated next tick */
    private int dirtyCount;
    private int field_73260_f;
    final PlayerManager playerManager;

    public PlayerInstance(PlayerManager var1, int var2, int var3)
    {
        this.playerManager = var1;
        this.b = new ArrayList();
        this.dirtyBlocks = new short[64];
        this.dirtyCount = 0;
        this.location = new ChunkCoordIntPair(var2, var3);
        var1.a().chunkProviderServer.getChunkAt(var2, var3);
    }

    /**
     * adds this player to the playerInstance
     */
    public void a(EntityPlayer var1)
    {
        if (this.b.contains(var1))
        {
            throw new IllegalStateException("Failed to add player. " + var1 + " already is in chunk " + this.location.x + ", " + this.location.z);
        }
        else
        {
            this.b.add(var1);
            var1.chunkCoordIntPairQueue.add(this.location);
        }
    }

    /**
     * remove player from this instance
     */
    public void b(EntityPlayer var1)
    {
        if (this.b.contains(var1))
        {
            var1.netServerHandler.sendPacket(new Packet51MapChunk(PlayerManager.a(this.playerManager).getChunkAt(this.location.x, this.location.z), true, 0));
            this.b.remove(var1);
            var1.chunkCoordIntPairQueue.remove(this.location);

            if (this.b.isEmpty())
            {
                long var2 = (long)this.location.x + 2147483647L | (long)this.location.z + 2147483647L << 32;
                PlayerManager.b(this.playerManager).remove(var2);

                if (this.dirtyCount > 0)
                {
                    PlayerManager.c(this.playerManager).remove(this);
                }

                this.playerManager.a().chunkProviderServer.queueUnload(this.location.x, this.location.z);
            }
        }
    }

    /**
     * mark the block as changed so that it will update clients who need to know about it
     */
    public void a(int var1, int var2, int var3)
    {
        if (this.dirtyCount == 0)
        {
            PlayerManager.c(this.playerManager).add(this);
        }

        this.field_73260_f |= 1 << (var2 >> 4);

        if (this.dirtyCount < 64)
        {
            short var4 = (short)(var1 << 12 | var3 << 8 | var2);

            for (int var5 = 0; var5 < this.dirtyCount; ++var5)
            {
                if (this.dirtyBlocks[var5] == var4)
                {
                    return;
                }
            }

            this.dirtyBlocks[this.dirtyCount++] = var4;
        }
    }

    /**
     * sends the packet to all players in the current instance
     */
    public void sendAll(Packet var1)
    {
        Iterator var2 = this.b.iterator();

        while (var2.hasNext())
        {
            EntityPlayer var3 = (EntityPlayer)var2.next();

            if (!var3.chunkCoordIntPairQueue.contains(this.location))
            {
                var3.netServerHandler.sendPacket(var1);
            }
        }
    }

    public void a()
    {
        if (this.dirtyCount != 0)
        {
            int var1;
            int var2;
            int var3;

            if (this.dirtyCount == 1)
            {
                var1 = this.location.x * 16 + (this.dirtyBlocks[0] >> 12 & 15);
                var2 = this.dirtyBlocks[0] & 255;
                var3 = this.location.z * 16 + (this.dirtyBlocks[0] >> 8 & 15);
                this.sendAll(new Packet53BlockChange(var1, var2, var3, PlayerManager.a(this.playerManager)));

                if (PlayerManager.a(this.playerManager).isTileEntity(var1, var2, var3))
                {
                    this.sendTileEntity(PlayerManager.a(this.playerManager).getTileEntity(var1, var2, var3));
                }
            }
            else
            {
                int var4;

                if (this.dirtyCount == 64)
                {
                    var1 = this.location.x * 16;
                    var2 = this.location.z * 16;
                    this.sendAll(new Packet51MapChunk(PlayerManager.a(this.playerManager).getChunkAt(this.location.x, this.location.z), false, this.field_73260_f));

                    for (var3 = 0; var3 < 16; ++var3)
                    {
                        if ((this.field_73260_f & 1 << var3) != 0)
                        {
                            var4 = var3 << 4;
                            List var5 = PlayerManager.a(this.playerManager).getTileEntities(var1, var4, var2, var1 + 15, var4 + 16, var2 + 15);
                            Iterator var6 = var5.iterator();

                            while (var6.hasNext())
                            {
                                TileEntity var7 = (TileEntity)var6.next();
                                this.sendTileEntity(var7);
                            }
                        }
                    }
                }
                else
                {
                    this.sendAll(new Packet52MultiBlockChange(this.location.x, this.location.z, this.dirtyBlocks, this.dirtyCount, PlayerManager.a(this.playerManager)));

                    for (var1 = 0; var1 < this.dirtyCount; ++var1)
                    {
                        var2 = this.location.x * 16 + (this.dirtyBlocks[var1] >> 12 & 15);
                        var3 = this.dirtyBlocks[var1] & 255;
                        var4 = this.location.z * 16 + (this.dirtyBlocks[var1] >> 8 & 15);

                        if (PlayerManager.a(this.playerManager).isTileEntity(var2, var3, var4))
                        {
                            this.sendTileEntity(PlayerManager.a(this.playerManager).getTileEntity(var2, var3, var4));
                        }
                    }
                }
            }

            this.dirtyCount = 0;
            this.field_73260_f = 0;
        }
    }

    /**
     * sends players update packet about the given entity
     */
    private void sendTileEntity(TileEntity var1)
    {
        if (var1 != null)
        {
            Packet var2 = var1.l();

            if (var2 != null)
            {
                this.sendAll(var2);
            }
        }
    }

    static ChunkCoordIntPair a(PlayerInstance var0)
    {
        return var0.location;
    }

    static List b(PlayerInstance var0)
    {
        return var0.b;
    }
}
