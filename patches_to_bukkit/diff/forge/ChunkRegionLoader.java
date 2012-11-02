package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent$Load;
import net.minecraftforge.event.world.ChunkDataEvent$Save;

public class ChunkRegionLoader implements IAsyncChunkSaver, IChunkLoader
{
    private List a = new ArrayList();
    private Set b = new HashSet();
    private Object c = new Object();

    /** Save directory for chunks using the Anvil format */
    final File d;

    public ChunkRegionLoader(File var1)
    {
        this.d = var1;
    }

    /**
     * Loads the specified(XZ) chunk into the specified world.
     */
    public Chunk a(World var1, int var2, int var3) throws IOException
    {
        NBTTagCompound var4 = null;
        ChunkCoordIntPair var5 = new ChunkCoordIntPair(var2, var3);
        Object var6 = this.c;
        Object var7 = this.c;

        synchronized (this.c)
        {
            if (this.b.contains(var5))
            {
                Iterator var8 = this.a.iterator();

                while (var8.hasNext())
                {
                    PendingChunkToSave var9 = (PendingChunkToSave)var8.next();

                    if (var9.a.equals(var5))
                    {
                        var4 = var9.b;
                        break;
                    }
                }
            }
        }

        if (var4 == null)
        {
            DataInputStream var12 = RegionFileCache.c(this.d, var2, var3);

            if (var12 == null)
            {
                return null;
            }

            var4 = NBTCompressedStreamTools.a(var12);
        }

        return this.a(var1, var2, var3, var4);
    }

    /**
     * Wraps readChunkFromNBT. Checks the coordinates and several NBT tags.
     */
    protected Chunk a(World var1, int var2, int var3, NBTTagCompound var4)
    {
        if (!var4.hasKey("Level"))
        {
            System.out.println("Chunk file at " + var2 + "," + var3 + " is missing level data, skipping");
            return null;
        }
        else if (!var4.getCompound("Level").hasKey("Sections"))
        {
            System.out.println("Chunk file at " + var2 + "," + var3 + " is missing block data, skipping");
            return null;
        }
        else
        {
            Chunk var5 = this.a(var1, var4.getCompound("Level"));

            if (!var5.a(var2, var3))
            {
                System.out.println("Chunk file at " + var2 + "," + var3 + " is in the wrong location; relocating. (Expected " + var2 + ", " + var3 + ", got " + var5.x + ", " + var5.z + ")");
                var4.setInt("xPos", var2);
                var4.setInt("zPos", var3);
                var5 = this.a(var1, var4.getCompound("Level"));
            }

            MinecraftForge.EVENT_BUS.post(new ChunkDataEvent$Load(var5, var4));
            return var5;
        }
    }

    public void a(World var1, Chunk var2) throws ExceptionWorldConflict, IOException
    {
        var1.C();

        try
        {
            NBTTagCompound var3 = new NBTTagCompound();
            NBTTagCompound var4 = new NBTTagCompound();
            var3.set("Level", var4);
            this.a(var2, var1, var4);
            this.func_75824_a(var2.l(), var3);
            MinecraftForge.EVENT_BUS.post(new ChunkDataEvent$Save(var2, var3));
        }
        catch (Exception var5)
        {
            var5.printStackTrace();
        }
    }

    protected void func_75824_a(ChunkCoordIntPair var1, NBTTagCompound var2)
    {
        Object var3 = this.c;
        Object var4 = this.c;

        synchronized (this.c)
        {
            if (this.b.contains(var1))
            {
                for (int var5 = 0; var5 < this.a.size(); ++var5)
                {
                    if (((PendingChunkToSave)this.a.get(var5)).a.equals(var1))
                    {
                        this.a.set(var5, new PendingChunkToSave(var1, var2));
                        return;
                    }
                }
            }

            this.a.add(new PendingChunkToSave(var1, var2));
            this.b.add(var1);
            FileIOThread.a.a(this);
        }
    }

    /**
     * Returns a boolean stating if the write was unsuccessful.
     */
    public boolean c()
    {
        PendingChunkToSave var1 = null;
        Object var2 = this.c;
        Object var3 = this.c;

        synchronized (this.c)
        {
            if (this.a.isEmpty())
            {
                return false;
            }

            var1 = (PendingChunkToSave)this.a.remove(0);
            this.b.remove(var1.a);
        }

        if (var1 != null)
        {
            try
            {
                this.a(var1);
            }
            catch (Exception var5)
            {
                var5.printStackTrace();
            }
        }

        return true;
    }

    private void a(PendingChunkToSave var1) throws IOException
    {
        DataOutputStream var2 = RegionFileCache.d(this.d, var1.a.x, var1.a.z);
        NBTCompressedStreamTools.a(var1.b, var2);
        var2.close();
    }

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    public void b(World var1, Chunk var2) {}

    /**
     * Called every World.tick()
     */
    public void a() {}

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unused.
     */
    public void b() {}

