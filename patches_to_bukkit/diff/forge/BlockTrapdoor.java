package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraftforge.common.ForgeDirection;

public class BlockTrapdoor extends Block
{
    public static boolean disableValidation = false;

    protected BlockTrapdoor(int var1, Material var2)
    {
        super(var1, var2);
        this.textureId = 84;

        if (var2 == Material.ORE)
        {
            ++this.textureId;
        }

        float var3 = 0.5F;
        float var4 = 1.0F;
        this.a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4, 0.5F + var3);
        this.a(CreativeModeTab.d);
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

    public boolean c(IBlockAccess var1, int var2, int var3, int var4)
    {
        return !g(var1.getData(var2, var3, var4));
    }

    /**
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB b_(World var1, int var2, int var3, int var4)
    {
        this.updateShape(var1, var2, var3, var4);
        return super.b_(var1, var2, var3, var4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        this.updateShape(var1, var2, var3, var4);
        return super.e(var1, var2, var3, var4);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void updateShape(IBlockAccess var1, int var2, int var3, int var4)
    {
        this.e(var1.getData(var2, var3, var4));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void f()
    {
        float var1 = 0.1875F;
        this.a(0.0F, 0.5F - var1 / 2.0F, 0.0F, 1.0F, 0.5F + var1 / 2.0F, 1.0F);
    }

    public void e(int var1)
    {
        float var2 = 0.1875F;

        if ((var1 & 8) != 0)
        {
            this.a(0.0F, 1.0F - var2, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, var2, 1.0F);
        }

        if (g(var1))
        {
            if ((var1 & 3) == 0)
            {
                this.a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            }

            if ((var1 & 3) == 1)
            {
                this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }

            if ((var1 & 3) == 2)
            {
                this.a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((var1 & 3) == 3)
            {
                this.a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void attack(World var1, int var2, int var3, int var4, EntityHuman var5) {}

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean interact(World var1, int var2, int var3, int var4, EntityHuman var5, int var6, float var7, float var8, float var9)
    {
        if (this.material == Material.ORE)
        {
            return true;
        }
        else
        {
            int var10 = var1.getData(var2, var3, var4);
            var1.setData(var2, var3, var4, var10 ^ 4);
            var1.a(var5, 1003, var2, var3, var4, 0);
            return true;
        }
    }

    public void setOpen(World var1, int var2, int var3, int var4, boolean var5)
    {
        int var6 = var1.getData(var2, var3, var4);
        boolean var7 = (var6 & 4) > 0;

        if (var7 != var5)
        {
            var1.setData(var2, var3, var4, var6 ^ 4);
            var1.a((EntityHuman)null, 1003, var2, var3, var4, 0);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (!var1.isStatic)
        {
            int var6 = var1.getData(var2, var3, var4);
            int var7 = var2;
            int var8 = var4;

            if ((var6 & 3) == 0)
            {
                var8 = var4 + 1;
            }

            if ((var6 & 3) == 1)
            {
                --var8;
            }

            if ((var6 & 3) == 2)
            {
                var7 = var2 + 1;
            }

            if ((var6 & 3) == 3)
            {
                --var7;
            }

            if (!j(var1.getTypeId(var7, var3, var8)) && !var1.isBlockSolidOnSide(var7, var3, var8, ForgeDirection.getOrientation((var6 & 3) + 2)))
            {
                var1.setTypeId(var2, var3, var4, 0);
                this.c(var1, var2, var3, var4, var6, 0);
            }

            boolean var9 = var1.isBlockIndirectlyPowered(var2, var3, var4);

            if (var9 || var5 > 0 && Block.byId[var5].isPowerSource() || var5 == 0)
            {
                this.setOpen(var1, var2, var3, var4, var9);
            }
        }
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition a(World var1, int var2, int var3, int var4, Vec3D var5, Vec3D var6)
    {
        this.updateShape(var1, var2, var3, var4);
        return super.a(var1, var2, var3, var4, var5, var6);
    }

    /**
     * called before onBlockPlacedBy by ItemBlock and ItemReed
     */
    public void postPlace(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8)
    {
        int var9 = 0;

        if (var5 == 2)
        {
            var9 = 0;
        }

        if (var5 == 3)
        {
            var9 = 1;
        }

        if (var5 == 4)
        {
            var9 = 2;
        }

        if (var5 == 5)
        {
            var9 = 3;
        }

        int var10 = Block.TRAP_DOOR.id;

        if (var5 != 1 && var5 != 0 && var7 > 0.5F)
        {
            var9 |= 8;
        }

        var1.setTypeIdAndData(var2, var3, var4, var10, var9);
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlace(World var1, int var2, int var3, int var4, int var5)
    {
        if (disableValidation)
        {
            return true;
        }
        else if (var5 == 0)
        {
            return false;
        }
        else if (var5 == 1)
        {
            return false;
        }
        else
        {
            if (var5 == 2)
            {
                ++var4;
            }

            if (var5 == 3)
            {
                --var4;
            }

            if (var5 == 4)
            {
                ++var2;
            }

            if (var5 == 5)
            {
                --var2;
            }

            return j(var1.getTypeId(var2, var3, var4)) || var1.isBlockSolidOnSide(var2, var3, var4, ForgeDirection.UP);
        }
    }

    public static boolean g(int var0)
    {
        return (var0 & 4) != 0;
    }

    /**
     * Checks if the block ID is a valid support block for the trap door to connect with. If it is not the trapdoor is
     * dropped into the world.
     */
    private static boolean j(int var0)
    {
        if (disableValidation)
        {
            return true;
        }
        else if (var0 <= 0)
        {
            return false;
        }
        else
        {
            Block var1 = Block.byId[var0];
            return var1 != null && var1.material.k() && var1.b() || var1 == Block.GLOWSTONE;
        }
    }
}
