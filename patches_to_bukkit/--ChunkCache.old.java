package net.minecraft.server;

public class ChunkCache
  implements IBlockAccess
{
  private int a;
  private int b;
  private Chunk[][] c;
  private boolean d;
  private World e;

  public ChunkCache(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.e = paramWorld;

    this.a = (paramInt1 >> 4);
    this.b = (paramInt3 >> 4);
    int i = paramInt4 >> 4;
    int j = paramInt6 >> 4;

    this.c = new Chunk[i - this.a + 1][j - this.b + 1];

    this.d = true;
    for (int k = this.a; k <= i; k++)
      for (int m = this.b; m <= j; m++) {
        Chunk localChunk = paramWorld.getChunkAt(k, m);
        if (localChunk != null) {
          this.c[(k - this.a)][(m - this.b)] = localChunk;

          if (!localChunk.c(paramInt2, paramInt5))
            this.d = false;
        }
      }
  }

  public int getTypeId(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 < 0) return 0;
    if (paramInt2 >= 256) return 0;

    int i = (paramInt1 >> 4) - this.a;
    int j = (paramInt3 >> 4) - this.b;

    if ((i < 0) || (i >= this.c.length) || (j < 0) || (j >= this.c[i].length)) {
      return 0;
    }

    Chunk localChunk = this.c[i][j];
    if (localChunk == null) return 0;

    return localChunk.getTypeId(paramInt1 & 0xF, paramInt2, paramInt3 & 0xF);
  }

  public TileEntity getTileEntity(int paramInt1, int paramInt2, int paramInt3) {
    int i = (paramInt1 >> 4) - this.a;
    int j = (paramInt3 >> 4) - this.b;

    return this.c[i][j].e(paramInt1 & 0xF, paramInt2, paramInt3 & 0xF);
  }

  public int getData(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 < 0) return 0;
    if (paramInt2 >= 256) return 0;
    int i = (paramInt1 >> 4) - this.a;
    int j = (paramInt3 >> 4) - this.b;

    return this.c[i][j].getData(paramInt1 & 0xF, paramInt2, paramInt3 & 0xF);
  }

  public Material getMaterial(int paramInt1, int paramInt2, int paramInt3) {
    int i = getTypeId(paramInt1, paramInt2, paramInt3);
    if (i == 0) return Material.AIR;
    return Block.byId[i].material;
  }

  public boolean e(int paramInt1, int paramInt2, int paramInt3)
  {
    Block localBlock = Block.byId[getTypeId(paramInt1, paramInt2, paramInt3)];
    if (localBlock == null) return false;
    return (localBlock.material.isSolid()) && (localBlock.b());
  }
}

