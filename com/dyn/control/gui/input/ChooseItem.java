package com.dyn.control.gui.input;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import com.dyn.control.command.Command;
import com.dyn.control.command.param.Parameter;
import com.dyn.control.gui.GuiServerCommand;
import com.dyn.control.gui.IGuiComponent;

public class ChooseItem implements IGuiComponent {
	protected static RenderItem itemRenderer = new RenderItem();

	private GuiServerCommand gui;
	private FontRenderer fontRenderer;
	private int lastX;
	private int lastY;
	private Command command;
	private Parameter parameter;
	private String inputText;
	Vector<CreativeTabs> tabs;
	List itemsList;
	int tab;
	int highlight;
	int tabHighlight = -1;

	public ChooseItem(GuiServerCommand gui, FontRenderer fontRenderer, Command command, Parameter parameter) {
		this.gui = gui;
		this.fontRenderer = fontRenderer;

		this.command = command;
		this.parameter = parameter;

		this.inputText = "";

		this.tabs = new Vector();
		for (CreativeTabs tab : CreativeTabs.creativeTabArray) {
			if ((tab != CreativeTabs.tabAllSearch) && (tab != CreativeTabs.tabInventory)) {

				this.tabs.add(tab);
			}
		}
		this.itemsList = new ArrayList();
		this.tab = 0;
		changeTab();
	}

	private void changeTab() {
		CreativeTabs current = (CreativeTabs) this.tabs.get(this.tab);
		this.itemsList.clear();

		for (Object obj : Item.itemRegistry) {
			Item item = (Item) obj;
			for (CreativeTabs tab : item.getCreativeTabs()) {
				if (tab == current) {
					item.getSubItems(item, tab, this.itemsList);
				}
			}
		}

		this.highlight = -1;
		this.tabHighlight = -1;
	}

	public int getWidth() {
		return 144;
	}

	public int getHeight() {
		int heightMain = this.itemsList.size() / 9 * 16 + 16 + 5 + (this.fontRenderer.FONT_HEIGHT + 1);
		int heightTabs = this.tabs.size() * 17;
		return Math.max(heightMain, heightTabs);
	}

	public String getTitle() {
		return this.command.getName();
	}

	public String getHelp() {
		return this.command.getHelp();
	}

	public void keyTyped(char c, int i) {
		if (i == 28) {
			select();
		}
		if (i == 200) {
			changeHighlight(-9);
		} else if (i == 208) {
			changeHighlight(9);
		} else if (i == 205) {
			changeHighlight(1);
		} else if (i == 203) {
			changeHighlight(-1);
		} else if (i == 201) {
			this.tab -= 1;
			if (this.tab < 0) {
				this.tab = (this.tabs.size() - 1);
			}
			changeTab();
		} else if (i == 209) {
			this.tab += 1;
			if (this.tab >= this.tabs.size()) {
				this.tab = 0;
			}
			changeTab();
		} else if (this.inputText != null) {
			if ((i == 14) && (this.inputText.length() > 0)) {
				this.inputText = this.inputText.substring(0, this.inputText.length() - 1);
			}
			if (this.inputText.length() < 50) {
				this.inputText += c;
			}
		}
	}

	private void changeHighlight(int increment) {
		this.highlight += increment;
		setHighlight();
	}

	private void setHighlight() {
		if (this.highlight < 0) {
			this.highlight = 0;
		} else if (this.highlight >= this.itemsList.size()) {
			this.highlight = (this.itemsList.size() - 1);
		}

		ItemStack itemstack = (ItemStack) this.itemsList.get(this.highlight);
		String name = GameData.getItemRegistry().getNameForObject(itemstack.getItem());
		if (name.startsWith("minecraft:")) {
			this.inputText = name.substring(10);
		} else {
			this.inputText = name;
		}
		if (itemstack.getHasSubtypes()) {
			this.inputText = (this.inputText + "/" + itemstack.getItemDamage());
		}
	}

	public void mouseMove(int x, int y) {
		this.tabHighlight = -1;
		int totalWidth = getWidth();
		int left = this.lastX + totalWidth + 4;
		if ((x >= left) && (x <= left + 18)) {
			for (int i = 0; i < this.tabs.size(); i++) {
				int top = this.lastY + i * 17;
				if ((y >= top) && (y <= top + 18)) {
					this.tabHighlight = i;
					break;
				}
			}
		}

		for (int i = 0; i < this.itemsList.size(); i++) {
			int posX = i % 9 * 16;
			int posY = i / 9 * 16;
			if ((x >= this.lastX + posX) && (x <= this.lastX + posX + 16)) {
				if ((y >= this.lastY + posY) && (y <= this.lastY + posY + 16)) {
					this.highlight = i;
					setHighlight();
					break;
				}
			}
		}
	}

