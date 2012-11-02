package cpw.mods.fml.common.asm;

import cpw.mods.fml.common.registry.BlockProxy;
import cpw.mods.fml.relauncher.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class ASMTransformer implements IClassTransformer
{
    public byte[] transform(String var1, byte[] var2)
    {
        if ("net.minecraft.src.Block".equals(var1))
        {
            ClassReader var3 = new ClassReader(var2);
            ClassNode var4 = new ClassNode(262144);
            var3.accept(var4, 8);
            var4.interfaces.add(Type.getInternalName(BlockProxy.class));
            ClassWriter var5 = new ClassWriter(3);
            var4.accept(var5);
            return var5.toByteArray();
        }
        else
        {
            return var2;
        }
    }
}
