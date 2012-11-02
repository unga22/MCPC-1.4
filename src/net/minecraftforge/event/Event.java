package net.minecraftforge.event;

import net.minecraftforge.event.Event$HasResult;
import net.minecraftforge.event.Event$Result;

public class Event
{
    private boolean isCanceled = false;
    private final boolean isCancelable;
    private Event$Result result;
    private final boolean hasResult;
    private static ListenerList listeners = new ListenerList();

    public Event()
    {
        this.result = Event$Result.DEFAULT;
        this.setup();
        this.isCancelable = this.hasAnnotation(Cancelable.class);
        this.hasResult = this.hasAnnotation(Event$HasResult.class);
    }

    private boolean hasAnnotation(Class var1)
    {
        for (Class var2 = this.getClass(); var2 != Event.class; var2 = var2.getSuperclass())
        {
            if (var2.isAnnotationPresent(Cancelable.class))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isCancelable()
    {
        return this.isCancelable;
    }

    public boolean isCanceled()
    {
        return this.isCanceled;
    }

    public void setCanceled(boolean var1)
    {
        if (!this.isCancelable())
        {
            throw new IllegalArgumentException("Attempted to cancel a uncancelable event");
        }
        else
        {
            this.isCanceled = var1;
        }
    }

    public boolean hasResult()
    {
        return this.hasResult;
    }

    public Event$Result getResult()
    {
        return this.result;
    }

    public void setResult(Event$Result var1)
    {
        this.result = var1;
    }

    protected void setup() {}

    public ListenerList getListenerList()
    {
        return listeners;
    }
}
