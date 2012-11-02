package cpw.mods.fml.common.network;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;
import cpw.mods.fml.common.network.FMLPacket$Type;
import java.util.Arrays;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.NetHandler;

public abstract class FMLPacket
{
    private FMLPacket$Type type;

    public static byte[] makePacket(FMLPacket$Type var0, Object ... var1)
    {
        byte[] var2 = var0.make().generatePacket(var1);
        return Bytes.concat(new byte[][] {{UnsignedBytes.checkedCast((long)var0.ordinal())}, var2});
    }

    public static FMLPacket readPacket(byte[] var0)
    {
        int var1 = UnsignedBytes.toInt(var0[0]);
        return FMLPacket$Type.values()[var1].make().consumePacket(Arrays.copyOfRange(var0, 1, var0.length));
    }

    public FMLPacket(FMLPacket$Type var1)
    {
        this.type = var1;
    }

    public abstract byte[] generatePacket(Object ... var1);

    public abstract FMLPacket consumePacket(byte[] var1);

    public abstract void execute(INetworkManager var1, FMLNetworkHandler var2, NetHandler var3, String var4);
}
