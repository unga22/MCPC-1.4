package cpw.mods.fml.common;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable$ASMData;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

public class ProxyInjector
{
    public static void inject(ModContainer var0, ASMDataTable var1, Side var2)
    {
        FMLLog.fine("Attempting to inject @SidedProxy classes into %s", new Object[] {var0.getModId()});
        Set var3 = var1.getAnnotationsFor(var0).get(SidedProxy.class.getName());
        ClassLoader var4 = Loader.instance().getModClassLoader();
        Iterator var5 = var3.iterator();

        while (var5.hasNext())
        {
            ASMDataTable$ASMData var6 = (ASMDataTable$ASMData)var5.next();

            try
            {
                Class var7 = Class.forName(var6.getClassName(), true, var4);
                Field var8 = var7.getDeclaredField(var6.getObjectName());

                if (var8 == null)
                {
                    FMLLog.severe("Attempted to load a proxy type into %s.%s but the field was not found", new Object[] {var6.getClassName(), var6.getObjectName()});
                    throw new LoaderException();
                }

                String var9 = var2.isClient() ? ((SidedProxy)var8.getAnnotation(SidedProxy.class)).clientSide() : ((SidedProxy)var8.getAnnotation(SidedProxy.class)).serverSide();
                Object var10 = Class.forName(var9, true, var4).newInstance();

                if ((var8.getModifiers() & 8) == 0)
                {
                    FMLLog.severe("Attempted to load a proxy type %s into %s.%s, but the field is not static", new Object[] {var9, var6.getClassName(), var6.getObjectName()});
                    throw new LoaderException();
                }

                if (!var8.getType().isAssignableFrom(var10.getClass()))
                {
                    FMLLog.severe("Attempted to load a proxy type %s into %s.%s, but the types don\'t match", new Object[] {var9, var6.getClassName(), var6.getObjectName()});
                    throw new LoaderException();
                }

                var8.set((Object)null, var10);
            }
            catch (Exception var11)
            {
                FMLLog.log(Level.SEVERE, var11, "An error occured trying to load a proxy into %s.%s", new Object[] {var6.getAnnotationInfo(), var6.getClassName(), var6.getObjectName()});
                throw new LoaderException(var11);
            }
        }
    }
}
