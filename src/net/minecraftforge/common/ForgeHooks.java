package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.server.Block;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.Item;
import net.minecraft.server.ItemArmor;
import net.minecraft.server.ItemAxe;
import net.minecraft.server.ItemPickaxe;
import net.minecraft.server.ItemSpade;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.WeightedRandom;
import net.minecraft.server.World;
import net.minecraftforge.common.ForgeHooks$GrassEntry;
import net.minecraftforge.common.ForgeHooks$SeedEntry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent$LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

public class ForgeHooks
{
    static final List grassList = new ArrayList();
    static final List seedList = new ArrayList();
    private static boolean toolInit = false;
    static HashMap toolClasses = new HashMap();
    static HashMap toolHarvestLevels = new HashMap();
    static HashSet toolEffectiveness = new HashSet();

    public static void plantGrass(World var0, int var1, int var2, int var3)
    {
        ForgeHooks$GrassEntry var4 = (ForgeHooks$GrassEntry)WeightedRandom.a(var0.random, grassList);

        if (var4 != null && var4.block != null && var4.block.d(var0, var1, var2, var3))
        {
            var0.setTypeIdAndData(var1, var2, var3, var4.block.id, var4.metadata);
        }
    }

    public static ItemStack getGrassSeed(World var0)
    {
        ForgeHooks$SeedEntry var1 = (ForgeHooks$SeedEntry)WeightedRandom.a(var0.random, seedList);
        return var1 != null && var1.seed != null ? var1.seed.cloneItemStack() : null;
    }

    public static boolean canHarvestBlock(Block var0, EntityHuman var1, int var2)
    {
        if (var0.material.isAlwaysDestroyable())
        {
            return true;
        }
        else
        {
            ItemStack var3 = var1.inventory.getItemInHand();

            if (var3 == null)
            {
                return var1.b(var0);
            }
            else
            {
                List var4 = (List)toolClasses.get(var3.getItem());

                if (var4 == null)
                {
                    return var1.b(var0);
                }
                else
                {
                    Object[] var5 = var4.toArray();
                    String var6 = (String)var5[0];
                    int var7 = ((Integer)var5[1]).intValue();
                    Integer var8 = (Integer)toolHarvestLevels.get(Arrays.asList(new Object[] {var0, Integer.valueOf(var2), var6}));
                    return var8 == null ? var1.b(var0) : var8.intValue() <= var7;
                }
            }
        }
    }

    public static float blockStrength(Block var0, EntityHuman var1, World var2, int var3, int var4, int var5)
    {
        int var6 = var2.getData(var3, var4, var5);
        float var7 = var0.m(var2, var3, var4, var5);

        if (var7 < 0.0F)
        {
            return 0.0F;
        }
        else if (!canHarvestBlock(var0, var1, var6))
        {
            float var8 = ForgeEventFactory.getBreakSpeed(var1, var0, var6, 1.0F);
            return (var8 < 0.0F ? 0.0F : var8) / var7 / 100.0F;
        }
        else
        {
            return var1.getCurrentPlayerStrVsBlock(var0, var6) / var7 / 30.0F;
        }
    }

    public static boolean isToolEffective(ItemStack var0, Block var1, int var2)
    {
        List var3 = (List)toolClasses.get(var0.getItem());
        return var3 == null ? false : toolEffectiveness.contains(Arrays.asList(new Object[] {var1, Integer.valueOf(var2), (String)var3.get(0)}));
    }

    static void initTools()
    {
        if (!toolInit)
        {
            toolInit = true;
            MinecraftForge.setToolClass(Item.WOOD_PICKAXE, "pickaxe", 0);
            MinecraftForge.setToolClass(Item.STONE_PICKAXE, "pickaxe", 1);
            MinecraftForge.setToolClass(Item.IRON_PICKAXE, "pickaxe", 2);
            MinecraftForge.setToolClass(Item.GOLD_PICKAXE, "pickaxe", 0);
            MinecraftForge.setToolClass(Item.DIAMOND_PICKAXE, "pickaxe", 3);
            MinecraftForge.setToolClass(Item.WOOD_AXE, "axe", 0);
            MinecraftForge.setToolClass(Item.STONE_AXE, "axe", 1);
            MinecraftForge.setToolClass(Item.IRON_AXE, "axe", 2);
            MinecraftForge.setToolClass(Item.GOLD_AXE, "axe", 0);
            MinecraftForge.setToolClass(Item.DIAMOND_AXE, "axe", 3);
            MinecraftForge.setToolClass(Item.WOOD_SPADE, "shovel", 0);
            MinecraftForge.setToolClass(Item.STONE_SPADE, "shovel", 1);
            MinecraftForge.setToolClass(Item.IRON_SPADE, "shovel", 2);
            MinecraftForge.setToolClass(Item.GOLD_SPADE, "shovel", 0);
            MinecraftForge.setToolClass(Item.DIAMOND_SPADE, "shovel", 3);
            Block[] var0 = ItemPickaxe.c;
            int var1 = var0.length;
            int var2;
            Block var3;

            for (var2 = 0; var2 < var1; ++var2)
            {
                var3 = var0[var2];
                MinecraftForge.setBlockHarvestLevel(var3, "pickaxe", 0);
            }

            var0 = ItemSpade.c;
            var1 = var0.length;

            for (var2 = 0; var2 < var1; ++var2)
            {
                var3 = var0[var2];
                MinecraftForge.setBlockHarvestLevel(var3, "shovel", 0);
            }

            var0 = ItemAxe.c;
            var1 = var0.length;

            for (var2 = 0; var2 < var1; ++var2)
            {
                var3 = var0[var2];
                MinecraftForge.setBlockHarvestLevel(var3, "axe", 0);
            }

            MinecraftForge.setBlockHarvestLevel(Block.OBSIDIAN, "pickaxe", 3);
            MinecraftForge.setBlockHarvestLevel(Block.EMERALD_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.DIAMOND_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.DIAMOND_BLOCK, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.GOLD_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.GOLD_BLOCK, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.IRON_ORE, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.IRON_BLOCK, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.LAPIS_ORE, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.LAPIS_BLOCK, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.REDSTONE_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.GLOWING_REDSTONE_ORE, "pickaxe", 2);
            MinecraftForge.removeBlockEffectiveness(Block.REDSTONE_ORE, "pickaxe");
            MinecraftForge.removeBlockEffectiveness(Block.OBSIDIAN, "pickaxe");
            MinecraftForge.removeBlockEffectiveness(Block.GLOWING_REDSTONE_ORE, "pickaxe");
        }
    }

