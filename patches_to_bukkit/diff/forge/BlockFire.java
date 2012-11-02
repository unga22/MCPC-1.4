package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;
import net.minecraftforge.common.ForgeDirection;

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
        this.b = Block.blockFlammability;
        this.a = Block.blockFireSpreadSpeed;
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
        Block.setBurnProperties(var1, var2, var3);
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
            Block var6 = Block.byId[var1.getTypeId(var2, var3 - 1, var4)];
            boolean var7 = var6 != null && var6.isFireSource(var1, var2, var3 - 1, var4, var1.getData(var2, var3 - 1, var4), ForgeDirection.UP);

            if (!this.canPlace(var1, var2, var3, var4))
            {
                var1.setTypeId(var2, var3, var4, 0);
            }

            if (!var7 && var1.M() && (var1.B(var2, var3, var4) || var1.B(var2 - 1, var3, var4) || var1.B(var2 + 1, var3, var4) || var1.B(var2, var3, var4 - 1) || var1.B(var2, var3, var4 + 1)))
            {
                var1.setTypeId(var2, var3, var4, 0);
            }
            else
            {
                int var8 = var1.getData(var2, var3, var4);

                if (var8 < 15)
                {
                    var1.setRawData(var2, var3, var4, var8 + var5.nextInt(3) / 2);
                }

                var1.a(var2, var3, var4, this.id, this.r_() + var5.nextInt(10));

                if (!var7 && !this.l(var1, var2, var3, var4))
                {
                    if (!var1.t(var2, var3 - 1, var4) || var8 > 3)
                    {
                        var1.setTypeId(var2, var3, var4, 0);
                    }
                }
                else if (!var7 && !this.canBlockCatchFire(var1, var2, var3 - 1, var4, ForgeDirection.UP) && var8 == 15 && var5.nextInt(4) == 0)
                {
                    var1.setTypeId(var2, var3, var4, 0);
                }
                else
                {
                    boolean var9 = var1.C(var2, var3, var4);
                    byte var10 = 0;

                    if (var9)
                    {
                        var10 = -50;
                    }

                    this.tryToCatchBlockOnFire(var1, var2 + 1, var3, var4, 300 + var10, var5, var8, ForgeDirection.WEST);
                    this.tryToCatchBlockOnFire(var1, var2 - 1, var3, var4, 300 + var10, var5, var8, ForgeDirection.EAST);
                    this.tryToCatchBlockOnFire(var1, var2, var3 - 1, var4, 250 + var10, var5, var8, ForgeDirection.UP);
                    this.tryToCatchBlockOnFire(var1, var2, var3 + 1, var4, 250 + var10, var5, var8, ForgeDirection.DOWN);
                    this.tryToCatchBlockOnFire(var1, var2, var3, var4 - 1, 300 + var10, var5, var8, ForgeDirection.SOUTH);
                    this.tryToCatchBlockOnFire(var1, var2, var3, var4 + 1, 300 + var10, var5, var8, ForgeDirection.NORTH);

                    for (int var11 = var2 - 1; var11 <= var2 + 1; ++var11)
                    {
                        for (int var12 = var4 - 1; var12 <= var4 + 1; ++var12)
                        {
                            for (int var13 = var3 - 1; var13 <= var3 + 4; ++var13)
                            {
                                if (var11 != var2 || var13 != var3 || var12 != var4)
                                {
                                    int var14 = 100;

                                    if (var13 > var3 + 1)
                                    {
                                        var14 += (var13 - (var3 + 1)) * 100;
                                    }

                                    int var15 = this.n(var1, var11, var13, var12);

                                    if (var15 > 0)
                                    {
                                        int var16 = (var15 + 40 + var1.difficulty * 7) / (var8 + 30);

                                        if (var9)
                                        {
                                            var16 /= 2;
                                        }

                                        if (var16 > 0 && var5.nextInt(var14) <= var16 && (!var1.M() || !var1.B(var11, var13, var12)) && !var1.B(var11 - 1, var13, var4) && !var1.B(var11 + 1, var13, var12) && !var1.B(var11, var13, var12 - 1) && !var1.B(var11, var13, var12 + 1))
                                        {
                                            int var17 = var8 + var5.nextInt(5) / 4;

                                            if (var17 > 15)
                                            {
                                                var17 = 15;
                                            }

                                            var1.setTypeIdAndData(var11, var13, var12, this.id, var17);
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

    @Deprecated
    private void a(World var1, int var2, int var3, int var4, int var5, Random var6, int var7)
    {
        this.tryToCatchBlockOnFire(var1, var2, var3, var4, var5, var6, var7, ForgeDirection.UP);
    }

    private void tryToCatchBlockOnFire(World var1, int var2, int var3, int var4, int var5, Random var6, int var7, ForgeDirection var8)
    {
        int var9 = 0;
        Block var10 = Block.byId[var1.getTypeId(var2, var3, var4)];

        if (var10 != null)
        {
            var9 = var10.getFlammability(var1, var2, var3, var4, var1.getData(var2, var3, var4), var8);
        }

        if (var6.nextInt(var5) < var9)
        {
            boolean var11 = var1.getTypeId(var2, var3, var4) == Block.TNT.id;

            if (var6.nextInt(var7 + 10) < 5 && !var1.B(var2, var3, var4))
            {
                int var12 = var7 + var6.nextInt(5) / 4;

                if (var12 > 15)
                {
                    var12 = 15;
                }

                var1.setTypeIdAndData(var2, var3, var4, this.id, var12);
            }
            else
            {
                var1.setTypeId(var2, var3, var4, 0);
            }

            if (var11)
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
        return this.canBlockCatchFire(var1, var2 + 1, var3, var4, ForgeDirection.WEST) || this.canBlockCatchFire(var1, var2 - 1, var3, var4, ForgeDirection.EAST) || this.canBlockCatchFire(var1, var2, var3 - 1, var4, ForgeDirection.UP) || this.canBlockCatchFire(var1, var2, var3 + 1, var4, ForgeDirection.DOWN) || this.canBlockCatchFire(var1, var2, var3, var4 - 1, ForgeDirection.SOUTH) || this.canBlockCatchFire(var1, var2, var3, var4 + 1, ForgeDirection.NORTH);
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
            int var6 = this.getChanceToEncourageFire(var1, var2 + 1, var3, var4, var5, ForgeDirection.WEST);
            var6 = this.getChanceToEncourageFire(var1, var2 - 1, var3, var4, var6, ForgeDirection.EAST);
            var6 = this.getChanceToEncourageFire(var1, var2, var3 - 1, var4, var6, ForgeDirection.UP);
            var6 = this.getChanceToEncourageFire(var1, var2, var3 + 1, var4, var6, ForgeDirection.DOWN);
            var6 = this.getChanceToEncourageFire(var1, var2, var3, var4 - 1, var6, ForgeDirection.SOUTH);
            var6 = this.getChanceToEncourageFire(var1, var2, var3, var4 + 1, var6, ForgeDirection.NORTH);
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

    @Deprecated

    /**
     * Checks the specified block coordinate to see if it can catch fire.  Args: blockAccess, x, y, z
     */
    public boolean d(IBlockAccess var1, int var2, int var3, int var4)
    {
        return this.canBlockCatchFire(var1, var2, var3, var4, ForgeDirection.UP);
    }

    @Deprecated

    /**
     * Retrieves a specified block's chance to encourage their neighbors to burn and if the number is greater than the
     * current number passed in it will return its number instead of the passed in one.  Args: world, x, y, z,
     * curChanceToEncourageFire
     */
    public int d(World var1, int var2, int var3, int var4, int var5)
    {
        return this.getChanceToEncourageFire(var1, var2, var3, var4, var5, ForgeDirection.UP);
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

    @SideOnly(Side.CLIENT)
    public void a(World var1, int var2, int var3, int var4, Random var5)
    {
        if (var5.nextInt(24) == 0)
        {
            var1.func_72980_b((double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), "fire.fire", 1.0F + var5.nextFloat(), var5.nextFloat() * 0.7F + 0.3F);
        }

        int var6;
        float var7;
        float var8;
        float var9;

        if (!var1.t(var2, var3 - 1, var4) && !Block.FIRE.canBlockCatchFire(var1, var2, var3 - 1, var4, ForgeDirection.UP))
        {
            if (Block.FIRE.canBlockCatchFire(var1, var2 - 1, var3, var4, ForgeDirection.EAST))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)var2 + var5.nextFloat() * 0.1F;
                    var8 = (float)var3 + var5.nextFloat();
                    var9 = (float)var4 + var5.nextFloat();
                    var1.addParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.FIRE.canBlockCatchFire(var1, var2 + 1, var3, var4, ForgeDirection.WEST))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)(var2 + 1) - var5.nextFloat() * 0.1F;
                    var8 = (float)var3 + var5.nextFloat();
                    var9 = (float)var4 + var5.nextFloat();
                    var1.addParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.FIRE.canBlockCatchFire(var1, var2, var3, var4 - 1, ForgeDirection.SOUTH))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)var2 + var5.nextFloat();
                    var8 = (float)var3 + var5.nextFloat();
                    var9 = (float)var4 + var5.nextFloat() * 0.1F;
                    var1.addParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.FIRE.canBlockCatchFire(var1, var2, var3, var4 + 1, ForgeDirection.NORTH))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)var2 + var5.nextFloat();
                    var8 = (float)var3 + var5.nextFloat();
                    var9 = (float)(var4 + 1) - var5.nextFloat() * 0.1F;
                    var1.addParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.FIRE.canBlockCatchFire(var1, var2, var3 + 1, var4, ForgeDirection.DOWN))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)var2 + var5.nextFloat();
                    var8 = (float)(var3 + 1) - var5.nextFloat() * 0.1F;
                    var9 = (float)var4 + var5.nextFloat();
                    var1.addParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
        {
            for (var6 = 0; var6 < 3; ++var6)
            {
                var7 = (float)var2 + var5.nextFloat();
                var8 = (float)var3 + var5.nextFloat() * 0.5F + 0.5F;
                var9 = (float)var4 + var5.nextFloat();
                var1.addParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public boolean canBlockCatchFire(IBlockAccess var1, int var2, int var3, int var4, ForgeDirection var5)
    {
        Block var6 = Block.byId[var1.getTypeId(var2, var3, var4)];
        return var6 != null ? var6.isFlammable(var1, var2, var3, var4, var1.getData(var2, var3, var4), var5) : false;
    }

    public int getChanceToEncourageFire(World var1, int var2, int var3, int var4, int var5, ForgeDirection var6)
    {
        int var7 = 0;
        Block var8 = Block.byId[var1.getTypeId(var2, var3, var4)];

        if (var8 != null)
        {
            var7 = var8.getFireSpreadSpeed(var1, var2, var3, var4, var1.getData(var2, var3, var4), var6);
        }

        return var7 > var5 ? var7 : var5;
    }
}
