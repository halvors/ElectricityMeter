package org.halvors.electrometrics.client.gui.component;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.halvors.electrometrics.client.gui.IGui;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
abstract class GuiComponentBase {
	static final Minecraft mc = Minecraft.getMinecraft();

	final ResourceLocation resource;
	final IGui gui;
	final ResourceLocation defaultResource;

	GuiComponentBase(ResourceLocation resource, IGui gui, ResourceLocation defaultResource) {
		this.resource = resource;
		this.gui = gui;
		this.defaultResource = defaultResource;
	}

	void displayTooltip(String text, int xAxis, int yAxis) {
		gui.displayTooltip(text, xAxis, yAxis);
	}

	void displayTooltips(List<String> list, int xAxis, int yAxis) {
		gui.displayTooltips(list, xAxis, yAxis);
	}

	void renderScaledText(String text, int x, int y, int color, int maxX) {
		int length = getFontRenderer().getStringWidth(text);

		if (length <= maxX) {
			getFontRenderer().drawString(text, x, y, color);
		} else {
			float scale = (float) maxX / length;
			float reverse = 1 / scale;
			float yAdd = 4 - (scale * 8) / 2F;

			GL11.glPushMatrix();

			GL11.glScalef(scale, scale, scale);
			getFontRenderer().drawString(text, (int) (x * reverse), (int) ((y * reverse) + yAdd), color);

			GL11.glPopMatrix();
		}
	}

	FontRenderer getFontRenderer() {
		return gui.getFontRenderer();
	}
}