package cpw.mods.fml.common.asm;

import cpw.mods.fml.common.asm.FMLSanityChecker$1;
import cpw.mods.fml.common.asm.FMLSanityChecker$MLDetectorClassVisitor;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.RelaunchClassLoader;
import java.awt.Component;
import java.util.Map;
import javax.swing.JOptionPane;
import org.objectweb.asm.ClassReader;

public class FMLSanityChecker implements IFMLCallHook
{
    private RelaunchClassLoader cl;

    public Void call() throws Exception
    {
        byte[] var1 = this.cl.getClassBytes("ModLoader");

        if (var1 == null)
        {
            return null;
        }
        else
        {
            FMLSanityChecker$MLDetectorClassVisitor var2 = new FMLSanityChecker$MLDetectorClassVisitor((FMLSanityChecker$1)null);
            ClassReader var3 = new ClassReader(var1);
            var3.accept(var2, 1);

            if (!FMLSanityChecker$MLDetectorClassVisitor.access$100(var2))
            {
                JOptionPane.showMessageDialog((Component)null, "<html>CRITICAL ERROR<br/>ModLoader was detected in this environment<br/>ForgeModLoader cannot be installed alongside ModLoader<br/>All mods should work without ModLoader being installed<br/>Because ForgeModLoader is 100% compatible with ModLoader<br/>Re-install Minecraft Forge or Forge ModLoader into a clean<br/>jar and try again.", "ForgeModLoader critical error", 0);
                throw new RuntimeException("Invalid ModLoader class detected");
            }
            else
            {
                return null;
            }
        }
    }

    public void injectData(Map var1)
    {
        this.cl = (RelaunchClassLoader)var1.get("classLoader");
    }
}
