package com.dyn.control.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import com.dyn.control.ServerCommands;
import com.dyn.control.command.Command;
import com.dyn.control.command.param.ItemMetaParameter;
import com.dyn.control.command.param.ItemParameter;
import com.dyn.control.command.param.Parameter;
import com.dyn.control.command.param.Parameter.ParameterType;
import com.dyn.control.gui.input.ChooseItem;
import com.dyn.control.gui.input.ChooseList;
import com.dyn.control.gui.input.ChoosePlayer;
import com.dyn.control.gui.input.EnterIPAddress;
import com.dyn.control.gui.input.EnterNumber;
import com.dyn.control.gui.input.EnterText;

public class GuiServerCommand extends GuiScreen {
	private static final int leftOffset = 5;
	private static final int bottomOffset = 25;
	private static final int xPadding = 5;
	private static final int yPadding = 5;
	public int updateCounter;
	private int lastMouseButton = -1;
	private ServerCommands mod;
	private GuiMenu menu;
	private IGuiComponent currentComponent;
	public ResourceLocation icons;
	private int showHelp;

	public GuiServerCommand(ServerCommands mod) {
		this.mod = mod;
		//we don't need the icons
		//this.icons = new ResourceLocation(mod.getName().toLowerCase() + ":textures/gui/icons.png");
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);

		this.menu = new GuiMenu(this, this.fontRendererObj, this.mod.commandList);
		this.currentComponent = this.menu;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void updateScreen() {
		this.updateCounter += 1;
	}

	protected void keyTyped(char c, int i) {
		if (i == 1) {
			close();
		} else {
			if ((!this.mod.IsReady()) || (this.mod.commandList == null)) {
				return;
			}

			this.currentComponent.keyTyped(c, i);
		}
	}

	public void handleMouseInput() {
		if ((!this.mod.IsReady()) || (this.mod.commandList == null)) {
			return;
		}

		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int k = Mouse.getEventButton();

		if ((Minecraft.isRunningOnMac) && (k == 0) && ((Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157)))) {
			k = 1;
		}

