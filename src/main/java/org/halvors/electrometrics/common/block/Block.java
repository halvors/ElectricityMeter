package org.halvors.electrometrics.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.halvors.electrometrics.Electrometrics;

/**
 * This is a basic Block that is meant to be extended by other Blocks.
 *
 * @author halvors
 */
public class Block extends BlockContainer {
	protected final String name;

	protected Block(String name, Material material) {
		super(material);

		this.name = name;

		setUnlocalizedName(name);
		setCreativeTab(Electrometrics.getTab());
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return null;
	}
}