package net.minecraftforge.common;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.server.EnchantmentSlotType;
import net.minecraft.server.EnumAnimation;
import net.minecraft.server.EnumArmorMaterial;
import net.minecraft.server.EnumArt;
import net.minecraft.server.EnumBedResult;
import net.minecraft.server.EnumCreatureType;
import net.minecraft.server.EnumEntitySize;
import net.minecraft.server.EnumMobType;
import net.minecraft.server.EnumMonsterType;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.EnumSkyBlock;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.Material;
import net.minecraft.server.WorldGenStrongholdDoorType;

public class EnumHelper
{
    private static Object reflectionFactory = null;
    private static Method newConstructorAccessor = null;
    private static Method newInstance = null;
    private static Method newFieldAccessor = null;
    private static Method fieldAccessorSet = null;
    private static boolean isSetup = false;
    private static Class[][] commonTypes = new Class[][] {{EnumAnimation.class}, {EnumArmorMaterial.class, Integer.TYPE, int[].class, Integer.TYPE}, {EnumArt.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, {EnumMonsterType.class}, {EnumCreatureType.class, Class.class, Integer.TYPE, Material.class, Boolean.TYPE}, {WorldGenStrongholdDoorType.class}, {EnchantmentSlotType.class}, {EnumEntitySize.class}, {EnumMobType.class}, {EnumMovingObjectType.class}, {EnumSkyBlock.class, Integer.TYPE}, {EnumBedResult.class}, {EnumToolMaterial.class, Integer.TYPE, Integer.TYPE, Float.TYPE, Integer.TYPE, Integer.TYPE}};

    public static EnumAnimation addAction(String var0)
    {
        return (EnumAnimation)addEnum(EnumAnimation.class, var0, new Object[0]);
    }

    public static EnumArmorMaterial addArmorMaterial(String var0, int var1, int[] var2, int var3)
    {
        return (EnumArmorMaterial)addEnum(EnumArmorMaterial.class, var0, new Object[] {Integer.valueOf(var1), var2, Integer.valueOf(var3)});
    }

    public static EnumArt addArt(String var0, String var1, int var2, int var3, int var4, int var5)
    {
        return (EnumArt)addEnum(EnumArt.class, var0, new Object[] {var1, Integer.valueOf(var2), Integer.valueOf(var3), Integer.valueOf(var4), Integer.valueOf(var5)});
    }

    public static EnumMonsterType addCreatureAttribute(String var0)
    {
        return (EnumMonsterType)addEnum(EnumMonsterType.class, var0, new Object[0]);
    }

    public static EnumCreatureType addCreatureType(String var0, Class var1, int var2, Material var3, boolean var4)
    {
        return (EnumCreatureType)addEnum(EnumCreatureType.class, var0, new Object[] {var1, Integer.valueOf(var2), var3, Boolean.valueOf(var4)});
    }

    public static WorldGenStrongholdDoorType addDoor(String var0)
    {
        return (WorldGenStrongholdDoorType)addEnum(WorldGenStrongholdDoorType.class, var0, new Object[0]);
    }

    public static EnchantmentSlotType addEnchantmentType(String var0)
    {
        return (EnchantmentSlotType)addEnum(EnchantmentSlotType.class, var0, new Object[0]);
    }

    public static EnumEntitySize addEntitySize(String var0)
    {
        return (EnumEntitySize)addEnum(EnumEntitySize.class, var0, new Object[0]);
    }

    public static EnumMobType addMobType(String var0)
    {
        return (EnumMobType)addEnum(EnumMobType.class, var0, new Object[0]);
    }

    public static EnumMovingObjectType addMovingObjectType(String var0)
    {
        if (!isSetup)
        {
            setup();
        }

        return (EnumMovingObjectType)addEnum(EnumMovingObjectType.class, var0, new Object[0]);
    }

    public static EnumSkyBlock addSkyBlock(String var0, int var1)
    {
        return (EnumSkyBlock)addEnum(EnumSkyBlock.class, var0, new Object[] {Integer.valueOf(var1)});
    }

    public static EnumBedResult addStatus(String var0)
    {
        return (EnumBedResult)addEnum(EnumBedResult.class, var0, new Object[0]);
    }

    public static EnumToolMaterial addToolMaterial(String var0, int var1, int var2, float var3, int var4, int var5)
    {
        return (EnumToolMaterial)addEnum(EnumToolMaterial.class, var0, new Object[] {Integer.valueOf(var1), Integer.valueOf(var2), Float.valueOf(var3), Integer.valueOf(var4), Integer.valueOf(var5)});
    }

    private static void setup()
    {
        if (!isSetup)
        {
            try
            {
                Method var0 = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory", new Class[0]);
                reflectionFactory = var0.invoke((Object)null, new Object[0]);
                newConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", new Class[] {Constructor.class});
                newInstance = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", new Class[] {Object[].class});
                newFieldAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", new Class[] {Field.class, Boolean.TYPE});
                fieldAccessorSet = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", new Class[] {Object.class, Object.class});
            }
            catch (Exception var1)
            {
                var1.printStackTrace();
            }

            isSetup = true;
        }
    }

    private static Object getConstructorAccessor(Class var0, Class[] var1) throws Exception
    {
        Class[] var2 = new Class[var1.length + 2];
        var2[0] = String.class;
        var2[1] = Integer.TYPE;
        System.arraycopy(var1, 0, var2, 2, var1.length);
        return newConstructorAccessor.invoke(reflectionFactory, new Object[] {var0.getDeclaredConstructor(var2)});
    }

    private static Enum makeEnum(Class var0, String var1, int var2, Class[] var3, Object[] var4) throws Exception
    {
        Object[] var5 = new Object[var4.length + 2];
        var5[0] = var1;
        var5[1] = Integer.valueOf(var2);
        System.arraycopy(var4, 0, var5, 2, var4.length);
        return (Enum)var0.cast(newInstance.invoke(getConstructorAccessor(var0, var3), new Object[] {var5}));
    }

    public static void setFailsafeFieldValue(Field var0, Object var1, Object var2) throws Exception
    {
        var0.setAccessible(true);
        Field var3 = Field.class.getDeclaredField("modifiers");
        var3.setAccessible(true);
        var3.setInt(var0, var0.getModifiers() & -17);
        Object var4 = newFieldAccessor.invoke(reflectionFactory, new Object[] {var0, Boolean.valueOf(false)});
        fieldAccessorSet.invoke(var4, new Object[] {var1, var2});
    }

    private static void blankField(Class var0, String var1) throws Exception
    {
        Field[] var2 = Class.class.getDeclaredFields();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            Field var5 = var2[var4];

            if (var5.getName().contains(var1))
            {
                var5.setAccessible(true);
                setFailsafeFieldValue(var5, var0, (Object)null);
                break;
            }
        }
    }

