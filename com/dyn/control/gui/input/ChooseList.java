package com.dyn.control.gui.input;

import java.util.Vector;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import com.dyn.control.command.Command;
import com.dyn.control.command.param.Parameter;
import com.dyn.control.command.param.SelectParameter;
import com.dyn.control.gui.GuiServerCommand;
import com.dyn.control.gui.IGuiComponent;

public class ChooseList implements IGuiComponent {
	protected GuiServerCommand gui;
	protected FontRenderer fontRenderer;
	private int lastX;
	private int lastY;
	protected Command command;
	protected Parameter parameter;
	protected Vector<String> names;
	protected Vector<String> values;
	protected int highlighted;

	public ChooseList(GuiServerCommand gui, FontRenderer fontRenderer, Command command, Parameter parameter) {
		this.gui = gui;
		this.fontRenderer = fontRenderer;

		this.command = command;
		this.parameter = parameter;

		if ((parameter instanceof SelectParameter)) {
			this.names = ((SelectParameter) parameter).selectNames;
			this.values = ((SelectParameter) parameter).selectValues;
		}

		this.highlighted = 0;
	}

	public int getWidth() {
		int menuWidth = 80;
		for (int idx = 0; idx < this.names.size(); idx++) {
			int width = this.fontRenderer.getStringWidth((String) this.names.get(idx));
			menuWidth = Math.max(menuWidth, width);
		}
		return menuWidth;
	}

	public int getHeight() {
		int menuHeight = (this.fontRenderer.FONT_HEIGHT + 1) * this.names.size();
		return menuHeight;
	}

	public String getTitle() {
		return this.command.getName() + " - " + this.parameter.getLabel();
	}

	public String getHelp() {
		return this.command.getHelp();
	}

	public void keyTyped(char c, int i) {
		if (i == 200) {
			up();
		} else if (i == 208) {
			down();
		} else if (i == 28) {
			select();
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
			for (int idx = 0; idx < this.names.size(); idx++) {
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

			select();

			this.gui.playClickSound();
		}
	}

	public void up() {
		this.highlighted -= 1;

		if (this.highlighted < 0) {
			this.highlighted = (this.names.size() - 1);
		}
	}

	public void down() {
		this.highlighted += 1;

		if (this.highlighted >= this.names.size()) {
			this.highlighted = 0;
		}
	}

	public void select() {
		boolean inputOk = this.parameter.setInput((String) this.values.get(this.highlighted));

		if (inputOk) {
			this.gui.selectCommand(this.command);
		} else {
		}
	}

	public void renderComponent(int x, int y) {
		this.lastX = x;
		this.lastY = y;

		for (int idx = 0; idx < this.names.size(); idx++) {
			int colour = 14737632;
			if (this.highlighted == idx) {
				colour = 0;
				int highlightColour = this.gui.fadeColourAlpha(-2130706433, -1593835521);

				Gui.drawRect(x - 2, y + (this.fontRenderer.FONT_HEIGHT + 1) * idx, x + getWidth() + 2,
						y + (this.fontRenderer.FONT_HEIGHT + 1) * (idx + 1), highlightColour);
			}

			this.fontRenderer.drawString((String) this.names.get(idx), x, y + 10 * idx + 1, colour);
		}
	}

	public void reset() {
		this.command.clearInput();
	}
}