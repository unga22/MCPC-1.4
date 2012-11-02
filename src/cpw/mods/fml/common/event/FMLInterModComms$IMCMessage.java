package cpw.mods.fml.common.event;

import cpw.mods.fml.common.event.FMLInterModComms$1;

public final class FMLInterModComms$IMCMessage
{
    public final String sender;
    public final String key;
    public final String value;

    private FMLInterModComms$IMCMessage(String var1, String var2, String var3)
    {
        this.key = var2;
        this.value = var3;
        this.sender = var1;
    }

    public String toString()
    {
        return this.sender;
    }

    FMLInterModComms$IMCMessage(String var1, String var2, String var3, FMLInterModComms$1 var4)
    {
        this(var1, var2, var3);
    }
}
