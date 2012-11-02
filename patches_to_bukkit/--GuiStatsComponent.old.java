package net.minecraft.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.Timer;

public class GuiStatsComponent extends JComponent
{
  private static final DecimalFormat a = new DecimalFormat("########0.000");

  private int[] b = new int[256];
  private int c = 0;
  private String[] d = new String[10];
  private final MinecraftServer e;

  public GuiStatsComponent(MinecraftServer paramMinecraftServer)
  {
    this.e = paramMinecraftServer;
    setPreferredSize(new Dimension(356, 246));
    setMinimumSize(new Dimension(356, 246));
    setMaximumSize(new Dimension(356, 246));
    new Timer(500, new GuiStatsListener(this)).start();

    setBackground(Color.BLACK);
  }

  private void a() {
    long l = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    System.gc();
    this.d[0] = ("Memory use: " + l / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)");
    this.d[1] = ("Threads: " + NetworkManager.b + " + " + NetworkManager.c);
    this.d[2] = ("Avg tick: " + a.format(a(this.e.f) * 1.0E-006D) + " ms");
    this.d[3] = ("Avg sent: " + (int)a(this.e.u) + ", Avg size: " + (int)a(this.e.v));
    this.d[4] = ("Avg rec: " + (int)a(this.e.w) + ", Avg size: " + (int)a(this.e.x));
    if (this.e.worldServer != null) {
      for (int i = 0; i < this.e.worldServer.length; tmp408_407++) {
        this.d[(5 + i)] = ("Lvl " + i + " tick: " + a.format(a(this.e.g[i]) * 1.0E-006D) + " ms");
        if ((this.e.worldServer[i] != null) && (this.e.worldServer[i].chunkProviderServer != null))
        {
          int tmp408_407 = (5 + i);
          String[] tmp408_402 = this.d; tmp408_402[tmp408_407] = (tmp408_402[tmp408_407] + ", " + this.e.worldServer[tmp408_407].chunkProviderServer.d());
        }

      }

    }

    this.b[(this.c++ & 0xFF)] = (int)(a(this.e.v) * 100.0D / 12500.0D);
    repaint();
  }

  private double a(long[] paramArrayOfLong) {
    long l = 0L;
    for (int i = 0; i < paramArrayOfLong.length; i++) {
      l += paramArrayOfLong[i];
    }
    return l / paramArrayOfLong.length;
  }

  public void paint(Graphics paramGraphics) {
    paramGraphics.setColor(new Color(16777215));
    paramGraphics.fillRect(0, 0, 356, 246);

    for (int i = 0; i < 256; i++) {
      int j = this.b[(i + this.c & 0xFF)];
      paramGraphics.setColor(new Color(j + 28 << 16));
      paramGraphics.fillRect(i, 100 - j, 1, j);
    }
    paramGraphics.setColor(Color.BLACK);
    for (i = 0; i < this.d.length; i++) {
      String str = this.d[i];
      if (str != null) paramGraphics.drawString(str, 32, 116 + i * 16);
    }
  }
}

