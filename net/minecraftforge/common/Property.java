package net.minecraftforge.common;

import net.minecraftforge.common.Property$Type;

public class Property
{
    private String name;
    public String value;
    public String comment;
    private Property$Type type;

    public Property() {}

    public Property(String var1, String var2, Property$Type var3)
    {
        this.setName(var1);
        this.value = var2;
        this.type = var3;
    }

    public int getInt()
    {
        return this.getInt(-1);
    }

    public int getInt(int var1)
    {
        try
        {
            return Integer.parseInt(this.value);
        }
        catch (NumberFormatException var3)
        {
            return var1;
        }
    }

    public boolean isIntValue()
    {
        try
        {
            Integer.parseInt(this.value);
            return true;
        }
        catch (NumberFormatException var2)
        {
            return false;
        }
    }

    public boolean getBoolean(boolean var1)
    {
        return this.isBooleanValue() ? Boolean.parseBoolean(this.value) : var1;
    }

    public boolean isBooleanValue()
    {
        return "true".equals(this.value.toLowerCase()) || "false".equals(this.value.toLowerCase());
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String var1)
    {
        this.name = var1;
    }
}
