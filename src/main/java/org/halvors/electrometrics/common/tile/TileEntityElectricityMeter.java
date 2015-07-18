package org.halvors.electrometrics.common.tile;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import org.halvors.electrometrics.common.base.MachineType;
import org.halvors.electrometrics.common.base.Tier;
import org.halvors.electrometrics.common.base.tile.*;
import org.halvors.electrometrics.common.util.PlayerUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * This is the TileEntity of the Electricity Meter which provides a simple way to keep count of the electricity you use.
 * Especially suitable for pay-by-use applications where a player buys electricity from another player on multiplayer worlds.
 *
 * @author halvors
 */
public class TileEntityElectricityMeter extends TileEntityElectricityProvider implements INetworkable, IActiveState, IOwnable, IRedstoneControl {
    // Whether or not this TileEntity's block is in it's active state.
    private boolean isActive;

    // The client's current active state.
    @SideOnly(Side.CLIENT)
    private boolean clientIsActive;

	// The UUID of the player owning this.
	private UUID ownerUUID;

	// The name of the player owning this.
	private String ownerName;

	// The current RedstoneControlType of this TileEntity.
	private RedstoneControlType redstoneControlType = RedstoneControlType.DISABLED;

    // The tier of this TileEntity.
	private Tier.ElectricityMeter electricityMeterTier = Tier.ElectricityMeter.BASIC;

	// The amount of energy that has passed thru.
	private double electricityCount;

	public TileEntityElectricityMeter(MachineType machineType) {
		super(machineType, Tier.ElectricityMeter.getFromMachineType(machineType).getMaxEnergy(), Tier.ElectricityMeter.getFromMachineType(machineType).getMaxTransfer());

		electricityMeterTier = Tier.ElectricityMeter.getFromMachineType(machineType);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags) {
		super.readFromNBT(nbtTags);

        isActive = nbtTags.getBoolean("isActive");

		/*
		if (nbtTags.hasKey("ownerUUIDM")) {
			ownerUUID = new UUID(nbtTags.getLong("ownerUUIDM"), nbtTags.getLong("ownerUUIDL"));

			System.out.println("ownerUUID is: " + ownerUUID.toString());
			System.out.println("ownerName is: " + ownerName);
		} else {
			System.out.println("Owner key don't exists.");
		}
		*/

		if (nbtTags.hasKey("ownerName")) {
			ownerName = nbtTags.getString("ownerName");
		}

		redstoneControlType = RedstoneControlType.values()[nbtTags.getInteger("redstoneControlType")];
		electricityMeterTier = Tier.ElectricityMeter.values()[nbtTags.getInteger("tier")];
		electricityCount = nbtTags.getDouble("electricityCount");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTags) {
		super.writeToNBT(nbtTags);

        nbtTags.setBoolean("isActive", isActive);

		/*
		if (ownerUUID != null) {
			nbtTags.setLong("ownerUUIDM", ownerUUID.getMostSignificantBits());
			nbtTags.setLong("ownerUUIDL", ownerUUID.getLeastSignificantBits());


			System.out.println("ownerUUID is: " + ownerUUID.toString());
			System.out.println("ownerName is: " + ownerName);
		} else {
			System.out.println("Owner is null.");
		}
		*/

		if (ownerName != null) {
			nbtTags.setString("ownerName", ownerName);
		}

        nbtTags.setInteger("redstoneControlType", redstoneControlType.ordinal());
		nbtTags.setInteger("tier", electricityMeterTier.ordinal());
		nbtTags.setDouble("electricityCount", electricityCount);
	}

	@Override
	public void handlePacketData(ByteBuf dataStream) throws Exception {
		super.handlePacketData(dataStream);

        isActive = dataStream.readBoolean();

		/*
		String ownerUUIDString = ByteBufUtils.readUTF8String(dataStream);

		if (!ownerUUIDString.equals("")) {
			ownerUUID = UUID.fromString(ownerUUIDString);

			System.out.println("ownerUUID is: " + ownerUUID.toString());
			System.out.println("ownerName is: " + ownerName);
		}
		*/

		ownerName = ByteBufUtils.readUTF8String(dataStream);
		redstoneControlType = RedstoneControlType.values()[dataStream.readInt()];

		// Check if client is in sync with the server, if not update it.
		if (worldObj.isRemote && clientIsActive != isActive) {
			clientIsActive = isActive;

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		electricityMeterTier = Tier.ElectricityMeter.values()[dataStream.readInt()];
		electricityCount = dataStream.readDouble();
	}

	@Override
	public List<Object> getPacketData(List<Object> list) {
		super.getPacketData(list);

		list.add(isActive);
		//list.add(ownerUUID.toString());
		list.add(ownerName != null ? ownerName : "");
		list.add(redstoneControlType.ordinal());
		list.add(electricityMeterTier.ordinal());
		list.add(electricityCount);

		return list;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (getExtractingSides().contains(from)) {
			// Add the amount of energy we're extracting to the counter.
			if (!simulate) {
				electricityCount += maxExtract;
			}
		}

		return super.extractEnergy(from, maxExtract, simulate);
	}

	@Override
	EnumSet<ForgeDirection> getExtractingSides() {
		return EnumSet.of(ForgeDirection.getOrientation(facing).getRotation(ForgeDirection.DOWN));
	}

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

	@Override
	public boolean hasOwner() {
		return ownerUUID != null && ownerName != null;
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return hasOwner() && ownerUUID.equals(player.getPersistentID());
	}

	@Override
	public EntityPlayer getOwner() {
		return PlayerUtils.getPlayerFromUUID(ownerUUID);
	}

	public UUID getOwnerUUID() {
		return ownerUUID;
	}

	@Override
	public String getOwnerName() {
		return ownerName;
	}

	@Override
	public void setOwner(EntityPlayer player) {
		this.ownerUUID = player.getPersistentID();
		this.ownerName = player.getDisplayName();
	}

	@Override
	public RedstoneControlType getControlType() {
		return redstoneControlType;
	}

	@Override
	public void setControlType(RedstoneControlType redstoneControlType) {
		this.redstoneControlType = redstoneControlType;
	}

	@Override
	public boolean isPowered() {
		return isPowered;
	}

	@Override
	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

	@Override
	public boolean wasPowered() {
		return wasPowered;
	}

	@Override
	public boolean canPulse() {
		return false;
	}

	public Tier.ElectricityMeter getTier() {
		return electricityMeterTier;
	}

	public void setTier(Tier.ElectricityMeter electricityMeterTier) {
		this.electricityMeterTier = electricityMeterTier;
	}

	/**
	 * Returns the amount of energy that this block has totally received.
	 */
	public double getElectricityCount() {
		return electricityCount;
	}

	/**
	 * Sets the amount of energy that this block has totally received.
	 */
	public void setElectricityCount(double electricityCount) {
		this.electricityCount = electricityCount;
	}
}
