package org.bukkit.craftbukkit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EntityAmbient;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityBat;
import net.minecraft.server.EntityBlaze;
import net.minecraft.server.EntityBoat;
import net.minecraft.server.EntityCaveSpider;
import net.minecraft.server.EntityChicken;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityCow;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityCreeper;
import net.minecraft.server.EntityEgg;
import net.minecraft.server.EntityEnderCrystal;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityEnderPearl;
import net.minecraft.server.EntityEnderSignal;
import net.minecraft.server.EntityEnderman;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityFlying;
import net.minecraft.server.EntityGhast;
import net.minecraft.server.EntityGiantZombie;
import net.minecraft.server.EntityGolem;
import net.minecraft.server.EntityHanging;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityIronGolem;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityItemFrame;
import net.minecraft.server.EntityLargeFireball;
import net.minecraft.server.EntityLightning;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMagmaCube;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityMushroomCow;
import net.minecraft.server.EntityOcelot;
import net.minecraft.server.EntityPainting;
import net.minecraft.server.EntityPig;
import net.minecraft.server.EntityPigZombie;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityPotion;
import net.minecraft.server.EntityProjectile;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.EntitySilverfish;
import net.minecraft.server.EntitySkeleton;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntitySmallFireball;
import net.minecraft.server.EntitySnowball;
import net.minecraft.server.EntitySnowman;
import net.minecraft.server.EntitySpider;
import net.minecraft.server.EntitySquid;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.EntityTameableAnimal;
import net.minecraft.server.EntityThrownExpBottle;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.EntityWaterAnimal;
import net.minecraft.server.EntityWeather;
import net.minecraft.server.EntityWitch;
import net.minecraft.server.EntityWither;
import net.minecraft.server.EntityWitherSkull;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.EntityZombie;
import net.minecraft.server.WorldServer;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.metadata.EntityMetadataStore;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class CraftEntity
implements org.bukkit.entity.Entity
{
	protected final CraftServer server;
	protected net.minecraft.server.Entity entity;
	private EntityDamageEvent lastDamageEvent;

	public CraftEntity(CraftServer server, net.minecraft.server.Entity entity)
	{
		this.server = server;
		this.entity = entity;
	}

	public static CraftEntity getEntity(CraftServer server, net.minecraft.server.Entity entity)
	{
		if ((entity instanceof EntityLiving))
		{
			if ((entity instanceof EntityHuman)) {
				if ((entity instanceof EntityPlayer)) return new CraftPlayer(server, (EntityPlayer)entity);
				return new CraftHumanEntity(server, (EntityHuman)entity);
			}
			if ((entity instanceof EntityCreature))
			{
				if ((entity instanceof EntityAnimal)) {
					if ((entity instanceof EntityChicken)) return new CraftChicken(server, (EntityChicken)entity);
					if ((entity instanceof EntityCow)) {
						if ((entity instanceof EntityMushroomCow)) return new CraftMushroomCow(server, (EntityMushroomCow)entity);
						return new CraftCow(server, (EntityCow)entity);
					}
					if ((entity instanceof EntityPig)) return new CraftPig(server, (EntityPig)entity);
					if ((entity instanceof EntityTameableAnimal)) {
						if ((entity instanceof EntityWolf)) return new CraftWolf(server, (EntityWolf)entity);
						if ((entity instanceof EntityOcelot)) return new CraftOcelot(server, (EntityOcelot)entity); 
					}
					else {
						if ((entity instanceof EntitySheep)) return new CraftSheep(server, (EntitySheep)entity);
						return new CraftAnimals(server, (EntityAnimal)entity);
					}
				} else {
					if ((entity instanceof EntityMonster)) {
						if ((entity instanceof EntityZombie)) {
							if ((entity instanceof EntityPigZombie)) return new CraftPigZombie(server, (EntityPigZombie)entity);
							return new CraftZombie(server, (EntityZombie)entity);
						}
						if ((entity instanceof EntityCreeper)) return new CraftCreeper(server, (EntityCreeper)entity);
						if ((entity instanceof EntityEnderman)) return new CraftEnderman(server, (EntityEnderman)entity);
						if ((entity instanceof EntitySilverfish)) return new CraftSilverfish(server, (EntitySilverfish)entity);
						if ((entity instanceof EntityGiantZombie)) return new CraftGiant(server, (EntityGiantZombie)entity);
						if ((entity instanceof EntitySkeleton)) return new CraftSkeleton(server, (EntitySkeleton)entity);
						if ((entity instanceof EntityBlaze)) return new CraftBlaze(server, (EntityBlaze)entity);
						if ((entity instanceof EntityWitch)) return new CraftWitch(server, (EntityWitch)entity);
						if ((entity instanceof EntityWither)) return new CraftWither(server, (EntityWither)entity);
						if ((entity instanceof EntitySpider)) {
							if ((entity instanceof EntityCaveSpider)) return new CraftCaveSpider(server, (EntityCaveSpider)entity);
							return new CraftSpider(server, (EntitySpider)entity);
						}

						return new CraftMonster(server, (EntityMonster)entity);
					}

					if ((entity instanceof EntityWaterAnimal)) {
						if ((entity instanceof EntitySquid)) return new CraftSquid(server, (EntitySquid)entity);
						return new CraftWaterMob(server, (EntityWaterAnimal)entity);
					}
					if ((entity instanceof EntityGolem)) {
						if ((entity instanceof EntitySnowman)) return new CraftSnowman(server, (EntitySnowman)entity);
						if ((entity instanceof EntityIronGolem)) return new CraftIronGolem(server, (EntityIronGolem)entity); 
					}
					else {
						if ((entity instanceof EntityVillager)) return new CraftVillager(server, (EntityVillager)entity);
						return new CraftCreature(server, (EntityCreature)entity);
					}
				}
			} else {
				if ((entity instanceof EntitySlime)) {
					if ((entity instanceof EntityMagmaCube)) return new CraftMagmaCube(server, (EntityMagmaCube)entity);
					return new CraftSlime(server, (EntitySlime)entity);
				}

				if ((entity instanceof EntityFlying)) {
					if ((entity instanceof EntityGhast)) return new CraftGhast(server, (EntityGhast)entity);
					return new CraftFlying(server, (EntityFlying)entity);
				}
				if ((entity instanceof EntityEnderDragon)) {
					return new CraftEnderDragon(server, (EntityEnderDragon)entity);
				}

				if ((entity instanceof EntityAmbient)) {
					if ((entity instanceof EntityBat)) return new CraftBat(server, (EntityBat)entity);
					return new CraftAmbient(server, (EntityAmbient)entity);
				}
				return new CraftLivingEntity(server, (EntityLiving)entity);
			}
		} else {
			if ((entity instanceof EntityComplexPart)) {
				EntityComplexPart part = (EntityComplexPart)entity;
				if ((part.owner instanceof EntityEnderDragon)) return new CraftEnderDragonPart(server, (EntityComplexPart)entity);
				return new CraftComplexPart(server, (EntityComplexPart)entity);
			}
			if ((entity instanceof EntityExperienceOrb)) return new CraftExperienceOrb(server, (EntityExperienceOrb)entity);
			if ((entity instanceof EntityArrow)) return new CraftArrow(server, (EntityArrow)entity);
			if ((entity instanceof EntityBoat)) return new CraftBoat(server, (EntityBoat)entity);
			if ((entity instanceof EntityProjectile)) {
				if ((entity instanceof EntityEgg)) return new CraftEgg(server, (EntityEgg)entity);
				if ((entity instanceof EntitySnowball)) return new CraftSnowball(server, (EntitySnowball)entity);
				if ((entity instanceof EntityPotion)) return new CraftThrownPotion(server, (EntityPotion)entity);
				if ((entity instanceof EntityEnderPearl)) return new CraftEnderPearl(server, (EntityEnderPearl)entity);
				if ((entity instanceof EntityThrownExpBottle)) return new CraftThrownExpBottle(server, (EntityThrownExpBottle)entity); 
			}
			else {
				if ((entity instanceof EntityFallingBlock)) return new CraftFallingSand(server, (EntityFallingBlock)entity);
				if ((entity instanceof EntityFireball)) {
					if ((entity instanceof EntitySmallFireball)) return new CraftSmallFireball(server, (EntitySmallFireball)entity);
					if ((entity instanceof EntityLargeFireball)) return new CraftLargeFireball(server, (EntityLargeFireball)entity);
					if ((entity instanceof EntityWitherSkull)) return new CraftWitherSkull(server, (EntityWitherSkull)entity);
					return new CraftFireball(server, (EntityFireball)entity);
				}
				if ((entity instanceof EntityEnderSignal)) return new CraftEnderSignal(server, (EntityEnderSignal)entity);
				if ((entity instanceof EntityEnderCrystal)) return new CraftEnderCrystal(server, (EntityEnderCrystal)entity);
				if ((entity instanceof EntityFishingHook)) return new CraftFish(server, (EntityFishingHook)entity);
				if ((entity instanceof EntityItem)) return new CraftItem(server, (EntityItem)entity);
				if ((entity instanceof EntityWeather)) {
					if ((entity instanceof EntityLightning)) return new CraftLightningStrike(server, (EntityLightning)entity);
					return new CraftWeather(server, (EntityWeather)entity);
				}
				if ((entity instanceof EntityMinecart)) {
					EntityMinecart mc = (EntityMinecart)entity;
					if (mc.type == CraftMinecart.Type.StorageMinecart.getId()) return new CraftStorageMinecart(server, mc);
					if (mc.type == CraftMinecart.Type.PoweredMinecart.getId()) return new CraftPoweredMinecart(server, mc);
					return new CraftMinecart(server, mc);
				}
				if ((entity instanceof EntityHanging)) {
					if ((entity instanceof EntityPainting)) return new CraftPainting(server, (EntityPainting)entity);
					if ((entity instanceof EntityItemFrame)) return new CraftItemFrame(server, (EntityItemFrame)entity);
					return new CraftHanging(server, (EntityHanging)entity);
				}
				if ((entity instanceof EntityTNTPrimed)) return new CraftTNTPrimed(server, (EntityTNTPrimed)entity); 
			}
		}
		
		return new CraftEntity(server, entity);
		//throw new IllegalArgumentException("Unknown entity");
	}

	public Location getLocation() {
		return new Location(getWorld(), this.entity.locX, this.entity.locY, this.entity.locZ, this.entity.yaw, this.entity.pitch);
	}

	public Vector getVelocity() {
		return new Vector(this.entity.motX, this.entity.motY, this.entity.motZ);
	}

	public void setVelocity(Vector vel) {
		this.entity.motX = vel.getX();
		this.entity.motY = vel.getY();
		this.entity.motZ = vel.getZ();
		this.entity.velocityChanged = true;
	}

	public org.bukkit.World getWorld() {
		return ((WorldServer)this.entity.world).getWorld();
	}

	public boolean teleport(Location location) {
		return teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
		this.entity.world = ((CraftWorld)location.getWorld()).getHandle();
		this.entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		return true;
	}

	public boolean teleport(org.bukkit.entity.Entity destination) {
		return teleport(destination.getLocation());
	}

	public boolean teleport(org.bukkit.entity.Entity destination, PlayerTeleportEvent.TeleportCause cause) {
		return teleport(destination.getLocation(), cause);
	}

	public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z)
	{
		List notchEntityList = this.entity.world.getEntities(this.entity, this.entity.boundingBox.grow(x, y, z));
		List bukkitEntityList = new ArrayList(notchEntityList.size());

		for (Object e : notchEntityList) {
			bukkitEntityList.add(((net.minecraft.server.Entity)e).getBukkitEntity());
		}
		return bukkitEntityList;
	}

	public int getEntityId() {
		return this.entity.id;
	}

	public int getFireTicks() {
		return this.entity.fireTicks;
	}

	public int getMaxFireTicks() {
		return this.entity.maxFireTicks;
	}

	public void setFireTicks(int ticks) {
		this.entity.fireTicks = ticks;
	}

	public void remove() {
		this.entity.dead = true;
	}

	public boolean isDead() {
		return !this.entity.isAlive();
	}

	public boolean isValid() {
		return (this.entity.isAlive()) && (this.entity.valid);
	}

	public Server getServer() {
		return this.server;
	}

	public Vector getMomentum() {
		return getVelocity();
	}

	public void setMomentum(Vector value) {
		setVelocity(value);
	}

	public org.bukkit.entity.Entity getPassenger() {
		return isEmpty() ? null : (CraftEntity)getHandle().passenger.getBukkitEntity();
	}

	public boolean setPassenger(org.bukkit.entity.Entity passenger) {
		if ((passenger instanceof CraftEntity)) {
			((CraftEntity)passenger).getHandle().setPassengerOf(getHandle());
			return true;
		}
		return false;
	}

	public boolean isEmpty()
	{
		return getHandle().passenger == null;
	}

	public boolean eject() {
		if (getHandle().passenger == null) {
			return false;
		}

		getHandle().passenger.setPassengerOf(null);
		return true;
	}

	public float getFallDistance() {
		return getHandle().fallDistance;
	}

	public void setFallDistance(float distance) {
		getHandle().fallDistance = distance;
	}

	public void setLastDamageCause(EntityDamageEvent event) {
		this.lastDamageEvent = event;
	}

	public EntityDamageEvent getLastDamageCause() {
		return this.lastDamageEvent;
	}

	public UUID getUniqueId() {
		return getHandle().uniqueId;
	}

	public int getTicksLived() {
		return getHandle().ticksLived;
	}

	public void setTicksLived(int value) {
		if (value <= 0) {
			throw new IllegalArgumentException("Age must be at least 1 tick");
		}
		getHandle().ticksLived = value;
	}

	public net.minecraft.server.Entity getHandle() {
		return this.entity;
	}

	public void playEffect(EntityEffect type) {
		getHandle().world.broadcastEntityEffect(getHandle(), type.getData());
	}

	public void setHandle(net.minecraft.server.Entity entity) {
		this.entity = entity;
	}

	public String toString()
	{
		return "CraftEntity{id=" + getEntityId() + '}';
	}

	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CraftEntity other = (CraftEntity)obj;
		return getEntityId() == other.getEntityId();
	}

	public int hashCode()
	{
		int hash = 7;
		hash = 29 * hash + getEntityId();
		return hash;
	}

	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		this.server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
	}

	public List<MetadataValue> getMetadata(String metadataKey) {
		return this.server.getEntityMetadata().getMetadata(this, metadataKey);
	}

	public boolean hasMetadata(String metadataKey) {
		return this.server.getEntityMetadata().hasMetadata(this, metadataKey);
	}

	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		this.server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
	}

	public boolean isInsideVehicle() {
		return getHandle().vehicle != null;
	}

	public boolean leaveVehicle() {
		if (getHandle().vehicle == null) {
			return false;
		}

		getHandle().setPassengerOf(null);
		return true;
	}

	public org.bukkit.entity.Entity getVehicle() {
		if (getHandle().vehicle == null) {
			return null;
		}

		return getHandle().vehicle.getBukkitEntity();
	}

	@Override
	public EntityType getType() 
	{
		return EntityType.UNKNOWN;
	}
}