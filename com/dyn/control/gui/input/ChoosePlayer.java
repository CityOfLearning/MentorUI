package com.dyn.control.gui.input;

import java.util.List;
import java.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import com.dyn.control.command.Command;
import com.dyn.control.command.param.Parameter;
import com.dyn.control.gui.GuiServerCommand;

public class ChoosePlayer extends ChooseList {
	public Vector<String> players;

	public ChoosePlayer(GuiServerCommand gui, FontRenderer fontRenderer, Command command, Parameter parameter) {
		super(gui, fontRenderer, command, parameter);

		this.players = new Vector();
		getPlayerList();

		this.names = new Vector(this.players);
		this.names.add(I18n.format("gui.entername", new Object[0]));
		this.values = new Vector(this.players);
		this.values.add("{PlayerCustom}");
	}

	public void select() {
		if (((String) this.values.get(this.highlighted)).equals("{PlayerCustom}")) {

			this.gui.setCurrentComponent(new EnterText(this.gui, this.fontRenderer, this.command, this.parameter));
		} else {
			super.select();
		}
	}

	public void getPlayerList() {
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		List list = player.sendQueue.playerInfoList;

		this.players.clear();
		this.players.add(player.getCommandSenderName());

		for (int i = 0; i < list.size(); i++) {
			GuiPlayerInfo guiplayerinfo = (GuiPlayerInfo) list.get(i);
			String name = StringUtils.stripControlCodes(guiplayerinfo.name);
			if (!name.equals(player.getCommandSenderName())) {
				this.players.add(name);
			}
		}
	}
}
