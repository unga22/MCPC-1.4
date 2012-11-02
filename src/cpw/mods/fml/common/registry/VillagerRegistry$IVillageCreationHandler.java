package cpw.mods.fml.common.registry;

import java.util.List;
import java.util.Random;
import net.minecraft.server.WorldGenVillagePieceWeight;
import net.minecraft.server.WorldGenVillageStartPiece;

public interface VillagerRegistry$IVillageCreationHandler
{
    WorldGenVillagePieceWeight getVillagePieceWeight(Random var1, int var2);

    Class getComponentClass();

    Object buildComponent(WorldGenVillagePieceWeight var1, WorldGenVillageStartPiece var2, List var3, Random var4, int var5, int var6, int var7, int var8, int var9);
}
