package com.dyn.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.server.StopServerPythonScriptMessage;
import com.dyn.utils.CCOLPlayerInfo;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.code.CodeInterface;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectElementEntry;
import com.rabbit.gui.component.list.entries.SelectListEntry;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Scripts extends Show {
	private EntityPlayerSP mentor;
	private SelectElementEntry selectedEntry;
	private ScrollableDisplayList rosterDisplayList;

	private CodeInterface codeWindow;

	public Scripts() {
		setBackground(new DefaultBackground());
		title = "Roster Script Management";
		mentor = Minecraft.getMinecraft().thePlayer;
	}

	private void entryClicked(SelectListEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = (SelectElementEntry) entry;
	}

	@Override
	public void setup() {

		SideButtons.init(this, 7);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (CCOLPlayerInfo student : DYNServerMod.roster) {
			if (DYNServerMod.usernames.contains(student.getMinecraftUsername())) {
				rlist.add(new SelectElementEntry(student.getMinecraftUsername(), student.getCCOLName(),
						(SelectElementEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry,
								dlist, mouseX, mouseY)));
			} else {
				rlist.add(new SelectElementEntry(student.getMinecraftUsername(), student.getCCOLName(), Color.GRAY,
						(SelectElementEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry,
								dlist, mouseX, mouseY)));
			}
		}

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .2), width / 3, 20, Color.black,
				"Roster Count: " + DYNServerMod.roster.size()));

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .25), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		registerComponent(
				new Button((int) (width * .175), (int) (height * .7), (int) (width / 3.5), 20, "Stop Student Script")
						.setClickListener(btn -> {

							if (selectedEntry != null) {
								NetworkManager.sendToServer(
										new StopServerPythonScriptMessage((String) selectedEntry.getValue()));
							}
						}));

		registerComponent(
				new Button((int) (width * .175), (int) (height * .8), (int) (width / 3.5), 20, "Stop Roster Scripts")
						.setClickListener(btn -> {

							for (CCOLPlayerInfo student : DYNServerMod.roster) {
								if (DYNServerMod.usernames.contains(student.getMinecraftUsername())) {
									NetworkManager.sendToServer(
											new StopServerPythonScriptMessage(student.getMinecraftUsername()));
								}
							}
						}));

		registerComponent(codeWindow = (CodeInterface) new CodeInterface(width / 2, height / 5, (int) (width * .3),
				(int) (height * .7)).setDrawUnicode(true));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}
}
