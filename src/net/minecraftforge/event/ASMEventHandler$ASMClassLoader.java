package net.minecraftforge.event;

import net.minecraftforge.event.ASMEventHandler$1;

class ASMEventHandler$ASMClassLoader extends ClassLoader
{
    private ASMEventHandler$ASMClassLoader()
    {
        super(ASMEventHandler$ASMClassLoader.class.getClassLoader());
    }

    public Class define(String var1, byte[] var2)
    {
        return this.defineClass(var1, var2, 0, var2.length);
    }

    ASMEventHandler$ASMClassLoader(ASMEventHandler$1 var1)
    {
        this();
    }
}
