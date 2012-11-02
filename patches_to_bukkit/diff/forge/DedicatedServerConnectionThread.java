package net.minecraft.server;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
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

    public DedicatedServerConnectionThread(ServerConnection var1, InetAddress var2, int var3) throws IOException
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
        List var2 = this.b;

        synchronized (this.b)
        {
            for (int var3 = 0; var3 < this.b.size(); ++var3)
            {
                NetLoginHandler var4 = (NetLoginHandler)this.b.get(var3);

                try
                {
                    var4.c();
                }
                catch (Exception var7)
                {
                    var4.disconnect("Internal server error");
                    FMLLog.log(Level.SEVERE, var7, "Error handling login related packet - connection from %s refused", new Object[] {var4.h});
                    a.log(Level.WARNING, "Failed to handle packet: " + var7, var7);
                }

                if (var4.c)
                {
                    this.b.remove(var3--);
                }

                var4.networkManager.a();
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
                HashMap var6 = this.field_71776_c;

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

                NetLoginHandler var10 = new NetLoginHandler(this.f.d(), var1, "Connection #" + this.d++);
                this.a(var10);
            }
            catch (IOException var9)
            {
                var9.printStackTrace();
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
            List var3 = this.b;

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
            HashMap var3 = this.field_71776_c;

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

    @SideOnly(Side.CLIENT)
    public InetAddress c()
    {
        return this.g;
    }

    @SideOnly(Side.CLIENT)
    public int d()
    {
        return this.h;
    }
}
