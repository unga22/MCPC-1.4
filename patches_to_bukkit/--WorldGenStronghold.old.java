package net.minecraft.server;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WorldGenStronghold extends StructureGenerator
{
  private BiomeBase[] a = { BiomeBase.DESERT, BiomeBase.FOREST, BiomeBase.EXTREME_HILLS, BiomeBase.SWAMPLAND, BiomeBase.TAIGA, BiomeBase.ICE_PLAINS, BiomeBase.ICE_MOUNTAINS, BiomeBase.DESERT_HILLS, BiomeBase.FOREST_HILLS, BiomeBase.SMALL_MOUNTAINS, BiomeBase.JUNGLE, BiomeBase.JUNGLE_HILLS };
  private boolean f;
  private ChunkCoordIntPair[] g = new ChunkCoordIntPair[3];

  protected boolean a(int paramInt1, int paramInt2)
  {
    Object localObject1;
    if (!this.f)
    {
      localObject1 = new Random();

      ((Random)localObject1).setSeed(this.d.getSeed());

      double d1 = ((Random)localObject1).nextDouble() * 3.141592653589793D * 2.0D;

      for (int i = 0; i < this.g.length; i++) {
        double d2 = (1.25D + ((Random)localObject1).nextDouble()) * 32.0D;
        int j = (int)Math.round(Math.cos(d1) * d2);
        int k = (int)Math.round(Math.sin(d1) * d2);

        ArrayList localArrayList = new ArrayList();
        for (Object localObject4 : this.a) {
          localArrayList.add(localObject4);
        }

        ??? = this.d.getWorldChunkManager().a((j << 4) + 8, (k << 4) + 8, 112, localArrayList, (Random)localObject1);
        if (??? != null) {
          j = ((ChunkPosition)???).x >> 4;
          k = ((ChunkPosition)???).z >> 4;
        }
        else {
          System.out.println("Placed stronghold in INVALID biome at (" + j + ", " + k + ")");
        }

        this.g[i] = new ChunkCoordIntPair(j, k);

        d1 += 6.283185307179586D / this.g.length;
      }
      this.f = true;
    }

    for (Object localObject2 : this.g) {
      if ((paramInt1 == localObject2.x) && (paramInt2 == localObject2.z)) {
        System.out.println(paramInt1 + ", " + paramInt2);
        return true;
      }
    }
    return false;
  }

  protected List a()
  {
    ArrayList localArrayList = new ArrayList();
    for (ChunkCoordIntPair localChunkCoordIntPair : this.g) {
      if (localChunkCoordIntPair != null) {
        localArrayList.add(localChunkCoordIntPair.a(64));
      }
    }
    return localArrayList;
  }

  protected StructureStart b(int paramInt1, int paramInt2)
  {
    WorldGenStrongholdStart localWorldGenStrongholdStart = new WorldGenStrongholdStart(this.d, this.c, paramInt1, paramInt2);

    while ((localWorldGenStrongholdStart.c().isEmpty()) || (((WorldGenStrongholdStairs2)localWorldGenStrongholdStart.c().get(0)).b == null))
    {
      localWorldGenStrongholdStart = new WorldGenStrongholdStart(this.d, this.c, paramInt1, paramInt2);
    }

    return localWorldGenStrongholdStart;
  }
}

