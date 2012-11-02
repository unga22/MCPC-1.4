package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;

public class CreativeModeTab
{
    public static CreativeModeTab[] a = new CreativeModeTab[12];
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
    private String p;
    private boolean q;

    /** Whether to draw the title in the foreground of the creative GUI */
    private boolean r;

    public CreativeModeTab(String var1)
    {
        this(getNextID(), var1);
    }

    public CreativeModeTab(int var1, String var2)
    {
        this.p = "list_items.png";
        this.q = true;
        this.r = true;

        if (var1 >= a.length)
        {
            CreativeModeTab[] var3 = new CreativeModeTab[var1 + 1];

            for (int var4 = 0; var4 < a.length; ++var4)
            {
                var3[var4] = a[var4];
            }

            a = var3;
        }

        this.n = var1;
        this.o = var2;
        a[var1] = this;
    }

    @SideOnly(Side.CLIENT)
    public int a()
    {
        return this.n;
    }

    public CreativeModeTab a(String var1)
    {
        this.p = var1;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String b()
    {
        return this.o;
    }

    @SideOnly(Side.CLIENT)
    public String c()
    {
        return LocaleLanguage.a().b("itemGroup." + this.b());
    }

    @SideOnly(Side.CLIENT)
    public Item d()
    {
        return Item.byId[this.e()];
    }

    @SideOnly(Side.CLIENT)
    public int e()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public String f()
    {
        return this.p;
    }

    @SideOnly(Side.CLIENT)
    public boolean g()
    {
        return this.r;
    }

    public CreativeModeTab h()
    {
        this.r = false;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean i()
    {
        return this.q;
    }

    public CreativeModeTab j()
    {
        this.q = false;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public int k()
    {
        return this.n > 11 ? (this.n - 12) % 10 % 5 : this.n % 6;
    }

    @SideOnly(Side.CLIENT)
    public boolean l()
    {
        return this.n > 11 ? (this.n - 12) % 10 < 5 : this.n < 6;
    }

    @SideOnly(Side.CLIENT)
    public void a(List var1)
    {
        Item[] var2 = Item.byId;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            Item var5 = var2[var4];

            if (var5 != null && var5.w() == this)
            {
                var5.a(var5.id, this, var1);
            }
        }
    }

    public int getTabPage()
    {
        return this.n > 11 ? (this.n - 12) / 10 + 1 : 0;
    }

    public static int getNextID()
    {
        return a.length;
    }

    public ItemStack getIconItemStack()
    {
        return new ItemStack(this.d());
    }
}
