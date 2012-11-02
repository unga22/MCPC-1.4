package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;

public class BlockLongGrass extends BlockFlower implements IShearable
{
    protected BlockLongGrass(int var1, int var2)
    {
        super(var1, var2, Material.REPLACEABLE_PLANT);
        float var3 = 0.4F;
        this.a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 0.8F, 0.5F + var3);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        return var2 == 1 ? this.textureId : (var2 == 2 ? this.textureId + 16 + 1 : (var2 == 0 ? this.textureId + 16 : this.textureId));
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return -1;
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int getDropCount(int var1, Random var2)
    {
        return 1 + var2.nextInt(var1 * 2 + 1);
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void a(World var1, EntityHuman var2, int var3, int var4, int var5, int var6)
    {
        super.a(var1, var2, var3, var4, var5, var6);
    }

    @SideOnly(Side.CLIENT)
    public int o()
    {
        double var1 = 0.5D;
        double var3 = 1.0D;
        return xd.a(var1, var3);
    }

    @SideOnly(Side.CLIENT)
    public int g_(int var1)
    {
        return var1 == 0 ? 16777215 : xa.c();
    }

    @SideOnly(Side.CLIENT)
    public int b(IBlockAccess var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);
        return var5 == 0 ? 16777215 : var1.getBiome(var2, var4).k();
    }

    public int func_71873_h(World var1, int var2, int var3, int var4)
    {
        return var1.getData(var2, var3, var4);
    }

    @SideOnly(Side.CLIENT)
    public void a(int var1, CreativeModeTab var2, List var3)
    {
        for (int var4 = 1; var4 < 3; ++var4)
        {
            var3.add(new ItemStack(var1, 1, var4));
        }
    }

    public ArrayList getBlockDropped(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        ArrayList var7 = new ArrayList();

        if (var1.random.nextInt(8) != 0)
        {
            return var7;
        }
        else
        {
            ItemStack var8 = ForgeHooks.getGrassSeed(var1);

            if (var8 != null)
            {
                var7.add(var8);
            }

            return var7;
        }
    }

    public boolean isShearable(ItemStack var1, World var2, int var3, int var4, int var5)
    {
        return true;
    }

    public ArrayList onSheared(ItemStack var1, World var2, int var3, int var4, int var5, int var6)
    {
        ArrayList var7 = new ArrayList();
        var7.add(new ItemStack(this, 1, var2.getData(var3, var4, var5)));
        return var7;
    }
}
