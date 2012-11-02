package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraftforge.common.ForgeDirection;

public class BlockCrops extends BlockFlower
{
    protected BlockCrops(int var1, int var2)
    {
        super(var1, var2);
        this.textureId = var2;
        this.b(true);
        float var3 = 0.5F;
        this.a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 0.25F, 0.5F + var3);
        this.a((CreativeModeTab)null);
        this.c(0.0F);
        this.a(g);
        this.D();
        this.r();
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean d_(int var1)
    {
        return var1 == Block.SOIL.id;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        super.b(var1, var2, var3, var4, var5);

        if (var1.getLightLevel(var2, var3 + 1, var4) >= 9)
        {
            int var6 = var1.getData(var2, var3, var4);

            if (var6 < 7)
            {
                float var7 = this.l(var1, var2, var3, var4);

                if (var5.nextInt((int)(25.0F / var7) + 1) == 0)
                {
                    ++var6;
                    var1.setData(var2, var3, var4, var6);
                }
            }
        }
    }

    /**
     * Apply bonemeal to the crops.
     */
    public void c_(World var1, int var2, int var3, int var4)
    {
        var1.setData(var2, var3, var4, 7);
    }

    /**
     * Gets the growth rate for the crop. Setup to encourage rows by halving growth rate if there is diagonals, crops on
     * different sides that aren't opposing, and by adding growth for every crop next to this one (and for crop below
     * this one). Args: x, y, z
     */
    private float l(World var1, int var2, int var3, int var4)
    {
        float var5 = 1.0F;
        int var6 = var1.getTypeId(var2, var3, var4 - 1);
        int var7 = var1.getTypeId(var2, var3, var4 + 1);
        int var8 = var1.getTypeId(var2 - 1, var3, var4);
        int var9 = var1.getTypeId(var2 + 1, var3, var4);
        int var10 = var1.getTypeId(var2 - 1, var3, var4 - 1);
        int var11 = var1.getTypeId(var2 + 1, var3, var4 - 1);
        int var12 = var1.getTypeId(var2 + 1, var3, var4 + 1);
        int var13 = var1.getTypeId(var2 - 1, var3, var4 + 1);
        boolean var14 = var8 == this.id || var9 == this.id;
        boolean var15 = var6 == this.id || var7 == this.id;
        boolean var16 = var10 == this.id || var11 == this.id || var12 == this.id || var13 == this.id;

        for (int var17 = var2 - 1; var17 <= var2 + 1; ++var17)
        {
            for (int var18 = var4 - 1; var18 <= var4 + 1; ++var18)
            {
                int var19 = var1.getTypeId(var17, var3 - 1, var18);
                float var20 = 0.0F;

                if (byId[var19] != null && byId[var19].canSustainPlant(var1, var17, var3 - 1, var18, ForgeDirection.UP, this))
                {
                    var20 = 1.0F;

                    if (byId[var19].isFertile(var1, var17, var3 - 1, var18))
                    {
                        var20 = 3.0F;
                    }
                }

                if (var17 != var2 || var18 != var4)
                {
                    var20 /= 4.0F;
                }

                var5 += var20;
            }
        }

        if (var16 || var14 && var15)
        {
            var5 /= 2.0F;
        }

        return var5;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        if (var2 < 0)
        {
            var2 = 7;
        }

        return this.textureId + var2;
    }

    /**
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 6;
    }

    /**
     * Generate a seed ItemStack for this crop.
     */
    protected int h()
    {
        return Item.SEEDS.id;
    }

    /**
     * Generate a crop produce ItemStack for this crop.
     */
    protected int j()
    {
        return Item.WHEAT.id;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropNaturally(World var1, int var2, int var3, int var4, int var5, float var6, int var7)
    {
        super.dropNaturally(var1, var2, var3, var4, var5, var6, 0);
    }

    public ArrayList getBlockDropped(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        ArrayList var7 = new ArrayList();
        int var8;

        if (var5 == 7)
        {
            var8 = this.quantityDropped(var5, var6, var1.random);

            for (int var9 = 0; var9 < var8; ++var9)
            {
                int var10 = this.getDropType(var5, var1.random, 0);

                if (var10 > 0)
                {
                    var7.add(new ItemStack(var10, 1, this.getDropData(var5)));
                }
            }
        }

        if (var5 >= 7)
        {
            for (var8 = 0; var8 < 3 + var6; ++var8)
            {
                if (var1.random.nextInt(15) <= var5)
                {
                    var7.add(new ItemStack(this.h(), 1, 0));
                }
            }
        }

        return var7;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return var1 == 7 ? this.j() : this.h();
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int a(Random var1)
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public int a(World var1, int var2, int var3, int var4)
    {
        return this.h();
    }
}
