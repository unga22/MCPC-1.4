package net.minecraft.server;

import java.util.Random;

public class BlockFire extends Block
{
    /** The chance this block will encourage nearby blocks to catch on fire */
    private int[] a = new int[256];

    /**
     * This is an array indexed by block ID the larger the number in the array the more likely a block type will catch
     * fires
     */
    private int[] b = new int[256];

    protected BlockFire(int var1, int var2)
    {
        super(var1, var2, Material.FIRE);
        this.b(true);
    }

    /**
     * This method is called on a block after all other blocks gets already created. You can use it to reference and
     * configure something on the block that needs the others ones.
     */
    public void t_()
    {
        this.a(Block.WOOD.id, 5, 20);
        this.a(Block.WOOD_DOUBLE_STEP.id, 5, 20);
        this.a(Block.WOOD_STEP.id, 5, 20);
        this.a(Block.FENCE.id, 5, 20);
        this.a(Block.WOOD_STAIRS.id, 5, 20);
        this.a(Block.BIRCH_WOOD_STAIRS.id, 5, 20);
        this.a(Block.SPRUCE_WOOD_STAIRS.id, 5, 20);
        this.a(Block.JUNGLE_WOOD_STAIRS.id, 5, 20);
        this.a(Block.LOG.id, 5, 5);
        this.a(Block.LEAVES.id, 30, 60);
        this.a(Block.BOOKSHELF.id, 30, 20);
        this.a(Block.TNT.id, 15, 100);
        this.a(Block.LONG_GRASS.id, 60, 100);
        this.a(Block.WOOL.id, 30, 60);
        this.a(Block.VINE.id, 15, 100);
    }

