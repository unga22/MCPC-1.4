package net.minecraft.server;

import java.io.File;
import java.util.List;

public class ServerNBTManager extends WorldNBTStorage
{
  public ServerNBTManager(File file1, String s, boolean flag)
  {
    super(file1, s, flag);
  }

  public IChunkLoader createChunkLoader(WorldProvider worldprovider) {
    File file1 = getDirectory();

    if (worldprovider.getSaveFolder() != null)
    {
      File file2 = new File(file1, worldprovider.getSaveFolder());
      file2.mkdirs();
      return new ChunkRegionLoader(file2);
    }

    return new ChunkRegionLoader(file1);
  }

  public void saveWorldData(WorldData worlddata, List list)
  {
    worlddata.a(19133);
    super.saveWorldData(worlddata, list);
  }

  public void e() {
    try {
      FileIOThread.a.a();
    }
    catch (InterruptedException interruptedexception) {
      interruptedexception.printStackTrace();
    }

    RegionFileCache.a();
  }
}

