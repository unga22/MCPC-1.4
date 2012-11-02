package net.minecraftforge.common;

public class MinecartRegistry$MinecartKey
{
    public final Class minecart;
    public final int type;

    public MinecartRegistry$MinecartKey(Class var1, int var2)
    {
        this.minecart = var1;
        this.type = var2;
    }

    public boolean equals(Object var1)
    {
        if (var1 == null)
        {
            return false;
        }
        else if (this.getClass() != var1.getClass())
        {
            return false;
        }
        else
        {
            MinecartRegistry$MinecartKey var2 = (MinecartRegistry$MinecartKey)var1;
            return this.minecart != var2.minecart && (this.minecart == null || !this.minecart.equals(var2.minecart)) ? false : this.type == var2.type;
        }
    }

    public int hashCode()
    {
        byte var1 = 7;
        int var2 = 59 * var1 + (this.minecart != null ? this.minecart.hashCode() : 0);
        var2 = 59 * var2 + this.type;
        return var2;
    }
}
