package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ChunkCache implements IBlockAccess
{
    private int a;
    private int b;
    private Chunk[][] c;

    /** set by !chunk.getAreLevelsEmpty */
    private boolean d;

    /** Reference to the World object. */
    private World e;

    public ChunkCache(World var1, int var2, int var3, int var4, int var5, int var6, int var7)
    {
        this.e = var1;
        this.a = var2 >> 4;
        this.b = var4 >> 4;
        int var8 = var5 >> 4;
        int var9 = var7 >> 4;
        this.c = new Chunk[var8 - this.a + 1][var9 - this.b + 1];
        this.d = true;

        for (int var10 = this.a; var10 <= var8; ++var10)
        {
            for (int var11 = this.b; var11 <= var9; ++var11)
            {
                Chunk var12 = var1.getChunkAt(var10, var11);

                if (var12 != null)
                {
                    this.c[var10 - this.a][var11 - this.b] = var12;

                    if (!var12.c(var3, var6))
                    {
                        this.d = false;
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean P()
    {
        return this.d;
    }

    /**
     * Returns the block ID at coords x,y,z
     */
    public int getTypeId(int var1, int var2, int var3)
    {
        if (var2 < 0)
        {
            return 0;
        }
        else if (var2 >= 256)
        {
            return 0;
        }
        else
        {
            int var4 = (var1 >> 4) - this.a;
            int var5 = (var3 >> 4) - this.b;

            if (var4 >= 0 && var4 < this.c.length && var5 >= 0 && var5 < this.c[var4].length)
            {
                Chunk var6 = this.c[var4][var5];
                return var6 == null ? 0 : var6.getTypeId(var1 & 15, var2, var3 & 15);
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getTileEntity(int var1, int var2, int var3)
    {
        int var4 = (var1 >> 4) - this.a;
        int var5 = (var3 >> 4) - this.b;

        if (var4 >= 0 && var4 < this.c.length && var5 >= 0 && var5 < this.c[var4].length)
        {
            Chunk var6 = this.c[var4][var5];
            return var6 == null ? null : var6.e(var1 & 15, var2, var3 & 15);
        }
        else
        {
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public float j(int var1, int var2, int var3, int var4)
    {
        int var5 = this.b(var1, var2, var3);

        if (var5 < var4)
        {
            var5 = var4;
        }

        return this.e.worldProvider.g[var5];
    }

    @SideOnly(Side.CLIENT)
    public int i(int var1, int var2, int var3, int var4)
    {
        int var5 = this.a(EnumSkyBlock.Sky, var1, var2, var3);
        int var6 = this.a(EnumSkyBlock.Block, var1, var2, var3);

        if (var6 < var4)
        {
            var6 = var4;
        }

        return var5 << 20 | var6 << 4;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     */
    public float o(int var1, int var2, int var3)
    {
        return this.e.worldProvider.g[this.b(var1, var2, var3)];
    }

    @SideOnly(Side.CLIENT)
    public int b(int var1, int var2, int var3)
    {
        return this.a(var1, var2, var3, true);
    }

    @SideOnly(Side.CLIENT)
    public int a(int var1, int var2, int var3, boolean var4)
    {
        if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 <= 30000000)
        {
            int var5;
            int var6;

            if (var4)
            {
                var5 = this.getTypeId(var1, var2, var3);

                if (var5 == Block.STEP.id || var5 == Block.WOOD_STEP.id || var5 == Block.SOIL.id || var5 == Block.WOOD_STAIRS.id || var5 == Block.COBBLESTONE_STAIRS.id)
                {
                    var6 = this.a(var1, var2 + 1, var3, false);
                    int var7 = this.a(var1 + 1, var2, var3, false);
                    int var8 = this.a(var1 - 1, var2, var3, false);
                    int var9 = this.a(var1, var2, var3 + 1, false);
                    int var10 = this.a(var1, var2, var3 - 1, false);

                    if (var7 > var6)
                    {
                        var6 = var7;
                    }

                    if (var8 > var6)
                    {
                        var6 = var8;
                    }

                    if (var9 > var6)
                    {
                        var6 = var9;
                    }

                    if (var10 > var6)
                    {
                        var6 = var10;
                    }

                    return var6;
                }
            }

            if (var2 < 0)
            {
                return 0;
            }
            else if (var2 >= 256)
            {
                var5 = 15 - this.e.j;

                if (var5 < 0)
                {
                    var5 = 0;
                }

                return var5;
            }
            else
            {
                var5 = (var1 >> 4) - this.a;
                var6 = (var3 >> 4) - this.b;
                return this.c[var5][var6].c(var1 & 15, var2, var3 & 15, this.e.j);
            }
        }
        else
        {
            return 15;
        }
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getData(int var1, int var2, int var3)
    {
        if (var2 < 0)
        {
            return 0;
        }
        else if (var2 >= 256)
        {
            return 0;
        }
        else
        {
            int var4 = (var1 >> 4) - this.a;
            int var5 = (var3 >> 4) - this.b;

            if (var4 >= 0 && var4 < this.c.length && var5 >= 0 && var5 < this.c[var4].length)
            {
                Chunk var6 = this.c[var4][var5];
                return var6 == null ? 0 : var6.getData(var1 & 15, var2, var3 & 15);
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Returns the block's material.
     */
    public Material getMaterial(int var1, int var2, int var3)
    {
        int var4 = this.getTypeId(var1, var2, var3);
        return var4 == 0 ? Material.AIR : Block.byId[var4].material;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    public BiomeBase getBiome(int var1, int var2)
    {
        return this.e.getBiome(var1, var2);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean r(int var1, int var2, int var3)
    {
        Block var4 = Block.byId[this.getTypeId(var1, var2, var3)];
        return var4 == null ? false : var4.c();
    }

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean s(int var1, int var2, int var3)
    {
        Block var4 = Block.byId[this.getTypeId(var1, var2, var3)];
        return var4 == null ? false : var4.material.isSolid() && var4.b();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the block at the given coordinate has a solid (buildable) top surface.
     */
    public boolean t(int var1, int var2, int var3)
    {
        Block var4 = Block.byId[this.getTypeId(var1, var2, var3)];
        return var4 == null ? false : (var4.material.k() && var4.b() ? true : (var4 instanceof BlockStairs ? (this.getData(var1, var2, var3) & 4) == 4 : (var4 instanceof BlockStepAbstract ? (this.getData(var1, var2, var3) & 8) == 8 : false)));
    }

    /**
     * Return the Vec3Pool object for this world.
     */
    public Vec3DPool getVec3DPool()
    {
        return this.e.getVec3DPool();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isEmpty(int var1, int var2, int var3)
    {
        Block var4 = Block.byId[this.getTypeId(var1, var2, var3)];
        return var4 == null;
    }

    @SideOnly(Side.CLIENT)
    public int a(EnumSkyBlock var1, int var2, int var3, int var4)
    {
        if (var3 < 0)
        {
            var3 = 0;
        }

        if (var3 >= 256)
        {
            var3 = 255;
        }

        if (var3 >= 0 && var3 < 256 && var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 <= 30000000)
        {
            int var5;
            int var6;

            if (Block.v[this.getTypeId(var2, var3, var4)])
            {
                var5 = this.b(var1, var2, var3 + 1, var4);
                var6 = this.b(var1, var2 + 1, var3, var4);
                int var7 = this.b(var1, var2 - 1, var3, var4);
                int var8 = this.b(var1, var2, var3, var4 + 1);
                int var9 = this.b(var1, var2, var3, var4 - 1);

                if (var6 > var5)
                {
                    var5 = var6;
                }

                if (var7 > var5)
                {
                    var5 = var7;
                }

                if (var8 > var5)
                {
                    var5 = var8;
                }

                if (var9 > var5)
                {
                    var5 = var9;
                }

                return var5;
            }
            else
            {
                var5 = (var2 >> 4) - this.a;
                var6 = (var4 >> 4) - this.b;
                return this.c[var5][var6].getBrightness(var1, var2 & 15, var3, var4 & 15);
            }
        }
        else
        {
            return var1.c;
        }
    }

    @SideOnly(Side.CLIENT)
    public int b(EnumSkyBlock var1, int var2, int var3, int var4)
    {
        if (var3 < 0)
        {
            var3 = 0;
        }

        if (var3 >= 256)
        {
            var3 = 255;
        }

        if (var3 >= 0 && var3 < 256 && var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 <= 30000000)
        {
            int var5 = (var2 >> 4) - this.a;
            int var6 = (var4 >> 4) - this.b;
            return this.c[var5][var6].getBrightness(var1, var2 & 15, var3, var4 & 15);
        }
        else
        {
            return var1.c;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns maximum world height.
     */
    public int getHeight()
    {
        return 256;
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    public boolean isBlockFacePowered(int var1, int var2, int var3, int var4)
    {
        int var5 = this.getTypeId(var1, var2, var3);
        return var5 == 0 ? false : Block.byId[var5].c(this, var1, var2, var3, var4);
    }
}
