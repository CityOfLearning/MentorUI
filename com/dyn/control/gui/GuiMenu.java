package com.dyn.control.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;
import com.dyn.control.command.Command;
import com.dyn.control.command.CommandListItem;

public class GuiMenu implements IGuiComponent {
	private GuiServerCommand gui;
	private FontRenderer fontRenderer;
	private int lastX;
	private int lastY;
	private CommandListItem commandList;
	private CommandListItem selected;
	private int highlighted;

	public GuiMenu(GuiServerCommand gui, FontRenderer fontRenderer, CommandListItem commandList) {
		this.gui = gui;
		this.fontRenderer = fontRenderer;

		this.commandList = commandList;
		this.selected = commandList;
		this.highlighted = 0;
	}

	public int getWidth() {
		int menuWidth = 80;
		for (int idx = 0; idx < this.selected.countSubCommands(); idx++) {
			int width = this.fontRenderer.getStringWidth(this.selected.getSubCommand(idx).getName());
			if (this.selected.getSubCommand(idx).hasSubCommands()) {
				width += 10;
			}
			menuWidth = Math.max(menuWidth, width);
		}
		return menuWidth;
	}

	public int getHeight() {
		return (this.fontRenderer.FONT_HEIGHT + 1) * this.selected.countSubCommands();
	}

	public String getTitle() {
		String title = this.selected.getName();
		if (title == null) {
			title = I18n.format("gui.title", new Object[0]);
		}
		return title;
	}

	public String getHelp() {
		String help = null;
		CommandListItem sub = this.selected.getSubCommand(this.highlighted);
		if (sub != null) {
			help = sub.getHelp();
		}
		if ((help == null) || (help.length() == 0)) {
			help = this.selected.getHelp();
		}
		return help;
	}

	public void keyTyped(char c, int i) {
		if (i == 200) {
			up();
		} else if (i == 208) {
			down();
		} else if (i == 205) {
			forward();
		} else if (i == 203) {
			back();
		} else if (i == 28) {
			boolean canGoForward = forward();
			if (!canGoForward) {
				select();
			}
		} else if (i == 46) {
			this.gui.close();
		} else if (i == 35) {
			this.gui.help();
		}
	}

	public void mouseMove(int x, int y) {
		int idx = getMouseIndex(x, y);
		if (idx != -1) {
			this.highlighted = idx;
		}
	}

	private int getMouseIndex(int x, int y) {
		if ((x >= this.lastX) && (x <= this.lastX + getWidth())) {
			for (int idx = 0; idx < this.selected.countSubCommands(); idx++) {
				if ((y >= this.lastY + (this.fontRenderer.FONT_HEIGHT + 1) * idx)
						&& (y <= this.lastY + (this.fontRenderer.FONT_HEIGHT + 1) * (idx + 1))) {

					return idx;
				}
			}
		}
		return -1;
	}

	public void mouseClick(int x, int y) {
		int idx = getMouseIndex(x, y);

		if (idx != -1) {
			this.highlighted = idx;

			boolean canGoForward = forward();
			if (!canGoForward) {
				select();
			}

			this.gui.playClickSound();
		}
	}

	public void up() {
		this.highlighted -= 1;

		if (this.highlighted < 0) {
			this.highlighted = (this.selected.countSubCommands() - 1);
		}
	}

	public void down() {
		this.highlighted += 1;

		if (this.highlighted >= this.selected.countSubCommands()) {
			this.highlighted = 0;
		}
	}

	public boolean forward() {
		if (this.selected.getSubCommand(this.highlighted).hasSubCommands()) {
			this.selected = this.selected.getSubCommand(this.highlighted);
			this.highlighted = 0;
			return true;
		}
		return false;
	}

	public void back() {
		this.selected = this.commandList;
		this.highlighted = 0;
	}

	public void select() {
		if (this.selected.getSubCommand(this.highlighted).getClass() == Command.class) {
			this.gui.selectCommand((Command) this.selected.getSubCommand(this.highlighted));
		}
	}

	public void reset() {
		this.selected = this.commandList;
		this.highlighted = 0;
	}

	public void renderComponent(int x, int y) {
		this.lastX = x;
		this.lastY = y;
		int width = getWidth();

		for (int idx = 0; idx < this.selected.countSubCommands(); idx++) {

			int colour = 14737632;
			if (this.highlighted == idx) {
				colour = 0;
				int highlightColour = this.gui.fadeColourAlpha(-2130706433, -1593835521);

				Gui.drawRect(x - 2, y + (this.fontRenderer.FONT_HEIGHT + 1) * idx, x + width + 2,
						y + (this.fontRenderer.FONT_HEIGHT + 1) * (idx + 1), highlightColour);
			}

			this.fontRenderer.drawString(this.selected.getSubCommand(idx).getName(), x,
					y + (this.fontRenderer.FONT_HEIGHT + 1) * idx + 1, colour);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(2896);
		Minecraft.getMinecraft().renderEngine.bindTexture(this.gui.icons);

		for (int idx = 0; idx < this.selected.countSubCommands(); idx++) {
			if (this.selected.getSubCommand(idx).hasSubCommands()) {
				this.gui.drawTexturedModalRect(x + width - 9, y + (this.fontRenderer.FONT_HEIGHT + 1) * idx + 2, 6, 0,
						9, 6);
			}
		}
	}
}