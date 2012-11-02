package net.minecraft.server;

import java.util.Random;

public class WorldGenDeadBush extends WorldGenerator
{
    /** stores the ID for WorldGenDeadBush */
    private int a;

    public WorldGenDeadBush(int var1)
    {
        this.a = var1;
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var11;

        for (boolean var6 = false; ((var11 = var1.getTypeId(var3, var4, var5)) == 0 || var11 == Block.LEAVES.id) && var4 > 0; --var4)
        {
            ;
        }

        for (int var7 = 0; var7 < 4; ++var7)
        {
            int var8 = var3 + var2.nextInt(8) - var2.nextInt(8);
            int var9 = var4 + var2.nextInt(4) - var2.nextInt(4);
            int var10 = var5 + var2.nextInt(8) - var2.nextInt(8);

            if (var1.isEmpty(var8, var9, var10) && Block.byId[this.a].d(var1, var8, var9, var10))
            {
                var1.setRawTypeId(var8, var9, var10, this.a);
            }
        }

        return true;
    }
}
