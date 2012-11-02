package cpw.mods.fml.relauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class RelaunchClassLoader extends URLClassLoader
{
    private static String[] excludedPackages = new String[0];
    private static String[] transformerExclusions = new String[0];
    private List sources;
    private ClassLoader parent;
    private List transformers;
    private Map cachedClasses;
    private Set classLoaderExceptions = new HashSet();
    private Set transformerExceptions = new HashSet();

    public RelaunchClassLoader(URL[] var1)
    {
        super(var1, (ClassLoader)null);
        this.sources = new ArrayList(Arrays.asList(var1));
        this.parent = this.getClass().getClassLoader();
        this.cachedClasses = new HashMap(1000);
        this.transformers = new ArrayList(2);
        Thread.currentThread().setContextClassLoader(this);
        this.addClassLoaderExclusion("java.");
        this.addClassLoaderExclusion("sun.");
        this.addClassLoaderExclusion("cpw.mods.fml.relauncher.");
        this.addClassLoaderExclusion("net.minecraftforge.classloading.");
        this.addTransformerExclusion("javax.");
        this.addTransformerExclusion("org.objectweb.asm.");
        this.addTransformerExclusion("com.google.common.");
        this.addTransformerExclusion("cpw.mods.fml.common.asm.SideOnly");
        this.addTransformerExclusion("cpw.mods.fml.common.Side");
    }

    public void registerTransformer(String var1)
    {
        try
        {
            this.transformers.add((IClassTransformer)this.loadClass(var1).newInstance());
        }
        catch (Exception var3)
        {
            FMLRelaunchLog.log(Level.SEVERE, var3, "A critical problem occured registering the ASM transformer class %s", new Object[] {var1});
        }
    }

    public Class findClass(String var1) throws ClassNotFoundException
    {
        if (excludedPackages.length != 0)
        {
            this.classLoaderExceptions.addAll(Arrays.asList(excludedPackages));
            excludedPackages = new String[0];
        }

        if (transformerExclusions.length != 0)
        {
            this.transformerExceptions.addAll(Arrays.asList(transformerExclusions));
            transformerExclusions = new String[0];
        }

        Iterator var2 = this.classLoaderExceptions.iterator();
        String var3;

        do
        {
            if (!var2.hasNext())
            {
                if (this.cachedClasses.containsKey(var1))
                {
                    return (Class)this.cachedClasses.get(var1);
                }

                var2 = this.transformerExceptions.iterator();

                do
                {
                    if (!var2.hasNext())
                    {
                        try
                        {
                            int var7 = var1.lastIndexOf(46);

                            if (var7 > -1)
                            {
                                var3 = var1.substring(0, var7);

                                if (this.getPackage(var3) == null)
                                {
                                    this.definePackage(var3, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
                                }
                            }

                            byte[] var8 = this.getClassBytes(var1);
                            byte[] var9 = this.runTransformers(var1, var8);
                            Class var5 = this.defineClass(var1, var9, 0, var9.length);
                            this.cachedClasses.put(var1, var5);
                            return var5;
                        }
                        catch (Throwable var6)
                        {
                            throw new ClassNotFoundException(var1, var6);
                        }
                    }

                    var3 = (String)var2.next();
                }
                while (!var1.startsWith(var3));

                Class var4 = super.findClass(var1);
                this.cachedClasses.put(var1, var4);
                return var4;
            }

            var3 = (String)var2.next();
        }
        while (!var1.startsWith(var3));

        return this.parent.loadClass(var1);
    }

    public byte[] getClassBytes(String var1) throws IOException
    {
        InputStream var2 = null;
        byte[] var4;

        try
        {
            URL var3 = this.findResource(var1.replace('.', '/').concat(".class"));

            if (var3 == null)
            {
                Object var14 = null;
                return (byte[])var14;
            }

            var2 = var3.openStream();
            var4 = this.readFully(var2);
        }
        finally
        {
            if (var2 != null)
            {
                try
                {
                    var2.close();
                }
                catch (IOException var12)
                {
                    ;
                }
            }
        }

        return var4;
    }

    private byte[] runTransformers(String var1, byte[] var2)
    {
        IClassTransformer var4;

        for (Iterator var3 = this.transformers.iterator(); var3.hasNext(); var2 = var4.transform(var1, var2))
        {
            var4 = (IClassTransformer)var3.next();
        }

        return var2;
    }

    public void addURL(URL var1)
    {
        super.addURL(var1);
        this.sources.add(var1);
    }

    public List getSources()
    {
        return this.sources;
    }

    private byte[] readFully(InputStream var1)
    {
        try
        {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream(var1.available());
            int var3;

            while ((var3 = var1.read()) != -1)
            {
                var2.write(var3);
            }

            return var2.toByteArray();
        }
        catch (Throwable var4)
        {
            return new byte[0];
        }
    }

    public List getTransformers()
    {
        return Collections.unmodifiableList(this.transformers);
    }

    private void addClassLoaderExclusion(String var1)
    {
        this.classLoaderExceptions.add(var1);
    }

    void addTransformerExclusion(String var1)
    {
        this.transformerExceptions.add(var1);
    }
}
