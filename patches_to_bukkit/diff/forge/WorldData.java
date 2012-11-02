package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Map;

public class WorldData
{
    /** Holds the seed of the currently world. */
    private long seed;
    private WorldType type;
    private String field_82576_c;

    /** The spawn zone position X coordinate. */
    private int spawnX;

    /** The spawn zone position Y coordinate. */
    private int spawnY;

    /** The spawn zone position Z coordinate. */
    private int spawnZ;

    /** Total time for this world. */
    private long time;

    /** The current world time in ticks, ranging from 0 to 23999. */
    private long dayTime;

    /** The last time the player was in this world. */
    private long lastPlayed;

    /** The size of entire save of current world on the disk, isn't exactly. */
    private long sizeOnDisk;
    private NBTTagCompound playerData;
    private int dimension;

    /** The name of the save defined at world creation. */
    private String name;

    /** Introduced in beta 1.3, is the save version for future control. */
    private int version;

    /** True if it's raining, false otherwise. */
    private boolean isRaining;

    /** Number of ticks until next rain. */
    private int rainTicks;

    /** Is thunderbolts failing now? */
    private boolean isThundering;

    /** Number of ticks untils next thunderbolt. */
    private int thunderTicks;

    /** The Game Type. */
    private EnumGamemode gameType;

    /**
     * Whether the map features (e.g. strongholds) generation is enabled or disabled.
     */
    private boolean useMapFeatures;

    /** Hardcore mode flag */
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private GameRules gameRules;
    private Map additionalProperties;

    protected WorldData()
    {
        this.type = WorldType.NORMAL;
        this.field_82576_c = "";
        this.gameRules = new GameRules();
    }

    public WorldData(NBTTagCompound var1)
    {
        this.type = WorldType.NORMAL;
        this.field_82576_c = "";
        this.gameRules = new GameRules();
        this.seed = var1.getLong("RandomSeed");

        if (var1.hasKey("generatorName"))
        {
            String var2 = var1.getString("generatorName");
            this.type = WorldType.getType(var2);

            if (this.type == null)
            {
                this.type = WorldType.NORMAL;
            }
            else if (this.type.e())
            {
                int var3 = 0;

                if (var1.hasKey("generatorVersion"))
                {
                    var3 = var1.getInt("generatorVersion");
                }

                this.type = this.type.a(var3);
            }

            if (var1.hasKey("generatorOptions"))
            {
                this.field_82576_c = var1.getString("generatorOptions");
            }
        }

        this.gameType = EnumGamemode.a(var1.getInt("GameType"));

        if (var1.hasKey("MapFeatures"))
        {
            this.useMapFeatures = var1.getBoolean("MapFeatures");
        }
        else
        {
            this.useMapFeatures = true;
        }

        this.spawnX = var1.getInt("SpawnX");
        this.spawnY = var1.getInt("SpawnY");
        this.spawnZ = var1.getInt("SpawnZ");
        this.time = var1.getLong("Time");

        if (var1.hasKey("DayTime"))
        {
            this.dayTime = var1.getLong("DayTime");
        }
        else
        {
            this.dayTime = this.time;
        }

        this.lastPlayed = var1.getLong("LastPlayed");
        this.sizeOnDisk = var1.getLong("SizeOnDisk");
        this.name = var1.getString("LevelName");
        this.version = var1.getInt("version");
        this.rainTicks = var1.getInt("rainTime");
        this.isRaining = var1.getBoolean("raining");
        this.thunderTicks = var1.getInt("thunderTime");
        this.isThundering = var1.getBoolean("thundering");
        this.hardcore = var1.getBoolean("hardcore");

        if (var1.hasKey("initialized"))
        {
            this.initialized = var1.getBoolean("initialized");
        }
        else
        {
            this.initialized = true;
        }

        if (var1.hasKey("allowCommands"))
        {
            this.allowCommands = var1.getBoolean("allowCommands");
        }
        else
        {
            this.allowCommands = this.gameType == EnumGamemode.CREATIVE;
        }

        if (var1.hasKey("Player"))
        {
            this.playerData = var1.getCompound("Player");
            this.dimension = this.playerData.getInt("Dimension");
        }

        if (var1.hasKey("GameRules"))
        {
            this.gameRules.a(var1.getCompound("GameRules"));
        }
    }

    public WorldData(WorldSettings var1, String var2)
    {
        this.type = WorldType.NORMAL;
        this.field_82576_c = "";
        this.gameRules = new GameRules();
        this.seed = var1.d();
        this.gameType = var1.e();
        this.useMapFeatures = var1.g();
        this.name = var2;
        this.hardcore = var1.f();
        this.type = var1.h();
        this.field_82576_c = var1.func_82749_j();
        this.allowCommands = var1.i();
        this.initialized = false;
    }

    public WorldData(WorldData var1)
    {
        this.type = WorldType.NORMAL;
        this.field_82576_c = "";
        this.gameRules = new GameRules();
        this.seed = var1.seed;
        this.type = var1.type;
        this.field_82576_c = var1.field_82576_c;
        this.gameType = var1.gameType;
        this.useMapFeatures = var1.useMapFeatures;
        this.spawnX = var1.spawnX;
        this.spawnY = var1.spawnY;
        this.spawnZ = var1.spawnZ;
        this.time = var1.time;
        this.dayTime = var1.dayTime;
        this.lastPlayed = var1.lastPlayed;
        this.sizeOnDisk = var1.sizeOnDisk;
        this.playerData = var1.playerData;
        this.dimension = var1.dimension;
        this.name = var1.name;
        this.version = var1.version;
        this.rainTicks = var1.rainTicks;
        this.isRaining = var1.isRaining;
        this.thunderTicks = var1.thunderTicks;
        this.isThundering = var1.isThundering;
        this.hardcore = var1.hardcore;
        this.allowCommands = var1.allowCommands;
        this.initialized = var1.initialized;
        this.gameRules = var1.gameRules;
    }

