package com.dyn.control.gui;

public abstract interface IGuiComponent {
	public abstract int getWidth();

	public abstract int getHeight();

	public abstract String getTitle();

	public abstract String getHelp();

	public abstract void keyTyped(char paramChar, int paramInt);

	public abstract void mouseMove(int paramInt1, int paramInt2);

	public abstract void mouseClick(int paramInt1, int paramInt2);

	public abstract void renderComponent(int paramInt1, int paramInt2);

	public abstract void reset();
}
