package net.minecraftforge.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

public class Configuration$UnicodeInputStreamReader extends Reader
{
    private final InputStreamReader input;
    private final String defaultEnc;

    public Configuration$UnicodeInputStreamReader(InputStream var1, String var2) throws IOException
    {
        this.defaultEnc = var2;
        String var3 = var2;
        byte[] var4 = new byte[4];
        PushbackInputStream var5 = new PushbackInputStream(var1, var4.length);
        int var6 = var5.read(var4, 0, var4.length);
        byte var7 = 0;
        int var8 = (var4[0] & 255) << 8 | var4[1] & 255;
        int var9 = var8 << 8 | var4[2] & 255;
        int var10 = var9 << 8 | var4[3] & 255;

        if (var9 == 15711167)
        {
            var3 = "UTF-8";
            var7 = 3;
        }
        else if (var8 == 65279)
        {
            var3 = "UTF-16BE";
            var7 = 2;
        }
        else if (var8 == 65534)
        {
            var3 = "UTF-16LE";
            var7 = 2;
        }
        else if (var10 == 65279)
        {
            var3 = "UTF-32BE";
            var7 = 4;
        }
        else if (var10 == -131072)
        {
            var3 = "UTF-32LE";
            var7 = 4;
        }

        if (var7 < var6)
        {
            var5.unread(var4, var7, var6 - var7);
        }

        this.input = new InputStreamReader(var5, var3);
    }

    public String getEncoding()
    {
        return this.input.getEncoding();
    }

    public int read(char[] var1, int var2, int var3) throws IOException
    {
        return this.input.read(var1, var2, var3);
    }

    public void close() throws IOException
    {
        this.input.close();
    }
}
