package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Packet1Login extends Packet
{
    /** The player's entity ID */
    public int a = 0;
    public WorldType b;
    public boolean field_73560_c;
    public EnumGamemode d;

    /** -1: The Nether, 0: The Overworld, 1: The End */
    public int e;

    /** The difficulty setting byte. */
    public byte f;

    /** Defaults to 128 */
    public byte g;

    /** The maximum players. */
    public byte h;

    public Packet1Login() {}

    public Packet1Login(int var1, WorldType var2, EnumGamemode var3, boolean var4, int var5, int var6, int var7, int var8)
    {
        this.a = var1;
        this.b = var2;
        this.e = var5;
        this.f = (byte)var6;
        this.d = var3;
        this.g = (byte)var7;
        this.h = (byte)var8;
        this.field_73560_c = var4;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void a(DataInputStream var1)
    {
        this.a = var1.readInt();
        String var2 = a(var1, 16);
        this.b = WorldType.getType(var2);

        if (this.b == null)
        {
            this.b = WorldType.NORMAL;
        }

        byte var3 = var1.readByte();
        this.field_73560_c = (var3 & 8) == 8;
        int var4 = var3 & -9;
        this.d = EnumGamemode.a(var4);
        this.e = var1.readByte();
        this.f = var1.readByte();
        this.g = var1.readByte();
        this.h = var1.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void a(DataOutputStream var1)
    {
        var1.writeInt(this.a);
        a(this.b == null ? "" : this.b.name(), var1);
        int var2 = this.d.a();

        if (this.field_73560_c)
        {
            var2 |= 8;
        }

        var1.writeByte(var2);
        var1.writeByte(this.e);
        var1.writeByte(this.f);
        var1.writeByte(this.g);
        var1.writeByte(this.h);
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
        int var1 = 0;

        if (this.b != null)
        {
            var1 = this.b.name().length();
        }

        return 6 + 2 * var1 + 4 + 4 + 1 + 1 + 1;
    }
}
