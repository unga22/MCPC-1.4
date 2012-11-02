package net.minecraft.server;

public class ItemInWorldManager
{
    /** The world object that this object is connected to. */
    public World world;

    /** The EntityPlayerMP object that this object is connected to. */
    public EntityPlayer player;
    private EnumGamemode gamemode;

    /**
     * set to true on first call of destroyBlockInWorldPartially, false before any further calls
     */
    private boolean d;
    private int lastDigTick;
    private int f;
    private int g;
    private int h;
    private int currentTick;
    private boolean field_73097_j;
    private int k;
    private int l;
    private int m;
    private int field_73093_n;
    private int o;

    public ItemInWorldManager(World var1)
    {
        this.gamemode = EnumGamemode.NOT_SET;
        this.o = -1;
        this.world = var1;
    }

    public void setGameMode(EnumGamemode var1)
    {
        this.gamemode = var1;
        var1.a(this.player.abilities);
        this.player.updateAbilities();
    }

    public EnumGamemode getGameMode()
    {
        return this.gamemode;
    }

    /**
     * Get if we are in creative game mode.
     */
    public boolean isCreative()
    {
        return this.gamemode.d();
    }

    /**
     * if the gameType is currently NOT_SET then change it to par1
     */
    public void b(EnumGamemode var1)
    {
        if (this.gamemode == EnumGamemode.NOT_SET)
        {
            this.gamemode = var1;
        }

        this.setGameMode(this.gamemode);
    }

    public void a()
    {
        ++this.currentTick;
        int var1;
        float var4;
        int var5;

        if (this.field_73097_j)
        {
            var1 = this.currentTick - this.field_73093_n;
            int var2 = this.world.getTypeId(this.k, this.l, this.m);

            if (var2 == 0)
            {
                this.field_73097_j = false;
            }
            else
            {
                Block var3 = Block.byId[var2];
                var4 = var3.getDamage(this.player, this.player.world, this.k, this.l, this.m) * (float)(var1 + 1);
                var5 = (int)(var4 * 10.0F);

                if (var5 != this.o)
                {
                    this.world.g(this.player.id, this.k, this.l, this.m, var5);
                    this.o = var5;
                }

                if (var4 >= 1.0F)
                {
                    this.field_73097_j = false;
                    this.breakBlock(this.k, this.l, this.m);
                }
            }
        }
        else if (this.d)
        {
            var1 = this.world.getTypeId(this.f, this.g, this.h);
            Block var6 = Block.byId[var1];

            if (var6 == null)
            {
                this.world.g(this.player.id, this.f, this.g, this.h, -1);
                this.o = -1;
                this.d = false;
            }
            else
            {
                int var7 = this.currentTick - this.lastDigTick;
                var4 = var6.getDamage(this.player, this.player.world, this.f, this.g, this.h) * (float)(var7 + 1);
                var5 = (int)(var4 * 10.0F);

                if (var5 != this.o)
                {
                    this.world.g(this.player.id, this.f, this.g, this.h, var5);
                    this.o = var5;
                }
            }
        }
    }

    public void dig(int var1, int var2, int var3, int var4)
    {
        if (!this.gamemode.isAdventure() || this.player.func_82246_f(var1, var2, var3))
        {
            if (this.isCreative())
            {
                if (!this.world.douseFire((EntityHuman)null, var1, var2, var3, var4))
                {
                    this.breakBlock(var1, var2, var3);
                }
            }
            else
            {
                this.world.douseFire(this.player, var1, var2, var3, var4);
                this.lastDigTick = this.currentTick;
                float var5 = 1.0F;
                int var6 = this.world.getTypeId(var1, var2, var3);

                if (var6 > 0)
                {
                    Block.byId[var6].attack(this.world, var1, var2, var3, this.player);
                    var5 = Block.byId[var6].getDamage(this.player, this.player.world, var1, var2, var3);
                }

                if (var6 > 0 && var5 >= 1.0F)
                {
                    this.breakBlock(var1, var2, var3);
                }
                else
                {
                    this.d = true;
                    this.f = var1;
                    this.g = var2;
                    this.h = var3;
                    int var7 = (int)(var5 * 10.0F);
                    this.world.g(this.player.id, var1, var2, var3, var7);
                    this.o = var7;
                }
            }
        }
    }

