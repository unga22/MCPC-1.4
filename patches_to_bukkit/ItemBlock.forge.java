package net.minecraft.server;

public class ItemBlock extends Item
{
    /** The block ID of the Block associated with this ItemBlock */
    private int id;

    public ItemBlock(int var1)
    {
        super(var1);
        this.id = var1 + 256;
        this.c(Block.byId[var1 + 256].a(2));
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
        else if (var11 != Block.VINE.id && var11 != Block.LONG_GRASS.id && var11 != Block.DEAD_BUSH.id)
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

            if (var3.setTypeIdAndData(var4, var5, var6, this.id, this.filterData(var1.getData())))
            {
                if (var3.getTypeId(var4, var5, var6) == this.id)
                {
                    Block.byId[this.id].postPlace(var3, var4, var5, var6, var7, var8, var9, var10);
                    Block.byId[this.id].postPlace(var3, var4, var5, var6, var2);
                }

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

    public String c_(ItemStack var1)
    {
        return Block.byId[this.id].a();
    }

    public String getName()
    {
        return Block.byId[this.id].a();
    }
}
