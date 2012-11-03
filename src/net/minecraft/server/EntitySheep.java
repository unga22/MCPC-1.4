package net.minecraft.server;

import java.util.Random;
import net.minecraftforge.common.IShearable;
import java.util.ArrayList;
// CraftBukkit start
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
// CraftBukkit end

public class EntitySheep extends EntityAnimal implements IShearable {

    public static final float[][] d = new float[][] { { 1.0F, 1.0F, 1.0F}, { 0.95F, 0.7F, 0.2F}, { 0.9F, 0.5F, 0.85F}, { 0.6F, 0.7F, 0.95F}, { 0.9F, 0.9F, 0.2F}, { 0.5F, 0.8F, 0.1F}, { 0.95F, 0.7F, 0.8F}, { 0.3F, 0.3F, 0.3F}, { 0.6F, 0.6F, 0.6F}, { 0.3F, 0.6F, 0.7F}, { 0.7F, 0.4F, 0.9F}, { 0.2F, 0.4F, 0.8F}, { 0.5F, 0.4F, 0.3F}, { 0.4F, 0.5F, 0.2F}, { 0.8F, 0.3F, 0.3F}, { 0.1F, 0.1F, 0.1F}};
    private int e;
    private PathfinderGoalEatTile f = new PathfinderGoalEatTile(this);
    EntityHuman shearer = null;
    
    public EntitySheep(World world) {
        super(world);
        this.texture = "/mob/sheep.png";
        this.a(0.9F, 1.3F);
        float f = 0.23F;

        this.getNavigation().a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 0.38F));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, f));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 0.25F, Item.WHEAT.id, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 0.25F));
        this.goalSelector.a(5, this.f);
        this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, f));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    protected boolean bb() {
        return true;
    }

    protected void bi() {
        this.e = this.f.f();
        super.bi();
    }

    public void c() {
        if (this.world.isStatic) {
            this.e = Math.max(0, this.e - 1);
        }

        super.c();
    }

    public int getMaxHealth() {
        return 8;
    }

    protected void a() {
        super.a();
        this.datawatcher.a(16, new Byte((byte) 0));
    }

    protected void dropDeathLoot(boolean flag, int i) {
        // CraftBukkit start - whole method
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();

        if (!this.isSheared()) {
            loot.add(new org.bukkit.inventory.ItemStack(org.bukkit.Material.WOOL, 1, (short) 0, (byte) this.getColor()));
        }

        org.bukkit.craftbukkit.event.CraftEventFactory.callEntityDeathEvent(this, loot);
        // CraftBukkit end
    }

    protected int getLootId() {
        return Block.WOOL.id;
    }

    public boolean c(EntityHuman entityhuman) {
    	shearer = entityhuman; // for bukkit down there
        return super.c(entityhuman);
    }
    

    public boolean isShearable(ItemStack var1, World var2, int var3, int var4, int var5)
    {
        return !this.isSheared() && !this.isBaby();
    }

    public ArrayList onSheared(ItemStack var1, World var2, int var3, int var4, int var5, int var6)
    {
    	ArrayList var7 = new ArrayList();
    	
        // CraftBukkit start
        PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) shearer.getBukkitEntity(), this.getBukkitEntity());
        this.world.getServer().getPluginManager().callEvent(event);
        shearer = null;

        if (event.isCancelled()) {
            return var7;
        }
        // CraftBukkit end
        
        
        this.setSheared(true);
        int var8 = 1 + this.random.nextInt(3);

        for (int var9 = 0; var9 < var8; ++var9)
        {
            var7.add(new ItemStack(Block.WOOL.id, 1, this.getColor()));
        }

        this.world.makeSound(this, "mob.sheep.shear", 1.0F, 1.0F);
        return var7;
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Sheared", this.isSheared());
        nbttagcompound.setByte("Color", (byte) this.getColor());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSheared(nbttagcompound.getBoolean("Sheared"));
        this.setColor(nbttagcompound.getByte("Color"));
    }

    protected String aW() {
        return "mob.sheep.say";
    }

    protected String aX() {
        return "mob.sheep.say";
    }

    protected String aY() {
        return "mob.sheep.say";
    }

    protected void a(int i, int j, int k, int l) {
        this.world.makeSound(this, "mob.sheep.step", 0.15F, 1.0F);
    }

    public int getColor() {
        return this.datawatcher.getByte(16) & 15;
    }

    public void setColor(int i) {
        byte b0 = this.datawatcher.getByte(16);

        this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & 240 | i & 15)));
    }

    public boolean isSheared() {
        return (this.datawatcher.getByte(16) & 16) != 0;
    }

    public void setSheared(boolean flag) {
        byte b0 = this.datawatcher.getByte(16);

        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 | 16)));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & -17)));
        }
    }

    public static int a(Random random) {
        int i = random.nextInt(100);

        return i < 5 ? 15 : (i < 10 ? 7 : (i < 15 ? 8 : (i < 18 ? 12 : (random.nextInt(500) == 0 ? 6 : 0))));
    }

    public EntityAnimal createChild(EntityAnimal entityanimal) {
        EntitySheep entitysheep = (EntitySheep) entityanimal;
        EntitySheep entitysheep1 = new EntitySheep(this.world);

        if (this.random.nextBoolean()) {
            entitysheep1.setColor(this.getColor());
        } else {
            entitysheep1.setColor(entitysheep.getColor());
        }

        return entitysheep1;
    }

    public void aG() {
        // CraftBukkit start
        SheepRegrowWoolEvent event = new SheepRegrowWoolEvent((org.bukkit.entity.Sheep) this.getBukkitEntity());
        this.world.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.setSheared(false);
        }
        // CraftBukkit end

        if (this.isBaby()) {
            int i = this.getAge() + 1200;

            if (i > 0) {
                i = 0;
            }

            this.setAge(i);
        }
    }

    public void bD() {
        this.setColor(a(this.world.random));
    }
}
