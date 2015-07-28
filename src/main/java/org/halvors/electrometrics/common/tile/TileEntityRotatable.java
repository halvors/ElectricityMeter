package org.halvors.electrometrics.common.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.halvors.electrometrics.common.base.tile.ITileNetworkable;
import org.halvors.electrometrics.common.base.tile.ITileRotatable;
import org.halvors.electrometrics.common.network.NetworkHandler;
import org.halvors.electrometrics.common.network.packet.PacketRequestData;
import org.halvors.electrometrics.common.network.packet.PacketTileEntity;

import java.util.List;

public class TileEntityRotatable extends TileEntity implements ITileNetworkable, ITileRotatable {
	// The direction this TileEntity's block is facing.
	protected int facing;

	protected TileEntityRotatable(String inventoryName) {
		super(inventoryName);
	}

	@Override
	public void validate() {
		super.validate();

		if (worldObj.isRemote) {
			NetworkHandler.sendToServer(new PacketRequestData(this));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		facing = nbtTagCompound.getInteger("facing");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		nbtTagCompound.setInteger("facing", facing);
	}

	@Override
	public void handlePacketData(ByteBuf dataStream) throws Exception {
		facing = dataStream.readInt();

		// Re-render the block.
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);

		// Update potentially connected redstone blocks.
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	@Override
	public List<Object> getPacketData(List<Object> objects) {
		objects.add(facing);

		return objects;
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

		if (!worldObj.isRemote) {
			NetworkHandler.sendToReceivers(new PacketTileEntity(this), this);
		}
	}
}