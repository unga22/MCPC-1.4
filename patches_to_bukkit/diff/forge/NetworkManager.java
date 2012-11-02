package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;

public class NetworkManager implements INetworkManager
{
    public static AtomicInteger field_74471_a = new AtomicInteger();
    public static AtomicInteger field_74469_b = new AtomicInteger();

    /** The object used for synchronization on the send queue. */
    private Object h;

    /** The socket used by this network manager. */
    private Socket socket;

    /** The InetSocketAddress of the remote endpoint */
    private final SocketAddress j;

    /** The input stream connected to the socket. */
    private volatile DataInputStream input;

    /** The output stream connected to the socket. */
    private volatile DataOutputStream output;

    /** Whether the network is currently operational. */
    private volatile boolean m;

    /**
     * Whether this network manager is currently terminating (and should ignore further errors).
     */
    private volatile boolean n;

    /**
     * Linked list of packets that have been read and are awaiting processing.
     */
    private List inboundQueue;

    /** Linked list of packets awaiting sending. */
    private List highPriorityQueue;

    /** Linked list of packets with chunk data that are awaiting sending. */
    private List lowPriorityQueue;

    /** A reference to the NetHandler object. */
    private NetHandler packetListener;

    /**
     * Whether this server is currently terminating. If this is a client, this is always false.
     */
    private boolean s;

    /** The thread used for writing. */
    private Thread t;

    /** The thread used for reading. */
    private Thread u;

    /** A String indicating why the network has shutdown. */
    private String v;
    private Object[] field_74480_w;
    private int field_74490_x;

    /**
     * The length in bytes of the packets in both send queues (data and chunkData).
     */
    private int y;
    public static int[] field_74470_c = new int[256];
    public static int[] field_74467_d = new int[256];
    public int field_74468_e;
    boolean f;
    boolean g;
    private SecretKey z;
    private PrivateKey field_74463_A;
    private int field_74464_B;

    @SideOnly(Side.CLIENT)
    public NetworkManager(Socket var1, String var2, NetHandler var3) throws IOException
    {
        this(var1, var2, var3, (PrivateKey)null);
    }

    public NetworkManager(Socket var1, String var2, NetHandler var3, PrivateKey var4) throws IOException
    {
        this.h = new Object();
        this.m = true;
        this.n = false;
        this.inboundQueue = Collections.synchronizedList(new ArrayList());
        this.highPriorityQueue = Collections.synchronizedList(new ArrayList());
        this.lowPriorityQueue = Collections.synchronizedList(new ArrayList());
        this.s = false;
        this.v = "";
        this.field_74490_x = 0;
        this.y = 0;
        this.field_74468_e = 0;
        this.f = false;
        this.g = false;
        this.z = null;
        this.field_74463_A = null;
        this.field_74464_B = 50;
        this.field_74463_A = var4;
        this.socket = var1;
        this.j = var1.getRemoteSocketAddress();
        this.packetListener = var3;

        try
        {
            var1.setSoTimeout(30000);
            var1.setTrafficClass(24);
        }
        catch (SocketException var6)
        {
            System.err.println(var6.getMessage());
        }

        this.input = new DataInputStream(var1.getInputStream());
        this.output = new DataOutputStream(new BufferedOutputStream(var1.getOutputStream(), 5120));
        this.u = new NetworkReaderThread(this, var2 + " read thread");
        this.t = new NetworkWriterThread(this, var2 + " write thread");
        this.u.start();
        this.t.start();
    }

    @SideOnly(Side.CLIENT)
    public void f()
    {
        this.a();
        this.t = null;
        this.u = null;
    }

    /**
     * Sets the NetHandler for this NetworkManager. Server-only.
     */
    public void a(NetHandler var1)
    {
        this.packetListener = var1;
    }

    /**
     * Adds the packet to the correct send queue (chunk data packets go to a separate queue).
     */
    public void queue(Packet var1)
    {
        if (!this.s)
        {
            Object var2 = this.h;
            Object var3 = this.h;

            synchronized (this.h)
            {
                this.y += var1.a() + 1;

                if (var1.lowPriority)
                {
                    this.lowPriorityQueue.add(var1);
                }
                else
                {
                    this.highPriorityQueue.add(var1);
                }
            }
        }
    }

