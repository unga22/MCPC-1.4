package net.minecraftforge.common;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLLog;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.World;
import net.minecraftforge.common.ForgeChunkManager$Type;

public class ForgeChunkManager$Ticket
{
    private String modId;
    private ForgeChunkManager$Type ticketType;
    private LinkedHashSet requestedChunks;
    private NBTTagCompound modData;
    private World world;
    private int maxDepth;
    private String entityClazz;
    private int entityChunkX;
    private int entityChunkZ;
    private Entity entity;
    private String player;

    ForgeChunkManager$Ticket(String var1, ForgeChunkManager$Type var2, World var3)
    {
        this.modId = var1;
        this.ticketType = var2;
        this.world = var3;
        this.maxDepth = ForgeChunkManager.access$000(var1);
        this.requestedChunks = Sets.newLinkedHashSet();
    }

    ForgeChunkManager$Ticket(String var1, ForgeChunkManager$Type var2, World var3, EntityHuman var4)
    {
        this(var1, var2, var3);

        if (var4 != null)
        {
            this.player = var4.getLocalizedName();
        }
        else
        {
            FMLLog.log(Level.SEVERE, "Attempt to create a player ticket without a valid player", new Object[0]);
            throw new RuntimeException();
        }
    }

    public void setChunkListDepth(int var1)
    {
        if (var1 <= ForgeChunkManager.access$000(this.modId) && (var1 > 0 || ForgeChunkManager.access$000(this.modId) <= 0))
        {
            this.maxDepth = var1;
        }
        else
        {
            FMLLog.warning("The mod %s tried to modify the chunk ticket depth to: %d, its allowed maximum is: %d", new Object[] {this.modId, Integer.valueOf(var1), Integer.valueOf(ForgeChunkManager.access$000(this.modId))});
        }
    }

    public int getChunkListDepth()
    {
        return this.maxDepth;
    }

    public int getMaxChunkListDepth()
    {
        return ForgeChunkManager.access$000(this.modId);
    }

    public void bindEntity(Entity var1)
    {
        if (this.ticketType != ForgeChunkManager$Type.ENTITY)
        {
            throw new RuntimeException("Cannot bind an entity to a non-entity ticket");
        }
        else
        {
            this.entity = var1;
        }
    }

    public NBTTagCompound getModData()
    {
        if (this.modData == null)
        {
            this.modData = new NBTTagCompound();
        }

        return this.modData;
    }

    public Entity getEntity()
    {
        return this.entity;
    }

    public boolean isPlayerTicket()
    {
        return this.player != null;
    }

    public String getPlayerName()
    {
        return this.player;
    }

    public String getModId()
    {
        return this.modId;
    }

    public ForgeChunkManager$Type getType()
    {
        return this.ticketType;
    }

    public ImmutableSet getChunkList()
    {
        return ImmutableSet.copyOf(this.requestedChunks);
    }

    static NBTTagCompound access$102(ForgeChunkManager$Ticket var0, NBTTagCompound var1)
    {
        return var0.modData = var1;
    }

    static String access$202(ForgeChunkManager$Ticket var0, String var1)
    {
        return var0.player = var1;
    }

    static String access$300(ForgeChunkManager$Ticket var0)
    {
        return var0.modId;
    }

    static String access$200(ForgeChunkManager$Ticket var0)
    {
        return var0.player;
    }

    static int access$402(ForgeChunkManager$Ticket var0, int var1)
    {
        return var0.entityChunkX = var1;
    }

    static int access$502(ForgeChunkManager$Ticket var0, int var1)
    {
        return var0.entityChunkZ = var1;
    }

    static ForgeChunkManager$Type access$600(ForgeChunkManager$Ticket var0)
    {
        return var0.ticketType;
    }

    static Entity access$700(ForgeChunkManager$Ticket var0)
    {
        return var0.entity;
    }

    static int access$400(ForgeChunkManager$Ticket var0)
    {
        return var0.entityChunkX;
    }

    static int access$500(ForgeChunkManager$Ticket var0)
    {
        return var0.entityChunkZ;
    }

    static World access$800(ForgeChunkManager$Ticket var0)
    {
        return var0.world;
    }

    static LinkedHashSet access$900(ForgeChunkManager$Ticket var0)
    {
        return var0.requestedChunks;
    }

    static int access$1000(ForgeChunkManager$Ticket var0)
    {
        return var0.maxDepth;
    }

    static NBTTagCompound access$100(ForgeChunkManager$Ticket var0)
    {
        return var0.modData;
    }
}
