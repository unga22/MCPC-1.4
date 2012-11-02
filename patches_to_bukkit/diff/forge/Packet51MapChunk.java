package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet
{
    /** The x-position of the transmitted chunk, in chunk coordinates. */
    public int a;

    /** The z-position of the transmitted chunk, in chunk coordinates. */
    public int b;

    /**
     * The y-position of the lowest chunk Section in the transmitted chunk, in chunk coordinates.
     */
    public int c;

    /**
     * The y-position of the highest chunk Section in the transmitted chunk, in chunk coordinates.
     */
    public int d;

    /** The transmitted chunk data, decompressed. */
    private byte[] buffer;
    private byte[] field_73596_g;

    /**
     * Whether to initialize the Chunk before applying the effect of the Packet51MapChunk.
     */
    public boolean e;

    /** The length of the compressed chunk data byte array. */
    private int size;

    /** A temporary storage for the compressed chunk data byte array. */
    private static byte[] buildBuffer = new byte[196864];

    public Packet51MapChunk()
    {
        this.lowPriority = true;
    }

    public Packet51MapChunk(Chunk var1, boolean var2, int var3)
    {
        this.lowPriority = true;
        this.a = var1.x;
        this.b = var1.z;
        this.e = var2;
        ChunkMap var4 = a(var1, var2, var3);
        Deflater var5 = new Deflater(-1);
        this.d = var4.field_74581_c;
        this.c = var4.field_74580_b;

        try
        {
            this.field_73596_g = var4.field_74582_a;
            var5.setInput(var4.field_74582_a, 0, var4.field_74582_a.length);
            var5.finish();
            this.buffer = new byte[var4.field_74582_a.length];
            this.size = var5.deflate(this.buffer);
        }
        finally
        {
            var5.end();
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void a(DataInputStream var1) throws IOException
    {
        this.a = var1.readInt();
        this.b = var1.readInt();
        this.e = var1.readBoolean();
        this.c = var1.readShort();
        this.d = var1.readShort();
        this.size = var1.readInt();

        if (buildBuffer.length < this.size)
        {
            buildBuffer = new byte[this.size];
        }

        var1.readFully(buildBuffer, 0, this.size);
        int var2 = 0;
        int var3;

        for (var3 = 0; var3 < 16; ++var3)
        {
            var2 += this.c >> var3 & 1;
        }

        var3 = 12288 * var2;
        int var4 = 0;

        for (int var5 = 0; var5 < 16; ++var5)
        {
            var4 += this.d >> var5 & 1;
        }

        var3 += 2048 * var4;

        if (this.e)
        {
            var3 += 256;
        }

        this.field_73596_g = new byte[var3];
        Inflater var12 = new Inflater();
        var12.setInput(buildBuffer, 0, this.size);

        try
        {
            var12.inflate(this.field_73596_g);
        }
        catch (DataFormatException var10)
        {
            throw new IOException("Bad compressed data format");
        }
        finally
        {
            var12.end();
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void a(DataOutputStream var1) throws IOException
    {
        var1.writeInt(this.a);
        var1.writeInt(this.b);
        var1.writeBoolean(this.e);
        var1.writeShort((short)(this.c & 65535));
        var1.writeShort((short)(this.d & 65535));
        var1.writeInt(this.size);
        var1.write(this.buffer, 0, this.size);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void handle(NetHandler var1)
    {
        var1.a(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int a()
    {
        return 17 + this.size;
    }

    public static ChunkMap a(Chunk var0, boolean var1, int var2)
    {
        int var3 = 0;
        ChunkSection[] var4 = var0.i();
        int var5 = 0;
        ChunkMap var6 = new ChunkMap();
        byte[] var7 = buildBuffer;

        if (var1)
        {
            var0.seenByPlayer = true;
        }

        int var8;

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!var1 || !var4[var8].a()) && (var2 & 1 << var8) != 0)
            {
                var6.field_74580_b |= 1 << var8;

                if (var4[var8].i() != null)
                {
                    var6.field_74581_c |= 1 << var8;
                    ++var5;
                }
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!var1 || !var4[var8].a()) && (var2 & 1 << var8) != 0)
            {
                byte[] var9 = var4[var8].g();
                System.arraycopy(var9, 0, var7, var3, var9.length);
                var3 += var9.length;
            }
        }

        NibbleArray var11;

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!var1 || !var4[var8].a()) && (var2 & 1 << var8) != 0)
            {
                var11 = var4[var8].j();
                System.arraycopy(var11.a, 0, var7, var3, var11.a.length);
                var3 += var11.a.length;
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!var1 || !var4[var8].a()) && (var2 & 1 << var8) != 0)
            {
                var11 = var4[var8].k();
                System.arraycopy(var11.a, 0, var7, var3, var11.a.length);
                var3 += var11.a.length;
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!var1 || !var4[var8].a()) && (var2 & 1 << var8) != 0)
            {
                var11 = var4[var8].l();
                System.arraycopy(var11.a, 0, var7, var3, var11.a.length);
                var3 += var11.a.length;
            }
        }

        if (var5 > 0)
        {
            for (var8 = 0; var8 < var4.length; ++var8)
            {
                if (var4[var8] != null && (!var1 || !var4[var8].a()) && var4[var8].i() != null && (var2 & 1 << var8) != 0)
                {
                    var11 = var4[var8].i();
                    System.arraycopy(var11.a, 0, var7, var3, var11.a.length);
                    var3 += var11.a.length;
                }
            }
        }

        if (var1)
        {
            byte[] var10 = var0.m();
            System.arraycopy(var10, 0, var7, var3, var10.length);
            var3 += var10.length;
        }

        var6.field_74582_a = new byte[var3];
        System.arraycopy(var7, 0, var6.field_74582_a, 0, var3);
        return var6;
    }

    @SideOnly(Side.CLIENT)
    public byte[] d()
    {
        return this.field_73596_g;
    }
}
