package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet56MapChunkBulk extends Packet
{
    private int[] field_73589_c;
    private int[] field_73586_d;
    public int[] field_73590_a;
    public int[] field_73588_b;
    private byte[] field_73587_e;
    private byte[][] field_73584_f;
    private int field_73585_g;
    private static byte[] field_73591_h = new byte[0];

    public Packet56MapChunkBulk() {}

    public Packet56MapChunkBulk(List var1)
    {
        int var2 = var1.size();
        this.field_73589_c = new int[var2];
        this.field_73586_d = new int[var2];
        this.field_73590_a = new int[var2];
        this.field_73588_b = new int[var2];
        this.field_73584_f = new byte[var2][];
        int var3 = 0;

        for (int var4 = 0; var4 < var2; ++var4)
        {
            Chunk var5 = (Chunk)var1.get(var4);
            ChunkMap var6 = Packet51MapChunk.a(var5, true, 65535);

            if (field_73591_h.length < var3 + var6.field_74582_a.length)
            {
                byte[] var7 = new byte[var3 + var6.field_74582_a.length];
                System.arraycopy(field_73591_h, 0, var7, 0, field_73591_h.length);
                field_73591_h = var7;
            }

            System.arraycopy(var6.field_74582_a, 0, field_73591_h, var3, var6.field_74582_a.length);
            var3 += var6.field_74582_a.length;
            this.field_73589_c[var4] = var5.x;
            this.field_73586_d[var4] = var5.z;
            this.field_73590_a[var4] = var6.field_74580_b;
            this.field_73588_b[var4] = var6.field_74581_c;
            this.field_73584_f[var4] = var6.field_74582_a;
        }

        Deflater var11 = new Deflater(-1);

        try
        {
            var11.setInput(field_73591_h, 0, var3);
            var11.finish();
            this.field_73587_e = new byte[var3];
            this.field_73585_g = var11.deflate(this.field_73587_e);
        }
        finally
        {
            var11.end();
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void a(DataInputStream var1) throws IOException
    {
        short var2 = var1.readShort();
        this.field_73585_g = var1.readInt();
        this.field_73589_c = new int[var2];
        this.field_73586_d = new int[var2];
        this.field_73590_a = new int[var2];
        this.field_73588_b = new int[var2];
        this.field_73584_f = new byte[var2][];

        if (field_73591_h.length < this.field_73585_g)
        {
            field_73591_h = new byte[this.field_73585_g];
        }

        var1.readFully(field_73591_h, 0, this.field_73585_g);
        byte[] var3 = new byte[196864 * var2];
        Inflater var4 = new Inflater();
        var4.setInput(field_73591_h, 0, this.field_73585_g);

        try
        {
            var4.inflate(var3);
        }
        catch (DataFormatException var13)
        {
            throw new IOException("Bad compressed data format");
        }
        finally
        {
            var4.end();
        }

        int var5 = 0;

        for (int var6 = 0; var6 < var2; ++var6)
        {
            this.field_73589_c[var6] = var1.readInt();
            this.field_73586_d[var6] = var1.readInt();
            this.field_73590_a[var6] = var1.readShort();
            this.field_73588_b[var6] = var1.readShort();
            int var7 = 0;
            int var8;

            for (var8 = 0; var8 < 16; ++var8)
            {
                var7 += this.field_73590_a[var6] >> var8 & 1;
            }

            int var9 = 0;

            for (int var10 = 0; var10 < 16; ++var10)
            {
                var9 += this.field_73588_b[var6] >> var10 & 1;
            }

            var8 = 10240 * var7 + 2048 * var9 + 256;
            this.field_73584_f[var6] = new byte[var8];
            System.arraycopy(var3, var5, this.field_73584_f[var6], 0, var8);
            var5 += var8;
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void a(DataOutputStream var1) throws IOException
    {
        var1.writeShort(this.field_73589_c.length);
        var1.writeInt(this.field_73585_g);
        var1.write(this.field_73587_e, 0, this.field_73585_g);

        for (int var2 = 0; var2 < this.field_73589_c.length; ++var2)
        {
            var1.writeInt(this.field_73589_c[var2]);
            var1.writeInt(this.field_73586_d[var2]);
            var1.writeShort((short)(this.field_73590_a[var2] & 65535));
            var1.writeShort((short)(this.field_73588_b[var2] & 65535));
        }
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
        return 6 + this.field_73585_g + 12 * this.func_73581_d();
    }

    @SideOnly(Side.CLIENT)
    public int a(int var1)
    {
        return this.field_73589_c[var1];
    }

    @SideOnly(Side.CLIENT)
    public int b(int var1)
    {
        return this.field_73586_d[var1];
    }

    public int func_73581_d()
    {
        return this.field_73589_c.length;
    }

    @SideOnly(Side.CLIENT)
    public byte[] c(int var1)
    {
        return this.field_73584_f[var1];
    }
}
