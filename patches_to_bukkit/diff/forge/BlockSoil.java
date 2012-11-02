package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public class BlockSoil extends Block
{
    protected BlockSoil(int var1)
    {
        super(var1, Material.EARTH);
        this.textureId = 87;
        this.b(true);
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.h(255);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        return AxisAlignedBB.a().a((double)(var2 + 0), (double)(var3 + 0), (double)(var4 + 0), (double)(var2 + 1), (double)(var3 + 1), (double)(var4 + 1));
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean c()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean b()
    {
        return false;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int a(int var1, int var2)
    {
        return var1 == 1 && var2 > 0 ? this.textureId - 1 : (var1 == 1 ? this.textureId : 2);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (!this.n(var1, var2, var3, var4) && !var1.B(var2, var3 + 1, var4))
        {
            int var6 = var1.getData(var2, var3, var4);

            if (var6 > 0)
            {
                var1.setData(var2, var3, var4, var6 - 1);
            }
            else if (!this.l(var1, var2, var3, var4))
            {
                var1.setTypeId(var2, var3, var4, Block.DIRT.id);
            }
        }
        else
        {
            var1.setData(var2, var3, var4, 7);
        }
    }

    /**
     * Block's chance to react to an entity falling on it.
     */
    public void a(World var1, int var2, int var3, int var4, Entity var5, float var6)
    {
        if (!var1.isStatic && var1.random.nextFloat() < var6 - 0.5F)
        {
            var1.setTypeId(var2, var3, var4, Block.DIRT.id);
        }
    }

    /**
     * returns true if there is at least one cropblock nearby (x-1 to x+1, y+1, z-1 to z+1)
     */
    private boolean l(World var1, int var2, int var3, int var4)
    {
        byte var5 = 0;

        for (int var6 = var2 - var5; var6 <= var2 + var5; ++var6)
        {
            for (int var7 = var4 - var5; var7 <= var4 + var5; ++var7)
            {
                int var8 = var1.getTypeId(var6, var3 + 1, var7);
                Block var9 = byId[var8];

                if (var9 instanceof IPlantable && this.canSustainPlant(var1, var2, var3, var4, ForgeDirection.UP, (IPlantable)var9))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * returns true if there's water nearby (x-4 to x+4, y to y+1, k-4 to k+4)
     */
    private boolean n(World var1, int var2, int var3, int var4)
    {
        for (int var5 = var2 - 4; var5 <= var2 + 4; ++var5)
        {
            for (int var6 = var3; var6 <= var3 + 1; ++var6)
            {
                for (int var7 = var4 - 4; var7 <= var4 + 4; ++var7)
                {
                    if (var1.getMaterial(var5, var6, var7) == Material.WATER)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        super.doPhysics(var1, var2, var3, var4, var5);
        Material var6 = var1.getMaterial(var2, var3 + 1, var4);

        if (var6.isBuildable())
        {
            var1.setTypeId(var2, var3, var4, Block.DIRT.id);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return Block.DIRT.getDropType(0, var2, var3);
    }

    @SideOnly(Side.CLIENT)
    public int a(World var1, int var2, int var3, int var4)
    {
        return Block.DIRT.id;
    }
}
