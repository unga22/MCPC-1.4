package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Iterator;

public class ContainerFurnace extends Container
{
    private TileEntityFurnace furnace;
    private int f = 0;
    private int g = 0;
    private int h = 0;

    public ContainerFurnace(PlayerInventory var1, TileEntityFurnace var2)
    {
        this.furnace = var2;
        this.a(new Slot(var2, 0, 56, 17));
        this.a(new Slot(var2, 1, 56, 53));
        this.a(new SlotFurnaceResult(var1.player, var2, 2, 116, 35));
        int var3;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.a(new Slot(var1, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.a(new Slot(var1, var3, 8 + var3 * 18, 142));
        }
    }

    public void addSlotListener(ICrafting var1)
    {
        super.addSlotListener(var1);
        var1.setContainerData(this, 0, this.furnace.cookTime);
        var1.setContainerData(this, 1, this.furnace.burnTime);
        var1.setContainerData(this, 2, this.furnace.ticksForCurrentFuel);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    public void b()
    {
        super.b();
        Iterator var1 = this.listeners.iterator();

        while (var1.hasNext())
        {
            ICrafting var2 = (ICrafting)var1.next();

            if (this.f != this.furnace.cookTime)
            {
                var2.setContainerData(this, 0, this.furnace.cookTime);
            }

            if (this.g != this.furnace.burnTime)
            {
                var2.setContainerData(this, 1, this.furnace.burnTime);
            }

            if (this.h != this.furnace.ticksForCurrentFuel)
            {
                var2.setContainerData(this, 2, this.furnace.ticksForCurrentFuel);
            }
        }

        this.f = this.furnace.cookTime;
        this.g = this.furnace.burnTime;
        this.h = this.furnace.ticksForCurrentFuel;
    }

    @SideOnly(Side.CLIENT)
    public void b(int var1, int var2)
    {
        if (var1 == 0)
        {
            this.furnace.cookTime = var2;
        }

        if (var1 == 1)
        {
            this.furnace.burnTime = var2;
        }

        if (var1 == 2)
        {
            this.furnace.ticksForCurrentFuel = var2;
        }
    }

    public boolean c(EntityHuman var1)
    {
        return this.furnace.a(var1);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack b(EntityHuman var1, int var2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.b.get(var2);

        if (var4 != null && var4.d())
        {
            ItemStack var5 = var4.getItem();
            var3 = var5.cloneItemStack();

            if (var2 == 2)
            {
                if (!this.a(var5, 3, 39, true))
                {
                    return null;
                }

                var4.a(var5, var3);
            }
            else if (var2 != 1 && var2 != 0)
            {
                if (RecipesFurnace.getInstance().getSmeltingResult(var5) != null)
                {
                    if (!this.a(var5, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (TileEntityFurnace.isFuel(var5))
                {
                    if (!this.a(var5, 1, 2, false))
                    {
                        return null;
                    }
                }
                else if (var2 >= 3 && var2 < 30)
                {
                    if (!this.a(var5, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (var2 >= 30 && var2 < 39 && !this.a(var5, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.a(var5, 3, 39, false))
            {
                return null;
            }

            if (var5.count == 0)
            {
                var4.set((ItemStack)null);
            }
            else
            {
                var4.e();
            }

            if (var5.count == var3.count)
            {
                return null;
            }

            var4.a(var1, var5);
        }

        return var3;
    }
}
