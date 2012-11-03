package net.minecraftforge.common;

import java.io.File;
import java.util.UUID;

import net.minecraft.server.ExceptionWorldConflict;
import net.minecraft.server.IChunkLoader;
import net.minecraft.server.IDataManager;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.PlayerFileData;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldProvider;
import net.minecraft.server.WorldServer;

public class WorldSpecificSaveHandler implements IDataManager
{
    private WorldServer world;
    private IDataManager parent;
    private File dataDir;

    public WorldSpecificSaveHandler(WorldServer var1, IDataManager var2)
    {
        this.world = var1;
        this.parent = var2;
        this.dataDir = new File(var1.getChunkSaveLocation(), "data");
        this.dataDir.mkdirs();
    }

    /**
     * Loads and returns the world info
     */
    public WorldData getWorldData()
    {
        return this.parent.getWorldData();
    }

    /**
     * Checks the session lock to prevent save collisions
     */
    public void checkSession()
    {
        try {
			this.parent.checkSession();
		} catch (ExceptionWorldConflict e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    public IChunkLoader createChunkLoader(WorldProvider var1)
    {
        return this.parent.createChunkLoader(var1);
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldData(WorldData var1, NBTTagCompound var2)
    {
        this.parent.saveWorldData(var1, var2);
    }

    /**
     * used to update level.dat from old format to MCRegion format
     */
    public void saveWorldData(WorldData var1)
    {
        this.parent.saveWorldData(var1);
    }

    public PlayerFileData getPlayerFileData()
    {
        return this.parent.getPlayerFileData();
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void a()
    {
        this.parent.a();
    }

    /**
     * Returns the name of the directory where world information is saved.
     */
    public String g()
    {
        return this.parent.g();
    }

    /**
     * Gets the file location of the given map
     */
    public File getDataFile(String var1)
    {
        System.out.println(new File(this.dataDir, var1 + ".dat"));
        return new File(this.dataDir, var1 + ".dat");
    }

	@Override
	public UUID getUUID() {
		return parent.getUUID();
	}
}
