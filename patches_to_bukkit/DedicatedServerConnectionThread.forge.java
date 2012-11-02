package net.minecraft.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DedicatedServerConnectionThread extends Thread
{
    private static Logger a = Logger.getLogger("Minecraft");
    private final List b = Collections.synchronizedList(new ArrayList());
    private final HashMap field_71776_c = new HashMap();
    private int d = 0;
    private final ServerSocket e;
    private ServerConnection f;
    private final InetAddress g;
    private final int h;

    public DedicatedServerConnectionThread(ServerConnection var1, InetAddress var2, int var3)
    {
        super("Listen thread");
        this.f = var1;
        this.g = var2;
        this.h = var3;
        this.e = new ServerSocket(var3, 0, var2);
        this.e.setPerformancePreferences(0, 2, 1);
    }

    public void a()
    {
        List var1 = this.b;

        synchronized (this.b)
        {
            for (int var2 = 0; var2 < this.b.size(); ++var2)
            {
                NetLoginHandler var3 = (NetLoginHandler)this.b.get(var2);

                try
                {
                    var3.c();
                }
                catch (Exception var6)
                {
                    var3.disconnect("Internal server error");
                    a.log(Level.WARNING, "Failed to handle packet: " + var6, var6);
                }

                if (var3.c)
                {
                    this.b.remove(var2--);
                }

                var3.networkManager.a();
            }
        }
    }

    public void run()
    {
        while (this.f.b)
        {
            try
            {
                Socket var1 = this.e.accept();
                InetAddress var2 = var1.getInetAddress();
                long var3 = System.currentTimeMillis();
                HashMap var5 = this.field_71776_c;

                synchronized (this.field_71776_c)
                {
                    if (this.field_71776_c.containsKey(var2) && !b(var2) && var3 - ((Long)this.field_71776_c.get(var2)).longValue() < 4000L)
                    {
                        this.field_71776_c.put(var2, Long.valueOf(var3));
                        var1.close();
                        continue;
                    }

                    this.field_71776_c.put(var2, Long.valueOf(var3));
                }

                NetLoginHandler var9 = new NetLoginHandler(this.f.d(), var1, "Connection #" + this.d++);
                this.a(var9);
            }
            catch (IOException var8)
            {
                var8.printStackTrace();
            }
        }

        System.out.println("Closing listening thread");
    }

    private void a(NetLoginHandler var1)
    {
        if (var1 == null)
        {
            throw new IllegalArgumentException("Got null pendingconnection!");
        }
        else
        {
            List var2 = this.b;

            synchronized (this.b)
            {
                this.b.add(var1);
            }
        }
    }

    private static boolean b(InetAddress var0)
    {
        return "127.0.0.1".equals(var0.getHostAddress());
    }

    public void func_71769_a(InetAddress var1)
    {
        if (var1 != null)
        {
            HashMap var2 = this.field_71776_c;

            synchronized (this.field_71776_c)
            {
                this.field_71776_c.remove(var1);
            }
        }
    }

    public void func_71768_b()
    {
        try
        {
            this.e.close();
        }
        catch (Throwable var2)
        {
            ;
        }
    }
}
