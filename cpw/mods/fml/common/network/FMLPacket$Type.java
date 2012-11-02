package cpw.mods.fml.common.network;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;

enum FMLPacket$Type
{
    MOD_LIST_REQUEST(ModListRequestPacket.class),
    MOD_LIST_RESPONSE(ModListResponsePacket.class),
    MOD_IDENTIFIERS(ModIdentifiersPacket.class),
    MOD_MISSING(ModMissingPacket.class),
    GUIOPEN(OpenGuiPacket.class),
    ENTITYSPAWN(EntitySpawnPacket.class),
    ENTITYSPAWNADJUSTMENT(EntitySpawnAdjustmentPacket.class);
    private Class packetType;

    private FMLPacket$Type(Class var3)
    {
        this.packetType = var3;
    }

    FMLPacket make()
    {
        try
        {
            return (FMLPacket)this.packetType.newInstance();
        }
        catch (Exception var2)
        {
            Throwables.propagateIfPossible(var2);
            FMLLog.log(Level.SEVERE, var2, "A bizarre critical error occured during packet encoding", new Object[0]);
            throw new FMLNetworkException(var2);
        }
    }
}
