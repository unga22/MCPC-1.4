package cpw.mods.fml.common.registry;

import net.minecraft.server.Entity;

public interface IThrowableEntity
{
    Entity getThrower();

    void setThrower(Entity var1);
}
