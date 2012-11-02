package net.minecraftforge.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraftforge.event.ListenerList$1;

class ListenerList$ListenerListInst
{
    private boolean rebuild;
    private IEventListener[] listeners;
    private ArrayList priorities;
    private ListenerList$ListenerListInst parent;

    final ListenerList this$0;

    private ListenerList$ListenerListInst(ListenerList var1)
    {
        this.this$0 = var1;
        this.rebuild = true;
        int var2 = EventPriority.values().length;
        this.priorities = new ArrayList(var2);

        for (int var3 = 0; var3 < var2; ++var3)
        {
            this.priorities.add(new ArrayList());
        }
    }

    public void dispose()
    {
        Iterator var1 = this.priorities.iterator();

        while (var1.hasNext())
        {
            ArrayList var2 = (ArrayList)var1.next();
            var2.clear();
        }

        this.priorities.clear();
        this.parent = null;
        this.listeners = null;
    }

    private ListenerList$ListenerListInst(ListenerList var1, ListenerList$ListenerListInst var2)
    {
        this(var1);
        this.parent = var2;
    }

    public ArrayList getListeners(EventPriority var1)
    {
        ArrayList var2 = new ArrayList((Collection)this.priorities.get(var1.ordinal()));

        if (this.parent != null)
        {
            var2.addAll(this.parent.getListeners(var1));
        }

        return var2;
    }

    public IEventListener[] getListeners()
    {
        if (this.shouldRebuild())
        {
            this.buildCache();
        }

        return this.listeners;
    }

    protected boolean shouldRebuild()
    {
        return this.rebuild || this.parent != null && this.parent.shouldRebuild();
    }

    private void buildCache()
    {
        ArrayList var1 = new ArrayList();
        EventPriority[] var2 = EventPriority.values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            EventPriority var5 = var2[var4];
            var1.addAll(this.getListeners(var5));
        }

        this.listeners = (IEventListener[])var1.toArray(new IEventListener[0]);
        this.rebuild = false;
    }

    public void register(EventPriority var1, IEventListener var2)
    {
        ((ArrayList)this.priorities.get(var1.ordinal())).add(var2);
        this.rebuild = true;
    }

    public void unregister(IEventListener var1)
    {
        Iterator var2 = this.priorities.iterator();

        while (var2.hasNext())
        {
            ArrayList var3 = (ArrayList)var2.next();
            var3.remove(var1);
        }
    }

    ListenerList$ListenerListInst(ListenerList var1, ListenerList$ListenerListInst var2, ListenerList$1 var3)
    {
        this(var1, var2);
    }

    ListenerList$ListenerListInst(ListenerList var1, ListenerList$1 var2)
    {
        this(var1);
    }
}