    /**
     * Sets the burn rate for a block. The larger abilityToCatchFire the more easily it will catch. The larger
     * chanceToEncourageFire the faster it will burn and spread to other blocks. Args: blockID, chanceToEncourageFire,
     * abilityToCatchFire
     */
    private void a(int var1, int var2, int var3)
    {
        this.a[var1] = var2;
        this.b[var1] = var3;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean c()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean b()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int d()
    {
        return 3;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int a(Random var1)
    {
        return 0;
    }

    /**
     * How many world ticks before ticking
     */
    public int r_()
    {
        return 30;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (var1.getGameRules().getBoolean("doFireTick"))
        {
            boolean var6 = var1.getTypeId(var2, var3 - 1, var4) == Block.NETHERRACK.id;

            if (var1.worldProvider instanceof WorldProviderTheEnd && var1.getTypeId(var2, var3 - 1, var4) == Block.BEDROCK.id)
            {
                var6 = true;
            }

            if (!this.canPlace(var1, var2, var3, var4))
            {
                var1.setTypeId(var2, var3, var4, 0);
            }

            if (!var6 && var1.M() && (var1.B(var2, var3, var4) || var1.B(var2 - 1, var3, var4) || var1.B(var2 + 1, var3, var4) || var1.B(var2, var3, var4 - 1) || var1.B(var2, var3, var4 + 1)))
            {
                var1.setTypeId(var2, var3, var4, 0);
            }
            else
            {
                int var7 = var1.getData(var2, var3, var4);

                if (var7 < 15)
                {
                    var1.setRawData(var2, var3, var4, var7 + var5.nextInt(3) / 2);
                }

                var1.a(var2, var3, var4, this.id, this.r_() + var5.nextInt(10));

                if (!var6 && !this.l(var1, var2, var3, var4))
                {
                    if (!var1.t(var2, var3 - 1, var4) || var7 > 3)
                    {
                        var1.setTypeId(var2, var3, var4, 0);
                    }
                }
                else if (!var6 && !this.d(var1, var2, var3 - 1, var4) && var7 == 15 && var5.nextInt(4) == 0)
                {
                    var1.setTypeId(var2, var3, var4, 0);
                }
                else
                {
                    boolean var8 = var1.C(var2, var3, var4);
                    byte var9 = 0;

                    if (var8)
                    {
                        var9 = -50;
                    }

                    this.a(var1, var2 + 1, var3, var4, 300 + var9, var5, var7);
                    this.a(var1, var2 - 1, var3, var4, 300 + var9, var5, var7);
                    this.a(var1, var2, var3 - 1, var4, 250 + var9, var5, var7);
                    this.a(var1, var2, var3 + 1, var4, 250 + var9, var5, var7);
                    this.a(var1, var2, var3, var4 - 1, 300 + var9, var5, var7);
                    this.a(var1, var2, var3, var4 + 1, 300 + var9, var5, var7);

                    for (int var10 = var2 - 1; var10 <= var2 + 1; ++var10)
                    {
                        for (int var11 = var4 - 1; var11 <= var4 + 1; ++var11)
                        {
                            for (int var12 = var3 - 1; var12 <= var3 + 4; ++var12)
                            {
                                if (var10 != var2 || var12 != var3 || var11 != var4)
                                {
                                    int var13 = 100;

                                    if (var12 > var3 + 1)
                                    {
                                        var13 += (var12 - (var3 + 1)) * 100;
                                    }

                                    int var14 = this.n(var1, var10, var12, var11);

                                    if (var14 > 0)
                                    {
                                        int var15 = (var14 + 40 + var1.difficulty * 7) / (var7 + 30);

                                        if (var8)
                                        {
                                            var15 /= 2;
                                        }

                                        if (var15 > 0 && var5.nextInt(var13) <= var15 && (!var1.M() || !var1.B(var10, var12, var11)) && !var1.B(var10 - 1, var12, var4) && !var1.B(var10 + 1, var12, var11) && !var1.B(var10, var12, var11 - 1) && !var1.B(var10, var12, var11 + 1))
                                        {
                                            int var16 = var7 + var5.nextInt(5) / 4;

                                            if (var16 > 15)
                                            {
                                                var16 = 15;
                                            }

                                            var1.setTypeIdAndData(var10, var12, var11, this.id, var16);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean func_82506_l()
    {
        return false;
    }

    private void a(World var1, int var2, int var3, int var4, int var5, Random var6, int var7)
    {
        int var8 = this.b[var1.getTypeId(var2, var3, var4)];

        if (var6.nextInt(var5) < var8)
        {
            boolean var9 = var1.getTypeId(var2, var3, var4) == Block.TNT.id;

            if (var6.nextInt(var7 + 10) < 5 && !var1.B(var2, var3, var4))
            {
                int var10 = var7 + var6.nextInt(5) / 4;

                if (var10 > 15)
                {
                    var10 = 15;
                }

                var1.setTypeIdAndData(var2, var3, var4, this.id, var10);
            }
            else
            {
                var1.setTypeId(var2, var3, var4, 0);
            }

            if (var9)
            {
                Block.TNT.postBreak(var1, var2, var3, var4, 1);
            }
        }
    }

    /**
     * Returns true if at least one block next to this one can burn.
     */
    private boolean l(World var1, int var2, int var3, int var4)
    {
        return this.d(var1, var2 + 1, var3, var4) ? true : (this.d(var1, var2 - 1, var3, var4) ? true : (this.d(var1, var2, var3 - 1, var4) ? true : (this.d(var1, var2, var3 + 1, var4) ? true : (this.d(var1, var2, var3, var4 - 1) ? true : this.d(var1, var2, var3, var4 + 1)))));
    }

    /**
     * Gets the highest chance of a neighbor block encouraging this block to catch fire
     */
    private int n(World var1, int var2, int var3, int var4)
    {
        byte var5 = 0;

        if (!var1.isEmpty(var2, var3, var4))
        {
            return 0;
        }
        else
        {
            int var6 = this.d(var1, var2 + 1, var3, var4, var5);
            var6 = this.d(var1, var2 - 1, var3, var4, var6);
            var6 = this.d(var1, var2, var3 - 1, var4, var6);
            var6 = this.d(var1, var2, var3 + 1, var4, var6);
            var6 = this.d(var1, var2, var3, var4 - 1, var6);
            var6 = this.d(var1, var2, var3, var4 + 1, var6);
            return var6;
        }
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean m()
    {
        return false;
    }

    /**
     * Checks the specified block coordinate to see if it can catch fire.  Args: blockAccess, x, y, z
     */
    public boolean d(IBlockAccess var1, int var2, int var3, int var4)
    {
        return this.a[var1.getTypeId(var2, var3, var4)] > 0;
    }

    /**
     * Retrieves a specified block's chance to encourage their neighbors to burn and if the number is greater than the
     * current number passed in it will return its number instead of the passed in one.  Args: world, x, y, z,
     * curChanceToEncourageFire
     */
    public int d(World var1, int var2, int var3, int var4, int var5)
    {
        int var6 = this.a[var1.getTypeId(var2, var3, var4)];
        return var6 > var5 ? var6 : var5;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var1.t(var2, var3 - 1, var4) || this.l(var1, var2, var3, var4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (!var1.t(var2, var3 - 1, var4) && !this.l(var1, var2, var3, var4))
        {
            var1.setTypeId(var2, var3, var4, 0);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onPlace(World var1, int var2, int var3, int var4)
    {
        if (var1.worldProvider.dimension > 0 || var1.getTypeId(var2, var3 - 1, var4) != Block.OBSIDIAN.id || !Block.PORTAL.i_(var1, var2, var3, var4))
        {
            if (!var1.t(var2, var3 - 1, var4) && !this.l(var1, var2, var3, var4))
            {
                var1.setTypeId(var2, var3, var4, 0);
            }
            else
            {
                var1.a(var2, var3, var4, this.id, this.r_() + var1.random.nextInt(10));
            }
        }
    }
}
