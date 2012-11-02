package net.minecraft.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EntityPlayer extends EntityHuman implements ICrafting
{
    private LocaleLanguage locale = new LocaleLanguage("en_US");

    /**
     * The NetServerHandler assigned to this player by the ServerConfigurationManager.
     */
    public NetServerHandler netServerHandler;

    /** Reference to the MinecraftServer object. */
    public MinecraftServer server;

    /** The ItemInWorldManager belonging to this player */
    public ItemInWorldManager itemInWorldManager;

    /** player X position as seen by PlayerManager */
    public double d;

    /** player Z position as seen by PlayerManager */
    public double e;

    /** LinkedList that holds the loaded chunks. */
    public final List chunkCoordIntPairQueue = new LinkedList();

    /** entities added to this list will  be packet29'd to the player */
    public final List removeQueue = new LinkedList();

    /** amount of health the client was last set to */
    private int cn = -99999999;

    /** set to foodStats.GetFoodLevel */
    private int co = -99999999;

    /** set to foodStats.getSaturationLevel() == 0.0F each tick */
    private boolean cp = true;

    /** Amount of experience the client was last set to */
    private int lastSentExp = -99999999;

    /** how many ticks of invulnerability(spawn protection) this player has */
    private int invulnerableTicks = 60;

    /** must be between 3>x>15 (strictly between) */
    private int cs = 0;
    private int ct = 0;
    private boolean cu = true;

    /**
     * The currently in use window ID. Incremented every time a window is opened.
     */
    private int containerCounter = 0;

    /**
     * set to true when player is moving quantity of items from one inventory to another(crafting) but item in either
     * slot is not changed
     */
    public boolean h;
    public int ping;

    /**
     * Set when a player beats the ender dragon, used to respawn the player at the spawn point while retaining inventory
     * and XP
     */
    public boolean viewingCredits = false;

    public EntityPlayer(MinecraftServer var1, World var2, String var3, ItemInWorldManager var4)
    {
        super(var2);
        var4.player = this;
        this.itemInWorldManager = var4;
        this.cs = var1.getServerConfigurationManager().o();
        ChunkCoordinates var5 = var2.getSpawn();
        int var6 = var5.x;
        int var7 = var5.z;
        int var8 = var5.y;

        if (!var2.worldProvider.f && var2.getWorldData().getGameType() != EnumGamemode.ADVENTURE)
        {
            int var9 = Math.max(5, var1.getSpawnProtection() - 6);
            var6 += this.random.nextInt(var9 * 2) - var9;
            var7 += this.random.nextInt(var9 * 2) - var9;
            var8 = var2.i(var6, var7);
        }

        this.setPositionRotation((double)var6 + 0.5D, (double)var8, (double)var7 + 0.5D, 0.0F, 0.0F);
        this.server = var1;
        this.X = 0.0F;
        this.name = var3;
        this.height = 0.0F;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        super.a(var1);

        if (var1.hasKey("playerGameType"))
        {
            this.itemInWorldManager.setGameMode(EnumGamemode.a(var1.getInt("playerGameType")));
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.setInt("playerGameType", this.itemInWorldManager.getGameMode().a());
    }

    /**
     * Add experience levels to this player.
     */
    public void levelDown(int var1)
    {
        super.levelDown(var1);
        this.lastSentExp = -1;
    }

    public void syncInventory()
    {
        this.activeContainer.addSlotListener(this);
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void e_()
    {
        this.height = 0.0F;
    }

    public float getHeadHeight()
    {
        return 1.62F;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void j_()
    {
        this.itemInWorldManager.a();
        --this.invulnerableTicks;
        this.activeContainer.b();

        if (!this.chunkCoordIntPairQueue.isEmpty())
        {
            ArrayList var1 = new ArrayList();
            Iterator var2 = this.chunkCoordIntPairQueue.iterator();
            ArrayList var3 = new ArrayList();

            while (var2.hasNext() && var1.size() < 5)
            {
                ChunkCoordIntPair var4 = (ChunkCoordIntPair)var2.next();
                var2.remove();

                if (var4 != null && this.world.isLoaded(var4.x << 4, 0, var4.z << 4))
                {
                    var1.add(this.world.getChunkAt(var4.x, var4.z));
                    var3.addAll(((WorldServer)this.world).getTileEntities(var4.x * 16, 0, var4.z * 16, var4.x * 16 + 16, 256, var4.z * 16 + 16));
                }
            }

            if (!var1.isEmpty())
            {
                this.netServerHandler.sendPacket(new Packet56MapChunkBulk(var1));
                Iterator var9 = var3.iterator();

                while (var9.hasNext())
                {
                    TileEntity var5 = (TileEntity)var9.next();
                    this.b(var5);
                }
            }
        }

        if (!this.removeQueue.isEmpty())
        {
            int var6 = Math.min(this.removeQueue.size(), 127);
            int[] var7 = new int[var6];
            Iterator var8 = this.removeQueue.iterator();
            int var10 = 0;

            while (var8.hasNext() && var10 < var6)
            {
                var7[var10++] = ((Integer)var8.next()).intValue();
                var8.remove();
            }

            this.netServerHandler.sendPacket(new Packet29DestroyEntity(var7));
        }
    }

    public void g()
    {
        super.j_();

        for (int var1 = 0; var1 < this.inventory.getSize(); ++var1)
        {
            ItemStack var2 = this.inventory.getItem(var1);

            if (var2 != null && Item.byId[var2.id].f() && this.netServerHandler.lowPriorityCount() <= 5)
            {
                Packet var3 = ((ItemWorldMapBase)Item.byId[var2.id]).c(var2, this.world, this);

                if (var3 != null)
                {
                    this.netServerHandler.sendPacket(var3);
                }
            }
        }

        if (this.getHealth() != this.cn || this.co != this.foodData.a() || this.foodData.e() == 0.0F != this.cp)
        {
            this.netServerHandler.sendPacket(new Packet8UpdateHealth(this.getHealth(), this.foodData.a(), this.foodData.e()));
            this.cn = this.getHealth();
            this.co = this.foodData.a();
            this.cp = this.foodData.e() == 0.0F;
        }

        if (this.expTotal != this.lastSentExp)
        {
            this.lastSentExp = this.expTotal;
            this.netServerHandler.sendPacket(new Packet43SetExperience(this.exp, this.expTotal, this.expLevel));
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void die(DamageSource var1)
    {
        this.server.getServerConfigurationManager().sendAll(new Packet3Chat(var1.getLocalizedDeathMessage(this)));

        if (!this.world.getGameRules().getBoolean("keepInventory"))
        {
            this.inventory.l();
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean damageEntity(DamageSource var1, int var2)
    {
        if (this.invulnerableTicks > 0)
        {
            return false;
        }
        else
        {
            if (!this.server.getPvP() && var1 instanceof EntityDamageSource)
            {
                Entity var3 = var1.getEntity();

                if (var3 instanceof EntityHuman)
                {
                    return false;
                }

                if (var3 instanceof EntityArrow)
                {
                    EntityArrow var4 = (EntityArrow)var3;

                    if (var4.shooter instanceof EntityHuman)
                    {
                        return false;
                    }
                }
            }

            return super.damageEntity(var1, var2);
        }
    }

    /**
     * returns if pvp is enabled or not
     */
    protected boolean h()
    {
        return this.server.getPvP();
    }

    public void b(int var1)
    {
        if (this.dimension == 1 && var1 == 1)
        {
            this.a(AchievementList.C);
            this.world.kill(this);
            this.viewingCredits = true;
            this.netServerHandler.sendPacket(new Packet70Bed(4, 0));
        }
        else
        {
            if (this.dimension == 1 && var1 == 0)
            {
                this.a(AchievementList.B);
                ChunkCoordinates var2 = this.server.getWorldServer(var1).getDimensionSpawn();

                if (var2 != null)
                {
                    this.netServerHandler.a((double)var2.x, (double)var2.y, (double)var2.z, 0.0F, 0.0F);
                }

                var1 = 1;
            }
            else
            {
                this.a(AchievementList.x);
            }

            this.server.getServerConfigurationManager().changeDimension(this, var1);
            this.lastSentExp = -1;
            this.cn = -1;
            this.co = -1;
        }
    }

    /**
     * gets description packets from all TileEntity's that override func_20070
     */
    private void b(TileEntity var1)
    {
        if (var1 != null)
        {
            Packet var2 = var1.l();

            if (var2 != null)
            {
                this.netServerHandler.sendPacket(var2);
            }
        }
    }

    /**
     * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
     */
    public void receive(Entity var1, int var2)
    {
        super.receive(var1, var2);
        this.activeContainer.b();
    }

    /**
     * puts player to sleep on specified bed if possible
     */
    public EnumBedResult a(int var1, int var2, int var3)
    {
        EnumBedResult var4 = super.a(var1, var2, var3);

        if (var4 == EnumBedResult.OK)
        {
            Packet17EntityLocationAction var5 = new Packet17EntityLocationAction(this, 0, var1, var2, var3);
            this.p().getTracker().a(this, var5);
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.netServerHandler.sendPacket(var5);
        }

        return var4;
    }

    /**
     * Wake up the player if they're sleeping.
     */
    public void a(boolean var1, boolean var2, boolean var3)
    {
        if (this.isSleeping())
        {
            this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(this, 3));
        }

        super.a(var1, var2, var3);

        if (this.netServerHandler != null)
        {
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mount(Entity var1)
    {
        super.mount(var1);
        this.netServerHandler.sendPacket(new Packet39AttachEntity(this, this.vehicle));
        this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void a(double var1, boolean var3) {}

    /**
     * process player falling based on movement packet
     */
    public void b(double var1, boolean var3)
    {
        super.a(var1, var3);
    }

    /**
     * get the next window id to use
     */
    private void nextContainerCounter()
    {
        this.containerCounter = this.containerCounter % 100 + 1;
    }

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void startCrafting(int var1, int var2, int var3)
    {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 1, "Crafting", 9));
        this.activeContainer = new ContainerWorkbench(this.inventory, this.world, var1, var2, var3);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void startEnchanting(int var1, int var2, int var3)
    {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 4, "Enchanting", 9));
        this.activeContainer = new ContainerEnchantTable(this.inventory, this.world, var1, var2, var3);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    /**
     * Displays the GUI for interacting with an anvil.
     */
    public void openAnvil(int var1, int var2, int var3)
    {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 8, "Repairing", 9));
        this.activeContainer = new ContainerAnvil(this.inventory, this.world, var1, var2, var3, this);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void openContainer(IInventory var1)
    {
        if (this.activeContainer != this.defaultContainer)
        {
            this.closeInventory();
        }

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 0, var1.getName(), var1.getSize()));
        this.activeContainer = new ContainerChest(this.inventory, var1);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    /**
     * Displays the furnace GUI for the passed in furnace entity. Args: tileEntityFurnace
     */
    public void openFurnace(TileEntityFurnace var1)
    {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 2, var1.getName(), var1.getSize()));
        this.activeContainer = new ContainerFurnace(this.inventory, var1);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    /**
     * Displays the dipsenser GUI for the passed in dispenser entity. Args: TileEntityDispenser
     */
    public void openDispenser(TileEntityDispenser var1)
    {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 3, var1.getName(), var1.getSize()));
        this.activeContainer = new ContainerDispenser(this.inventory, var1);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    /**
     * Displays the GUI for interacting with a brewing stand.
     */
    public void openBrewingStand(TileEntityBrewingStand var1)
    {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 5, var1.getName(), var1.getSize()));
        this.activeContainer = new ContainerBrewingStand(this.inventory, var1);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    /**
     * Displays the GUI for interacting with a beacon.
     */
    public void openBeacon(TileEntityBeacon var1)
    {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 7, var1.getName(), var1.getSize()));
        this.activeContainer = new ContainerBeacon(this.inventory, var1);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openTrade(IMerchant var1)
    {
        this.nextContainerCounter();
        this.activeContainer = new ContainerMerchant(this.inventory, var1, this.world);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
        InventoryMerchant var2 = ((ContainerMerchant)this.activeContainer).getMerchantInventory();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 6, var2.getName(), var2.getSize()));
        MerchantRecipeList var3 = var1.getOffers(this);

        if (var3 != null)
        {
            try
            {
                ByteArrayOutputStream var4 = new ByteArrayOutputStream();
                DataOutputStream var5 = new DataOutputStream(var4);
                var5.writeInt(this.containerCounter);
                var3.a(var5);
                this.netServerHandler.sendPacket(new Packet250CustomPayload("MC|TrList", var4.toByteArray()));
            }
            catch (IOException var6)
            {
                var6.printStackTrace();
            }
        }
    }

    /**
     * inform the player of a change in a single slot
     */
    public void a(Container var1, int var2, ItemStack var3)
    {
        if (!(var1.getSlot(var2) instanceof SlotResult))
        {
            if (!this.h)
            {
                this.netServerHandler.sendPacket(new Packet103SetSlot(var1.windowId, var2, var3));
            }
        }
    }

    public void updateInventory(Container var1)
    {
        this.a(var1, var1.a());
    }

    /**
     * update the crafting window inventory with the items in the list
     */
    public void a(Container var1, List var2)
    {
        this.netServerHandler.sendPacket(new Packet104WindowItems(var1.windowId, var2));
        this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getCarried()));
    }

    /**
     * send information about the crafting inventory to the client(currently only for furnace times)
     */
    public void setContainerData(Container var1, int var2, int var3)
    {
        this.netServerHandler.sendPacket(new Packet105CraftProgressBar(var1.windowId, var2, var3));
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    public void closeInventory()
    {
        this.netServerHandler.sendPacket(new Packet101CloseWindow(this.activeContainer.windowId));
        this.k();
    }

    /**
     * updates item held by mouse, This method always returns before doing anything...
     */
    public void broadcastCarriedItem()
    {
        if (!this.h)
        {
            this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getCarried()));
        }
    }

    /**
     * close the current crafting gui
     */
    public void k()
    {
        this.activeContainer.a(this);
        this.activeContainer = this.defaultContainer;
    }

    /**
     * Adds a value to a statistic field.
     */
    public void a(Statistic var1, int var2)
    {
        if (var1 != null)
        {
            if (!var1.f)
            {
                while (var2 > 100)
                {
                    this.netServerHandler.sendPacket(new Packet200Statistic(var1.e, 100));
                    var2 -= 100;
                }

                this.netServerHandler.sendPacket(new Packet200Statistic(var1.e, var2));
            }
        }
    }

    public void l()
    {
        if (this.vehicle != null)
        {
            this.mount(this.vehicle);
        }

        if (this.passenger != null)
        {
            this.passenger.mount(this);
        }

        if (this.sleeping)
        {
            this.a(true, false, false);
        }
    }

    /**
     * this function is called when a players inventory is sent to him, lastHealth is updated on any dimension
     * transitions, then reset.
     */
    public void m()
    {
        this.cn = -99999999;
    }

    /**
     * Add a chat message to the player
     */
    public void b(String var1)
    {
        LocaleLanguage var2 = LocaleLanguage.a();
        String var3 = var2.b(var1);
        this.netServerHandler.sendPacket(new Packet3Chat(var3));
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void n()
    {
        this.netServerHandler.sendPacket(new Packet38EntityStatus(this.id, (byte)9));
        super.n();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int maxItemUseDuration
     */
    public void a(ItemStack var1, int var2)
    {
        super.a(var1, var2);

        if (var1 != null && var1.getItem() != null && var1.getItem().d_(var1) == EnumAnimation.eat)
        {
            this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(this, 5));
        }
    }

    /**
     * Copies the values from the given player into this player if boolean par2 is true. Always clones Ender Chest
     * Inventory.
     */
    public void copyTo(EntityHuman var1, boolean var2)
    {
        super.copyTo(var1, var2);
        this.lastSentExp = -1;
        this.cn = -1;
        this.co = -1;
        this.removeQueue.addAll(((EntityPlayer)var1).removeQueue);
    }

    protected void a(MobEffect var1)
    {
        super.a(var1);
        this.netServerHandler.sendPacket(new Packet41MobEffect(this.id, var1));
    }

    protected void b(MobEffect var1)
    {
        super.b(var1);
        this.netServerHandler.sendPacket(new Packet41MobEffect(this.id, var1));
    }

    protected void c(MobEffect var1)
    {
        super.c(var1);
        this.netServerHandler.sendPacket(new Packet42RemoveMobEffect(this.id, var1));
    }

    /**
     * Sets the position of the entity and updates the 'last' variables
     */
    public void enderTeleportTo(double var1, double var3, double var5)
    {
        this.netServerHandler.a(var1, var3, var5, this.yaw, this.pitch);
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void b(Entity var1)
    {
        this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(var1, 6));
    }

    public void c(Entity var1)
    {
        this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(var1, 7));
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void updateAbilities()
    {
        if (this.netServerHandler != null)
        {
            this.netServerHandler.sendPacket(new Packet202Abilities(this.abilities));
        }
    }

    public WorldServer p()
    {
        return (WorldServer)this.world;
    }

    /**
     * Sets the player's game type
     */
    public void a(EnumGamemode var1)
    {
        this.itemInWorldManager.setGameMode(var1);
        this.netServerHandler.sendPacket(new Packet70Bed(3, var1.a()));
    }

    public void sendMessage(String var1)
    {
        this.netServerHandler.sendPacket(new Packet3Chat(var1));
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean a(int var1, String var2)
    {
        return "seed".equals(var2) && !this.server.T() ? true : (!"tell".equals(var2) && !"help".equals(var2) && !"me".equals(var2) ? this.server.getServerConfigurationManager().isOp(this.name) : true);
    }

    public String func_71114_r()
    {
        String var1 = this.netServerHandler.networkManager.getSocketAddress().toString();
        var1 = var1.substring(var1.indexOf("/") + 1);
        var1 = var1.substring(0, var1.indexOf(":"));
        return var1;
    }

    public void a(Packet204LocaleAndViewDistance var1)
    {
        if (this.locale.b().containsKey(var1.d()))
        {
            this.locale.a(var1.d());
        }

        int var2 = 256 >> var1.f();

        if (var2 > 3 && var2 < 15)
        {
            this.cs = var2;
        }

        this.ct = var1.g();
        this.cu = var1.h();

        if (this.server.I() && this.server.H().equals(this.name))
        {
            this.server.c(var1.i());
        }

        this.func_82239_b(1, !var1.func_82563_j());
    }

    public LocaleLanguage getLocale()
    {
        return this.locale;
    }

    public int getChatFlags()
    {
        return this.ct;
    }

    /**
     * on recieving this message the client (if permission is given) will download the requested textures
     */
    public void a(String var1, int var2)
    {
        String var3 = var1 + "\u0000" + var2;
        this.netServerHandler.sendPacket(new Packet250CustomPayload("MC|TPack", var3.getBytes()));
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates b()
    {
        return new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY + 0.5D), MathHelper.floor(this.locZ));
    }
}
