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
    /**
     * Load the gzipped compound from the inputstream.
     */
    public static NBTTagCompound a(InputStream var0)
    {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var0)));
        NBTTagCompound var2;

        try
        {
            var2 = a(var1);
        }
        finally
        {
            var1.close();
        }

        return var2;
    }

    /**
     * Write the compound, gzipped, to the outputstream.
     */
    public static void a(NBTTagCompound var0, OutputStream var1)
    {
        DataOutputStream var2 = new DataOutputStream(new GZIPOutputStream(var1));

        try
        {
            a(var0, var2);
        }
        finally
        {
            var2.close();
        }
    }

    public static NBTTagCompound a(byte[] var0)
    {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var0))));
        NBTTagCompound var2;

        try
        {
            var2 = a(var1);
        }
        finally
        {
            var1.close();
        }

        return var2;
    }

    public static byte[] a(NBTTagCompound var0)
    {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        DataOutputStream var2 = new DataOutputStream(new GZIPOutputStream(var1));

        try
        {
            a(var0, var2);
        }
        finally
        {
            var2.close();
        }

        return var1.toByteArray();
    }

    /**
     * Reads from a CompressedStream.
     */
    public static NBTTagCompound a(DataInput var0)
    {
        NBTBase var1 = NBTBase.b(var0);

        if (var1 instanceof NBTTagCompound)
        {
            return (NBTTagCompound)var1;
        }
        else
        {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void a(NBTTagCompound var0, DataOutput var1)
    {
        NBTBase.a(var0, var1);
    }
}
