package net.minecraft.server;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;

public class BlockDispenser extends BlockContainer
{
    /** Registry for all dispense behaviors. */
    public static final IRegistry a = new RegistryDefault(new DispenseBehaviorItem());
    private Random b = new Random();

    protected BlockDispenser(int var1)
    {
        super(var1, Material.STONE);
        this.textureId = 45;
        this.a(CreativeModeTab.d);
    }

    /**
     * How many world ticks before ticking
     */
    public int r_()
    {
        return 4;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int getDropType(int var1, Random var2, int var3)
    {
        return Block.DISPENSER.id;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onPlace(World var1, int var2, int var3, int var4)
    {
        super.onPlace(var1, var2, var3, var4);
        this.l(var1, var2, var3, var4);
    }

    /**
     * sets Dispenser block direction so that the front faces an non-opaque block; chooses west to be direction if all
     * surrounding blocks are opaque.
     */
    private void l(World var1, int var2, int var3, int var4)
    {
        if (!var1.isStatic)
        {
            int var5 = var1.getTypeId(var2, var3, var4 - 1);
            int var6 = var1.getTypeId(var2, var3, var4 + 1);
            int var7 = var1.getTypeId(var2 - 1, var3, var4);
            int var8 = var1.getTypeId(var2 + 1, var3, var4);
            byte var9 = 3;

            if (Block.q[var5] && !Block.q[var6])
            {
                var9 = 3;
            }

            if (Block.q[var6] && !Block.q[var5])
            {
                var9 = 2;
            }

            if (Block.q[var7] && !Block.q[var8])
            {
                var9 = 5;
            }

            if (Block.q[var8] && !Block.q[var7])
            {
                var9 = 4;
            }

            var1.setData(var2, var3, var4, var9);
        }
    }

    @SideOnly(Side.CLIENT)
    public int d(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        if (var5 == 1)
        {
            return this.textureId + 17;
        }
        else if (var5 == 0)
        {
            return this.textureId + 17;
        }
        else
        {
            int var6 = var1.getData(var2, var3, var4);
            return var5 == var6 ? this.textureId + 1 : this.textureId;
        }
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int a(int var1)
    {
        return var1 == 1 ? this.textureId + 17 : (var1 == 0 ? this.textureId + 17 : (var1 == 3 ? this.textureId + 1 : this.textureId));
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean interact(World var1, int var2, int var3, int var4, EntityHuman var5, int var6, float var7, float var8, float var9)
    {
        if (var1.isStatic)
        {
            return true;
        }
        else
        {
            TileEntityDispenser var10 = (TileEntityDispenser)var1.getTileEntity(var2, var3, var4);

            if (var10 != null)
            {
                var5.openDispenser(var10);
            }

            return true;
        }
    }

    private void func_82526_n(World var1, int var2, int var3, int var4)
    {
        SourceBlock var5 = new SourceBlock(var1, var2, var3, var4);
        TileEntityDispenser var6 = (TileEntityDispenser)var5.func_82619_j();

        if (var6 != null)
        {
            int var7 = var6.func_70361_i();

            if (var7 < 0)
            {
                var1.triggerEffect(1001, var2, var3, var4, 0);
            }
            else
            {
                ItemStack var8 = var6.getItem(var7);
                IDispenseBehavior var9 = (IDispenseBehavior)a.func_82594_a(var8.getItem());

                if (var9 != IDispenseBehavior.a)
                {
                    ItemStack var10 = var9.a(var5, var8);
                    var6.setItem(var7, var10.count == 0 ? null : var10);
                }
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        if (var5 > 0 && Block.byId[var5].isPowerSource())
        {
            boolean var6 = var1.isBlockIndirectlyPowered(var2, var3, var4) || var1.isBlockIndirectlyPowered(var2, var3 + 1, var4);

            if (var6)
            {
                var1.a(var2, var3, var4, this.id, this.r_());
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (!var1.isStatic && (var1.isBlockIndirectlyPowered(var2, var3, var4) || var1.isBlockIndirectlyPowered(var2, var3 + 1, var4)))
        {
            this.func_82526_n(var1, var2, var3, var4);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity a(World var1)
    {
        return new TileEntityDispenser();
    }

    /**
     * Called when the block is placed in the world.
     */
    public void postPlace(World var1, int var2, int var3, int var4, EntityLiving var5)
    {
        int var6 = MathHelper.floor((double)(var5.yaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (var6 == 0)
        {
            var1.setData(var2, var3, var4, 2);
        }

        if (var6 == 1)
        {
            var1.setData(var2, var3, var4, 5);
        }

        if (var6 == 2)
        {
            var1.setData(var2, var3, var4, 3);
        }

        if (var6 == 3)
        {
            var1.setData(var2, var3, var4, 4);
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void remove(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        TileEntityDispenser var7 = (TileEntityDispenser)var1.getTileEntity(var2, var3, var4);

        if (var7 != null)
        {
            for (int var8 = 0; var8 < var7.getSize(); ++var8)
            {
                ItemStack var9 = var7.getItem(var8);

                if (var9 != null)
                {
                    float var10 = this.b.nextFloat() * 0.8F + 0.1F;
                    float var11 = this.b.nextFloat() * 0.8F + 0.1F;
                    float var12 = this.b.nextFloat() * 0.8F + 0.1F;

                    while (var9.count > 0)
                    {
                        int var13 = this.b.nextInt(21) + 10;

                        if (var13 > var9.count)
                        {
                            var13 = var9.count;
                        }

                        var9.count -= var13;
                        EntityItem var14 = new EntityItem(var1, (double)((float)var2 + var10), (double)((float)var3 + var11), (double)((float)var4 + var12), new ItemStack(var9.id, var13, var9.getData()));

                        if (var9.hasTag())
                        {
                            var14.itemStack.setTag((NBTTagCompound)var9.getTag().clone());
                        }

                        float var15 = 0.05F;
                        var14.motX = (double)((float)this.b.nextGaussian() * var15);
                        var14.motY = (double)((float)this.b.nextGaussian() * var15 + 0.2F);
                        var14.motZ = (double)((float)this.b.nextGaussian() * var15);
                        var1.addEntity(var14);
                    }
                }
            }
        }

        super.remove(var1, var2, var3, var4, var5, var6);
    }

    public static IPosition func_82525_a(ISourceBlock var0)
    {
        EnumFacing var1 = EnumFacing.func_82600_a(var0.func_82620_h());
        double var2 = var0.func_82615_a() + 0.6D * (double)var1.func_82601_c();
        double var4 = var0.func_82617_b();
        double var6 = var0.func_82616_c() + 0.6D * (double)var1.func_82599_e();
        return new Position(var2, var4, var6);
    }
}
