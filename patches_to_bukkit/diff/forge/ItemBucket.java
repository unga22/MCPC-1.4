package net.minecraft.server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event$Result;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class ItemBucket extends Item
{
    /** field for checking if the bucket has been filled. */
    private int a;

    public ItemBucket(int var1, int var2)
    {
        super(var1);
        this.maxStackSize = 1;
        this.a = var2;
        this.a(CreativeModeTab.f);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack a(ItemStack var1, World var2, EntityHuman var3)
    {
        float var4 = 1.0F;
        double var5 = var3.lastX + (var3.locX - var3.lastX) * (double)var4;
        double var7 = var3.lastY + (var3.locY - var3.lastY) * (double)var4 + 1.62D - (double)var3.height;
        double var9 = var3.lastZ + (var3.locZ - var3.lastZ) * (double)var4;
        boolean var11 = this.a == 0;
        MovingObjectPosition var12 = this.a(var2, var3, var11);

        if (var12 == null)
        {
            return var1;
        }
        else
        {
            FillBucketEvent var13 = new FillBucketEvent(var3, var1, var2, var12);

            if (MinecraftForge.EVENT_BUS.post(var13))
            {
                return var1;
            }
            else if (var13.getResult() == Event$Result.ALLOW)
            {
                if (var3.abilities.canInstantlyBuild)
                {
                    return var1;
                }
                else if (--var1.count <= 0)
                {
                    return var13.result;
                }
                else
                {
                    if (!var3.inventory.pickup(var13.result))
                    {
                        var3.drop(var13.result);
                    }

                    return var1;
                }
            }
            else
            {
                if (var12.type == EnumMovingObjectType.TILE)
                {
                    int var14 = var12.b;
                    int var15 = var12.c;
                    int var16 = var12.d;

                    if (!var2.a(var3, var14, var15, var16))
                    {
                        return var1;
                    }

                    if (this.a == 0)
                    {
                        if (!var3.func_82247_a(var14, var15, var16, var12.face, var1))
                        {
                            return var1;
                        }

                        if (var2.getMaterial(var14, var15, var16) == Material.WATER && var2.getData(var14, var15, var16) == 0)
                        {
                            var2.setTypeId(var14, var15, var16, 0);

                            if (var3.abilities.canInstantlyBuild)
                            {
                                return var1;
                            }

                            if (--var1.count <= 0)
                            {
                                return new ItemStack(Item.WATER_BUCKET);
                            }

                            if (!var3.inventory.pickup(new ItemStack(Item.WATER_BUCKET)))
                            {
                                var3.drop(new ItemStack(Item.WATER_BUCKET.id, 1, 0));
                            }

                            return var1;
                        }

                        if (var2.getMaterial(var14, var15, var16) == Material.LAVA && var2.getData(var14, var15, var16) == 0)
                        {
                            var2.setTypeId(var14, var15, var16, 0);

                            if (var3.abilities.canInstantlyBuild)
                            {
                                return var1;
                            }

                            if (--var1.count <= 0)
                            {
                                return new ItemStack(Item.LAVA_BUCKET);
                            }

                            if (!var3.inventory.pickup(new ItemStack(Item.LAVA_BUCKET)))
                            {
                                var3.drop(new ItemStack(Item.LAVA_BUCKET.id, 1, 0));
                            }

                            return var1;
                        }
                    }
                    else
                    {
                        if (this.a < 0)
                        {
                            return new ItemStack(Item.BUCKET);
                        }

                        if (var12.face == 0)
                        {
                            --var15;
                        }

                        if (var12.face == 1)
                        {
                            ++var15;
                        }

                        if (var12.face == 2)
                        {
                            --var16;
                        }

                        if (var12.face == 3)
                        {
                            ++var16;
                        }

                        if (var12.face == 4)
                        {
                            --var14;
                        }

                        if (var12.face == 5)
                        {
                            ++var14;
                        }

                        if (!var3.func_82247_a(var14, var15, var16, var12.face, var1))
                        {
                            return var1;
                        }

                        if (this.a(var2, var5, var7, var9, var14, var15, var16) && !var3.abilities.canInstantlyBuild)
                        {
                            return new ItemStack(Item.BUCKET);
                        }
                    }
                }
                else if (this.a == 0 && var12.entity instanceof EntityCow)
                {
                    return new ItemStack(Item.MILK_BUCKET);
                }

                return var1;
            }
        }
    }

    /**
     * Attempts to place the liquid contained inside the bucket.
     */
    public boolean a(World var1, double var2, double var4, double var6, int var8, int var9, int var10)
    {
        if (this.a <= 0)
        {
            return false;
        }
        else if (!var1.isEmpty(var8, var9, var10) && var1.getMaterial(var8, var9, var10).isBuildable())
        {
            return false;
        }
        else
        {
            if (var1.worldProvider.e && this.a == Block.WATER.id)
            {
                var1.makeSound(var2 + 0.5D, var4 + 0.5D, var6 + 0.5D, "random.fizz", 0.5F, 2.6F + (var1.random.nextFloat() - var1.random.nextFloat()) * 0.8F);

                for (int var11 = 0; var11 < 8; ++var11)
                {
                    var1.addParticle("largesmoke", (double)var8 + Math.random(), (double)var9 + Math.random(), (double)var10 + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            }
            else
            {
                var1.setTypeIdAndData(var8, var9, var10, this.a, 0);
            }

            return true;
        }
    }
}
