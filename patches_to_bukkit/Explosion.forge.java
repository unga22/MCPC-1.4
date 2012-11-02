package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Explosion
{
    /** whether or not the explosion sets fire to blocks around it */
    public boolean a = false;

    /** whether or not this explosion spawns smoke particles */
    public boolean b = true;
    private int field_77289_h = 16;
    private Random j = new Random();
    private World world;
    public double posX;
    public double posY;
    public double posZ;
    public Entity source;
    public float size;
    public List field_77281_g = new ArrayList();
    private Map field_77288_k = new HashMap();

    public Explosion(World var1, Entity var2, double var3, double var5, double var7, float var9)
    {
        this.world = var1;
        this.source = var2;
        this.size = var9;
        this.posX = var3;
        this.posY = var5;
        this.posZ = var7;
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    public void a()
    {
        float var1 = this.size;
        HashSet var2 = new HashSet();
        int var3;
        int var4;
        int var5;
        double var15;
        double var17;
        double var19;

        for (var3 = 0; var3 < this.field_77289_h; ++var3)
        {
            for (var4 = 0; var4 < this.field_77289_h; ++var4)
            {
                for (var5 = 0; var5 < this.field_77289_h; ++var5)
                {
                    if (var3 == 0 || var3 == this.field_77289_h - 1 || var4 == 0 || var4 == this.field_77289_h - 1 || var5 == 0 || var5 == this.field_77289_h - 1)
                    {
                        double var6 = (double)((float)var3 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                        double var8 = (double)((float)var4 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                        double var10 = (double)((float)var5 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                        double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                        var6 /= var12;
                        var8 /= var12;
                        var10 /= var12;
                        float var14 = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                        var15 = this.posX;
                        var17 = this.posY;
                        var19 = this.posZ;

                        for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
                        {
                            int var22 = MathHelper.floor(var15);
                            int var23 = MathHelper.floor(var17);
                            int var24 = MathHelper.floor(var19);
                            int var25 = this.world.getTypeId(var22, var23, var24);

                            if (var25 > 0)
                            {
                                Block var26 = Block.byId[var25];
                                float var27 = this.source != null ? this.source.func_82146_a(this, var26, var22, var23, var24) : var26.a(this.source);
                                var14 -= (var27 + 0.3F) * var21;
                            }

                            if (var14 > 0.0F)
                            {
                                var2.add(new ChunkPosition(var22, var23, var24));
                            }

                            var15 += var6 * (double)var21;
                            var17 += var8 * (double)var21;
                            var19 += var10 * (double)var21;
                        }
                    }
                }
            }
        }

        this.field_77281_g.addAll(var2);
        this.size *= 2.0F;
        var3 = MathHelper.floor(this.posX - (double)this.size - 1.0D);
        var4 = MathHelper.floor(this.posX + (double)this.size + 1.0D);
        var5 = MathHelper.floor(this.posY - (double)this.size - 1.0D);
        int var28 = MathHelper.floor(this.posY + (double)this.size + 1.0D);
        int var7 = MathHelper.floor(this.posZ - (double)this.size - 1.0D);
        int var29 = MathHelper.floor(this.posZ + (double)this.size + 1.0D);
        List var9 = this.world.getEntities(this.source, AxisAlignedBB.a().a((double)var3, (double)var5, (double)var7, (double)var4, (double)var28, (double)var29));
        Vec3D var30 = this.world.getVec3DPool().create(this.posX, this.posY, this.posZ);

        for (int var11 = 0; var11 < var9.size(); ++var11)
        {
            Entity var31 = (Entity)var9.get(var11);
            double var13 = var31.f(this.posX, this.posY, this.posZ) / (double)this.size;

            if (var13 <= 1.0D)
            {
                var15 = var31.locX - this.posX;
                var17 = var31.locY + (double)var31.getHeadHeight() - this.posY;
                var19 = var31.locZ - this.posZ;
                double var33 = (double)MathHelper.sqrt(var15 * var15 + var17 * var17 + var19 * var19);

                if (var33 != 0.0D)
                {
                    var15 /= var33;
                    var17 /= var33;
                    var19 /= var33;
                    double var32 = (double)this.world.a(var30, var31.boundingBox);
                    double var34 = (1.0D - var13) * var32;
                    var31.damageEntity(DamageSource.EXPLOSION, (int)((var34 * var34 + var34) / 2.0D * 8.0D * (double)this.size + 1.0D));
                    var31.motX += var15 * var34;
                    var31.motY += var17 * var34;
                    var31.motZ += var19 * var34;

                    if (var31 instanceof EntityHuman)
                    {
                        this.field_77288_k.put((EntityHuman)var31, this.world.getVec3DPool().create(var15 * var34, var17 * var34, var19 * var34));
                    }
                }
            }
        }

        this.size = var1;
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    public void a(boolean var1)
    {
        this.world.makeSound(this.posX, this.posY, this.posZ, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);

        if (this.size >= 2.0F && this.b)
        {
            this.world.addParticle("hugeexplosion", this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            this.world.addParticle("largeexplode", this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D);
        }

        Iterator var2;
        ChunkPosition var3;
        int var4;
        int var5;
        int var6;
        int var7;

        if (this.b)
        {
            var2 = this.field_77281_g.iterator();

            while (var2.hasNext())
            {
                var3 = (ChunkPosition)var2.next();
                var4 = var3.x;
                var5 = var3.y;
                var6 = var3.z;
                var7 = this.world.getTypeId(var4, var5, var6);

                if (var1)
                {
                    double var8 = (double)((float)var4 + this.world.random.nextFloat());
                    double var10 = (double)((float)var5 + this.world.random.nextFloat());
                    double var12 = (double)((float)var6 + this.world.random.nextFloat());
                    double var14 = var8 - this.posX;
                    double var16 = var10 - this.posY;
                    double var18 = var12 - this.posZ;
                    double var20 = (double)MathHelper.sqrt(var14 * var14 + var16 * var16 + var18 * var18);
                    var14 /= var20;
                    var16 /= var20;
                    var18 /= var20;
                    double var22 = 0.5D / (var20 / (double)this.size + 0.1D);
                    var22 *= (double)(this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
                    var14 *= var22;
                    var16 *= var22;
                    var18 *= var22;
                    this.world.addParticle("explode", (var8 + this.posX * 1.0D) / 2.0D, (var10 + this.posY * 1.0D) / 2.0D, (var12 + this.posZ * 1.0D) / 2.0D, var14, var16, var18);
                    this.world.addParticle("smoke", var8, var10, var12, var14, var16, var18);
                }

                if (var7 > 0)
                {
                    Block.byId[var7].dropNaturally(this.world, var4, var5, var6, this.world.getData(var4, var5, var6), 0.3F, 0);

                    if (this.world.setRawTypeIdAndData(var4, var5, var6, 0, 0, this.world.isStatic))
                    {
                        this.world.applyPhysics(var4, var5, var6, 0);
                    }

                    Block.byId[var7].wasExploded(this.world, var4, var5, var6);
                }
            }
        }

        if (this.a)
        {
            var2 = this.field_77281_g.iterator();

            while (var2.hasNext())
            {
                var3 = (ChunkPosition)var2.next();
                var4 = var3.x;
                var5 = var3.y;
                var6 = var3.z;
                var7 = this.world.getTypeId(var4, var5, var6);
                int var24 = this.world.getTypeId(var4, var5 - 1, var6);

                if (var7 == 0 && Block.q[var24] && this.j.nextInt(3) == 0)
                {
                    this.world.setTypeId(var4, var5, var6, Block.FIRE.id);
                }
            }
        }
    }

    public Map func_77277_b()
    {
        return this.field_77288_k;
    }
}
