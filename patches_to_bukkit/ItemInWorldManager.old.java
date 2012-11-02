package net.minecraft.server;

import forge.ForgeHooks;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

public class ItemInWorldManager
{
  private double blockReachDistance = 5.0D;
  public World world;
  public EntityHuman player;
  private int c = -1;
  private float d = 0.0F;
  private int lastDigTick;
  private int f;
  private int g;
  private int h;
  private int currentTick;
  private boolean j;
  private int k;
  private int l;
  private int m;
  private int n;

  public ItemInWorldManager(World world)
  {
    this.world = world;
  }

  public ItemInWorldManager(WorldServer world)
  {
    this.world = world;
  }

  public void setGameMode(int i)
  {
    this.c = i;
    if (i == 0) {
      this.player.abilities.canFly = false;
      this.player.abilities.isFlying = false;
      this.player.abilities.canInstantlyBuild = false;
      this.player.abilities.isInvulnerable = false;
    } else {
      this.player.abilities.canFly = true;
      this.player.abilities.canInstantlyBuild = true;
      this.player.abilities.isInvulnerable = true;
    }

    this.player.updateAbilities();
  }

  public int getGameMode() {
    return this.c;
  }

  public boolean isCreative() {
    return this.c == 1;
  }

  public void b(int i) {
    if (this.c == -1) {
      this.c = i;
    }

    setGameMode(this.c);
  }

  public void c() {
    this.currentTick = (int)(System.currentTimeMillis() / 50L);
    if (this.j) {
      int i = this.currentTick - this.n;
      int j = this.world.getTypeId(this.k, this.l, this.m);

      if (j != 0) {
        Block block = Block.byId[j];
        float f = block.blockStrength(this.world, this.player, this.l, this.m, this.n) * i + 1;

        if (f >= 1.0F) {
          this.j = false;
          breakBlock(this.k, this.l, this.m);
        }
      } else {
        this.j = false;
      }
    }
  }

  public void dig(int i, int j, int k, int l)
  {
    PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, i, j, k, l, this.player.inventory.getItemInHand());

