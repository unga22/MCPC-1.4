package net.minecraft.server;

import forge.ForgeHooks;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.craftbukkit.util.LongHashset;
import org.bukkit.craftbukkit.util.UnsafeList;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;

public class World
  implements IBlockAccess
{
  public static double MAX_ENTITY_RADIUS = 2.0D;

  public boolean a = false;
  public List entityList = new ArrayList();
  private List G = new ArrayList();
  private TreeSet H = new TreeSet();
  private Set I = new HashSet();
  public List tileEntityList = new ArrayList();
  private List J = new ArrayList();
  private List K = new ArrayList();
  public List players = new ArrayList();
  public UnsafeList e = new UnsafeList();
  private long L = 16777215L;
  public int f = 0;
  protected int g = new Random().nextInt();
  protected final int h = 1013904223;
  protected float i;
  protected float j;
  protected float k;
  protected float l;
  protected int m = 0;
  public int n = 0;
  public boolean suppressPhysics = false;
  private long M = System.currentTimeMillis();
  public int p = 40;
  public int difficulty;
  public Random random = new Random();
  public boolean s = false;
  public WorldProvider worldProvider;
  protected List u = new ArrayList();
  public IChunkProvider chunkProvider;
  protected final IDataManager dataManager;
  public WorldData worldData;
  public boolean isLoading;
  private boolean N;
  public WorldMapCollection worldMaps;
  public final VillageCollection villages = new VillageCollection(this);
  private final VillageSiege O = new VillageSiege(this);
  private ArrayList P = new ArrayList();
  private boolean Q;
  public boolean allowMonsters = true;
  public boolean allowAnimals = true;
  private LongHashset chunkTickList;
  public long ticksPerAnimalSpawns;
  public long ticksPerMonsterSpawns;
  private int R;
  int[] E;
  private List S;
  public boolean isStatic;
  private final CraftWorld world;
  public boolean pvpMode;
  public boolean keepSpawnInMemory = true;
  public ChunkGenerator generator;
  Chunk lastChunkAccessed;
  int lastXAccessed = -2147483648;
  int lastZAccessed = -2147483648;
  final Object chunkLock = new Object();

  public BiomeBase getBiome(int i, int j)
  {
    if (isLoaded(i, 0, j)) {
      Chunk chunk = getChunkAtWorldCoords(i, j);

      if (chunk != null) {
        return chunk.a(i & 0xF, j & 0xF, this.worldProvider.c);
      }
    }

    return this.worldProvider.c.getBiome(i, j);
  }

  public WorldChunkManager getWorldChunkManager() {
    return this.worldProvider.c;
  }

  private boolean canSpawn(int x, int z)
  {
    if (this.generator != null) {
      return this.generator.canSpawn(getWorld(), x, z);
    }
    return this.worldProvider.canSpawn(x, z);
  }

  public CraftWorld getWorld()
  {
    return this.world;
  }

  public CraftServer getServer() {
    return (CraftServer)Bukkit.getServer();
  }

  public World(IDataManager idatamanager, String s, WorldSettings worldsettings, WorldProvider worldprovider, ChunkGenerator gen, World.Environment env)
  {
    this.generator = gen;
    this.world = new CraftWorld((WorldServer)this, gen, env);

    this.chunkTickList = new LongHashset();
    this.ticksPerAnimalSpawns = getServer().getTicksPerAnimalSpawns();
    this.ticksPerMonsterSpawns = getServer().getTicksPerMonsterSpawns();
    this.R = this.random.nextInt(12000);
    this.E = new int[32768];
    this.S = new ArrayList();
    this.isStatic = false;
    this.dataManager = idatamanager;
    this.worldMaps = new WorldMapCollection(idatamanager);
    this.worldData = idatamanager.getWorldData();
    this.s = (this.worldData == null);
    if (worldprovider != null)
      this.worldProvider = worldprovider;
    else if ((this.worldData != null) && (this.worldData.g() != 0))
      this.worldProvider = WorldProvider.byDimension(this.worldData.g());
    else {
      this.worldProvider = WorldProvider.byDimension(0);
    }

    boolean flag = false;

    if (this.worldData == null) {
      this.worldData = new WorldData(worldsettings, s);
      flag = true;
    } else {
      this.worldData.a(s);
    }

    this.worldProvider.a(this);
    this.chunkProvider = b();
    if (flag) {
      c();
    }

    g();
    B();
    getServer().addWorld(this.world);
  }

  protected IChunkProvider b() {
    IChunkLoader ichunkloader = this.dataManager.createChunkLoader(this.worldProvider);

    return new ChunkProviderLoadOrGenerate(this, ichunkloader, this.worldProvider.getChunkProvider());
  }

  protected void c() {
    if (!this.worldProvider.c()) {
      this.worldData.setSpawn(0, this.worldProvider.getSeaLevel(), 0);
    } else {
      this.isLoading = true;
      WorldChunkManager worldchunkmanager = this.worldProvider.c;
      List list = worldchunkmanager.a();
      Random random = new Random(getSeed());
      ChunkPosition chunkposition = worldchunkmanager.a(0, 0, 256, list, random);
      int i = 0;
      int j = this.worldProvider.getSeaLevel();
      int k = 0;

      if (this.generator != null) {
        Random rand = new Random(getSeed());
        Location spawn = this.generator.getFixedSpawnLocation(((WorldServer)this).getWorld(), rand);

        if (spawn != null) {
          if (spawn.getWorld() != ((WorldServer)this).getWorld()) {
            throw new IllegalStateException("Cannot set spawn point for " + this.worldData.name + " to be in another world (" + spawn.getWorld().getName() + ")");
          }
          this.worldData.setSpawn(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
          this.isLoading = false;
          return;
        }

      }

      if (chunkposition != null) {
        i = chunkposition.x;
        k = chunkposition.z;
      } else {
        System.out.println("Unable to find spawn biome");
      }

      int l = 0;

      while (!canSpawn(i, k)) {
        i += random.nextInt(64) - random.nextInt(64);
        k += random.nextInt(64) - random.nextInt(64);
        l++;
        if (l == 1000) {
          break;
        }
      }

      this.worldData.setSpawn(i, j, k);
      this.isLoading = false;
    }
  }

  public ChunkCoordinates getDimensionSpawn() {
    return this.worldProvider.e();
  }

  public int b(int i, int j)
  {
    for (int k = 63; !isEmpty(i, k + 1, j); k++);
    return getTypeId(i, k, j);
  }

  public void save(boolean flag, IProgressUpdate iprogressupdate) {
    if (this.chunkProvider.canSave()) {
      if (iprogressupdate != null) {
        iprogressupdate.a("Saving level");
      }

      A();
      if (iprogressupdate != null) {
        iprogressupdate.b("Saving chunks");
      }

      this.chunkProvider.saveChunks(flag, iprogressupdate);
      ForgeHooks.onWorldSave(this);
    }
  }

  private void A() {
    m();
    this.dataManager.saveWorldData(this.worldData, this.players);
    this.worldMaps.a();
  }

  public int getTypeId(int i, int j, int k) {
    return (i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000) ? getChunkAt(i >> 4, k >> 4).getTypeId(i & 0xF, j, k & 0xF) : j >= 256 ? 0 : j < 0 ? 0 : 0;
  }

  public int f(int i, int j, int k) {
    return (i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000) ? getChunkAt(i >> 4, k >> 4).b(i & 0xF, j, k & 0xF) : j >= 256 ? 0 : j < 0 ? 0 : 0;
  }

  public boolean isEmpty(int i, int j, int k)
  {
    int id = getTypeId(i, j, k);
    return (id == 0) || (Block.byId[id] == null) || (Block.byId[id].isAirBlock(this, i, j, k));
  }

  public boolean isTileEntity(int i, int j, int k) {
    int l = getTypeId(i, j, k);

    return (Block.byId[l] != null) && (Block.byId[l].o());
  }

  public boolean isLoaded(int i, int j, int k) {
    return (j >= 0) && (j < 256) ? isChunkLoaded(i >> 4, k >> 4) : false;
  }

  public boolean areChunksLoaded(int i, int j, int k, int l) {
    return a(i - l, j - l, k - l, i + l, j + l, k + l);
  }

  public boolean a(int i, int j, int k, int l, int i1, int j1) {
    if ((i1 >= 0) && (j < 256)) {
      i >>= 4;
      k >>= 4;
      l >>= 4;
      j1 >>= 4;

      for (int k1 = i; k1 <= l; k1++) {
        for (int l1 = k; l1 <= j1; l1++) {
          if (!isChunkLoaded(k1, l1)) {
            return false;
          }
        }
      }

      return true;
    }
    return false;
  }

  private boolean isChunkLoaded(int i, int j)
  {
    return this.chunkProvider.isChunkLoaded(i, j);
  }

  public Chunk getChunkAtWorldCoords(int i, int j) {
    return getChunkAt(i >> 4, j >> 4);
  }

  public Chunk getChunkAt(int i, int j)
  {
    Chunk result = null;
    synchronized (this.chunkLock) {
      if ((this.lastChunkAccessed == null) || (this.lastXAccessed != i) || (this.lastZAccessed != j)) {
        this.lastXAccessed = i;
        this.lastZAccessed = j;
        this.lastChunkAccessed = this.chunkProvider.getOrCreateChunk(i, j);
      }
      result = this.lastChunkAccessed;
    }
    return result;
  }

  public boolean setRawTypeIdAndData(int i, int j, int k, int l, int i1)
  {
    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000)) {
      if (j < 0)
        return false;
      if (j >= 256) {
        return false;
      }
      Chunk chunk = getChunkAt(i >> 4, k >> 4);
      boolean flag = chunk.a(i & 0xF, j, k & 0xF, l, i1);

      v(i, j, k);

      return flag;
    }

    return false;
  }

  public boolean setRawTypeId(int i, int j, int k, int l)
  {
    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000)) {
      if (j < 0)
        return false;
      if (j >= 256) {
        return false;
      }
      Chunk chunk = getChunkAt(i >> 4, k >> 4);
      boolean flag = chunk.a(i & 0xF, j, k & 0xF, l);

      v(i, j, k);

      return flag;
    }

    return false;
  }

  public Material getMaterial(int i, int j, int k)
  {
    int l = getTypeId(i, j, k);

    return l == 0 ? Material.AIR : Block.byId[l].material;
  }

  public int getData(int i, int j, int k) {
    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000)) {
      if (j < 0)
        return 0;
      if (j >= 256) {
        return 0;
      }
      Chunk chunk = getChunkAt(i >> 4, k >> 4);

      i &= 15;
      k &= 15;
      return chunk.getData(i, j, k);
    }

    return 0;
  }

  public void setData(int i, int j, int k, int l)
  {
    if (setRawData(i, j, k, l)) {
      int i1 = getTypeId(i, j, k);

      if (Block.r[(i1 & 0xFFF)] != 0)
        update(i, j, k, i1);
      else
        applyPhysics(i, j, k, i1);
    }
  }

  public boolean setRawData(int i, int j, int k, int l)
  {
    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000)) {
      if (j < 0)
        return false;
      if (j >= 256) {
        return false;
      }
      Chunk chunk = getChunkAt(i >> 4, k >> 4);

      i &= 15;
      k &= 15;
      return chunk.b(i, j, k, l);
    }

    return false;
  }

  public boolean setTypeId(int i, int j, int k, int l)
  {
    int old = getTypeId(i, j, k);
    if (setRawTypeId(i, j, k, l)) {
      update(i, j, k, l == 0 ? old : l);
      return true;
    }
    return false;
  }

  public boolean setTypeIdAndData(int i, int j, int k, int l, int i1)
  {
    if (setRawTypeIdAndData(i, j, k, l, i1)) {
      update(i, j, k, l);
      return true;
    }
    return false;
  }

  public void notify(int i, int j, int k)
  {
    for (int l = 0; l < this.u.size(); l++)
      ((IWorldAccess)this.u.get(l)).a(i, j, k);
  }

  public void update(int i, int j, int k, int l)
  {
    notify(i, j, k);
    applyPhysics(i, j, k, l);
  }

  public void g(int i, int j, int k, int l)
  {
    if (k > l) {
      int i1 = l;
      l = k;
      k = i1;
    }

    if (!this.worldProvider.e) {
      for (int i1 = k; i1 <= l; i1++) {
        b(EnumSkyBlock.SKY, i, i1, j);
      }
    }

    b(i, k, j, i, l, j);
  }

  public void k(int i, int j, int k) {
    for (int l = 0; l < this.u.size(); l++)
      ((IWorldAccess)this.u.get(l)).a(i, j, k, i, j, k);
  }

  public void b(int i, int j, int k, int l, int i1, int j1)
  {
    for (int k1 = 0; k1 < this.u.size(); k1++)
      ((IWorldAccess)this.u.get(k1)).a(i, j, k, l, i1, j1);
  }

  public void applyPhysics(int i, int j, int k, int l)
  {
    k(i - 1, j, k, l);
    k(i + 1, j, k, l);
    k(i, j - 1, k, l);
    k(i, j + 1, k, l);
    k(i, j, k - 1, l);
    k(i, j, k + 1, l);
  }

  private void k(int i, int j, int k, int l) {
    if ((!this.suppressPhysics) && (!this.isStatic)) {
      Block block = Block.byId[getTypeId(i, j, k)];

      if (block != null)
      {
        CraftWorld world = ((WorldServer)this).getWorld();
        if (world != null) {
          BlockPhysicsEvent event = new BlockPhysicsEvent(world.getBlockAt(i, j, k), l);
          getServer().getPluginManager().callEvent(event);

          if (event.isCancelled()) {
            return;
          }

        }

        block.doPhysics(this, i, j, k, l);
      }
    }
  }

  public boolean isChunkLoaded(int i, int j, int k) {
    return getChunkAt(i >> 4, k >> 4).d(i & 0xF, j, k & 0xF);
  }

  public int m(int i, int j, int k) {
    if (j < 0) {
      return 0;
    }
    if (j >= 256) {
      j = 255;
    }

    return getChunkAt(i >> 4, k >> 4).c(i & 0xF, j, k & 0xF, 0);
  }

  public int getLightLevel(int i, int j, int k)
  {
    return a(i, j, k, true);
  }

  public int a(int i, int j, int k, boolean flag) {
    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000)) {
      if (flag) {
        int l = getTypeId(i, j, k);

        if ((l == Block.STEP.id) || (l == Block.SOIL.id) || (l == Block.COBBLESTONE_STAIRS.id) || (l == Block.WOOD_STAIRS.id)) {
          int i1 = a(i, j + 1, k, false);
          int j1 = a(i + 1, j, k, false);
          int k1 = a(i - 1, j, k, false);
          int l1 = a(i, j, k + 1, false);
          int i2 = a(i, j, k - 1, false);

          if (j1 > i1) {
            i1 = j1;
          }

          if (k1 > i1) {
            i1 = k1;
          }

          if (l1 > i1) {
            i1 = l1;
          }

          if (i2 > i1) {
            i1 = i2;
          }

          return i1;
        }
      }

      if (j < 0) {
        return 0;
      }
      if (j >= 256) {
        j = 255;
      }

      Chunk chunk = getChunkAt(i >> 4, k >> 4);

      i &= 15;
      k &= 15;
      return chunk.c(i, j, k, this.f);
    }

    return 15;
  }

  public int getHighestBlockYAt(int i, int j)
  {
    if ((i >= -30000000) && (j >= -30000000) && (i < 30000000) && (j < 30000000)) {
      if (!isChunkLoaded(i >> 4, j >> 4)) {
        return 0;
      }
      Chunk chunk = getChunkAt(i >> 4, j >> 4);

      return chunk.b(i & 0xF, j & 0xF);
    }

    return 0;
  }

  public int a(EnumSkyBlock enumskyblock, int i, int j, int k)
  {
    if (j < 0) {
      j = 0;
    }

    if (j >= 256) {
      j = 255;
    }

    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000)) {
      int l = i >> 4;
      int i1 = k >> 4;

      if (!isChunkLoaded(l, i1)) {
        return enumskyblock.c;
      }
      Chunk chunk = getChunkAt(l, i1);

      return chunk.getBrightness(enumskyblock, i & 0xF, j, k & 0xF);
    }

    return enumskyblock.c;
  }

  public void a(EnumSkyBlock enumskyblock, int i, int j, int k, int l)
  {
    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000) && 
      (j >= 0) && 
      (j < 256) && 
      (isChunkLoaded(i >> 4, k >> 4))) {
      Chunk chunk = getChunkAt(i >> 4, k >> 4);

      chunk.a(enumskyblock, i & 0xF, j, k & 0xF, l);

      for (int i1 = 0; i1 < this.u.size(); i1++)
        ((IWorldAccess)this.u.get(i1)).b(i, j, k);
    }
  }

  public void o(int i, int j, int k)
  {
    for (int l = 0; l < this.u.size(); l++)
      ((IWorldAccess)this.u.get(l)).b(i, j, k);
  }

  public float p(int i, int j, int k)
  {
    return this.worldProvider.f[getLightLevel(i, j, k)];
  }

  public boolean e() {
    return this.f < 4;
  }

  public MovingObjectPosition a(Vec3D vec3d, Vec3D vec3d1) {
    return rayTrace(vec3d, vec3d1, false, false);
  }

  public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag) {
    return rayTrace(vec3d, vec3d1, flag, false);
  }

  public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag, boolean flag1) {
    if ((!Double.isNaN(vec3d.a)) && (!Double.isNaN(vec3d.b)) && (!Double.isNaN(vec3d.c))) {
      if ((!Double.isNaN(vec3d1.a)) && (!Double.isNaN(vec3d1.b)) && (!Double.isNaN(vec3d1.c))) {
        int i = MathHelper.floor(vec3d1.a);
        int j = MathHelper.floor(vec3d1.b);
        int k = MathHelper.floor(vec3d1.c);
        int l = MathHelper.floor(vec3d.a);
        int i1 = MathHelper.floor(vec3d.b);
        int j1 = MathHelper.floor(vec3d.c);
        int k1 = getTypeId(l, i1, j1);
        int l1 = getData(l, i1, j1);
        Block block = Block.byId[k1];

        if ((block != null) && ((!flag1) || (block == null) || (block.e(this, l, i1, j1) != null)) && (k1 > 0) && (block.a(l1, flag)))
        {
          MovingObjectPosition movingobjectposition = block.a(this, l, i1, j1, vec3d, vec3d1);

          if (movingobjectposition != null) {
            return movingobjectposition;
          }
        }

        k1 = 200;

        while (k1-- >= 0) {
          if ((Double.isNaN(vec3d.a)) || (Double.isNaN(vec3d.b)) || (Double.isNaN(vec3d.c))) {
            return null;
          }

          if ((l == i) && (i1 == j) && (j1 == k)) {
            return null;
          }

          boolean flag2 = true;
          boolean flag3 = true;
          boolean flag4 = true;
          double d0 = 999.0D;
          double d1 = 999.0D;
          double d2 = 999.0D;

          if (i > l)
            d0 = l + 1.0D;
          else if (i < l)
            d0 = l + 0.0D;
          else {
            flag2 = false;
          }

          if (j > i1)
            d1 = i1 + 1.0D;
          else if (j < i1)
            d1 = i1 + 0.0D;
          else {
            flag3 = false;
          }

          if (k > j1)
            d2 = j1 + 1.0D;
          else if (k < j1)
            d2 = j1 + 0.0D;
          else {
            flag4 = false;
          }

          double d3 = 999.0D;
          double d4 = 999.0D;
          double d5 = 999.0D;
          double d6 = vec3d1.a - vec3d.a;
          double d7 = vec3d1.b - vec3d.b;
          double d8 = vec3d1.c - vec3d.c;

          if (flag2) {
            d3 = (d0 - vec3d.a) / d6;
          }

          if (flag3) {
            d4 = (d1 - vec3d.b) / d7;
          }

          if (flag4) {
            d5 = (d2 - vec3d.c) / d8;
          }

          boolean flag5 = false;
          byte b0;
          if ((d3 < d4) && (d3 < d5))
          {
            byte b0;
            byte b0;
            if (i > l)
              b0 = 4;
            else {
              b0 = 5;
            }

            vec3d.a = d0;
            vec3d.b += d7 * d3;
            vec3d.c += d8 * d3;
          } else if (d4 < d5)
          {
            byte b0;
            byte b0;
            if (j > i1)
              b0 = 0;
            else {
              b0 = 1;
            }

            vec3d.a += d6 * d4;
            vec3d.b = d1;
            vec3d.c += d8 * d4;
          }
          else
          {
            byte b0;
            if (k > j1)
              b0 = 2;
            else {
              b0 = 3;
            }

            vec3d.a += d6 * d5;
            vec3d.b += d7 * d5;
            vec3d.c = d2;
          }

          Vec3D vec3d2 = Vec3D.create(vec3d.a, vec3d.b, vec3d.c);

          l = (int)(vec3d2.a = MathHelper.floor(vec3d.a));
          if (b0 == 5) {
            l--;
            vec3d2.a += 1.0D;
          }

          i1 = (int)(vec3d2.b = MathHelper.floor(vec3d.b));
          if (b0 == 1) {
            i1--;
            vec3d2.b += 1.0D;
          }

          j1 = (int)(vec3d2.c = MathHelper.floor(vec3d.c));
          if (b0 == 3) {
            j1--;
            vec3d2.c += 1.0D;
          }

          int i2 = getTypeId(l, i1, j1);
          int j2 = getData(l, i1, j1);
          Block block1 = Block.byId[i2];

          if (((!flag1) || (block1 == null) || (block1.e(this, l, i1, j1) != null)) && (i2 > 0) && (block1.a(j2, flag))) {
            MovingObjectPosition movingobjectposition1 = block1.a(this, l, i1, j1, vec3d, vec3d1);

            if (movingobjectposition1 != null) {
              return movingobjectposition1;
            }
          }
        }

        return null;
      }
      return null;
    }

    return null;
  }

  public void makeSound(Entity entity, String s, float f, float f1)
  {
    for (int i = 0; i < this.u.size(); i++)
      ((IWorldAccess)this.u.get(i)).a(s, entity.locX, entity.locY - entity.height, entity.locZ, f, f1);
  }

  public void makeSound(double d0, double d1, double d2, String s, float f, float f1)
  {
    for (int i = 0; i < this.u.size(); i++)
      ((IWorldAccess)this.u.get(i)).a(s, d0, d1, d2, f, f1);
  }

  public void a(String s, int i, int j, int k)
  {
    for (int l = 0; l < this.u.size(); l++)
      ((IWorldAccess)this.u.get(l)).a(s, i, j, k);
  }

  public void a(String s, double d0, double d1, double d2, double d3, double d4, double d5)
  {
    for (int i = 0; i < this.u.size(); i++)
      ((IWorldAccess)this.u.get(i)).a(s, d0, d1, d2, d3, d4, d5);
  }

  public boolean strikeLightning(Entity entity)
  {
    this.e.add(entity);
    return true;
  }

  public boolean addEntity(Entity entity)
  {
    return addEntity(entity, CreatureSpawnEvent.SpawnReason.DEFAULT);
  }

  public boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
    if (entity == null) return false;

    int i = MathHelper.floor(entity.locX / 16.0D);
    int j = MathHelper.floor(entity.locZ / 16.0D);
    boolean flag = false;

    if ((entity instanceof EntityHuman)) {
      flag = true;
    }

    Cancellable event = null;
    if (((entity instanceof EntityLiving)) && (!(entity instanceof EntityPlayer))) {
      boolean isAnimal = ((entity instanceof EntityAnimal)) || ((entity instanceof EntityWaterAnimal)) || ((entity instanceof EntityGolem));
      boolean isMonster = ((entity instanceof EntityMonster)) || ((entity instanceof EntityGhast)) || ((entity instanceof EntitySlime));

      if ((spawnReason != CreatureSpawnEvent.SpawnReason.CUSTOM) && (
        ((isAnimal) && (!this.allowAnimals)) || ((isMonster) && (!this.allowMonsters)))) {
        entity.dead = true;
        return false;
      }

      event = CraftEventFactory.callCreatureSpawnEvent((EntityLiving)entity, spawnReason);
    } else if ((entity instanceof EntityItem)) {
      event = CraftEventFactory.callItemSpawnEvent((EntityItem)entity);
    } else if ((entity.getBukkitEntity() instanceof Projectile))
    {
      event = CraftEventFactory.callProjectileLaunchEvent(entity);
    }

    if ((event != null) && ((event.isCancelled()) || (entity.dead))) {
      entity.dead = true;
      return false;
    }

    if ((!flag) && (!isChunkLoaded(i, j))) {
      entity.dead = true;
      return false;
    }
    if ((entity instanceof EntityHuman)) {
      EntityHuman entityhuman = (EntityHuman)entity;

      this.players.add(entityhuman);
      everyoneSleeping();
    }

    getChunkAt(i, j).a(entity);
    this.entityList.add(entity);
    c(entity);
    return true;
  }

  protected void c(Entity entity)
  {
    for (int i = 0; i < this.u.size(); i++)
      ((IWorldAccess)this.u.get(i)).a(entity);
  }

  protected void d(Entity entity)
  {
    for (int i = 0; i < this.u.size(); i++) {
      ((IWorldAccess)this.u.get(i)).b(entity);
    }

    entity.valid = false;
  }

  public void kill(Entity entity) {
    if (entity.passenger != null) {
      entity.passenger.mount((Entity)null);
    }

    if (entity.vehicle != null) {
      entity.mount((Entity)null);
    }

    entity.die();
    if ((entity instanceof EntityHuman)) {
      this.players.remove((EntityHuman)entity);
      everyoneSleeping();
    }
  }

  public void removeEntity(Entity entity) {
    entity.die();
    if ((entity instanceof EntityHuman)) {
      this.players.remove((EntityHuman)entity);
      everyoneSleeping();
    }

    int i = entity.ca;
    int j = entity.cc;

    if ((entity.bZ) && (isChunkLoaded(i, j))) {
      getChunkAt(i, j).b(entity);
    }

    this.entityList.remove(entity);
    d(entity);
  }

  public void addIWorldAccess(IWorldAccess iworldaccess) {
    this.u.add(iworldaccess);
  }

  public List getCubes(Entity entity, AxisAlignedBB axisalignedbb) {
    this.P.clear();
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.d + 1.0D);
    int k = MathHelper.floor(axisalignedbb.b);
    int l = MathHelper.floor(axisalignedbb.e + 1.0D);
    int i1 = MathHelper.floor(axisalignedbb.c);
    int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

    for (int k1 = i; k1 < j; k1++) {
      for (int l1 = i1; l1 < j1; l1++) {
        if (isLoaded(k1, 64, l1)) {
          for (int i2 = k - 1; i2 < l; i2++) {
            Block block = Block.byId[getTypeId(k1, i2, l1)];

            if (block != null) {
              block.a(this, k1, i2, l1, axisalignedbb, this.P);
            }
          }
        }
      }
    }

    double d0 = 0.25D;
    List list = getEntities(entity, axisalignedbb.grow(d0, d0, d0));

    for (int j2 = 0; j2 < list.size(); j2++) {
      AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).h();

      if ((axisalignedbb1 != null) && (axisalignedbb1.a(axisalignedbb))) {
        this.P.add(axisalignedbb1);
      }

      axisalignedbb1 = entity.b_((Entity)list.get(j2));
      if ((axisalignedbb1 != null) && (axisalignedbb1.a(axisalignedbb))) {
        this.P.add(axisalignedbb1);
      }
    }

    return this.P;
  }

  public int a(float f) {
    float f1 = b(f);
    float f2 = 1.0F - (MathHelper.cos(f1 * 3.141593F * 2.0F) * 2.0F + 0.5F);

    if (f2 < 0.0F) {
      f2 = 0.0F;
    }

    if (f2 > 1.0F) {
      f2 = 1.0F;
    }

    f2 = 1.0F - f2;
    f2 = (float)(f2 * (1.0D - d(f) * 5.0F / 16.0D));
    f2 = (float)(f2 * (1.0D - c(f) * 5.0F / 16.0D));
    f2 = 1.0F - f2;
    return (int)(f2 * 11.0F);
  }

  public float b(float f) {
    return this.worldProvider.a(this.worldData.getTime(), f);
  }

  public int f(int i, int j) {
    return getChunkAtWorldCoords(i, j).d(i & 0xF, j & 0xF);
  }

  public int g(int i, int j) {
    Chunk chunk = getChunkAtWorldCoords(i, j);
    int k = chunk.g() + 16;

    i &= 15;

    for (j &= 15; k > 0; k--) {
      int l = chunk.getTypeId(i, k, j);

      if ((l != 0) && (Block.byId[l].material.isSolid()) && (Block.byId[l].material != Material.LEAVES)) {
        return k + 1;
      }
    }

    return -1;
  }

  public void c(int i, int j, int k, int l, int i1) {
    NextTickListEntry nextticklistentry = new NextTickListEntry(i, j, k, l);
    byte b0 = 8;

    if (this.a) {
      if (a(nextticklistentry.a - b0, nextticklistentry.b - b0, nextticklistentry.c - b0, nextticklistentry.a + b0, nextticklistentry.b + b0, nextticklistentry.c + b0)) {
        int j1 = getTypeId(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c);

        if ((j1 == nextticklistentry.d) && (j1 > 0)) {
          Block.byId[j1].a(this, nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, this.random);
        }
      }
    }
    else if (a(i - b0, j - b0, k - b0, i + b0, j + b0, k + b0)) {
      if (l > 0) {
        nextticklistentry.a(i1 + this.worldData.getTime());
      }

      if (!this.I.contains(nextticklistentry)) {
        this.I.add(nextticklistentry);
        this.H.add(nextticklistentry);
      }
    }
  }

  public void d(int i, int j, int k, int l, int i1)
  {
    NextTickListEntry nextticklistentry = new NextTickListEntry(i, j, k, l);

    if (l > 0) {
      nextticklistentry.a(i1 + this.worldData.getTime());
    }

    if (!this.I.contains(nextticklistentry)) {
      this.I.add(nextticklistentry);
      this.H.add(nextticklistentry);
    }
  }

  public void tickEntities()
  {
    for (int i = 0; i < this.e.size(); i++) {
      Entity entity = (Entity)this.e.unsafeGet(i);

      if (entity != null)
      {
        entity.F_();
        if (entity.dead) {
          this.e.remove(i--);
        }
      }
    }

    this.entityList.removeAll(this.G);

    for (i = 0; i < this.G.size(); i++) {
      Entity entity = (Entity)this.G.get(i);
      int j = entity.ca;
      int k = entity.cc;
      if ((entity.bZ) && (isChunkLoaded(j, k))) {
        getChunkAt(j, k).b(entity);
      }
    }

    for (i = 0; i < this.G.size(); i++) {
      d((Entity)this.G.get(i));
    }

    this.G.clear();

    for (i = 0; i < this.entityList.size(); i++) {
      Entity entity = (Entity)this.entityList.get(i);
      if (entity.vehicle != null) {
        if ((entity.vehicle.dead) || (entity.vehicle.passenger != entity))
        {
          entity.vehicle.passenger = null;
          entity.vehicle = null;
        }
      } else {
        if (!entity.dead) {
          playerJoinedWorld(entity);
        }

        if (entity.dead) {
          int j = entity.ca;
          int k = entity.cc;
          if ((entity.bZ) && (isChunkLoaded(j, k))) {
            getChunkAt(j, k).b(entity);
          }

          this.entityList.remove(i--);
          d(entity);
        }

      }

    }

    this.Q = true;
    Iterator iterator = this.tileEntityList.iterator();

    while (iterator.hasNext()) {
      TileEntity tileentity = (TileEntity)iterator.next();

      if ((!tileentity.l()) && (tileentity.world != null) && (isLoaded(tileentity.x, tileentity.y, tileentity.z))) {
        tileentity.q_();
      }

      if (tileentity.l()) {
        iterator.remove();
        if (isChunkLoaded(tileentity.x >> 4, tileentity.z >> 4)) {
          Chunk chunk = getChunkAt(tileentity.x >> 4, tileentity.z >> 4);

          if (chunk != null) {
            chunk.cleanChunkBlockTileEntity(tileentity.x & 0xF, tileentity.y, tileentity.z & 0xF);
          }
        }
      }
    }

    this.Q = false;
    if (!this.K.isEmpty()) {
      for (Iterator i$ = this.K.iterator(); i$.hasNext(); ) { Object tile = i$.next();

        ((TileEntity)tile).onChunkUnload();
      }

      this.tileEntityList.removeAll(this.K);
      this.K.clear();
    }

    if (!this.J.isEmpty()) {
      Iterator iterator1 = this.J.iterator();

      while (iterator1.hasNext()) {
        TileEntity tileentity1 = (TileEntity)iterator1.next();

        if (!tileentity1.l()) {
          if (!this.tileEntityList.contains(tileentity1)) {
            this.tileEntityList.add(tileentity1);
          }

        }
        else
        {
          if (isChunkLoaded(tileentity1.x >> 4, tileentity1.z >> 4)) {
            Chunk chunk1 = getChunkAt(tileentity1.x >> 4, tileentity1.z >> 4);

            if (chunk1 != null) {
              chunk1.cleanChunkBlockTileEntity(tileentity1.x & 0xF, tileentity1.y, tileentity1.z & 0xF);
            }

          }

          notify(tileentity1.x, tileentity1.y, tileentity1.z);
        }
      }

      this.J.clear();
    }
  }

  public void a(Collection collection)
  {
    List dest = this.Q ? this.J : this.tileEntityList;
    for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object entity = i$.next();

      if (((TileEntity)entity).canUpdate())
      {
        dest.add(entity);
      } }
  }

  public void playerJoinedWorld(Entity entity)
  {
    entityJoinedWorld(entity, true);
  }

  public void entityJoinedWorld(Entity entity, boolean flag) {
    int i = MathHelper.floor(entity.locX);
    int j = MathHelper.floor(entity.locZ);
    byte b0 = 32;

    if ((!flag) || (a(i - b0, 0, j - b0, i + b0, 0, j + b0)) || (ForgeHooks.canUpdateEntity(entity))) {
      entity.bL = entity.locX;
      entity.bM = entity.locY;
      entity.bN = entity.locZ;
      entity.lastYaw = entity.yaw;
      entity.lastPitch = entity.pitch;
      if ((flag) && (entity.bZ)) {
        if (entity.vehicle != null)
          entity.R();
        else {
          entity.F_();
        }

      }

      if ((Double.isNaN(entity.locX)) || (Double.isInfinite(entity.locX))) {
        entity.locX = entity.bL;
      }

      if ((Double.isNaN(entity.locY)) || (Double.isInfinite(entity.locY))) {
        entity.locY = entity.bM;
      }

      if ((Double.isNaN(entity.locZ)) || (Double.isInfinite(entity.locZ))) {
        entity.locZ = entity.bN;
      }

      if ((Double.isNaN(entity.pitch)) || (Double.isInfinite(entity.pitch))) {
        entity.pitch = entity.lastPitch;
      }

      if ((Double.isNaN(entity.yaw)) || (Double.isInfinite(entity.yaw))) {
        entity.yaw = entity.lastYaw;
      }

      int k = MathHelper.floor(entity.locX / 16.0D);
      int l = MathHelper.floor(entity.locY / 16.0D);
      int i1 = MathHelper.floor(entity.locZ / 16.0D);

      if ((!entity.bZ) || (entity.ca != k) || (entity.cb != l) || (entity.cc != i1)) {
        if ((entity.bZ) && (isChunkLoaded(entity.ca, entity.cc))) {
          getChunkAt(entity.ca, entity.cc).a(entity, entity.cb);
        }

        if (isChunkLoaded(k, i1)) {
          entity.bZ = true;
          getChunkAt(k, i1).a(entity);
        } else {
          entity.bZ = false;
        }

      }

      if ((flag) && (entity.bZ) && (entity.passenger != null))
        if ((!entity.passenger.dead) && (entity.passenger.vehicle == entity)) {
          playerJoinedWorld(entity.passenger);
        } else {
          entity.passenger.vehicle = null;
          entity.passenger = null;
        }
    }
  }

  public boolean containsEntity(AxisAlignedBB axisalignedbb)
  {
    List list = getEntities((Entity)null, axisalignedbb);

    for (int i = 0; i < list.size(); i++) {
      Entity entity = (Entity)list.get(i);

      if ((!entity.dead) && (entity.bf)) {
        return false;
      }
    }

    return true;
  }

  public boolean b(AxisAlignedBB axisalignedbb) {
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.d + 1.0D);
    int k = MathHelper.floor(axisalignedbb.b);
    int l = MathHelper.floor(axisalignedbb.e + 1.0D);
    int i1 = MathHelper.floor(axisalignedbb.c);
    int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

    if (axisalignedbb.a < 0.0D) {
      i--;
    }

    if (axisalignedbb.b < 0.0D) {
      k--;
    }

    if (axisalignedbb.c < 0.0D) {
      i1--;
    }

    for (int k1 = i; k1 < j; k1++) {
      for (int l1 = k; l1 < l; l1++) {
        for (int i2 = i1; i2 < j1; i2++) {
          Block block = Block.byId[getTypeId(k1, l1, i2)];

          if (block != null) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public boolean containsLiquid(AxisAlignedBB axisalignedbb) {
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.d + 1.0D);
    int k = MathHelper.floor(axisalignedbb.b);
    int l = MathHelper.floor(axisalignedbb.e + 1.0D);
    int i1 = MathHelper.floor(axisalignedbb.c);
    int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

    if (axisalignedbb.a < 0.0D) {
      i--;
    }

    if (axisalignedbb.b < 0.0D) {
      k--;
    }

    if (axisalignedbb.c < 0.0D) {
      i1--;
    }

    for (int k1 = i; k1 < j; k1++) {
      for (int l1 = k; l1 < l; l1++) {
        for (int i2 = i1; i2 < j1; i2++) {
          Block block = Block.byId[getTypeId(k1, l1, i2)];

          if ((block != null) && (block.material.isLiquid())) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public boolean d(AxisAlignedBB axisalignedbb) {
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.d + 1.0D);
    int k = MathHelper.floor(axisalignedbb.b);
    int l = MathHelper.floor(axisalignedbb.e + 1.0D);
    int i1 = MathHelper.floor(axisalignedbb.c);
    int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

    if (a(i, k, i1, j, l, j1)) {
      for (int k1 = i; k1 < j; k1++) {
        for (int l1 = k; l1 < l; l1++) {
          for (int i2 = i1; i2 < j1; i2++) {
            int j2 = getTypeId(k1, l1, i2);

            if ((j2 == Block.FIRE.id) || (j2 == Block.LAVA.id) || (j2 == Block.STATIONARY_LAVA.id)) {
              return true;
            }

            if ((j2 > 0) && (Block.byId[j2] != null) && (Block.byId[j2].isBlockBurning(this, k1, l1, i2)))
            {
              return true;
            }
          }
        }

      }

    }

    return false;
  }

  public boolean a(AxisAlignedBB axisalignedbb, Material material, Entity entity) {
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.d + 1.0D);
    int k = MathHelper.floor(axisalignedbb.b);
    int l = MathHelper.floor(axisalignedbb.e + 1.0D);
    int i1 = MathHelper.floor(axisalignedbb.c);
    int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

    if (!a(i, k, i1, j, l, j1)) {
      return false;
    }
    boolean flag = false;
    Vec3D vec3d = Vec3D.create(0.0D, 0.0D, 0.0D);

    for (int k1 = i; k1 < j; k1++) {
      for (int l1 = k; l1 < l; l1++) {
        for (int i2 = i1; i2 < j1; i2++) {
          Block block = Block.byId[getTypeId(k1, l1, i2)];

          if ((block != null) && (block.material == material)) {
            double d0 = l1 + 1 - BlockFluids.d(getData(k1, l1, i2));

            if (l >= d0) {
              flag = true;
              block.a(this, k1, l1, i2, entity, vec3d);
            }
          }
        }
      }
    }

    if (vec3d.c() > 0.0D) {
      vec3d = vec3d.b();
      double d1 = 0.014D;

      entity.motX += vec3d.a * d1;
      entity.motY += vec3d.b * d1;
      entity.motZ += vec3d.c * d1;
    }

    return flag;
  }

  public boolean a(AxisAlignedBB axisalignedbb, Material material)
  {
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.d + 1.0D);
    int k = MathHelper.floor(axisalignedbb.b);
    int l = MathHelper.floor(axisalignedbb.e + 1.0D);
    int i1 = MathHelper.floor(axisalignedbb.c);
    int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

    for (int k1 = i; k1 < j; k1++) {
      for (int l1 = k; l1 < l; l1++) {
        for (int i2 = i1; i2 < j1; i2++) {
          Block block = Block.byId[getTypeId(k1, l1, i2)];

          if ((block != null) && (block.material == material)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public boolean b(AxisAlignedBB axisalignedbb, Material material) {
    int i = MathHelper.floor(axisalignedbb.a);
    int j = MathHelper.floor(axisalignedbb.d + 1.0D);
    int k = MathHelper.floor(axisalignedbb.b);
    int l = MathHelper.floor(axisalignedbb.e + 1.0D);
    int i1 = MathHelper.floor(axisalignedbb.c);
    int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

    for (int k1 = i; k1 < j; k1++) {
      for (int l1 = k; l1 < l; l1++) {
        for (int i2 = i1; i2 < j1; i2++) {
          Block block = Block.byId[getTypeId(k1, l1, i2)];

          if ((block != null) && (block.material == material)) {
            int j2 = getData(k1, l1, i2);
            double d0 = l1 + 1;

            if (j2 < 8) {
              d0 = l1 + 1 - j2 / 8.0D;
            }

            if (d0 >= axisalignedbb.b) {
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  public Explosion explode(Entity entity, double d0, double d1, double d2, float f) {
    return createExplosion(entity, d0, d1, d2, f, false);
  }

  public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag) {
    Explosion explosion = new Explosion(this, entity, d0, d1, d2, f);

    explosion.a = flag;
    explosion.a();
    explosion.a(true);
    return explosion;
  }

  public float a(Vec3D vec3d, AxisAlignedBB axisalignedbb) {
    double d0 = 1.0D / ((axisalignedbb.d - axisalignedbb.a) * 2.0D + 1.0D);
    double d1 = 1.0D / ((axisalignedbb.e - axisalignedbb.b) * 2.0D + 1.0D);
    double d2 = 1.0D / ((axisalignedbb.f - axisalignedbb.c) * 2.0D + 1.0D);
    int i = 0;
    int j = 0;

    for (float f = 0.0F; f <= 1.0F; f = (float)(f + d0)) {
      for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1)) {
        for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2)) {
          double d3 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * f;
          double d4 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * f1;
          double d5 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * f2;

          if (a(Vec3D.create(d3, d4, d5), vec3d) == null) {
            i++;
          }

          j++;
        }
      }
    }

    return i / j;
  }

  public boolean douseFire(EntityHuman entityhuman, int i, int j, int k, int l) {
    if (l == 0) {
      j--;
    }

    if (l == 1) {
      j++;
    }

    if (l == 2) {
      k--;
    }

    if (l == 3) {
      k++;
    }

    if (l == 4) {
      i--;
    }

    if (l == 5) {
      i++;
    }

    if (getTypeId(i, j, k) == Block.FIRE.id) {
      a(entityhuman, 1004, i, j, k, 0);
      setTypeId(i, j, k, 0);
      return true;
    }
    return false;
  }

  public TileEntity getTileEntity(int i, int j, int k)
  {
    if (j >= 256) {
      return null;
    }
    Chunk chunk = getChunkAt(i >> 4, k >> 4);

    if (chunk == null) {
      return null;
    }
    TileEntity tileentity = chunk.e(i & 0xF, j, k & 0xF);

    if (tileentity == null) {
      Iterator iterator = this.J.iterator();

      while (iterator.hasNext()) {
        TileEntity tileentity1 = (TileEntity)iterator.next();

        if ((!tileentity1.l()) && (tileentity1.x == i) && (tileentity1.y == j) && (tileentity1.z == k)) {
          tileentity = tileentity1;
          break;
        }
      }
    }

    return tileentity;
  }

  public void setTileEntity(int i, int j, int k, TileEntity tileentity)
  {
    if ((tileentity == null) || (tileentity.l())) {
      return;
    }
    List dest = this.Q ? this.J : this.tileEntityList;
    if (tileentity.canUpdate())
    {
      synchronized (dest) {
        dest.add(tileentity);
      }

    }

    Chunk chunk = getChunkAt(i >> 4, k >> 4);

    if (chunk != null)
      chunk.a(i & 0xF, j, k & 0xF, tileentity);
  }

  public void q(int i, int j, int k)
  {
    Chunk chunk = getChunkAt(i >> 4, k >> 4);

    if (chunk != null)
      chunk.f(i & 0xF, j, k & 0xF);
  }

  public void a(TileEntity tileentity)
  {
    this.K.add(tileentity);
  }

  public boolean r(int i, int j, int k) {
    Block block = Block.byId[getTypeId(i, j, k)];

    return block == null ? false : block.a();
  }

  public boolean e(int i, int j, int k) {
    Block block = Block.byId[getTypeId(i, j, k)];
    return (block != null) && (block.isBlockNormalCube(this, i, j, k));
  }

  public boolean b(int i, int j, int k, boolean flag) {
    if ((i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000)) {
      Chunk chunk = this.chunkProvider.getOrCreateChunk(i >> 4, k >> 4);

      if ((chunk != null) && (!chunk.isEmpty())) {
        Block block = Block.byId[getTypeId(i, j, k)];

        return block == null ? false : e(i, j, k);
      }
      return flag;
    }

    return flag;
  }

  public void g()
  {
    int i = a(1.0F);

    if (i != this.f)
      this.f = i;
  }

  public void setSpawnFlags(boolean flag, boolean flag1)
  {
    this.allowMonsters = flag;
    this.allowAnimals = flag1;
  }

  public void doTick() {
    if ((getWorldData().isHardcore()) && (this.difficulty < 3)) {
      this.difficulty = 3;
    }

    this.worldProvider.c.b();
    i();

    if (everyoneDeeplySleeping()) {
      boolean flag = false;

      if (((!this.allowMonsters) || (this.difficulty < 1)) || 
        (!flag)) {
        long i = this.worldData.getTime() + 24000L;
        this.worldData.a(i - i % 24000L);
        u();
      }

    }

    long time = this.worldData.getTime();
    if (((this.allowMonsters) || (this.allowAnimals)) && ((this instanceof WorldServer)) && (getServer().getHandle().players.size() > 0)) {
      SpawnerCreature.spawnEntities(this, (this.allowMonsters) && (this.ticksPerMonsterSpawns != 0L) && (time % this.ticksPerMonsterSpawns == 0L), (this.allowAnimals) && (this.ticksPerAnimalSpawns != 0L) && (time % this.ticksPerAnimalSpawns == 0L));
    }

    this.chunkProvider.unloadChunks();
    int j = a(1.0F);

    if (j != this.f) {
      this.f = j;
    }

    long i = this.worldData.getTime() + 1L;
    if (i % this.p == 0L)
    {
      save(false, (IProgressUpdate)null);
    }

    this.worldData.a(i);

    a(false);

    l();

    this.villages.tick();
    this.O.a();
  }

  private void B()
  {
    if (this.worldData.hasStorm()) {
      this.j = 1.0F;
      if (this.worldData.isThundering())
        this.l = 1.0F;
    }
  }

  protected void i()
  {
    if (!this.worldProvider.e) {
      if (this.m > 0) {
        this.m -= 1;
      }

      int i = this.worldData.getThunderDuration();

      if (i <= 0) {
        if (this.worldData.isThundering())
          this.worldData.setThunderDuration(this.random.nextInt(12000) + 3600);
        else
          this.worldData.setThunderDuration(this.random.nextInt(168000) + 12000);
      }
      else {
        i--;
        this.worldData.setThunderDuration(i);
        if (i <= 0)
        {
          ThunderChangeEvent thunder = new ThunderChangeEvent(getWorld(), !this.worldData.isThundering());
          getServer().getPluginManager().callEvent(thunder);
          if (!thunder.isCancelled()) {
            this.worldData.setThundering(!this.worldData.isThundering());
          }
        }

      }

      int j = this.worldData.getWeatherDuration();

      if (j <= 0) {
        if (this.worldData.hasStorm())
          this.worldData.setWeatherDuration(this.random.nextInt(12000) + 12000);
        else
          this.worldData.setWeatherDuration(this.random.nextInt(168000) + 12000);
      }
      else {
        j--;
        this.worldData.setWeatherDuration(j);
        if (j <= 0)
        {
          WeatherChangeEvent weather = new WeatherChangeEvent(getWorld(), !this.worldData.hasStorm());
          getServer().getPluginManager().callEvent(weather);

          if (!weather.isCancelled()) {
            this.worldData.setStorm(!this.worldData.hasStorm());
          }
        }

      }

      this.i = this.j;
      if (this.worldData.hasStorm())
        this.j = (float)(this.j + 0.01D);
      else {
        this.j = (float)(this.j - 0.01D);
      }

      if (this.j < 0.0F) {
        this.j = 0.0F;
      }

      if (this.j > 1.0F) {
        this.j = 1.0F;
      }

      this.k = this.l;
      if (this.worldData.isThundering())
        this.l = (float)(this.l + 0.01D);
      else {
        this.l = (float)(this.l - 0.01D);
      }

      if (this.l < 0.0F) {
        this.l = 0.0F;
      }

      if (this.l > 1.0F)
        this.l = 1.0F;
    }
  }

  private void C()
  {
    WeatherChangeEvent weather = new WeatherChangeEvent(getWorld(), false);
    getServer().getPluginManager().callEvent(weather);

    ThunderChangeEvent thunder = new ThunderChangeEvent(getWorld(), false);
    getServer().getPluginManager().callEvent(thunder);
    if (!weather.isCancelled()) {
      this.worldData.setWeatherDuration(0);
      this.worldData.setStorm(false);
    }
    if (!thunder.isCancelled()) {
      this.worldData.setThunderDuration(0);
      this.worldData.setThundering(false);
    }
  }

  public void j()
  {
    this.worldData.setWeatherDuration(1);
  }

  protected void k()
  {
    for (int i = 0; i < this.players.size(); i++) {
      EntityHuman entityhuman = (EntityHuman)this.players.get(i);
      int j = MathHelper.floor(entityhuman.locX / 16.0D);
      int k = MathHelper.floor(entityhuman.locZ / 16.0D);
      byte b0 = 7;

      for (int l = -b0; l <= b0; l++) {
        for (int i1 = -b0; i1 <= b0; i1++) {
          this.chunkTickList.add(LongHash.toLong(l + j, i1 + k));
        }
      }
    }

    ForgeHooks.addActiveChunks(this, this.chunkTickList);

    if (this.R > 0) {
      this.R -= 1;
    }

    if (!this.players.isEmpty()) {
      i = this.random.nextInt(this.players.size());
      EntityHuman entityhuman = (EntityHuman)this.players.get(i);
      int j = MathHelper.floor(entityhuman.locX) + this.random.nextInt(11) - 5;
      int k = MathHelper.floor(entityhuman.locY) + this.random.nextInt(11) - 5;
      int j1 = MathHelper.floor(entityhuman.locZ) + this.random.nextInt(11) - 5;

      v(j, k, j1);
    }
  }

  protected void a(int i, int j, Chunk chunk)
  {
    chunk.j();

    if (this.R == 0) {
      this.g = (this.g * 3 + 1013904223);
      int k = this.g >> 2;
      int l = k & 0xF;
      int i1 = k >> 8 & 0xF;
      int j1 = k >> 16 & 0xFF;
      int k1 = chunk.getTypeId(l, j1, i1);

      l += i;
      i1 += j;
      if ((k1 == 0) && (m(l, j1, i1) <= this.random.nextInt(8)) && (a(EnumSkyBlock.SKY, l, j1, i1) <= 0)) {
        EntityHuman entityhuman = findNearbyPlayer(l + 0.5D, j1 + 0.5D, i1 + 0.5D, 8.0D);

        if ((entityhuman != null) && (entityhuman.e(l + 0.5D, j1 + 0.5D, i1 + 0.5D) > 4.0D)) {
          makeSound(l + 0.5D, j1 + 0.5D, i1 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
          this.R = (this.random.nextInt(12000) + 6000);
        }
      }

    }

    chunk.n();
  }

  protected void l() {
    k();
    int i = 0;
    int j = 0;

    for (long chunkCoord : this.chunkTickList.popAll()) {
      int chunkX = LongHash.msw(chunkCoord);
      int chunkZ = LongHash.lsw(chunkCoord);

      int k = chunkX * 16;
      int l = chunkZ * 16;

      Chunk chunk = getChunkAt(chunkX, chunkZ);

      a(k, l, chunk);

      if ((this.random.nextInt(100000) == 0) && (x()) && (w())) {
        this.g = (this.g * 3 + 1013904223);
        int i1 = this.g >> 2;
        int j1 = k + (i1 & 0xF);
        int k1 = l + (i1 >> 8 & 0xF);
        int l1 = f(j1, k1);
        if (y(j1, l1, k1)) {
          strikeLightning(new EntityWeatherLighting(this, j1, l1, k1));
          this.m = 2;
        }

      }

      if (this.random.nextInt(16) == 0) {
        this.g = (this.g * 3 + 1013904223);
        int i1 = this.g >> 2;
        int j1 = i1 & 0xF;
        int k1 = i1 >> 8 & 0xF;
        int l1 = f(j1 + k, k1 + l);
        if (t(j1 + k, l1 - 1, k1 + l))
        {
          BlockState blockState = getWorld().getBlockAt(j1 + k, l1 - 1, k1 + l).getState();
          blockState.setTypeId(Block.ICE.id);

          BlockFormEvent iceBlockForm = new BlockFormEvent(blockState.getBlock(), blockState);
          getServer().getPluginManager().callEvent(iceBlockForm);
          if (!iceBlockForm.isCancelled()) {
            blockState.update(true);
          }

        }

        if ((x()) && (u(j1 + k, l1, k1 + l)))
        {
          BlockState blockState = getWorld().getBlockAt(j1 + k, l1, k1 + l).getState();
          blockState.setTypeId(Block.SNOW.id);

          BlockFormEvent snow = new BlockFormEvent(blockState.getBlock(), blockState);
          getServer().getPluginManager().callEvent(snow);
          if (!snow.isCancelled()) {
            blockState.update(true);
          }

        }

      }

      ChunkSection[] achunksection = chunk.h();

      int j1 = achunksection.length;

      for (int k1 = 0; k1 < j1; k1++) {
        ChunkSection chunksection = achunksection[k1];

        if ((chunksection != null) && (chunksection.b()))
          for (int i2 = 0; i2 < 3; i2++) {
            this.g = (this.g * 3 + 1013904223);
            int j2 = this.g >> 2;
            int k2 = j2 & 0xF;
            int l2 = j2 >> 8 & 0xF;
            int i3 = j2 >> 16 & 0xF;
            int j3 = chunksection.a(k2, i3, l2);

            j++;
            Block block = Block.byId[j3];

            if ((block != null) && (block.n())) {
              i++;
              block.a(this, k2 + k, i3 + chunksection.c(), l2 + l, this.random);
            }
          }
      }
    }
  }

  public boolean s(int i, int j, int k)
  {
    return c(i, j, k, false);
  }

  public boolean t(int i, int j, int k) {
    return c(i, j, k, true);
  }

  public boolean c(int i, int j, int k, boolean flag) {
    BiomeBase biomebase = getBiome(i, k);
    float f = biomebase.i();

    if (f > 0.15F) {
      return false;
    }
    if ((j >= 0) && (j < 256) && (a(EnumSkyBlock.BLOCK, i, j, k) < 10)) {
      int l = getTypeId(i, j, k);

      if (((l == Block.STATIONARY_WATER.id) || (l == Block.WATER.id)) && (getData(i, j, k) == 0)) {
        if (!flag) {
          return true;
        }

        boolean flag1 = true;

        if ((flag1) && (getMaterial(i - 1, j, k) != Material.WATER)) {
          flag1 = false;
        }

        if ((flag1) && (getMaterial(i + 1, j, k) != Material.WATER)) {
          flag1 = false;
        }

        if ((flag1) && (getMaterial(i, j, k - 1) != Material.WATER)) {
          flag1 = false;
        }

        if ((flag1) && (getMaterial(i, j, k + 1) != Material.WATER)) {
          flag1 = false;
        }

        if (!flag1) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean u(int i, int j, int k)
  {
    BiomeBase biomebase = getBiome(i, k);
    float f = biomebase.i();

    if (f > 0.15F) {
      return false;
    }
    if ((j >= 0) && (j < 256) && (a(EnumSkyBlock.BLOCK, i, j, k) < 10)) {
      int l = getTypeId(i, j - 1, k);
      int i1 = getTypeId(i, j, k);

      if ((i1 == 0) && (Block.SNOW.canPlace(this, i, j, k)) && (l != 0) && (l != Block.ICE.id) && (Block.byId[l].material.isSolid())) {
        return true;
      }
    }

    return false;
  }

  public void v(int i, int j, int k)
  {
    if (!this.worldProvider.e) {
      b(EnumSkyBlock.SKY, i, j, k);
    }

    b(EnumSkyBlock.BLOCK, i, j, k);
  }

  private int c(int i, int j, int k, int l, int i1, int j1) {
    int k1 = 0;

    if (isChunkLoaded(j, k, l)) {
      k1 = 15;
    } else {
      if (j1 == 0) {
        j1 = 1;
      }

      int l1 = a(EnumSkyBlock.SKY, j - 1, k, l) - j1;
      int i2 = a(EnumSkyBlock.SKY, j + 1, k, l) - j1;
      int j2 = a(EnumSkyBlock.SKY, j, k - 1, l) - j1;
      int k2 = a(EnumSkyBlock.SKY, j, k + 1, l) - j1;
      int l2 = a(EnumSkyBlock.SKY, j, k, l - 1) - j1;
      int i3 = a(EnumSkyBlock.SKY, j, k, l + 1) - j1;

      if (l1 > k1) {
        k1 = l1;
      }

      if (i2 > k1) {
        k1 = i2;
      }

      if (j2 > k1) {
        k1 = j2;
      }

      if (k2 > k1) {
        k1 = k2;
      }

      if (l2 > k1) {
        k1 = l2;
      }

      if (i3 > k1) {
        k1 = i3;
      }
    }

    return k1;
  }

  private int d(int i, int j, int k, int l, int i1, int j1) {
    int k1 = (i1 == 0) || (Block.byId[i1] == null) ? 0 : Block.byId[i1].getLightValue(this, j, k, l);
    int l1 = a(EnumSkyBlock.BLOCK, j - 1, k, l) - j1;
    int i2 = a(EnumSkyBlock.BLOCK, j + 1, k, l) - j1;
    int j2 = a(EnumSkyBlock.BLOCK, j, k - 1, l) - j1;
    int k2 = a(EnumSkyBlock.BLOCK, j, k + 1, l) - j1;
    int l2 = a(EnumSkyBlock.BLOCK, j, k, l - 1) - j1;
    int i3 = a(EnumSkyBlock.BLOCK, j, k, l + 1) - j1;

    if (l1 > k1) {
      k1 = l1;
    }

    if (i2 > k1) {
      k1 = i2;
    }

    if (j2 > k1) {
      k1 = j2;
    }

    if (k2 > k1) {
      k1 = k2;
    }

    if (l2 > k1) {
      k1 = l2;
    }

    if (i3 > k1) {
      k1 = i3;
    }

    return k1;
  }

  public void b(EnumSkyBlock enumskyblock, int i, int j, int k) {
    if ((areChunksLoaded(i, j, k, 17)) && (!this.suppressPhysics)) {
      int l = 0;
      int i1 = 0;

      int j1 = a(enumskyblock, i, j, k);
      boolean flag = false;
      int k1 = getTypeId(i, j, k);
      int l1 = f(i, j, k);

      if (l1 == 0) {
        l1 = 1;
      }

      boolean flag1 = false;
      int i2;
      int i2;
      if (enumskyblock == EnumSkyBlock.SKY)
        i2 = c(j1, i, j, k, k1, l1);
      else {
        i2 = d(j1, i, j, k, k1, l1);
      }

      if (i2 > j1) {
        this.E[(i1++)] = 133152;
      } else if (i2 < j1) {
        if (enumskyblock != EnumSkyBlock.BLOCK);
        this.E[(i1++)] = (133152 + (j1 << 18));

        while (l < i1) {
          int j2 = this.E[(l++)];
          k1 = (j2 & 0x3F) - 32 + i;
          l1 = (j2 >> 6 & 0x3F) - 32 + j;
          i2 = (j2 >> 12 & 0x3F) - 32 + k;
          int k2 = j2 >> 18 & 0xF;
          int l2 = a(enumskyblock, k1, l1, i2);
          if (l2 == k2) {
            a(enumskyblock, k1, l1, i2, 0);
            if (k2 > 0) {
              int i3 = k1 - i;
              int k3 = l1 - j;
              int j3 = i2 - k;
              if (i3 < 0) {
                i3 = -i3;
              }

              if (k3 < 0) {
                k3 = -k3;
              }

              if (j3 < 0) {
                j3 = -j3;
              }

              if (i3 + k3 + j3 < 17) {
                for (int l3 = 0; l3 < 6; l3++) {
                  int i4 = l3 % 2 * 2 - 1;
                  int j4 = k1 + l3 / 2 % 3 / 2 * i4;
                  int k4 = l1 + (l3 / 2 + 1) % 3 / 2 * i4;
                  int l4 = i2 + (l3 / 2 + 2) % 3 / 2 * i4;

                  l2 = a(enumskyblock, j4, k4, l4);
                  int i5 = Block.lightBlock[getTypeId(j4, k4, l4)];

                  if (i5 == 0) {
                    i5 = 1;
                  }

                  if ((l2 == k2 - i5) && (i1 < this.E.length)) {
                    this.E[(i1++)] = (j4 - i + 32 + (k4 - j + 32 << 6) + (l4 - k + 32 << 12) + (k2 - i5 << 18));
                  }
                }
              }
            }
          }
        }

        l = 0;
      }

      while (l < i1) {
        j1 = this.E[(l++)];
        int j5 = (j1 & 0x3F) - 32 + i;

        int j2 = (j1 >> 6 & 0x3F) - 32 + j;
        k1 = (j1 >> 12 & 0x3F) - 32 + k;
        l1 = a(enumskyblock, j5, j2, k1);
        i2 = getTypeId(j5, j2, k1);
        int k2 = Block.lightBlock[i2];
        if (k2 == 0) {
          k2 = 1;
        }

        boolean flag2 = false;
        int l2;
        int l2;
        if (enumskyblock == EnumSkyBlock.SKY)
          l2 = c(l1, j5, j2, k1, i2, k2);
        else {
          l2 = d(l1, j5, j2, k1, i2, k2);
        }

        if (l2 != l1) {
          a(enumskyblock, j5, j2, k1, l2);
          if (l2 > l1) {
            int i3 = j5 - i;
            int k3 = j2 - j;
            int j3 = k1 - k;
            if (i3 < 0) {
              i3 = -i3;
            }

            if (k3 < 0) {
              k3 = -k3;
            }

            if (j3 < 0) {
              j3 = -j3;
            }

            if ((i3 + k3 + j3 < 17) && (i1 < this.E.length - 6)) {
              if (a(enumskyblock, j5 - 1, j2, k1) < l2) {
                this.E[(i1++)] = (j5 - 1 - i + 32 + (j2 - j + 32 << 6) + (k1 - k + 32 << 12));
              }

              if (a(enumskyblock, j5 + 1, j2, k1) < l2) {
                this.E[(i1++)] = (j5 + 1 - i + 32 + (j2 - j + 32 << 6) + (k1 - k + 32 << 12));
              }

              if (a(enumskyblock, j5, j2 - 1, k1) < l2) {
                this.E[(i1++)] = (j5 - i + 32 + (j2 - 1 - j + 32 << 6) + (k1 - k + 32 << 12));
              }

              if (a(enumskyblock, j5, j2 + 1, k1) < l2) {
                this.E[(i1++)] = (j5 - i + 32 + (j2 + 1 - j + 32 << 6) + (k1 - k + 32 << 12));
              }

              if (a(enumskyblock, j5, j2, k1 - 1) < l2) {
                this.E[(i1++)] = (j5 - i + 32 + (j2 - j + 32 << 6) + (k1 - 1 - k + 32 << 12));
              }

              if (a(enumskyblock, j5, j2, k1 + 1) < l2)
                this.E[(i1++)] = (j5 - i + 32 + (j2 - j + 32 << 6) + (k1 + 1 - k + 32 << 12));
            }
          }
        }
      }
    }
  }

  public boolean a(boolean flag)
  {
    int i = this.H.size();

    if (i != this.I.size()) {
      throw new IllegalStateException("TickNextTick list out of synch");
    }
    if (i > 1000)
    {
      if (i > 20000)
        i /= 20;
      else {
        i = 1000;
      }

    }

    for (int j = 0; j < i; j++) {
      NextTickListEntry nextticklistentry = (NextTickListEntry)this.H.first();

      if ((!flag) && (nextticklistentry.e > this.worldData.getTime()))
      {
        break;
      }
      this.H.remove(nextticklistentry);
      this.I.remove(nextticklistentry);
      byte b0 = 8;

      if (a(nextticklistentry.a - b0, nextticklistentry.b - b0, nextticklistentry.c - b0, nextticklistentry.a + b0, nextticklistentry.b + b0, nextticklistentry.c + b0)) {
        int k = getTypeId(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c);

        if ((k == nextticklistentry.d) && (k > 0)) {
          Block.byId[k].a(this, nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, this.random);
        }
      }
    }

    return this.H.size() != 0;
  }

  public List a(Chunk chunk, boolean flag)
  {
    ArrayList arraylist = null;
    ChunkCoordIntPair chunkcoordintpair = chunk.k();
    int i = chunkcoordintpair.x << 4;
    int j = i + 16;
    int k = chunkcoordintpair.z << 4;
    int l = k + 16;
    Iterator iterator = this.I.iterator();

    while (iterator.hasNext()) {
      NextTickListEntry nextticklistentry = (NextTickListEntry)iterator.next();

      if ((nextticklistentry.a >= i) && (nextticklistentry.a < j) && (nextticklistentry.c >= k) && (nextticklistentry.c < l)) {
        if (flag) {
          this.H.remove(nextticklistentry);
          iterator.remove();
        }

        if (arraylist == null) {
          arraylist = new ArrayList();
        }

        arraylist.add(nextticklistentry);
      }
    }

    return arraylist;
  }

  public List getEntities(Entity entity, AxisAlignedBB axisalignedbb) {
    this.S.clear();
    int i = MathHelper.floor((axisalignedbb.a - MAX_ENTITY_RADIUS) / 16.0D);
    int j = MathHelper.floor((axisalignedbb.d + MAX_ENTITY_RADIUS) / 16.0D);
    int k = MathHelper.floor((axisalignedbb.c - MAX_ENTITY_RADIUS) / 16.0D);
    int l = MathHelper.floor((axisalignedbb.f + MAX_ENTITY_RADIUS) / 16.0D);

    for (int i1 = i; i1 <= j; i1++) {
      for (int j1 = k; j1 <= l; j1++) {
        if (isChunkLoaded(i1, j1)) {
          getChunkAt(i1, j1).a(entity, axisalignedbb, this.S);
        }
      }
    }

    return this.S;
  }

  public List a(Class oclass, AxisAlignedBB axisalignedbb) {
    int i = MathHelper.floor((axisalignedbb.a - MAX_ENTITY_RADIUS) / 16.0D);
    int j = MathHelper.floor((axisalignedbb.d + MAX_ENTITY_RADIUS) / 16.0D);
    int k = MathHelper.floor((axisalignedbb.c - MAX_ENTITY_RADIUS) / 16.0D);
    int l = MathHelper.floor((axisalignedbb.f + MAX_ENTITY_RADIUS) / 16.0D);
    ArrayList arraylist = new ArrayList();

    for (int i1 = i; i1 <= j; i1++) {
      for (int j1 = k; j1 <= l; j1++) {
        if (isChunkLoaded(i1, j1)) {
          getChunkAt(i1, j1).a(oclass, axisalignedbb, arraylist);
        }
      }
    }

    return arraylist;
  }

  public Entity a(Class oclass, AxisAlignedBB axisalignedbb, Entity entity) {
    List list = a(oclass, axisalignedbb);
    Entity entity1 = null;
    double d0 = 1.7976931348623157E+308D;
    Iterator iterator = list.iterator();

    while (iterator.hasNext()) {
      Entity entity2 = (Entity)iterator.next();

      if (entity2 != entity) {
        double d1 = entity.j(entity2);

        if (d1 <= d0) {
          entity1 = entity2;
          d0 = d1;
        }
      }
    }

    return entity1;
  }

  public void b(int i, int j, int k, TileEntity tileentity) {
    if (isLoaded(i, j, k)) {
      getChunkAtWorldCoords(i, k).e();
    }

    for (int l = 0; l < this.u.size(); l++)
      ((IWorldAccess)this.u.get(l)).a(i, j, k, tileentity);
  }

  public int a(Class oclass)
  {
    int i = 0;

    for (int j = 0; j < this.entityList.size(); j++) {
      Entity entity = (Entity)this.entityList.get(j);

      if (oclass.isAssignableFrom(entity.getClass())) {
        i++;
      }
    }

    return i;
  }

  public void a(List list)
  {
    Entity entity = null;
    for (int i = 0; i < list.size(); i++) {
      entity = (Entity)list.get(i);
      if (entity != null)
      {
        this.entityList.add(entity);

        c((Entity)list.get(i));
      }
    }
  }

  public void b(List list) { this.G.addAll(list); }

  public boolean mayPlace(int i, int j, int k, int l, boolean flag, int i1)
  {
    int j1 = getTypeId(j, k, l);
    Block block = Block.byId[j1];
    Block block1 = Block.byId[i];
    AxisAlignedBB axisalignedbb = block1.e(this, j, k, l);

    if (flag)
      axisalignedbb = null;
    boolean defaultReturn;
    boolean defaultReturn;
    if ((axisalignedbb != null) && (!containsEntity(axisalignedbb))) {
      defaultReturn = false;
    } else {
      if ((block != null) && ((block == Block.WATER) || (block == Block.STATIONARY_WATER) || (block == Block.LAVA) || (block == Block.STATIONARY_LAVA) || (block == Block.FIRE) || (block.material.isReplacable()))) {
        block = null;
      }

      if ((block != null) && (block.isBlockReplaceable(this, j, k, l))) {
        block = null;
      }

      defaultReturn = (i > 0) && (block == null) && (block1.canPlace(this, j, k, l, i1));
    }

    BlockCanBuildEvent event = new BlockCanBuildEvent(getWorld().getBlockAt(j, k, l), i, defaultReturn);
    getServer().getPluginManager().callEvent(event);

    return event.isBuildable();
  }

  public PathEntity findPath(Entity entity, Entity entity1, float f, boolean flag, boolean flag1, boolean flag2, boolean flag3)
  {
    int i = MathHelper.floor(entity.locX);
    int j = MathHelper.floor(entity.locY + 1.0D);
    int k = MathHelper.floor(entity.locZ);
    int l = (int)(f + 16.0F);
    int i1 = i - l;
    int j1 = j - l;
    int k1 = k - l;
    int l1 = i + l;
    int i2 = j + l;
    int j2 = k + l;
    ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2);
    PathEntity pathentity = new Pathfinder(chunkcache, flag, flag1, flag2, flag3).a(entity, entity1, f);

    return pathentity;
  }

  public PathEntity a(Entity entity, int i, int j, int k, float f, boolean flag, boolean flag1, boolean flag2, boolean flag3)
  {
    int l = MathHelper.floor(entity.locX);
    int i1 = MathHelper.floor(entity.locY);
    int j1 = MathHelper.floor(entity.locZ);
    int k1 = (int)(f + 8.0F);
    int l1 = l - k1;
    int i2 = i1 - k1;
    int j2 = j1 - k1;
    int k2 = l + k1;
    int l2 = i1 + k1;
    int i3 = j1 + k1;
    ChunkCache chunkcache = new ChunkCache(this, l1, i2, j2, k2, l2, i3);
    PathEntity pathentity = new Pathfinder(chunkcache, flag, flag1, flag2, flag3).a(entity, i, j, k, f);

    return pathentity;
  }

  public boolean isBlockFacePowered(int i, int j, int k, int l) {
    int i1 = getTypeId(i, j, k);

    return i1 == 0 ? false : Block.byId[i1].d(this, i, j, k, l);
  }

  public boolean isBlockPowered(int i, int j, int k) {
    return isBlockFacePowered(i - 1, j, k, 4) ? true : isBlockFacePowered(i, j, k + 1, 3) ? true : isBlockFacePowered(i, j, k - 1, 2) ? true : isBlockFacePowered(i, j + 1, k, 1) ? true : isBlockFacePowered(i, j - 1, k, 0) ? true : isBlockFacePowered(i + 1, j, k, 5);
  }

  public boolean isBlockFaceIndirectlyPowered(int i, int j, int k, int l) {
    if (e(i, j, k)) {
      return isBlockPowered(i, j, k);
    }
    int i1 = getTypeId(i, j, k);

    return i1 == 0 ? false : Block.byId[i1].a(this, i, j, k, l);
  }

  public boolean isBlockIndirectlyPowered(int i, int j, int k)
  {
    return isBlockFaceIndirectlyPowered(i - 1, j, k, 4) ? true : isBlockFaceIndirectlyPowered(i, j, k + 1, 3) ? true : isBlockFaceIndirectlyPowered(i, j, k - 1, 2) ? true : isBlockFaceIndirectlyPowered(i, j + 1, k, 1) ? true : isBlockFaceIndirectlyPowered(i, j - 1, k, 0) ? true : isBlockFaceIndirectlyPowered(i + 1, j, k, 5);
  }

  public EntityHuman findNearbyPlayer(Entity entity, double d0) {
    return findNearbyPlayer(entity.locX, entity.locY, entity.locZ, d0);
  }

  public EntityHuman findNearbyPlayer(double d0, double d1, double d2, double d3) {
    double d4 = -1.0D;
    EntityHuman entityhuman = null;

    for (int i = 0; i < this.players.size(); i++) {
      EntityHuman entityhuman1 = (EntityHuman)this.players.get(i);

      if ((entityhuman1 != null) && (!entityhuman1.dead))
      {
        double d5 = entityhuman1.e(d0, d1, d2);

        if (((d3 < 0.0D) || (d5 < d3 * d3)) && ((d4 == -1.0D) || (d5 < d4))) {
          d4 = d5;
          entityhuman = entityhuman1;
        }
      }
    }
    return entityhuman;
  }

  public EntityHuman a(double d0, double d1, double d2) {
    double d3 = -1.0D;
    EntityHuman entityhuman = null;

    for (int i = 0; i < this.players.size(); i++) {
      EntityHuman entityhuman1 = (EntityHuman)this.players.get(i);
      double d4 = entityhuman1.e(d0, entityhuman1.locY, d1);

      if (((d2 < 0.0D) || (d4 < d2 * d2)) && ((d3 == -1.0D) || (d4 < d3))) {
        d3 = d4;
        entityhuman = entityhuman1;
      }
    }

    return entityhuman;
  }

  public EntityHuman findNearbyVulnerablePlayer(Entity entity, double d0) {
    return findNearbyVulnerablePlayer(entity.locX, entity.locY, entity.locZ, d0);
  }

  public EntityHuman findNearbyVulnerablePlayer(double d0, double d1, double d2, double d3) {
    double d4 = -1.0D;
    EntityHuman entityhuman = null;

    for (int i = 0; i < this.players.size(); i++) {
      EntityHuman entityhuman1 = (EntityHuman)this.players.get(i);

      if ((entityhuman1 != null) && (!entityhuman1.dead))
      {
        if (!entityhuman1.abilities.isInvulnerable) {
          double d5 = entityhuman1.e(d0, d1, d2);

          if (((d3 < 0.0D) || (d5 < d3 * d3)) && ((d4 == -1.0D) || (d5 < d4))) {
            d4 = d5;
            entityhuman = entityhuman1;
          }
        }
      }
    }
    return entityhuman;
  }

  public EntityHuman a(String s) {
    for (int i = 0; i < this.players.size(); i++) {
      if (s.equals(((EntityHuman)this.players.get(i)).name)) {
        return (EntityHuman)this.players.get(i);
      }
    }

    return null;
  }

  public void m() {
    this.dataManager.checkSession();
  }

  public void setTime(long i) {
    this.worldData.a(i);
  }

  public void setTimeAndFixTicklists(long i) {
    long j = i - this.worldData.getTime();
    NextTickListEntry nextticklistentry;
    for (Iterator iterator = this.I.iterator(); iterator.hasNext(); nextticklistentry.e += j) {
      nextticklistentry = (NextTickListEntry)iterator.next();
    }

    setTime(i);
  }

  public long getSeed() {
    return this.worldData.getSeed();
  }

  public long getTime() {
    return this.worldData.getTime();
  }

  public ChunkCoordinates getSpawn() {
    return new ChunkCoordinates(this.worldData.c(), this.worldData.d(), this.worldData.e());
  }

  public boolean a(EntityHuman entityhuman, int i, int j, int k) {
    return true;
  }
  public void broadcastEntityEffect(Entity entity, byte b0) {
  }

  public IChunkProvider q() {
    return this.chunkProvider;
  }

  public void playNote(int i, int j, int k, int l, int i1) {
    int j1 = getTypeId(i, j, k);

    if (j1 > 0)
      Block.byId[j1].a(this, i, j, k, l, i1);
  }

  public IDataManager getDataManager()
  {
    return this.dataManager;
  }

  public WorldData getWorldData() {
    return this.worldData;
  }

  public void everyoneSleeping() {
    this.N = (!this.players.isEmpty());
    Iterator iterator = this.players.iterator();

    while (iterator.hasNext()) {
      EntityHuman entityhuman = (EntityHuman)iterator.next();

      if ((!entityhuman.isSleeping()) && (!entityhuman.fauxSleeping)) {
        this.N = false;
        break;
      }
    }
  }

  public void checkSleepStatus()
  {
    if (!this.isStatic)
      everyoneSleeping();
  }

  protected void u()
  {
    this.N = false;
    Iterator iterator = this.players.iterator();

    while (iterator.hasNext()) {
      EntityHuman entityhuman = (EntityHuman)iterator.next();

      if (entityhuman.isSleeping()) {
        entityhuman.a(false, false, true);
      }
    }

    C();
  }

  public boolean everyoneDeeplySleeping() {
    if ((this.N) && (!this.isStatic)) {
      Iterator iterator = this.players.iterator();

      boolean foundActualSleepers = false;
      EntityHuman entityhuman;
      do
      {
        if (!iterator.hasNext())
        {
          return foundActualSleepers;
        }

        entityhuman = (EntityHuman)iterator.next();

        if (entityhuman.isDeeplySleeping())
          foundActualSleepers = true;
      }
      while ((entityhuman.isDeeplySleeping()) || (entityhuman.fauxSleeping));

      return false;
    }
    return false;
  }

  public float c(float f)
  {
    return (this.k + (this.l - this.k) * f) * d(f);
  }

  public float d(float f) {
    return this.i + (this.j - this.i) * f;
  }

  public boolean w() {
    return c(1.0F) > 0.9D;
  }

  public boolean x() {
    return d(1.0F) > 0.2D;
  }

  public boolean y(int i, int j, int k) {
    if (!x())
      return false;
    if (!isChunkLoaded(i, j, k))
      return false;
    if (f(i, k) > j) {
      return false;
    }
    BiomeBase biomebase = getBiome(i, k);

    return biomebase.c() ? false : biomebase.d();
  }

  public boolean z(int i, int j, int k)
  {
    BiomeBase biomebase = getBiome(i, k);

    return biomebase.e();
  }

  public void a(String s, WorldMapBase worldmapbase) {
    this.worldMaps.a(s, worldmapbase);
  }

  public WorldMapBase a(Class oclass, String s) {
    return this.worldMaps.get(oclass, s);
  }

  public int b(String s) {
    return this.worldMaps.a(s);
  }

  public void triggerEffect(int i, int j, int k, int l, int i1) {
    a((EntityHuman)null, i, j, k, l, i1);
  }

  public void a(EntityHuman entityhuman, int i, int j, int k, int l, int i1) {
    for (int j1 = 0; j1 < this.u.size(); j1++)
      ((IWorldAccess)this.u.get(j1)).a(entityhuman, i, j, k, l, i1);
  }

  public int getHeight()
  {
    return this.worldProvider.e ? 128 : 256;
  }

  public Random A(int i, int j, int k) {
    long l = i * 341873128712L + j * 132897987541L + getWorldData().getSeed() + k;

    this.random.setSeed(l);
    return this.random;
  }

  public boolean updateLights() {
    return false;
  }

  public BiomeMeta a(EnumCreatureType enumcreaturetype, int i, int j, int k) {
    List list = q().getMobsFor(enumcreaturetype, i, j, k);

    return (list != null) && (!list.isEmpty()) ? (BiomeMeta)WeightedRandom.a(this.random, list) : null;
  }

  public ChunkPosition b(String s, int i, int j, int k) {
    return q().findNearestMapFeature(this, s, i, j, k);
  }

  public UUID getUUID()
  {
    return this.dataManager.getUUID();
  }

  public void addTileEntity(TileEntity entity)
  {
    List dest = this.Q ? this.J : this.tileEntityList;
    if (entity.canUpdate())
    {
      synchronized (dest) {
        dest.add(entity);
      }
    }
  }

  public boolean isBlockSolidOnSide(int x, int y, int z, int side)
  {
    Block block = Block.byId[getTypeId(x, y, z)];
    if (block == null) {
      return false;
    }
    return block.isBlockSolidOnSide(this, x, y, z, side);
  }
}

