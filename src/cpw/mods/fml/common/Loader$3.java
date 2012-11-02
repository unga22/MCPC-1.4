package cpw.mods.fml.common;

class Loader$3 implements ICrashCallable
{
    final Loader this$0;

    Loader$3(Loader var1)
    {
        this.this$0 = var1;
    }

    public String call() throws Exception
    {
        return this.this$0.getCrashInformation();
    }

    public String getLabel()
    {
        return "FML";
    }
}
