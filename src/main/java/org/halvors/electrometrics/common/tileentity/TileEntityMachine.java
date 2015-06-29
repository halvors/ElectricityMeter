package org.halvors.electrometrics.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.halvors.electrometrics.common.network.PacketHandler;
import org.halvors.electrometrics.common.network.PacketRequestData;

import java.util.ArrayList;

/**
 * This is a basic TileEntity that is meant to be extended by other TileEntities.
 *
 * @author halvors
 */
public class TileEntityMachine extends TileEntity implements IRotatable, INetworkable {
	// The name of this TileEntity.
	private String name;

	// The direction this TileEntity's block is facing.
	private int facing;

	// The direction this TileEntity's block is facing, client side.
	@SideOnly(Side.CLIENT)
	private int clientFacing;

	protected TileEntityMachine(String name) {
		this.name = name;
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

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public ArrayList<Object> getPacketData(ArrayList<Object> data) {
		data.add(facing);

		return data;
	}

	/**
	 * Whether or not this block's orientation can be changed to a specific direction. True by default.
	 */
	@Override
	public boolean canSetFacing(int facing) {
		return true;
	}

	@Override
	public short getFacing() {
		return (short) facing;
	}

	@Override
	public void setFacing(short facing) {
		if (canSetFacing(facing)) {
			this.facing = facing;
		}

		/*
		if(!worldObj.isRemote) {
			markDirty();
		}
		*/
	}

	public String getName() {
		return name;
	}
}
