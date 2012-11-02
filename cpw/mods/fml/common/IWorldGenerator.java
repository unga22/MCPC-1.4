package cpw.mods.fml.common;

import java.util.Random;
import net.minecraft.server.IChunkProvider;
import net.minecraft.server.World;

public interface IWorldGenerator
{
    void generate(Random var1, int var2, int var3, World var4, IChunkProvider var5, IChunkProvider var6);
}
