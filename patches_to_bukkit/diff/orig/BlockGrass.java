package net.minecraft.server;

import java.util.Random;

public class BlockGrass extends Block
{
    protected BlockGrass(int var1)
    {
        super(var1, Material.GRASS);
        this.textureId = 3;
        this.b(true);
        this.a(CreativeModeTab.b);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        return var1 == 1 ? 0 : (var1 == 0 ? 2 : 3);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (!var1.isStatic)
        {
            if (var1.getLightLevel(var2, var3 + 1, var4) < 4 && Block.lightBlock[var1.getTypeId(var2, var3 + 1, var4)] > 2)
            {
                var1.setTypeId(var2, var3, var4, Block.DIRT.id);
            }
            else if (var1.getLightLevel(var2, var3 + 1, var4) >= 9)
            {
                for (int var6 = 0; var6 < 4; ++var6)
                {
                    int var7 = var2 + var5.nextInt(3) - 1;
                    int var8 = var3 + var5.nextInt(5) - 3;
                    int var9 = var4 + var5.nextInt(3) - 1;
                    int var10 = var1.getTypeId(var7, var8 + 1, var9);

                    if (var1.getTypeId(var7, var8, var9) == Block.DIRT.id && var1.getLightLevel(var7, var8 + 1, var9) >= 4 && Block.lightBlock[var10] <= 2)
                    {
                        var1.setTypeId(var7, var8, var9, Block.GRASS.id);
                    }
                }
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return Block.DIRT.getDropType(0, var2, var3);
    }
}
