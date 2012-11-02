package net.minecraft.server;

import java.util.Random;

public class WorldGenGrass extends WorldGenerator
{
    /** Stores ID for WorldGenTallGrass */
    private int a;
    private int b;

    public WorldGenGrass(int var1, int var2)
    {
        this.a = var1;
        this.b = var2;
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var11;

        for (boolean var6 = false; ((var11 = var1.getTypeId(var3, var4, var5)) == 0 || var11 == Block.LEAVES.id) && var4 > 0; --var4)
        {
            ;
        }

        for (int var7 = 0; var7 < 128; ++var7)
        {
            int var8 = var3 + var2.nextInt(8) - var2.nextInt(8);
            int var9 = var4 + var2.nextInt(4) - var2.nextInt(4);
            int var10 = var5 + var2.nextInt(8) - var2.nextInt(8);

            if (var1.isEmpty(var8, var9, var10) && Block.byId[this.a].d(var1, var8, var9, var10))
            {
                var1.setRawTypeIdAndData(var8, var9, var10, this.a, this.b);
            }
        }

        return true;
    }
}
