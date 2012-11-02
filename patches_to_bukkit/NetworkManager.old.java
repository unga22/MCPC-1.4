package net.minecraft.server;

import forge.ForgeHooks;
import forge.MessageManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager
{
  public static final Object a = new Object();
  public static int b;
  public static int c;
  private Object g = new Object();
  public Socket socket;
  private final SocketAddress i;
  private DataInputStream input;
  private DataOutputStream output;
  private boolean l = true;
  private Queue m = new ConcurrentLinkedQueue();
  private List highPriorityQueue = Collections.synchronizedList(new ArrayList());
  private List lowPriorityQueue = Collections.synchronizedList(new ArrayList());
  private NetHandler packetListener;
  private boolean q = false;
  private Thread r;
  private Thread s;
  private boolean t = false;
  private String u = "";
  private Object[] v;
  private int w = 0;
  private int x = 0;
  public static int[] d = new int[256];
  public static int[] e = new int[256];
  public int f = 0;
  private int lowPriorityQueueDelay = 50;

  public NetworkManager(Socket socket, String s, NetHandler nethandler) {
    this.socket = socket;
    this.i = socket.getRemoteSocketAddress();
    this.packetListener = nethandler;
    try
    {
      socket.setTrafficClass(24);
    }
    catch (SocketException e)
    {
    }
    try {
      socket.setSoTimeout(30000);
      this.input = new DataInputStream(new BufferedInputStream(socket.getInputStream(), 2));
      this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 5120));
    }
    catch (IOException socketexception) {
      System.err.println(socketexception.getMessage());
    }

    this.s = new NetworkReaderThread(this, s + " read thread");
    this.r = new NetworkWriterThread(this, s + " write thread");
    this.s.start();
    this.r.start();
  }

  public void a(NetHandler nethandler) {
    this.packetListener = nethandler;
  }

  public void queue(Packet packet) {
    if (!this.q) {
      Object object = this.g;

      synchronized (this.g) {
        this.x += packet.a() + 1;
        if (packet.lowPriority)
          this.lowPriorityQueue.add(packet);
        else
          this.highPriorityQueue.add(packet);
      }
    }
  }

  private boolean g()
  {
    boolean flag = false;
    try
    {
      if ((!this.highPriorityQueue.isEmpty()) && ((this.f == 0) || (System.currentTimeMillis() - ((Packet)this.highPriorityQueue.get(0)).timestamp >= this.f))) {
        Object object = this.g;
        Packet packet;
        synchronized (this.g) {
          packet = (Packet)this.highPriorityQueue.remove(0);
          this.x -= packet.a() + 1;
        }

        Packet.a(packet, this.output);
        int[] aint = e;
        int i = packet.b();
        aint[i] += packet.a() + 1;
        flag = true;
      }

      if (((flag) || (this.lowPriorityQueueDelay-- <= 0)) && (!this.lowPriorityQueue.isEmpty()) && ((this.highPriorityQueue.isEmpty()) || (((Packet)this.highPriorityQueue.get(0)).timestamp > ((Packet)this.lowPriorityQueue.get(0)).timestamp))) {
        Object object = this.g;
        Packet packet;
        synchronized (this.g) {
          packet = (Packet)this.lowPriorityQueue.remove(0);
          this.x -= packet.a() + 1;
        }

        Packet.a(packet, this.output);
        int[] aint = e;
        int i = packet.b();
        aint[i] += packet.a() + 1;
        this.lowPriorityQueueDelay = 0;
      }return true;
    }
    catch (Exception exception)
    {
      if (!this.t) {
        a(exception);
      }
    }
    return false;
  }

  public void a()
  {
    this.s.interrupt();
    this.r.interrupt();
  }

  private boolean h() {
    boolean flag = false;
    try
    {
      this.input.mark(2);
      if ((this.input.read() == 2) && (this.input.read() != 0)) {
        Packet.a(this.input, 16);
        Packet.a(this.input, 255);
        this.input.readInt();

        if (this.q) {
          return true;
        }

        this.m.clear();
        this.m.add(new Packet2Handshake(null));
        return true;
      }
      this.input.reset();

      Packet packet = Packet.a(this.input, this.packetListener.c());

      if (packet != null) {
        int[] aint = d;
        int i = packet.b();

        aint[i] += packet.a() + 1;
        if (!this.q) {
          this.m.add(packet);
        }

        flag = true;
      } else {
        a("disconnect.endOfStream", new Object[0]);
      }

      return flag;
    } catch (Exception exception) {
      if (!this.t) {
        a(exception);
      }
    }
    return false;
  }

  private void a(Exception exception)
  {
    a("disconnect.genericReason", new Object[] { "Internal exception: " + exception.toString() });
  }

  public void a(String s, Object[] aobject) {
    if (this.l) {
      this.t = true;
      this.u = s;
      this.v = aobject;
      new NetworkMasterThread(this).start();
      this.l = false;
      try
      {
        this.input.close();
        this.input = null;
      }
      catch (Throwable throwable)
      {
      }
      try {
        this.output.close();
        this.output = null;
      }
      catch (Throwable throwable1)
      {
      }
      try {
        this.socket.close();
        this.socket = null;
      }
      catch (Throwable throwable2) {
      }
    }
    ForgeHooks.onDisconnect(this, s, aobject);
    MessageManager.getInstance().removeConnection(this);
  }

  public void b() {
    if (this.x > 1048576) {
      a("disconnect.overflow", new Object[0]);
    }

    if (this.m.isEmpty()) {
      if (this.w++ == 1200)
        a("disconnect.timeout", new Object[0]);
    }
    else {
      this.w = 0;
    }

    int i = 1000;

    while ((!this.m.isEmpty()) && (i-- >= 0)) {
      Packet packet = (Packet)this.m.poll();

      if (!this.q) packet.handle(this.packetListener);
    }

    a();
    if ((this.t) && (this.m.isEmpty()))
      this.packetListener.a(this.u, this.v);
  }

  public SocketAddress getSocketAddress()
  {
    return this.i;
  }

  public void d() {
    if (!this.q) {
      a();
      this.q = true;
      this.s.interrupt();
      new NetworkMonitorThread(this).start();
    }
  }

  public int e() {
    return this.lowPriorityQueue.size();
  }

  public Socket getSocket() {
    return this.socket;
  }

  static boolean a(NetworkManager networkmanager) {
    return networkmanager.l;
  }

  static boolean b(NetworkManager networkmanager) {
    return networkmanager.q;
  }

  static boolean c(NetworkManager networkmanager) {
    return networkmanager.h();
  }

  static boolean d(NetworkManager networkmanager) {
    return networkmanager.g();
  }

  static DataOutputStream e(NetworkManager networkmanager) {
    return networkmanager.output;
  }

  static boolean f(NetworkManager networkmanager) {
    return networkmanager.t;
  }

  static void a(NetworkManager networkmanager, Exception exception) {
    networkmanager.a(exception);
  }

  static Thread g(NetworkManager networkmanager) {
    return networkmanager.s;
  }

  static Thread h(NetworkManager networkmanager) {
    return networkmanager.r;
  }

  public NetHandler getNetHandler()
  {
    return this.packetListener;
  }
}

