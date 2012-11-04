package net.minecraft.server;

public class SlotArmor extends Slot
{
	public SlotArmor(ContainerPlayer paramContainerPlayer, IInventory paramIInventory, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
	{
		super(paramIInventory, paramInt1, paramInt2, paramInt3);
	}
	public int a() {
		return 1;
	}

	public boolean isAllowed(ItemStack paramItemStack)
	{
		if (paramItemStack == null) {
			return false;
		}
		if ((paramItemStack.getItem() instanceof ItemArmor)) {
			return ((ItemArmor)paramItemStack.getItem()).a == this.a();
		}
		if ((paramItemStack.getItem().id == Block.PUMPKIN.id) || (paramItemStack.getItem().id == Item.SKULL.id)) {
			return this.a() == 0;
		}
		return false;
	}
}