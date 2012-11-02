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
        Block var7 = null;

        do
        {
            var7 = Block.byId[var1.getTypeId(var3, var4, var5)];

            if (var7 != null && !var7.isLeaves(var1, var3, var4, var5))
            {
                break;
            }

            --var4;
        }
        while (var4 > 0);

        int var8 = var1.getTypeId(var3, var4, var5);

        if (var8 == Block.DIRT.id || var8 == Block.GRASS.id)
        {
            ++var4;
            this.setTypeAndData(var1, var3, var4, var5, Block.LOG.id, this.field_76526_b);

            for (int var9 = var4; var9 <= var4 + 2; ++var9)
            {
                int var10 = var9 - var4;
                int var11 = 2 - var10;

                for (int var12 = var3 - var11; var12 <= var3 + var11; ++var12)
                {
                    int var13 = var12 - var3;

                    for (int var14 = var5 - var11; var14 <= var5 + var11; ++var14)
                    {
                        int var15 = var14 - var5;
                        var7 = Block.byId[var1.getTypeId(var12, var9, var14)];

                        if ((Math.abs(var13) != var11 || Math.abs(var15) != var11 || var2.nextInt(2) != 0) && (var7 == null || var7.canBeReplacedByLeaves(var1, var12, var9, var14)))
                        {
                            this.setTypeAndData(var1, var12, var9, var14, Block.LEAVES.id, this.field_76527_a);
                        }
                    }
                }
            }
        }

        return true;
    }
}
