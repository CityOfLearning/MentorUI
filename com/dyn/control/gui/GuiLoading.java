package com.dyn.control.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.resources.I18n;
import com.dyn.control.ServerCommands;

public class GuiLoading extends GuiChat {
	ServerCommands scg;
	String message;
	int progress;
	boolean waiting;
	int updateCounter;
	int progStart;
	int progEnd;

	public GuiLoading(ServerCommands scg) {
		this.scg = scg;

		this.message = I18n.format("gui.initialising", new Object[0]);
		this.progress = -1;
		this.waiting = false;
		this.progStart = 0;
		this.progEnd = 10;
	}

	public void updateScreen() {
		this.updateCounter += 1;
	}

	public void drawScreen(int par1, int par2, float par3) {
		if (this.fontRendererObj == null) {
			return;
		}

		String text = I18n.format("gui.statusmessage", new Object[] { this.message });

		int titleHeight = this.fontRendererObj.FONT_HEIGHT + 4;
		int titleWidth = this.fontRendererObj.getStringWidth(text) + 4;
		int top = this.height - (25 + titleHeight);
		int right = 5 + titleWidth;
		int bottom = this.height - 25;

		drawRect(5, top, right, bottom, Integer.MIN_VALUE);
		this.fontRendererObj.drawString(text, 7, top + 2, 14737632);

		if (this.progress == -1) {

			this.progStart += 2;
			this.progEnd += 2;
			if (this.progEnd > 100) {
				this.progStart = 0;
				this.progEnd = 10;
			}

			drawRect(5, bottom, right, bottom + 3, Integer.MIN_VALUE);
			drawRect((int) (5.0F + titleWidth * (this.progStart / 100.0F)), bottom,
					(int) (5.0F + titleWidth * (this.progEnd / 100.0F)), bottom + 3, -1863389458);
		} else {
			drawRect(5, bottom, right, bottom + 3, Integer.MIN_VALUE);
			drawRect(5, bottom, (int) (5.0F + titleWidth * (this.progress / 100.0F)), bottom + 3, -1863389458);
		}
	}

	public void requestCommand(String cmd) {
		Minecraft.getMinecraft().getNetHandler().addToSendQueue(
				new net.minecraft.network.play.client.C14PacketTabComplete(ServerCommands.commandPrefix + cmd));
		this.waiting = true;
	}

	public void func_146406_a(String[] list) {
		for (String item : list) {
			this.scg.commandFound(item.replace(ServerCommands.commandPrefix, ""));
		}
		this.waiting = false;
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void setStatus(String message, int progress) {
		this.message = message;
		this.progress = progress;
	}

	public boolean isWaiting() {
		return this.waiting;
	}
}