    /**
     * Sends a data packet if there is one to send, or sends a chunk data packet if there is one and the counter is up,
     * or does nothing.
     */
    private boolean h()
    {
        boolean var1 = false;

        try
        {
            Packet var2;
            int var3;
            int[] var4;

            if (this.field_74468_e == 0 || System.currentTimeMillis() - ((Packet)this.highPriorityQueue.get(0)).timestamp >= (long)this.field_74468_e)
            {
                var2 = this.func_74460_a(false);

                if (var2 != null)
                {
                    Packet.a(var2, this.output);

                    if (var2 instanceof Packet252KeyResponse && !this.g)
                    {
                        if (!this.packetListener.a())
                        {
                            this.z = ((Packet252KeyResponse)var2).func_73304_d();
                        }

                        this.k();
                    }

                    var4 = field_74467_d;
                    var3 = var2.k();
                    var4[var3] += var2.a() + 1;
                    var1 = true;
                }
            }

            if (this.field_74464_B-- <= 0 && (this.field_74468_e == 0 || System.currentTimeMillis() - ((Packet)this.lowPriorityQueue.get(0)).timestamp >= (long)this.field_74468_e))
            {
                var2 = this.func_74460_a(true);

                if (var2 != null)
                {
                    Packet.a(var2, this.output);
                    var4 = field_74467_d;
                    var3 = var2.k();
                    var4[var3] += var2.a() + 1;
                    this.field_74464_B = 0;
                    var1 = true;
                }
            }

            return var1;
        }
        catch (Exception var5)
        {
            if (!this.n)
            {
                this.a(var5);
            }

            return false;
        }
    }

    private Packet func_74460_a(boolean var1)
    {
        Packet var2 = null;
        List var3 = var1 ? this.lowPriorityQueue : this.highPriorityQueue;
        Object var4 = this.h;
        Object var5 = this.h;

        synchronized (this.h)
        {
            while (!var3.isEmpty() && var2 == null)
            {
                var2 = (Packet)var3.remove(0);
                this.y -= var2.a() + 1;

                if (this.func_74454_a(var2, var1))
                {
                    var2 = null;
                }
            }

            return var2;
        }
    }

    private boolean func_74454_a(Packet var1, boolean var2)
    {
        if (!var1.e())
        {
            return false;
        }
        else
        {
            List var3 = var2 ? this.lowPriorityQueue : this.highPriorityQueue;
            Iterator var4 = var3.iterator();

            while (var4.hasNext())
            {
                Packet var5 = (Packet)var4.next();

                if (var5.k() == var1.k())
                {
                    return var1.a(var5);
                }
            }

            return false;
        }
    }

    /**
     * Wakes reader and writer threads
     */
    public void a()
    {
        if (this.u != null)
        {
            this.u.interrupt();
        }

        if (this.t != null)
        {
            this.t.interrupt();
        }
    }

    /**
     * Reads a single packet from the input stream and adds it to the read queue. If no packet is read, it shuts down
     * the network.
     */
    private boolean i()
    {
        boolean var1 = false;

        try
        {
            Packet var2 = Packet.a(this.input, this.packetListener.a(), this.socket);

            if (var2 != null)
            {
                if (var2 instanceof Packet252KeyResponse && !this.f)
                {
                    if (this.packetListener.a())
                    {
                        this.z = ((Packet252KeyResponse)var2).func_73303_a(this.field_74463_A);
                    }

                    this.j();
                }

                int[] var3 = field_74470_c;
                int var4 = var2.k();
                var3[var4] += var2.a() + 1;

                if (!this.s)
                {
                    if (var2.a_() && this.packetListener.b())
                    {
                        this.field_74490_x = 0;
                        var2.handle(this.packetListener);
                    }
                    else
                    {
                        this.inboundQueue.add(var2);
                    }
                }

                var1 = true;
            }
            else
            {
                this.a("disconnect.endOfStream", new Object[0]);
            }

            return var1;
        }
        catch (Exception var5)
        {
            if (!this.n)
            {
                this.a(var5);
            }

            return false;
        }
    }

