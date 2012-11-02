package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public abstract class Enchantment
{
    public static final Enchantment[] byId = new Enchantment[256];

    /** Converts environmental damage to armour damage */
    public static final Enchantment PROTECTION_ENVIRONMENTAL = new EnchantmentProtection(0, 10, 0);

    /** Protection against fire */
    public static final Enchantment PROTECTION_FIRE = new EnchantmentProtection(1, 5, 1);

    /** Less fall damage */
    public static final Enchantment PROTECTION_FALL = new EnchantmentProtection(2, 5, 2);

    /** Protection against explosions */
    public static final Enchantment PROTECTION_EXPLOSIONS = new EnchantmentProtection(3, 2, 3);

    /** Protection against projectile entities (e.g. arrows) */
    public static final Enchantment PROTECTION_PROJECTILE = new EnchantmentProtection(4, 5, 4);

    /**
     * Decreases the rate of air loss underwater; increases time between damage while suffocating
     */
    public static final Enchantment OXYGEN = new EnchantmentOxygen(5, 2);

    /** Increases underwater mining rate */
    public static final Enchantment WATER_WORKER = new EnchantmentWaterWorker(6, 2);

    /** Extra damage to mobs */
    public static final Enchantment DAMAGE_ALL = new EnchantmentWeaponDamage(16, 10, 0);

    /** Extra damage to zombies, zombie pigmen and skeletons */
    public static final Enchantment DAMAGE_UNDEAD = new EnchantmentWeaponDamage(17, 5, 1);

    /** Extra damage to spiders, cave spiders and silverfish */
    public static final Enchantment DAMAGE_ARTHROPODS = new EnchantmentWeaponDamage(18, 5, 2);

    /** Knocks mob and players backwards upon hit */
    public static final Enchantment KNOCKBACK = new EnchantmentKnockback(19, 5);

    /** Lights mobs on fire */
    public static final Enchantment FIRE_ASPECT = new EnchantmentFire(20, 2);

    /** Mobs have a chance to drop more loot */
    public static final Enchantment LOOT_BONUS_MOBS = new EnchantmentLootBonus(21, 2, EnchantmentSlotType.weapon);

    /** Faster resource gathering while in use */
    public static final Enchantment DIG_SPEED = new EnchantmentDigging(32, 10);

    /**
     * Blocks mined will drop themselves, even if it should drop something else (e.g. stone will drop stone, not
     * cobblestone)
     */
    public static final Enchantment SILK_TOUCH = new EnchantmentSilkTouch(33, 1);

    /**
     * Sometimes, the tool's durability will not be spent when the tool is used
     */
    public static final Enchantment DURABILITY = new EnchantmentDurability(34, 5);

    /** Can multiply the drop rate of items from blocks */
    public static final Enchantment LOOT_BONUS_BLOCKS = new EnchantmentLootBonus(35, 2, EnchantmentSlotType.digger);

    /** Power enchantment for bows, add's extra damage to arrows. */
    public static final Enchantment ARROW_DAMAGE = new EnchantmentArrowDamage(48, 10);

    /**
     * Knockback enchantments for bows, the arrows will knockback the target when hit.
     */
    public static final Enchantment ARROW_KNOCKBACK = new EnchantmentArrowKnockback(49, 2);

    /**
     * Flame enchantment for bows. Arrows fired by the bow will be on fire. Any target hit will also set on fire.
     */
    public static final Enchantment ARROW_FIRE = new EnchantmentFlameArrows(50, 2);

    /**
     * Infinity enchantment for bows. The bow will not consume arrows anymore, but will still required at least one
     * arrow on inventory use the bow.
     */
    public static final Enchantment ARROW_INFINITE = new EnchantmentInfiniteArrows(51, 1);
    public final int id;
    private final int weight;

    /** The EnumEnchantmentType given to this Enchantment. */
    public EnchantmentSlotType slot;

    /** Used in localisation and stats. */
    protected String name;

    protected Enchantment(int var1, int var2, EnchantmentSlotType var3)
    {
        this.id = var1;
        this.weight = var2;
        this.slot = var3;

        if (byId[var1] != null)
        {
            throw new IllegalArgumentException("Duplicate enchantment id!");
        }
        else
        {
            byId[var1] = this;
        }
    }

    public int getRandomWeight()
    {
        return this.weight;
    }

    /**
     * Returns the minimum level that the enchantment can have.
     */
    public int getStartLevel()
    {
        return 1;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 1;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int a(int var1)
    {
        return 1 + var1 * 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int b(int var1)
    {
        return this.a(var1) + 5;
    }

    /**
     * Calculates de damage protection of the enchantment based on level and damage source passed.
     */
    public int a(int var1, DamageSource var2)
    {
        return 0;
    }

    /**
     * Calculates de (magic) damage done by the enchantment on a living entity based on level and entity passed.
     */
    public int a(int var1, EntityLiving var2)
    {
        return 0;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean a(Enchantment var1)
    {
        return this != var1;
    }

    /**
     * Sets the enchantment name
     */
    public Enchantment b(String var1)
    {
        this.name = var1;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String a()
    {
        return "enchantment." + this.name;
    }

    @SideOnly(Side.CLIENT)
    public String c(int var1)
    {
        String var2 = LocaleI18n.get(this.a());
        return var2 + " " + LocaleI18n.get("enchantment.level." + var1);
    }

    public boolean canEnchantItem(ItemStack var1)
    {
        return this.slot.canEnchant(var1.getItem());
    }
}
