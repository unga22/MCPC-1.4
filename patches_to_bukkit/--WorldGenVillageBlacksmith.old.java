package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class WorldGenVillageBlacksmith extends WorldGenVillagePiece
{
  private static final StructurePieceTreasure[] a = { new StructurePieceTreasure(Item.DIAMOND.id, 0, 1, 3, 3), new StructurePieceTreasure(Item.IRON_INGOT.id, 0, 1, 5, 10), new StructurePieceTreasure(Item.GOLD_INGOT.id, 0, 1, 3, 5), new StructurePieceTreasure(Item.BREAD.id, 0, 1, 3, 15), new StructurePieceTreasure(Item.APPLE.id, 0, 1, 3, 15), new StructurePieceTreasure(Item.IRON_PICKAXE.id, 0, 1, 1, 5), new StructurePieceTreasure(Item.IRON_SWORD.id, 0, 1, 1, 5), new StructurePieceTreasure(Item.IRON_CHESTPLATE.id, 0, 1, 1, 5), new StructurePieceTreasure(Item.IRON_HELMET.id, 0, 1, 1, 5), new StructurePieceTreasure(Item.IRON_LEGGINGS.id, 0, 1, 1, 5), new StructurePieceTreasure(Item.IRON_BOOTS.id, 0, 1, 1, 5), new StructurePieceTreasure(Block.OBSIDIAN.id, 0, 3, 7, 5), new StructurePieceTreasure(Block.SAPLING.id, 0, 3, 7, 5) };

  private int b = -1;
  private boolean c;

  public WorldGenVillageBlacksmith(int paramInt1, Random paramRandom, StructureBoundingBox paramStructureBoundingBox, int paramInt2)
  {
    super(paramInt1);

    this.h = paramInt2;
    this.g = paramStructureBoundingBox;
  }

  public void a(StructurePiece paramStructurePiece, List paramList, Random paramRandom)
  {
  }

  public static WorldGenVillageBlacksmith a(List paramList, Random paramRandom, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    StructureBoundingBox localStructureBoundingBox = StructureBoundingBox.a(paramInt1, paramInt2, paramInt3, 0, 0, 0, 10, 6, 7, paramInt4);

    if ((!a(localStructureBoundingBox)) || (StructurePiece.a(paramList, localStructureBoundingBox) != null)) {
      return null;
    }

    return new WorldGenVillageBlacksmith(paramInt5, paramRandom, localStructureBoundingBox, paramInt4);
  }

  public boolean a(World paramWorld, Random paramRandom, StructureBoundingBox paramStructureBoundingBox)
  {
    if (this.b < 0) {
      this.b = b(paramWorld, paramStructureBoundingBox);
      if (this.b < 0) {
        return true;
      }
      this.g.a(0, this.b - this.g.e + 6 - 1, 0);
    }

    a(paramWorld, paramStructureBoundingBox, 0, 1, 0, 9, 4, 6, 0, 0, false);

    a(paramWorld, paramStructureBoundingBox, 0, 0, 0, 9, 0, 6, Block.COBBLESTONE.id, Block.COBBLESTONE.id, false);

    a(paramWorld, paramStructureBoundingBox, 0, 4, 0, 9, 4, 6, Block.COBBLESTONE.id, Block.COBBLESTONE.id, false);
    a(paramWorld, paramStructureBoundingBox, 0, 5, 0, 9, 5, 6, Block.STEP.id, Block.STEP.id, false);
    a(paramWorld, paramStructureBoundingBox, 1, 5, 1, 8, 5, 5, 0, 0, false);

    a(paramWorld, paramStructureBoundingBox, 1, 1, 0, 2, 3, 0, Block.WOOD.id, Block.WOOD.id, false);
    a(paramWorld, paramStructureBoundingBox, 0, 1, 0, 0, 4, 0, Block.LOG.id, Block.LOG.id, false);
    a(paramWorld, paramStructureBoundingBox, 3, 1, 0, 3, 4, 0, Block.LOG.id, Block.LOG.id, false);
    a(paramWorld, paramStructureBoundingBox, 0, 1, 6, 0, 4, 6, Block.LOG.id, Block.LOG.id, false);
    a(paramWorld, Block.WOOD.id, 0, 3, 3, 1, paramStructureBoundingBox);
    a(paramWorld, paramStructureBoundingBox, 3, 1, 2, 3, 3, 2, Block.WOOD.id, Block.WOOD.id, false);
    a(paramWorld, paramStructureBoundingBox, 4, 1, 3, 5, 3, 3, Block.WOOD.id, Block.WOOD.id, false);
    a(paramWorld, paramStructureBoundingBox, 0, 1, 1, 0, 3, 5, Block.WOOD.id, Block.WOOD.id, false);
    a(paramWorld, paramStructureBoundingBox, 1, 1, 6, 5, 3, 6, Block.WOOD.id, Block.WOOD.id, false);

    a(paramWorld, paramStructureBoundingBox, 5, 1, 0, 5, 3, 0, Block.FENCE.id, Block.FENCE.id, false);
    a(paramWorld, paramStructureBoundingBox, 9, 1, 0, 9, 3, 0, Block.FENCE.id, Block.FENCE.id, false);

    a(paramWorld, paramStructureBoundingBox, 6, 1, 4, 9, 4, 6, Block.COBBLESTONE.id, Block.COBBLESTONE.id, false);
    a(paramWorld, Block.LAVA.id, 0, 7, 1, 5, paramStructureBoundingBox);
    a(paramWorld, Block.LAVA.id, 0, 8, 1, 5, paramStructureBoundingBox);
    a(paramWorld, Block.IRON_FENCE.id, 0, 9, 2, 5, paramStructureBoundingBox);
    a(paramWorld, Block.IRON_FENCE.id, 0, 9, 2, 4, paramStructureBoundingBox);
    a(paramWorld, paramStructureBoundingBox, 7, 2, 4, 8, 2, 5, 0, 0, false);
    a(paramWorld, Block.COBBLESTONE.id, 0, 6, 1, 3, paramStructureBoundingBox);
    a(paramWorld, Block.FURNACE.id, 0, 6, 2, 3, paramStructureBoundingBox);
    a(paramWorld, Block.FURNACE.id, 0, 6, 3, 3, paramStructureBoundingBox);
    a(paramWorld, Block.DOUBLE_STEP.id, 0, 8, 1, 1, paramStructureBoundingBox);

    a(paramWorld, Block.THIN_GLASS.id, 0, 0, 2, 2, paramStructureBoundingBox);
    a(paramWorld, Block.THIN_GLASS.id, 0, 0, 2, 4, paramStructureBoundingBox);
    a(paramWorld, Block.THIN_GLASS.id, 0, 2, 2, 6, paramStructureBoundingBox);
    a(paramWorld, Block.THIN_GLASS.id, 0, 4, 2, 6, paramStructureBoundingBox);

    a(paramWorld, Block.FENCE.id, 0, 2, 1, 4, paramStructureBoundingBox);
    a(paramWorld, Block.WOOD_PLATE.id, 0, 2, 2, 4, paramStructureBoundingBox);
    a(paramWorld, Block.WOOD.id, 0, 1, 1, 5, paramStructureBoundingBox);
    a(paramWorld, Block.WOOD_STAIRS.id, c(Block.WOOD_STAIRS.id, 3), 2, 1, 5, paramStructureBoundingBox);
    a(paramWorld, Block.WOOD_STAIRS.id, c(Block.WOOD_STAIRS.id, 1), 1, 1, 4, paramStructureBoundingBox);
    int j;
    if (!this.c) {
      i = b(1);
      j = a(5, 5); int k = b(5, 5);
      if (paramStructureBoundingBox.b(j, i, k)) {
        this.c = true;
        a(paramWorld, paramStructureBoundingBox, paramRandom, 5, 1, 5, a, 3 + paramRandom.nextInt(6));
      }

    }

    for (int i = 6; i <= 8; i++) {
      if ((a(paramWorld, i, 0, -1, paramStructureBoundingBox) == 0) && (a(paramWorld, i, -1, -1, paramStructureBoundingBox) != 0)) {
        a(paramWorld, Block.COBBLESTONE_STAIRS.id, c(Block.COBBLESTONE_STAIRS.id, 3), i, 0, -1, paramStructureBoundingBox);
      }
    }

    for (i = 0; i < 7; i++) {
      for (j = 0; j < 10; j++) {
        b(paramWorld, j, 6, i, paramStructureBoundingBox);
        b(paramWorld, Block.COBBLESTONE.id, 0, j, -1, i, paramStructureBoundingBox);
      }
    }

    a(paramWorld, paramStructureBoundingBox, 7, 1, 1, 1);

    return true;
  }

  protected int a(int paramInt)
  {
    return 3;
  }
}

