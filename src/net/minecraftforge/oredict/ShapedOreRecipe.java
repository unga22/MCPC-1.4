package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.inventory.Recipe;

import net.minecraft.server.Block;
import net.minecraft.server.IRecipe;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ShapedOreRecipe implements IRecipe
{
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;
    private ItemStack output;
    private Object[] input;
    private int width;
    private int height;
    private boolean mirrored;

    public ShapedOreRecipe(Block var1, Object ... var2)
    {
        this(new ItemStack(var1), var2);
    }

    public ShapedOreRecipe(Item var1, Object ... var2)
    {
        this(new ItemStack(var1), var2);
    }

    public ShapedOreRecipe(ItemStack var1, Object ... var2)
    {
        this.output = null;
        this.input = null;
        this.width = 0;
        this.height = 0;
        this.mirrored = true;
        this.output = var1.cloneItemStack();
        String var3 = "";
        int var4 = 0;

        if (var2[var4] instanceof Boolean)
        {
            this.mirrored = ((Boolean)var2[var4]).booleanValue();

            if (var2[var4 + 1] instanceof Object[])
            {
                var2 = (Object[])((Object[])var2[var4 + 1]);
            }
            else
            {
                var4 = 1;
            }
        }

        int var7;
        int var8;
        String var9;
        String var14;

        if (var2[var4] instanceof String[])
        {
            String[] var5 = (String[])((String[])var2[var4++]);
            String[] var6 = var5;
            var7 = var5.length;

            for (var8 = 0; var8 < var7; ++var8)
            {
                var9 = var6[var8];
                this.width = var9.length();
                var3 = var3 + var9;
            }

            this.height = var5.length;
        }
        else
        {
            while (var2[var4] instanceof String)
            {
                var14 = (String)var2[var4++];
                var3 = var3 + var14;
                this.width = var14.length();
                ++this.height;
            }
        }

        if (this.width * this.height != var3.length())
        {
            var14 = "Invalid shaped ore recipe: ";
            Object[] var19 = var2;
            var7 = var2.length;

            for (var8 = 0; var8 < var7; ++var8)
            {
                Object var22 = var19[var8];
                var14 = var14 + var22 + ", ";
            }

            var14 = var14 + this.output;
            throw new RuntimeException(var14);
        }
        else
        {
            HashMap var15;

            for (var15 = new HashMap(); var4 < var2.length; var4 += 2)
            {
                Character var17 = (Character)var2[var4];
                Object var18 = var2[var4 + 1];
                Object var21 = null;

                if (var18 instanceof ItemStack)
                {
                    var15.put(var17, ((ItemStack)var18).cloneItemStack());
                }
                else if (var18 instanceof Item)
                {
                    var15.put(var17, new ItemStack((Item)var18));
                }
                else if (var18 instanceof Block)
                {
                    var15.put(var17, new ItemStack((Block)var18, 1, -1));
                }
                else
                {
                    if (!(var18 instanceof String))
                    {
                        var9 = "Invalid shaped ore recipe: ";
                        Object[] var10 = var2;
                        int var11 = var2.length;

                        for (int var12 = 0; var12 < var11; ++var12)
                        {
                            Object var13 = var10[var12];
                            var9 = var9 + var13 + ", ";
                        }

                        var9 = var9 + this.output;
                        throw new RuntimeException(var9);
                    }

                    var15.put(var17, OreDictionary.getOres((String)var18));
                }
            }

            this.input = new Object[this.width * this.height];
            int var16 = 0;
            char[] var20 = var3.toCharArray();
            var8 = var20.length;

            for (int var23 = 0; var23 < var8; ++var23)
            {
                char var24 = var20[var23];
                this.input[var16++] = var15.get(Character.valueOf(var24));
            }
        }
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack a(InventoryCrafting var1)
    {
        return this.output.cloneItemStack();
    }

    /**
     * Returns the size of the recipe area
     */
    public int a()
    {
        return this.input.length;
    }

    public ItemStack b()
    {
        return this.output;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean a(InventoryCrafting var1, World var2)
    {
        for (int var3 = 0; var3 <= 3 - this.width; ++var3)
        {
            for (int var4 = 0; var4 <= 3 - this.height; ++var4)
            {
                if (this.checkMatch(var1, var3, var4, true))
                {
                    return true;
                }

                if (this.mirrored && this.checkMatch(var1, var3, var4, false))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting var1, int var2, int var3, boolean var4)
    {
        for (int var5 = 0; var5 < 3; ++var5)
        {
            for (int var6 = 0; var6 < 3; ++var6)
            {
                int var7 = var5 - var2;
                int var8 = var6 - var3;
                Object var9 = null;

                if (var7 >= 0 && var8 >= 0 && var7 < this.width && var8 < this.height)
                {
                    if (var4)
                    {
                        var9 = this.input[this.width - var7 - 1 + var8 * this.width];
                    }
                    else
                    {
                        var9 = this.input[var7 + var8 * this.width];
                    }
                }

                ItemStack var10 = var1.b(var5, var6);

                if (var9 instanceof ItemStack)
                {
                    if (!this.checkItemEquals((ItemStack)var9, var10))
                    {
                        return false;
                    }
                }
                else if (var9 instanceof ArrayList)
                {
                    boolean var11 = false;
                    ItemStack var13;

                    for (Iterator var12 = ((ArrayList)var9).iterator(); var12.hasNext(); var11 = var11 || this.checkItemEquals(var13, var10))
                    {
                        var13 = (ItemStack)var12.next();
                    }

                    if (!var11)
                    {
                        return false;
                    }
                }
                else if (var9 == null && var10 != null)
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkItemEquals(ItemStack var1, ItemStack var2)
    {
        return (var2 != null || var1 == null) && (var2 == null || var1 != null) ? var1.id == var2.id && (var1.getData() == -1 || var1.getData() == var2.getData()) : false;
    }

    public ShapedOreRecipe setMirrored(boolean var1)
    {
        this.mirrored = var1;
        return this;
    }

	@Override
	public Recipe toBukkitRecipe() {
		// TODO Auto-generated method stub
		return null;
	}
}
