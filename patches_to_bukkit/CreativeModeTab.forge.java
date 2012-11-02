package net.minecraft.server;

public class CreativeModeTab
{
    public static final CreativeModeTab[] a = new CreativeModeTab[12];
    public static final CreativeModeTab b = new CreativeModeTab1(0, "buildingBlocks");
    public static final CreativeModeTab c = new CreativeModeTab5(1, "decorations");
    public static final CreativeModeTab d = new CreativeModeTab6(2, "redstone");
    public static final CreativeModeTab e = new CreativeModeTab7(3, "transportation");
    public static final CreativeModeTab f = new CreativeModeTab8(4, "misc");
    public static final CreativeModeTab g = (new CreativeModeTab9(5, "search")).a("search.png");
    public static final CreativeModeTab h = new CreativeModeTab10(6, "food");
    public static final CreativeModeTab i = new CreativeModeTab11(7, "tools");
    public static final CreativeModeTab j = new CreativeModeTab12(8, "combat");
    public static final CreativeModeTab k = new CreativeModeTab2(9, "brewing");
    public static final CreativeModeTab l = new CreativeModeTab3(10, "materials");
    public static final CreativeModeTab m = (new CreativeModeTab4(11, "inventory")).a("survival_inv.png").j().h();
    private final int n;
    private final String o;

    /** Texture to use. */
    private String p = "list_items.png";
    private boolean q = true;

    /** Whether to draw the title in the foreground of the creative GUI */
    private boolean r = true;

    public CreativeModeTab(int var1, String var2)
    {
        this.n = var1;
        this.o = var2;
        a[var1] = this;
    }

    public CreativeModeTab a(String var1)
    {
        this.p = var1;
        return this;
    }

    public CreativeModeTab h()
    {
        this.r = false;
        return this;
    }

    public CreativeModeTab j()
    {
        this.q = false;
        return this;
    }
}
