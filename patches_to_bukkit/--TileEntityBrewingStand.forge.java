package net.minecraft.server;

import java.util.List;

public class TileEntityBrewingStand extends TileEntity implements IInventory
{
    /** The itemstacks currently placed in the slots of the brewing stand */
    private ItemStack[] items = new ItemStack[4];
    private int brewTime;

    /**
     * an integer with each bit specifying whether that slot of the stand contains a potion
     */
    private int c;
    private int d;

    /**
     * Returns the name of the inventory.
     */
    public String getName()
    {
        return "container.brewing";
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSize()
    {
        return this.items.length;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void g()
    {
        if (this.brewTime > 0)
        {
            --this.brewTime;

            if (this.brewTime == 0)
            {
                this.t();
                this.update();
            }
            else if (!this.k())
            {
                this.brewTime = 0;
                this.update();
            }
            else if (this.d != this.items[3].id)
            {
                this.brewTime = 0;
                this.update();
            }
        }
        else if (this.k())
        {
            this.brewTime = 400;
            this.d = this.items[3].id;
        }

        int var1 = this.i();

        if (var1 != this.c)
        {
            this.c = var1;
            this.world.setData(this.x, this.y, this.z, var1);
        }

        super.g();
    }

    public int x_()
    {
        return this.brewTime;
    }

    private boolean k()
    {
        if (this.items[3] != null && this.items[3].count > 0)
        {
            ItemStack var1 = this.items[3];

            if (!Item.byId[var1.id].v())
            {
                return false;
            }
            else
            {
                boolean var2 = false;

                for (int var3 = 0; var3 < 3; ++var3)
                {
                    if (this.items[var3] != null && this.items[var3].id == Item.POTION.id)
                    {
                        int var4 = this.items[var3].getData();
                        int var5 = this.b(var4, var1);

                        if (!ItemPotion.g(var4) && ItemPotion.g(var5))
                        {
                            var2 = true;
                            break;
                        }

                        List var6 = Item.POTION.f(var4);
                        List var7 = Item.POTION.f(var5);

                        if ((var4 <= 0 || var6 != var7) && (var6 == null || !var6.equals(var7) && var7 != null) && var4 != var5)
                        {
                            var2 = true;
                            break;
                        }
                    }
                }

                return var2;
            }
        }
        else
        {
            return false;
        }
    }

    private void t()
    {
        if (this.k())
        {
            ItemStack var1 = this.items[3];

            for (int var2 = 0; var2 < 3; ++var2)
            {
                if (this.items[var2] != null && this.items[var2].id == Item.POTION.id)
                {
                    int var3 = this.items[var2].getData();
                    int var4 = this.b(var3, var1);
                    List var5 = Item.POTION.f(var3);
                    List var6 = Item.POTION.f(var4);

                    if ((var3 <= 0 || var5 != var6) && (var5 == null || !var5.equals(var6) && var6 != null))
                    {
                        if (var3 != var4)
                        {
                            this.items[var2].setData(var4);
                        }
                    }
                    else if (!ItemPotion.g(var3) && ItemPotion.g(var4))
                    {
                        this.items[var2].setData(var4);
                    }
                }
            }

            if (Item.byId[var1.id].s())
            {
                this.items[3] = new ItemStack(Item.byId[var1.id].r());
            }
            else
            {
                --this.items[3].count;

                if (this.items[3].count <= 0)
                {
                    this.items[3] = null;
                }
            }
        }
    }

    /**
     * Returns the new potion damage value after the specified item is applied as an ingredient to the specified potion.
     */
    private int b(int var1, ItemStack var2)
    {
        return var2 == null ? var1 : (Item.byId[var2.id].v() ? PotionBrewer.a(var1, Item.byId[var2.id].u()) : var1);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        NBTTagList var2 = var1.getList("Items");
        this.items = new ItemStack[this.getSize()];

        for (int var3 = 0; var3 < var2.size(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.get(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.items.length)
            {
                this.items[var5] = ItemStack.a(var4);
            }
        }

        this.brewTime = var1.getShort("BrewTime");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.setShort("BrewTime", (short)this.brewTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.items.length; ++var3)
        {
            if (this.items[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.items[var3].save(var4);
                var2.add(var4);
            }
        }

        var1.set("Items", var2);
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getItem(int var1)
    {
        return var1 >= 0 && var1 < this.items.length ? this.items[var1] : null;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack splitStack(int var1, int var2)
    {
        if (var1 >= 0 && var1 < this.items.length)
        {
            ItemStack var3 = this.items[var1];
            this.items[var1] = null;
            return var3;
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack splitWithoutUpdate(int var1)
    {
        if (var1 >= 0 && var1 < this.items.length)
        {
            ItemStack var2 = this.items[var1];
            this.items[var1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setItem(int var1, ItemStack var2)
    {
        if (var1 >= 0 && var1 < this.items.length)
        {
            this.items[var1] = var2;
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getMaxStackSize()
    {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean a(EntityHuman var1)
    {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : var1.e((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 64.0D;
    }

    public void startOpen() {}

    public void f() {}

    /**
     * returns an integer with each bit specifying wether that slot of the stand contains a potion
     */
    public int i()
    {
        int var1 = 0;

        for (int var2 = 0; var2 < 3; ++var2)
        {
            if (this.items[var2] != null)
            {
                var1 |= 1 << var2;
            }
        }

        return var1;
    }
}