    /**
     * Used to report network errors and causes a network shutdown.
     */
    private void a(Exception var1)
    {
        var1.printStackTrace();
        this.a("disconnect.genericReason", new Object[] {"Internal exception: " + var1.toString()});
    }

    /**
     * Shuts down the network with the specified reason. Closes all streams and sockets, spawns NetworkMasterThread to
     * stop reading and writing threads.
     */
    public void a(String var1, Object ... var2)
    {
        if (this.m)
        {
            this.n = true;
            this.v = var1;
            this.field_74480_w = var2;
            this.m = false;
            (new NetworkMasterThread(this)).start();

            try
            {
                this.input.close();
                this.input = null;
                this.output.close();
                this.output = null;
                this.socket.close();
                this.socket = null;
            }
            catch (Throwable var4)
            {
                ;
            }
        }
    }

    /**
     * Checks timeouts and processes all pending read packets.
     */
    public void b()
    {
        if (this.y > 2097152)
        {
            this.a("disconnect.overflow", new Object[0]);
        }

        if (this.inboundQueue.isEmpty())
        {
            if (this.field_74490_x++ == 1200)
            {
                this.a("disconnect.timeout", new Object[0]);
            }
        }
        else
        {
            this.field_74490_x = 0;
        }

        int var1 = 1000;

        while (!this.inboundQueue.isEmpty() && var1-- >= 0)
        {
            Packet var2 = (Packet)this.inboundQueue.remove(0);
            var2.handle(this.packetListener);
        }

        this.a();

        if (this.n && this.inboundQueue.isEmpty())
        {
            this.packetListener.a(this.v, this.field_74480_w);
            FMLNetworkHandler.onConnectionClosed(this, this.packetListener.getPlayer());
        }
    }

    /**
     * Returns the socket address of the remote side. Server-only.
     */
    public SocketAddress getSocketAddress()
    {
        return this.j;
    }

    /**
     * Shuts down the server. (Only actually used on the server)
     */
    public void d()
    {
        if (!this.s)
        {
            this.a();
            this.s = true;
            this.u.interrupt();
            (new NetworkMonitorThread(this)).start();
        }
    }

    private void j() throws IOException
    {
        this.f = true;
        this.input = new DataInputStream(MinecraftEncryption.a(this.z, this.socket.getInputStream()));
    }

    /**
     * flushes the stream and replaces it with an encryptedOutputStream
     */
    private void k() throws IOException
    {
        this.output.flush();
        this.g = true;
        this.output = new DataOutputStream(new BufferedOutputStream(MinecraftEncryption.a(this.z, this.socket.getOutputStream()), 5120));
    }

    /**
     * Returns the number of chunk data packets waiting to be sent.
     */
    public int e()
    {
        return this.lowPriorityQueue.size();
    }

    public Socket getSocket()
    {
        return this.socket;
    }

    /**
     * Whether the network is operational.
     */
    static boolean a(NetworkManager var0)
    {
        return var0.m;
    }

    /**
     * Is the server terminating? Client side aways returns false.
     */
    static boolean b(NetworkManager var0)
    {
        return var0.s;
    }

    /**
     * Static accessor to readPacket.
     */
    static boolean c(NetworkManager var0)
    {
        return var0.i();
    }

    /**
     * Static accessor to sendPacket.
     */
    static boolean d(NetworkManager var0)
    {
        return var0.h();
    }

    static DataOutputStream e(NetworkManager var0)
    {
        return var0.output;
    }

    /**
     * Gets whether the Network manager is terminating.
     */
    static boolean f(NetworkManager var0)
    {
        return var0.n;
    }

    /**
     * Sends the network manager an error
     */
    static void a(NetworkManager var0, Exception var1)
    {
        var0.a(var1);
    }

    /**
     * Returns the read thread.
     */
    static Thread g(NetworkManager var0)
    {
        return var0.u;
    }

    /**
     * Returns the write thread.
     */
    static Thread h(NetworkManager var0)
    {
        return var0.t;
    }
}
