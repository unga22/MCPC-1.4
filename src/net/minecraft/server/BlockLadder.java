package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;
import net.minecraftforge.common.ForgeDirection;

public class BlockLadder extends Block
{
    protected BlockLadder(int var1, int var2)
    {
        super(var1, var2, Material.ORIENTABLE);
        this.a(CreativeModeTab.c);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);
        float var6 = 0.125F;

        if (var5 == 2)
        {
            this.a(0.0F, 0.0F, 1.0F - var6, 1.0F, 1.0F, 1.0F);
        }

        if (var5 == 3)
        {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var6);
        }

        if (var5 == 4)
        {
            this.a(1.0F - var6, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (var5 == 5)
        {
            this.a(0.0F, 0.0F, 0.0F, var6, 1.0F, 1.0F);
        }

        return super.e(var1, var2, var3, var4);
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
        return 8;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var1.isBlockSolidOnSide(var2 - 1, var3, var4, ForgeDirection.EAST) || var1.isBlockSolidOnSide(var2 + 1, var3, var4, ForgeDirection.WEST) || var1.isBlockSolidOnSide(var2, var3, var4 - 1, ForgeDirection.SOUTH) || var1.isBlockSolidOnSide(var2, var3, var4 + 1, ForgeDirection.NORTH);
    }

    /**
     * called before onBlockPlacedBy by ItemBlock and ItemReed
     */
    public void postPlace(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8)
    {
        int var9 = var1.getData(var2, var3, var4);

        if ((var9 == 0 || var5 == 2) && var1.isBlockSolidOnSide(var2, var3, var4 + 1, ForgeDirection.NORTH))
        {
            var9 = 2;
        }

        if ((var9 == 0 || var5 == 3) && var1.isBlockSolidOnSide(var2, var3, var4 - 1, ForgeDirection.SOUTH))
        {
            var9 = 3;
        }

        if ((var9 == 0 || var5 == 4) && var1.isBlockSolidOnSide(var2 + 1, var3, var4, ForgeDirection.WEST))
        {
            var9 = 4;
        }

        if ((var9 == 0 || var5 == 5) && var1.isBlockSolidOnSide(var2 - 1, var3, var4, ForgeDirection.EAST))
        {
            var9 = 5;
        }

        var1.setData(var2, var3, var4, var9);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        int var6 = var1.getData(var2, var3, var4);
        boolean var7 = false;

        if (var6 == 2 && var1.isBlockSolidOnSide(var2, var3, var4 + 1, ForgeDirection.NORTH))
        {
            var7 = true;
        }

        if (var6 == 3 && var1.isBlockSolidOnSide(var2, var3, var4 - 1, ForgeDirection.SOUTH))
        {
            var7 = true;
        }

        if (var6 == 4 && var1.isBlockSolidOnSide(var2 + 1, var3, var4, ForgeDirection.WEST))
        {
            var7 = true;
        }

        if (var6 == 5 && var1.isBlockSolidOnSide(var2 - 1, var3, var4, ForgeDirection.EAST))
        {
            var7 = true;
        }

        if (!var7)
        {
            this.c(var1, var2, var3, var4, var6, 0);
            var1.setTypeId(var2, var3, var4, 0);
        }

        super.doPhysics(var1, var2, var3, var4, var5);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int a(Random var1)
    {
        return 1;
    }

    public boolean isLadder(World var1, int var2, int var3, int var4)
    {
        return true;
    }
}
