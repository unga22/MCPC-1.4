package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;

public class ItemWorldMap extends ItemWorldMapBase
{
    protected ItemWorldMap(int var1)
    {
        super(var1);
        this.a(true);
    }

    @SideOnly(Side.CLIENT)
    public static WorldMap a(short var0, World var1)
    {
        String var2 = "map_" + var0;
        WorldMap var3 = (WorldMap)var1.a(WorldMap.class, var2);

        if (var3 == null)
        {
            var3 = new WorldMap(var2);
            var1.a(var2, var3);
        }

        return var3;
    }

    public WorldMap getSavedMap(ItemStack var1, World var2)
    {
        String var3 = "map_" + var1.getData();
        WorldMap var4 = (WorldMap)var2.a(WorldMap.class, var3);

        if (var4 == null && !var2.isStatic)
        {
            var1.setData(var2.b("map"));
            var3 = "map_" + var1.getData();
            var4 = new WorldMap(var3);
            var4.scale = 3;
            int var5 = 128 * (1 << var4.scale);
            var4.centerX = Math.round((float)var2.getWorldData().c() / (float)var5) * var5;
            var4.centerZ = Math.round((float)(var2.getWorldData().e() / var5)) * var5;
            var4.map = var2.worldProvider.dimension;
            var4.c();
            var2.a(var3, var4);
        }

        return var4;
    }

