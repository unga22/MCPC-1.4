package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class WorldGenStrongholdLibrary extends WorldGenStrongholdPiece
{
  private static final StructurePieceTreasure[] b = { new StructurePieceTreasure(Item.BOOK.id, 0, 1, 3, 20), new StructurePieceTreasure(Item.PAPER.id, 0, 2, 7, 20), new StructurePieceTreasure(Item.MAP.id, 0, 1, 1, 1), new StructurePieceTreasure(Item.COMPASS.id, 0, 1, 1, 1) };
  protected final WorldGenStrongholdDoorType a;
  private final boolean c;

  public WorldGenStrongholdLibrary(int paramInt1, Random paramRandom, StructureBoundingBox paramStructureBoundingBox, int paramInt2)
  {
    super(paramInt1);

    this.h = paramInt2;
    this.a = a(paramRandom);
    this.g = paramStructureBoundingBox;
    this.c = (paramStructureBoundingBox.c() > 6);
  }

  public void a(StructurePiece paramStructurePiece, List paramList, Random paramRandom)
  {
  }

  public static WorldGenStrongholdLibrary a(List paramList, Random paramRandom, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    StructureBoundingBox localStructureBoundingBox = StructureBoundingBox.a(paramInt1, paramInt2, paramInt3, -4, -1, 0, 14, 11, 15, paramInt4);

    if ((!a(localStructureBoundingBox)) || (StructurePiece.a(paramList, localStructureBoundingBox) != null))
    {
      localStructureBoundingBox = StructureBoundingBox.a(paramInt1, paramInt2, paramInt3, -4, -1, 0, 14, 6, 15, paramInt4);

      if ((!a(localStructureBoundingBox)) || (StructurePiece.a(paramList, localStructureBoundingBox) != null)) {
        return null;
      }
    }

    return new WorldGenStrongholdLibrary(paramInt5, paramRandom, localStructureBoundingBox, paramInt4);
  }

  public boolean a(World paramWorld, Random paramRandom, StructureBoundingBox paramStructureBoundingBox)
  {
    if (a(paramWorld, paramStructureBoundingBox)) {
      return false;
    }

    int i = 11;
    if (!this.c) {
      i = 6;
    }

    a(paramWorld, paramStructureBoundingBox, 0, 0, 0, 13, i - 1, 14, true, paramRandom, WorldGenStrongholdPieces.b());

    a(paramWorld, paramRandom, paramStructureBoundingBox, this.a, 4, 1, 0);

    a(paramWorld, paramStructureBoundingBox, paramRandom, 0.07F, 2, 1, 1, 11, 4, 13, Block.WEB.id, Block.WEB.id, false);

    for (int j = 1; j <= 13; j++) {
      if ((j - 1) % 4 == 0) {
        a(paramWorld, paramStructureBoundingBox, 1, 1, j, 1, 4, j, Block.WOOD.id, Block.WOOD.id, false);
        a(paramWorld, paramStructureBoundingBox, 12, 1, j, 12, 4, j, Block.WOOD.id, Block.WOOD.id, false);

        a(paramWorld, Block.TORCH.id, 0, 2, 3, j, paramStructureBoundingBox);
        a(paramWorld, Block.TORCH.id, 0, 11, 3, j, paramStructureBoundingBox);

        if (this.c) {
          a(paramWorld, paramStructureBoundingBox, 1, 6, j, 1, 9, j, Block.WOOD.id, Block.WOOD.id, false);
          a(paramWorld, paramStructureBoundingBox, 12, 6, j, 12, 9, j, Block.WOOD.id, Block.WOOD.id, false);
        }

      }
      else
      {
        a(paramWorld, paramStructureBoundingBox, 1, 1, j, 1, 4, j, Block.BOOKSHELF.id, Block.BOOKSHELF.id, false);
        a(paramWorld, paramStructureBoundingBox, 12, 1, j, 12, 4, j, Block.BOOKSHELF.id, Block.BOOKSHELF.id, false);

        if (this.c) {
          a(paramWorld, paramStructureBoundingBox, 1, 6, j, 1, 9, j, Block.BOOKSHELF.id, Block.BOOKSHELF.id, false);
          a(paramWorld, paramStructureBoundingBox, 12, 6, j, 12, 9, j, Block.BOOKSHELF.id, Block.BOOKSHELF.id, false);
        }
      }

    }

    for (j = 3; j < 12; j += 2) {
      a(paramWorld, paramStructureBoundingBox, 3, 1, j, 4, 3, j, Block.BOOKSHELF.id, Block.BOOKSHELF.id, false);
      a(paramWorld, paramStructureBoundingBox, 6, 1, j, 7, 3, j, Block.BOOKSHELF.id, Block.BOOKSHELF.id, false);
      a(paramWorld, paramStructureBoundingBox, 9, 1, j, 10, 3, j, Block.BOOKSHELF.id, Block.BOOKSHELF.id, false);
    }

    if (this.c)
    {
      a(paramWorld, paramStructureBoundingBox, 1, 5, 1, 3, 5, 13, Block.WOOD.id, Block.WOOD.id, false);
      a(paramWorld, paramStructureBoundingBox, 10, 5, 1, 12, 5, 13, Block.WOOD.id, Block.WOOD.id, false);
      a(paramWorld, paramStructureBoundingBox, 4, 5, 1, 9, 5, 2, Block.WOOD.id, Block.WOOD.id, false);
      a(paramWorld, paramStructureBoundingBox, 4, 5, 12, 9, 5, 13, Block.WOOD.id, Block.WOOD.id, false);

      a(paramWorld, Block.WOOD.id, 0, 9, 5, 11, paramStructureBoundingBox);
      a(paramWorld, Block.WOOD.id, 0, 8, 5, 11, paramStructureBoundingBox);
      a(paramWorld, Block.WOOD.id, 0, 9, 5, 10, paramStructureBoundingBox);

      a(paramWorld, paramStructureBoundingBox, 3, 6, 2, 3, 6, 12, Block.FENCE.id, Block.FENCE.id, false);
      a(paramWorld, paramStructureBoundingBox, 10, 6, 2, 10, 6, 10, Block.FENCE.id, Block.FENCE.id, false);
      a(paramWorld, paramStructureBoundingBox, 4, 6, 2, 9, 6, 2, Block.FENCE.id, Block.FENCE.id, false);
      a(paramWorld, paramStructureBoundingBox, 4, 6, 12, 8, 6, 12, Block.FENCE.id, Block.FENCE.id, false);
      a(paramWorld, Block.FENCE.id, 0, 9, 6, 11, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, 8, 6, 11, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, 9, 6, 10, paramStructureBoundingBox);

      j = c(Block.LADDER.id, 3);
      a(paramWorld, Block.LADDER.id, j, 10, 1, 13, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, j, 10, 2, 13, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, j, 10, 3, 13, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, j, 10, 4, 13, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, j, 10, 5, 13, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, j, 10, 6, 13, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, j, 10, 7, 13, paramStructureBoundingBox);

      int k = 7;
      int m = 7;
      a(paramWorld, Block.FENCE.id, 0, k - 1, 9, m, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k, 9, m, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k - 1, 8, m, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k, 8, m, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k - 1, 7, m, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k, 7, m, paramStructureBoundingBox);

      a(paramWorld, Block.FENCE.id, 0, k - 2, 7, m, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k + 1, 7, m, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k - 1, 7, m - 1, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k - 1, 7, m + 1, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k, 7, m - 1, paramStructureBoundingBox);
      a(paramWorld, Block.FENCE.id, 0, k, 7, m + 1, paramStructureBoundingBox);

      a(paramWorld, Block.TORCH.id, 0, k - 2, 8, m, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, k + 1, 8, m, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, k - 1, 8, m - 1, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, k - 1, 8, m + 1, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, k, 8, m - 1, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, k, 8, m + 1, paramStructureBoundingBox);
    }

    a(paramWorld, paramStructureBoundingBox, paramRandom, 3, 3, 5, b, 1 + paramRandom.nextInt(4));
    if (this.c) {
      a(paramWorld, 0, 0, 12, 9, 1, paramStructureBoundingBox);
      a(paramWorld, paramStructureBoundingBox, paramRandom, 12, 8, 1, b, 1 + paramRandom.nextInt(4));
    }

    return true;
  }
}

