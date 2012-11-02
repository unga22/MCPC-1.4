package net.minecraftforge.transformers;

import cpw.mods.fml.relauncher.IClassTransformer;
import java.util.Iterator;
import net.minecraftforge.event.Event;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EventTransformer implements IClassTransformer
{
    public byte[] transform(String var1, byte[] var2)
    {
        if (!var1.equals("net.minecraftforge.event.Event") && !var1.startsWith("net.minecraft.") && var1.indexOf(46) != -1)
        {
            ClassReader var3 = new ClassReader(var2);
            ClassNode var4 = new ClassNode();
            var3.accept(var4, 0);

            try
            {
                if (this.buildEvents(var4))
                {
                    ClassWriter var5 = new ClassWriter(3);
                    var4.accept(var5);
                    return var5.toByteArray();
                }
                else
                {
                    return var2;
                }
            }
            catch (Exception var6)
            {
                var6.printStackTrace();
                return var2;
            }
        }
        else
        {
            return var2;
        }
    }

    private boolean buildEvents(ClassNode var1) throws Exception
    {
        Class var2 = this.getClass().getClassLoader().loadClass(var1.superName.replace('/', '.'));

        if (!Event.class.isAssignableFrom(var2))
        {
            return false;
        }
        else
        {
            boolean var3 = false;
            boolean var4 = false;
            boolean var5 = false;
            Class var6 = Class.forName("net.minecraftforge.event.ListenerList", false, this.getClass().getClassLoader());
            Type var7 = Type.getType(var6);
            Iterator var8 = var1.methods.iterator();
            MethodNode var9;

            while (var8.hasNext())
            {
                var9 = (MethodNode)var8.next();

                if (var9.name.equals("setup") && var9.desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])) && (var9.access & 4) == 4)
                {
                    var3 = true;
                }

                if (var9.name.equals("getListenerList") && var9.desc.equals(Type.getMethodDescriptor(var7, new Type[0])) && (var9.access & 1) == 1)
                {
                    var4 = true;
                }

                if (var9.name.equals("<init>") && var9.desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])))
                {
                    var5 = true;
                }
            }

            if (var3)
            {
                if (!var4)
                {
                    throw new RuntimeException("Event class defines setup() but does not define getListenerList! " + var1.name);
                }
                else
                {
                    return false;
                }
            }
            else
            {
                Type var11 = Type.getType(var1.superName);
                var1.fields.add(new FieldNode(10, "LISTENER_LIST", var7.getDescriptor(), (String)null, (Object)null));
                var9 = new MethodNode(262144, 1, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), (String)null, (String[])null);
                var9.instructions.add(new VarInsnNode(25, 0));
                var9.instructions.add(new MethodInsnNode(183, var11.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
                var9.instructions.add(new InsnNode(177));

                if (!var5)
                {
                    var1.methods.add(var9);
                }

                var9 = new MethodNode(262144, 4, "setup", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), (String)null, (String[])null);
                var9.instructions.add(new VarInsnNode(25, 0));
                var9.instructions.add(new MethodInsnNode(183, var11.getInternalName(), "setup", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
                var9.instructions.add(new FieldInsnNode(178, var1.name, "LISTENER_LIST", var7.getDescriptor()));
                LabelNode var10 = new LabelNode();
                var9.instructions.add(new JumpInsnNode(198, var10));
                var9.instructions.add(new InsnNode(177));
                var9.instructions.add(var10);
                var9.instructions.add(new FrameNode(3, 0, (Object[])null, 0, (Object[])null));
                var9.instructions.add(new TypeInsnNode(187, var7.getInternalName()));
                var9.instructions.add(new InsnNode(89));
                var9.instructions.add(new VarInsnNode(25, 0));
                var9.instructions.add(new MethodInsnNode(183, var11.getInternalName(), "getListenerList", Type.getMethodDescriptor(var7, new Type[0])));
                var9.instructions.add(new MethodInsnNode(183, var7.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {var7})));
                var9.instructions.add(new FieldInsnNode(179, var1.name, "LISTENER_LIST", var7.getDescriptor()));
                var9.instructions.add(new InsnNode(177));
                var1.methods.add(var9);
                var9 = new MethodNode(262144, 1, "getListenerList", Type.getMethodDescriptor(var7, new Type[0]), (String)null, (String[])null);
                var9.instructions.add(new FieldInsnNode(178, var1.name, "LISTENER_LIST", var7.getDescriptor()));
                var9.instructions.add(new InsnNode(176));
                var1.methods.add(var9);
                return true;
            }
        }
    }
}
