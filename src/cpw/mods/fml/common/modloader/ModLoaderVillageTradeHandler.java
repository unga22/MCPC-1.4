package cpw.mods.fml.common.modloader;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry$IVillageTradeHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.Item;
import net.minecraft.server.MerchantRecipeList;
import net.minecraft.server.TradeEntry;

public class ModLoaderVillageTradeHandler implements VillagerRegistry$IVillageTradeHandler
{
    private List trades = Lists.newArrayList();

    public void manipulateTradesForVillager(EntityVillager var1, MerchantRecipeList var2, Random var3)
    {
        Iterator var4 = this.trades.iterator();

        while (var4.hasNext())
        {
            TradeEntry var5 = (TradeEntry)var4.next();

            if (var5.buying)
            {
                VillagerRegistry.addEmeraldBuyRecipe(var1, var2, var3, Item.byId[var5.id], var5.chance, var5.min, var5.max);
            }
            else
            {
                VillagerRegistry.addEmeraldSellRecipe(var1, var2, var3, Item.byId[var5.id], var5.chance, var5.min, var5.max);
            }
        }
    }

    public void addTrade(TradeEntry var1)
    {
        this.trades.add(var1);
    }
}
