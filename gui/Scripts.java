package com.dyn.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.bidirectional.MentorRequstScriptMessage;
import com.dyn.server.network.packets.server.RequestUserlistMessage;
import com.dyn.server.network.packets.server.StopServerPythonScriptMessage;
import com.dyn.utils.BooleanChangeListener;
import com.dyn.utils.CCOLPlayerInfo;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.code.CodeInterface;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.PictureButton;
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

	private BooleanChangeListener scriptlistener;
	private BooleanChangeListener rosterlistener;

	public Scripts() {
		setBackground(new DefaultBackground());
		title = "Roster Script Management";
		mentor = Minecraft.getMinecraft().thePlayer;
	}

	private void entryClicked(SelectListEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = (SelectElementEntry) entry;
		NetworkManager.sendToServer(new MentorRequstScriptMessage("_",
				Minecraft.getMinecraft().theWorld.getPlayerEntityByName((String) entry.getValue()).getEntityId()));
	}

	@Override
	public void onClose() {
		DYNServerMod.studentScriptMessageRecieved.removeBooleanChangeListener(scriptlistener);
		DYNServerMod.serverUserlistReturned.removeBooleanChangeListener(rosterlistener);
		super.onClose();
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
				rlist.add(
						new SelectElementEntry(student.getMinecraftUsername(),
								student.getCCOLName(), Color.GRAY, (SelectElementEntry entry, DisplayList dlist,
										int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY))
												.setIsEnabled(false));
			}
		}

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .2), width / 3, 20, Color.black,
				"Roster Count: " + DYNServerMod.roster.size()));

		registerComponent(
				new PictureButton((int) (width * .45), (int) (height * .175), 15, 15, DYNServerConstants.REFRESH_IMAGE)
						.addHoverText("Refresh Roster").doesDrawHoverText(true).setClickListener(
								but -> NetworkManager.sendToServer(new RequestUserlistMessage())));

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .25), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		registerComponent(
				new Button((int) (width * .15), (int) (height * .7), (int) (width / 3.5), 20, "Stop Student Script")
						.setClickListener(btn -> {
							if (selectedEntry != null) {
								NetworkManager.sendToServer(
										new StopServerPythonScriptMessage((String) selectedEntry.getValue()));
							}
						}));

		registerComponent(
				new Button((int) (width * .15), (int) (height * .8), (int) (width / 3.5), 20, "Stop Roster Scripts")
						.setClickListener(btn -> {
							for (CCOLPlayerInfo student : DYNServerMod.roster) {
								if (DYNServerMod.usernames.contains(student.getMinecraftUsername())) {
									NetworkManager.sendToServer(
											new StopServerPythonScriptMessage(student.getMinecraftUsername()));
								}
							}
						}));

		registerComponent(
				new PictureButton((int) (width * .45), (int) (height * .7), 20, 20, DYNServerConstants.REFRESH_IMAGE)
						.addHoverText("Refresh Script").doesDrawHoverText(true).setClickListener(but -> {
							if (selectedEntry != null) {
								NetworkManager.sendToServer(new MentorRequstScriptMessage("_",
										Minecraft.getMinecraft().theWorld
												.getPlayerEntityByName((String) selectedEntry.getValue())
												.getEntityId()));
							}
						}));

		registerComponent(codeWindow = (CodeInterface) new CodeInterface((int) (width * .52), height / 5,
				(int) (width * .33), (int) (height * .7)).setDrawUnicode(true));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));

		scriptlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				((Scripts) show).updateCodeWindowText(DYNServerMod.studentSctipt);
				event.getDispatcher().setFlag(false);
			}
		};
		DYNServerMod.studentScriptMessageRecieved.addBooleanChangeListener(scriptlistener, this);

		rosterlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				((Scripts) show).updateRoster();
			}
		};
		DYNServerMod.serverUserlistReturned.addBooleanChangeListener(rosterlistener, this);
	}

	public void updateCodeWindowText(String script) {
		codeWindow.setText(script);
	}

	public void updateRoster() {
		rosterDisplayList.clear();
		for (CCOLPlayerInfo student : DYNServerMod.roster) {
			if (DYNServerMod.usernames.contains(student.getMinecraftUsername())) {
				rosterDisplayList.add(new SelectElementEntry(student.getMinecraftUsername(), student.getCCOLName(),
						(SelectElementEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry,
								dlist, mouseX, mouseY)));
			} else {
				rosterDisplayList
						.add(new SelectElementEntry(student.getMinecraftUsername(),
								student.getCCOLName(), Color.GRAY, (SelectElementEntry entry, DisplayList dlist,
										int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY))
												.setIsEnabled(false));
			}
		}
	}
}
