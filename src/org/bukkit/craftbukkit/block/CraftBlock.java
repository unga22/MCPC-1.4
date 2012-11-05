package org.bukkit.craftbukkit.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.BlockRedstoneWire;
import net.minecraft.server.EnumSkyBlock;
import net.minecraft.server.Item;
import net.minecraftforge.common.EnumHelper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.metadata.BlockMetadataStore;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

public class CraftBlock
  implements org.bukkit.block.Block
{
  private final CraftChunk chunk;
  private final int x;
  private final int y;
  private final int z;
  private static final Biome[] BIOME_MAPPING = new Biome[BiomeBase.biomes.length];
  private static final BiomeBase[] BIOMEBASE_MAPPING = new BiomeBase[BiomeBase.biomes.length];

  public CraftBlock(CraftChunk chunk, int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.chunk = chunk;
  }

  public org.bukkit.World getWorld() {
    return this.chunk.getWorld();
  }

  public Location getLocation() {
    return new Location(getWorld(), this.x, this.y, this.z);
  }

  public BlockVector getVector() {
    return new BlockVector(this.x, this.y, this.z);
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getZ() {
    return this.z;
  }

  public org.bukkit.Chunk getChunk() {
    return this.chunk;
  }

  public void setData(byte data) {
    this.chunk.getHandle().world.setData(this.x, this.y, this.z, data);
  }

  public void setData(byte data, boolean applyPhysics) {
    if (applyPhysics)
      this.chunk.getHandle().world.setData(this.x, this.y, this.z, data);
    else
      this.chunk.getHandle().world.setRawData(this.x, this.y, this.z, data);
  }

  public byte getData()
  {
    return (byte)this.chunk.getHandle().getData(this.x & 0xF, this.y & 0xFF, this.z & 0xF);
  }

  public void setType(org.bukkit.Material type) {
    setTypeId(type.getId());
  }

  public boolean setTypeId(int type) {
    return this.chunk.getHandle().world.setTypeId(this.x, this.y, this.z, type);
  }

  public boolean setTypeId(int type, boolean applyPhysics) {
    if (applyPhysics) {
      return setTypeId(type);
    }
    return this.chunk.getHandle().world.setRawTypeId(this.x, this.y, this.z, type);
  }

  public boolean setTypeIdAndData(int type, byte data, boolean applyPhysics)
  {
    if (applyPhysics) {
      return this.chunk.getHandle().world.setTypeIdAndData(this.x, this.y, this.z, type, data);
    }
    boolean success = this.chunk.getHandle().world.setRawTypeIdAndData(this.x, this.y, this.z, type, data);
    if (success) {
      this.chunk.getHandle().world.notify(this.x, this.y, this.z);
    }
    return success;
  }

  public org.bukkit.Material getType()
  {
    return org.bukkit.Material.getMaterial(getTypeId());
  }

  public int getTypeId() {
    return this.chunk.getHandle().getTypeId(this.x & 0xF, this.y & 0xFF, this.z & 0xF);
  }

  public byte getLightLevel() {
    return (byte)this.chunk.getHandle().world.getLightLevel(this.x, this.y, this.z);
  }

  public byte getLightFromSky() {
    return (byte)this.chunk.getHandle().getBrightness(EnumSkyBlock.SKY, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
  }

  public byte getLightFromBlocks() {
    return (byte)this.chunk.getHandle().getBrightness(EnumSkyBlock.BLOCK, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
  }

  public org.bukkit.block.Block getFace(BlockFace face)
  {
    return getRelative(face, 1);
  }

  public org.bukkit.block.Block getFace(BlockFace face, int distance) {
    return getRelative(face, distance);
  }

  public org.bukkit.block.Block getRelative(int modX, int modY, int modZ) {
    return getWorld().getBlockAt(getX() + modX, getY() + modY, getZ() + modZ);
  }

  public org.bukkit.block.Block getRelative(BlockFace face) {
    return getRelative(face, 1);
  }

  public org.bukkit.block.Block getRelative(BlockFace face, int distance) {
    return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
  }

  public BlockFace getFace(org.bukkit.block.Block block) {
    BlockFace[] values = BlockFace.values();

    for (BlockFace face : values) {
      if ((getX() + face.getModX() == block.getX()) && (getY() + face.getModY() == block.getY()) && (getZ() + face.getModZ() == block.getZ()))
      {
        return face;
      }
    }

    return null;
  }

  public String toString()
  {
    return "CraftBlock{chunk=" + this.chunk + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ",type=" + getType() + ",data=" + getData() + '}';
  }
  
  /**
   * Notch uses a 0-5 to mean DOWN, UP, EAST, WEST, NORTH, SOUTH
   * in that order all over. This method is convenience to convert for us.
   *
   * @return BlockFace the BlockFace represented by this number
   */
  public static BlockFace notchToBlockFace(int notch)
  {
      switch (notch) {
      case 0:
          return BlockFace.DOWN;
      case 1:
          return BlockFace.UP;
      case 2:
          return BlockFace.EAST;
      case 3:
          return BlockFace.WEST;
      case 4:
          return BlockFace.NORTH;
      case 5:
          return BlockFace.SOUTH;
      default:
          return BlockFace.SELF;
      }
  }

  public static int blockFaceToNotch(BlockFace face) 
  {
      switch (face) {
      case DOWN:
          return 0;
      case UP:
          return 1;
      case EAST:
          return 2;
      case WEST:
          return 3;
      case NORTH:
          return 4;
      case SOUTH:
          return 5;
      default:
          return 7; // Good as anything here, but technically invalid
      }
  }

  public BlockState getState()
  {
	  Material material = getType();

	  switch (material) 
	  {
	  case SIGN:
	  case SIGN_POST:
	  case WALL_SIGN:
		  return new CraftSign(this);
	  case CHEST:
		  return new CraftChest(this);
	  case BURNING_FURNACE:
	  case FURNACE:
		  return new CraftFurnace(this);
	  case DISPENSER:
		  return new CraftDispenser(this);
	  case MOB_SPAWNER:
		  return new CraftCreatureSpawner(this);
	  case NOTE_BLOCK:
		  return new CraftNoteBlock(this);
	  case JUKEBOX:
		  return new CraftJukebox(this);
	  case BREWING_STAND:
		  return new CraftBrewingStand(this);
	  default:
		  return new CraftBlockState(this);
	  }
  }


  public Biome getBiome()
  {
    return getWorld().getBiome(this.x, this.z);
  }

  public void setBiome(Biome bio) {
    getWorld().setBiome(this.x, this.z, bio);
  }

  public static Biome biomeBaseToBiome(BiomeBase base) {
    if (base == null) {
      return null;
    }

    return BIOME_MAPPING[base.id];
  }

  public static BiomeBase biomeToBiomeBase(Biome bio) {
    if (bio == null) {
      return null;
    }
    return BIOMEBASE_MAPPING[bio.ordinal()];
  }

  public double getTemperature() {
    return getWorld().getTemperature(this.x, this.z);
  }

  public double getHumidity() {
    return getWorld().getHumidity(this.x, this.z);
  }

  public boolean isBlockPowered() {
    return this.chunk.getHandle().world.isBlockPowered(this.x, this.y, this.z);
  }

  public boolean isBlockIndirectlyPowered() {
    return this.chunk.getHandle().world.isBlockIndirectlyPowered(this.x, this.y, this.z);
  }

  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (!(o instanceof CraftBlock)) return false;
    CraftBlock other = (CraftBlock)o;

    return (this.x == other.x) && (this.y == other.y) && (this.z == other.z) && (getWorld().equals(other.getWorld()));
  }

  public int hashCode()
  {
    return this.y << 24 ^ this.x ^ this.z ^ getWorld().hashCode();
  }

  public boolean isBlockFacePowered(BlockFace face) {
    return this.chunk.getHandle().world.isBlockFacePowered(this.x, this.y, this.z, blockFaceToNotch(face));
  }

  public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
    return this.chunk.getHandle().world.isBlockFaceIndirectlyPowered(this.x, this.y, this.z, blockFaceToNotch(face));
  }

  public int getBlockPower(BlockFace face) {
    int power = 0;
    BlockRedstoneWire wire = (BlockRedstoneWire)net.minecraft.server.Block.REDSTONE_WIRE;
    net.minecraft.server.World world = this.chunk.getHandle().world;
    if (((face == BlockFace.DOWN) || (face == BlockFace.SELF)) && (world.isBlockFacePowered(this.x, this.y - 1, this.z, 0))) power = wire.getPower(world, this.x, this.y - 1, this.z, power);
    if (((face == BlockFace.UP) || (face == BlockFace.SELF)) && (world.isBlockFacePowered(this.x, this.y + 1, this.z, 1))) power = wire.getPower(world, this.x, this.y + 1, this.z, power);
    if (((face == BlockFace.EAST) || (face == BlockFace.SELF)) && (world.isBlockFacePowered(this.x, this.y, this.z - 1, 2))) power = wire.getPower(world, this.x, this.y, this.z - 1, power);
    if (((face == BlockFace.WEST) || (face == BlockFace.SELF)) && (world.isBlockFacePowered(this.x, this.y, this.z + 1, 3))) power = wire.getPower(world, this.x, this.y, this.z + 1, power);
    if (((face == BlockFace.NORTH) || (face == BlockFace.SELF)) && (world.isBlockFacePowered(this.x - 1, this.y, this.z, 4))) power = wire.getPower(world, this.x - 1, this.y, this.z, power);
    if (((face == BlockFace.SOUTH) || (face == BlockFace.SELF)) && (world.isBlockFacePowered(this.x + 1, this.y, this.z, 5))) power = wire.getPower(world, this.x + 1, this.y, this.z, power);
    return face == BlockFace.SELF ? isBlockIndirectlyPowered() ? 15 : 0 : isBlockFaceIndirectlyPowered(face) ? 15 : power > 0 ? power : 0;
  }

  public int getBlockPower() {
    return getBlockPower(BlockFace.SELF);
  }

  public boolean isEmpty() {
    return getType() == org.bukkit.Material.AIR;
  }

  public boolean isLiquid() {
    return (getType() == org.bukkit.Material.WATER) || (getType() == org.bukkit.Material.STATIONARY_WATER) || (getType() == org.bukkit.Material.LAVA) || (getType() == org.bukkit.Material.STATIONARY_LAVA);
  }

  public PistonMoveReaction getPistonMoveReaction() {
    return PistonMoveReaction.getById(net.minecraft.server.Block.byId[getTypeId()].material.getPushReaction());
  }

  private boolean itemCausesDrops(ItemStack item) {
    net.minecraft.server.Block block = net.minecraft.server.Block.byId[getTypeId()];
    Item itemType = item != null ? Item.byId[item.getTypeId()] : null;
    return (block != null) && ((block.material.isAlwaysDestroyable()) || ((itemType != null) && (itemType.canDestroySpecialBlock(block))));
  }

  public boolean breakNaturally()
  {
    net.minecraft.server.Block block = net.minecraft.server.Block.byId[getTypeId()];
    byte data = getData();
    boolean result = false;

    if (block != null) {
      if (block.id == net.minecraft.server.Block.SKULL.id) {
        data = (byte)block.getDropData(this.chunk.getHandle().world, this.x, this.y, this.z);
      }

      block.dropNaturally(this.chunk.getHandle().world, this.x, this.y, this.z, data, 1.0F, 0);
      result = true;
    }

    setTypeId(org.bukkit.Material.AIR.getId());
    return result;
  }

  public boolean breakNaturally(ItemStack item) {
    if (itemCausesDrops(item)) {
      return breakNaturally();
    }
    return setTypeId(org.bukkit.Material.AIR.getId());
  }

  public Collection<ItemStack> getDrops()
  {
    List drops = new ArrayList();

    net.minecraft.server.Block block = net.minecraft.server.Block.byId[getTypeId()];
    if (block != null) {
      byte data = getData();

      int count = block.getDropCount(0, this.chunk.getHandle().world.random);
      for (int i = 0; i < count; i++) {
        int item = block.getDropType(data, this.chunk.getHandle().world.random, 0);
        if (item > 0) {
          drops.add(new ItemStack(item, 1, (short)block.getDropData(data)));
        }
      }
    }
    return drops;
  }

  public Collection<ItemStack> getDrops(ItemStack item) {
    if (itemCausesDrops(item)) {
      return getDrops();
    }
    return Collections.emptyList();
  }

  public void setMetadata(String metadataKey, MetadataValue newMetadataValue)
  {
    this.chunk.getCraftWorld().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
  }

  public List<MetadataValue> getMetadata(String metadataKey) {
    return this.chunk.getCraftWorld().getBlockMetadata().getMetadata(this, metadataKey);
  }

  public boolean hasMetadata(String metadataKey) {
    return this.chunk.getCraftWorld().getBlockMetadata().hasMetadata(this, metadataKey);
  }

  public void removeMetadata(String metadataKey, Plugin owningPlugin) {
    this.chunk.getCraftWorld().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
  }

  static
  {
    BIOME_MAPPING[BiomeBase.SWAMPLAND.id] = Biome.SWAMPLAND;
    BIOME_MAPPING[BiomeBase.FOREST.id] = Biome.FOREST;
    BIOME_MAPPING[BiomeBase.TAIGA.id] = Biome.TAIGA;
    BIOME_MAPPING[BiomeBase.DESERT.id] = Biome.DESERT;
    BIOME_MAPPING[BiomeBase.PLAINS.id] = Biome.PLAINS;
    BIOME_MAPPING[BiomeBase.HELL.id] = Biome.HELL;
    BIOME_MAPPING[BiomeBase.SKY.id] = Biome.SKY;
    BIOME_MAPPING[BiomeBase.RIVER.id] = Biome.RIVER;
    BIOME_MAPPING[BiomeBase.EXTREME_HILLS.id] = Biome.EXTREME_HILLS;
    BIOME_MAPPING[BiomeBase.OCEAN.id] = Biome.OCEAN;
    BIOME_MAPPING[BiomeBase.FROZEN_OCEAN.id] = Biome.FROZEN_OCEAN;
    BIOME_MAPPING[BiomeBase.FROZEN_RIVER.id] = Biome.FROZEN_RIVER;
    BIOME_MAPPING[BiomeBase.ICE_PLAINS.id] = Biome.ICE_PLAINS;
    BIOME_MAPPING[BiomeBase.ICE_MOUNTAINS.id] = Biome.ICE_MOUNTAINS;
    BIOME_MAPPING[BiomeBase.MUSHROOM_ISLAND.id] = Biome.MUSHROOM_ISLAND;
    BIOME_MAPPING[BiomeBase.MUSHROOM_SHORE.id] = Biome.MUSHROOM_SHORE;
    BIOME_MAPPING[BiomeBase.BEACH.id] = Biome.BEACH;
    BIOME_MAPPING[BiomeBase.DESERT_HILLS.id] = Biome.DESERT_HILLS;
    BIOME_MAPPING[BiomeBase.FOREST_HILLS.id] = Biome.FOREST_HILLS;
    BIOME_MAPPING[BiomeBase.TAIGA_HILLS.id] = Biome.TAIGA_HILLS;
    BIOME_MAPPING[BiomeBase.SMALL_MOUNTAINS.id] = Biome.SMALL_MOUNTAINS;
    BIOME_MAPPING[BiomeBase.JUNGLE.id] = Biome.JUNGLE;
    BIOME_MAPPING[BiomeBase.JUNGLE_HILLS.id] = Biome.JUNGLE_HILLS;

    for (int i = 0; i < BIOME_MAPPING.length; i++) {
      if ((BiomeBase.biomes[i] != null) && (BIOME_MAPPING[i] == null)) 
      {
          String name = BiomeBase.biomes[i].y;
          int id = BiomeBase.biomes[i].id;

          System.out.println("Adding biome mapping " + BiomeBase.biomes[i].id + " " + name + " at BiomeBase[" + i + "]");
          EnumHelper.addBukkitBiome(name);
          BIOME_MAPPING[BiomeBase.biomes[i].id] = ((Biome)Enum.valueOf(Biome.class, name));
      }
      if (BIOME_MAPPING[i] != null)
        BIOMEBASE_MAPPING[BIOME_MAPPING[i].ordinal()] = BiomeBase.biomes[i];
    }
  }
}