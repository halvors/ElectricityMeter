package org.halvors.electrometrics.common.util;

import buildcraft.api.tools.IToolWrench;
import cofh.api.item.IToolHammer;
import mekanism.api.IMekWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.halvors.electrometrics.common.base.tile.ITileRedstoneControl;
import org.halvors.electrometrics.common.item.ItemMultimeter;
import org.halvors.electrometrics.common.tile.TileEntity;

public class MachineUtils {
	/**
	 * Whether or not the player has a usable wrench for a block at the coordinates given.
	 * @param player - the player using the wrench
	 * @param x - the x coordinate of the block being wrenched
	 * @param y - the y coordinate of the block being wrenched
	 * @param z - the z coordinate of the block being wrenched
	 * @return if the player can use the wrench
	 */
	public static boolean hasUsableWrench(EntityPlayer player, int x, int y, int z) {
		ItemStack itemStack = player.getCurrentEquippedItem();

		if (itemStack != null) {
			Item item = itemStack.getItem();

			// Check if item is a Buildcraft wrench.
			if (item instanceof IToolWrench) {
				IToolWrench wrench = (IToolWrench) item;

				if (wrench.canWrench(player, x, y, z)) {
					wrench.wrenchUsed(player, x, y, z);

					return true;
				}
			}

			// Check if item is a CoFH wrench.
			if (item instanceof IToolHammer) {
				IToolHammer wrench = (IToolHammer) item;

				return wrench.isUsable(itemStack, player, x, y, z);
			}

			// Check if item is a Mekanism wrench.
			if (item instanceof IMekWrench) {
				IMekWrench wrench = (IMekWrench) item;

				return wrench.canUseWrench(player, x, y, z);
			}
		}

		return false;
	}

	/**
	 * Whether or not a certain TileEntity can function with redstone logic. Illogical to use unless the defined TileEntity implements
	 * ITileRedstoneControl.
	 * @param tileEntity - TileEntity to check
	 * @return if the TileEntity can function with redstone logic
	 */
	public static boolean canFunction(TileEntity tileEntity) {
		if (tileEntity instanceof ITileRedstoneControl) {
			ITileRedstoneControl redstoneControl = (ITileRedstoneControl) tileEntity;

			switch (redstoneControl.getControlType()) {
				case DISABLED:
					return true;

				case HIGH:
					return redstoneControl.isPowered();

				case LOW:
					return !redstoneControl.isPowered();

				case PULSE:
					return redstoneControl.isPowered() && !redstoneControl.wasPowered();
			}
		}

		return false;
	}
}
