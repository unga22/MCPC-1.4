package cpw.mods.fml.common;

import java.util.EnumSet;

public enum TickType
{
    WORLD,
    RENDER,
    GUI,
    CLIENTGUI,
    WORLDLOAD,
    CLIENT,
    PLAYER,
    SERVER;

    public EnumSet partnerTicks()
    {
        return this == CLIENT ? EnumSet.of(RENDER) : (this == RENDER ? EnumSet.of(CLIENT) : (this == GUI ? EnumSet.of(CLIENTGUI) : (this == CLIENTGUI ? EnumSet.of(GUI) : EnumSet.noneOf(TickType.class))));
    }
}
