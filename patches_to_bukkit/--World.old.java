package net.minecraft.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class World implements IBlockAccess
{
    /**
     * boolean; if true updates scheduled by scheduleBlockUpdate happen immediately
     */
    public boolean d = false;

    /** A list of all Entities in all currently-loaded chunks */
    public List entityList = new ArrayList();
    protected List f = new ArrayList();

    /** A list of all TileEntities in all currently-loaded chunks */
    public List tileEntityList = new ArrayList();
    private List a = new ArrayList();

    /** Entities marked for removal. */
    private List b = new ArrayList();

    /** Array list of players in the world. */
    public List players = new ArrayList();

    /** a list of all the lightning entities */
    public List i = new ArrayList();
    private long c = 16777215L;

    /** How much light is subtracted from full daylight */
    public int j = 0;

    /**
     * Contains the current Linear Congruential Generator seed for block updates. Used with an A value of 3 and a C
     * value of 0x3c6ef35f, producing a highly planar series of values ill-suited for choosing random blocks in a
     * 16x128x16 field.
     */
    protected int k = (new Random()).nextInt();

    /**
     * magic number used to generate fast random numbers for 3d distribution within a chunk
     */
    protected final int l = 1013904223;
    protected float m;
    protected float n;
    protected float o;
    protected float p;

    /**
     * Set to 2 whenever a lightning bolt is generated in SSP. Decrements if > 0 in updateWeather(). Value appears to be
     * unused.
     */
    protected int q = 0;

    /**
     * If > 0, the sky and skylight colors are illuminated by a lightning flash
     */
    public int r = 0;

    /** true while the server is editing blocks */
    public boolean suppressPhysics = false;

    /** Whether monsters are enabled or not. (1 = on, 0 = off) */
    public int difficulty;

    /** RNG for World. */
    public Random random = new Random();

    /** The WorldProvider instance that World uses. */
    public final WorldProvider worldProvider;
    protected List w = new ArrayList();

    /** Handles chunk operations and caching */
    protected IChunkProvider chunkProvider;
    protected final IDataManager dataManager;

    /**
     * holds information about a world (size on disk, time, spawn point, seed, ...)
     */
    protected WorldData worldData;

    /**
     * if set, this flag forces a request to load a chunk to load the chunk rather than defaulting to the world's
     * chunkprovider's dummy if possible
     */
    public boolean isLoading;
    public WorldMapCollection worldMaps;
    public final VillageCollection villages;
    protected final VillageSiege siegeManager = new VillageSiege(this);
    public final MethodProfiler methodProfiler;
    private final Vec3DPool field_82741_K = new Vec3DPool(300, 2000);
    private final Calendar L = Calendar.getInstance();
    private ArrayList M = new ArrayList();
    private boolean N;

    /** indicates if enemies are spawned or not */
    protected boolean allowMonsters = true;

    /** A flag indicating whether we should spawn peaceful mobs. */
    protected boolean allowAnimals = true;

    /** populated by chunks that are within 9 chunks of any player */
    protected Set chunkTickList = new HashSet();

    /** number of ticks until the next random ambients play */
    private int O;

    /**
     * is a temporary list of blocks and light values used when updating light levels. Holds up to 32x32x32 blocks (the
     * maximum influence of a light source.) Every element is a packed bit value: 0000000000LLLLzzzzzzyyyyyyxxxxxx. The
     * 4-bit L is a light level used when darkening blocks. 6-bit numbers x, y and z represent the block's offset from
     * the original block, plus 32 (i.e. value of 31 would mean a -1 offset
     */
    int[] I;

    /**
     * entities within AxisAlignedBB excluding one, set and returned in getEntitiesWithinAABBExcludingEntity(Entity
     * var1, AxisAlignedBB var2)
     */
    private List P;

    /**
     * This is set to true when you are a client connected to a multiplayer world, false otherwise.
     */
    public boolean isStatic;

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    public BiomeBase getBiome(int var1, int var2)
    {
        if (this.isLoaded(var1, 0, var2))
        {
            Chunk var3 = this.getChunkAtWorldCoords(var1, var2);

            if (var3 != null)
            {
                return var3.a(var1 & 15, var2 & 15, this.worldProvider.d);
            }
        }

        return this.worldProvider.d.a(var1, var2);
    }

    public WorldChunkManager getWorldChunkManager()
    {
        return this.worldProvider.d;
    }

    public World(IDataManager var1, String var2, WorldSettings var3, WorldProvider var4, MethodProfiler var5)
    {
        this.O = this.random.nextInt(12000);
        this.I = new int[32768];
        this.P = new ArrayList();
        this.isStatic = false;
        this.dataManager = var1;
        this.methodProfiler = var5;
        this.worldMaps = new WorldMapCollection(var1);
        this.worldData = var1.getWorldData();

        if (var4 != null)
        {
            this.worldProvider = var4;
        }
        else if (this.worldData != null && this.worldData.j() != 0)
        {
            this.worldProvider = WorldProvider.byDimension(this.worldData.j());
        }
        else
        {
            this.worldProvider = WorldProvider.byDimension(0);
        }

        if (this.worldData == null)
        {
            this.worldData = new WorldData(var3, var2);
        }
        else
        {
            this.worldData.setName(var2);
        }

        this.worldProvider.a(this);
        this.chunkProvider = this.j();

        if (!this.worldData.isInitialized())
        {
            this.a(var3);
            this.worldData.d(true);
        }

        VillageCollection var6 = (VillageCollection)this.worldMaps.get(VillageCollection.class, "villages");

        if (var6 == null)
        {
            this.villages = new VillageCollection(this);
            this.worldMaps.a("villages", this.villages);
        }
        else
        {
            this.villages = var6;
            this.villages.func_82566_a(this);
        }

        this.w();
        this.a();
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected abstract IChunkProvider j();

    protected void a(WorldSettings var1)
    {
        this.worldData.d(true);
    }

    /**
     * Returns the block ID of the first block at this (x,z) location with air above it, searching from sea level
     * upwards.
     */
    public int b(int var1, int var2)
    {
        int var3;

        for (var3 = 63; !this.isEmpty(var1, var3 + 1, var2); ++var3)
        {
            ;
        }

        return this.getTypeId(var1, var3, var2);
    }

    /**
     * Returns the block ID at coords x,y,z
     */
    public int getTypeId(int var1, int var2, int var3)
    {
        return var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000 ? (var2 < 0 ? 0 : (var2 >= 256 ? 0 : this.getChunkAt(var1 >> 4, var3 >> 4).getTypeId(var1 & 15, var2, var3 & 15))) : 0;
    }

    public int b(int var1, int var2, int var3)
    {
        return var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000 ? (var2 < 0 ? 0 : (var2 >= 256 ? 0 : this.getChunkAt(var1 >> 4, var3 >> 4).b(var1 & 15, var2, var3 & 15))) : 0;
    }

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isEmpty(int var1, int var2, int var3)
    {
        return this.getTypeId(var1, var2, var3) == 0;
    }

    /**
     * Checks if a block at a given position should have a tile entity.
     */
    public boolean isTileEntity(int var1, int var2, int var3)
    {
        int var4 = this.getTypeId(var1, var2, var3);
        return Block.byId[var4] != null && Block.byId[var4].u();
    }

    /**
     * Returns whether a block exists at world coordinates x, y, z
     */
    public boolean isLoaded(int var1, int var2, int var3)
    {
        return var2 >= 0 && var2 < 256 ? this.isChunkLoaded(var1 >> 4, var3 >> 4) : false;
    }

    /**
     * Checks if any of the chunks within distance (argument 4) blocks of the given block exist
     */
    public boolean areChunksLoaded(int var1, int var2, int var3, int var4)
    {
        return this.d(var1 - var4, var2 - var4, var3 - var4, var1 + var4, var2 + var4, var3 + var4);
    }

    /**
     * Checks between a min and max all the chunks inbetween actually exist. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public boolean d(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        if (var5 >= 0 && var2 < 256)
        {
            var1 >>= 4;
            var3 >>= 4;
            var4 >>= 4;
            var6 >>= 4;

            for (int var7 = var1; var7 <= var4; ++var7)
            {
                for (int var8 = var3; var8 <= var6; ++var8)
                {
                    if (!this.isChunkLoaded(var7, var8))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    protected boolean isChunkLoaded(int var1, int var2)
    {
        return this.chunkProvider.isChunkLoaded(var1, var2);
    }

    /**
     * Returns a chunk looked up by block coordinates. Args: x, z
     */
    public Chunk getChunkAtWorldCoords(int var1, int var2)
    {
        return this.getChunkAt(var1 >> 4, var2 >> 4);
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    public Chunk getChunkAt(int var1, int var2)
    {
        return this.chunkProvider.getOrCreateChunk(var1, var2);
    }

    /**
     * Sets the block ID and metadata of a block in global coordinates
     */
    public boolean setRawTypeIdAndData(int var1, int var2, int var3, int var4, int var5)
    {
        return this.setRawTypeIdAndData(var1, var2, var3, var4, var5, true);
    }

    /**
     * Sets the block ID and metadata of a block, optionally marking it as needing update. Args: X,Y,Z, blockID,
     * metadata, needsUpdate
     */
    public boolean setRawTypeIdAndData(int var1, int var2, int var3, int var4, int var5, boolean var6)
    {
        if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000)
        {
            if (var2 < 0)
            {
                return false;
            }
            else if (var2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var7 = this.getChunkAt(var1 >> 4, var3 >> 4);
                boolean var8 = var7.a(var1 & 15, var2, var3 & 15, var4, var5);
                this.methodProfiler.a("checkLight");
                this.x(var1, var2, var3);
                this.methodProfiler.b();

                if (var6 && var8 && (this.isStatic || var7.seenByPlayer))
                {
                    this.notify(var1, var2, var3);
                }

                return var8;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets the block to the specified blockID at the block coordinates Args x, y, z, blockID
     */
    public boolean setRawTypeId(int var1, int var2, int var3, int var4)
    {
        if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000)
        {
            if (var2 < 0)
            {
                return false;
            }
            else if (var2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var5 = this.getChunkAt(var1 >> 4, var3 >> 4);
                boolean var6 = var5.a(var1 & 15, var2, var3 & 15, var4);
                this.methodProfiler.a("checkLight");
                this.x(var1, var2, var3);
                this.methodProfiler.b();

                if (var6 && (this.isStatic || var5.seenByPlayer))
                {
                    this.notify(var1, var2, var3);
                }

                return var6;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the block's material.
     */
    public Material getMaterial(int var1, int var2, int var3)
    {
        int var4 = this.getTypeId(var1, var2, var3);
        return var4 == 0 ? Material.AIR : Block.byId[var4].material;
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getData(int var1, int var2, int var3)
    {
        if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000)
        {
            if (var2 < 0)
            {
                return 0;
            }
            else if (var2 >= 256)
            {
                return 0;
            }
            else
            {
                Chunk var4 = this.getChunkAt(var1 >> 4, var3 >> 4);
                var1 &= 15;
                var3 &= 15;
                return var4.getData(var1, var2, var3);
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Sets the blocks metadata and if set will then notify blocks that this block changed. Args: x, y, z, metadata
     */
    public void setData(int var1, int var2, int var3, int var4)
    {
        if (this.setRawData(var1, var2, var3, var4))
        {
            this.update(var1, var2, var3, this.getTypeId(var1, var2, var3));
        }
    }

    /**
     * Set the metadata of a block in global coordinates
     */
    public boolean setRawData(int var1, int var2, int var3, int var4)
    {
        if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000)
        {
            if (var2 < 0)
            {
                return false;
            }
            else if (var2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var5 = this.getChunkAt(var1 >> 4, var3 >> 4);
                int var6 = var1 & 15;
                int var7 = var3 & 15;
                boolean var8 = var5.b(var6, var2, var7, var4);

                if (var8 && (this.isStatic || var5.seenByPlayer && Block.u[var5.getTypeId(var6, var2, var7) & 4095]))
                {
                    this.notify(var1, var2, var3);
                }

                return var8;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets a block and notifies relevant systems with the block change  Args: x, y, z, blockID
     */
    public boolean setTypeId(int var1, int var2, int var3, int var4)
    {
        if (this.setRawTypeId(var1, var2, var3, var4))
        {
            this.update(var1, var2, var3, var4);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets the block ID and metadata, then notifies neighboring blocks of the change Params: x, y, z, BlockID, Metadata
     */
    public boolean setTypeIdAndData(int var1, int var2, int var3, int var4, int var5)
    {
        if (this.setRawTypeIdAndData(var1, var2, var3, var4, var5))
        {
            this.update(var1, var2, var3, var4);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Marks the block as needing an update with the renderer. Args: x, y, z
     */
    public void notify(int var1, int var2, int var3)
    {
        Iterator var4 = this.w.iterator();

        while (var4.hasNext())
        {
            IWorldAccess var5 = (IWorldAccess)var4.next();
            var5.a(var1, var2, var3);
        }
    }

    /**
     * The block type change and need to notify other systems  Args: x, y, z, blockID
     */
    public void update(int var1, int var2, int var3, int var4)
    {
        this.applyPhysics(var1, var2, var3, var4);
    }

    /**
     * marks a vertical line of blocks as dirty
     */
    public void g(int var1, int var2, int var3, int var4)
    {
        int var5;

        if (var3 > var4)
        {
            var5 = var4;
            var4 = var3;
            var3 = var5;
        }

        if (!this.worldProvider.f)
        {
            for (var5 = var3; var5 <= var4; ++var5)
            {
                this.c(EnumSkyBlock.Sky, var1, var5, var2);
            }
        }

        this.e(var1, var3, var2, var1, var4, var2);
    }

    /**
     * calls the 'MarkBlockAsNeedsUpdate' in all block accesses in this world
     */
    public void i(int var1, int var2, int var3)
    {
        Iterator var4 = this.w.iterator();

        while (var4.hasNext())
        {
            IWorldAccess var5 = (IWorldAccess)var4.next();
            var5.a(var1, var2, var3, var1, var2, var3);
        }
    }

    public void e(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        Iterator var7 = this.w.iterator();

        while (var7.hasNext())
        {
            IWorldAccess var8 = (IWorldAccess)var7.next();
            var8.a(var1, var2, var3, var4, var5, var6);
        }
    }

    /**
     * Notifies neighboring blocks that this specified block changed  Args: x, y, z, blockID
     */
    public void applyPhysics(int var1, int var2, int var3, int var4)
    {
        this.m(var1 - 1, var2, var3, var4);
        this.m(var1 + 1, var2, var3, var4);
        this.m(var1, var2 - 1, var3, var4);
        this.m(var1, var2 + 1, var3, var4);
        this.m(var1, var2, var3 - 1, var4);
        this.m(var1, var2, var3 + 1, var4);
    }

    /**
     * Notifies a block that one of its neighbor change to the specified type Args: x, y, z, blockID
     */
    private void m(int var1, int var2, int var3, int var4)
    {
        if (!this.suppressPhysics && !this.isStatic)
        {
            Block var5 = Block.byId[this.getTypeId(var1, var2, var3)];

            if (var5 != null)
            {
                var5.doPhysics(this, var1, var2, var3, var4);
            }
        }
    }

    /**
     * Checks if the specified block is able to see the sky
     */
    public boolean j(int var1, int var2, int var3)
    {
        return this.getChunkAt(var1 >> 4, var3 >> 4).d(var1 & 15, var2, var3 & 15);
    }

    /**
     * gets the block's light value - without the _do function's checks.
     */
    public int k(int var1, int var2, int var3)
    {
        if (var2 < 0)
        {
            return 0;
        }
        else
        {
            if (var2 >= 256)
            {
                var2 = 255;
            }

            return this.getChunkAt(var1 >> 4, var3 >> 4).c(var1 & 15, var2, var3 & 15, 0);
        }
    }

    /**
     * Gets the light value of a block location
     */
    public int getLightLevel(int var1, int var2, int var3)
    {
        return this.a(var1, var2, var3, true);
    }

    /**
     * Gets the light value of a block location. This is the actual function that gets the value and has a bool flag
     * that indicates if its a half step block to get the maximum light value of a direct neighboring block (left,
     * right, forward, back, and up)
     */
    public int a(int var1, int var2, int var3, boolean var4)
    {
        if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000)
        {
            if (var4)
            {
                int var5 = this.getTypeId(var1, var2, var3);

                if (var5 == Block.STEP.id || var5 == Block.WOOD_STEP.id || var5 == Block.SOIL.id || var5 == Block.COBBLESTONE_STAIRS.id || var5 == Block.WOOD_STAIRS.id)
                {
                    int var6 = this.a(var1, var2 + 1, var3, false);
                    int var7 = this.a(var1 + 1, var2, var3, false);
                    int var8 = this.a(var1 - 1, var2, var3, false);
                    int var9 = this.a(var1, var2, var3 + 1, false);
                    int var10 = this.a(var1, var2, var3 - 1, false);

                    if (var7 > var6)
                    {
                        var6 = var7;
                    }

                    if (var8 > var6)
                    {
                        var6 = var8;
                    }

                    if (var9 > var6)
                    {
                        var6 = var9;
                    }

                    if (var10 > var6)
                    {
                        var6 = var10;
                    }

                    return var6;
                }
            }

            if (var2 < 0)
            {
                return 0;
            }
            else
            {
                if (var2 >= 256)
                {
                    var2 = 255;
                }

                Chunk var11 = this.getChunkAt(var1 >> 4, var3 >> 4);
                var1 &= 15;
                var3 &= 15;
                return var11.c(var1, var2, var3, this.j);
            }
        }
        else
        {
            return 15;
        }
    }

    /**
     * Returns the y coordinate with a block in it at this x, z coordinate
     */
    public int getHighestBlockYAt(int var1, int var2)
    {
        if (var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000)
        {
            if (!this.isChunkLoaded(var1 >> 4, var2 >> 4))
            {
                return 0;
            }
            else
            {
                Chunk var3 = this.getChunkAt(var1 >> 4, var2 >> 4);
                return var3.b(var1 & 15, var2 & 15);
            }
        }
        else
        {
            return 0;
        }
    }

    public int func_82734_g(int var1, int var2)
    {
        if (var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000)
        {
            if (!this.isChunkLoaded(var1 >> 4, var2 >> 4))
            {
                return 0;
            }
            else
            {
                Chunk var3 = this.getChunkAt(var1 >> 4, var2 >> 4);
                return var3.field_82912_p;
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns saved light value without taking into account the time of day.  Either looks in the sky light map or
     * block light map based on the enumSkyBlock arg.
     */
    public int b(EnumSkyBlock var1, int var2, int var3, int var4)
    {
        if (var3 < 0)
        {
            var3 = 0;
        }

        if (var3 >= 256)
        {
            var3 = 255;
        }

        if (var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000)
        {
            int var5 = var2 >> 4;
            int var6 = var4 >> 4;

            if (!this.isChunkLoaded(var5, var6))
            {
                return var1.c;
            }
            else
            {
                Chunk var7 = this.getChunkAt(var5, var6);
                return var7.getBrightness(var1, var2 & 15, var3, var4 & 15);
            }
        }
        else
        {
            return var1.c;
        }
    }

    /**
     * Sets the light value either into the sky map or block map depending on if enumSkyBlock is set to sky or block.
     * Args: enumSkyBlock, x, y, z, lightValue
     */
    public void b(EnumSkyBlock var1, int var2, int var3, int var4, int var5)
    {
        if (var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000)
        {
            if (var3 >= 0)
            {
                if (var3 < 256)
                {
                    if (this.isChunkLoaded(var2 >> 4, var4 >> 4))
                    {
                        Chunk var6 = this.getChunkAt(var2 >> 4, var4 >> 4);
                        var6.a(var1, var2 & 15, var3, var4 & 15, var5);
                        Iterator var7 = this.w.iterator();

                        while (var7.hasNext())
                        {
                            IWorldAccess var8 = (IWorldAccess)var7.next();
                            var8.b(var2, var3, var4);
                        }
                    }
                }
            }
        }
    }

    public void func_72902_n(int var1, int var2, int var3)
    {
        Iterator var4 = this.w.iterator();

        while (var4.hasNext())
        {
            IWorldAccess var5 = (IWorldAccess)var4.next();
            var5.b(var1, var2, var3);
        }
    }

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     */
    public float o(int var1, int var2, int var3)
    {
        return this.worldProvider.g[this.getLightLevel(var1, var2, var3)];
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean t()
    {
        return this.j < 4;
    }

    /**
     * ray traces all blocks, including non-collideable ones
     */
    public MovingObjectPosition a(Vec3D var1, Vec3D var2)
    {
        return this.rayTrace(var1, var2, false, false);
    }

    public MovingObjectPosition rayTrace(Vec3D var1, Vec3D var2, boolean var3)
    {
        return this.rayTrace(var1, var2, var3, false);
    }

    public MovingObjectPosition rayTrace(Vec3D var1, Vec3D var2, boolean var3, boolean var4)
    {
        if (!Double.isNaN(var1.c) && !Double.isNaN(var1.d) && !Double.isNaN(var1.e))
        {
            if (!Double.isNaN(var2.c) && !Double.isNaN(var2.d) && !Double.isNaN(var2.e))
            {
                int var5 = MathHelper.floor(var2.c);
                int var6 = MathHelper.floor(var2.d);
                int var7 = MathHelper.floor(var2.e);
                int var8 = MathHelper.floor(var1.c);
                int var9 = MathHelper.floor(var1.d);
                int var10 = MathHelper.floor(var1.e);
                int var11 = this.getTypeId(var8, var9, var10);
                int var12 = this.getData(var8, var9, var10);
                Block var13 = Block.byId[var11];

                if ((!var4 || var13 == null || var13.e(this, var8, var9, var10) != null) && var11 > 0 && var13.a(var12, var3))
                {
                    MovingObjectPosition var14 = var13.a(this, var8, var9, var10, var1, var2);

                    if (var14 != null)
                    {
                        return var14;
                    }
                }

                var11 = 200;

                while (var11-- >= 0)
                {
                    if (Double.isNaN(var1.c) || Double.isNaN(var1.d) || Double.isNaN(var1.e))
                    {
                        return null;
                    }

                    if (var8 == var5 && var9 == var6 && var10 == var7)
                    {
                        return null;
                    }

                    boolean var39 = true;
                    boolean var40 = true;
                    boolean var41 = true;
                    double var15 = 999.0D;
                    double var17 = 999.0D;
                    double var19 = 999.0D;

                    if (var5 > var8)
                    {
                        var15 = (double)var8 + 1.0D;
                    }
                    else if (var5 < var8)
                    {
                        var15 = (double)var8 + 0.0D;
                    }
                    else
                    {
                        var39 = false;
                    }

                    if (var6 > var9)
                    {
                        var17 = (double)var9 + 1.0D;
                    }
                    else if (var6 < var9)
                    {
                        var17 = (double)var9 + 0.0D;
                    }
                    else
                    {
                        var40 = false;
                    }

                    if (var7 > var10)
                    {
                        var19 = (double)var10 + 1.0D;
                    }
                    else if (var7 < var10)
                    {
                        var19 = (double)var10 + 0.0D;
                    }
                    else
                    {
                        var41 = false;
                    }

                    double var21 = 999.0D;
                    double var23 = 999.0D;
                    double var25 = 999.0D;
                    double var27 = var2.c - var1.c;
                    double var29 = var2.d - var1.d;
                    double var31 = var2.e - var1.e;

                    if (var39)
                    {
                        var21 = (var15 - var1.c) / var27;
                    }

                    if (var40)
                    {
                        var23 = (var17 - var1.d) / var29;
                    }

                    if (var41)
                    {
                        var25 = (var19 - var1.e) / var31;
                    }

                    boolean var33 = false;
                    byte var42;

                    if (var21 < var23 && var21 < var25)
                    {
                        if (var5 > var8)
                        {
                            var42 = 4;
                        }
                        else
                        {
                            var42 = 5;
                        }

                        var1.c = var15;
                        var1.d += var29 * var21;
                        var1.e += var31 * var21;
                    }
                    else if (var23 < var25)
                    {
                        if (var6 > var9)
                        {
                            var42 = 0;
                        }
                        else
                        {
                            var42 = 1;
                        }

                        var1.c += var27 * var23;
                        var1.d = var17;
                        var1.e += var31 * var23;
                    }
                    else
                    {
                        if (var7 > var10)
                        {
                            var42 = 2;
                        }
                        else
                        {
                            var42 = 3;
                        }

                        var1.c += var27 * var25;
                        var1.d += var29 * var25;
                        var1.e = var19;
                    }

                    Vec3D var34 = this.getVec3DPool().create(var1.c, var1.d, var1.e);
                    var8 = (int)(var34.c = (double)MathHelper.floor(var1.c));

                    if (var42 == 5)
                    {
                        --var8;
                        ++var34.c;
                    }

                    var9 = (int)(var34.d = (double)MathHelper.floor(var1.d));

                    if (var42 == 1)
                    {
                        --var9;
                        ++var34.d;
                    }

                    var10 = (int)(var34.e = (double)MathHelper.floor(var1.e));

                    if (var42 == 3)
                    {
                        --var10;
                        ++var34.e;
                    }

                    int var35 = this.getTypeId(var8, var9, var10);
                    int var36 = this.getData(var8, var9, var10);
                    Block var37 = Block.byId[var35];

                    if ((!var4 || var37 == null || var37.e(this, var8, var9, var10) != null) && var35 > 0 && var37.a(var36, var3))
                    {
                        MovingObjectPosition var38 = var37.a(this, var8, var9, var10, var1, var2);

                        if (var38 != null)
                        {
                            return var38;
                        }
                    }
                }

                return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Plays a sound at the entity's position. Args: entity, sound, volume (relative to 1.0), and frequency (or pitch,
     * also relative to 1.0).
     */
    public void makeSound(Entity var1, String var2, float var3, float var4)
    {
        if (var1 != null && var2 != null)
        {
            Iterator var5 = this.w.iterator();

            while (var5.hasNext())
            {
                IWorldAccess var6 = (IWorldAccess)var5.next();
                var6.a(var2, var1.locX, var1.locY - (double)var1.height, var1.locZ, var3, var4);
            }
        }
    }

    /**
     * Play a sound effect. Many many parameters for this function. Not sure what they do, but a classic call is :
     * (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 'random.door_open', 1.0F, world.rand.nextFloat() * 0.1F +
     * 0.9F with i,j,k position of the block.
     */
    public void makeSound(double var1, double var3, double var5, String var7, float var8, float var9)
    {
        if (var7 != null)
        {
            Iterator var10 = this.w.iterator();

            while (var10.hasNext())
            {
                IWorldAccess var11 = (IWorldAccess)var10.next();
                var11.a(var7, var1, var3, var5, var8, var9);
            }
        }
    }

    public void func_72980_b(double var1, double var3, double var5, String var7, float var8, float var9) {}

    /**
     * Plays a record at the specified coordinates of the specified name. Args: recordName, x, y, z
     */
    public void a(String var1, int var2, int var3, int var4)
    {
        Iterator var5 = this.w.iterator();

        while (var5.hasNext())
        {
            IWorldAccess var6 = (IWorldAccess)var5.next();
            var6.a(var1, var2, var3, var4);
        }
    }

    /**
     * Spawns a particle.  Args particleName, x, y, z, velX, velY, velZ
     */
    public void addParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12)
    {
        Iterator var14 = this.w.iterator();

        while (var14.hasNext())
        {
            IWorldAccess var15 = (IWorldAccess)var14.next();
            var15.a(var1, var2, var4, var6, var8, var10, var12);
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean strikeLightning(Entity var1)
    {
        this.i.add(var1);
        return true;
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean addEntity(Entity var1)
    {
        int var2 = MathHelper.floor(var1.locX / 16.0D);
        int var3 = MathHelper.floor(var1.locZ / 16.0D);
        boolean var4 = false;

        if (var1 instanceof EntityHuman)
        {
            var4 = true;
        }

        if (!var4 && !this.isChunkLoaded(var2, var3))
        {
            return false;
        }
        else
        {
            if (var1 instanceof EntityHuman)
            {
                EntityHuman var5 = (EntityHuman)var1;
                this.players.add(var5);
                this.everyoneSleeping();
            }

            this.getChunkAt(var2, var3).a(var1);
            this.entityList.add(var1);
            this.a(var1);
            return true;
        }
    }

    /**
     * Start the skin for this entity downloading, if necessary, and increment its reference counter
     */
    protected void a(Entity var1)
    {
        Iterator var2 = this.w.iterator();

        while (var2.hasNext())
        {
            IWorldAccess var3 = (IWorldAccess)var2.next();
            var3.a(var1);
        }
    }

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    protected void b(Entity var1)
    {
        Iterator var2 = this.w.iterator();

        while (var2.hasNext())
        {
            IWorldAccess var3 = (IWorldAccess)var2.next();
            var3.b(var1);
        }
    }

    /**
     * Dismounts the entity (and anything riding the entity), sets the dead flag, and removes the player entity from the
     * player entity list. Called by the playerLoggedOut function.
     */
    public void kill(Entity var1)
    {
        if (var1.passenger != null)
        {
            var1.passenger.mount((Entity)null);
        }

        if (var1.vehicle != null)
        {
            var1.mount((Entity)null);
        }

        var1.die();

        if (var1 instanceof EntityHuman)
        {
            this.players.remove(var1);
            this.everyoneSleeping();
        }
    }

    /**
     * remove dat player from dem servers
     */
    public void removeEntity(Entity var1)
    {
        var1.die();

        if (var1 instanceof EntityHuman)
        {
            this.players.remove(var1);
            this.everyoneSleeping();
        }

        int var2 = var1.ai;
        int var3 = var1.ak;

        if (var1.ah && this.isChunkLoaded(var2, var3))
        {
            this.getChunkAt(var2, var3).b(var1);
        }

        this.entityList.remove(var1);
        this.b(var1);
    }

    /**
     * Adds a IWorldAccess to the list of worldAccesses
     */
    public void addIWorldAccess(IWorldAccess var1)
    {
        this.w.add(var1);
    }

    /**
     * Returns a list of bounding boxes that collide with aabb excluding the passed in entity's collision. Args: entity,
     * aabb
     */
    public List getCubes(Entity var1, AxisAlignedBB var2)
    {
        this.M.clear();
        int var3 = MathHelper.floor(var2.a);
        int var4 = MathHelper.floor(var2.d + 1.0D);
        int var5 = MathHelper.floor(var2.b);
        int var6 = MathHelper.floor(var2.e + 1.0D);
        int var7 = MathHelper.floor(var2.c);
        int var8 = MathHelper.floor(var2.f + 1.0D);

        for (int var9 = var3; var9 < var4; ++var9)
        {
            for (int var10 = var7; var10 < var8; ++var10)
            {
                if (this.isLoaded(var9, 64, var10))
                {
                    for (int var11 = var5 - 1; var11 < var6; ++var11)
                    {
                        Block var12 = Block.byId[this.getTypeId(var9, var11, var10)];

                        if (var12 != null)
                        {
                            var12.a(this, var9, var11, var10, var2, this.M, var1);
                        }
                    }
                }
            }
        }

        double var15 = 0.25D;
        List var17 = this.getEntities(var1, var2.grow(var15, var15, var15));
        Iterator var16 = var17.iterator();

        while (var16.hasNext())
        {
            Entity var13 = (Entity)var16.next();
            AxisAlignedBB var14 = var13.E();

            if (var14 != null && var14.a(var2))
            {
                this.M.add(var14);
            }

            var14 = var1.g(var13);

            if (var14 != null && var14.a(var2))
            {
                this.M.add(var14);
            }
        }

        return this.M;
    }

    /**
     * calculates and returns a list of colliding bounding boxes within a given AABB
     */
    public List a(AxisAlignedBB var1)
    {
        this.M.clear();
        int var2 = MathHelper.floor(var1.a);
        int var3 = MathHelper.floor(var1.d + 1.0D);
        int var4 = MathHelper.floor(var1.b);
        int var5 = MathHelper.floor(var1.e + 1.0D);
        int var6 = MathHelper.floor(var1.c);
        int var7 = MathHelper.floor(var1.f + 1.0D);

        for (int var8 = var2; var8 < var3; ++var8)
        {
            for (int var9 = var6; var9 < var7; ++var9)
            {
                if (this.isLoaded(var8, 64, var9))
                {
                    for (int var10 = var4 - 1; var10 < var5; ++var10)
                    {
                        Block var11 = Block.byId[this.getTypeId(var8, var10, var9)];

                        if (var11 != null)
                        {
                            var11.a(this, var8, var10, var9, var1, this.M, (Entity)null);
                        }
                    }
                }
            }
        }

        return this.M;
    }

    /**
     * Returns the amount of skylight subtracted for the current time
     */
    public int a(float var1)
    {
        float var2 = this.c(var1);
        float var3 = 1.0F - (MathHelper.cos(var2 * (float)Math.PI * 2.0F) * 2.0F + 0.5F);

        if (var3 < 0.0F)
        {
            var3 = 0.0F;
        }

        if (var3 > 1.0F)
        {
            var3 = 1.0F;
        }

        var3 = 1.0F - var3;
        var3 = (float)((double)var3 * (1.0D - (double)(this.j(var1) * 5.0F) / 16.0D));
        var3 = (float)((double)var3 * (1.0D - (double)(this.i(var1) * 5.0F) / 16.0D));
        var3 = 1.0F - var3;
        return (int)(var3 * 11.0F);
    }

    /**
     * calls calculateCelestialAngle
     */
    public float c(float var1)
    {
        return this.worldProvider.a(this.worldData.g(), var1);
    }

    /**
     * Gets the height to which rain/snow will fall. Calculates it if not already stored.
     */
    public int h(int var1, int var2)
    {
        return this.getChunkAtWorldCoords(var1, var2).d(var1 & 15, var2 & 15);
    }

    /**
     * Finds the highest block on the x, z coordinate that is solid and returns its y coord. Args x, z
     */
    public int i(int var1, int var2)
    {
        Chunk var3 = this.getChunkAtWorldCoords(var1, var2);
        int var4 = var3.h() + 15;
        var1 &= 15;

        for (var2 &= 15; var4 > 0; --var4)
        {
            int var5 = var3.getTypeId(var1, var4, var2);

            if (var5 != 0 && Block.byId[var5].material.isSolid() && Block.byId[var5].material != Material.LEAVES)
            {
                return var4 + 1;
            }
        }

        return -1;
    }

    /**
     * Used to schedule a call to the updateTick method on the specified block.
     */
    public void a(int var1, int var2, int var3, int var4, int var5) {}

    public void func_82740_a(int var1, int var2, int var3, int var4, int var5, int var6) {}

    /**
     * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
     */
    public void b(int var1, int var2, int var3, int var4, int var5) {}

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void tickEntities()
    {
        this.methodProfiler.a("entities");
        this.methodProfiler.a("global");
        int var1;
        Entity var2;

        for (var1 = 0; var1 < this.i.size(); ++var1)
        {
            var2 = (Entity)this.i.get(var1);
            var2.j_();

            if (var2.dead)
            {
                this.i.remove(var1--);
            }
        }

        this.methodProfiler.c("remove");
        this.entityList.removeAll(this.f);
        Iterator var5 = this.f.iterator();
        int var3;
        int var4;

        while (var5.hasNext())
        {
            var2 = (Entity)var5.next();
            var3 = var2.ai;
            var4 = var2.ak;

            if (var2.ah && this.isChunkLoaded(var3, var4))
            {
                this.getChunkAt(var3, var4).b(var2);
            }
        }

        var5 = this.f.iterator();

        while (var5.hasNext())
        {
            var2 = (Entity)var5.next();
            this.b(var2);
        }

        this.f.clear();
        this.methodProfiler.c("regular");

        for (var1 = 0; var1 < this.entityList.size(); ++var1)
        {
            var2 = (Entity)this.entityList.get(var1);

            if (var2.vehicle != null)
            {
                if (!var2.vehicle.dead && var2.vehicle.passenger == var2)
                {
                    continue;
                }

                var2.vehicle.passenger = null;
                var2.vehicle = null;
            }

            this.methodProfiler.a("tick");

            if (!var2.dead)
            {
                this.playerJoinedWorld(var2);
            }

            this.methodProfiler.b();
            this.methodProfiler.a("remove");

            if (var2.dead)
            {
                var3 = var2.ai;
                var4 = var2.ak;

                if (var2.ah && this.isChunkLoaded(var3, var4))
                {
                    this.getChunkAt(var3, var4).b(var2);
                }

                this.entityList.remove(var1--);
                this.b(var2);
            }

            this.methodProfiler.b();
        }

        this.methodProfiler.c("tileEntities");
        this.N = true;
        var5 = this.tileEntityList.iterator();

        while (var5.hasNext())
        {
            TileEntity var6 = (TileEntity)var5.next();

            if (!var6.r() && var6.func_70309_m() && this.isLoaded(var6.x, var6.y, var6.z))
            {
                var6.g();
            }

            if (var6.r())
            {
                var5.remove();

                if (this.isChunkLoaded(var6.x >> 4, var6.z >> 4))
                {
                    Chunk var8 = this.getChunkAt(var6.x >> 4, var6.z >> 4);

                    if (var8 != null)
                    {
                        var8.f(var6.x & 15, var6.y, var6.z & 15);
                    }
                }
            }
        }

        this.N = false;

        if (!this.b.isEmpty())
        {
            this.tileEntityList.removeAll(this.b);
            this.b.clear();
        }

        this.methodProfiler.c("pendingTileEntities");

        if (!this.a.isEmpty())
        {
            Iterator var7 = this.a.iterator();

            while (var7.hasNext())
            {
                TileEntity var9 = (TileEntity)var7.next();

                if (!var9.r())
                {
                    if (!this.tileEntityList.contains(var9))
                    {
                        this.tileEntityList.add(var9);
                    }

                    if (this.isChunkLoaded(var9.x >> 4, var9.z >> 4))
                    {
                        Chunk var10 = this.getChunkAt(var9.x >> 4, var9.z >> 4);

                        if (var10 != null)
                        {
                            var10.a(var9.x & 15, var9.y, var9.z & 15, var9);
                        }
                    }

                    this.notify(var9.x, var9.y, var9.z);
                }
            }

            this.a.clear();
        }

        this.methodProfiler.b();
        this.methodProfiler.b();
    }

    public void a(Collection var1)
    {
        if (this.N)
        {
            this.a.addAll(var1);
        }
        else
        {
            this.tileEntityList.addAll(var1);
        }
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded. Args: entity
     */
    public void playerJoinedWorld(Entity var1)
    {
        this.entityJoinedWorld(var1, true);
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void entityJoinedWorld(Entity var1, boolean var2)
    {
        int var3 = MathHelper.floor(var1.locX);
        int var4 = MathHelper.floor(var1.locZ);
        byte var5 = 32;

        if (!var2 || this.d(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5))
        {
            var1.T = var1.locX;
            var1.U = var1.locY;
            var1.V = var1.locZ;
            var1.lastYaw = var1.yaw;
            var1.lastPitch = var1.pitch;

            if (var2 && var1.ah)
            {
                if (var1.vehicle != null)
                {
                    var1.U();
                }
                else
                {
                    var1.j_();
                }
            }

            this.methodProfiler.a("chunkCheck");

            if (Double.isNaN(var1.locX) || Double.isInfinite(var1.locX))
            {
                var1.locX = var1.T;
            }

            if (Double.isNaN(var1.locY) || Double.isInfinite(var1.locY))
            {
                var1.locY = var1.U;
            }

            if (Double.isNaN(var1.locZ) || Double.isInfinite(var1.locZ))
            {
                var1.locZ = var1.V;
            }

            if (Double.isNaN((double)var1.pitch) || Double.isInfinite((double)var1.pitch))
            {
                var1.pitch = var1.lastPitch;
            }

            if (Double.isNaN((double)var1.yaw) || Double.isInfinite((double)var1.yaw))
            {
                var1.yaw = var1.lastYaw;
            }

            int var6 = MathHelper.floor(var1.locX / 16.0D);
            int var7 = MathHelper.floor(var1.locY / 16.0D);
            int var8 = MathHelper.floor(var1.locZ / 16.0D);

            if (!var1.ah || var1.ai != var6 || var1.aj != var7 || var1.ak != var8)
            {
                if (var1.ah && this.isChunkLoaded(var1.ai, var1.ak))
                {
                    this.getChunkAt(var1.ai, var1.ak).a(var1, var1.aj);
                }

                if (this.isChunkLoaded(var6, var8))
                {
                    var1.ah = true;
                    this.getChunkAt(var6, var8).a(var1);
                }
                else
                {
                    var1.ah = false;
                }
            }

            this.methodProfiler.b();

            if (var2 && var1.ah && var1.passenger != null)
            {
                if (!var1.passenger.dead && var1.passenger.vehicle == var1)
                {
                    this.playerJoinedWorld(var1.passenger);
                }
                else
                {
                    var1.passenger.vehicle = null;
                    var1.passenger = null;
                }
            }
        }
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB
     */
    public boolean b(AxisAlignedBB var1)
    {
        return this.a(var1, (Entity)null);
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB, excluding the given entity
     */
    public boolean a(AxisAlignedBB var1, Entity var2)
    {
        List var3 = this.getEntities((Entity)null, var1);
        Iterator var4 = var3.iterator();
        Entity var5;

        do
        {
            if (!var4.hasNext())
            {
                return true;
            }

            var5 = (Entity)var4.next();
        }
        while (var5.dead || !var5.m || var5 == var2);

        return false;
    }

    /**
     * Returns true if there are any blocks in the region constrained by an AxisAlignedBB
     */
    public boolean c(AxisAlignedBB var1)
    {
        int var2 = MathHelper.floor(var1.a);
        int var3 = MathHelper.floor(var1.d + 1.0D);
        int var4 = MathHelper.floor(var1.b);
        int var5 = MathHelper.floor(var1.e + 1.0D);
        int var6 = MathHelper.floor(var1.c);
        int var7 = MathHelper.floor(var1.f + 1.0D);

        if (var1.a < 0.0D)
        {
            --var2;
        }

        if (var1.b < 0.0D)
        {
            --var4;
        }

        if (var1.c < 0.0D)
        {
            --var6;
        }

        for (int var8 = var2; var8 < var3; ++var8)
        {
            for (int var9 = var4; var9 < var5; ++var9)
            {
                for (int var10 = var6; var10 < var7; ++var10)
                {
                    Block var11 = Block.byId[this.getTypeId(var8, var9, var10)];

                    if (var11 != null)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns if any of the blocks within the aabb are liquids. Args: aabb
     */
    public boolean containsLiquid(AxisAlignedBB var1)
    {
        int var2 = MathHelper.floor(var1.a);
        int var3 = MathHelper.floor(var1.d + 1.0D);
        int var4 = MathHelper.floor(var1.b);
        int var5 = MathHelper.floor(var1.e + 1.0D);
        int var6 = MathHelper.floor(var1.c);
        int var7 = MathHelper.floor(var1.f + 1.0D);

        if (var1.a < 0.0D)
        {
            --var2;
        }

        if (var1.b < 0.0D)
        {
            --var4;
        }

        if (var1.c < 0.0D)
        {
            --var6;
        }

        for (int var8 = var2; var8 < var3; ++var8)
        {
            for (int var9 = var4; var9 < var5; ++var9)
            {
                for (int var10 = var6; var10 < var7; ++var10)
                {
                    Block var11 = Block.byId[this.getTypeId(var8, var9, var10)];

                    if (var11 != null && var11.material.isLiquid())
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns whether or not the given bounding box is on fire or not
     */
    public boolean e(AxisAlignedBB var1)
    {
        int var2 = MathHelper.floor(var1.a);
        int var3 = MathHelper.floor(var1.d + 1.0D);
        int var4 = MathHelper.floor(var1.b);
        int var5 = MathHelper.floor(var1.e + 1.0D);
        int var6 = MathHelper.floor(var1.c);
        int var7 = MathHelper.floor(var1.f + 1.0D);

        if (this.d(var2, var4, var6, var3, var5, var7))
        {
            for (int var8 = var2; var8 < var3; ++var8)
            {
                for (int var9 = var4; var9 < var5; ++var9)
                {
                    for (int var10 = var6; var10 < var7; ++var10)
                    {
                        int var11 = this.getTypeId(var8, var9, var10);

                        if (var11 == Block.FIRE.id || var11 == Block.LAVA.id || var11 == Block.STATIONARY_LAVA.id)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * handles the acceleration of an object whilst in water. Not sure if it is used elsewhere.
     */
    public boolean a(AxisAlignedBB var1, Material var2, Entity var3)
    {
        int var4 = MathHelper.floor(var1.a);
        int var5 = MathHelper.floor(var1.d + 1.0D);
        int var6 = MathHelper.floor(var1.b);
        int var7 = MathHelper.floor(var1.e + 1.0D);
        int var8 = MathHelper.floor(var1.c);
        int var9 = MathHelper.floor(var1.f + 1.0D);

        if (!this.d(var4, var6, var8, var5, var7, var9))
        {
            return false;
        }
        else
        {
            boolean var10 = false;
            Vec3D var11 = this.getVec3DPool().create(0.0D, 0.0D, 0.0D);

            for (int var12 = var4; var12 < var5; ++var12)
            {
                for (int var13 = var6; var13 < var7; ++var13)
                {
                    for (int var14 = var8; var14 < var9; ++var14)
                    {
                        Block var15 = Block.byId[this.getTypeId(var12, var13, var14)];

                        if (var15 != null && var15.material == var2)
                        {
                            double var16 = (double)((float)(var13 + 1) - BlockFluids.d(this.getData(var12, var13, var14)));

                            if ((double)var7 >= var16)
                            {
                                var10 = true;
                                var15.a(this, var12, var13, var14, var3, var11);
                            }
                        }
                    }
                }
            }

            if (var11.b() > 0.0D)
            {
                var11 = var11.a();
                double var18 = 0.014D;
                var3.motX += var11.c * var18;
                var3.motY += var11.d * var18;
                var3.motZ += var11.e * var18;
            }

            return var10;
        }
    }

    /**
     * Returns true if the given bounding box contains the given material
     */
    public boolean a(AxisAlignedBB var1, Material var2)
    {
        int var3 = MathHelper.floor(var1.a);
        int var4 = MathHelper.floor(var1.d + 1.0D);
        int var5 = MathHelper.floor(var1.b);
        int var6 = MathHelper.floor(var1.e + 1.0D);
        int var7 = MathHelper.floor(var1.c);
        int var8 = MathHelper.floor(var1.f + 1.0D);

        for (int var9 = var3; var9 < var4; ++var9)
        {
            for (int var10 = var5; var10 < var6; ++var10)
            {
                for (int var11 = var7; var11 < var8; ++var11)
                {
                    Block var12 = Block.byId[this.getTypeId(var9, var10, var11)];

                    if (var12 != null && var12.material == var2)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * checks if the given AABB is in the material given. Used while swimming.
     */
    public boolean b(AxisAlignedBB var1, Material var2)
    {
        int var3 = MathHelper.floor(var1.a);
        int var4 = MathHelper.floor(var1.d + 1.0D);
        int var5 = MathHelper.floor(var1.b);
        int var6 = MathHelper.floor(var1.e + 1.0D);
        int var7 = MathHelper.floor(var1.c);
        int var8 = MathHelper.floor(var1.f + 1.0D);

        for (int var9 = var3; var9 < var4; ++var9)
        {
            for (int var10 = var5; var10 < var6; ++var10)
            {
                for (int var11 = var7; var11 < var8; ++var11)
                {
                    Block var12 = Block.byId[this.getTypeId(var9, var10, var11)];

                    if (var12 != null && var12.material == var2)
                    {
                        int var13 = this.getData(var9, var10, var11);
                        double var14 = (double)(var10 + 1);

                        if (var13 < 8)
                        {
                            var14 = (double)(var10 + 1) - (double)var13 / 8.0D;
                        }

                        if (var14 >= var1.b)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Creates an explosion. Args: entity, x, y, z, strength
     */
    public Explosion explode(Entity var1, double var2, double var4, double var6, float var8, boolean var9)
    {
        return this.createExplosion(var1, var2, var4, var6, var8, false, var9);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion createExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10)
    {
        Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8);
        var11.a = var9;
        var11.b = var10;
        var11.a();
        var11.a(true);
        return var11;
    }

    /**
     * Gets the percentage of real blocks within within a bounding box, along a specified vector.
     */
    public float a(Vec3D var1, AxisAlignedBB var2)
    {
        double var3 = 1.0D / ((var2.d - var2.a) * 2.0D + 1.0D);
        double var5 = 1.0D / ((var2.e - var2.b) * 2.0D + 1.0D);
        double var7 = 1.0D / ((var2.f - var2.c) * 2.0D + 1.0D);
        int var9 = 0;
        int var10 = 0;

        for (float var11 = 0.0F; var11 <= 1.0F; var11 = (float)((double)var11 + var3))
        {
            for (float var12 = 0.0F; var12 <= 1.0F; var12 = (float)((double)var12 + var5))
            {
                for (float var13 = 0.0F; var13 <= 1.0F; var13 = (float)((double)var13 + var7))
                {
                    double var14 = var2.a + (var2.d - var2.a) * (double)var11;
                    double var16 = var2.b + (var2.e - var2.b) * (double)var12;
                    double var18 = var2.c + (var2.f - var2.c) * (double)var13;

                    if (this.a(this.getVec3DPool().create(var14, var16, var18), var1) == null)
                    {
                        ++var9;
                    }

                    ++var10;
                }
            }
        }

        return (float)var9 / (float)var10;
    }

    /**
     * If the block in the given direction of the given coordinate is fire, extinguish it. Args: Player, X,Y,Z,
     * blockDirection
     */
    public boolean douseFire(EntityHuman var1, int var2, int var3, int var4, int var5)
    {
        if (var5 == 0)
        {
            --var3;
        }

        if (var5 == 1)
        {
            ++var3;
        }

        if (var5 == 2)
        {
            --var4;
        }

        if (var5 == 3)
        {
            ++var4;
        }

        if (var5 == 4)
        {
            --var2;
        }

        if (var5 == 5)
        {
            ++var2;
        }

        if (this.getTypeId(var2, var3, var4) == Block.FIRE.id)
        {
            this.a(var1, 1004, var2, var3, var4, 0);
            this.setTypeId(var2, var3, var4, 0);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getTileEntity(int var1, int var2, int var3)
    {
        if (var2 >= 256)
        {
            return null;
        }
        else
        {
            Chunk var4 = this.getChunkAt(var1 >> 4, var3 >> 4);

            if (var4 == null)
            {
                return null;
            }
            else
            {
                TileEntity var5 = var4.e(var1 & 15, var2, var3 & 15);

                if (var5 == null)
                {
                    Iterator var6 = this.a.iterator();

                    while (var6.hasNext())
                    {
                        TileEntity var7 = (TileEntity)var6.next();

                        if (!var7.r() && var7.x == var1 && var7.y == var2 && var7.z == var3)
                        {
                            var5 = var7;
                            break;
                        }
                    }
                }

                return var5;
            }
        }
    }

    /**
     * Sets the TileEntity for a given block in X, Y, Z coordinates
     */
    public void setTileEntity(int var1, int var2, int var3, TileEntity var4)
    {
        if (var4 != null && !var4.r())
        {
            if (this.N)
            {
                var4.x = var1;
                var4.y = var2;
                var4.z = var3;
                this.a.add(var4);
            }
            else
            {
                this.tileEntityList.add(var4);
                Chunk var5 = this.getChunkAt(var1 >> 4, var3 >> 4);

                if (var5 != null)
                {
                    var5.a(var1 & 15, var2, var3 & 15, var4);
                }
            }
        }
    }

    /**
     * Removes the TileEntity for a given block in X,Y,Z coordinates
     */
    public void q(int var1, int var2, int var3)
    {
        TileEntity var4 = this.getTileEntity(var1, var2, var3);

        if (var4 != null && this.N)
        {
            var4.w_();
            this.a.remove(var4);
        }
        else
        {
            if (var4 != null)
            {
                this.a.remove(var4);
                this.tileEntityList.remove(var4);
            }

            Chunk var5 = this.getChunkAt(var1 >> 4, var3 >> 4);

            if (var5 != null)
            {
                var5.f(var1 & 15, var2, var3 & 15);
            }
        }
    }

    /**
     * Adds TileEntity to despawn list
     */
    public void a(TileEntity var1)
    {
        this.b.add(var1);
    }

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean r(int var1, int var2, int var3)
    {
        Block var4 = Block.byId[this.getTypeId(var1, var2, var3)];
        return var4 == null ? false : var4.c();
    }

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean s(int var1, int var2, int var3)
    {
        return Block.i(this.getTypeId(var1, var2, var3));
    }

    /**
     * Returns true if the block at the given coordinate has a solid (buildable) top surface.
     */
    public boolean t(int var1, int var2, int var3)
    {
        Block var4 = Block.byId[this.getTypeId(var1, var2, var3)];
        return var4 == null ? false : (var4.material.k() && var4.b() ? true : (var4 instanceof BlockStairs ? (this.getData(var1, var2, var3) & 4) == 4 : (var4 instanceof BlockStepAbstract ? (this.getData(var1, var2, var3) & 8) == 8 : false)));
    }

    /**
     * Checks if the block is a solid, normal cube. If the chunk does not exist, or is not loaded, it returns the
     * boolean parameter.
     */
    public boolean b(int var1, int var2, int var3, boolean var4)
    {
        if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000)
        {
            Chunk var5 = this.chunkProvider.getOrCreateChunk(var1 >> 4, var3 >> 4);

            if (var5 != null && !var5.isEmpty())
            {
                Block var6 = Block.byId[this.getTypeId(var1, var2, var3)];
                return var6 == null ? false : var6.material.k() && var6.b();
            }
            else
            {
                return var4;
            }
        }
        else
        {
            return var4;
        }
    }

    /**
     * Called on construction of the World class to setup the initial skylight values
     */
    public void w()
    {
        int var1 = this.a(1.0F);

        if (var1 != this.j)
        {
            this.j = var1;
        }
    }

    /**
     * first boolean for hostile mobs and second for peaceful mobs
     */
    public void setSpawnFlags(boolean var1, boolean var2)
    {
        this.allowMonsters = var1;
        this.allowAnimals = var2;
    }

    /**
     * Runs a single tick for the world
     */
    public void doTick()
    {
        this.n();
    }

    /**
     * Called from World constructor to set rainingStrength and thunderingStrength
     */
    private void a()
    {
        if (this.worldData.hasStorm())
        {
            this.n = 1.0F;

            if (this.worldData.isThundering())
            {
                this.p = 1.0F;
            }
        }
    }

    /**
     * Updates all weather states.
     */
    protected void n()
    {
        if (!this.worldProvider.f)
        {
            if (this.q > 0)
            {
                --this.q;
            }

            int var1 = this.worldData.getThunderDuration();

            if (var1 <= 0)
            {
                if (this.worldData.isThundering())
                {
                    this.worldData.setThunderDuration(this.random.nextInt(12000) + 3600);
                }
                else
                {
                    this.worldData.setThunderDuration(this.random.nextInt(168000) + 12000);
                }
            }
            else
            {
                --var1;
                this.worldData.setThunderDuration(var1);

                if (var1 <= 0)
                {
                    this.worldData.setThundering(!this.worldData.isThundering());
                }
            }

            int var2 = this.worldData.getWeatherDuration();

            if (var2 <= 0)
            {
                if (this.worldData.hasStorm())
                {
                    this.worldData.setWeatherDuration(this.random.nextInt(12000) + 12000);
                }
                else
                {
                    this.worldData.setWeatherDuration(this.random.nextInt(168000) + 12000);
                }
            }
            else
            {
                --var2;
                this.worldData.setWeatherDuration(var2);

                if (var2 <= 0)
                {
                    this.worldData.setStorm(!this.worldData.hasStorm());
                }
            }

            this.m = this.n;

            if (this.worldData.hasStorm())
            {
                this.n = (float)((double)this.n + 0.01D);
            }
            else
            {
                this.n = (float)((double)this.n - 0.01D);
            }

            if (this.n < 0.0F)
            {
                this.n = 0.0F;
            }

            if (this.n > 1.0F)
            {
                this.n = 1.0F;
            }

            this.o = this.p;

            if (this.worldData.isThundering())
            {
                this.p = (float)((double)this.p + 0.01D);
            }
            else
            {
                this.p = (float)((double)this.p - 0.01D);
            }

            if (this.p < 0.0F)
            {
                this.p = 0.0F;
            }

            if (this.p > 1.0F)
            {
                this.p = 1.0F;
            }
        }
    }

    /**
     * start precipitation in this world (2 ticks after command posted)
     */
    public void x()
    {
        this.worldData.setWeatherDuration(1);
    }

    protected void y()
    {
        this.chunkTickList.clear();
        this.methodProfiler.a("buildList");
        int var1;
        EntityHuman var2;
        int var3;
        int var4;

        for (var1 = 0; var1 < this.players.size(); ++var1)
        {
            var2 = (EntityHuman)this.players.get(var1);
            var3 = MathHelper.floor(var2.locX / 16.0D);
            var4 = MathHelper.floor(var2.locZ / 16.0D);
            byte var5 = 7;

            for (int var6 = -var5; var6 <= var5; ++var6)
            {
                for (int var7 = -var5; var7 <= var5; ++var7)
                {
                    this.chunkTickList.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
                }
            }
        }

        this.methodProfiler.b();

        if (this.O > 0)
        {
            --this.O;
        }

        this.methodProfiler.a("playerCheckLight");

        if (!this.players.isEmpty())
        {
            var1 = this.random.nextInt(this.players.size());
            var2 = (EntityHuman)this.players.get(var1);
            var3 = MathHelper.floor(var2.locX) + this.random.nextInt(11) - 5;
            var4 = MathHelper.floor(var2.locY) + this.random.nextInt(11) - 5;
            int var8 = MathHelper.floor(var2.locZ) + this.random.nextInt(11) - 5;
            this.x(var3, var4, var8);
        }

        this.methodProfiler.b();
    }

    protected void a(int var1, int var2, Chunk var3)
    {
        this.methodProfiler.c("moodSound");

        if (this.O == 0 && !this.isStatic)
        {
            this.k = this.k * 3 + 1013904223;
            int var4 = this.k >> 2;
            int var5 = var4 & 15;
            int var6 = var4 >> 8 & 15;
            int var7 = var4 >> 16 & 127;
            int var8 = var3.getTypeId(var5, var7, var6);
            var5 += var1;
            var6 += var2;

            if (var8 == 0 && this.k(var5, var7, var6) <= this.random.nextInt(8) && this.b(EnumSkyBlock.Sky, var5, var7, var6) <= 0)
            {
                EntityHuman var9 = this.findNearbyPlayer((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, 8.0D);

                if (var9 != null && var9.e((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D) > 4.0D)
                {
                    this.makeSound((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
                    this.O = this.random.nextInt(12000) + 6000;
                }
            }
        }

        this.methodProfiler.c("checkLight");
        var3.o();
    }

    /**
     * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
     * player
     */
    protected void g()
    {
        this.y();
    }

    /**
     * checks to see if a given block is both water and is cold enough to freeze
     */
    public boolean u(int var1, int var2, int var3)
    {
        return this.c(var1, var2, var3, false);
    }

    /**
     * checks to see if a given block is both water and has at least one immediately adjacent non-water block
     */
    public boolean v(int var1, int var2, int var3)
    {
        return this.c(var1, var2, var3, true);
    }

    /**
     * checks to see if a given block is both water, and cold enough to freeze - if the par4 boolean is set, this will
     * only return true if there is a non-water block immediately adjacent to the specified block
     */
    public boolean c(int var1, int var2, int var3, boolean var4)
    {
        BiomeBase var5 = this.getBiome(var1, var3);
        float var6 = var5.j();

        if (var6 > 0.15F)
        {
            return false;
        }
        else
        {
            if (var2 >= 0 && var2 < 256 && this.b(EnumSkyBlock.Block, var1, var2, var3) < 10)
            {
                int var7 = this.getTypeId(var1, var2, var3);

                if ((var7 == Block.STATIONARY_WATER.id || var7 == Block.WATER.id) && this.getData(var1, var2, var3) == 0)
                {
                    if (!var4)
                    {
                        return true;
                    }

                    boolean var8 = true;

                    if (var8 && this.getMaterial(var1 - 1, var2, var3) != Material.WATER)
                    {
                        var8 = false;
                    }

                    if (var8 && this.getMaterial(var1 + 1, var2, var3) != Material.WATER)
                    {
                        var8 = false;
                    }

                    if (var8 && this.getMaterial(var1, var2, var3 - 1) != Material.WATER)
                    {
                        var8 = false;
                    }

                    if (var8 && this.getMaterial(var1, var2, var3 + 1) != Material.WATER)
                    {
                        var8 = false;
                    }

                    if (!var8)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * Tests whether or not snow can be placed at a given location
     */
    public boolean w(int var1, int var2, int var3)
    {
        BiomeBase var4 = this.getBiome(var1, var3);
        float var5 = var4.j();

        if (var5 > 0.15F)
        {
            return false;
        }
        else
        {
            if (var2 >= 0 && var2 < 256 && this.b(EnumSkyBlock.Block, var1, var2, var3) < 10)
            {
                int var6 = this.getTypeId(var1, var2 - 1, var3);
                int var7 = this.getTypeId(var1, var2, var3);

                if (var7 == 0 && Block.SNOW.canPlace(this, var1, var2, var3) && var6 != 0 && var6 != Block.ICE.id && Block.byId[var6].material.isSolid())
                {
                    return true;
                }
            }

            return false;
        }
    }

    public void x(int var1, int var2, int var3)
    {
        if (!this.worldProvider.f)
        {
            this.c(EnumSkyBlock.Sky, var1, var2, var3);
        }

        this.c(EnumSkyBlock.Block, var1, var2, var3);
    }

    private int b(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        int var7 = 0;

        if (this.j(var2, var3, var4))
        {
            var7 = 15;
        }
        else
        {
            if (var6 == 0)
            {
                var6 = 1;
            }

            int var8 = this.b(EnumSkyBlock.Sky, var2 - 1, var3, var4) - var6;
            int var9 = this.b(EnumSkyBlock.Sky, var2 + 1, var3, var4) - var6;
            int var10 = this.b(EnumSkyBlock.Sky, var2, var3 - 1, var4) - var6;
            int var11 = this.b(EnumSkyBlock.Sky, var2, var3 + 1, var4) - var6;
            int var12 = this.b(EnumSkyBlock.Sky, var2, var3, var4 - 1) - var6;
            int var13 = this.b(EnumSkyBlock.Sky, var2, var3, var4 + 1) - var6;

            if (var8 > var7)
            {
                var7 = var8;
            }

            if (var9 > var7)
            {
                var7 = var9;
            }

            if (var10 > var7)
            {
                var7 = var10;
            }

            if (var11 > var7)
            {
                var7 = var11;
            }

            if (var12 > var7)
            {
                var7 = var12;
            }

            if (var13 > var7)
            {
                var7 = var13;
            }
        }

        return var7;
    }

    private int g(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        int var7 = Block.lightEmission[var5];
        int var8 = this.b(EnumSkyBlock.Block, var2 - 1, var3, var4) - var6;
        int var9 = this.b(EnumSkyBlock.Block, var2 + 1, var3, var4) - var6;
        int var10 = this.b(EnumSkyBlock.Block, var2, var3 - 1, var4) - var6;
        int var11 = this.b(EnumSkyBlock.Block, var2, var3 + 1, var4) - var6;
        int var12 = this.b(EnumSkyBlock.Block, var2, var3, var4 - 1) - var6;
        int var13 = this.b(EnumSkyBlock.Block, var2, var3, var4 + 1) - var6;

        if (var8 > var7)
        {
            var7 = var8;
        }

        if (var9 > var7)
        {
            var7 = var9;
        }

        if (var10 > var7)
        {
            var7 = var10;
        }

        if (var11 > var7)
        {
            var7 = var11;
        }

        if (var12 > var7)
        {
            var7 = var12;
        }

        if (var13 > var7)
        {
            var7 = var13;
        }

        return var7;
    }

    public void c(EnumSkyBlock var1, int var2, int var3, int var4)
    {
        if (this.areChunksLoaded(var2, var3, var4, 17))
        {
            int var5 = 0;
            int var6 = 0;
            this.methodProfiler.a("getBrightness");
            int var7 = this.b(var1, var2, var3, var4);
            boolean var8 = false;
            int var9 = this.getTypeId(var2, var3, var4);
            int var10 = this.b(var2, var3, var4);

            if (var10 == 0)
            {
                var10 = 1;
            }

            boolean var11 = false;
            int var24;

            if (var1 == EnumSkyBlock.Sky)
            {
                var24 = this.b(var7, var2, var3, var4, var9, var10);
            }
            else
            {
                var24 = this.g(var7, var2, var3, var4, var9, var10);
            }

            int var12;
            int var13;
            int var14;
            int var15;
            int var17;
            int var16;
            int var19;
            int var18;

            if (var24 > var7)
            {
                this.I[var6++] = 133152;
            }
            else if (var24 < var7)
            {
                if (var1 != EnumSkyBlock.Block)
                {
                    ;
                }

                this.I[var6++] = 133152 + (var7 << 18);

                while (var5 < var6)
                {
                    var9 = this.I[var5++];
                    var10 = (var9 & 63) - 32 + var2;
                    var24 = (var9 >> 6 & 63) - 32 + var3;
                    var12 = (var9 >> 12 & 63) - 32 + var4;
                    var13 = var9 >> 18 & 15;
                    var14 = this.b(var1, var10, var24, var12);

                    if (var14 == var13)
                    {
                        this.b(var1, var10, var24, var12, 0);

                        if (var13 > 0)
                        {
                            var15 = var10 - var2;
                            var16 = var24 - var3;
                            var17 = var12 - var4;

                            if (var15 < 0)
                            {
                                var15 = -var15;
                            }

                            if (var16 < 0)
                            {
                                var16 = -var16;
                            }

                            if (var17 < 0)
                            {
                                var17 = -var17;
                            }

                            if (var15 + var16 + var17 < 17)
                            {
                                for (var18 = 0; var18 < 6; ++var18)
                                {
                                    var19 = var18 % 2 * 2 - 1;
                                    int var20 = var10 + var18 / 2 % 3 / 2 * var19;
                                    int var21 = var24 + (var18 / 2 + 1) % 3 / 2 * var19;
                                    int var22 = var12 + (var18 / 2 + 2) % 3 / 2 * var19;
                                    var14 = this.b(var1, var20, var21, var22);
                                    int var23 = Block.lightBlock[this.getTypeId(var20, var21, var22)];

                                    if (var23 == 0)
                                    {
                                        var23 = 1;
                                    }

                                    if (var14 == var13 - var23 && var6 < this.I.length)
                                    {
                                        this.I[var6++] = var20 - var2 + 32 + (var21 - var3 + 32 << 6) + (var22 - var4 + 32 << 12) + (var13 - var23 << 18);
                                    }
                                }
                            }
                        }
                    }
                }

                var5 = 0;
            }

            this.methodProfiler.b();
            this.methodProfiler.a("checkedPosition < toCheckCount");

            while (var5 < var6)
            {
                var9 = this.I[var5++];
                var10 = (var9 & 63) - 32 + var2;
                var24 = (var9 >> 6 & 63) - 32 + var3;
                var12 = (var9 >> 12 & 63) - 32 + var4;
                var13 = this.b(var1, var10, var24, var12);
                var14 = this.getTypeId(var10, var24, var12);
                var15 = Block.lightBlock[var14];

                if (var15 == 0)
                {
                    var15 = 1;
                }

                boolean var25 = false;

                if (var1 == EnumSkyBlock.Sky)
                {
                    var16 = this.b(var13, var10, var24, var12, var14, var15);
                }
                else
                {
                    var16 = this.g(var13, var10, var24, var12, var14, var15);
                }

                if (var16 != var13)
                {
                    this.b(var1, var10, var24, var12, var16);

                    if (var16 > var13)
                    {
                        var17 = var10 - var2;
                        var18 = var24 - var3;
                        var19 = var12 - var4;

                        if (var17 < 0)
                        {
                            var17 = -var17;
                        }

                        if (var18 < 0)
                        {
                            var18 = -var18;
                        }

                        if (var19 < 0)
                        {
                            var19 = -var19;
                        }

                        if (var17 + var18 + var19 < 17 && var6 < this.I.length - 6)
                        {
                            if (this.b(var1, var10 - 1, var24, var12) < var16)
                            {
                                this.I[var6++] = var10 - 1 - var2 + 32 + (var24 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
                            }

                            if (this.b(var1, var10 + 1, var24, var12) < var16)
                            {
                                this.I[var6++] = var10 + 1 - var2 + 32 + (var24 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
                            }

                            if (this.b(var1, var10, var24 - 1, var12) < var16)
                            {
                                this.I[var6++] = var10 - var2 + 32 + (var24 - 1 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
                            }

                            if (this.b(var1, var10, var24 + 1, var12) < var16)
                            {
                                this.I[var6++] = var10 - var2 + 32 + (var24 + 1 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
                            }

                            if (this.b(var1, var10, var24, var12 - 1) < var16)
                            {
                                this.I[var6++] = var10 - var2 + 32 + (var24 - var3 + 32 << 6) + (var12 - 1 - var4 + 32 << 12);
                            }

                            if (this.b(var1, var10, var24, var12 + 1) < var16)
                            {
                                this.I[var6++] = var10 - var2 + 32 + (var24 - var3 + 32 << 6) + (var12 + 1 - var4 + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.methodProfiler.b();
        }
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean a(boolean var1)
    {
        return false;
    }

    public List a(Chunk var1, boolean var2)
    {
        return null;
    }

    /**
     * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
     */
    public List getEntities(Entity var1, AxisAlignedBB var2)
    {
        this.P.clear();
        int var3 = MathHelper.floor((var2.a - 2.0D) / 16.0D);
        int var4 = MathHelper.floor((var2.d + 2.0D) / 16.0D);
        int var5 = MathHelper.floor((var2.c - 2.0D) / 16.0D);
        int var6 = MathHelper.floor((var2.f + 2.0D) / 16.0D);

        for (int var7 = var3; var7 <= var4; ++var7)
        {
            for (int var8 = var5; var8 <= var6; ++var8)
            {
                if (this.isChunkLoaded(var7, var8))
                {
                    this.getChunkAt(var7, var8).a(var1, var2, this.P);
                }
            }
        }

        return this.P;
    }

    /**
     * Returns all entities of the specified class type which intersect with the AABB. Args: entityClass, aabb
     */
    public List a(Class var1, AxisAlignedBB var2)
    {
        return this.func_82733_a(var1, var2, (IEntitySelector)null);
    }

    public List func_82733_a(Class var1, AxisAlignedBB var2, IEntitySelector var3)
    {
        int var4 = MathHelper.floor((var2.a - 2.0D) / 16.0D);
        int var5 = MathHelper.floor((var2.d + 2.0D) / 16.0D);
        int var6 = MathHelper.floor((var2.c - 2.0D) / 16.0D);
        int var7 = MathHelper.floor((var2.f + 2.0D) / 16.0D);
        ArrayList var8 = new ArrayList();

        for (int var9 = var4; var9 <= var5; ++var9)
        {
            for (int var10 = var6; var10 <= var7; ++var10)
            {
                if (this.isChunkLoaded(var9, var10))
                {
                    this.getChunkAt(var9, var10).a(var1, var2, var8, var3);
                }
            }
        }

        return var8;
    }

    public Entity a(Class var1, AxisAlignedBB var2, Entity var3)
    {
        List var4 = this.a(var1, var2);
        Entity var5 = null;
        double var6 = Double.MAX_VALUE;
        Iterator var8 = var4.iterator();

        while (var8.hasNext())
        {
            Entity var9 = (Entity)var8.next();

            if (var9 != var3)
            {
                double var10 = var3.e(var9);

                if (var10 <= var6)
                {
                    var5 = var9;
                    var6 = var10;
                }
            }
        }

        return var5;
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public abstract Entity getEntity(int var1);

    /**
     * marks the chunk that contains this tilentity as modified and then calls worldAccesses.doNothingWithTileEntity
     */
    public void b(int var1, int var2, int var3, TileEntity var4)
    {
        if (this.isLoaded(var1, var2, var3))
        {
            this.getChunkAtWorldCoords(var1, var3).e();
        }
    }

    /**
     * Counts how many entities of an entity class exist in the world. Args: entityClass
     */
    public int a(Class var1)
    {
        int var2 = 0;

        for (int var3 = 0; var3 < this.entityList.size(); ++var3)
        {
            Entity var4 = (Entity)this.entityList.get(var3);

            if (var1.isAssignableFrom(var4.getClass()))
            {
                ++var2;
            }
        }

        return var2;
    }

    /**
     * adds entities to the loaded entities list, and loads thier skins.
     */
    public void a(List var1)
    {
        this.entityList.addAll(var1);

        for (int var2 = 0; var2 < var1.size(); ++var2)
        {
            this.a((Entity)var1.get(var2));
        }
    }

    /**
     * adds entities to the list of unloaded entities
     */
    public void b(List var1)
    {
        this.f.addAll(var1);
    }

    /**
     * Returns true if the given Entity can be placed on the given side of the given block position.
     */
    public boolean mayPlace(int var1, int var2, int var3, int var4, boolean var5, int var6, Entity var7)
    {
        int var8 = this.getTypeId(var2, var3, var4);
        Block var9 = Block.byId[var8];
        Block var10 = Block.byId[var1];
        AxisAlignedBB var11 = var10.e(this, var2, var3, var4);

        if (var5)
        {
            var11 = null;
        }

        if (var11 != null && !this.a(var11, var7))
        {
            return false;
        }
        else
        {
            if (var9 != null && (var9 == Block.WATER || var9 == Block.STATIONARY_WATER || var9 == Block.LAVA || var9 == Block.STATIONARY_LAVA || var9 == Block.FIRE || var9.material.isReplaceable()))
            {
                var9 = null;
            }

            return var9 != null && var9.material == Material.ORIENTABLE && var10 == Block.ANVIL ? true : var1 > 0 && var9 == null && var10.canPlace(this, var2, var3, var4, var6);
        }
    }

    public PathEntity findPath(Entity var1, Entity var2, float var3, boolean var4, boolean var5, boolean var6, boolean var7)
    {
        this.methodProfiler.a("pathfind");
        int var8 = MathHelper.floor(var1.locX);
        int var9 = MathHelper.floor(var1.locY + 1.0D);
        int var10 = MathHelper.floor(var1.locZ);
        int var11 = (int)(var3 + 16.0F);
        int var12 = var8 - var11;
        int var13 = var9 - var11;
        int var14 = var10 - var11;
        int var15 = var8 + var11;
        int var16 = var9 + var11;
        int var17 = var10 + var11;
        ChunkCache var18 = new ChunkCache(this, var12, var13, var14, var15, var16, var17);
        PathEntity var19 = (new Pathfinder(var18, var4, var5, var6, var7)).a(var1, var2, var3);
        this.methodProfiler.b();
        return var19;
    }

    public PathEntity a(Entity var1, int var2, int var3, int var4, float var5, boolean var6, boolean var7, boolean var8, boolean var9)
    {
        this.methodProfiler.a("pathfind");
        int var10 = MathHelper.floor(var1.locX);
        int var11 = MathHelper.floor(var1.locY);
        int var12 = MathHelper.floor(var1.locZ);
        int var13 = (int)(var5 + 8.0F);
        int var14 = var10 - var13;
        int var15 = var11 - var13;
        int var16 = var12 - var13;
        int var17 = var10 + var13;
        int var18 = var11 + var13;
        int var19 = var12 + var13;
        ChunkCache var20 = new ChunkCache(this, var14, var15, var16, var17, var18, var19);
        PathEntity var21 = (new Pathfinder(var20, var6, var7, var8, var9)).a(var1, var2, var3, var4, var5);
        this.methodProfiler.b();
        return var21;
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    public boolean isBlockFacePowered(int var1, int var2, int var3, int var4)
    {
        int var5 = this.getTypeId(var1, var2, var3);
        return var5 == 0 ? false : Block.byId[var5].c(this, var1, var2, var3, var4);
    }

    /**
     * Whether one of the neighboring blocks is putting power into this block. Args: x, y, z
     */
    public boolean isBlockPowered(int var1, int var2, int var3)
    {
        return this.isBlockFacePowered(var1, var2 - 1, var3, 0) ? true : (this.isBlockFacePowered(var1, var2 + 1, var3, 1) ? true : (this.isBlockFacePowered(var1, var2, var3 - 1, 2) ? true : (this.isBlockFacePowered(var1, var2, var3 + 1, 3) ? true : (this.isBlockFacePowered(var1 - 1, var2, var3, 4) ? true : this.isBlockFacePowered(var1 + 1, var2, var3, 5)))));
    }

    /**
     * Is a block next to you getting powered (if its an attachable block) or is it providing power directly to you.
     * Args: x, y, z, direction
     */
    public boolean isBlockFaceIndirectlyPowered(int var1, int var2, int var3, int var4)
    {
        if (this.s(var1, var2, var3))
        {
            return this.isBlockPowered(var1, var2, var3);
        }
        else
        {
            int var5 = this.getTypeId(var1, var2, var3);
            return var5 == 0 ? false : Block.byId[var5].b(this, var1, var2, var3, var4);
        }
    }

    /**
     * Used to see if one of the blocks next to you or your block is getting power from a neighboring block. Used by
     * items like TNT or Doors so they don't have redstone going straight into them.  Args: x, y, z
     */
    public boolean isBlockIndirectlyPowered(int var1, int var2, int var3)
    {
        return this.isBlockFaceIndirectlyPowered(var1, var2 - 1, var3, 0) ? true : (this.isBlockFaceIndirectlyPowered(var1, var2 + 1, var3, 1) ? true : (this.isBlockFaceIndirectlyPowered(var1, var2, var3 - 1, 2) ? true : (this.isBlockFaceIndirectlyPowered(var1, var2, var3 + 1, 3) ? true : (this.isBlockFaceIndirectlyPowered(var1 - 1, var2, var3, 4) ? true : this.isBlockFaceIndirectlyPowered(var1 + 1, var2, var3, 5)))));
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     * Args: entity, dist
     */
    public EntityHuman findNearbyPlayer(Entity var1, double var2)
    {
        return this.findNearbyPlayer(var1.locX, var1.locY, var1.locZ, var2);
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not
     * limit the distance). Args: x, y, z, dist
     */
    public EntityHuman findNearbyPlayer(double var1, double var3, double var5, double var7)
    {
        double var9 = -1.0D;
        EntityHuman var11 = null;

        for (int var12 = 0; var12 < this.players.size(); ++var12)
        {
            EntityHuman var13 = (EntityHuman)this.players.get(var12);
            double var14 = var13.e(var1, var3, var5);

            if ((var7 < 0.0D || var14 < var7 * var7) && (var9 == -1.0D || var14 < var9))
            {
                var9 = var14;
                var11 = var13;
            }
        }

        return var11;
    }

    /**
     * Returns the closest vulnerable player to this entity within the given radius, or null if none is found
     */
    public EntityHuman findNearbyVulnerablePlayer(Entity var1, double var2)
    {
        return this.findNearbyVulnerablePlayer(var1.locX, var1.locY, var1.locZ, var2);
    }

    /**
     * Returns the closest vulnerable player within the given radius, or null if none is found.
     */
    public EntityHuman findNearbyVulnerablePlayer(double var1, double var3, double var5, double var7)
    {
        double var9 = -1.0D;
        EntityHuman var11 = null;

        for (int var12 = 0; var12 < this.players.size(); ++var12)
        {
            EntityHuman var13 = (EntityHuman)this.players.get(var12);

            if (!var13.abilities.isInvulnerable)
            {
                double var14 = var13.e(var1, var3, var5);
                double var16 = var7;

                if (var13.isSneaking())
                {
                    var16 = var7 * 0.800000011920929D;
                }

                if (var13.func_82150_aj())
                {
                    float var18 = var13.func_82243_bO();

                    if (var18 < 0.1F)
                    {
                        var18 = 0.1F;
                    }

                    var16 *= (double)(0.7F * var18);
                }

                if ((var7 < 0.0D || var14 < var16 * var16) && (var9 == -1.0D || var14 < var9))
                {
                    var9 = var14;
                    var11 = var13;
                }
            }
        }

        return var11;
    }

    /**
     * Find a player by name in this world.
     */
    public EntityHuman a(String var1)
    {
        for (int var2 = 0; var2 < this.players.size(); ++var2)
        {
            if (var1.equals(((EntityHuman)this.players.get(var2)).name))
            {
                return (EntityHuman)this.players.get(var2);
            }
        }

        return null;
    }

    /**
     * Checks whether the session lock file was modified by another process
     */
    public void C()
    {
        this.dataManager.checkSession();
    }

    /**
     * gets the random world seed
     */
    public long getSeed()
    {
        return this.worldData.getSeed();
    }

    public long getTime()
    {
        return this.worldData.getTime();
    }

    public long F()
    {
        return this.worldData.g();
    }

    /**
     * Sets the world time.
     */
    public void setTime(long var1)
    {
        this.worldData.c(var1);
    }

    /**
     * Returns the coordinates of the spawn point
     */
    public ChunkCoordinates getSpawn()
    {
        return new ChunkCoordinates(this.worldData.c(), this.worldData.d(), this.worldData.e());
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean a(EntityHuman var1, int var2, int var3, int var4)
    {
        return true;
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void broadcastEntityEffect(Entity var1, byte var2) {}

    /**
     * gets the world's chunk provider
     */
    public IChunkProvider H()
    {
        return this.chunkProvider;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    public void playNote(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        if (var4 > 0)
        {
            Block.byId[var4].b(this, var1, var2, var3, var5, var6);
        }
    }

    /**
     * Returns this world's current save handler
     */
    public IDataManager getDataManager()
    {
        return this.dataManager;
    }

    /**
     * Returns the world's WorldInfo object
     */
    public WorldData getWorldData()
    {
        return this.worldData;
    }

    /**
     * Gets the GameRules Class Instance.
     */
    public GameRules getGameRules()
    {
        return this.worldData.getGameRules();
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void everyoneSleeping() {}

    public float i(float var1)
    {
        return (this.o + (this.p - this.o) * var1) * this.j(var1);
    }

    /**
     * Not sure about this actually. Reverting this one myself.
     */
    public float j(float var1)
    {
        return this.m + (this.n - this.m) * var1;
    }

    /**
     * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    public boolean L()
    {
        return (double)this.i(1.0F) > 0.9D;
    }

    /**
     * Returns true if the current rain strength is greater than 0.2
     */
    public boolean M()
    {
        return (double)this.j(1.0F) > 0.2D;
    }

    public boolean B(int var1, int var2, int var3)
    {
        if (!this.M())
        {
            return false;
        }
        else if (!this.j(var1, var2, var3))
        {
            return false;
        }
        else if (this.h(var1, var3) > var2)
        {
            return false;
        }
        else
        {
            BiomeBase var4 = this.getBiome(var1, var3);
            return var4.c() ? false : var4.d();
        }
    }

    /**
     * Checks to see if the biome rainfall values for a given x,y,z coordinate set are extremely high
     */
    public boolean C(int var1, int var2, int var3)
    {
        BiomeBase var4 = this.getBiome(var1, var3);
        return var4.e();
    }

    /**
     * Assigns the given String id to the given MapDataBase using the MapStorage, removing any existing ones of the same
     * id.
     */
    public void a(String var1, WorldMapBase var2)
    {
        this.worldMaps.a(var1, var2);
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk using the MapStorage, instantiating
     * the given Class, or returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldMapBase a(Class var1, String var2)
    {
        return this.worldMaps.get(var1, var2);
    }

    /**
     * Returns an unique new data id from the MapStorage for the given prefix and saves the idCounts map to the
     * 'idcounts' file.
     */
    public int b(String var1)
    {
        return this.worldMaps.a(var1);
    }

    public void func_82739_e(int var1, int var2, int var3, int var4, int var5)
    {
        for (int var6 = 0; var6 < this.w.size(); ++var6)
        {
            ((IWorldAccess)this.w.get(var6)).func_82746_a(var1, var2, var3, var4, var5);
        }
    }

    /**
     * Plays a sound or particle effect. Parameters: Effect ID, X, Y, Z, Data. For a list of ids and data, see
     * http://wiki.vg/Protocol#Effects
     */
    public void triggerEffect(int var1, int var2, int var3, int var4, int var5)
    {
        this.a((EntityHuman)null, var1, var2, var3, var4, var5);
    }

    /**
     * See description for playAuxSFX.
     */
    public void a(EntityHuman var1, int var2, int var3, int var4, int var5, int var6)
    {
        for (int var7 = 0; var7 < this.w.size(); ++var7)
        {
            ((IWorldAccess)this.w.get(var7)).a(var1, var2, var3, var4, var5, var6);
        }
    }

    /**
     * Returns maximum world height.
     */
    public int getHeight()
    {
        return 256;
    }

    /**
     * Returns current world height.
     */
    public int O()
    {
        return this.worldProvider.f ? 128 : 256;
    }

    public IUpdatePlayerListBox func_82735_a(EntityMinecart var1)
    {
        return null;
    }

    /**
     * puts the World Random seed to a specific state dependant on the inputs
     */
    public Random D(int var1, int var2, int var3)
    {
        long var4 = (long)var1 * 341873128712L + (long)var2 * 132897987541L + this.getWorldData().getSeed() + (long)var3;
        this.random.setSeed(var4);
        return this.random;
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition b(String var1, int var2, int var3, int var4)
    {
        return this.H().findNearestMapFeature(this, var1, var2, var3, var4);
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReport a(CrashReport var1)
    {
        var1.a("World " + this.worldData.getName() + " Entities", new CrashReportEntities(this));
        var1.a("World " + this.worldData.getName() + " Players", new CrashReportPlayerCount(this));
        var1.a("World " + this.worldData.getName() + " Chunk Stats", new CrashReportChunkStats(this));
        return var1;
    }

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    public void g(int var1, int var2, int var3, int var4, int var5)
    {
        Iterator var6 = this.w.iterator();

        while (var6.hasNext())
        {
            IWorldAccess var7 = (IWorldAccess)var6.next();
            var7.b(var1, var2, var3, var4, var5);
        }
    }

    /**
     * Return the Vec3Pool object for this world.
     */
    public Vec3DPool getVec3DPool()
    {
        return this.field_82741_K;
    }

    /**
     * returns a calendar object containing the current date
     */
    public Calendar S()
    {
        this.L.setTimeInMillis(System.currentTimeMillis());
        return this.L;
    }
}
