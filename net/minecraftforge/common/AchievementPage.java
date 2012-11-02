package net.minecraftforge.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.server.Achievement;

public class AchievementPage
{
    private String name;
    private LinkedList achievements;
    private static LinkedList achievementPages = new LinkedList();

    public AchievementPage(String var1, Achievement ... var2)
    {
        this.name = var1;
        this.achievements = new LinkedList(Arrays.asList(var2));
    }

    public String getName()
    {
        return this.name;
    }

    public List getAchievements()
    {
        return this.achievements;
    }

    public static void registerAchievementPage(AchievementPage var0)
    {
        if (getAchievementPage(var0.getName()) != null)
        {
            throw new RuntimeException("Duplicate achievement page name \"" + var0.getName() + "\"!");
        }
        else
        {
            achievementPages.add(var0);
        }
    }

    public static AchievementPage getAchievementPage(int var0)
    {
        return (AchievementPage)achievementPages.get(var0);
    }

    public static AchievementPage getAchievementPage(String var0)
    {
        Iterator var1 = achievementPages.iterator();
        AchievementPage var2;

        do
        {
            if (!var1.hasNext())
            {
                return null;
            }

            var2 = (AchievementPage)var1.next();
        }
        while (!var2.getName().equals(var0));

        return var2;
    }

    public static Set getAchievementPages()
    {
        return new HashSet(achievementPages);
    }

    public static boolean isAchievementInPages(Achievement var0)
    {
        Iterator var1 = achievementPages.iterator();
        AchievementPage var2;

        do
        {
            if (!var1.hasNext())
            {
                return false;
            }

            var2 = (AchievementPage)var1.next();
        }
        while (!var2.getAchievements().contains(var0));

        return true;
    }

    public static String getTitle(int var0)
    {
        return var0 == -1 ? "Minecraft" : getAchievementPage(var0).getName();
    }
}
