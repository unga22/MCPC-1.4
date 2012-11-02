package net.minecraft.server;

public class StructurePieceTreasure extends WeightedRandomChoice
{
  public int a;
  public int b;
  public int c;
  public int e;

  public StructurePieceTreasure(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt5);
    this.a = paramInt1;
    this.b = paramInt2;
    this.c = paramInt3;
    this.e = paramInt4;
  }
}