    /**
     * Gets the NBTTagCompound for the worldInfo
     */
    public NBTTagCompound a()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.a(var1, this.playerData);
        return var1;
    }

    /**
     * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
     */
    public NBTTagCompound a(NBTTagCompound var1)
    {
        NBTTagCompound var2 = new NBTTagCompound();
        this.a(var2, var1);
        return var2;
    }

    private void a(NBTTagCompound var1, NBTTagCompound var2)
    {
        var1.setLong("RandomSeed", this.seed);
        var1.setString("generatorName", this.type.name());
        var1.setInt("generatorVersion", this.type.getVersion());
        var1.setString("generatorOptions", this.field_82576_c);
        var1.setInt("GameType", this.gameType.a());
        var1.setBoolean("MapFeatures", this.useMapFeatures);
        var1.setInt("SpawnX", this.spawnX);
        var1.setInt("SpawnY", this.spawnY);
        var1.setInt("SpawnZ", this.spawnZ);
        var1.setLong("Time", this.time);
        var1.setLong("DayTime", this.dayTime);
        var1.setLong("SizeOnDisk", this.sizeOnDisk);
        var1.setLong("LastPlayed", System.currentTimeMillis());
        var1.setString("LevelName", this.name);
        var1.setInt("version", this.version);
        var1.setInt("rainTime", this.rainTicks);
        var1.setBoolean("raining", this.isRaining);
        var1.setInt("thunderTime", this.thunderTicks);
        var1.setBoolean("thundering", this.isThundering);
        var1.setBoolean("hardcore", this.hardcore);
        var1.setBoolean("allowCommands", this.allowCommands);
        var1.setBoolean("initialized", this.initialized);
        var1.setCompound("GameRules", this.gameRules.a());

        if (var2 != null)
        {
            var1.setCompound("Player", var2);
        }
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed()
    {
        return this.seed;
    }

    /**
     * Returns the x spawn position
     */
    public int c()
    {
        return this.spawnX;
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int d()
    {
        return this.spawnY;
    }

    /**
     * Returns the z spawn position
     */
    public int e()
    {
        return this.spawnZ;
    }

    public long getTime()
    {
        return this.time;
    }

    /**
     * Get current world time
     */
    public long g()
    {
        return this.dayTime;
    }

    @SideOnly(Side.CLIENT)
    public long h()
    {
        return this.sizeOnDisk;
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound i()
    {
        return this.playerData;
    }

    public int j()
    {
        return this.dimension;
    }

    @SideOnly(Side.CLIENT)
    public void a(int var1)
    {
        this.spawnX = var1;
    }

    @SideOnly(Side.CLIENT)
    public void b(int var1)
    {
        this.spawnY = var1;
    }

    public void func_82572_b(long var1)
    {
        this.time = var1;
    }

    @SideOnly(Side.CLIENT)
    public void c(int var1)
    {
        this.spawnZ = var1;
    }

    /**
     * Set current world time
     */
    public void c(long var1)
    {
        this.dayTime = var1;
    }

    /**
     * Sets the spawn zone position. Args: x, y, z
     */
    public void setSpawn(int var1, int var2, int var3)
    {
        this.spawnX = var1;
        this.spawnY = var2;
        this.spawnZ = var3;
    }

    /**
     * Get current world name
     */
    public String getName()
    {
        return this.name;
    }

    public void setName(String var1)
    {
        this.name = var1;
    }

    /**
     * Returns the save version of this world
     */
    public int l()
    {
        return this.version;
    }

    /**
     * Sets the save version of the world
     */
    public void e(int var1)
    {
        this.version = var1;
    }

    @SideOnly(Side.CLIENT)
    public long m()
    {
        return this.lastPlayed;
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering()
    {
        return this.isThundering;
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean var1)
    {
        this.isThundering = var1;
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderDuration()
    {
        return this.thunderTicks;
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderDuration(int var1)
    {
        this.thunderTicks = var1;
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean hasStorm()
    {
        return this.isRaining;
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setStorm(boolean var1)
    {
        this.isRaining = var1;
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getWeatherDuration()
    {
        return this.rainTicks;
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setWeatherDuration(int var1)
    {
        this.rainTicks = var1;
    }

    /**
     * Gets the GameType.
     */
    public EnumGamemode getGameType()
    {
        return this.gameType;
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
     */
    public boolean shouldGenerateMapFeatures()
    {
        return this.useMapFeatures;
    }

    /**
     * Sets the GameType.
     */
    public void setGameType(EnumGamemode var1)
    {
        this.gameType = var1;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcore()
    {
        return this.hardcore;
    }

    public WorldType getType()
    {
        return this.type;
    }

    public void setType(WorldType var1)
    {
        this.type = var1;
    }

    public String func_82571_y()
    {
        return this.field_82576_c;
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean allowCommands()
    {
        return this.allowCommands;
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized()
    {
        return this.initialized;
    }

    /**
     * Sets the initialization status of the World.
     */
    public void d(boolean var1)
    {
        this.initialized = var1;
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRules()
    {
        return this.gameRules;
    }

    public void setAdditionalProperties(Map var1)
    {
        if (this.additionalProperties == null)
        {
            this.additionalProperties = var1;
        }
    }

    public NBTBase getAdditionalProperty(String var1)
    {
        return this.additionalProperties != null ? (NBTBase)this.additionalProperties.get(var1) : null;
    }
}
