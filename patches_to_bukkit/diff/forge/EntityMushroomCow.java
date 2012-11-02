package net.minecraft.server;

import java.util.ArrayList;
import net.minecraftforge.common.IShearable;

public class EntityMushroomCow extends EntityCow implements IShearable
{
    public EntityMushroomCow(World var1)
    {
        super(var1);
        this.texture = "/mob/redcow.png";
        this.a(0.9F, 1.3F);
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean c(EntityHuman var1)
    {
        ItemStack var2 = var1.inventory.getItemInHand();

        if (var2 != null && var2.id == Item.BOWL.id && this.getAge() >= 0)
        {
            if (var2.count == 1)
            {
                var1.inventory.setItem(var1.inventory.itemInHandIndex, new ItemStack(Item.MUSHROOM_SOUP));
                return true;
            }

            if (var1.inventory.pickup(new ItemStack(Item.MUSHROOM_SOUP)) && !var1.abilities.canInstantlyBuild)
            {
                var1.inventory.splitStack(var1.inventory.itemInHandIndex, 1);
                return true;
            }
        }

        return super.c(var1);
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public EntityAnimal createChild(EntityAnimal var1)
    {
        return new EntityMushroomCow(this.world);
    }

    public boolean isShearable(ItemStack var1, World var2, int var3, int var4, int var5)
    {
        return this.getAge() >= 0;
    }

    public ArrayList onSheared(ItemStack var1, World var2, int var3, int var4, int var5, int var6)
    {
        this.die();
        EntityCow var7 = new EntityCow(this.world);
        var7.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        var7.setHealth(this.getHealth());
        var7.aw = this.aw;
        this.world.addEntity(var7);
        this.world.addParticle("largeexplode", this.locX, this.locY + (double)(this.length / 2.0F), this.locZ, 0.0D, 0.0D, 0.0D);
        ArrayList var8 = new ArrayList();

        for (int var9 = 0; var9 < 5; ++var9)
        {
            var8.add(new ItemStack(Block.RED_MUSHROOM));
        }

        return var8;
    }
}
