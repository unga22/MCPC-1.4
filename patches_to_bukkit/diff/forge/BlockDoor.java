package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;

public class BlockDoor extends Block
{
    protected BlockDoor(int var1, Material var2)
    {
        super(var1, var2);
        this.textureId = 97;

        if (var2 == Material.ORE)
        {
            ++this.textureId;
        }

        float var3 = 0.5F;
        float var4 = 1.0F;
        this.a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4, 0.5F + var3);
    }

    @SideOnly(Side.CLIENT)
    public int d(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        if (var5 != 0 && var5 != 1)
        {
            int var6 = this.b_(var1, var2, var3, var4);
            int var7 = this.textureId;

            if ((var6 & 8) != 0)
            {
                var7 -= 16;
            }

            int var8 = var6 & 3;
            boolean var9 = (var6 & 4) != 0;

            if (var9)
            {
                if (var8 == 0 && var5 == 2)
                {
                    var7 = -var7;
                }
                else if (var8 == 1 && var5 == 5)
                {
                    var7 = -var7;
                }
                else if (var8 == 2 && var5 == 3)
                {
                    var7 = -var7;
                }
                else if (var8 == 3 && var5 == 4)
                {
                    var7 = -var7;
                }
            }
            else
            {
                if (var8 == 0 && var5 == 5)
                {
                    var7 = -var7;
                }
                else if (var8 == 1 && var5 == 3)
                {
                    var7 = -var7;
                }
                else if (var8 == 2 && var5 == 4)
                {
                    var7 = -var7;
                }
                else if (var8 == 3 && var5 == 2)
                {
                    var7 = -var7;
                }

                if ((var6 & 16) != 0)
                {
                    var7 = -var7;
                }
            }

            return var7;
        }
        else
        {
            return this.textureId;
        }
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean c()
    {
        return false;
    }

    public boolean c(IBlockAccess var1, int var2, int var3, int var4)
    {
        int var5 = this.b_(var1, var2, var3, var4);
        return (var5 & 4) != 0;
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
        return 7;
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
        this.e(this.b_(var1, var2, var3, var4));
    }

    /**
     * Returns 0, 1, 2 or 3 depending on where the hinge is.
     */
    public int d(IBlockAccess var1, int var2, int var3, int var4)
    {
        return this.b_(var1, var2, var3, var4) & 3;
    }

    public boolean a_(IBlockAccess var1, int var2, int var3, int var4)
    {
        return (this.b_(var1, var2, var3, var4) & 4) != 0;
    }

    private void e(int var1)
    {
        float var2 = 0.1875F;
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int var3 = var1 & 3;
        boolean var4 = (var1 & 4) != 0;
        boolean var5 = (var1 & 16) != 0;

        if (var3 == 0)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
                }
                else
                {
                    this.a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
        }
        else if (var3 == 1)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
                }
            }
            else
            {
                this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }
        }
        else if (var3 == 2)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
                }
            }
            else
            {
                this.a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        else if (var3 == 3)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
                }
                else
                {
                    this.a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
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
            return false;
        }
        else
        {
            int var10 = this.b_(var1, var2, var3, var4);
            int var11 = var10 & 7;
            var11 ^= 4;

            if ((var10 & 8) == 0)
            {
                var1.setData(var2, var3, var4, var11);
                var1.e(var2, var3, var4, var2, var3, var4);
            }
            else
            {
                var1.setData(var2, var3 - 1, var4, var11);
                var1.e(var2, var3 - 1, var4, var2, var3, var4);
            }

            var1.a(var5, 1003, var2, var3, var4, 0);
            return true;
        }
    }

    /**
     * A function to open a door.
     */
    public void setDoor(World var1, int var2, int var3, int var4, boolean var5)
    {
        int var6 = this.b_(var1, var2, var3, var4);
        boolean var7 = (var6 & 4) != 0;

        if (var7 != var5)
        {
            int var8 = var6 & 7;
            var8 ^= 4;

            if ((var6 & 8) == 0)
            {
                var1.setData(var2, var3, var4, var8);
                var1.e(var2, var3, var4, var2, var3, var4);
            }
            else
            {
                var1.setData(var2, var3 - 1, var4, var8);
                var1.e(var2, var3 - 1, var4, var2, var3, var4);
            }

            var1.a((EntityHuman)null, 1003, var2, var3, var4, 0);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        int var6 = var1.getData(var2, var3, var4);

        if ((var6 & 8) == 0)
        {
            boolean var7 = false;

            if (var1.getTypeId(var2, var3 + 1, var4) != this.id)
            {
                var1.setTypeId(var2, var3, var4, 0);
                var7 = true;
            }

            if (!var1.t(var2, var3 - 1, var4))
            {
                var1.setTypeId(var2, var3, var4, 0);
                var7 = true;

                if (var1.getTypeId(var2, var3 + 1, var4) == this.id)
                {
                    var1.setTypeId(var2, var3 + 1, var4, 0);
                }
            }

            if (var7)
            {
                if (!var1.isStatic)
                {
                    this.c(var1, var2, var3, var4, var6, 0);
                }
            }
            else
            {
                boolean var8 = var1.isBlockIndirectlyPowered(var2, var3, var4) || var1.isBlockIndirectlyPowered(var2, var3 + 1, var4);

                if ((var8 || var5 > 0 && Block.byId[var5].isPowerSource() || var5 == 0) && var5 != this.id)
                {
                    this.setDoor(var1, var2, var3, var4, var8);
                }
            }
        }
        else
        {
            if (var1.getTypeId(var2, var3 - 1, var4) != this.id)
            {
                var1.setTypeId(var2, var3, var4, 0);
            }

            if (var5 > 0 && var5 != this.id)
            {
                this.doPhysics(var1, var2, var3 - 1, var4, var5);
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return (var1 & 8) != 0 ? 0 : (this.material == Material.ORE ? Item.IRON_DOOR.id : Item.WOOD_DOOR.id);
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
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var3 >= 255 ? false : var1.t(var2, var3 - 1, var4) && super.canPlace(var1, var2, var3, var4) && super.canPlace(var1, var2, var3 + 1, var4);
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int q_()
    {
        return 1;
    }

    /**
     * Returns the full metadata value created by combining the metadata of both blocks the door takes up.
     */
    public int b_(IBlockAccess var1, int var2, int var3, int var4)
    {
        int var5 = var1.getData(var2, var3, var4);
        boolean var6 = (var5 & 8) != 0;
        int var7;
        int var8;

        if (var6)
        {
            var7 = var1.getData(var2, var3 - 1, var4);
            var8 = var5;
        }
        else
        {
            var7 = var5;
            var8 = var1.getData(var2, var3 + 1, var4);
        }

        boolean var9 = (var8 & 1) != 0;
        return var7 & 7 | (var6 ? 8 : 0) | (var9 ? 16 : 0);
    }

    @SideOnly(Side.CLIENT)
    public int a(World var1, int var2, int var3, int var4)
    {
        return this.material == Material.ORE ? Item.IRON_DOOR.id : Item.WOOD_DOOR.id;
    }
}
