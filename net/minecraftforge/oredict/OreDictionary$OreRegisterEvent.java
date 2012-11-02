package net.minecraftforge.oredict;

import net.minecraft.server.ItemStack;
import net.minecraftforge.event.Event;

public class OreDictionary$OreRegisterEvent extends Event
{
    public final String Name;
    public final ItemStack Ore;

    public OreDictionary$OreRegisterEvent(String var1, ItemStack var2)
    {
        this.Name = var1;
        this.Ore = var2;
    }
}
