package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;

public class ItemBlock extends Item
{
    /** The block ID of the Block associated with this ItemBlock */
    private int id;

    public ItemBlock(int var1)
    {
        super(var1);
        this.id = var1 + 256;
        this.c(Block.byId[var1 + 256].a(2));
        this.isDefaultTexture = Block.byId[var1 + 256].isDefaultTexture;
    }

    /**
     * Returns the blockID for this Item
     */
    public int g()
    {
        return this.id;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10)
    {
        int var11 = var3.getTypeId(var4, var5, var6);

        if (var11 == Block.SNOW.id)
        {
            var7 = 1;
        }
        else if (var11 != Block.VINE.id && var11 != Block.LONG_GRASS.id && var11 != Block.DEAD_BUSH.id && (Block.byId[var11] == null || !Block.byId[var11].isBlockReplaceable(var3, var4, var5, var6)))
        {
            if (var7 == 0)
            {
                --var5;
            }

            if (var7 == 1)
            {
                ++var5;
            }

            if (var7 == 2)
            {
                --var6;
            }

            if (var7 == 3)
            {
                ++var6;
            }

            if (var7 == 4)
            {
                --var4;
            }

            if (var7 == 5)
            {
                ++var4;
            }
        }

        if (var1.count == 0)
        {
            return false;
        }
        else if (!var2.func_82247_a(var4, var5, var6, var7, var1))
        {
            return false;
        }
        else if (var5 == 255 && Block.byId[this.id].material.isBuildable())
        {
            return false;
        }
        else if (var3.mayPlace(this.id, var4, var5, var6, false, var7, var2))
        {
            Block var12 = Block.byId[this.id];

            if (this.placeBlockAt(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10))
            {
                var3.makeSound((double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), var12.stepSound.func_82593_b(), (var12.stepSound.getVolume1() + 1.0F) / 2.0F, var12.stepSound.getVolume2() * 0.8F);
                --var1.count;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean a(World var1, int var2, int var3, int var4, int var5, EntityHuman var6, ItemStack var7)
    {
        int var8 = var1.getTypeId(var2, var3, var4);

        if (var8 == Block.SNOW.id)
        {
            var5 = 1;
        }
        else if (var8 != Block.VINE.id && var8 != Block.LONG_GRASS.id && var8 != Block.DEAD_BUSH.id && (Block.byId[var8] == null || !Block.byId[var8].isBlockReplaceable(var1, var2, var3, var4)))
        {
            if (var5 == 0)
            {
                --var3;
            }

            if (var5 == 1)
            {
                ++var3;
            }

            if (var5 == 2)
            {
                --var4;
            }

            if (var5 == 3)
            {
                ++var4;
            }

            if (var5 == 4)
            {
                --var2;
            }

            if (var5 == 5)
            {
                ++var2;
            }
        }

        return var1.mayPlace(this.g(), var2, var3, var4, false, var5, (Entity)null);
    }

    public String c_(ItemStack var1)
    {
        return Block.byId[this.id].a();
    }

    public String getName()
    {
        return Block.byId[this.id].a();
    }

    @SideOnly(Side.CLIENT)
    public CreativeModeTab w()
    {
        return Block.byId[this.id].E();
    }

    @SideOnly(Side.CLIENT)
    public void a(int var1, CreativeModeTab var2, List var3)
    {
        Block.byId[this.id].a(var1, var2, var3);
    }

    public boolean placeBlockAt(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10)
    {
        if (!var3.setTypeIdAndData(var4, var5, var6, this.id, this.filterData(var1.getData())))
        {
            return false;
        }
        else
        {
            if (var3.getTypeId(var4, var5, var6) == this.id)
            {
                Block.byId[this.id].postPlace(var3, var4, var5, var6, var7, var8, var9, var10);
                Block.byId[this.id].postPlace(var3, var4, var5, var6, var2);
            }

            return true;
        }
    }
}
