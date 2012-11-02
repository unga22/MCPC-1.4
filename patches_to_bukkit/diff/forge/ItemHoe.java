package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event$Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemHoe extends Item
{
    protected EnumToolMaterial a;

    public ItemHoe(int var1, EnumToolMaterial var2)
    {
        super(var1);
        this.a = var2;
        this.maxStackSize = 1;
        this.setMaxDurability(var2.a());
        this.a(CreativeModeTab.i);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10)
    {
        if (!var2.func_82247_a(var4, var5, var6, var7, var1))
        {
            return false;
        }
        else
        {
            UseHoeEvent var11 = new UseHoeEvent(var2, var1, var3, var4, var5, var6);

            if (MinecraftForge.EVENT_BUS.post(var11))
            {
                return false;
            }
            else if (var11.getResult() == Event$Result.ALLOW)
            {
                var1.damage(1, var2);
                return true;
            }
            else
            {
                int var12 = var3.getTypeId(var4, var5, var6);
                int var13 = var3.getTypeId(var4, var5 + 1, var6);

                if ((var7 == 0 || var13 != 0 || var12 != Block.GRASS.id) && var12 != Block.DIRT.id)
                {
                    return false;
                }
                else
                {
                    Block var14 = Block.SOIL;
                    var3.makeSound((double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), var14.stepSound.getName(), (var14.stepSound.getVolume1() + 1.0F) / 2.0F, var14.stepSound.getVolume2() * 0.8F);

                    if (var3.isStatic)
                    {
                        return true;
                    }
                    else
                    {
                        var3.setTypeId(var4, var5, var6, var14.id);
                        var1.damage(1, var2);
                        return true;
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean n_()
    {
        return true;
    }

    public String func_77842_f()
    {
        return this.a.toString();
    }
}
