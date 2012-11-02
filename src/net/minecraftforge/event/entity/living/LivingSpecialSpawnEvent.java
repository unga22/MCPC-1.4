package net.minecraftforge.event.entity.living;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingSpecialSpawnEvent extends LivingEvent
{
    public final World world;
    public final float x;
    public final float y;
    public final float z;
    private boolean handeled = false;

    public LivingSpecialSpawnEvent(EntityLiving var1, World var2, float var3, float var4, float var5)
    {
        super(var1);
        this.world = var2;
        this.x = var3;
        this.y = var4;
        this.z = var5;
    }
}
