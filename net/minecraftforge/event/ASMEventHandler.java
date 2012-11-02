package net.minecraftforge.event;

import java.lang.reflect.Method;
import net.minecraftforge.event.ASMEventHandler$1;
import net.minecraftforge.event.ASMEventHandler$ASMClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ASMEventHandler implements IEventListener
{
    private static int IDs = 0;
    private static final String HANDLER_DESC = Type.getInternalName(IEventListener.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(IEventListener.class.getDeclaredMethods()[0]);
    private static final ASMEventHandler$ASMClassLoader LOADER = new ASMEventHandler$ASMClassLoader((ASMEventHandler$1)null);
    private final IEventListener handler;
    private final ForgeSubscribe subInfo;

    public ASMEventHandler(Object var1, Method var2) throws Exception
    {
        this.handler = (IEventListener)this.createWrapper(var2).getConstructor(new Class[] {Object.class}).newInstance(new Object[] {var1});
        this.subInfo = (ForgeSubscribe)var2.getAnnotation(ForgeSubscribe.class);
    }

    public void invoke(Event var1)
    {
        if (this.handler != null && (!var1.isCancelable() || !var1.isCanceled() || this.subInfo.receiveCanceled()))
        {
            this.handler.invoke(var1);
        }
    }

    public EventPriority getPriority()
    {
        return this.subInfo.priority();
    }

    public Class createWrapper(Method var1)
    {
        ClassWriter var2 = new ClassWriter(0);
        String var4 = this.getUniqueName(var1);
        String var5 = var4.replace('.', '/');
        String var6 = Type.getInternalName(var1.getDeclaringClass());
        String var7 = Type.getInternalName(var1.getParameterTypes()[0]);
        var2.visit(50, 33, var5, (String)null, "java/lang/Object", new String[] {HANDLER_DESC});
        var2.visitSource(".dynamic", (String)null);
        var2.visitField(1, "instance", "Ljava/lang/Object;", (String)null, (Object)null).visitEnd();
        MethodVisitor var3 = var2.visitMethod(1, "<init>", "(Ljava/lang/Object;)V", (String)null, (String[])null);
        var3.visitCode();
        var3.visitVarInsn(25, 0);
        var3.visitMethodInsn(183, "java/lang/Object", "<init>", "()V");
        var3.visitVarInsn(25, 0);
        var3.visitVarInsn(25, 1);
        var3.visitFieldInsn(181, var5, "instance", "Ljava/lang/Object;");
        var3.visitInsn(177);
        var3.visitMaxs(2, 2);
        var3.visitEnd();
        var3 = var2.visitMethod(1, "invoke", HANDLER_FUNC_DESC, (String)null, (String[])null);
        var3.visitCode();
        var3.visitVarInsn(25, 0);
        var3.visitFieldInsn(180, var5, "instance", "Ljava/lang/Object;");
        var3.visitTypeInsn(192, var6);
        var3.visitVarInsn(25, 1);
        var3.visitTypeInsn(192, var7);
        var3.visitMethodInsn(182, var6, var1.getName(), Type.getMethodDescriptor(var1));
        var3.visitInsn(177);
        var3.visitMaxs(2, 2);
        var3.visitEnd();
        var2.visitEnd();
        return LOADER.define(var4, var2.toByteArray());
    }

    private String getUniqueName(Method var1)
    {
        return String.format("%s_%d_%s_%s_%s", new Object[] {this.getClass().getName(), Integer.valueOf(IDs++), var1.getDeclaringClass().getSimpleName(), var1.getName(), var1.getParameterTypes()[0].getSimpleName()});
    }
}