		if (k == -1) {
			int ccWidth = this.currentComponent.getWidth();
			int ccHeight = this.currentComponent.getHeight();
			int titleWidth = this.fontRendererObj.getStringWidth(this.currentComponent.getTitle());
			int helpLeft = Math.max(5 + ccWidth + 10 - 10, 5 + titleWidth + 10);
			if ((x >= helpLeft) && (x <= helpLeft + 10) && (y >= this.height - (ccHeight + 10 + 25 + 14))
					&& (y <= this.height - (ccHeight + 10 + 25))) {

				help();
			} else {
				this.currentComponent.mouseMove(x, y);
			}
		} else if (Mouse.getEventButtonState()) {
			this.lastMouseButton = k;
		} else if ((this.lastMouseButton == k) && (!Mouse.getEventButtonState())) {
			this.lastMouseButton = -1;
			if (k == 0) {
				if ((x >= 0) && (x <= 20) && (y >= this.height - 20) && (y <= this.height)) {

					close();

				} else {

					this.currentComponent.mouseClick(x, y);
				}
			}
		}
	}

	public void close() {
		this.currentComponent.reset();

		this.mc.displayGuiScreen(null);
		this.mc.setIngameFocus();
	}

	public void help() {
		this.showHelp = 250;
	}

	public void selectCommand(Command command) {
		if (command.NeedsInput()) {
			Parameter param = command.getNextInputParameter();
			switch (param.getType()) {
			case Player:
				this.currentComponent = new ChoosePlayer(this, this.fontRendererObj, command, param);
				break;
			case Select:
				this.currentComponent = new ChooseList(this, this.fontRendererObj, command, param);
				break;
			case IPAddress:
				this.currentComponent = new EnterIPAddress(this, this.fontRendererObj, command, param);
				break;
			case Item:
				this.currentComponent = new ChooseItem(this, this.fontRendererObj, command, param);
				break;

			case ItemMeta:
				for (Parameter item : command.parameters) {
					if (item.getType() == Parameter.ParameterType.Item) {
						((ItemMetaParameter) param).setFromItemParameter((ItemParameter) item);
						break;
					}
				}
				selectCommand(command);
				break;
			case Number:
				this.currentComponent = new EnterNumber(this, this.fontRendererObj, command, param);
				break;
			case Text:
				this.currentComponent = new EnterText(this, this.fontRendererObj, command, param);
				break;

			}

		} else {
			String commandStr = command.getCommand();
			sendCommand(commandStr);

			command.clearInput();

			this.currentComponent = this.menu;
			this.menu.reset();
		}
	}

	private void sendCommand(String command) {
		this.mc.thePlayer.sendChatMessage(ServerCommands.commandPrefix + command);

		this.mc.displayGuiScreen(null);
		this.mc.setIngameFocus();
	}

	public void drawScreen(int i, int j, float f) {
		if ((this.mod.IsReady()) && (this.mod.commandList != null)) {
			int ccWidth = this.currentComponent.getWidth();
			int ccHeight = this.currentComponent.getHeight();

			drawBackground(ccWidth, ccHeight);
			int titleWidth = drawTitle(ccWidth, ccHeight, this.currentComponent.getTitle());

			this.currentComponent.renderComponent(10, this.height - (ccHeight + 5 + 25));

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
			GL11.glDisable(2896);
			Minecraft.getMinecraft().renderEngine.bindTexture(this.icons);

			drawTexturedModalRect(0, this.height - 19, 0, 20, 18, 19);

			String help = this.currentComponent.getHelp();
			int helpLeft = Math.max(5 + ccWidth + 10 - 10, 5 + titleWidth + 10);
			if ((help != null) && (help.length() > 0)) {
				drawTexturedModalRect(helpLeft, this.height - (ccHeight + 10 + 25 + 13), 18, 20, 10, 14);
			}

			if (this.showHelp > 0) {
				this.showHelp -= 1;
				drawHelp(helpLeft, ccHeight, this.currentComponent.getHelp());
			}

		} else {
			if (this.updateCounter > 40) {

				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
				return;
			}

			drawTitle(0, 0, I18n.format("gui.notavailable", new Object[0]));
		}

		super.drawScreen(i, j, f);
	}

	private void drawBackground(int contentWidth, int contentHeight) {
		int left = 5;
		int top = this.height - (contentHeight + 10 + 25);
		int right = 5 + contentWidth + 10;
		int bottom = this.height - 25;

		drawRect(left, top, right, bottom, Integer.MIN_VALUE);
		drawBorder(left, top, right, bottom);
	}

	private int drawTitle(int contentWidth, int contentHeight, String title) {
		int titleHeight = this.fontRendererObj.FONT_HEIGHT + 4;
		int titleWidth = this.fontRendererObj.getStringWidth(title);
		int top = this.height - (contentHeight + 10 + 25 + titleHeight);
		int right = 5 + titleWidth + 10;
		int bottom = this.height - (contentHeight + 10 + 25) + 1;

		drawRect(5, top, right, bottom, Integer.MIN_VALUE);
		drawBorder(5, top, right, bottom);

		this.fontRendererObj.drawString(title, 10, top + 3, 14737632);

		return titleWidth;
	}

	private void drawHelp(int titleWidth, int contentHeight, String help) {
		if ((help == null) || (help.length() == 0)) {
			this.showHelp = 0;
			return;
		}

		int helpHeight = this.fontRendererObj.FONT_HEIGHT + 4;
		int helpWidth = this.fontRendererObj.getStringWidth(help);

		int left = titleWidth + 12;
		int right = left + helpWidth + 10;
		int top = this.height - (contentHeight + 10 + 25 + helpHeight);
		int bottom = this.height - (contentHeight + 10 + 25);

		drawRect(left, top, right, bottom, Integer.MIN_VALUE);
		drawBorder(left, top, right, bottom);

		this.fontRendererObj.drawString(help, left + 5, top + 3, 14737632);
	}

	public void drawBorder(int left, int top, int right, int bottom) {
		int colour = 1627389951;
		drawRect(left, top, right, top + 1, colour);
		drawRect(left, bottom - 1, right, bottom, colour);
		drawRect(left, top, left + 1, bottom, colour);
		drawRect(right - 1, top, right, bottom, colour);
	}

	public int fadeColourAlpha(int from, int to) {
		int modulo = this.updateCounter % 12;
		modulo = Math.abs(modulo - 6);
		if (modulo < 2) {
			return from;
		}

		modulo -= 2;
		int diff = to - from;
		return from + (int) (diff * (modulo / 4.0F));
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void setCurrentComponent(IGuiComponent currentComponent) {
		this.currentComponent = currentComponent;
	}

	public void playClickSound() {
		this.mc.theWorld.playSound(0.0D, 0.0D, 0.0D, "random.click", 1.0F, 1.0F, false);
	}
}