	public void mouseClick(int x, int y) {
		int totalWidth = getWidth();
		int left = this.lastX + totalWidth + 4;
		if ((x >= left) && (x <= left + 18)) {

			for (int i = 0; i < this.tabs.size(); i++) {
				int top = this.lastY + i * 17;
				if ((y >= top) && (y <= top + 18)) {
					this.tab = i;
					changeTab();
					break;
				}
			}
		}

		for (int i = 0; i < this.itemsList.size(); i++) {
			int posX = i % 9 * 16;
			int posY = i / 9 * 16;
			if ((x >= this.lastX + posX) && (x <= this.lastX + posX + 16)) {
				if ((y >= this.lastY + posY) && (y <= this.lastY + posY + 16)) {
					this.highlight = i;
					setHighlight();
					select();
					break;
				}
			}
		}
	}

	public void select() {
		boolean inputOk = this.parameter.setInput(this.inputText);

		if (inputOk) {
			this.gui.selectCommand(this.command);
		}
	}

	public void renderComponent(int x, int y) {
		this.lastX = x;
		this.lastY = y;
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

		GL11.glDisable(32826);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896);
		GL11.glDisable(2929);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int totalWidth = getWidth();
		for (int i = 0; i < this.tabs.size(); i++) {
			int left = x + totalWidth + 4;
			int top = y + i * 17;
			int tcolour = Integer.MIN_VALUE;
			if (this.tab == i) {
				tcolour = -2131824914;
			} else if (this.tabHighlight == i) {
				tcolour = -2130706433;
			}
			Gui.drawRect(left, top, left + 18, top + 18, tcolour);
			this.gui.drawBorder(left, top, left + 18, top + 18);

			ItemStack itemstack = ((CreativeTabs) this.tabs.get(i)).getIconItemStack();

			GL11.glEnable(2896);
			GL11.glEnable(2929);
			itemRenderer.zLevel += 100.0F;

			itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, textureManager, itemstack, left + 1, top + 1);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, textureManager, itemstack, left + 1, top + 1);

			itemRenderer.zLevel -= 100.0F;
			GL11.glDisable(2896);
			GL11.glDisable(2929);

			if (Tessellator.renderingWorldRenderer) {
				Tessellator.instance.draw();
			}

			if (this.tabHighlight == i) {
				String label = I18n.format(((CreativeTabs) this.tabs.get(i)).getTranslatedTabLabel(), new Object[0]);
				Gui.drawRect(left + 20, top, left + 20 + this.fontRenderer.getStringWidth(label) + 4, top + 10,
						Integer.MIN_VALUE);
				this.fontRenderer.drawString(label, left + 20 + 2, top + 1, 14737632);
			}
		}

		for (int i = 0; i < this.itemsList.size(); i++) {
			ItemStack itemstack = (ItemStack) this.itemsList.get(i);
			int posX = i % 9 * 16;
			int posY = i / 9 * 16;

			if (this.highlight == i) {
				int highlightColour = this.gui.fadeColourAlpha(-2130706433, -1593835521);
				Gui.drawRect(x + posX, y + posY, x + posX + 16, y + posY + 16, highlightColour);
			}

			GL11.glEnable(2896);
			GL11.glEnable(2929);
			itemRenderer.zLevel += 100.0F;

			itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, textureManager, itemstack, x + posX, y + posY);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, textureManager, itemstack, x + posX, y + posY);

			itemRenderer.zLevel -= 100.0F;
			GL11.glDisable(2896);
			GL11.glDisable(2929);

			if (Tessellator.renderingWorldRenderer) {
				Tessellator.instance.draw();
			}
		}

		int itemsHeight = getHeight() - (this.fontRenderer.FONT_HEIGHT + 1);
		int colour = 14737632;
		String prompt = this.parameter.getLabel() + ":";
		int pWidth = this.fontRenderer.getStringWidth(prompt);
		this.fontRenderer.drawString(prompt, x, y + itemsHeight + 1, colour);
		this.fontRenderer.drawString(this.inputText, x + pWidth + 5, y + itemsHeight + 1, colour);

		if (this.gui.updateCounter / 6 % 2 == 0) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(2896);
			Minecraft.getMinecraft().renderEngine.bindTexture(this.gui.icons);

			int width = this.fontRenderer.getStringWidth(this.inputText);
			this.gui.drawTexturedModalRect(x + pWidth + 5 + width, y + itemsHeight + 1, 0, 6, 3, 8);
		}

		if ((this.highlight >= 0) && (this.tabHighlight == -1)) {
			ItemStack itemstack = (ItemStack) this.itemsList.get(this.highlight);
			int posX = x + this.highlight % 9 * 16;
			int posY = y + this.highlight / 9 * 16;

			String label = itemstack.getDisplayName();
			Gui.drawRect(posX + 20, posY, posX + 20 + this.fontRenderer.getStringWidth(label) + 4, posY + 10,
					Integer.MIN_VALUE);
			this.fontRenderer.drawString(label, posX + 20 + 2, posY + 1, 14737632);
		}

		RenderHelper.enableStandardItemLighting();
	}

	public void reset() {
		this.command.clearInput();
	}
}