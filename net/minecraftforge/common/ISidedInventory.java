package net.minecraftforge.common;

import net.minecraft.server.IInventory;

public interface ISidedInventory extends IInventory
{
    int getStartInventorySide(ForgeDirection var1);

    int getSizeInventorySide(ForgeDirection var1);
}
