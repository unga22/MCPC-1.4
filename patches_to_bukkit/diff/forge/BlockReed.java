package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class BlockReed extends Block implements IPlantable
{
    protected BlockReed(int var1, int var2)
    {
        super(var1, Material.PLANT);
        this.textureId = var2;
        float var3 = 0.375F;
        this.a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 1.0F, 0.5F + var3);
        this.b(true);
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
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        int var5 = var1.getTypeId(var2, var3 - 1, var4);
        return var5 == this.id ? true : (var5 != Block.GRASS.id && var5 != Block.DIRT.id && var5 != Block.SAND.id ? false : (var1.getMaterial(var2 - 1, var3 - 1, var4) == Material.WATER ? true : (var1.getMaterial(var2 + 1, var3 - 1, var4) == Material.WATER ? true : (var1.getMaterial(var2, var3 - 1, var4 - 1) == Material.WATER ? true : var1.getMaterial(var2, var3 - 1, var4 + 1) == Material.WATER))));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        this.k_(var1, var2, var3, var4);
    }

    /**
     * Checks if current block pos is valid, if not, breaks the block as dropable item. Used for reed and cactus.
     */
    protected final void k_(World var1, int var2, int var3, int var4)
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
        return this.canPlace(var1, var2, var3, var4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        return null;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return Item.SUGAR_CANE.id;
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
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public int a(World var1, int var2, int var3, int var4)
    {
        return Item.SUGAR_CANE.id;
    }

    public EnumPlantType getPlantType(World var1, int var2, int var3, int var4)
    {
        return EnumPlantType.Beach;
    }

    public int getPlantID(World var1, int var2, int var3, int var4)
    {
        return this.id;
    }

    public int getPlantMetadata(World var1, int var2, int var3, int var4)
    {
        return var1.getData(var2, var3, var4);
    }
}
