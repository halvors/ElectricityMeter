package org.halvors.electrometrics.common.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.halvors.electrometrics.Electrometrics;
import org.halvors.electrometrics.common.util.Location;

import java.util.ArrayList;

/**
 * This is a packet that provides a location, and is meant to be extended.
 *
 * @author halvors
 */
public class PacketLocation implements IMessage {
    private Location location;

    public PacketLocation() {

    }

    public PacketLocation(Location location) {
        this.location = location;
    }

    @Override
    public void fromBytes(ByteBuf dataStream) {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dataStream.readInt());

        location = new Location(world, dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
    }

    @Override
    public void toBytes(ByteBuf dataStream) {
        dataStream.writeInt(location.getWorld().provider.dimensionId);
        dataStream.writeInt(location.getX());
        dataStream.writeInt(location.getY());
        dataStream.writeInt(location.getZ());
    }

    public Location getLocation() {
        return location;
    }
}