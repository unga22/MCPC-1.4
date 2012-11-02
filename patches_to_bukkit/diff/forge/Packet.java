package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Packet
{
    /** Maps packet id to packet class */
    public static IntHashMap l = new IntHashMap();

    /** Maps packet class to packet id */
    private static Map a = new HashMap();

    /** List of the client's packet IDs. */
    private static Set b = new HashSet();

    /** List of the server's packet IDs. */
    private static Set c = new HashSet();

    /** the system time in milliseconds when this packet was created. */
    public final long timestamp = System.currentTimeMillis();
    public static long n;
    public static long o;

    /** Assumed to be sequential by the profiler. */
    public static long p;
    public static long q;

    /**
     * Only true for Packet51MapChunk, Packet52MultiBlockChange, Packet53BlockChange and Packet59ComplexEntity. Used to
     * separate them into a different send queue.
     */
    public boolean lowPriority = false;

    /**
     * Adds a two way mapping between the packet ID and packet class.
     */
    static void a(int var0, boolean var1, boolean var2, Class var3)
    {
        if (l.b(var0))
        {
            throw new IllegalArgumentException("Duplicate packet id:" + var0);
        }
        else if (a.containsKey(var3))
        {
            throw new IllegalArgumentException("Duplicate packet class:" + var3);
        }
        else
        {
            l.a(var0, var3);
            a.put(var3, Integer.valueOf(var0));

            if (var1)
            {
                b.add(Integer.valueOf(var0));
            }

            if (var2)
            {
                c.add(Integer.valueOf(var0));
            }
        }
    }

    /**
     * Returns a new instance of the specified Packet class.
     */
    public static Packet d(int var0)
    {
        try
        {
            Class var1 = (Class)l.get(var0);
            return var1 == null ? null : (Packet)var1.newInstance();
        }
        catch (Exception var2)
        {
            var2.printStackTrace();
            System.out.println("Skipping packet with id " + var0);
            return null;
        }
    }

    /**
     * Writes a byte array to the DataOutputStream
     */
    public static void a(DataOutputStream var0, byte[] var1) throws IOException
    {
        var0.writeShort(var1.length);
        var0.write(var1);
    }

    /**
     * the first short in the stream indicates the number of bytes to read
     */
    public static byte[] b(DataInputStream var0) throws IOException
    {
        short var1 = var0.readShort();

        if (var1 < 0)
        {
            throw new IOException("Key was smaller than nothing!  Weird key!");
        }
        else
        {
            byte[] var2 = new byte[var1];
            var0.read(var2);
            return var2;
        }
    }

    /**
     * Returns the ID of this packet.
     */
    public final int k()
    {
        return ((Integer)a.get(this.getClass())).intValue();
    }

    /**
     * Read a packet, prefixed by its ID, from the data stream.
     */
    public static Packet a(DataInputStream var0, boolean var1, Socket var2) throws IOException
    {
        boolean var3 = false;
        Packet var4 = null;
        int var5 = var2.getSoTimeout();
        int var6;

        try
        {
            var6 = var0.read();

            if (var6 == -1)
            {
                return null;
            }

            if (var1 && !c.contains(Integer.valueOf(var6)) || !var1 && !b.contains(Integer.valueOf(var6)))
            {
                throw new IOException("Bad packet id " + var6);
            }

            var4 = d(var6);

            if (var4 == null)
            {
                throw new IOException("Bad packet id " + var6);
            }

            if (var4 instanceof Packet254GetInfo)
            {
                var2.setSoTimeout(1500);
            }

            var4.a(var0);
            ++n;
            o += (long)var4.a();
        }
        catch (EOFException var8)
        {
            System.out.println("Reached end of stream");
            return null;
        }

        PacketCounter.a(var6, (long)var4.a());
        ++n;
        o += (long)var4.a();
        var2.setSoTimeout(var5);
        return var4;
    }

    /**
     * Writes a packet, prefixed by its ID, to the data stream.
     */
    public static void a(Packet var0, DataOutputStream var1) throws IOException
    {
        var1.write(var0.k());
        var0.a(var1);
        ++p;
        q += (long)var0.a();
    }

    /**
     * Writes a String to the DataOutputStream
     */
    public static void a(String var0, DataOutputStream var1) throws IOException
    {
        if (var0.length() > 32767)
        {
            throw new IOException("String too big");
        }
        else
        {
            var1.writeShort(var0.length());
            var1.writeChars(var0);
        }
    }

    /**
     * Reads a string from a packet
     */
    public static String a(DataInputStream var0, int var1) throws IOException
    {
        short var2 = var0.readShort();

        if (var2 > var1)
        {
            throw new IOException("Received string length longer than maximum allowed (" + var2 + " > " + var1 + ")");
        }
        else if (var2 < 0)
        {
            throw new IOException("Received string length is less than zero! Weird string!");
        }
        else
        {
            StringBuilder var3 = new StringBuilder();

            for (int var4 = 0; var4 < var2; ++var4)
            {
                var3.append(var0.readChar());
            }

            return var3.toString();
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public abstract void a(DataInputStream var1) throws IOException;

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public abstract void a(DataOutputStream var1) throws IOException;

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public abstract void handle(NetHandler var1);

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public abstract int a();

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean e()
    {
        return false;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean a(Packet var1)
    {
        return false;
    }

    /**
     * if this returns false, processPacket is deffered for processReadPackets to handle
     */
    public boolean a_()
    {
        return false;
    }

    public String toString()
    {
        String var1 = this.getClass().getSimpleName();
        return var1;
    }

    /**
     * Reads a ItemStack from the InputStream
     */
    public static ItemStack c(DataInputStream var0) throws IOException
    {
        ItemStack var1 = null;
        short var2 = var0.readShort();

        if (var2 >= 0)
        {
            byte var3 = var0.readByte();
            short var4 = var0.readShort();
            var1 = new ItemStack(var2, var3, var4);
            var1.tag = d(var0);
        }

        return var1;
    }

    /**
     * Writes the ItemStack's ID (short), then size (byte), then damage. (short)
     */
    public static void a(ItemStack var0, DataOutputStream var1) throws IOException
    {
        if (var0 == null)
        {
            var1.writeShort(-1);
        }
        else
        {
            var1.writeShort(var0.id);
            var1.writeByte(var0.count);
            var1.writeShort(var0.getData());
            NBTTagCompound var2 = null;

            if (var0.getItem().n() || var0.getItem().q())
            {
                var2 = var0.tag;
            }

            a(var2, var1);
        }
    }

    /**
     * Reads a compressed NBTTagCompound from the InputStream
     */
    public static NBTTagCompound d(DataInputStream var0) throws IOException
    {
        short var1 = var0.readShort();

        if (var1 < 0)
        {
            return null;
        }
        else
        {
            byte[] var2 = new byte[var1];
            var0.readFully(var2);
            return NBTCompressedStreamTools.a(var2);
        }
    }

    /**
     * Writes a compressed NBTTagCompound to the OutputStream
     */
    protected static void a(NBTTagCompound var0, DataOutputStream var1) throws IOException
    {
        if (var0 == null)
        {
            var1.writeShort(-1);
        }
        else
        {
            byte[] var2 = NBTCompressedStreamTools.a(var0);
            var1.writeShort((short)var2.length);
            var1.write(var2);
        }
    }

    static
    {
        a(0, true, true, Packet0KeepAlive.class);
        a(1, true, true, Packet1Login.class);
        a(2, false, true, Packet2Handshake.class);
        a(3, true, true, Packet3Chat.class);
        a(4, true, false, Packet4UpdateTime.class);
        a(5, true, false, Packet5EntityEquipment.class);
        a(6, true, false, Packet6SpawnPosition.class);
        a(7, false, true, Packet7UseEntity.class);
        a(8, true, false, Packet8UpdateHealth.class);
        a(9, true, true, Packet9Respawn.class);
        a(10, true, true, Packet10Flying.class);
        a(11, true, true, Packet11PlayerPosition.class);
        a(12, true, true, Packet12PlayerLook.class);
        a(13, true, true, Packet13PlayerLookMove.class);
        a(14, false, true, Packet14BlockDig.class);
        a(15, false, true, Packet15Place.class);
        a(16, false, true, Packet16BlockItemSwitch.class);
        a(17, true, false, Packet17EntityLocationAction.class);
        a(18, true, true, Packet18ArmAnimation.class);
        a(19, false, true, Packet19EntityAction.class);
        a(20, true, false, Packet20NamedEntitySpawn.class);
        a(21, true, false, Packet21PickupSpawn.class);
        a(22, true, false, Packet22Collect.class);
        a(23, true, false, Packet23VehicleSpawn.class);
        a(24, true, false, Packet24MobSpawn.class);
        a(25, true, false, Packet25EntityPainting.class);
        a(26, true, false, Packet26AddExpOrb.class);
        a(28, true, false, Packet28EntityVelocity.class);
        a(29, true, false, Packet29DestroyEntity.class);
        a(30, true, false, Packet30Entity.class);
        a(31, true, false, Packet31RelEntityMove.class);
        a(32, true, false, Packet32EntityLook.class);
        a(33, true, false, Packet33RelEntityMoveLook.class);
        a(34, true, false, Packet34EntityTeleport.class);
        a(35, true, false, Packet35EntityHeadRotation.class);
        a(38, true, false, Packet38EntityStatus.class);
        a(39, true, false, Packet39AttachEntity.class);
        a(40, true, false, Packet40EntityMetadata.class);
        a(41, true, false, Packet41MobEffect.class);
        a(42, true, false, Packet42RemoveMobEffect.class);
        a(43, true, false, Packet43SetExperience.class);
        a(51, true, false, Packet51MapChunk.class);
        a(52, true, false, Packet52MultiBlockChange.class);
        a(53, true, false, Packet53BlockChange.class);
        a(54, true, false, Packet54PlayNoteBlock.class);
        a(55, true, false, Packet55BlockBreakAnimation.class);
        a(56, true, false, Packet56MapChunkBulk.class);
        a(60, true, false, Packet60Explosion.class);
        a(61, true, false, Packet61WorldEvent.class);
        a(62, true, false, Packet62NamedSoundEffect.class);
        a(70, true, false, Packet70Bed.class);
        a(71, true, false, Packet71Weather.class);
        a(100, true, false, Packet100OpenWindow.class);
        a(101, true, true, Packet101CloseWindow.class);
        a(102, false, true, Packet102WindowClick.class);
        a(103, true, false, Packet103SetSlot.class);
        a(104, true, false, Packet104WindowItems.class);
        a(105, true, false, Packet105CraftProgressBar.class);
        a(106, true, true, Packet106Transaction.class);
        a(107, true, true, Packet107SetCreativeSlot.class);
        a(108, false, true, Packet108ButtonClick.class);
        a(130, true, true, Packet130UpdateSign.class);
        a(131, true, true, Packet131ItemData.class);
        a(132, true, false, Packet132TileEntityData.class);
        a(200, true, false, Packet200Statistic.class);
        a(201, true, false, Packet201PlayerInfo.class);
        a(202, true, true, Packet202Abilities.class);
        a(203, true, true, Packet203TabComplete.class);
        a(204, false, true, Packet204LocaleAndViewDistance.class);
        a(205, false, true, Packet205ClientCommand.class);
        a(250, true, true, Packet250CustomPayload.class);
        a(252, true, true, Packet252KeyResponse.class);
        a(253, true, false, Packet253KeyRequest.class);
        a(254, false, true, Packet254GetInfo.class);
        a(255, true, true, Packet255KickDisconnect.class);
    }
}
