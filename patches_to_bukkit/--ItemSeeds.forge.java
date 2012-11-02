package net.minecraft.server;

public class ItemSeeds extends Item
{
    /**
     * The type of block this seed turns into (wheat or pumpkin stems for instance)
     */
    private int id;

    /** BlockID of the block the seeds can be planted on. */
    private int b;

    public ItemSeeds(int var1, int var2, int var3)
    {
        super(var1);
        this.id = var2;
        this.b = var3;
        this.a(CreativeModeTab.l);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10)
    {
        if (var7 != 1)
        {
            return false;
        }
        else if (var2.func_82247_a(var4, var5, var6, var7, var1) && var2.func_82247_a(var4, var5 + 1, var6, var7, var1))
        {
            int var11 = var3.getTypeId(var4, var5, var6);

            if (var11 == this.b && var3.isEmpty(var4, var5 + 1, var6))
            {
                var3.setTypeId(var4, var5 + 1, var6, this.id);
                --var1.count;
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
