package net.minecraft.server;

public class EntityMushroomCow extends EntityCow
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

        if (var2 != null && var2.id == Item.SHEARS.id && this.getAge() >= 0)
        {
            this.die();
            this.world.addParticle("largeexplode", this.locX, this.locY + (double)(this.length / 2.0F), this.locZ, 0.0D, 0.0D, 0.0D);

            if (!this.world.isStatic)
            {
                EntityCow var3 = new EntityCow(this.world);
                var3.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
                var3.setHealth(this.getHealth());
                var3.aw = this.aw;
                this.world.addEntity(var3);

                for (int var4 = 0; var4 < 5; ++var4)
                {
                    this.world.addEntity(new EntityItem(this.world, this.locX, this.locY + (double)this.length, this.locZ, new ItemStack(Block.RED_MUSHROOM)));
                }
            }

            return true;
        }
        else
        {
            return super.c(var1);
        }
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public EntityAnimal createChild(EntityAnimal var1)
    {
        return new EntityMushroomCow(this.world);
    }
}
