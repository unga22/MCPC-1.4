package net.minecraftforge.common;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityMinecart;

public interface IMinecartCollisionHandler
{
    void onEntityCollision(EntityMinecart var1, Entity var2);

    AxisAlignedBB getCollisionBox(EntityMinecart var1, Entity var2);

    AxisAlignedBB getMinecartCollisionBox(EntityMinecart var1);

    AxisAlignedBB getBoundingBox(EntityMinecart var1);
}
