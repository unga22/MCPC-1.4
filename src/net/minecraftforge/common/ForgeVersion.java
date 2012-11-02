package net.minecraftforge.common;

public class ForgeVersion
{
    public static final int majorVersion = 6;
    public static final int minorVersion = 0;
    public static final int revisionVersion = 1;
    public static final int buildVersion = 341;

    public static int getMajorVersion()
    {
        return 6;
    }

    public static int getMinorVersion()
    {
        return 0;
    }

    public static int getRevisionVersion()
    {
        return 1;
    }

    public static int getBuildVersion()
    {
        return 341;
    }

    public static String getVersion()
    {
        return String.format("%d.%d.%d.%d", new Object[] {Integer.valueOf(6), Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(341)});
    }
}
