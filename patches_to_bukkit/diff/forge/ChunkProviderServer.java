package net.minecraft.server;

import cpw.mods.fml.common.registry.GameRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

public class ChunkProviderServer implements IChunkProvider
{
    private Set unloadQueue = new HashSet();

    /** a dummy chunk, returned in place of an actual chunk. */
    private Chunk emptyChunk;

    /**
     * chunk generator object. Calls to load nonexistent chunks are forwarded to this object.
     */
    private IChunkProvider chunkProvider;
    IChunkLoader e;

    /**
     * if set, this flag forces a request to load a chunk to load the chunk rather than defaulting to the dummy if
     * possible
     */
    public boolean forceChunkLoad = true;

    /** map of chunk Id's to Chunk instances */
    private LongHashMap chunks = new LongHashMap();
    private List chunkList = new ArrayList();
    private WorldServer world;

    public ChunkProviderServer(WorldServer var1, IChunkLoader var2, IChunkProvider var3)
    {
        this.emptyChunk = new EmptyChunk(var1, 0, 0);
        this.world = var1;
        this.e = var2;
        this.chunkProvider = var3;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean isChunkLoaded(int var1, int var2)
    {
        return this.chunks.contains(ChunkCoordIntPair.a(var1, var2));
    }

    public void queueUnload(int var1, int var2)
    {
        if (this.world.worldProvider.e() && DimensionManager.shouldLoadSpawn(this.world.worldProvider.dimension))
        {
            ChunkCoordinates var3 = this.world.getSpawn();
            int var4 = var1 * 16 + 8 - var3.x;
            int var5 = var2 * 16 + 8 - var3.z;
            short var6 = 128;

            if (var4 < -var6 || var4 > var6 || var5 < -var6 || var5 > var6)
            {
                this.unloadQueue.add(Long.valueOf(ChunkCoordIntPair.a(var1, var2)));
            }
        }
        else
        {
            this.unloadQueue.add(Long.valueOf(ChunkCoordIntPair.a(var1, var2)));
        }
    }

    /**
     * marks all chunks for unload, ignoring those near the spawn
     */
    public void a()
    {
        Iterator var1 = this.chunkList.iterator();

        while (var1.hasNext())
        {
            Chunk var2 = (Chunk)var1.next();
            this.queueUnload(var2.x, var2.z);
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk getChunkAt(int var1, int var2)
    {
        long var3 = ChunkCoordIntPair.a(var1, var2);
        this.unloadQueue.remove(Long.valueOf(var3));
        Chunk var5 = (Chunk)this.chunks.getEntry(var3);

        if (var5 == null)
        {
            var5 = ForgeChunkManager.fetchDormantChunk(var3, this.world);

            if (var5 == null)
            {
                var5 = this.loadChunk(var1, var2);
            }

            if (var5 == null)
            {
                if (this.chunkProvider == null)
                {
                    var5 = this.emptyChunk;
                }
                else
                {
                    var5 = this.chunkProvider.getOrCreateChunk(var1, var2);
                }
            }

            this.chunks.put(var3, var5);
            this.chunkList.add(var5);

            if (var5 != null)
            {
                var5.addEntities();
            }

            var5.a(this, this, var1, var2);
        }

        return var5;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk getOrCreateChunk(int var1, int var2)
    {
        Chunk var3 = (Chunk)this.chunks.getEntry(ChunkCoordIntPair.a(var1, var2));
        return var3 == null ? (!this.world.isLoading && !this.forceChunkLoad ? this.emptyChunk : this.getChunkAt(var1, var2)) : var3;
    }

    private Chunk loadChunk(int var1, int var2)
    {
        if (this.e == null)
        {
            return null;
        }
        else
        {
            try
            {
                Chunk var3 = this.e.a(this.world, var1, var2);

                if (var3 != null)
                {
                    var3.n = this.world.getTime();

                    if (this.chunkProvider != null)
                    {
                        this.chunkProvider.func_82695_e(var1, var2);
                    }
                }

                return var3;
            }
            catch (Exception var4)
            {
                var4.printStackTrace();
                return null;
            }
        }
    }

    private void saveChunkNOP(Chunk var1)
    {
        if (this.e != null)
        {
            try
            {
                this.e.b(this.world, var1);
            }
            catch (Exception var3)
            {
                var3.printStackTrace();
            }
        }
    }

    private void saveChunk(Chunk var1)
    {
        if (this.e != null)
        {
            try
            {
                var1.n = this.world.getTime();
                this.e.a(this.world, var1);
            }
            catch (IOException var3)
            {
                var3.printStackTrace();
            }
            catch (ExceptionWorldConflict var4)
            {
                var4.printStackTrace();
            }
        }
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void getChunkAt(IChunkProvider var1, int var2, int var3)
    {
        Chunk var4 = this.getOrCreateChunk(var2, var3);

        if (!var4.done)
        {
            var4.done = true;

            if (this.chunkProvider != null)
            {
                this.chunkProvider.getChunkAt(var1, var2, var3);
                GameRegistry.generateWorld(var2, var3, this.world, this.chunkProvider, var1);
                var4.e();
            }
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean var1, IProgressUpdate var2)
    {
        int var3 = 0;
        Iterator var4 = this.chunkList.iterator();

        while (var4.hasNext())
        {
            Chunk var5 = (Chunk)var4.next();

            if (var1)
            {
                this.saveChunkNOP(var5);
            }

            if (var5.a(var1))
            {
                this.saveChunk(var5);
                var5.l = false;
                ++var3;

                if (var3 == 24 && !var1)
                {
                    return false;
                }
            }
        }

        if (var1)
        {
            if (this.e == null)
            {
                return true;
            }

            this.e.b();
        }

        return true;
    }

    /**
     * Unloads the 100 oldest chunks from memory, due to a bug with chunkSet.add() never being called it thinks the list
     * is always empty and will not remove any chunks.
     */
    public boolean unloadChunks()
    {
        if (!this.world.savingDisabled)
        {
            Iterator var1 = this.world.getPersistentChunks().keySet().iterator();

            while (var1.hasNext())
            {
                ChunkCoordIntPair var2 = (ChunkCoordIntPair)var1.next();
                this.unloadQueue.remove(Long.valueOf(ChunkCoordIntPair.a(var2.x, var2.z)));
            }

            for (int var4 = 0; var4 < 100; ++var4)
            {
                if (!this.unloadQueue.isEmpty())
                {
                    Long var5 = (Long)this.unloadQueue.iterator().next();
                    Chunk var3 = (Chunk)this.chunks.getEntry(var5.longValue());
                    var3.removeEntities();
                    this.saveChunk(var3);
                    this.saveChunkNOP(var3);
                    this.unloadQueue.remove(var5);
                    this.chunks.remove(var5.longValue());
                    this.chunkList.remove(var3);
                    ForgeChunkManager.putDormantChunk(ChunkCoordIntPair.a(var3.x, var3.z), var3);

                    if (this.chunkList.size() == 0 && ForgeChunkManager.getPersistentChunksFor(this.world).size() == 0 && !DimensionManager.shouldLoadSpawn(this.world.worldProvider.dimension))
                    {
                        DimensionManager.unloadWorld(this.world.worldProvider.dimension);
                        return this.chunkProvider.unloadChunks();
                    }
                }
            }

            if (this.e != null)
            {
                this.e.a();
            }
        }

        return this.chunkProvider.unloadChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return !this.world.savingDisabled;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String getName()
    {
        return "ServerChunkCache: " + this.chunks.count() + " Drop: " + this.unloadQueue.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getMobsFor(EnumCreatureType var1, int var2, int var3, int var4)
    {
        return this.chunkProvider.getMobsFor(var1, var2, var3, var4);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findNearestMapFeature(World var1, String var2, int var3, int var4, int var5)
    {
        return this.chunkProvider.findNearestMapFeature(var1, var2, var3, var4, var5);
    }

    public int getLoadedChunks()
    {
        return this.chunks.count();
    }

    public void func_82695_e(int var1, int var2) {}
}
