package net.minecraft.server;

public class ItemShears extends Item
{
    public ItemShears(int var1)
    {
        super(var1);
        this.d(1);
        this.setMaxDurability(238);
        this.a(CreativeModeTab.i);
    }

    public boolean a(ItemStack var1, World var2, int var3, int var4, int var5, int var6, EntityLiving var7)
    {
        if (var3 != Block.LEAVES.id && var3 != Block.WEB.id && var3 != Block.LONG_GRASS.id && var3 != Block.VINE.id && var3 != Block.TRIPWIRE.id)
        {
            return super.a(var1, var2, var3, var4, var5, var6, var7);
        }
        else
        {
            var1.damage(1, var7);
            return true;
        }
    }

    /**
     * Returns if the item (tool) can harvest results from the block type.
     */
    public boolean canDestroySpecialBlock(Block var1)
    {
        return var1.id == Block.WEB.id || var1.id == Block.REDSTONE_WIRE.id || var1.id == Block.TRIPWIRE.id;
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base, (Quality+1)*2 if correct blocktype, 1.5F if
     * sword
     */
    public float getDestroySpeed(ItemStack var1, Block var2)
    {
        return var2.id != Block.WEB.id && var2.id != Block.LEAVES.id ? (var2.id == Block.WOOL.id ? 5.0F : super.getDestroySpeed(var1, var2)) : 15.0F;
    }
}
