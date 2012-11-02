package cpw.mods.fml.common;

import com.google.common.base.Function;
import cpw.mods.fml.common.versioning.ArtifactVersion;

class Loader$1 implements Function
{
    final Loader this$0;

    Loader$1(Loader var1)
    {
        this.this$0 = var1;
    }

    public String apply(ArtifactVersion var1)
    {
        return var1.getLabel();
    }

    public Object apply(Object var1)
    {
        return this.apply((ArtifactVersion)var1);
    }
}
