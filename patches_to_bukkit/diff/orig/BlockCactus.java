package net.minecraft.server;

import java.util.Random;

public class BlockCactus extends Block
{
    protected BlockCactus(int var1, int var2)
    {
        super(var1, var2, Material.CACTUS);
        this.b(true);
        this.a(CreativeModeTab.c);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (var1.isEmpty(var2, var3 + 1, var4))
        {
            int var6;

            for (var6 = 1; var1.getTypeId(var2, var3 - var6, var4) == this.id; ++var6)
            {
                ;
            }

            if (var6 < 3)
            {
                int var7 = var1.getData(var2, var3, var4);

                if (var7 == 15)
                {
                    var1.setTypeId(var2, var3 + 1, var4, this.id);
                    var1.setData(var2, var3, var4, 0);
                }
                else
                {
                    var1.setData(var2, var3, var4, var7 + 1);
                }
            }
        }
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        float var5 = 0.0625F;
        return AxisAlignedBB.a().a((double)((float)var2 + var5), (double)var3, (double)((float)var4 + var5), (double)((float)(var2 + 1) - var5), (double)((float)(var3 + 1) - var5), (double)((float)(var4 + 1) - var5));
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int a(int var1)
    {
        return var1 == 1 ? this.textureId - 1 : (var1 == 0 ? this.textureId + 1 : this.textureId);
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean b()
    {
        return false;
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
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 13;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return !super.canPlace(var1, var2, var3, var4) ? false : this.d(var1, var2, var3, var4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (!this.d(var1, var2, var3, var4))
        {
            this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
            var1.setTypeId(var2, var3, var4, 0);
        }
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean d(World var1, int var2, int var3, int var4)
    {
        if (var1.getMaterial(var2 - 1, var3, var4).isBuildable())
        {
            return false;
        }
        else if (var1.getMaterial(var2 + 1, var3, var4).isBuildable())
        {
            return false;
        }
        else if (var1.getMaterial(var2, var3, var4 - 1).isBuildable())
        {
            return false;
        }
        else if (var1.getMaterial(var2, var3, var4 + 1).isBuildable())
        {
            return false;
        }
        else
        {
            int var5 = var1.getTypeId(var2, var3 - 1, var4);
            return var5 == Block.CACTUS.id || var5 == Block.SAND.id;
        }
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void a(World var1, int var2, int var3, int var4, Entity var5)
    {
        var5.damageEntity(DamageSource.CACTUS, 1);
    }
}
