package net.minecraft.server;

import cpw.mods.fml.common.registry.EntityRegistry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EntityTracker
{
    private final WorldServer world;

    /**
     * List of tracked entities, used for iteration operations on tracked entities.
     */
    private Set b = new HashSet();

    /** Used for identity lookup of tracked entities. */
    private IntHashMap trackedEntities = new IntHashMap();
    private int d;

    public EntityTracker(WorldServer var1)
    {
        this.world = var1;
        this.d = var1.getMinecraftServer().getServerConfigurationManager().a();
    }

    public void addEntity(Entity var1)
    {
        if (!EntityRegistry.instance().tryTrackingEntity(this, var1))
        {
            if (var1 instanceof EntityPlayer)
            {
                this.addEntity(var1, 512, 2);
                EntityPlayer var2 = (EntityPlayer)var1;
                Iterator var3 = this.b.iterator();

                while (var3.hasNext())
                {
                    EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();

                    if (var4.tracker != var2)
                    {
                        var4.updatePlayer(var2);
                    }
                }
            }
            else if (var1 instanceof EntityFishingHook)
            {
                this.addEntity(var1, 64, 5, true);
            }
            else if (var1 instanceof EntityArrow)
            {
                this.addEntity(var1, 64, 20, false);
            }
            else if (var1 instanceof EntitySmallFireball)
            {
                this.addEntity(var1, 64, 10, false);
            }
            else if (var1 instanceof EntityFireball)
            {
                this.addEntity(var1, 64, 10, false);
            }
            else if (var1 instanceof EntitySnowball)
            {
                this.addEntity(var1, 64, 10, true);
            }
            else if (var1 instanceof EntityEnderPearl)
            {
                this.addEntity(var1, 64, 10, true);
            }
            else if (var1 instanceof EntityEnderSignal)
            {
                this.addEntity(var1, 64, 4, true);
            }
            else if (var1 instanceof EntityEgg)
            {
                this.addEntity(var1, 64, 10, true);
            }
            else if (var1 instanceof EntityPotion)
            {
                this.addEntity(var1, 64, 10, true);
            }
            else if (var1 instanceof EntityThrownExpBottle)
            {
                this.addEntity(var1, 64, 10, true);
            }
            else if (var1 instanceof EntityItem)
            {
                this.addEntity(var1, 64, 20, true);
            }
            else if (var1 instanceof EntityMinecart)
            {
                this.addEntity(var1, 80, 3, true);
            }
            else if (var1 instanceof EntityBoat)
            {
                this.addEntity(var1, 80, 3, true);
            }
            else if (var1 instanceof EntitySquid)
            {
                this.addEntity(var1, 64, 3, true);
            }
            else if (var1 instanceof EntityWither)
            {
                this.addEntity(var1, 80, 3, false);
            }
            else if (var1 instanceof EntityBat)
            {
                this.addEntity(var1, 80, 3, false);
            }
            else if (var1 instanceof IAnimal)
            {
                this.addEntity(var1, 80, 3, true);
            }
            else if (var1 instanceof EntityEnderDragon)
            {
                this.addEntity(var1, 160, 3, true);
            }
            else if (var1 instanceof EntityTNTPrimed)
            {
                this.addEntity(var1, 160, 10, true);
            }
            else if (var1 instanceof EntityFallingBlock)
            {
                this.addEntity(var1, 160, 20, true);
            }
            else if (var1 instanceof EntityPainting)
            {
                this.addEntity(var1, 160, Integer.MAX_VALUE, false);
            }
            else if (var1 instanceof EntityExperienceOrb)
            {
                this.addEntity(var1, 160, 20, true);
            }
            else if (var1 instanceof EntityEnderCrystal)
            {
                this.addEntity(var1, 256, Integer.MAX_VALUE, false);
            }
            else if (var1 instanceof EntityItemFrame)
            {
                this.addEntity(var1, 160, Integer.MAX_VALUE, false);
            }
        }
    }

    public void addEntity(Entity var1, int var2, int var3)
    {
        this.addEntity(var1, var2, var3, false);
    }

    public void addEntity(Entity var1, int var2, int var3, boolean var4)
    {
        if (var2 > this.d)
        {
            var2 = this.d;
        }

        if (this.trackedEntities.b(var1.id))
        {
            throw new IllegalStateException("Entity is already tracked!");
        }
        else
        {
            EntityTrackerEntry var5 = new EntityTrackerEntry(var1, var2, var3, var4);
            this.b.add(var5);
            this.trackedEntities.a(var1.id, var5);
            var5.scanPlayers(this.world.players);
        }
    }

    public void untrackEntity(Entity var1)
    {
        if (var1 instanceof EntityPlayer)
        {
            EntityPlayer var2 = (EntityPlayer)var1;
            Iterator var3 = this.b.iterator();

            while (var3.hasNext())
            {
                EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
                var4.a(var2);
            }
        }

        EntityTrackerEntry var5 = (EntityTrackerEntry)this.trackedEntities.d(var1.id);

        if (var5 != null)
        {
            this.b.remove(var5);
            var5.a();
        }
    }

    public void updatePlayers()
    {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.b.iterator();

        while (var2.hasNext())
        {
            EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
            var3.track(this.world.players);

            if (var3.n && var3.tracker instanceof EntityPlayer)
            {
                var1.add((EntityPlayer)var3.tracker);
            }
        }

        var2 = var1.iterator();

        while (var2.hasNext())
        {
            EntityPlayer var7 = (EntityPlayer)var2.next();
            EntityPlayer var4 = var7;
            Iterator var5 = this.b.iterator();

            while (var5.hasNext())
            {
                EntityTrackerEntry var6 = (EntityTrackerEntry)var5.next();

                if (var6.tracker != var4)
                {
                    var6.updatePlayer(var4);
                }
            }
        }
    }

    public void a(Entity var1, Packet var2)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntities.get(var1.id);

        if (var3 != null)
        {
            var3.broadcast(var2);
        }
    }

    public void sendPacketToEntity(Entity var1, Packet var2)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntities.get(var1.id);

        if (var3 != null)
        {
            var3.broadcastIncludingSelf(var2);
        }
    }

    public void untrackPlayer(EntityPlayer var1)
    {
        Iterator var2 = this.b.iterator();

        while (var2.hasNext())
        {
            EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
            var3.clear(var1);
        }
    }
}
