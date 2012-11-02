package net.minecraft.server;

import cpw.mods.fml.common.FMLCommonHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class WorldNBTStorage implements IDataManager, PlayerFileData
{
    /** Reference to the logger. */
    private static final Logger log = Logger.getLogger("Minecraft");

    /** The directory in which to save world data. */
    private final File baseDir;

    /** The directory in which to save player data. */
    private final File playerDir;
    private final File dataDir;

    /**
     * The time in milliseconds when this field was initialized. Stored in the session lock file.
     */
    private final long sessionId = System.currentTimeMillis();

    /** The directory name of the world */
    private final String f;

    public WorldNBTStorage(File var1, String var2, boolean var3)
    {
        this.baseDir = new File(var1, var2);
        this.baseDir.mkdirs();
        this.playerDir = new File(this.baseDir, "players");
        this.dataDir = new File(this.baseDir, "data");
        this.dataDir.mkdirs();
        this.f = var2;

        if (var3)
        {
            this.playerDir.mkdirs();
        }

        this.h();
    }

    /**
     * Creates a session lock file for this process
     */
    private void h()
    {
        try
        {
            File var1 = new File(this.baseDir, "session.lock");
            DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

            try
            {
                var2.writeLong(this.sessionId);
            }
            finally
            {
                var2.close();
            }
        }
        catch (IOException var7)
        {
            var7.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    /**
     * Gets the File object corresponding to the base directory of this world.
     */
    protected File getDirectory()
    {
        return this.baseDir;
    }

    /**
     * Checks the session lock to prevent save collisions
     */
    public void checkSession() throws ExceptionWorldConflict
    {
        try
        {
            File var1 = new File(this.baseDir, "session.lock");
            DataInputStream var2 = new DataInputStream(new FileInputStream(var1));

            try
            {
                if (var2.readLong() != this.sessionId)
                {
                    throw new ExceptionWorldConflict("The save is being accessed from another location, aborting");
                }
            }
            finally
            {
                var2.close();
            }
        }
        catch (IOException var7)
        {
            throw new ExceptionWorldConflict("Failed to check session lock, aborting");
        }
    }

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    public IChunkLoader createChunkLoader(WorldProvider var1)
    {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    /**
     * Loads and returns the world info
     */
    public WorldData getWorldData()
    {
        File var1 = new File(this.baseDir, "level.dat");
        WorldData var4 = null;
        NBTTagCompound var2;
        NBTTagCompound var3;

        if (var1.exists())
        {
            try
            {
                var2 = NBTCompressedStreamTools.a(new FileInputStream(var1));
                var3 = var2.getCompound("Data");
                var4 = new WorldData(var3);
                FMLCommonHandler.instance().handleWorldDataLoad(this, var4, var2);
                return var4;
            }
            catch (Exception var7)
            {
                var7.printStackTrace();
            }
        }

        var1 = new File(this.baseDir, "level.dat_old");

        if (var1.exists())
        {
            try
            {
                var2 = NBTCompressedStreamTools.a(new FileInputStream(var1));
                var3 = var2.getCompound("Data");
                var4 = new WorldData(var3);
                FMLCommonHandler.instance().handleWorldDataLoad(this, var4, var2);
                return var4;
            }
            catch (Exception var6)
            {
                var6.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldData(WorldData var1, NBTTagCompound var2)
    {
        NBTTagCompound var3 = var1.a(var2);
        NBTTagCompound var4 = new NBTTagCompound();
        var4.set("Data", var3);
        FMLCommonHandler.instance().handleWorldDataSave(this, var1, var4);

        try
        {
            File var5 = new File(this.baseDir, "level.dat_new");
            File var6 = new File(this.baseDir, "level.dat_old");
            File var7 = new File(this.baseDir, "level.dat");
            NBTCompressedStreamTools.a(var4, new FileOutputStream(var5));

            if (var6.exists())
            {
                var6.delete();
            }

            var7.renameTo(var6);

            if (var7.exists())
            {
                var7.delete();
            }

            var5.renameTo(var7);

            if (var5.exists())
            {
                var5.delete();
            }
        }
        catch (Exception var8)
        {
            var8.printStackTrace();
        }
    }

    /**
     * used to update level.dat from old format to MCRegion format
     */
    public void saveWorldData(WorldData var1)
    {
        NBTTagCompound var2 = var1.a();
        NBTTagCompound var3 = new NBTTagCompound();
        var3.set("Data", var2);
        FMLCommonHandler.instance().handleWorldDataSave(this, var1, var3);

        try
        {
            File var4 = new File(this.baseDir, "level.dat_new");
            File var5 = new File(this.baseDir, "level.dat_old");
            File var6 = new File(this.baseDir, "level.dat");
            NBTCompressedStreamTools.a(var3, new FileOutputStream(var4));

            if (var5.exists())
            {
                var5.delete();
            }

            var6.renameTo(var5);

            if (var6.exists())
            {
                var6.delete();
            }

            var4.renameTo(var6);

            if (var4.exists())
            {
                var4.delete();
            }
        }
        catch (Exception var7)
        {
            var7.printStackTrace();
        }
    }

    /**
     * Writes the player data to disk from the specified PlayerEntityMP.
     */
    public void save(EntityHuman var1)
    {
        try
        {
            NBTTagCompound var2 = new NBTTagCompound();
            var1.d(var2);
            File var3 = new File(this.playerDir, var1.name + ".dat.tmp");
            File var4 = new File(this.playerDir, var1.name + ".dat");
            NBTCompressedStreamTools.a(var2, new FileOutputStream(var3));

            if (var4.exists())
            {
                var4.delete();
            }

            var3.renameTo(var4);
        }
        catch (Exception var5)
        {
            log.warning("Failed to save player data for " + var1.name);
        }
    }

    /**
     * Reads the player data from disk into the specified PlayerEntityMP.
     */
    public void load(EntityHuman var1)
    {
        NBTTagCompound var2 = this.getPlayerData(var1.name);

        if (var2 != null)
        {
            var1.e(var2);
        }
    }

    /**
     * Gets the player data for the given playername as a NBTTagCompound.
     */
    public NBTTagCompound getPlayerData(String var1)
    {
        try
        {
            File var2 = new File(this.playerDir, var1 + ".dat");

            if (var2.exists())
            {
                return NBTCompressedStreamTools.a(new FileInputStream(var2));
            }
        }
        catch (Exception var3)
        {
            log.warning("Failed to load player data for " + var1);
        }

        return null;
    }

    public PlayerFileData getPlayerFileData()
    {
        return this;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getSeenPlayers()
    {
        String[] var1 = this.playerDir.list();

        for (int var2 = 0; var2 < var1.length; ++var2)
        {
            if (var1[var2].endsWith(".dat"))
            {
                var1[var2] = var1[var2].substring(0, var1[var2].length() - 4);
            }
        }

        return var1;
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void a() {}

    /**
     * Gets the file location of the given map
     */
    public File getDataFile(String var1)
    {
        return new File(this.dataDir, var1 + ".dat");
    }

    /**
     * Returns the name of the directory where world information is saved.
     */
    public String g()
    {
        return this.f;
    }
}
