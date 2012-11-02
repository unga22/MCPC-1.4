package cpw.mods.fml.common.registry;

import java.util.Random;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.MerchantRecipeList;

public interface VillagerRegistry$IVillageTradeHandler
{
    void manipulateTradesForVillager(EntityVillager var1, MerchantRecipeList var2, Random var3);
}
