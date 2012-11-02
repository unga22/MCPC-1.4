package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Packet1Login extends Packet
{
  public int a;
  public String name;
  public WorldType c;
  public int d;
  public int e;
  public byte f;
  public byte g;
  public byte h;

  public Packet1Login()
  {
  }

  public Packet1Login(String paramString, int paramInt1, WorldType paramWorldType, int paramInt2, int paramInt3, byte paramByte1, byte paramByte2, byte paramByte3)
  {
    this.name = paramString;
    this.a = paramInt1;
    this.c = paramWorldType;
    this.e = paramInt3;
    this.f = paramByte1;
    this.d = paramInt2;
    this.g = paramByte2;
    this.h = paramByte3;
  }

  public void a(DataInputStream paramDataInputStream) {
    this.a = paramDataInputStream.readInt();
    this.name = a(paramDataInputStream, 16);
    String str = a(paramDataInputStream, 16);
    this.c = WorldType.getType(str);
    if (this.c == null) {
      this.c = WorldType.NORMAL;
    }
    this.d = paramDataInputStream.readInt();
    this.e = paramDataInputStream.readInt();
    this.f = paramDataInputStream.readByte();
    this.g = paramDataInputStream.readByte();
    this.h = paramDataInputStream.readByte();
  }

  public void a(DataOutputStream paramDataOutputStream) {
    paramDataOutputStream.writeInt(this.a);
    a(this.name, paramDataOutputStream);
    if (this.c == null)
      a("", paramDataOutputStream);
    else {
      a(this.c.name(), paramDataOutputStream);
    }
    paramDataOutputStream.writeInt(this.d);
    paramDataOutputStream.writeInt(this.e);
    paramDataOutputStream.writeByte(this.f);
    paramDataOutputStream.writeByte(this.g);
    paramDataOutputStream.writeByte(this.h);
  }

  public void handle(NetHandler paramNetHandler) {
    paramNetHandler.a(this);
  }

  public int a() {
    int i = 0;
    if (this.c != null) {
      i = this.c.name().length();
    }
    return 4 + this.name.length() + 4 + 7 + 7 + i;
  }
}

