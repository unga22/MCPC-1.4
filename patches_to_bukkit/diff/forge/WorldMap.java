package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorldMap extends WorldMapBase
{
    public int centerX;
    public int centerZ;
    public int map;
    public byte scale;

    /** colours */
    public byte[] colors = new byte[16384];

    /**
     * Holds a reference to the MapInfo of the players who own a copy of the map
     */
    public List f = new ArrayList();

    /**
     * Holds a reference to the players who own a copy of the map and a reference to their MapInfo
     */
    private Map i = new HashMap();
    public Map g = new LinkedHashMap();

    public WorldMap(String var1)
    {
        super(var1);
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void a(NBTTagCompound var1)
    {
        NBTBase var2 = var1.get("dimension");

        if (var2 instanceof NBTTagByte)
        {
            this.map = ((NBTTagByte)var2).data;
        }
        else
        {
            this.map = ((NBTTagInt)var2).data;
        }

        this.centerX = var1.getInt("xCenter");
        this.centerZ = var1.getInt("zCenter");
        this.scale = var1.getByte("scale");

        if (this.scale < 0)
        {
            this.scale = 0;
        }

        if (this.scale > 4)
        {
            this.scale = 4;
        }

        short var3 = var1.getShort("width");
        short var4 = var1.getShort("height");

        if (var3 == 128 && var4 == 128)
        {
            this.colors = var1.getByteArray("colors");
        }
        else
        {
            byte[] var5 = var1.getByteArray("colors");
            this.colors = new byte[16384];
            int var6 = (128 - var3) / 2;
            int var7 = (128 - var4) / 2;

            for (int var8 = 0; var8 < var4; ++var8)
            {
                int var9 = var8 + var7;

                if (var9 >= 0 || var9 < 128)
                {
                    for (int var10 = 0; var10 < var3; ++var10)
                    {
                        int var11 = var10 + var6;

                        if (var11 >= 0 || var11 < 128)
                        {
                            this.colors[var11 + var9 * 128] = var5[var10 + var8 * var3];
                        }
                    }
                }
            }
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void b(NBTTagCompound var1)
    {
        var1.setInt("dimension", this.map);
        var1.setInt("xCenter", this.centerX);
        var1.setInt("zCenter", this.centerZ);
        var1.setByte("scale", this.scale);
        var1.setShort("width", (short)128);
        var1.setShort("height", (short)128);
        var1.setByteArray("colors", this.colors);
    }

    /**
     * Adds the player passed to the list of visible players and checks to see which players are visible
     */
    public void a(EntityHuman var1, ItemStack var2)
    {
        if (!this.i.containsKey(var1))
        {
            WorldMapHumanTracker var3 = new WorldMapHumanTracker(this, var1);
            this.i.put(var1, var3);
            this.f.add(var3);
        }

        if (!var1.inventory.c(var2))
        {
            this.g.remove(var1.getName());
        }

        for (int var5 = 0; var5 < this.f.size(); ++var5)
        {
            WorldMapHumanTracker var4 = (WorldMapHumanTracker)this.f.get(var5);

            if (!var4.trackee.dead && (var4.trackee.inventory.c(var2) || var2.y()))
            {
                if (!var2.y() && var4.trackee.dimension == this.map)
                {
                    this.func_82567_a(0, var4.trackee.world, var4.trackee.getName(), var4.trackee.locX, var4.trackee.locZ, (double)var4.trackee.yaw);
                }
            }
            else
            {
                this.i.remove(var4.trackee);
                this.f.remove(var4);
            }
        }

        if (var2.y())
        {
            this.func_82567_a(1, var1.world, "frame-" + var2.z().id, (double)var2.z().x, (double)var2.z().z, (double)(var2.z().field_82332_a * 90));
        }
    }

    private void func_82567_a(int var1, World var2, String var3, double var4, double var6, double var8)
    {
        int var10 = 1 << this.scale;
        float var11 = (float)(var4 - (double)this.centerX) / (float)var10;
        float var12 = (float)(var6 - (double)this.centerZ) / (float)var10;
        byte var13 = (byte)((int)((double)(var11 * 2.0F) + 0.5D));
        byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
        byte var15 = 63;
        byte var16;

        if (var11 >= (float)(-var15) && var12 >= (float)(-var15) && var11 <= (float)var15 && var12 <= (float)var15)
        {
            var8 += var8 < 0.0D ? -8.0D : 8.0D;
            var16 = (byte)((int)(var8 * 16.0D / 360.0D));

            if (this.map < 0)
            {
                int var17 = (int)(var2.getWorldData().g() / 10L);
                var16 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(var11) >= 320.0F || Math.abs(var12) >= 320.0F)
            {
                this.g.remove(var3);
                return;
            }

            var1 = 6;
            var16 = 0;

            if (var11 <= (float)(-var15))
            {
                var13 = (byte)((int)((double)(var15 * 2) + 2.5D));
            }

            if (var12 <= (float)(-var15))
            {
                var14 = (byte)((int)((double)(var15 * 2) + 2.5D));
            }

            if (var11 >= (float)var15)
            {
                var13 = (byte)(var15 * 2 + 1);
            }

            if (var12 >= (float)var15)
            {
                var14 = (byte)(var15 * 2 + 1);
            }
        }

        this.g.put(var3, new WorldMapDecoration(this, (byte)var1, var13, var14, var16));
    }

    public byte[] func_76193_a(ItemStack var1, World var2, EntityHuman var3)
    {
        WorldMapHumanTracker var4 = (WorldMapHumanTracker)this.i.get(var3);
        return var4 == null ? null : var4.a(var1);
    }

    public void func_76194_a(int var1, int var2, int var3)
    {
        super.c();

        for (int var4 = 0; var4 < this.f.size(); ++var4)
        {
            WorldMapHumanTracker var5 = (WorldMapHumanTracker)this.f.get(var4);

            if (var5.field_76209_b[var1] < 0 || var5.field_76209_b[var1] > var2)
            {
                var5.field_76209_b[var1] = var2;
            }

            if (var5.field_76210_c[var1] < 0 || var5.field_76210_c[var1] < var3)
            {
                var5.field_76210_c[var1] = var3;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void a(byte[] var1)
    {
        int var2;

        if (var1[0] == 0)
        {
            var2 = var1[1] & 255;
            int var3 = var1[2] & 255;

            for (int var4 = 0; var4 < var1.length - 3; ++var4)
            {
                this.colors[(var4 + var3) * 128 + var2] = var1[var4 + 3];
            }

            this.c();
        }
        else if (var1[0] == 1)
        {
            this.g.clear();

            for (var2 = 0; var2 < (var1.length - 1) / 3; ++var2)
            {
                byte var7 = (byte)(var1[var2 * 3 + 1] >> 4);
                byte var8 = var1[var2 * 3 + 2];
                byte var5 = var1[var2 * 3 + 3];
                byte var6 = (byte)(var1[var2 * 3 + 1] & 15);
                this.g.put("icon-" + var2, new WorldMapDecoration(this, var7, var8, var5, var6));
            }
        }
        else if (var1[0] == 2)
        {
            this.scale = var1[1];
        }
    }

    public WorldMapHumanTracker func_82568_a(EntityHuman var1)
    {
        WorldMapHumanTracker var2 = (WorldMapHumanTracker)this.i.get(var1);

        if (var2 == null)
        {
            var2 = new WorldMapHumanTracker(this, var1);
            this.i.put(var1, var2);
            this.f.add(var2);
        }

        return var2;
    }
}
