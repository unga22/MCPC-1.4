package net.minecraftforge.event;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus
{
    private static int maxID = 0;
    private ConcurrentHashMap listeners = new ConcurrentHashMap();
    private final int busID;

    public EventBus()
    {
        this.busID = maxID++;
        ListenerList.resize(this.busID + 1);
    }

    public void register(Object var1)
    {
        Set var2 = TypeToken.of(var1.getClass()).getTypes().rawTypes();
        Method[] var3 = var1.getClass().getMethods();
        int var4 = var3.length;
        int var5 = 0;

        while (var5 < var4)
        {
            Method var6 = var3[var5];
            Iterator var7 = var2.iterator();

            while (true)
            {
                if (var7.hasNext())
                {
                    Class var8 = (Class)var7.next();

                    try
                    {
                        Method var9 = var8.getDeclaredMethod(var6.getName(), var6.getParameterTypes());

                        if (!var9.isAnnotationPresent(ForgeSubscribe.class))
                        {
                            continue;
                        }

                        Class[] var10 = var6.getParameterTypes();

                        if (var10.length != 1)
                        {
                            throw new IllegalArgumentException("Method " + var6 + " has @ForgeSubscribe annotation, but requires " + var10.length + " arguments.  Event handler methods must require a single argument.");
                        }

                        Class var11 = var10[0];

                        if (!Event.class.isAssignableFrom(var11))
                        {
                            throw new IllegalArgumentException("Method " + var6 + " has @ForgeSubscribe annotation, but takes a argument that is not a Event " + var11);
                        }

                        this.register(var11, var1, var6);
                    }
                    catch (NoSuchMethodException var12)
                    {
                        continue;
                    }
                }

                ++var5;
                break;
            }
        }
    }

    private void register(Class var1, Object var2, Method var3)
    {
        try
        {
            Constructor var4 = var1.getConstructor(new Class[0]);
            var4.setAccessible(true);
            Event var5 = (Event)var4.newInstance(new Object[0]);
            ASMEventHandler var6 = new ASMEventHandler(var2, var3);
            var5.getListenerList().register(this.busID, var6.getPriority(), var6);
            ArrayList var7 = (ArrayList)this.listeners.get(var2);

            if (var7 == null)
            {
                var7 = new ArrayList();
                this.listeners.put(var2, var7);
            }

            var7.add(var6);
        }
        catch (Exception var8)
        {
            var8.printStackTrace();
        }
    }

    public void unregister(Object var1)
    {
        ArrayList var2 = (ArrayList)this.listeners.remove(var1);
        Iterator var3 = var2.iterator();

        while (var3.hasNext())
        {
            IEventListener var4 = (IEventListener)var3.next();
            ListenerList.unregiterAll(this.busID, var4);
        }
    }

    public boolean post(Event var1)
    {
        IEventListener[] var2 = var1.getListenerList().getListeners(this.busID);
        IEventListener[] var3 = var2;
        int var4 = var2.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            IEventListener var6 = var3[var5];
            var6.invoke(var1);
        }

        return var1.isCancelable() ? var1.isCanceled() : false;
    }
}