    private static void cleanEnumCache(Class var0) throws Exception
    {
        blankField(var0, "enumConstantDirectory");
        blankField(var0, "enumConstants");
    }

    public static Enum addEnum(Class var0, String var1, Object ... var2)
    {
        return addEnum(commonTypes, var0, var1, var2);
    }

    public static Enum addEnum(Class[][] var0, Class var1, String var2, Object ... var3)
    {
        Class[][] var4 = var0;
        int var5 = var0.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            Class[] var7 = var4[var6];

            if (var7[0] == var1)
            {
                Class[] var8 = new Class[var7.length - 1];

                if (var8.length > 0)
                {
                    System.arraycopy(var7, 1, var8, 0, var8.length);
                }

                return addEnum(var1, var2, var8, var3);
            }
        }

        return null;
    }

    public static Enum addEnum(Class var0, String var1, Class[] var2, Object[] var3)
    {
        if (!isSetup)
        {
            setup();
        }

        Field var4 = null;
        Field[] var5 = var0.getDeclaredFields();
        short var6 = 4122;
        String var7 = String.format("[L%s;", new Object[] {var0.getName().replace('.', '/')});
        Field[] var8 = var5;
        int var9 = var5.length;

        for (int var10 = 0; var10 < var9; ++var10)
        {
            Field var11 = var8[var10];

            if ((var11.getModifiers() & var6) == var6 && var11.getType().getName().replace('.', '/').equals(var7))
            {
                var4 = var11;
                break;
            }
        }

        var4.setAccessible(true);

        try
        {
            Enum[] var13 = (Enum[])((Enum[])var4.get(var0));
            ArrayList var14 = new ArrayList(Arrays.asList(var13));
            Enum var15 = makeEnum(var0, var1, var14.size(), var2, var3);
            var14.add(var15);
            setFailsafeFieldValue(var4, (Object)null, var14.toArray((Enum[])((Enum[])Array.newInstance(var0, 0))));
            cleanEnumCache(var0);
            return var15;
        }
        catch (Exception var12)
        {
            var12.printStackTrace();
            throw new RuntimeException(var12.getMessage(), var12);
        }
    }

    static
    {
        if (!isSetup)
        {
            setup();
        }
    }
}
