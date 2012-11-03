package net.minecraft.server;

import org.bukkit.craftbukkit.block.CraftBlockState; // CraftBukkit

public class ItemBlock extends Item {

    private int id;

    public ItemBlock(int i) {
        super(i);
        this.id = i + 256;
        this.c(Block.byId[i + 256].a(2));
        this.isDefaultTexture = Block.byId[this.id].isDefaultTexture;
    }

    public int g() {
        return this.id;
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        int clickedX = i, clickedY = j, clickedZ = k; // CraftBukkit
        int i1 = world.getTypeId(i, j, k);

        if (i1 == Block.SNOW.id) {
            l = 1;
        } else if (i1 != Block.VINE.id && i1 != Block.LONG_GRASS.id && i1 != Block.DEAD_BUSH.id && (Block.byId[i1] == null || !Block.byId[i1].isBlockReplaceable(world, i, j, k)))
        {
        	if (l == 0) {
                --j;
            }

            if (l == 1) {
                ++j;
            }

            if (l == 2) {
                --k;
            }

            if (l == 3) {
                ++k;
            }

            if (l == 4) {
                --i;
            }

            if (l == 5) {
                ++i;
            }
        }

        if (itemstack.count == 0) {
            return false;
        } else if (!entityhuman.a(i, j, k, l, itemstack)) {
            return false;
        } else if (j == 255 && Block.byId[this.id].material.isBuildable()) {
            return false;
            // CraftBukkit start
        }

        int id = this.id;
        if (l == -1 && itemstack.getItem() instanceof ItemStep) {
            if (this.id == Block.STEP.id) {
                id = Block.DOUBLE_STEP.id;
            } else if (this.id == Block.WOOD_STEP.id) {
                id = Block.WOOD_DOUBLE_STEP.id;
            }
        }

        if (id != this.id || world.mayPlace(this.id, i, j, k, false, l, entityhuman)) {
            Block block = Block.byId[id];

            CraftBlockState replacedBlockState = CraftBlockState.getBlockState(world, i, j, k);

            world.suppressPhysics = true;
            world.setTypeIdAndData(i, j, k, id, this.filterData(itemstack.getData()));
            org.bukkit.event.block.BlockPlaceEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockPlaceEvent(world, entityhuman, replacedBlockState, clickedX, clickedY, clickedZ);
            id = world.getTypeId(i, j, k);
            int data = world.getData(i, j, k);
            replacedBlockState.update(true);
            world.suppressPhysics = false;

            if (event.isCancelled() || !event.canBuild()) {
                return true;
            }
            
            if (this.placeBlockAt(itemstack, entityhuman, world,  i, j, k, l, f, f1, f2))
            {
                world.makeSound((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), block.stepSound.b(), (block.stepSound.getVolume1() + 1.0F) / 2.0F, block.stepSound.getVolume2() * 0.8F);
                --itemstack.count;
            }

            return true;
        } else {
            return false;
        }
    }

    public String c_(ItemStack itemstack) {
        return Block.byId[this.id].a();
    }

    public String getName() {
        return Block.byId[this.id].a();
    }

    public boolean placeBlockAt(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10)
    {
        if (!var3.setTypeIdAndData(var4, var5, var6, this.id, this.filterData(var1.getData())))
        {
            return false;
        }
        else
        {
            if (var3.getTypeId(var4, var5, var6) == this.id)
            {
                Block.byId[this.id].postPlace(var3, var4, var5, var6, var7, var8, var9, var10);
                Block.byId[this.id].postPlace(var3, var4, var5, var6, var2);
            }

            return true;
        }
    }
}
