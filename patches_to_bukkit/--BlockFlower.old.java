package net.minecraft.server;

import java.util.Random;

public class BlockFlower extends Block
{
  protected BlockFlower(int paramInt1, int paramInt2, Material paramMaterial)
  {
    super(paramInt1, paramMaterial);
    this.textureId = paramInt2;
    a(true);
    float f = 0.2F;
    a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
  }

  protected BlockFlower(int paramInt1, int paramInt2) {
    this(paramInt1, paramInt2, Material.PLANT);
  }

  public boolean canPlace(World paramWorld, int paramInt1, int paramInt2, int paramInt3) {
    return (super.canPlace(paramWorld, paramInt1, paramInt2, paramInt3)) && (d(paramWorld.getTypeId(paramInt1, paramInt2 - 1, paramInt3)));
  }

  protected boolean d(int paramInt) {
    return (paramInt == Block.GRASS.id) || (paramInt == Block.DIRT.id) || (paramInt == Block.SOIL.id);
  }

  public void doPhysics(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.doPhysics(paramWorld, paramInt1, paramInt2, paramInt3, paramInt4);
    h(paramWorld, paramInt1, paramInt2, paramInt3);
  }

  public void a(World paramWorld, int paramInt1, int paramInt2, int paramInt3, Random paramRandom) {
    h(paramWorld, paramInt1, paramInt2, paramInt3);
  }

  protected final void h(World paramWorld, int paramInt1, int paramInt2, int paramInt3) {
    if (!f(paramWorld, paramInt1, paramInt2, paramInt3)) {
      b(paramWorld, paramInt1, paramInt2, paramInt3, paramWorld.getData(paramInt1, paramInt2, paramInt3), 0);
      paramWorld.setTypeId(paramInt1, paramInt2, paramInt3, 0);
    }
  }

  public boolean f(World paramWorld, int paramInt1, int paramInt2, int paramInt3) {
    return ((paramWorld.m(paramInt1, paramInt2, paramInt3) >= 8) || (paramWorld.isChunkLoaded(paramInt1, paramInt2, paramInt3))) && (d(paramWorld.getTypeId(paramInt1, paramInt2 - 1, paramInt3)));
  }

  public AxisAlignedBB e(World paramWorld, int paramInt1, int paramInt2, int paramInt3) {
    return null;
  }

  public boolean a()
  {
    return false;
  }

  public boolean b() {
    return false;
  }

  public int c() {
    return 1;
  }
}

