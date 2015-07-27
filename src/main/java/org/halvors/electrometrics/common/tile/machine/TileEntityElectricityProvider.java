package org.halvors.electrometrics.common.tile.machine;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.util.ForgeDirection;
import org.halvors.electrometrics.common.base.MachineType;
import org.halvors.electrometrics.common.util.MachineUtils;

import java.util.EnumSet;

/**
 * When extended, this makes a TileEntity able to provide electricity.
 *
 * @author halvors
 */
public abstract class TileEntityElectricityProvider extends TileEntityElectricityReceiver implements IEnergyProvider {
	protected TileEntityElectricityProvider(MachineType machineType, int maxEnergy) {
		super(machineType, maxEnergy);
	}

	protected TileEntityElectricityProvider(MachineType machineType, int maxEnergy, int maxTransfer) {
		super(machineType, maxEnergy, maxTransfer);
	}

	protected TileEntityElectricityProvider(MachineType machineType, int maxEnergy, int maxReceive, int maxExtract) {
		super(machineType, maxEnergy, maxReceive, maxExtract);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote && MachineUtils.canFunction(this)) {
			if (storage.getEnergyStored() > 0) {
				transferEnergy();
			}
		}
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (getExtractingSides().contains(from)) {
			return storage.extractEnergy(maxExtract, simulate);
		}

		return 0;
	}

	@Override
	protected EnumSet<ForgeDirection> getReceivingSides() {
		EnumSet<ForgeDirection> directions = EnumSet.allOf(ForgeDirection.class);
		directions.removeAll(getExtractingSides());
		directions.remove(ForgeDirection.UNKNOWN);

		return directions;
	}

	@Override
	protected EnumSet<ForgeDirection> getExtractingSides() {
		return EnumSet.of(ForgeDirection.getOrientation(facing));
	}

	/**
	 * Transfer energy to any blocks demanding energy that are connected to
	 * this one.
	 */
	private void transferEnergy() {
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			net.minecraft.tileentity.TileEntity tileEntity = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);

			if (tileEntity instanceof IEnergyReceiver) {
				IEnergyReceiver receiver = (IEnergyReceiver) tileEntity;

				extractEnergy(direction.getOpposite(), receiver.receiveEnergy(direction.getOpposite(), storage.getEnergyStored(), false), false);
			}
		}
	}
}