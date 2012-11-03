package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.inventory.Recipe;

import net.minecraft.server.Block;
import net.minecraft.server.IRecipe;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ShapelessOreRecipe implements IRecipe
{
    private ItemStack output;
    private ArrayList input;

    public ShapelessOreRecipe(Block var1, Object ... var2)
    {
        this(new ItemStack(var1), var2);
    }

    public ShapelessOreRecipe(Item var1, Object ... var2)
    {
        this(new ItemStack(var1), var2);
    }

    public ShapelessOreRecipe(ItemStack var1, Object ... var2)
    {
        this.output = null;
        this.input = new ArrayList();
        this.output = var1.cloneItemStack();
        Object[] var3 = var2;
        int var4 = var2.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            Object var6 = var3[var5];

            if (var6 instanceof ItemStack)
            {
                this.input.add(((ItemStack)var6).cloneItemStack());
            }
            else if (var6 instanceof Item)
            {
                this.input.add(new ItemStack((Item)var6));
            }
            else if (var6 instanceof Block)
            {
                this.input.add(new ItemStack((Block)var6));
            }
            else
            {
                if (!(var6 instanceof String))
                {
                    String var7 = "Invalid shapeless ore recipe: ";
                    Object[] var8 = var2;
                    int var9 = var2.length;

                    for (int var10 = 0; var10 < var9; ++var10)
                    {
                        Object var11 = var8[var10];
                        var7 = var7 + var11 + ", ";
                    }

                    var7 = var7 + this.output;
                    throw new RuntimeException(var7);
                }

                this.input.add(OreDictionary.getOres((String)var6));
            }
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int a()
    {
        return this.input.size();
    }

    public ItemStack b()
    {
        return this.output;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack a(InventoryCrafting var1)
    {
        return this.output.cloneItemStack();
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean a(InventoryCrafting var1, World var2)
    {
        ArrayList var3 = new ArrayList(this.input);

        for (int var4 = 0; var4 < var1.getSize(); ++var4)
        {
            ItemStack var5 = var1.getItem(var4);

            if (var5 != null)
            {
                boolean var6 = false;
                Iterator var7 = var3.iterator();

                while (var7.hasNext())
                {
                    boolean var8 = false;
                    Object var9 = var7.next();

                    if (var9 instanceof ItemStack)
                    {
                        var8 = this.checkItemEquals((ItemStack)var9, var5);
                    }
                    else
                    {
                        ItemStack var11;

                        if (var9 instanceof ArrayList)
                        {
                            for (Iterator var10 = ((ArrayList)var9).iterator(); var10.hasNext(); var8 = var8 || this.checkItemEquals(var11, var5))
                            {
                                var11 = (ItemStack)var10.next();
                            }
                        }
                    }

                    if (var8)
                    {
                        var6 = true;
                        var3.remove(var9);
                        break;
                    }
                }

                if (!var6)
                {
                    return false;
                }
            }
        }

        return var3.isEmpty();
    }

    private boolean checkItemEquals(ItemStack var1, ItemStack var2)
    {
        return var1.id == var2.id && (var1.getData() == -1 || var1.getData() == var2.getData());
    }

	@Override
	public Recipe toBukkitRecipe() {
		// TODO Auto-generated method stub
		return null;
	}
}
