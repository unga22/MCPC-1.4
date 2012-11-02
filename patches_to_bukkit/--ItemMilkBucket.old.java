package net.minecraft.server;

public class ItemMilkBucket extends Item
{
  public ItemMilkBucket(int paramInt)
  {
    super(paramInt);

    e(1);
  }

  public ItemStack b(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman) {
    paramItemStack.count -= 1;

    if (!paramWorld.isStatic) {
      paramEntityHuman.aL();
    }

    if (paramItemStack.count <= 0) {
      return new ItemStack(Item.BUCKET);
    }
    return paramItemStack;
  }

  public int c(ItemStack paramItemStack) {
    return 32;
  }

  public EnumAnimation d(ItemStack paramItemStack) {
    return EnumAnimation.c;
  }

  public ItemStack a(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman) {
    paramEntityHuman.a(paramItemStack, c(paramItemStack));
    return paramItemStack;
  }
}

