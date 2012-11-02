package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BlockChest extends BlockContainer
{
  private Random a = new Random();

  protected BlockChest(int i) {
    super(i, Material.WOOD);
    this.textureId = 26;
  }

  public boolean a() {
    return false;
  }

  public boolean b() {
    return false;
  }

  public int c() {
    return 22;
  }

  public void onPlace(World world, int i, int j, int k) {
    super.onPlace(world, i, j, k);
    b(world, i, j, k);
    int l = world.getTypeId(i, j, k - 1);
    int i1 = world.getTypeId(i, j, k + 1);
    int j1 = world.getTypeId(i - 1, j, k);
    int k1 = world.getTypeId(i + 1, j, k);

    if (l == this.id) {
      b(world, i, j, k - 1);
    }

    if (i1 == this.id) {
      b(world, i, j, k + 1);
    }

    if (j1 == this.id) {
      b(world, i - 1, j, k);
    }

    if (k1 == this.id)
      b(world, i + 1, j, k);
  }

  public void postPlace(World world, int i, int j, int k, EntityLiving entityliving)
  {
    int l = world.getTypeId(i, j, k - 1);
    int i1 = world.getTypeId(i, j, k + 1);
    int j1 = world.getTypeId(i - 1, j, k);
    int k1 = world.getTypeId(i + 1, j, k);
    byte b0 = 0;
    int l1 = MathHelper.floor(entityliving.yaw * 4.0F / 360.0F + 0.5D) & 0x3;

    if (l1 == 0) {
      b0 = 2;
    }

    if (l1 == 1) {
      b0 = 5;
    }

    if (l1 == 2) {
      b0 = 3;
    }

    if (l1 == 3) {
      b0 = 4;
    }

    if ((l != this.id) && (i1 != this.id) && (j1 != this.id) && (k1 != this.id)) {
      world.setData(i, j, k, b0);
    } else {
      if (((l == this.id) || (i1 == this.id)) && ((b0 == 4) || (b0 == 5))) {
        if (l == this.id)
          world.setData(i, j, k - 1, b0);
        else {
          world.setData(i, j, k + 1, b0);
        }

        world.setData(i, j, k, b0);
      }

      if (((j1 == this.id) || (k1 == this.id)) && ((b0 == 2) || (b0 == 3))) {
        if (j1 == this.id)
          world.setData(i - 1, j, k, b0);
        else {
          world.setData(i + 1, j, k, b0);
        }

        world.setData(i, j, k, b0);
      }
    }
  }

  public void b(World world, int i, int j, int k) {
    if (!world.isStatic) {
      int l = world.getTypeId(i, j, k - 1);
      int i1 = world.getTypeId(i, j, k + 1);
      int j1 = world.getTypeId(i - 1, j, k);
      int k1 = world.getTypeId(i + 1, j, k);
      boolean flag = true;
      byte b0;
      if ((l != this.id) && (i1 != this.id)) {
        if ((j1 != this.id) && (k1 != this.id)) {
          byte b0 = 3;
          if ((Block.n[l] != 0) && (Block.n[i1] == 0)) {
            b0 = 3;
          }

          if ((Block.n[i1] != 0) && (Block.n[l] == 0)) {
            b0 = 2;
          }

          if ((Block.n[j1] != 0) && (Block.n[k1] == 0)) {
            b0 = 5;
          }

          if ((Block.n[k1] != 0) && (Block.n[j1] == 0))
            b0 = 4;
        }
        else {
          int l1 = world.getTypeId(j1 == this.id ? i - 1 : i + 1, j, k - 1);
          int i2 = world.getTypeId(j1 == this.id ? i - 1 : i + 1, j, k + 1);
          byte b0 = 3;
          boolean flag1 = true;
          int j2;
          int j2;
          if (j1 == this.id)
            j2 = world.getData(i - 1, j, k);
          else {
            j2 = world.getData(i + 1, j, k);
          }

          if (j2 == 2) {
            b0 = 2;
          }

          if (((Block.n[l] != 0) || (Block.n[l1] != 0)) && (Block.n[i1] == 0) && (Block.n[i2] == 0)) {
            b0 = 3;
          }

          if (((Block.n[i1] != 0) || (Block.n[i2] != 0)) && (Block.n[l] == 0) && (Block.n[l1] == 0))
            b0 = 2;
        }
      }
      else {
        int l1 = world.getTypeId(i - 1, j, l == this.id ? k - 1 : k + 1);
        int i2 = world.getTypeId(i + 1, j, l == this.id ? k - 1 : k + 1);
        b0 = 5;
        boolean flag1 = true;
        int j2;
        int j2;
        if (l == this.id)
          j2 = world.getData(i, j, k - 1);
        else {
          j2 = world.getData(i, j, k + 1);
        }

        if (j2 == 4) {
          b0 = 4;
        }

        if (((Block.n[j1] != 0) || (Block.n[l1] != 0)) && (Block.n[k1] == 0) && (Block.n[i2] == 0)) {
          b0 = 5;
        }

        if (((Block.n[k1] != 0) || (Block.n[i2] != 0)) && (Block.n[j1] == 0) && (Block.n[l1] == 0)) {
          b0 = 4;
        }
      }

      world.setData(i, j, k, b0);
    }
  }

  public int a(int i) {
    return i == 3 ? this.textureId + 1 : i == 0 ? this.textureId - 1 : i == 1 ? this.textureId - 1 : this.textureId;
  }

  public boolean canPlace(World world, int i, int j, int k) {
    int l = 0;

    if (world.getTypeId(i - 1, j, k) == this.id) {
      l++;
    }

    if (world.getTypeId(i + 1, j, k) == this.id) {
      l++;
    }

    if (world.getTypeId(i, j, k - 1) == this.id) {
      l++;
    }

    if (world.getTypeId(i, j, k + 1) == this.id) {
      l++;
    }

    return l <= 1;
  }

  private boolean g(World world, int i, int j, int k) {
    return world.getTypeId(i, j, k) == this.id;
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    super.doPhysics(world, i, j, k, l);
    TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(i, j, k);

    if (tileentitychest != null)
      tileentitychest.h();
  }

  public void remove(World world, int i, int j, int k)
  {
    TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(i, j, k);

    if (tileentitychest != null) {
      for (int l = 0; l < tileentitychest.getSize(); l++) {
        ItemStack itemstack = tileentitychest.getItem(l);

        if (itemstack != null) {
          float f = this.a.nextFloat() * 0.8F + 0.1F;
          float f1 = this.a.nextFloat() * 0.8F + 0.1F;
          EntityItem entityitem;
          for (float f2 = this.a.nextFloat() * 0.8F + 0.1F; itemstack.count > 0; world.addEntity(entityitem)) {
            int i1 = this.a.nextInt(21) + 10;

            if (i1 > itemstack.count) {
              i1 = itemstack.count;
            }

            itemstack.count -= i1;
            entityitem = new EntityItem(world, i + f, j + f1, k + f2, new ItemStack(itemstack.id, i1, itemstack.getData()));
            float f3 = 0.05F;

            entityitem.motX = (float)this.a.nextGaussian() * f3;
            entityitem.motY = (float)this.a.nextGaussian() * f3 + 0.2F;
            entityitem.motZ = (float)this.a.nextGaussian() * f3;
            if (itemstack.hasTag()) {
              entityitem.itemStack.setTag((NBTTagCompound)itemstack.getTag().clone());
            }
          }
        }
      }
    }

    super.remove(world, i, j, k);
  }

  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    Object object = (TileEntityChest)world.getTileEntity(i, j, k);

    if (object == null) {
      return true;
    }
    if (world.isBlockSolidOnSide(i, j + 1, k, 0))
      return true;
    if ((world.getTypeId(i - 1, j, k) == this.id) && ((world.isBlockSolidOnSide(i - 1, j + 1, k, 0)) || (h(world, i - 1, j, k))))
      return true;
    if ((world.getTypeId(i + 1, j, k) == this.id) && ((world.isBlockSolidOnSide(i + 1, j + 1, k, 0)) || (h(world, i - 1, j, k))))
      return true;
    if ((world.getTypeId(i, j, k - 1) == this.id) && ((world.isBlockSolidOnSide(i, j + 1, k - 1, 0)) || (h(world, i - 1, j, k))))
      return true;
    if ((world.getTypeId(i, j, k + 1) == this.id) && ((world.isBlockSolidOnSide(i, j + 1, k + 1, 0)) || (h(world, i - 1, j, k)))) {
      return true;
    }
    if (world.getTypeId(i - 1, j, k) == this.id) {
      object = new InventoryLargeChest("Large chest", (TileEntityChest)world.getTileEntity(i - 1, j, k), (IInventory)object);
    }

    if (world.getTypeId(i + 1, j, k) == this.id) {
      object = new InventoryLargeChest("Large chest", (IInventory)object, (TileEntityChest)world.getTileEntity(i + 1, j, k));
    }

    if (world.getTypeId(i, j, k - 1) == this.id) {
      object = new InventoryLargeChest("Large chest", (TileEntityChest)world.getTileEntity(i, j, k - 1), (IInventory)object);
    }

    if (world.getTypeId(i, j, k + 1) == this.id) {
      object = new InventoryLargeChest("Large chest", (IInventory)object, (TileEntityChest)world.getTileEntity(i, j, k + 1));
    }

    if (world.isStatic) {
      return true;
    }
    entityhuman.openContainer((IInventory)object);
    return true;
  }

  public TileEntity a_()
  {
    return new TileEntityChest();
  }

  private static boolean h(World world, int i, int j, int k) {
    Iterator iterator = world.a(EntityOcelot.class, AxisAlignedBB.b(i, j + 1, k, i + 1, j + 2, k + 1)).iterator();
    EntityOcelot entityocelot;
    do
    {
      if (!iterator.hasNext()) {
        return false;
      }

      Entity entity = (Entity)iterator.next();

      entityocelot = (EntityOcelot)entity;
    }while (!entityocelot.isSitting());

    return true;
  }
}