    /**
     * Writes the Chunk passed as an argument to the NBTTagCompound also passed, using the World argument to retrieve
     * the Chunk's last update time.
     */
    private void a(Chunk var1, World var2, NBTTagCompound var3)
    {
        var3.setInt("xPos", var1.x);
        var3.setInt("zPos", var1.z);
        var3.setLong("LastUpdate", var2.getTime());
        var3.setIntArray("HeightMap", var1.heightMap);
        var3.setBoolean("TerrainPopulated", var1.done);
        ChunkSection[] var4 = var1.i();
        NBTTagList var5 = new NBTTagList("Sections");
        ChunkSection[] var6 = var4;
        int var7 = var4.length;
        NBTTagCompound var8;

        for (int var9 = 0; var9 < var7; ++var9)
        {
            ChunkSection var10 = var6[var9];

            if (var10 != null)
            {
                var8 = new NBTTagCompound();
                var8.setByte("Y", (byte)(var10.d() >> 4 & 255));
                var8.setByteArray("Blocks", var10.g());

                if (var10.i() != null)
                {
                    var8.setByteArray("Add", var10.i().a);
                }

                var8.setByteArray("Data", var10.j().a);
                var8.setByteArray("SkyLight", var10.l().a);
                var8.setByteArray("BlockLight", var10.k().a);
                var5.add(var8);
            }
        }

        var3.set("Sections", var5);
        var3.setByteArray("Biomes", var1.m());
        var1.m = false;
        NBTTagList var19 = new NBTTagList();
        Iterator var21;

        for (var7 = 0; var7 < var1.entitySlices.length; ++var7)
        {
            var21 = var1.entitySlices[var7].iterator();

            while (var21.hasNext())
            {
                Entity var11 = (Entity)var21.next();
                var1.m = true;
                var8 = new NBTTagCompound();

                if (var11.c(var8))
                {
                    var19.add(var8);
                }
            }
        }

        var3.set("Entities", var19);
        NBTTagList var20 = new NBTTagList();
        var21 = var1.tileEntities.values().iterator();

        while (var21.hasNext())
        {
            TileEntity var12 = (TileEntity)var21.next();
            var8 = new NBTTagCompound();
            var12.b(var8);
            var20.add(var8);
        }

        var3.set("TileEntities", var20);
        List var22 = var2.a(var1, false);

        if (var22 != null)
        {
            long var13 = var2.getTime();
            NBTTagList var15 = new NBTTagList();
            Iterator var16 = var22.iterator();

            while (var16.hasNext())
            {
                NextTickListEntry var17 = (NextTickListEntry)var16.next();
                NBTTagCompound var18 = new NBTTagCompound();
                var18.setInt("i", var17.d);
                var18.setInt("x", var17.a);
                var18.setInt("y", var17.b);
                var18.setInt("z", var17.c);
                var18.setInt("t", (int)(var17.e - var13));
                var15.add(var18);
            }

            var3.set("TileTicks", var15);
        }
    }

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    private Chunk a(World var1, NBTTagCompound var2)
    {
        int var3 = var2.getInt("xPos");
        int var4 = var2.getInt("zPos");
        Chunk var5 = new Chunk(var1, var3, var4);
        var5.heightMap = var2.getIntArray("HeightMap");
        var5.done = var2.getBoolean("TerrainPopulated");
        NBTTagList var6 = var2.getList("Sections");
        byte var7 = 16;
        ChunkSection[] var8 = new ChunkSection[var7];

        for (int var9 = 0; var9 < var6.size(); ++var9)
        {
            NBTTagCompound var10 = (NBTTagCompound)var6.get(var9);
            byte var11 = var10.getByte("Y");
            ChunkSection var12 = new ChunkSection(var11 << 4);
            var12.a(var10.getByteArray("Blocks"));

            if (var10.hasKey("Add"))
            {
                var12.a(new NibbleArray(var10.getByteArray("Add"), 4));
            }

            var12.b(new NibbleArray(var10.getByteArray("Data"), 4));
            var12.d(new NibbleArray(var10.getByteArray("SkyLight"), 4));
            var12.c(new NibbleArray(var10.getByteArray("BlockLight"), 4));
            var12.recalcBlockCounts();
            var8[var11] = var12;
        }

        var5.a(var8);

        if (var2.hasKey("Biomes"))
        {
            var5.a(var2.getByteArray("Biomes"));
        }

        NBTTagList var14 = var2.getList("Entities");

        if (var14 != null)
        {
            for (int var17 = 0; var17 < var14.size(); ++var17)
            {
                NBTTagCompound var16 = (NBTTagCompound)var14.get(var17);
                Entity var18 = EntityTypes.a(var16, var1);
                var5.m = true;

                if (var18 != null)
                {
                    var5.a(var18);
                }
            }
        }

        NBTTagList var15 = var2.getList("TileEntities");

        if (var15 != null)
        {
            for (int var21 = 0; var21 < var15.size(); ++var21)
            {
                NBTTagCompound var20 = (NBTTagCompound)var15.get(var21);
                TileEntity var13 = TileEntity.c(var20);

                if (var13 != null)
                {
                    var5.a(var13);
                }
            }
        }

        if (var2.hasKey("TileTicks"))
        {
            NBTTagList var19 = var2.getList("TileTicks");

            if (var19 != null)
            {
                for (int var22 = 0; var22 < var19.size(); ++var22)
                {
                    NBTTagCompound var23 = (NBTTagCompound)var19.get(var22);
                    var1.b(var23.getInt("x"), var23.getInt("y"), var23.getInt("z"), var23.getInt("i"), var23.getInt("t"));
                }
            }
        }

        return var5;
    }
}
