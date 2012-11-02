package net.minecraft.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ServerConnection
{
    /** Reference to the logger. */
    public static Logger a = Logger.getLogger("Minecraft");

    /** Reference to the MinecraftServer object. */
    private final MinecraftServer c;
    private final List d = Collections.synchronizedList(new ArrayList());

    /** Whether the network listener object is listening. */
    public volatile boolean b = false;

    public ServerConnection(MinecraftServer var1)
    {
        this.c = var1;
        this.b = true;
    }

    /**
     * adds this connection to the list of currently connected players
     */
    public void a(NetServerHandler var1)
    {
        this.d.add(var1);
    }

    public void a()
    {
        this.b = false;
    }

    /**
     * Handles all incoming connections and packets
     */
    public void b()
    {
        for (int var1 = 0; var1 < this.d.size(); ++var1)
        {
            NetServerHandler var2 = (NetServerHandler)this.d.get(var1);

            try
            {
                var2.d();
            }
            catch (Exception var4)
            {
                a.log(Level.WARNING, "Failed to handle packet: " + var4, var4);
                var2.disconnect("Internal server error");
            }

            if (var2.disconnected)
            {
                this.d.remove(var1--);
            }

            var2.networkManager.a();
        }
    }

    public MinecraftServer d()
    {
        return this.c;
    }
}
