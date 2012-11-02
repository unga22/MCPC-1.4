package net.minecraft.server;

import cpw.mods.fml.server.FMLBukkitHandler;
import forge.ForgeHooks;

public class SlotResult extends Slot
{
  private final IInventory a;
  private EntityHuman f;
  private int g;

  public SlotResult(EntityHuman entityhuman, IInventory iinventory, IInventory iinventory1, int i, int j, int k)
  {
    super(iinventory1, i, j, k);
    this.f = entityhuman;
    this.a = iinventory;
  }

  public boolean isAllowed(ItemStack itemstack) {
    return false;
  }

  public ItemStack a(int i) {
    if (c()) {
      this.g += Math.min(i, getItem().count);
    }

    return super.a(i);
  }

  protected void a(ItemStack itemstack, int i) {
    this.g += i;
    b(itemstack);
  }

  protected void b(ItemStack itemstack) {
    itemstack.a(this.f.world, this.f, this.g);
    this.g = 0;
    if (itemstack.id == Block.WORKBENCH.id)
      this.f.a(AchievementList.h, 1);
    else if (itemstack.id == Item.WOOD_PICKAXE.id)
      this.f.a(AchievementList.i, 1);
    else if (itemstack.id == Block.FURNACE.id)
      this.f.a(AchievementList.j, 1);
    else if (itemstack.id == Item.WOOD_HOE.id)
      this.f.a(AchievementList.l, 1);
    else if (itemstack.id == Item.BREAD.id)
      this.f.a(AchievementList.m, 1);
    else if (itemstack.id == Item.CAKE.id)
      this.f.a(AchievementList.n, 1);
    else if (itemstack.id == Item.STONE_PICKAXE.id)
      this.f.a(AchievementList.o, 1);
    else if (itemstack.id == Item.WOOD_SWORD.id)
      this.f.a(AchievementList.r, 1);
    else if (itemstack.id == Block.ENCHANTMENT_TABLE.id)
      this.f.a(AchievementList.D, 1);
    else if (itemstack.id == Block.BOOKSHELF.id)
      this.f.a(AchievementList.F, 1);
  }

  public void c(ItemStack itemstack)
  {
    FMLBukkitHandler.instance().onItemCrafted(this.f, itemstack, this.a);
    ForgeHooks.onTakenFromCrafting(this.f, itemstack, this.a);

    b(itemstack);

    for (int i = 0; i < this.a.getSize(); i++) {
      ItemStack itemstack1 = this.a.getItem(i);

      if (itemstack1 != null) {
        this.a.splitStack(i, 1);
        if (itemstack1.getItem().k()) {
          ItemStack itemstack2 = new ItemStack(itemstack1.getItem().j());

          if ((!itemstack1.getItem().e(itemstack1)) || (!this.f.inventory.pickup(itemstack2)))
            if (this.a.getItem(i) == null)
              this.a.setItem(i, itemstack2);
            else
              this.f.drop(itemstack2);
        }
      }
    }
  }
}

