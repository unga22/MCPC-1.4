package cpw.mods.fml.common;

import net.minecraft.server.EntityHuman;

public interface IPlayerTracker
{
    void onPlayerLogin(EntityHuman var1);

    void onPlayerLogout(EntityHuman var1);

    void onPlayerChangedDimension(EntityHuman var1);

    void onPlayerRespawn(EntityHuman var1);
}
