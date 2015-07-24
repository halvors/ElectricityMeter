package org.halvors.electrometrics.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.halvors.electrometrics.common.Reference;
import org.halvors.electrometrics.common.tile.TileEntity;
import org.halvors.electrometrics.common.util.PlayerUtils;
import org.halvors.electrometrics.common.util.location.BlockLocation;
import org.halvors.electrometrics.common.util.location.Range;

/**
 * This is the PacketHandler which is responsible for registering the packet that we are going to use.
 *
 * @author halvors
 */
public class PacketHandler {
	private static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.ID);

	static {
		// Register packets.
		network.registerMessage(PacketConfiguration.PacketConfigurationMessage.class, PacketConfiguration.class, 0, Side.CLIENT);
		network.registerMessage(PacketRequestData.PacketRequestDataMessage.class, PacketRequestData.class, 1, Side.SERVER);
		network.registerMessage(PacketTileEntity.PacketTileEntityMessage.class, PacketTileEntity.class, 2, Side.SERVER);
		network.registerMessage(PacketTileEntity.PacketTileEntityMessage.class, PacketTileEntity.class, 2, Side.CLIENT);
	}

	public static EntityPlayer getPlayer(MessageContext context) {
		return context.side.isClient() ? PlayerUtils.getClientPlayer() : context.getServerHandler().playerEntity;
	}

	public static World getWorld(MessageContext context) {
		return getPlayer(context).worldObj;
	}

	public static void sendTo(IMessage message, EntityPlayerMP player) {
		network.sendTo(message, player);
	}

	public static void sendToAll(IMessage message) {
		network.sendToAll(message);
	}

	public static void sendToAllAround(IMessage message, TargetPoint point) {
		network.sendToAllAround(message, point);
	}

	public static void sendToDimension(IMessage message, int dimensionId) {
		network.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message) {
		network.sendToServer(message);
	}

	/**
	 * Send this message to all players within a defined AABB cuboid.
	 * @param message - the message to send
	 * @param cuboid - the AABB cuboid to send the packet in
	 * @param dimId - the dimension the cuboid is in
	 */
	public static void sendToCuboid(IMessage message, AxisAlignedBB cuboid, int dimId) {
		if (cuboid != null) {
			for (EntityPlayerMP player : PlayerUtils.getPlayers()) {
				if (player.dimension == dimId && cuboid.isVecInside(Vec3.createVectorHelper(player.posX, player.posY, player.posZ))) {
					sendTo(message, player);
				}
			}
		}
	}

	public static void sendToReceivers(IMessage message, Range range) {
		for (EntityPlayerMP player : PlayerUtils.getPlayers()) {
			if (player.dimension == range.getDimensionId() && Range.getChunkRange(player).intersects(range)) {
				sendTo(message, player);
			}
		}
	}

	public static void sendToReceivers(IMessage message, TileEntity tileEntity) {
		sendToReceivers(message, new Range(new BlockLocation(tileEntity)));
	}
}
