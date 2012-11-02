package net.minecraft.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTCompressedStreamTools
{
  public static NBTTagCompound a(InputStream paramInputStream)
  {
    DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(paramInputStream)));
    try {
      return a(localDataInputStream);
    } finally {
      localDataInputStream.close();
    }
  }

  public static void a(NBTTagCompound paramNBTTagCompound, OutputStream paramOutputStream) {
    DataOutputStream localDataOutputStream = new DataOutputStream(new GZIPOutputStream(paramOutputStream));
    try {
      a(paramNBTTagCompound, localDataOutputStream);
    } finally {
      localDataOutputStream.close();
    }
  }

  public static NBTTagCompound a(byte[] paramArrayOfByte) {
    DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(paramArrayOfByte))));
    try {
      return a(localDataInputStream);
    } finally {
      localDataInputStream.close();
    }
  }

  public static byte[] a(NBTTagCompound paramNBTTagCompound) {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(new GZIPOutputStream(localByteArrayOutputStream));
    try {
      a(paramNBTTagCompound, localDataOutputStream);
    } finally {
      localDataOutputStream.close();
    }
    return localByteArrayOutputStream.toByteArray();
  }

  public static NBTTagCompound a(DataInput paramDataInput)
  {
    NBTBase localNBTBase = NBTBase.b(paramDataInput);
    if ((localNBTBase instanceof NBTTagCompound)) return (NBTTagCompound)localNBTBase;
    throw new IOException("Root tag must be a named compound tag");
  }

  public static void a(NBTTagCompound paramNBTTagCompound, DataOutput paramDataOutput) {
    NBTBase.a(paramNBTTagCompound, paramDataOutput);
  }
}

