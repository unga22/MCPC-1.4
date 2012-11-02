package net.minecraftforge.event;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraftforge.event.ListenerList$1;
import net.minecraftforge.event.ListenerList$ListenerListInst;

public class ListenerList
{
    private static ArrayList allLists = new ArrayList();
    private static int maxSize = 0;
    private ListenerList parent;
    private ListenerList$ListenerListInst[] lists = new ListenerList$ListenerListInst[0];

    public ListenerList()
    {
        allLists.add(this);
        this.resizeLists(maxSize);
    }

    public ListenerList(ListenerList var1)
    {
        allLists.add(this);
        this.parent = var1;
        this.resizeLists(maxSize);
    }

    public static void resize(int var0)
    {
        if (var0 > maxSize)
        {
            Iterator var1 = allLists.iterator();

            while (var1.hasNext())
            {
                ListenerList var2 = (ListenerList)var1.next();
                var2.resizeLists(var0);
            }

            maxSize = var0;
        }
    }

    public void resizeLists(int var1)
    {
        if (this.parent != null)
        {
            this.parent.resizeLists(var1);
        }

        if (this.lists.length < var1)
        {
            ListenerList$ListenerListInst[] var2 = new ListenerList$ListenerListInst[var1];
            int var3;

            for (var3 = 0; var3 < this.lists.length; ++var3)
            {
                var2[var3] = this.lists[var3];
            }

            for (; var3 < var1; ++var3)
            {
                if (this.parent != null)
                {
                    var2[var3] = new ListenerList$ListenerListInst(this, this.parent.getInstance(var3), (ListenerList$1)null);
                }
                else
                {
                    var2[var3] = new ListenerList$ListenerListInst(this, (ListenerList$1)null);
                }
            }

            this.lists = var2;
        }
    }

    public static void clearBusID(int var0)
    {
        Iterator var1 = allLists.iterator();

        while (var1.hasNext())
        {
            ListenerList var2 = (ListenerList)var1.next();
            var2.lists[var0].dispose();
        }
    }

    protected ListenerList$ListenerListInst getInstance(int var1)
    {
        return this.lists[var1];
    }

    public IEventListener[] getListeners(int var1)
    {
        return this.lists[var1].getListeners();
    }

    public void register(int var1, EventPriority var2, IEventListener var3)
    {
        this.lists[var1].register(var2, var3);
    }

    public void unregister(int var1, IEventListener var2)
    {
        this.lists[var1].unregister(var2);
    }

    public static void unregiterAll(int var0, IEventListener var1)
    {
        Iterator var2 = allLists.iterator();

        while (var2.hasNext())
        {
            ListenerList var3 = (ListenerList)var2.next();
            var3.unregister(var0, var1);
        }
    }
}
