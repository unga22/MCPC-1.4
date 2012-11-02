package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.ArrayList;
import java.util.Random;

public class BlockNetherWart extends BlockFlower {

    protected BlockNetherWart(int i) {
        super(i, 226);
        this.b(true);
        float f = 0.5F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.a((CreativeModeTab) null);
    }

    protected boolean d_(int i) {
        return i == Block.SOUL_SAND.id;
    }

    public boolean d(World world, int i, int j, int k) {
        return this.d_(world.getTypeId(i, j - 1, k));
    }

    public void b(World world, int i, int j, int k, Random random) {
        int l = world.getData(i, j, k);

        if (l < 3 && random.nextInt(10) == 0) {
            org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, i, j, k, this.id, ++l); // CraftBukkit
        }

        super.b(world, i, j, k, random);
    }

    public int a(int i, int j) {
        return j >= 3 ? this.textureId + 2 : (j > 0 ? this.textureId + 1 : this.textureId);
    }

    public int d() {
        return 6;
    }
    
    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropNaturally(World var1, int var2, int var3, int var4, int var5, float var6, int var7)
    {
        super.dropNaturally(var1, var2, var3, var4, var5, var6, var7);
    }

    public int getDropType(int i, Random random, int j) {
        return 0;
    }

    public int a(Random random) {
        return 0;
    }
    

    public ArrayList getBlockDropped(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        ArrayList var7 = new ArrayList();
        int var8 = 1;

        if (var5 >= 3)
        {
            var8 = 2 + var1.random.nextInt(3) + (var6 > 0 ? var1.random.nextInt(var6 + 1) : 0);
        }

        for (int var9 = 0; var9 < var8; ++var9)
        {
            var7.add(new ItemStack(Item.NETHER_STALK));
        }

        return var7;
    }
}
