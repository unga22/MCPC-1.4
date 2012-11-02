package net.minecraft.server;

public class WorldType
{
    /** List of world types. */
    public static final WorldType[] types = new WorldType[16];

    /** Default world type. */
    public static final WorldType NORMAL = (new WorldType(0, "default", 1)).g();

    /** Flat world type. */
    public static final WorldType FLAT = new WorldType(1, "flat");

    /** Large Biome world Type. */
    public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");

    /** Default (1.1) world type. */
    public static final WorldType NORMAL_1_1 = (new WorldType(8, "default_1_1", 0)).a(false);

    /** ID for this world type. */
    private final int f;
    private final String name;

    /** The int version of the ChunkProvider that generated this world. */
    private final int version;

    /**
     * Whether this world type can be generated. Normally true; set to false for out-of-date generator versions.
     */
    private boolean i;

    /** Whether this WorldType has a version or not. */
    private boolean j;

    private WorldType(int var1, String var2)
    {
        this(var1, var2, 0);
    }

    private WorldType(int var1, String var2, int var3)
    {
        this.name = var2;
        this.version = var3;
        this.i = true;
        this.f = var1;
        types[var1] = this;
    }

    public String name()
    {
        return this.name;
    }

    /**
     * Returns generatorVersion.
     */
    public int getVersion()
    {
        return this.version;
    }

    public WorldType a(int var1)
    {
        return this == NORMAL && var1 == 0 ? NORMAL_1_1 : this;
    }

    /**
     * Sets canBeCreated to the provided value, and returns this.
     */
    private WorldType a(boolean var1)
    {
        this.i = var1;
        return this;
    }

    /**
     * Flags this world type as having an associated version.
     */
    private WorldType g()
    {
        this.j = true;
        return this;
    }

    /**
     * Returns true if this world Type has a version associated with it.
     */
    public boolean e()
    {
        return this.j;
    }

    public static WorldType getType(String var0)
    {
        WorldType[] var1 = types;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            WorldType var4 = var1[var3];

            if (var4 != null && var4.name.equalsIgnoreCase(var0))
            {
                return var4;
            }
        }

        return null;
    }
}