    public void a(World var1, Entity var2, WorldMap var3)
    {
        if (var1.worldProvider.dimension == var3.map && var2 instanceof EntityHuman)
        {
            short var4 = 128;
            short var5 = 128;
            int var6 = 1 << var3.scale;
            int var7 = var3.centerX;
            int var8 = var3.centerZ;
            int var9 = MathHelper.floor(var2.locX - (double)var7) / var6 + var4 / 2;
            int var10 = MathHelper.floor(var2.locZ - (double)var8) / var6 + var5 / 2;
            int var11 = 128 / var6;

            if (var1.worldProvider.f)
            {
                var11 /= 2;
            }

            WorldMapHumanTracker var12 = var3.func_82568_a((EntityHuman)var2);
            ++var12.field_82569_d;

            for (int var13 = var9 - var11 + 1; var13 < var9 + var11; ++var13)
            {
                if ((var13 & 15) == (var12.field_82569_d & 15))
                {
                    int var14 = 255;
                    int var15 = 0;
                    double var16 = 0.0D;

                    for (int var18 = var10 - var11 - 1; var18 < var10 + var11; ++var18)
                    {
                        if (var13 >= 0 && var18 >= -1 && var13 < var4 && var18 < var5)
                        {
                            int var19 = var13 - var9;
                            int var20 = var18 - var10;
                            boolean var21 = var19 * var19 + var20 * var20 > (var11 - 2) * (var11 - 2);
                            int var22 = (var7 / var6 + var13 - var4 / 2) * var6;
                            int var23 = (var8 / var6 + var18 - var5 / 2) * var6;
                            int[] var24 = new int[Block.byId.length];
                            Chunk var25 = var1.getChunkAtWorldCoords(var22, var23);

                            if (!var25.isEmpty())
                            {
                                int var26 = var22 & 15;
                                int var27 = var23 & 15;
                                int var28 = 0;
                                double var29 = 0.0D;
                                int var31;
                                int var34;
                                int var32;
                                int var33;

                                if (var1.worldProvider.f)
                                {
                                    var31 = var22 + var23 * 231871;
                                    var31 = var31 * var31 * 31287121 + var31 * 11;

                                    if ((var31 >> 20 & 1) == 0)
                                    {
                                        var24[Block.DIRT.id] += 10;
                                    }
                                    else
                                    {
                                        var24[Block.STONE.id] += 10;
                                    }

                                    var29 = 100.0D;
                                }
                                else
                                {
                                    for (var31 = 0; var31 < var6; ++var31)
                                    {
                                        for (var32 = 0; var32 < var6; ++var32)
                                        {
                                            var33 = var25.b(var31 + var26, var32 + var27) + 1;
                                            int var35 = 0;

                                            if (var33 > 1)
                                            {
                                                boolean var36;

                                                do
                                                {
                                                    var36 = true;
                                                    var35 = var25.getTypeId(var31 + var26, var33 - 1, var32 + var27);

                                                    if (var35 == 0)
                                                    {
                                                        var36 = false;
                                                    }
                                                    else if (var33 > 0 && var35 > 0 && Block.byId[var35].material.G == MaterialMapColor.b)
                                                    {
                                                        var36 = false;
                                                    }

                                                    if (!var36)
                                                    {
                                                        --var33;

                                                        if (var33 <= 0)
                                                        {
                                                            break;
                                                        }

                                                        var35 = var25.getTypeId(var31 + var26, var33 - 1, var32 + var27);
                                                    }
                                                }
                                                while (var33 > 0 && !var36);

                                                if (var33 > 0 && var35 != 0 && Block.byId[var35].material.isLiquid())
                                                {
                                                    var34 = var33 - 1;
                                                    boolean var37 = false;
                                                    int var38;

                                                    do
                                                    {
                                                        var38 = var25.getTypeId(var31 + var26, var34--, var32 + var27);
                                                        ++var28;
                                                    }
                                                    while (var34 > 0 && var38 != 0 && Block.byId[var38].material.isLiquid());
                                                }
                                            }

                                            var29 += (double)var33 / (double)(var6 * var6);
                                            ++var24[var35];
                                        }
                                    }
                                }

                                var28 /= var6 * var6;
                                var31 = 0;
                                var32 = 0;

                                for (var33 = 0; var33 < Block.byId.length; ++var33)
                                {
                                    if (var24[var33] > var31)
                                    {
                                        var32 = var33;
                                        var31 = var24[var33];
                                    }
                                }

                                double var40 = (var29 - var16) * 4.0D / (double)(var6 + 4) + ((double)(var13 + var18 & 1) - 0.5D) * 0.4D;
                                byte var43 = 1;

                                if (var40 > 0.6D)
                                {
                                    var43 = 2;
                                }

                                if (var40 < -0.6D)
                                {
                                    var43 = 0;
                                }

                                var34 = 0;

                                if (var32 > 0)
                                {
                                    MaterialMapColor var42 = Block.byId[var32].material.G;

                                    if (var42 == MaterialMapColor.n)
                                    {
                                        var40 = (double)var28 * 0.1D + (double)(var13 + var18 & 1) * 0.2D;
                                        var43 = 1;

                                        if (var40 < 0.5D)
                                        {
                                            var43 = 2;
                                        }

                                        if (var40 > 0.9D)
                                        {
                                            var43 = 0;
                                        }
                                    }

                                    var34 = var42.q;
                                }

                                var16 = var29;

                                if (var18 >= 0 && var19 * var19 + var20 * var20 < var11 * var11 && (!var21 || (var13 + var18 & 1) != 0))
                                {
                                    byte var41 = var3.colors[var13 + var18 * var4];
                                    byte var39 = (byte)(var34 * 4 + var43);

                                    if (var41 != var39)
                                    {
                                        if (var14 > var18)
                                        {
                                            var14 = var18;
                                        }

                                        if (var15 < var18)
                                        {
                                            var15 = var18;
                                        }

                                        var3.colors[var13 + var18 * var4] = var39;
                                    }
                                }
                            }
                        }
                    }

                    if (var14 <= var15)
                    {
                        var3.func_76194_a(var13, var14, var15);
                    }
                }
            }
        }
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void a(ItemStack var1, World var2, Entity var3, int var4, boolean var5)
    {
        if (!var2.isStatic)
        {
            WorldMap var6 = this.getSavedMap(var1, var2);

            if (var3 instanceof EntityHuman)
            {
                EntityHuman var7 = (EntityHuman)var3;
                var6.a(var7, var1);
            }

            if (var5)
            {
                this.a(var2, var3, var6);
            }
        }
    }

    public Packet c(ItemStack var1, World var2, EntityHuman var3)
    {
        byte[] var4 = this.getSavedMap(var1, var2).func_76193_a(var1, var2, var3);
        return var4 == null ? null : new Packet131ItemData((short)Item.MAP.id, (short)var1.getData(), var4);
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void d(ItemStack var1, World var2, EntityHuman var3)
    {
        if (var1.hasTag() && var1.getTag().getBoolean("map_is_scaling"))
        {
            WorldMap var4 = Item.MAP.getSavedMap(var1, var2);
            var1.setData(var2.b("map"));
            WorldMap var5 = new WorldMap("map_" + var1.getData());
            var5.scale = (byte)(var4.scale + 1);

            if (var5.scale > 4)
            {
                var5.scale = 4;
            }

            var5.centerX = var4.centerX;
            var5.centerZ = var4.centerZ;
            var5.map = var4.map;
            var5.c();
            var2.a("map_" + var1.getData(), var5);
        }
    }

    @SideOnly(Side.CLIENT)
    public void a(ItemStack var1, EntityHuman var2, List var3, boolean var4)
    {
        WorldMap var5 = this.getSavedMap(var1, var2.world);

        if (var4)
        {
            if (var5 == null)
            {
                var3.add("Unknown map");
            }
            else
            {
                var3.add("Scaling at 1:" + (1 << var5.scale));
                var3.add("(Level " + var5.scale + "/" + 4 + ")");
            }
        }
    }
}
