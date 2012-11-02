package cpw.mods.fml.common.event;

import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.event.FMLInterModComms$1;
import cpw.mods.fml.common.event.FMLInterModComms$IMCMessage;

public class FMLInterModComms
{
    private static ArrayListMultimap modMessages = ArrayListMultimap.create();

    public static boolean sendMessage(String var0, String var1, String var2)
    {
        if (Loader.instance().activeModContainer() == null)
        {
            return false;
        }
        else
        {
            modMessages.put(var0, new FMLInterModComms$IMCMessage(Loader.instance().activeModContainer().getModId(), var1, var2, (FMLInterModComms$1)null));
            return Loader.isModLoaded(var0) && !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
        }
    }

    static ArrayListMultimap access$000()
    {
        return modMessages;
    }
}
