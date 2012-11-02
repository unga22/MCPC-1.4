package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class WorldServer extends World
{
    private final MinecraftServer server;
    private final EntityTracker tracker;
    private final PlayerManager manager;
    private Set field_73064_N;

    /** All work to do in future ticks. */
    private TreeSet N;
    public ChunkProviderServer chunkProviderServer;

    /** Whether or not level saving is enabled */
    public boolean savingDisabled;

    /** is false if there are no players */
    private boolean O;
    private int field_80004_Q = 0;

    /**
     * Double buffer of ServerBlockEventList[] for holding pending BlockEventData's
     */
    private NoteDataList[] Q = new NoteDataList[] {new NoteDataList((EmptyClass)null), new NoteDataList((EmptyClass)null)};

    /**
     * The index into the blockEventCache; either 0, or 1, toggled in sendBlockEventPackets  where all BlockEvent are
     * applied locally and send to clients.
     */
    private int R = 0;
    private static final StructurePieceTreasure[] S = new StructurePieceTreasure[] {new StructurePieceTreasure(Item.STICK.id, 0, 1, 3, 10), new StructurePieceTreasure(Block.WOOD.id, 0, 1, 3, 10), new StructurePieceTreasure(Block.LOG.id, 0, 1, 3, 10), new StructurePieceTreasure(Item.STONE_AXE.id, 0, 1, 1, 3), new StructurePieceTreasure(Item.WOOD_AXE.id, 0, 1, 1, 5), new StructurePieceTreasure(Item.STONE_PICKAXE.id, 0, 1, 1, 3), new StructurePieceTreasure(Item.WOOD_PICKAXE.id, 0, 1, 1, 5), new StructurePieceTreasure(Item.APPLE.id, 0, 2, 3, 5), new StructurePieceTreasure(Item.BREAD.id, 0, 2, 3, 3)};

    /** An IntHashMap of entity IDs (integers) to their Entity objects. */
    private IntHashMap entitiesById;

    public WorldServer(MinecraftServer var1, IDataManager var2, String var3, int var4, WorldSettings var5, MethodProfiler var6)
    {
        super(var2, var3, var5, WorldProvider.byDimension(var4), var6);
        this.server = var1;
        this.tracker = new EntityTracker(this);
        this.manager = new PlayerManager(this, var1.getServerConfigurationManager().o());

        if (this.entitiesById == null)
        {
            this.entitiesById = new IntHashMap();
        }

        if (this.field_73064_N == null)
        {
            this.field_73064_N = new HashSet();
        }

        if (this.N == null)
        {
            this.N = new TreeSet();
        }
    }

    /**
     * Runs a single tick for the world
     */
    public void doTick()
    {
        super.doTick();

        if (this.getWorldData().isHardcore() && this.difficulty < 3)
        {
            this.difficulty = 3;
        }

        this.worldProvider.d.b();

        if (this.everyoneDeeplySleeping())
        {
            boolean var1 = false;

            if (this.allowMonsters && this.difficulty >= 1)
            {
                ;
            }

            if (!var1)
            {
                long var2 = this.worldData.g() + 24000L;
                this.worldData.c(var2 - var2 % 24000L);
                this.d();
            }
        }

        this.methodProfiler.a("mobSpawner");

        if (this.getGameRules().getBoolean("doMobSpawning"))
        {
            SpawnerCreature.spawnEntities(this, this.allowMonsters, this.allowAnimals, this.worldData.getTime() % 400L == 0L);
        }

        this.methodProfiler.c("chunkSource");
        this.chunkProvider.unloadChunks();
        int var4 = this.a(1.0F);

        if (var4 != this.j)
        {
            this.j = var4;
        }

        this.U();
        this.worldData.func_82572_b(this.worldData.getTime() + 1L);
        this.worldData.c(this.worldData.g() + 1L);
        this.methodProfiler.c("tickPending");
        this.a(false);
        this.methodProfiler.c("tickTiles");
        this.g();
        this.methodProfiler.c("chunkMap");
        this.manager.flush();
        this.methodProfiler.c("village");
        this.villages.tick();
        this.siegeManager.a();
        this.methodProfiler.b();
        this.U();
    }

    /**
     * only spawns creatures allowed by the chunkProvider
     */
    public BiomeMeta a(EnumCreatureType var1, int var2, int var3, int var4)
    {
        List var5 = this.H().getMobsFor(var1, var2, var3, var4);
        return var5 != null && !var5.isEmpty() ? (BiomeMeta)WeightedRandom.a(this.random, var5) : null;
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void everyoneSleeping()
    {
        this.O = !this.players.isEmpty();
        Iterator var1 = this.players.iterator();

        while (var1.hasNext())
        {
            EntityHuman var2 = (EntityHuman)var1.next();

            if (!var2.isSleeping())
            {
                this.O = false;
                break;
            }
        }
    }

    protected void d()
    {
        this.O = false;
        Iterator var1 = this.players.iterator();

        while (var1.hasNext())
        {
            EntityHuman var2 = (EntityHuman)var1.next();

            if (var2.isSleeping())
            {
                var2.a(false, false, true);
            }
        }

        this.T();
    }

    private void T()
    {
        this.worldData.setWeatherDuration(0);
        this.worldData.setStorm(false);
        this.worldData.setThunderDuration(0);
        this.worldData.setThundering(false);
    }

    public boolean everyoneDeeplySleeping()
    {
        if (this.O && !this.isStatic)
        {
            Iterator var1 = this.players.iterator();
            EntityHuman var2;

            do
            {
                if (!var1.hasNext())
                {
                    return true;
                }

                var2 = (EntityHuman)var1.next();
            }
            while (var2.isDeeplySleeping());

            return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
     * player
     */
    protected void g()
    {
        super.g();
        int var1 = 0;
        int var2 = 0;
        Iterator var3 = this.chunkTickList.iterator();

        while (var3.hasNext())
        {
            ChunkCoordIntPair var4 = (ChunkCoordIntPair)var3.next();
            int var5 = var4.x * 16;
            int var6 = var4.z * 16;
            this.methodProfiler.a("getChunk");
            Chunk var7 = this.getChunkAt(var4.x, var4.z);
            this.a(var5, var6, var7);
            this.methodProfiler.c("tickChunk");
            var7.k();
            this.methodProfiler.c("thunder");
            int var8;
            int var9;
            int var10;
            int var11;

            if (this.random.nextInt(100000) == 0 && this.M() && this.L())
            {
                this.k = this.k * 3 + 1013904223;
                var8 = this.k >> 2;
                var9 = var5 + (var8 & 15);
                var10 = var6 + (var8 >> 8 & 15);
                var11 = this.h(var9, var10);

                if (this.B(var9, var11, var10))
                {
                    this.strikeLightning(new EntityLightning(this, (double)var9, (double)var11, (double)var10));
                    this.q = 2;
                }
            }

            this.methodProfiler.c("iceandsnow");
            int var13;

            if (this.random.nextInt(16) == 0)
            {
                this.k = this.k * 3 + 1013904223;
                var8 = this.k >> 2;
                var9 = var8 & 15;
                var10 = var8 >> 8 & 15;
                var11 = this.h(var9 + var5, var10 + var6);

                if (this.v(var9 + var5, var11 - 1, var10 + var6))
                {
                    this.setTypeId(var9 + var5, var11 - 1, var10 + var6, Block.ICE.id);
                }

                if (this.M() && this.w(var9 + var5, var11, var10 + var6))
                {
                    this.setTypeId(var9 + var5, var11, var10 + var6, Block.SNOW.id);
                }

                if (this.M())
                {
                    BiomeBase var12 = this.getBiome(var9 + var5, var10 + var6);

                    if (var12.d())
                    {
                        var13 = this.getTypeId(var9 + var5, var11 - 1, var10 + var6);

                        if (var13 != 0)
                        {
                            Block.byId[var13].f(this, var9 + var5, var11 - 1, var10 + var6);
                        }
                    }
                }
            }

            this.methodProfiler.c("tickTiles");
            ChunkSection[] var19 = var7.i();
            var9 = var19.length;

            for (var10 = 0; var10 < var9; ++var10)
            {
                ChunkSection var21 = var19[var10];

                if (var21 != null && var21.b())
                {
                    for (int var20 = 0; var20 < 3; ++var20)
                    {
                        this.k = this.k * 3 + 1013904223;
                        var13 = this.k >> 2;
                        int var14 = var13 & 15;
                        int var15 = var13 >> 8 & 15;
                        int var16 = var13 >> 16 & 15;
                        int var17 = var21.a(var14, var16, var15);
                        ++var2;
                        Block var18 = Block.byId[var17];

                        if (var18 != null && var18.isTicking())
                        {
                            ++var1;
                            var18.b(this, var14 + var5, var16 + var21.d(), var15 + var6, this.random);
                        }
                    }
                }
            }

            this.methodProfiler.b();
        }
    }

    /**
     * Used to schedule a call to the updateTick method on the specified block.
     */
    public void a(int var1, int var2, int var3, int var4, int var5)
    {
        this.func_82740_a(var1, var2, var3, var4, var5, 0);
    }

    public void func_82740_a(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        NextTickListEntry var7 = new NextTickListEntry(var1, var2, var3, var4);
        byte var8 = 8;

        if (this.d && var4 > 0)
        {
            if (Block.byId[var4].func_82506_l())
            {
                if (this.d(var7.a - var8, var7.b - var8, var7.c - var8, var7.a + var8, var7.b + var8, var7.c + var8))
                {
                    int var9 = this.getTypeId(var7.a, var7.b, var7.c);

                    if (var9 == var7.d && var9 > 0)
                    {
                        Block.byId[var9].b(this, var7.a, var7.b, var7.c, this.random);
                    }
                }

                return;
            }

            var5 = 1;
        }

        if (this.d(var1 - var8, var2 - var8, var3 - var8, var1 + var8, var2 + var8, var3 + var8))
        {
            if (var4 > 0)
            {
                var7.a((long)var5 + this.worldData.getTime());
                var7.func_82753_a(var6);
            }

            if (!this.field_73064_N.contains(var7))
            {
                this.field_73064_N.add(var7);
                this.N.add(var7);
            }
        }
    }

    /**
     * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
     */
    public void b(int var1, int var2, int var3, int var4, int var5)
    {
        NextTickListEntry var6 = new NextTickListEntry(var1, var2, var3, var4);

        if (var4 > 0)
        {
            var6.a((long)var5 + this.worldData.getTime());
        }

        if (!this.field_73064_N.contains(var6))
        {
            this.field_73064_N.add(var6);
            this.N.add(var6);
        }
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void tickEntities()
    {
        if (this.players.isEmpty())
        {
            if (this.field_80004_Q++ >= 1200)
            {
                return;
            }
        }
        else
        {
            this.func_82742_i();
        }

        super.tickEntities();
    }

    public void func_82742_i()
    {
        this.field_80004_Q = 0;
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean a(boolean var1)
    {
        int var2 = this.N.size();

        if (var2 != this.field_73064_N.size())
        {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        else
        {
            if (var2 > 1000)
            {
                var2 = 1000;
            }

            for (int var3 = 0; var3 < var2; ++var3)
            {
                NextTickListEntry var4 = (NextTickListEntry)this.N.first();

                if (!var1 && var4.e > this.worldData.getTime())
                {
                    break;
                }

                this.N.remove(var4);
                this.field_73064_N.remove(var4);
                byte var5 = 8;

                if (this.d(var4.a - var5, var4.b - var5, var4.c - var5, var4.a + var5, var4.b + var5, var4.c + var5))
                {
                    int var6 = this.getTypeId(var4.a, var4.b, var4.c);

                    if (var6 == var4.d && var6 > 0)
                    {
                        Block.byId[var6].b(this, var4.a, var4.b, var4.c, this.random);
                    }
                }
            }

            return !this.N.isEmpty();
        }
    }

    public List a(Chunk var1, boolean var2)
    {
        ArrayList var3 = null;
        ChunkCoordIntPair var4 = var1.l();
        int var5 = var4.x << 4;
        int var6 = var5 + 16;
        int var7 = var4.z << 4;
        int var8 = var7 + 16;
        Iterator var9 = this.N.iterator();

        while (var9.hasNext())
        {
            NextTickListEntry var10 = (NextTickListEntry)var9.next();

            if (var10.a >= var5 && var10.a < var6 && var10.c >= var7 && var10.c < var8)
            {
                if (var2)
                {
                    this.field_73064_N.remove(var10);
                    var9.remove();
                }

                if (var3 == null)
                {
                    var3 = new ArrayList();
                }

                var3.add(var10);
            }
        }

        return var3;
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void entityJoinedWorld(Entity var1, boolean var2)
    {
        if (!this.server.getSpawnAnimals() && (var1 instanceof EntityAnimal || var1 instanceof EntityWaterAnimal))
        {
            var1.die();
        }

        if (!this.server.getSpawnNPCs() && var1 instanceof NPC)
        {
            var1.die();
        }

        if (!(var1.passenger instanceof EntityHuman))
        {
            super.entityJoinedWorld(var1, var2);
        }
    }

    /**
     * direct call to super.updateEntityWithOptionalForce
     */
    public void vehicleEnteredWorld(Entity var1, boolean var2)
    {
        super.entityJoinedWorld(var1, var2);
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider j()
    {
        IChunkLoader var1 = this.dataManager.createChunkLoader(this.worldProvider);
        this.chunkProviderServer = new ChunkProviderServer(this, var1, this.worldProvider.getChunkProvider());
        return this.chunkProviderServer;
    }

    /**
     * get a list of tileEntity's
     */
    public List getTileEntities(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        ArrayList var7 = new ArrayList();
        Iterator var8 = this.tileEntityList.iterator();

        while (var8.hasNext())
        {
            TileEntity var9 = (TileEntity)var8.next();

            if (var9.x >= var1 && var9.y >= var2 && var9.z >= var3 && var9.x < var4 && var9.y < var5 && var9.z < var6)
            {
                var7.add(var9);
            }
        }

        return var7;
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean a(EntityHuman var1, int var2, int var3, int var4)
    {
        int var5 = MathHelper.a(var2 - this.worldData.c());
        int var6 = MathHelper.a(var4 - this.worldData.e());

        if (var5 > var6)
        {
            var6 = var5;
        }

        return var6 > 16 || this.server.getServerConfigurationManager().isOp(var1.name) || this.server.I();
    }

    protected void a(WorldSettings var1)
    {
        if (this.entitiesById == null)
        {
            this.entitiesById = new IntHashMap();
        }

        if (this.field_73064_N == null)
        {
            this.field_73064_N = new HashSet();
        }

        if (this.N == null)
        {
            this.N = new TreeSet();
        }

        this.b(var1);
        super.a(var1);
    }

    /**
     * creates a spawn position at random within 256 blocks of 0,0
     */
    protected void b(WorldSettings var1)
    {
        if (!this.worldProvider.e())
        {
            this.worldData.setSpawn(0, this.worldProvider.getSeaLevel(), 0);
        }
        else
        {
            this.isLoading = true;
            WorldChunkManager var2 = this.worldProvider.d;
            List var3 = var2.a();
            Random var4 = new Random(this.getSeed());
            ChunkPosition var5 = var2.a(0, 0, 256, var3, var4);
            int var6 = 0;
            int var7 = this.worldProvider.getSeaLevel();
            int var8 = 0;

            if (var5 != null)
            {
                var6 = var5.x;
                var8 = var5.z;
            }
            else
            {
                System.out.println("Unable to find spawn biome");
            }

            int var9 = 0;

            while (!this.worldProvider.canSpawn(var6, var8))
            {
                var6 += var4.nextInt(64) - var4.nextInt(64);
                var8 += var4.nextInt(64) - var4.nextInt(64);
                ++var9;

                if (var9 == 1000)
                {
                    break;
                }
            }

            this.worldData.setSpawn(var6, var7, var8);
            this.isLoading = false;

            if (var1.c())
            {
                this.k();
            }
        }
    }

    /**
     * Creates the bonus chest in the world.
     */
    protected void k()
    {
        WorldGenBonusChest var1 = new WorldGenBonusChest(S, 10);

        for (int var2 = 0; var2 < 10; ++var2)
        {
            int var3 = this.worldData.c() + this.random.nextInt(6) - this.random.nextInt(6);
            int var4 = this.worldData.e() + this.random.nextInt(6) - this.random.nextInt(6);
            int var5 = this.i(var3, var4) + 1;

            if (var1.a(this, this.random, var3, var5, var4))
            {
                break;
            }
        }
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension.
     */
    public ChunkCoordinates getDimensionSpawn()
    {
        return this.worldProvider.h();
    }

    /**
     * Saves all chunks to disk while updating progress bar.
     */
    public void save(boolean var1, IProgressUpdate var2)
    {
        if (this.chunkProvider.canSave())
        {
            if (var2 != null)
            {
                var2.a("Saving level");
            }

            this.a();

            if (var2 != null)
            {
                var2.c("Saving chunks");
            }

            this.chunkProvider.saveChunks(var1, var2);
        }
    }

    /**
     * Saves the chunks to disk.
     */
    protected void a()
    {
        this.C();
        this.dataManager.saveWorldData(this.worldData, this.server.getServerConfigurationManager().q());
        this.worldMaps.a();
    }

    /**
     * Start the skin for this entity downloading, if necessary, and increment its reference counter
     */
    protected void a(Entity var1)
    {
        super.a(var1);
        this.entitiesById.a(var1.id, var1);
        Entity[] var2 = var1.ao();

        if (var2 != null)
        {
            Entity[] var3 = var2;
            int var4 = var2.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                Entity var6 = var3[var5];
                this.entitiesById.a(var6.id, var6);
            }
        }
    }

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    protected void b(Entity var1)
    {
        super.b(var1);
        this.entitiesById.d(var1.id);
        Entity[] var2 = var1.ao();

        if (var2 != null)
        {
            Entity[] var3 = var2;
            int var4 = var2.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                Entity var6 = var3[var5];
                this.entitiesById.d(var6.id);
            }
        }
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntity(int var1)
    {
        return (Entity)this.entitiesById.get(var1);
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean strikeLightning(Entity var1)
    {
        if (super.strikeLightning(var1))
        {
            this.server.getServerConfigurationManager().sendPacketNearby(var1.locX, var1.locY, var1.locZ, 512.0D, this.worldProvider.dimension, new Packet71Weather(var1));
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void broadcastEntityEffect(Entity var1, byte var2)
    {
        Packet38EntityStatus var3 = new Packet38EntityStatus(var1.id, var2);
        this.getTracker().sendPacketToEntity(var1, var3);
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
        var11.a(false);

        if (!var10)
        {
            var11.field_77281_g.clear();
        }

        Iterator var12 = this.players.iterator();

        while (var12.hasNext())
        {
            EntityHuman var13 = (EntityHuman)var12.next();

            if (var13.e(var2, var4, var6) < 4096.0D)
            {
                ((EntityPlayer)var13).netServerHandler.sendPacket(new Packet60Explosion(var2, var4, var6, var8, var11.field_77281_g, (Vec3D)var11.func_77277_b().get(var13)));
            }
        }

        return var11;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    public void playNote(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        NoteBlockData var7 = new NoteBlockData(var1, var2, var3, var4, var5, var6);
        Iterator var8 = this.Q[this.R].iterator();
        NoteBlockData var9;

        do
        {
            if (!var8.hasNext())
            {
                this.Q[this.R].add(var7);
                return;
            }

            var9 = (NoteBlockData)var8.next();
        }
        while (!var9.equals(var7));
    }

    /**
     * Send and apply locally all pending BlockEvents to each player with 64m radius of the event.
     */
    private void U()
    {
        while (!this.Q[this.R].isEmpty())
        {
            int var1 = this.R;
            this.R ^= 1;
            Iterator var2 = this.Q[var1].iterator();

            while (var2.hasNext())
            {
                NoteBlockData var3 = (NoteBlockData)var2.next();

                if (this.a(var3))
                {
                    this.server.getServerConfigurationManager().sendPacketNearby((double)var3.a(), (double)var3.b(), (double)var3.c(), 64.0D, this.worldProvider.dimension, new Packet54PlayNoteBlock(var3.a(), var3.b(), var3.c(), var3.f(), var3.d(), var3.e()));
                }
            }

            this.Q[var1].clear();
        }
    }

    /**
     * Called to apply a pending BlockEvent to apply to the current world.
     */
    private boolean a(NoteBlockData var1)
    {
        int var2 = this.getTypeId(var1.a(), var1.b(), var1.c());

        if (var2 == var1.f())
        {
            Block.byId[var2].b(this, var1.a(), var1.b(), var1.c(), var1.d(), var1.e());
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Syncs all changes to disk and wait for completion.
     */
    public void saveLevel()
    {
        this.dataManager.a();
    }

    /**
     * Updates all weather states.
     */
    protected void n()
    {
        boolean var1 = this.M();
        super.n();

        if (var1 != this.M())
        {
            if (var1)
            {
                this.server.getServerConfigurationManager().sendAll(new Packet70Bed(2, 0));
            }
            else
            {
                this.server.getServerConfigurationManager().sendAll(new Packet70Bed(1, 0));
            }
        }
    }

    /**
     * Gets the MinecraftServer.
     */
    public MinecraftServer getMinecraftServer()
    {
        return this.server;
    }

    /**
     * Gets the EntityTracker
     */
    public EntityTracker getTracker()
    {
        return this.tracker;
    }

    public PlayerManager getPlayerManager()
    {
        return this.manager;
    }
}
