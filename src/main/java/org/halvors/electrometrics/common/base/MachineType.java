package org.halvors.electrometrics.common.base;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.halvors.electrometrics.Electrometrics;
import org.halvors.electrometrics.client.gui.GuiElectricityMeter;
import org.halvors.electrometrics.common.base.Tier.ElectricityMeterTier;
import org.halvors.electrometrics.common.block.BlockMachine;
import org.halvors.electrometrics.common.tile.TileEntityElectricityMeter;
import org.halvors.electrometrics.common.tile.TileEntityMachine;
import org.halvors.electrometrics.common.util.Utils;

public enum MachineType {
    BASIC_ELECTRICITY_METER("BasicElectricityMeter", 0, TileEntityElectricityMeter.class, GuiElectricityMeter.class),
    ADVANCED_ELECTRICITY_METER("AdvancedElectricityMeter", 1, TileEntityElectricityMeter.class, GuiElectricityMeter.class),
    ELITE_ELECTRICITY_METER("EliteElectricityMeter", 2, TileEntityElectricityMeter.class, GuiElectricityMeter.class),
    ULTIMATE_ELECTRICITY_METER("UltimateElectricityMeter", 3, TileEntityElectricityMeter.class, GuiElectricityMeter.class),
    CREATIVE_ELECTRICITY_METER("CreativeElectricityMeter", 4, TileEntityElectricityMeter.class, GuiElectricityMeter.class);

    private final String name;
    private final int metadata;
    private final Class<? extends TileEntityElectricityMeter> tileEntityClass;
    private final Class<? extends GuiScreen> guiClass;

    MachineType(String name, int metadata, Class<? extends TileEntityElectricityMeter> tileEntityClass, Class<? extends GuiScreen> guiClass) {
        this.name = name;
        this.metadata = metadata;
        this.tileEntityClass = tileEntityClass;
        this.guiClass = guiClass;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return Utils.translate("tile." + name + ".name");
    }

    public int getMetadata() {
        return metadata;
    }

    public TileEntityElectricityMeter getTileEntity() {
        try {
            switch (this) {
                case BASIC_ELECTRICITY_METER:
                case ADVANCED_ELECTRICITY_METER:
                case ELITE_ELECTRICITY_METER:
                case ULTIMATE_ELECTRICITY_METER:
                case CREATIVE_ELECTRICITY_METER:
                    return (TileEntityElectricityMeter) tileEntityClass.getConstructor(new Class[] { String.class, ElectricityMeterTier.class }).newInstance(getLocalizedName(), ElectricityMeterTier.getFromMachineType(this));

                default:
                    return tileEntityClass.newInstance();
            }
        } catch(Exception e) {
            e.printStackTrace();
            Electrometrics.getLogger().error("Unable to indirectly create tile entity.");
        }

        return null;
    }

    public GuiScreen getGui() {
        try {
            return guiClass.getConstructor(TileEntityElectricityMeter.class).newInstance(getTileEntity());
        } catch(Exception e) {
            e.printStackTrace();
            Electrometrics.getLogger().error("Unable to indirectly create gui.");
        }

        return null;
    }

    public static MachineType getType(Block block, int meta) {
        if (block instanceof BlockMachine) {
            for (MachineType type : values()) {
                if (meta == type.getMetadata()) {
                    return type;
                }
            }
        }

        return null;
    }

    public static MachineType getType(ItemStack itemStack) {
        return getType(Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage());
    }

    public ItemStack getItemStack() {
        return new ItemStack(Electrometrics.blockElectricityMeter, 1, metadata);
    }

    public Item getItem() {
        return getItemStack().getItem();
    }
}