package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class WorldGenMineshaftCorridor extends StructurePiece
{
  private final boolean a;
  private final boolean b;
  private boolean c;
  private int d;

  public WorldGenMineshaftCorridor(int paramInt1, Random paramRandom, StructureBoundingBox paramStructureBoundingBox, int paramInt2)
  {
    super(paramInt1);
    this.h = paramInt2;
    this.g = paramStructureBoundingBox;
    this.a = (paramRandom.nextInt(3) == 0);
    this.b = ((!this.a) && (paramRandom.nextInt(23) == 0));

    if ((this.h == 2) || (this.h == 0))
      this.d = (paramStructureBoundingBox.d() / 5);
    else
      this.d = (paramStructureBoundingBox.b() / 5);
  }

  public static StructureBoundingBox a(List paramList, Random paramRandom, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    StructureBoundingBox localStructureBoundingBox = new StructureBoundingBox(paramInt1, paramInt2, paramInt3, paramInt1, paramInt2 + 2, paramInt3);

    int i = paramRandom.nextInt(3) + 2;
    while (i > 0) {
      int j = i * 5;

      switch (paramInt4) {
      case 2:
        localStructureBoundingBox.d = (paramInt1 + 2);
        localStructureBoundingBox.c = (paramInt3 - (j - 1));
        break;
      case 0:
        localStructureBoundingBox.d = (paramInt1 + 2);
        localStructureBoundingBox.f = (paramInt3 + (j - 1));
        break;
      case 1:
        localStructureBoundingBox.a = (paramInt1 - (j - 1));
        localStructureBoundingBox.f = (paramInt3 + 2);
        break;
      case 3:
        localStructureBoundingBox.d = (paramInt1 + (j - 1));
        localStructureBoundingBox.f = (paramInt3 + 2);
      }

      if (StructurePiece.a(paramList, localStructureBoundingBox) == null) break;
      i--;
    }

    if (i > 0) {
      return localStructureBoundingBox;
    }

    return null;
  }

  public void a(StructurePiece paramStructurePiece, List paramList, Random paramRandom)
  {
    int i = c();
    int j = paramRandom.nextInt(4);
    switch (this.h) {
    case 2:
      if (j <= 1)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a, this.g.b - 1 + paramRandom.nextInt(3), this.g.c - 1, this.h, i);
      else if (j == 2)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a - 1, this.g.b - 1 + paramRandom.nextInt(3), this.g.c, 1, i);
      else {
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.d + 1, this.g.b - 1 + paramRandom.nextInt(3), this.g.c, 3, i);
      }
      break;
    case 0:
      if (j <= 1)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a, this.g.b - 1 + paramRandom.nextInt(3), this.g.f + 1, this.h, i);
      else if (j == 2)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a - 1, this.g.b - 1 + paramRandom.nextInt(3), this.g.f - 3, 1, i);
      else {
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.d + 1, this.g.b - 1 + paramRandom.nextInt(3), this.g.f - 3, 3, i);
      }
      break;
    case 1:
      if (j <= 1)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a - 1, this.g.b - 1 + paramRandom.nextInt(3), this.g.c, this.h, i);
      else if (j == 2)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a, this.g.b - 1 + paramRandom.nextInt(3), this.g.c - 1, 2, i);
      else {
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a, this.g.b - 1 + paramRandom.nextInt(3), this.g.f + 1, 0, i);
      }
      break;
    case 3:
      if (j <= 1)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.d + 1, this.g.b - 1 + paramRandom.nextInt(3), this.g.c, this.h, i);
      else if (j == 2)
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.d - 3, this.g.b - 1 + paramRandom.nextInt(3), this.g.c - 1, 2, i);
      else {
        WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.d - 3, this.g.b - 1 + paramRandom.nextInt(3), this.g.f + 1, 0, i);
      }

      break;
    }

    if (i < 8)
    {
      int k;
      int m;
      if ((this.h == 2) || (this.h == 0)) {
        for (k = this.g.c + 3; k + 3 <= this.g.f; k += 5) {
          m = paramRandom.nextInt(5);
          if (m == 0)
            WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.a - 1, this.g.b, k, 1, i + 1);
          else if (m == 1)
            WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, this.g.d + 1, this.g.b, k, 3, i + 1);
        }
      }
      else
        for (k = this.g.a + 3; k + 3 <= this.g.d; k += 5) {
          m = paramRandom.nextInt(5);
          if (m == 0)
            WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, k, this.g.b, this.g.c - 1, 2, i + 1);
          else if (m == 1)
            WorldGenMineshaftPieces.a(paramStructurePiece, paramList, paramRandom, k, this.g.b, this.g.f + 1, 0, i + 1);
        }
    }
  }

  public boolean a(World paramWorld, Random paramRandom, StructureBoundingBox paramStructureBoundingBox)
  {
    if (a(paramWorld, paramStructureBoundingBox)) {
      return false;
    }

    int i = this.d * 5 - 1;

    a(paramWorld, paramStructureBoundingBox, 0, 0, 0, 2, 1, i, 0, 0, false);
    a(paramWorld, paramStructureBoundingBox, paramRandom, 0.8F, 0, 2, 0, 2, 2, i, 0, 0, false);

    if (this.b)
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.6F, 0, 0, 0, 2, 1, i, Block.WEB.id, 0, false);
    int k;
    int m;
    for (int j = 0; j < this.d; j++)
    {
      k = 2 + j * 5;

      a(paramWorld, paramStructureBoundingBox, 0, 0, k, 0, 1, k, Block.FENCE.id, 0, false);
      a(paramWorld, paramStructureBoundingBox, 2, 0, k, 2, 1, k, Block.FENCE.id, 0, false);
      if (paramRandom.nextInt(4) != 0) {
        a(paramWorld, paramStructureBoundingBox, 0, 2, k, 2, 2, k, Block.WOOD.id, 0, false);
      } else {
        a(paramWorld, paramStructureBoundingBox, 0, 2, k, 0, 2, k, Block.WOOD.id, 0, false);
        a(paramWorld, paramStructureBoundingBox, 2, 2, k, 2, 2, k, Block.WOOD.id, 0, false);
      }
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.1F, 0, 2, k - 1, Block.WEB.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.1F, 2, 2, k - 1, Block.WEB.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.1F, 0, 2, k + 1, Block.WEB.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.1F, 2, 2, k + 1, Block.WEB.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.05F, 0, 2, k - 2, Block.WEB.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.05F, 2, 2, k - 2, Block.WEB.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.05F, 0, 2, k + 2, Block.WEB.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.05F, 2, 2, k + 2, Block.WEB.id, 0);

      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.05F, 1, 2, k - 1, Block.TORCH.id, 0);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 0.05F, 1, 2, k + 1, Block.TORCH.id, 0);

      if (paramRandom.nextInt(100) == 0) {
        a(paramWorld, paramStructureBoundingBox, paramRandom, 2, 0, k - 1, WorldGenMineshaftPieces.a(), 3 + paramRandom.nextInt(4));
      }
      if (paramRandom.nextInt(100) == 0) {
        a(paramWorld, paramStructureBoundingBox, paramRandom, 0, 0, k + 1, WorldGenMineshaftPieces.a(), 3 + paramRandom.nextInt(4));
      }
      if ((this.b) && (!this.c)) {
        m = b(0); int n = k - 1 + paramRandom.nextInt(3);
        int i1 = a(1, n);
        n = b(1, n);
        if (paramStructureBoundingBox.b(i1, m, n)) {
          this.c = true;
          paramWorld.setTypeId(i1, m, n, Block.MOB_SPAWNER.id);
          TileEntityMobSpawner localTileEntityMobSpawner = (TileEntityMobSpawner)paramWorld.getTileEntity(i1, m, n);
          if (localTileEntityMobSpawner != null) localTileEntityMobSpawner.a("CaveSpider");
        }
      }

    }

    for (j = 0; j <= 2; j++) {
      for (k = 0; k <= i; k++) {
        m = a(paramWorld, j, -1, k, paramStructureBoundingBox);
        if (m == 0) {
          a(paramWorld, Block.WOOD.id, 0, j, -1, k, paramStructureBoundingBox);
        }
      }
    }

    if (this.a) {
      for (j = 0; j <= i; j++) {
        k = a(paramWorld, 1, -1, j, paramStructureBoundingBox);
        if ((k > 0) && (Block.n[k] != 0)) {
          a(paramWorld, paramStructureBoundingBox, paramRandom, 0.7F, 1, 0, j, Block.RAILS.id, c(Block.RAILS.id, 0));
        }
      }
    }

    return true;
  }
}

