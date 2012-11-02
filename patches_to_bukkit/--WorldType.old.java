package net.minecraft.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldType
{
  public static final WorldType[] types = new WorldType[16];
  public static final WorldType NORMAL = new WorldType(0, "default", 1).d();
  public static final WorldType FLAT = new WorldType(1, "flat");
  public static final WorldType VERSION_1_1f = new WorldType(8, "default_1_1", 0).a(false);
  private final String name;
  private final int version;
  private boolean g;
  private boolean h;
  protected BiomeBase[] biomesForWorldType;

  protected WorldType(int i, String s)
  {
    this(i, s, 0);
  }

  protected WorldType(int i, String s, int j) {
    this.name = s;
    this.version = j;
    this.g = true;
    types[i] = this;
    switch (i) {
    case 8:
      this.biomesForWorldType = new BiomeBase[] { BiomeBase.DESERT, BiomeBase.FOREST, BiomeBase.EXTREME_HILLS, BiomeBase.SWAMPLAND, BiomeBase.PLAINS, BiomeBase.TAIGA };
      break;
    default:
      this.biomesForWorldType = new BiomeBase[] { BiomeBase.DESERT, BiomeBase.FOREST, BiomeBase.EXTREME_HILLS, BiomeBase.SWAMPLAND, BiomeBase.PLAINS, BiomeBase.TAIGA, BiomeBase.JUNGLE };
    }
  }

  public String name()
  {
    return this.name;
  }

  public int getVersion() {
    return this.version;
  }

  public WorldType a(int i) {
    return (this == NORMAL) && (i == 0) ? VERSION_1_1f : this;
  }

  private WorldType a(boolean flag) {
    this.g = flag;
    return this;
  }

  private WorldType d() {
    this.h = true;
    return this;
  }

  public boolean c() {
    return this.h;
  }

  public static WorldType getType(String s) {
    for (int i = 0; i < types.length; i++) {
      if ((types[i] != null) && (types[i].name.equalsIgnoreCase(s))) {
        return types[i];
      }
    }

    return null;
  }

  public WorldChunkManager getChunkManager(World world)
  {
    return this == FLAT ? new WorldChunkManagerHell(BiomeBase.PLAINS, 0.5F, 0.5F) : new WorldChunkManager(world);
  }

  public IChunkProvider getChunkGenerator(World world)
  {
    return this == FLAT ? new ChunkProviderFlat(world, world.getSeed(), world.getWorldData().shouldGenerateMapFeatures()) : new ChunkProviderGenerate(world, world.getSeed(), world.getWorldData().shouldGenerateMapFeatures());
  }

  public int getMinimumSpawnHeight(World world)
  {
    return this == FLAT ? 4 : 64;
  }

  public BiomeBase[] getBiomesForWorldType() {
    return this.biomesForWorldType;
  }

  public void addNewBiome(BiomeBase biome) {
    List newBiomesForWorld = new ArrayList();
    newBiomesForWorld.addAll(Arrays.asList(this.biomesForWorldType));

    if (!newBiomesForWorld.contains(biome))
      newBiomesForWorld.add(biome);
    this.biomesForWorldType = ((BiomeBase[])newBiomesForWorld.toArray(new BiomeBase[0]));
  }

  public void removeBiome(BiomeBase biome) {
    List newBiomesForWorld = new ArrayList();
    newBiomesForWorld.addAll(Arrays.asList(this.biomesForWorldType));

    newBiomesForWorld.remove(biome);
    this.biomesForWorldType = ((BiomeBase[])newBiomesForWorld.toArray(new BiomeBase[0]));
  }
}

