package cpw.mods.fml.common.event;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.ModContainer;

public class FMLInterModComms$IMCEvent extends FMLEvent
{
    private ImmutableList currentList;

    public void applyModContainer(ModContainer var1)
    {
        this.currentList = ImmutableList.copyOf(FMLInterModComms.access$000().get(var1.getModId()));
    }

    public ImmutableList getMessages()
    {
        return this.currentList;
    }
}
