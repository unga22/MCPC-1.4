package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.ItemArmor;
import net.minecraft.server.ItemStack;

public class ISpecialArmor$ArmorProperties implements Comparable
{
    public int Priority = 0;
    public int AbsorbMax = Integer.MAX_VALUE;
    public double AbsorbRatio = 0.0D;
    public int Slot = 0;
    private static final boolean DEBUG = false;

    public ISpecialArmor$ArmorProperties(int var1, double var2, int var4)
    {
        this.Priority = var1;
        this.AbsorbRatio = var2;
        this.AbsorbMax = var4;
    }

    public static int ApplyArmor(EntityLiving var0, ItemStack[] var1, DamageSource var2, double var3)
    {
        var3 *= 25.0D;
        ArrayList var5 = new ArrayList();

        for (int var6 = 0; var6 < var1.length; ++var6)
        {
            ItemStack var7 = var1[var6];

            if (var7 != null)
            {
                ISpecialArmor$ArmorProperties var8 = null;

                if (var7.getItem() instanceof ISpecialArmor)
                {
                    ISpecialArmor var9 = (ISpecialArmor)var7.getItem();
                    var8 = var9.getProperties(var0, var7, var2, var3 / 25.0D, var6).copy();
                }
                else if (var7.getItem() instanceof ItemArmor && !var2.ignoresArmor())
                {
                    ItemArmor var21 = (ItemArmor)var7.getItem();
                    var8 = new ISpecialArmor$ArmorProperties(0, (double)var21.b / 25.0D, var21.getMaxDurability() + 1 - var7.getData());
                }

                if (var8 != null)
                {
                    var8.Slot = var6;
                    var5.add(var8);
                }
            }
        }

        if (var5.size() > 0)
        {
            ISpecialArmor$ArmorProperties[] var18 = (ISpecialArmor$ArmorProperties[])var5.toArray(new ISpecialArmor$ArmorProperties[0]);
            StandardizeList(var18, var3);
            int var19 = var18[0].Priority;
            double var20 = 0.0D;
            ISpecialArmor$ArmorProperties[] var10 = var18;
            int var11 = var18.length;

            for (int var12 = 0; var12 < var11; ++var12)
            {
                ISpecialArmor$ArmorProperties var13 = var10[var12];

                if (var19 != var13.Priority)
                {
                    var3 -= var3 * var20;
                    var20 = 0.0D;
                    var19 = var13.Priority;
                }

                var20 += var13.AbsorbRatio;
                double var14 = var3 * var13.AbsorbRatio;

                if (var14 > 0.0D)
                {
                    ItemStack var16 = var1[var13.Slot];
                    int var17 = (int)(var14 / 25.0D < 1.0D ? 1.0D : var14 / 25.0D);

                    if (var16.getItem() instanceof ISpecialArmor)
                    {
                        ((ISpecialArmor)var16.getItem()).damageArmor(var0, var16, var2, var17, var13.Slot);
                    }
                    else
                    {
                        var16.damage(var17, var0);
                    }

                    if (var16.count <= 0)
                    {
                        var1[var13.Slot] = null;
                    }
                }
            }

            var3 -= var3 * var20;
        }

        var3 += (double)var0.aS;
        var0.aS = (int)var3 % 25;
        return (int)(var3 / 25.0D);
    }

    private static void StandardizeList(ISpecialArmor$ArmorProperties[] var0, double var1)
    {
        Arrays.sort(var0);
        int var3 = 0;
        double var4 = 0.0D;
        int var6 = var0[0].Priority;
        int var7 = 0;
        boolean var8 = false;
        boolean var9 = false;

        for (int var10 = 0; var10 < var0.length; ++var10)
        {
            var4 += var0[var10].AbsorbRatio;

            if (var10 == var0.length - 1 || var0[var10].Priority != var6)
            {
                if (var0[var10].Priority != var6)
                {
                    var4 -= var0[var10].AbsorbRatio;
                    --var10;
                    var8 = true;
                }

                int var11;

                if (var4 > 1.0D)
                {
                    for (var11 = var3; var11 <= var10; ++var11)
                    {
                        double var12 = var0[var11].AbsorbRatio / var4;

                        if (var12 * var1 > (double)var0[var11].AbsorbMax)
                        {
                            var0[var11].AbsorbRatio = (double)var0[var11].AbsorbMax / var1;
                            var4 = 0.0D;

                            for (int var14 = var7; var14 <= var11; ++var14)
                            {
                                var4 += var0[var14].AbsorbRatio;
                            }

                            var3 = var11 + 1;
                            var10 = var11;
                            break;
                        }

                        var0[var11].AbsorbRatio = var12;
                        var9 = true;
                    }

                    if (var8 && var9)
                    {
                        var1 -= var1 * var4;
                        var4 = 0.0D;
                        var3 = var10 + 1;
                        var6 = var0[var3].Priority;
                        var7 = var3;
                        var8 = false;
                        var9 = false;

                        if (var1 <= 0.0D)
                        {
                            for (var11 = var10 + 1; var11 < var0.length; ++var11)
                            {
                                var0[var11].AbsorbRatio = 0.0D;
                            }

                            return;
                        }
                    }
                }
                else
                {
                    for (var11 = var3; var11 <= var10; ++var11)
                    {
                        var4 -= var0[var11].AbsorbRatio;

                        if (var1 * var0[var11].AbsorbRatio > (double)var0[var11].AbsorbMax)
                        {
                            var0[var11].AbsorbRatio = (double)var0[var11].AbsorbMax / var1;
                        }

                        var4 += var0[var11].AbsorbRatio;
                    }

                    var1 -= var1 * var4;
                    var4 = 0.0D;

                    if (var10 != var0.length - 1)
                    {
                        var3 = var10 + 1;
                        var6 = var0[var3].Priority;
                        var7 = var3;
                        var8 = false;

                        if (var1 <= 0.0D)
                        {
                            for (var11 = var10 + 1; var11 < var0.length; ++var11)
                            {
                                var0[var11].AbsorbRatio = 0.0D;
                            }

                            return;
                        }
                    }
                }
            }
        }
    }

    public int compareTo(ISpecialArmor$ArmorProperties var1)
    {
        if (var1.Priority != this.Priority)
        {
            return var1.Priority - this.Priority;
        }
        else
        {
            double var2 = this.AbsorbRatio == 0.0D ? 0.0D : (double)this.AbsorbMax * 100.0D / this.AbsorbRatio;
            double var4 = var1.AbsorbRatio == 0.0D ? 0.0D : (double)var1.AbsorbMax * 100.0D / var1.AbsorbRatio;
            return (int)(var2 - var4);
        }
    }

    public String toString()
    {
        return String.format("%d, %d, %f, %d", new Object[] {Integer.valueOf(this.Priority), Integer.valueOf(this.AbsorbMax), Double.valueOf(this.AbsorbRatio), Integer.valueOf(this.AbsorbRatio == 0.0D ? 0 : (int)((double)this.AbsorbMax * 100.0D / this.AbsorbRatio))});
    }

    public ISpecialArmor$ArmorProperties copy()
    {
        return new ISpecialArmor$ArmorProperties(this.Priority, this.AbsorbRatio, this.AbsorbMax);
    }

    public int compareTo(Object var1)
    {
        return this.compareTo((ISpecialArmor$ArmorProperties)var1);
    }
}