    if (isCreative())
    {
      if (event.isCancelled())
      {
        ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
        return;
      }

      if (!this.world.douseFire((EntityHuman)null, i, j, k, l))
        breakBlock(i, j, k);
    }
    else {
      this.lastDigTick = (int)(System.currentTimeMillis() / 50L);
      int i1 = this.world.getTypeId(i, j, k);

      if (i1 <= 0) {
        return;
      }

      if (event.useInteractedBlock() == Event.Result.DENY)
      {
        if (i1 == Block.WOODEN_DOOR.id)
        {
          boolean bottom = (this.world.getData(i, j, k) & 0x8) == 0;
          ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
          ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j + (bottom ? 1 : -1), k, this.world));
        } else if (i1 == Block.TRAP_DOOR.id) {
          ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
        }
      } else {
        Block.byId[i1].attack(this.world, i, j, k, this.player);

        this.world.douseFire((EntityHuman)null, i, j, k, l);
      }

      float toolDamage = Block.byId[i1].blockStrength(this.world, this.player, i, j, k);
      if (event.useItemInHand() == Event.Result.DENY)
      {
        if (toolDamage > 1.0F) {
          ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
        }
        return;
      }
      BlockDamageEvent blockEvent = CraftEventFactory.callBlockDamageEvent(this.player, i, j, k, this.player.inventory.getItemInHand(), toolDamage >= 1.0F);

      if (blockEvent.isCancelled())
      {
        ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
        return;
      }

      if (blockEvent.getInstaBreak()) {
        toolDamage = 2.0F;
      }

      if (toolDamage >= 1.0F)
      {
        breakBlock(i, j, k);
      } else {
        this.f = i;
        this.g = j;
        this.h = k;
      }
    }
  }

  public void a(int i, int j, int k) {
    if ((i == this.f) && (j == this.g) && (k == this.h)) {
      this.currentTick = (int)(System.currentTimeMillis() / 50L);
      int l = this.currentTick - this.lastDigTick;
      int i1 = this.world.getTypeId(i, j, k);

      if (i1 != 0) {
        Block block = Block.byId[i1];
        float f = block.blockStrength(this.world, this.player, i, j, k) * l + 1;

        if (f >= 0.7F) {
          breakBlock(i, j, k);
        } else if (!this.j) {
          this.j = true;
          this.k = i;
          this.l = j;
          this.m = k;
          this.n = this.lastDigTick;
        }
      }
    }
    else {
      ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
    }

    this.d = 0.0F;
  }

  public boolean b(int i, int j, int k) {
    Block block = Block.byId[this.world.getTypeId(i, j, k)];
    int l = this.world.getData(i, j, k);
    boolean flag = (block != null) && (block.removeBlockByPlayer(this.world, this.player, i, j, k));

    if ((block != null) && (flag)) {
      block.postBreak(this.world, i, j, k, l);
    }

    return flag;
  }

  public boolean breakBlock(int i, int j, int k) {
    ItemStack stack = this.player.U();
    if ((stack != null) && (stack.getItem().onBlockStartBreak(stack, i, j, k, this.player))) {
      return false;
    }

    if ((this.player instanceof EntityPlayer)) {
      org.bukkit.block.Block block = this.world.getWorld().getBlockAt(i, j, k);

      if (this.world.getTileEntity(i, j, k) == null) {
        Packet53BlockChange packet = new Packet53BlockChange(i, j, k, this.world);

        packet.material = 0;
        packet.data = 0;
        ((EntityPlayer)this.player).netServerHandler.sendPacket(packet);
      }

      BlockBreakEvent event = new BlockBreakEvent(block, (Player)this.player.getBukkitEntity());
      this.world.getServer().getPluginManager().callEvent(event);

      if (event.isCancelled())
      {
        ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
        return false;
      }

    }

    int l = this.world.getTypeId(i, j, k);
    int i1 = this.world.getData(i, j, k);

    this.world.a(this.player, 2001, i, j, k, l + (this.world.getData(i, j, k) << 12));
    boolean flag = b(i, j, k);

    if (isCreative()) {
      ((EntityPlayer)this.player).netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, this.world));
    } else {
      ItemStack itemstack = this.player.U();
      boolean flag1 = Block.byId[l].canHarvestBlock(this.player, i1);

      if (itemstack != null) {
        itemstack.a(l, i, j, k, this.player);
        if (itemstack.count == 0) {
          itemstack.a(this.player);
          this.player.V();
          ForgeHooks.onDestroyCurrentItem(this.player, itemstack);
        }
      }

      if ((flag) && (flag1)) {
        Block.byId[l].a(this.world, this.player, i, j, k, i1);
      }
    }

    return flag;
  }

  public boolean useItem(EntityHuman entityhuman, World world, ItemStack itemstack) {
    int i = itemstack.count;
    int j = itemstack.getData();
    ItemStack itemstack1 = itemstack.a(world, entityhuman);

    if ((itemstack1 == itemstack) && ((itemstack1 == null) || (itemstack1.count == i)) && ((itemstack1 == null) || (itemstack1.l() <= 0))) {
      return false;
    }
    entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = itemstack1;
    if (isCreative()) {
      itemstack1.count = i;
      itemstack1.setData(j);
    }

    if (itemstack1.count == 0) {
      entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
    }

    return true;
  }

  public boolean interact(EntityHuman entityhuman, World world, ItemStack itemstack, int i, int j, int k, int l)
  {
    PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(entityhuman, Action.RIGHT_CLICK_BLOCK, i, j, k, l, itemstack);

    if ((itemstack != null) && (event.useItemInHand() != Event.Result.DENY) && (event.useInteractedBlock() != Event.Result.DENY) && (itemstack.getItem().onItemUseFirst(itemstack, entityhuman, world, i, j, k, l)))
    {
      return true;
    }

    int i1 = world.getTypeId(i, j, k);

    boolean result = false;
    if (i1 > 0) {
      if (event.useInteractedBlock() == Event.Result.DENY)
      {
        if (i1 == Block.WOODEN_DOOR.id) {
          boolean bottom = (world.getData(i, j, k) & 0x8) == 0;
          ((EntityPlayer)entityhuman).netServerHandler.sendPacket(new Packet53BlockChange(i, j + (bottom ? 1 : -1), k, world));
        }
        result = event.useItemInHand() != Event.Result.ALLOW;
      } else {
        result = Block.byId[i1].interact(world, i, j, k, entityhuman);
      }

      if ((itemstack != null) && (!result)) {
        int j1 = itemstack.getData();
        int k1 = itemstack.count;

        result = itemstack.placeItem(entityhuman, world, i, j, k, l);

        if (isCreative()) {
          itemstack.setData(j1);
          itemstack.count = k1;
        } else if ((result) && (itemstack.count == 0)) {
          ForgeHooks.onDestroyCurrentItem(entityhuman, itemstack);
        }

      }

      if ((itemstack != null) && (((!result) && (event.useItemInHand() != Event.Result.DENY)) || (event.useItemInHand() == Event.Result.ALLOW))) {
        useItem(entityhuman, world, itemstack);
      }
    }
    return result;
  }

  public void a(WorldServer worldserver)
  {
    this.world = worldserver;
  }

  public double getBlockReachDistance()
  {
    return this.blockReachDistance;
  }

  public void setBlockReachDistance(double distance) {
    this.blockReachDistance = distance;
  }
}

