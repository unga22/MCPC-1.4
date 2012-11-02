package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.HashMap;
import java.util.Map;

public class TileEntity
{
    /**
     * A HashMap storing string names of classes mapping to the actual java.lang.Class type.
     */
    private static Map a = new HashMap();

    /**
     * A HashMap storing the classes and mapping to the string names (reverse of nameToClassMap).
     */
    private static Map b = new HashMap();

    /** The reference to the world. */
    public World world;

    /** The x coordinate of the tile entity. */
    public int x;

    /** The y coordinate of the tile entity. */
    public int y;

    /** The z coordinate of the tile entity. */
    public int z;
    protected boolean o;
    public int p = -1;

    /** the Block type that this TileEntity is contained within */
    public Block q;

    /**
     * Adds a new two-way mapping between the class and its string name in both hashmaps.
     */
    public static void a(Class var0, String var1)
    {
        if (a.containsKey(var1))
        {
            throw new IllegalArgumentException("Duplicate id: " + var1);
        }
        else
        {
            a.put(var1, var0);
            b.put(var0, var1);
        }
    }

    @SideOnly(Side.CLIENT)
    public World n()
    {
        return this.world;
    }

    /**
     * Sets the worldObj for this tileEntity.
     */
    public void b(World var1)
    {
        this.world = var1;
    }

    public boolean func_70309_m()
    {
        return this.world != null;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        this.x = var1.getInt("x");
        this.y = var1.getInt("y");
        this.z = var1.getInt("z");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        String var2 = (String)b.get(this.getClass());

        if (var2 == null)
        {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        else
        {
            var1.setString("id", var2);
            var1.setInt("x", this.x);
            var1.setInt("y", this.y);
            var1.setInt("z", this.z);
        }
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void g() {}

    /**
     * Creates a new entity and loads its data from the specified NBT.
     */
    public static TileEntity c(NBTTagCompound var0)
    {
        TileEntity var1 = null;

        try
        {
            Class var2 = (Class)a.get(var0.getString("id"));

            if (var2 != null)
            {
                var1 = (TileEntity)var2.newInstance();
            }
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }

        if (var1 != null)
        {
            var1.a(var0);
        }
        else
        {
            System.out.println("Skipping TileEntity with id " + var0.getString("id"));
        }

        return var1;
    }

    /**
     * Returns block data at the location of this entity (client-only).
     */
    public int p()
    {
        if (this.p == -1)
        {
            this.p = this.world.getData(this.x, this.y, this.z);
        }

        return this.p;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void update()
    {
        if (this.world != null)
        {
            this.p = this.world.getData(this.x, this.y, this.z);
            this.world.b(this.x, this.y, this.z, this);
        }
    }

    @SideOnly(Side.CLIENT)
    public double a(double var1, double var3, double var5)
    {
        double var7 = (double)this.x + 0.5D - var1;
        double var9 = (double)this.y + 0.5D - var3;
        double var11 = (double)this.z + 0.5D - var5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    @SideOnly(Side.CLIENT)
    public double m()
    {
        return 4096.0D;
    }

    public Block func_70311_o()
    {
        if (this.q == null)
        {
            this.q = Block.byId[this.world.getTypeId(this.x, this.y, this.z)];
        }

        return this.q;
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet l()
    {
        return null;
    }

    /**
     * returns true if tile entity is invalid, false otherwise
     */
    public boolean r()
    {
        return this.o;
    }

    /**
     * invalidates a tile entity
     */
    public void w_()
    {
        this.o = true;
    }

    /**
     * validates a tile entity
     */
    public void s()
    {
        this.o = false;
    }

    /**
     * Called when a client event is received with the event number and argument, see World.sendClientEvent
     */
    public void b(int var1, int var2) {}

    /**
     * Causes the TileEntity to reset all it's cached values for it's container block, blockID, metaData and in the case
     * of chests, the adjcacent chest check
     */
    public void h()
    {
        this.q = null;
        this.p = -1;
    }

    public boolean canUpdate()
    {
        return true;
    }

    public void onDataPacket(INetworkManager var1, Packet132TileEntityData var2) {}

    public void onChunkUnload() {}

    static
    {
        a(TileEntityFurnace.class, "Furnace");
        a(TileEntityChest.class, "Chest");
        a(TileEntityEnderChest.class, "EnderChest");
        a(TileEntityRecordPlayer.class, "RecordPlayer");
        a(TileEntityDispenser.class, "Trap");
        a(TileEntitySign.class, "Sign");
        a(TileEntityMobSpawner.class, "MobSpawner");
        a(TileEntityNote.class, "Music");
        a(TileEntityPiston.class, "Piston");
        a(TileEntityBrewingStand.class, "Cauldron");
        a(TileEntityEnchantTable.class, "EnchantTable");
        a(TileEntityEnderPortal.class, "Airportal");
        a(TileEntityCommand.class, "Control");
        a(TileEntityBeacon.class, "Beacon");
        a(TileEntitySkull.class, "Skull");
    }
}