    public static String getTexture(String var0, Object var1)
    {
        return var1 instanceof Item ? ((Item)var1).getTextureFile() : (var1 instanceof Block ? ((Block)var1).getTextureFile() : var0);
    }

    public static int getTotalArmorValue(EntityHuman var0)
    {
        int var1 = 0;

        for (int var2 = 0; var2 < var0.inventory.armor.length; ++var2)
        {
            ItemStack var3 = var0.inventory.armor[var2];

            if (var3 != null && var3.getItem() instanceof ISpecialArmor)
            {
                var1 += ((ISpecialArmor)var3.getItem()).getArmorDisplay(var0, var3, var2);
            }
            else if (var3 != null && var3.getItem() instanceof ItemArmor)
            {
                var1 += ((ItemArmor)var3.getItem()).b;
            }
        }

        return var1;
    }

    public static boolean onPickBlock(MovingObjectPosition var0, EntityHuman var1, World var2)
    {
        ItemStack var3 = null;
        boolean var4 = var1.abilities.canInstantlyBuild;
        int var5;

        if (var0.type == EnumMovingObjectType.TILE)
        {
            var5 = var0.b;
            int var6 = var0.c;
            int var7 = var0.d;
            Block var8 = Block.byId[var2.getTypeId(var5, var6, var7)];

            if (var8 == null)
            {
                return false;
            }

            var3 = var8.getPickBlock(var0, var2, var5, var6, var7);
        }
        else
        {
            if (var0.type != EnumMovingObjectType.ENTITY || var0.entity == null || !var4)
            {
                return false;
            }

            var3 = var0.entity.getPickedResult(var0);
        }

        if (var3 == null)
        {
            return false;
        }
        else
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                ItemStack var9 = var1.inventory.getItem(var5);

                if (var9 != null && var9.doMaterialsMatch(var3))
                {
                    var1.inventory.itemInHandIndex = var5;
                    return true;
                }
            }

            if (!var4)
            {
                return false;
            }
            else
            {
                var5 = var1.inventory.i();

                if (var5 < 0 || var5 >= 9)
                {
                    var5 = var1.inventory.itemInHandIndex;
                }

                var1.inventory.setItem(var5, var3);
                var1.inventory.itemInHandIndex = var5;
                return true;
            }
        }
    }

    public static void onLivingSetAttackTarget(EntityLiving var0, EntityLiving var1)
    {
        MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(var0, var1));
    }

    public static boolean onLivingUpdate(EntityLiving var0)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingEvent$LivingUpdateEvent(var0));
    }

    public static boolean onLivingAttack(EntityLiving var0, DamageSource var1, int var2)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(var0, var1, var2));
    }

    public static int onLivingHurt(EntityLiving var0, DamageSource var1, int var2)
    {
        LivingHurtEvent var3 = new LivingHurtEvent(var0, var1, var2);
        return MinecraftForge.EVENT_BUS.post(var3) ? 0 : var3.ammount;
    }

    public static boolean onLivingDeath(EntityLiving var0, DamageSource var1)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(var0, var1));
    }

    public static boolean onLivingDrops(EntityLiving var0, DamageSource var1, ArrayList var2, int var3, boolean var4, int var5)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(var0, var1, var2, var3, var4, var5));
    }

    public static float onLivingFall(EntityLiving var0, float var1)
    {
        LivingFallEvent var2 = new LivingFallEvent(var0, var1);
        return MinecraftForge.EVENT_BUS.post(var2) ? 0.0F : var2.distance;
    }

    public static boolean isLivingOnLadder(Block var0, World var1, int var2, int var3, int var4)
    {
        return var0 != null && var0.isLadder(var1, var2, var3, var4);
    }

    public static void onLivingJump(EntityLiving var0)
    {
        MinecraftForge.EVENT_BUS.post(new LivingEvent$LivingJumpEvent(var0));
    }

    public static EntityItem onPlayerTossEvent(EntityHuman var0, ItemStack var1)
    {
        var0.captureDrops = true;
        EntityItem var2 = var0.a(var1, false);
        var0.capturedDrops.clear();
        var0.captureDrops = false;
        ItemTossEvent var3 = new ItemTossEvent(var2, var0);

        if (MinecraftForge.EVENT_BUS.post(var3))
        {
            return null;
        }
        else
        {
            var0.a(var3.entityItem);
            return var3.entityItem;
        }
    }

    static
    {
        grassList.add(new ForgeHooks$GrassEntry(Block.YELLOW_FLOWER, 0, 20));
        grassList.add(new ForgeHooks$GrassEntry(Block.RED_ROSE, 0, 10));
        seedList.add(new ForgeHooks$SeedEntry(new ItemStack(Item.SEEDS), 10));
        initTools();
    }
}
