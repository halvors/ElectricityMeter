package org.halvors.electrometrics.common.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.halvors.electrometrics.common.base.tile.INetworkable;
import org.halvors.electrometrics.common.base.tile.IRotatable;
import org.halvors.electrometrics.common.network.PacketHandler;
import org.halvors.electrometrics.common.network.PacketRequestData;
import org.halvors.electrometrics.common.network.PacketTileEntity;

import java.util.List;

public class TileEntityRotatable extends TileEntity implements INetworkable, IRotatable {
    // The direction this TileEntity's block is facing.
    int facing;

    // The direction this TileEntity's block is facing, client side.
    private int clientFacing;

    TileEntityRotatable(String inventoryName) {
        super(inventoryName);
    }

    @Override
    public void validate() {
        super.validate();

        if (worldObj.isRemote) {
            PacketHandler.sendToServer(new PacketRequestData(this));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTags) {
        super.readFromNBT(nbtTags);

        facing = nbtTags.getInteger("facing");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTags) {
        super.writeToNBT(nbtTags);

        nbtTags.setInteger("facing", facing);
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) throws Exception {
        facing = dataStream.readInt();

        // Check if client is in sync with the server, if not update it.
        if (clientFacing != facing) {
            clientFacing = facing;

            // Update the block's rotation.
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

            // Update potentially connected redstone blocks.
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
        }
    }

    @Override
    public List<Object> getPacketData(List<Object> list) {
        list.add(facing);

        return list;
    }

    @Override
    public boolean canSetFacing(int facing) {
        return true;
    }

    @Override
    public int getFacing() {
        return facing;
    }

    @Override
    public void setFacing(int facing) {
        if (canSetFacing(facing)) {
            this.facing = facing;
        }

        if (!worldObj.isRemote || clientFacing != facing) {
            clientFacing = facing;

            PacketHandler.sendToReceivers(new PacketTileEntity(this), this);
        }
    }
}