package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class WorldGenStrongholdRoomCrossing extends WorldGenStrongholdPiece
{
  private static final StructurePieceTreasure[] c = { new StructurePieceTreasure(Item.IRON_INGOT.id, 0, 1, 5, 10), new StructurePieceTreasure(Item.GOLD_INGOT.id, 0, 1, 3, 5), new StructurePieceTreasure(Item.REDSTONE.id, 0, 4, 9, 5), new StructurePieceTreasure(Item.COAL.id, 0, 3, 8, 10), new StructurePieceTreasure(Item.BREAD.id, 0, 1, 3, 15), new StructurePieceTreasure(Item.APPLE.id, 0, 1, 3, 15), new StructurePieceTreasure(Item.IRON_PICKAXE.id, 0, 1, 1, 1) };
  protected final WorldGenStrongholdDoorType a;
  protected final int b;

  public WorldGenStrongholdRoomCrossing(int paramInt1, Random paramRandom, StructureBoundingBox paramStructureBoundingBox, int paramInt2)
  {
    super(paramInt1);

    this.h = paramInt2;
    this.a = a(paramRandom);
    this.g = paramStructureBoundingBox;
    this.b = paramRandom.nextInt(5);
  }

  public void a(StructurePiece paramStructurePiece, List paramList, Random paramRandom)
  {
    a((WorldGenStrongholdStairs2)paramStructurePiece, paramList, paramRandom, 4, 1);
    b((WorldGenStrongholdStairs2)paramStructurePiece, paramList, paramRandom, 1, 4);
    c((WorldGenStrongholdStairs2)paramStructurePiece, paramList, paramRandom, 1, 4);
  }

  public static WorldGenStrongholdRoomCrossing a(List paramList, Random paramRandom, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    StructureBoundingBox localStructureBoundingBox = StructureBoundingBox.a(paramInt1, paramInt2, paramInt3, -4, -1, 0, 11, 7, 11, paramInt4);

    if ((!a(localStructureBoundingBox)) || (StructurePiece.a(paramList, localStructureBoundingBox) != null)) {
      return null;
    }

    return new WorldGenStrongholdRoomCrossing(paramInt5, paramRandom, localStructureBoundingBox, paramInt4);
  }

  public boolean a(World paramWorld, Random paramRandom, StructureBoundingBox paramStructureBoundingBox)
  {
    if (a(paramWorld, paramStructureBoundingBox)) {
      return false;
    }

    a(paramWorld, paramStructureBoundingBox, 0, 0, 0, 10, 6, 10, true, paramRandom, WorldGenStrongholdPieces.b());

    a(paramWorld, paramRandom, paramStructureBoundingBox, this.a, 4, 1, 0);

    a(paramWorld, paramStructureBoundingBox, 4, 1, 10, 6, 3, 10, 0, 0, false);
    a(paramWorld, paramStructureBoundingBox, 0, 1, 4, 0, 3, 6, 0, 0, false);
    a(paramWorld, paramStructureBoundingBox, 10, 1, 4, 10, 3, 6, 0, 0, false);
    int i;
    switch (this.b) {
    default:
      break;
    case 0:
      a(paramWorld, Block.SMOOTH_BRICK.id, 0, 5, 1, 5, paramStructureBoundingBox);
      a(paramWorld, Block.SMOOTH_BRICK.id, 0, 5, 2, 5, paramStructureBoundingBox);
      a(paramWorld, Block.SMOOTH_BRICK.id, 0, 5, 3, 5, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, 4, 3, 5, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, 6, 3, 5, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, 5, 3, 4, paramStructureBoundingBox);
      a(paramWorld, Block.TORCH.id, 0, 5, 3, 6, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 4, 1, 4, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 4, 1, 5, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 4, 1, 6, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 6, 1, 4, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 6, 1, 5, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 6, 1, 6, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 5, 1, 4, paramStructureBoundingBox);
      a(paramWorld, Block.STEP.id, 0, 5, 1, 6, paramStructureBoundingBox);
      break;
    case 1:
      for (i = 0; i < 5; i++) {
        a(paramWorld, Block.SMOOTH_BRICK.id, 0, 3, 1, 3 + i, paramStructureBoundingBox);
        a(paramWorld, Block.SMOOTH_BRICK.id, 0, 7, 1, 3 + i, paramStructureBoundingBox);
        a(paramWorld, Block.SMOOTH_BRICK.id, 0, 3 + i, 1, 3, paramStructureBoundingBox);
        a(paramWorld, Block.SMOOTH_BRICK.id, 0, 3 + i, 1, 7, paramStructureBoundingBox);
      }
      a(paramWorld, Block.SMOOTH_BRICK.id, 0, 5, 1, 5, paramStructureBoundingBox);
      a(paramWorld, Block.SMOOTH_BRICK.id, 0, 5, 2, 5, paramStructureBoundingBox);
      a(paramWorld, Block.SMOOTH_BRICK.id, 0, 5, 3, 5, paramStructureBoundingBox);
      a(paramWorld, Block.WATER.id, 0, 5, 4, 5, paramStructureBoundingBox);

      break;
    case 2:
      for (i = 1; i <= 9; i++) {
        a(paramWorld, Block.COBBLESTONE.id, 0, 1, 3, i, paramStructureBoundingBox);
        a(paramWorld, Block.COBBLESTONE.id, 0, 9, 3, i, paramStructureBoundingBox);
      }
      for (i = 1; i <= 9; i++) {
        a(paramWorld, Block.COBBLESTONE.id, 0, i, 3, 1, paramStructureBoundingBox);
        a(paramWorld, Block.COBBLESTONE.id, 0, i, 3, 9, paramStructureBoundingBox);
      }
      a(paramWorld, Block.COBBLESTONE.id, 0, 5, 1, 4, paramStructureBoundingBox);
      a(paramWorld, Block.COBBLESTONE.id, 0, 5, 1, 6, paramStructureBoundingBox);
      a(paramWorld, Block.COBBLESTONE.id, 0, 5, 3, 4, paramStructureBoundingBox);
      a(paramWorld, Block.COBBLESTONE.id, 0, 5, 3, 6, paramStructureBoundingBox);
      a(paramWorld, Block.COBBLESTONE.id, 0, 4, 1, 5, paramStructureBoundingBox);
      a(paramWorld, Block.COBBLESTONE.id, 0, 6, 1, 5, paramStructureBoundingBox);
      a(paramWorld, Block.COBBLESTONE.id, 0, 4, 3, 5, paramStructureBoundingBox);
      a(paramWorld, Block.COBBLESTONE.id, 0, 6, 3, 5, paramStructureBoundingBox);
      for (i = 1; i <= 3; i++) {
        a(paramWorld, Block.COBBLESTONE.id, 0, 4, i, 4, paramStructureBoundingBox);
        a(paramWorld, Block.COBBLESTONE.id, 0, 6, i, 4, paramStructureBoundingBox);
        a(paramWorld, Block.COBBLESTONE.id, 0, 4, i, 6, paramStructureBoundingBox);
        a(paramWorld, Block.COBBLESTONE.id, 0, 6, i, 6, paramStructureBoundingBox);
      }
      a(paramWorld, Block.TORCH.id, 0, 5, 3, 5, paramStructureBoundingBox);
      for (i = 2; i <= 8; i++) {
        a(paramWorld, Block.WOOD.id, 0, 2, 3, i, paramStructureBoundingBox);
        a(paramWorld, Block.WOOD.id, 0, 3, 3, i, paramStructureBoundingBox);
        if ((i <= 3) || (i >= 7)) {
          a(paramWorld, Block.WOOD.id, 0, 4, 3, i, paramStructureBoundingBox);
          a(paramWorld, Block.WOOD.id, 0, 5, 3, i, paramStructureBoundingBox);
          a(paramWorld, Block.WOOD.id, 0, 6, 3, i, paramStructureBoundingBox);
        }
        a(paramWorld, Block.WOOD.id, 0, 7, 3, i, paramStructureBoundingBox);
        a(paramWorld, Block.WOOD.id, 0, 8, 3, i, paramStructureBoundingBox);
      }
      a(paramWorld, Block.LADDER.id, c(Block.LADDER.id, 4), 9, 1, 3, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, c(Block.LADDER.id, 4), 9, 2, 3, paramStructureBoundingBox);
      a(paramWorld, Block.LADDER.id, c(Block.LADDER.id, 4), 9, 3, 3, paramStructureBoundingBox);

      a(paramWorld, paramStructureBoundingBox, paramRandom, 3, 4, 8, c, 1 + paramRandom.nextInt(4));
    }

    return true;
  }
}

