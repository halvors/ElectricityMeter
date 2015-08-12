package org.halvors.electrometrics.client.gui.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.halvors.electrometrics.client.gui.component.*;
import org.halvors.electrometrics.common.base.tile.ITileOwnable;
import org.halvors.electrometrics.common.tile.machine.TileEntityElectricMachine;
import org.halvors.electrometrics.common.util.LanguageUtils;
import org.halvors.electrometrics.common.util.energy.EnergyUtils;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiElectricMachine extends GuiMachine {
	protected GuiElectricMachine(final TileEntityElectricMachine tileEntity) {
		super(tileEntity);

		if (tileEntity instanceof ITileOwnable) {
			final ITileOwnable tileOwnable = (ITileOwnable) tileEntity;

			components.add(new GuiOwnerInfo(new IInfoHandler() {
				@Override
				public List<String> getInfo() {
					List<String> list = new ArrayList<>();
					list.add(tileOwnable.getOwnerName());

					return list;
				}
			}, this, defaultResource));
		}

		components.add(new GuiEnergyInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo() {
				List<String> list = new ArrayList<>();
				list.add(LanguageUtils.localize("gui.stored") + ": " + EnergyUtils.getEnergyDisplay(tileEntity.getStorage().getEnergyStored()));
				list.add(LanguageUtils.localize("gui.maxOutput") + ": " + EnergyUtils.getEnergyDisplay(tileEntity.getStorage().getMaxExtract()));

				return list;
			}
		}, this, defaultResource));
		components.add(new GuiEnergyUnitType(this, defaultResource));
		components.add(new GuiRedstoneControl<>(this, tileEntity, defaultResource));
	}
}