    public void a(int var1, int var2, int var3)
    {
        if (var1 == this.f && var2 == this.g && var3 == this.h)
        {
            int var4 = this.currentTick - this.lastDigTick;
            int var5 = this.world.getTypeId(var1, var2, var3);

            if (var5 != 0)
            {
                Block var6 = Block.byId[var5];
                float var7 = var6.getDamage(this.player, this.player.world, var1, var2, var3) * (float)(var4 + 1);

                if (var7 >= 0.7F)
                {
                    this.d = false;
                    this.world.g(this.player.id, var1, var2, var3, -1);
                    this.breakBlock(var1, var2, var3);
                }
                else if (!this.field_73097_j)
                {
                    this.d = false;
                    this.field_73097_j = true;
                    this.k = var1;
                    this.l = var2;
                    this.m = var3;
                    this.field_73093_n = this.lastDigTick;
                }
            }
        }
    }

    /**
     * note: this ignores the pars passed in and continues to destroy the onClickedBlock
     */
    public void c(int var1, int var2, int var3)
    {
        this.d = false;
        this.world.g(this.player.id, this.f, this.g, this.h, -1);
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    private boolean d(int var1, int var2, int var3)
    {
        Block var4 = Block.byId[this.world.getTypeId(var1, var2, var3)];
        int var5 = this.world.getData(var1, var2, var3);

        if (var4 != null)
        {
            var4.a(this.world, var1, var2, var3, var5, this.player);
        }

        boolean var6 = this.world.setTypeId(var1, var2, var3, 0);

        if (var4 != null && var6)
        {
            var4.postBreak(this.world, var1, var2, var3, var5);
        }

        return var6;
    }

    /**
     * Attempts to harvest a block at the given coordinate
     */
    public boolean breakBlock(int var1, int var2, int var3)
    {
        if (this.gamemode.isAdventure() && !this.player.func_82246_f(var1, var2, var3))
        {
            return false;
        }
        else
        {
            int var4 = this.world.getTypeId(var1, var2, var3);
            int var5 = this.world.getData(var1, var2, var3);
            this.world.a(this.player, 2001, var1, var2, var3, var4 + (this.world.getData(var1, var2, var3) << 12));
            boolean var6 = this.d(var1, var2, var3);

            if (this.isCreative())
            {
                this.player.netServerHandler.sendPacket(new Packet53BlockChange(var1, var2, var3, this.world));
            }
            else
            {
                ItemStack var7 = this.player.bP();
                boolean var8 = this.player.b(Block.byId[var4]);

                if (var7 != null)
                {
                    var7.a(this.world, var4, var1, var2, var3, this.player);

                    if (var7.count == 0)
                    {
                        this.player.bQ();
                    }
                }

                if (var6 && var8)
                {
                    Block.byId[var4].a(this.world, this.player, var1, var2, var3, var5);
                }
            }

            return var6;
        }
    }

    /**
     * Attempts to right-click use an item by the given EntityPlayer in the given World
     */
    public boolean useItem(EntityHuman var1, World var2, ItemStack var3)
    {
        int var4 = var3.count;
        int var5 = var3.getData();
        ItemStack var6 = var3.a(var2, var1);

        if (var6 == var3 && (var6 == null || var6.count == var4 && var6.m() <= 0 && var6.getData() == var5))
        {
            return false;
        }
        else
        {
            var1.inventory.items[var1.inventory.itemInHandIndex] = var6;

            if (this.isCreative())
            {
                var6.count = var4;

                if (var6.f())
                {
                    var6.setData(var5);
                }
            }

            if (var6.count == 0)
            {
                var1.inventory.items[var1.inventory.itemInHandIndex] = null;
            }

            if (!var1.bI())
            {
                ((EntityPlayer)var1).updateInventory(var1.defaultContainer);
            }

            return true;
        }
    }

    /**
     * Activate the clicked on block, otherwise use the held item. Args: player, world, itemStack, x, y, z, side,
     * xOffset, yOffset, zOffset
     */
    public boolean interact(EntityHuman var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10)
    {
        int var11 = var2.getTypeId(var4, var5, var6);

        if (var11 > 0 && Block.byId[var11].interact(var2, var4, var5, var6, var1, var7, var8, var9, var10))
        {
            return true;
        }
        else if (var3 == null)
        {
            return false;
        }
        else if (this.isCreative())
        {
            int var12 = var3.getData();
            int var13 = var3.count;
            boolean var14 = var3.placeItem(var1, var2, var4, var5, var6, var7, var8, var9, var10);
            var3.setData(var12);
            var3.count = var13;
            return var14;
        }
        else
        {
            return var3.placeItem(var1, var2, var4, var5, var6, var7, var8, var9, var10);
        }
    }

    /**
     * Sets the world instance.
     */
    public void a(WorldServer var1)
    {
        this.world = var1;
    }
}
