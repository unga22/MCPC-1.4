package net.minecraft.server;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Iterator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event$Result;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class EntityItem extends Entity
{
    /** The item stack of this EntityItem. */
    public ItemStack itemStack;

    /**
     * The age of this EntityItem (used to animate it up and down as well as expire it)
     */
    public int age = 0;
    public int pickupDelay;

    /** The health of this EntityItem. (For example, damage for tools) */
    private int e = 5;

    /** The EntityItem's random initial float height. */
    public float d = (float)(Math.random() * Math.PI * 2.0D);
    public int lifespan = 6000;

    public EntityItem(World var1, double var2, double var4, double var6, ItemStack var8)
    {
        super(var1);
        this.a(0.25F, 0.25F);
        this.height = this.length / 2.0F;
        this.setPosition(var2, var4, var6);
        this.itemStack = var8;
        this.yaw = (float)(Math.random() * 360.0D);
        this.motX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motY = 0.20000000298023224D;
        this.motZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.lifespan = var8.getItem() == null ? 6000 : var8.getItem().getEntityLifespan(var8, var1);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean f_()
    {
        return false;
    }

    public EntityItem(World var1)
    {
        super(var1);
        this.a(0.25F, 0.25F);
        this.height = this.length / 2.0F;
    }

    protected void a() {}

    /**
     * Called to update the entity's position/logic.
     */
    public void j_()
    {
        super.j_();

        if (this.pickupDelay > 0)
        {
            --this.pickupDelay;
        }

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.motY -= 0.03999999910593033D;
        this.i(this.locX, (this.boundingBox.b + this.boundingBox.e) / 2.0D, this.locZ);
        this.move(this.motX, this.motY, this.motZ);
        boolean var1 = (int)this.lastX != (int)this.locX || (int)this.lastY != (int)this.locY || (int)this.lastZ != (int)this.locZ;

        if (var1)
        {
            if (this.world.getMaterial(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) == Material.LAVA)
            {
                this.motY = 0.20000000298023224D;
                this.motX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                this.motZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                this.world.makeSound(this, "random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }

            if (!this.world.isStatic)
            {
                Iterator var2 = this.world.a(EntityItem.class, this.boundingBox.grow(0.5D, 0.0D, 0.5D)).iterator();

                while (var2.hasNext())
                {
                    EntityItem var3 = (EntityItem)var2.next();
                    this.func_70289_a(var3);
                }
            }
        }

        float var4 = 0.98F;

        if (this.onGround)
        {
            var4 = 0.58800006F;
            int var5 = this.world.getTypeId(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));

            if (var5 > 0)
            {
                var4 = Block.byId[var5].frictionFactor * 0.98F;
            }
        }

        this.motX *= (double)var4;
        this.motY *= 0.9800000190734863D;
        this.motZ *= (double)var4;

        if (this.onGround)
        {
            this.motY *= -0.5D;
        }

        ++this.age;

        if (!this.world.isStatic && this.age >= this.lifespan)
        {
            ItemExpireEvent var6 = new ItemExpireEvent(this, this.itemStack.getItem() == null ? 6000 : this.itemStack.getItem().getEntityLifespan(this.itemStack, this.world));

            if (MinecraftForge.EVENT_BUS.post(var6))
            {
                this.lifespan += var6.extraLife;
            }
            else
            {
                this.die();
            }
        }

        if (this.itemStack == null || this.itemStack.count <= 0)
        {
            this.die();
        }
    }

    public boolean func_70289_a(EntityItem var1)
    {
        if (var1 == this)
        {
            return false;
        }
        else if (var1.isAlive() && this.isAlive())
        {
            if (var1.itemStack.getItem() != this.itemStack.getItem())
            {
                return false;
            }
            else if (!var1.itemStack.hasTag() && !this.itemStack.hasTag())
            {
                if (var1.itemStack.getItem().l() && var1.itemStack.getData() != this.itemStack.getData())
                {
                    return false;
                }
                else if (var1.itemStack.count < this.itemStack.count)
                {
                    return var1.func_70289_a(this);
                }
                else if (var1.itemStack.count + this.itemStack.count > var1.itemStack.getMaxStackSize())
                {
                    return false;
                }
                else
                {
                    var1.itemStack.count += this.itemStack.count;
                    var1.pickupDelay = Math.max(var1.pickupDelay, this.pickupDelay);
                    var1.age = Math.min(var1.age, this.age);
                    this.die();
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public void func_70288_d()
    {
        this.age = 4800;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean I()
    {
        return this.world.a(this.boundingBox, Material.WATER, this);
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void burn(int var1)
    {
        this.damageEntity(DamageSource.FIRE, var1);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean damageEntity(DamageSource var1, int var2)
    {
        this.K();
        this.e -= var2;

        if (this.e <= 0)
        {
            this.die();
        }

        return false;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        var1.setShort("Health", (short)((byte)this.e));
        var1.setShort("Age", (short)this.age);
        var1.setInt("Lifespan", this.lifespan);

        if (this.itemStack != null)
        {
            var1.setCompound("Item", this.itemStack.save(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        this.e = var1.getShort("Health") & 255;
        this.age = var1.getShort("Age");
        NBTTagCompound var2 = var1.getCompound("Item");
        this.itemStack = ItemStack.a(var2);

        if (this.itemStack == null || this.itemStack.count <= 0)
        {
            this.die();
        }

        if (var1.hasKey("Lifespan"))
        {
            this.lifespan = var1.getInt("Lifespan");
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void b_(EntityHuman var1)
    {
        if (!this.world.isStatic)
        {
            if (this.pickupDelay > 0)
            {
                return;
            }

            EntityItemPickupEvent var2 = new EntityItemPickupEvent(var1, this);

            if (MinecraftForge.EVENT_BUS.post(var2))
            {
                return;
            }

            int var3 = this.itemStack.count;

            if (this.pickupDelay <= 0 && (var2.getResult() == Event$Result.ALLOW || var3 <= 0 || var1.inventory.pickup(this.itemStack)))
            {
                if (this.itemStack.id == Block.LOG.id)
                {
                    var1.a(AchievementList.g);
                }

                if (this.itemStack.id == Item.LEATHER.id)
                {
                    var1.a(AchievementList.t);
                }

                if (this.itemStack.id == Item.DIAMOND.id)
                {
                    var1.a(AchievementList.w);
                }

                if (this.itemStack.id == Item.BLAZE_ROD.id)
                {
                    var1.a(AchievementList.z);
                }

                GameRegistry.onPickupNotification(var1, this);
                this.world.makeSound(this, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                var1.receive(this, var3);

                if (this.itemStack.count <= 0)
                {
                    this.die();
                }
            }
        }
    }

    /**
     * Gets the username of the entity.
     */
    public String getLocalizedName()
    {
        return LocaleI18n.get("item." + this.itemStack.a());
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean aq()
    {
        return false;
    }
}
