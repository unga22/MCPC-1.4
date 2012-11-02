package net.minecraft.server;

import java.util.Random;

public class WorldGenGroundBush extends WorldGenerator
{
    private int field_76527_a;
    private int field_76526_b;

    public WorldGenGroundBush(int var1, int var2)
    {
        this.field_76526_b = var1;
        this.field_76527_a = var2;
    }

    public boolean a(World var1, Random var2, int var3, int var4, int var5)
    {
        int var15;

        for (boolean var6 = false; ((var15 = var1.getTypeId(var3, var4, var5)) == 0 || var15 == Block.LEAVES.id) && var4 > 0; --var4)
        {
            ;
        }

        int var7 = var1.getTypeId(var3, var4, var5);

        if (var7 == Block.DIRT.id || var7 == Block.GRASS.id)
        {
            ++var4;
            this.setTypeAndData(var1, var3, var4, var5, Block.LOG.id, this.field_76526_b);

            for (int var8 = var4; var8 <= var4 + 2; ++var8)
            {
                int var9 = var8 - var4;
                int var10 = 2 - var9;

                for (int var11 = var3 - var10; var11 <= var3 + var10; ++var11)
                {
                    int var12 = var11 - var3;

                    for (int var13 = var5 - var10; var13 <= var5 + var10; ++var13)
                    {
                        int var14 = var13 - var5;

                        if ((Math.abs(var12) != var10 || Math.abs(var14) != var10 || var2.nextInt(2) != 0) && !Block.q[var1.getTypeId(var11, var8, var13)])
                        {
                            this.setTypeAndData(var1, var11, var8, var13, Block.LEAVES.id, this.field_76527_a);
                        }
                    }
                }
            }
        }

        return true;
    }
}
