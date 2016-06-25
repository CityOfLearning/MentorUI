package com.dyn.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.names.manager.NamesManager;
import com.dyn.server.database.DBManager;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.FeedPlayerMessage;
import com.dyn.server.packets.server.RemoveEffectsMessage;
import com.dyn.server.packets.server.RequestFreezePlayerMessage;
import com.dyn.server.packets.server.RequestUserStatusMessage;
import com.dyn.server.packets.server.RequestUserlistMessage;
import com.dyn.server.packets.server.ServerCommandMessage;
import com.dyn.server.utils.BooleanChangeListener;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBoxButton;
import com.rabbit.gui.component.control.CheckBoxPictureButton;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.PictureToggleButton;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class ManageStudent extends Show {

	private EntityPlayerSP mentor;
	private SelectStringEntry selectedEntry;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList<String>();

	private boolean isFrozen;
	private boolean isMuted;
	private boolean isStudentInCreative;

	private String muteText;
	private String freezeText;
	private String modeText;
	private String dynUsername;
	private String dynPassword;
	private PictureToggleButton muteButton;
	private CheckBoxPictureButton freezeButton;
	private CheckBoxButton modeButton;
	private TextLabel dynUsernameLabel;
	private TextLabel dynPasswordLabel;

	public ManageStudent() {
		setBackground(new DefaultBackground());
		title = "Mentor GUI Manage A Student";
		freezeText = "Freeze Students";
		muteText = "Mute Students";
		modeText = "Creative Mode";
		isFrozen = false;
		isMuted = false;
		isStudentInCreative = false;
		dynUsername = "";
		dynPassword = "";

		BooleanChangeListener listener = event -> {
			if (event.getDispatcher().getFlag()) {
				isFrozen = DYNServerMod.playerStatus[0];
				isMuted = DYNServerMod.playerStatus[1];
				isStudentInCreative = DYNServerMod.playerStatus[2];
			}
		};
		DYNServerMod.playerStatusReturned.addBooleanChangeListener(listener);
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		for (ListEntry listEntry : list.getContent()) {
			if (!listEntry.equals(entry)) {
				listEntry.setSelected(false);
			}
		}
		PacketDispatcher.sendToServer(new RequestUserStatusMessage(selectedEntry.getTitle().split("-")[0]));
		selectedEntry = entry;
		usernameAndPassword();
	}

	private void feedStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				PacketDispatcher.sendToServer(new FeedPlayerMessage(selectedEntry.getTitle().split("-")[0]));
			}
		}
	}

	private void freezeUnfreezeStudent() {
		if (selectedEntry != null) {
			if (isFrozen) {
				PacketDispatcher.sendToServer(new ServerCommandMessage(
						"/p user " + selectedEntry.getTitle().split("-")[0] + " group add _FROZEN_"));
			} else {
				PacketDispatcher.sendToServer(new ServerCommandMessage(
						"/p user " + selectedEntry.getTitle().split("-")[0] + " group remove _FROZEN_"));
			}

			PacketDispatcher
					.sendToServer(new RequestFreezePlayerMessage(selectedEntry.getTitle().split("-")[0], isFrozen));

			isFrozen = !isFrozen;
			if (isFrozen) {
				freezeText = "UnFreeze Students";
				List<String> text = freezeButton.getHoverText();
				text.clear();
				text.add(freezeText);
				freezeButton.setHoverText(text);
			} else {
				freezeText = "Freeze Students";
				List<String> text = freezeButton.getHoverText();
				text.clear();
				text.add(freezeText);
				freezeButton.setHoverText(text);
			}
		}
	}

	private void healStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				PacketDispatcher
						.sendToServer(new ServerCommandMessage("/heal " + selectedEntry.getTitle().split("-")[0]));
			}
		}
	}

	private void muteUnmuteStudent() {
		if (selectedEntry != null) {
			if (isMuted) {
				PacketDispatcher
						.sendToServer(new ServerCommandMessage("/mute " + selectedEntry.getTitle().split("-")[0]));
			} else {
				PacketDispatcher
						.sendToServer(new ServerCommandMessage("/unmute " + selectedEntry.getTitle().split("-")[0]));
			}

			isMuted = !isMuted;
			if (isMuted) {
				muteText = "UnMute Students";
				List<String> text = muteButton.getHoverText();
				text.clear();
				text.add(muteText);
				muteButton.setHoverText(text);
			} else {
				muteText = "Mute Students";
				List<String> text = muteButton.getHoverText();
				text.clear();
				text.add(muteText);
				muteButton.setHoverText(text);
			}
		}
	}

	@Override
	public void setup() {
		super.setup();

		mentor = Minecraft.getMinecraft().thePlayer;

		for (String s : DYNServerMod.usernames) {
			if (!DYNServerMod.roster.contains(s) && (s != Minecraft.getMinecraft().thePlayer.getDisplayNameString())) {
				userlist.add(s);
			}
		}

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Manage a Student", TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList<ListEntry>();

		for (String s : userlist) {
			ulist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		registerComponent(new TextBox((int) (width * .23), (int) (height * .25), width / 4, 20, "Search for User")
				.setId("rostersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : DYNServerMod.roster) {
			rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// the side buttons
		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_1.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_1.getSecond()), 30, 30,
				DYNServerConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_2.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_2.getSecond()), 30, 30,
				DYNServerConstants.ROSTER_IMAGE).setIsEnabled(true).addHoverText("Student Rosters")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_3.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_3.getSecond()), 30, 30,
				DYNServerConstants.STUDENT_IMAGE).setIsEnabled(false).addHoverText("Manage a Student")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new ManageStudent())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_4.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_4.getSecond()), 30, 30,
				DYNServerConstants.INVENTORY_IMAGE).setIsEnabled(true).addHoverText("Manage Inventory")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudentsInventory())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_5.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_5.getSecond()), 30, 30,
				DYNServerConstants.ACHIEVEMENT_IMAGE).setIsEnabled(true).addHoverText("Award Achievements")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new MonitorAchievements())));

		// GUI main section
		registerComponent(
				new PictureButton((int) (width * .15), (int) (height * .25), 20, 20, DYNServerConstants.REFRESH_IMAGE)
						.addHoverText("Refresh").doesDrawHoverText(true).setClickListener(but -> updateUserList()));

		freezeButton = new CheckBoxPictureButton((int) (width * .55), (int) (height * .25), 50, 25,
				DYNServerConstants.FREEZE_IMAGE, false);
		freezeButton.setIsEnabled(true).addHoverText(freezeText).doesDrawHoverText(true)
				.setClickListener(but -> freezeUnfreezeStudent());
		registerComponent(freezeButton);

		muteButton = new PictureToggleButton((int) (width * .55), (int) (height * .365), 50, 25,
				DYNServerConstants.UNMUTE_IMAGE, DYNServerConstants.MUTE_IMAGE, false);
		muteButton.setIsEnabled(true).addHoverText(muteText).doesDrawHoverText(true)
				.setClickListener(but -> muteUnmuteStudent());
		registerComponent(muteButton);

		modeButton = new CheckBoxButton((int) (width * .55), (int) (height * .5), (int) (width / 3.3), 20,
				"Toggle Creative", false);
		modeButton.setIsEnabled(true).addHoverText(modeText).doesDrawHoverText(true)
				.setClickListener(but -> switchMode());
		registerComponent(modeButton);

		registerComponent(
				new PictureButton((int) (width * .7), (int) (height * .25), 50, 25, DYNServerConstants.HEART_IMAGE)
						.setIsEnabled(true).addHoverText("Heal Students").doesDrawHoverText(true)
						.setClickListener(but -> healStudent()));

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .365), 50, 25,
				new ResourceLocation("minecraft", "textures/items/chicken_cooked.png")).setIsEnabled(true)
						.addHoverText("Feed Students").doesDrawHoverText(true).setClickListener(but -> feedStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .6), (int) (width / 3.3), 20, "Teleport to Student")
						.setClickListener(but -> teleportToStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .7), (int) (width / 3.3), 20, "Teleport Student to Me")
						.setClickListener(but -> teleportStudentTo()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .8), (int) (width / 3.3), 20, "Remove Effects")
						.addHoverText("Removes effects like poison and invisibility").doesDrawHoverText(true)
						.setClickListener(but -> {
							if ((selectedEntry != null) && !selectedEntry.getTitle().isEmpty()) {
								PacketDispatcher
										.sendToServer(new RemoveEffectsMessage(selectedEntry.getTitle().split("-")[0]));
							}
						}));

		dynUsernameLabel = new TextLabel((int) (width * .15), (int) (height * .8), (int) (width / 2.5), 20, Color.black,
				"Username: " + dynUsername);
		dynPasswordLabel = new TextLabel((int) (width * .15), (int) (height * .85), (int) (width / 2.5), 20,
				Color.black, "Password: " + dynPassword);
		registerComponent(dynUsernameLabel);
		registerComponent(dynPasswordLabel);

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}

	private void switchMode() {
		if (selectedEntry != null) {
			PacketDispatcher.sendToServer(new ServerCommandMessage(
					"/gamemode " + (isStudentInCreative ? "0 " : "1 ") + selectedEntry.getTitle().split("-")[0]));
			isStudentInCreative = !isStudentInCreative;
			if (isStudentInCreative) {
				modeText = "Survival Mode";
				List<String> text = modeButton.getHoverText();
				text.clear();
				text.add(modeText);
				modeButton.setHoverText(text);
			} else {
				modeText = "Creative Mode";
				List<String> text = modeButton.getHoverText();
				text.clear();
				text.add(modeText);
				modeButton.setHoverText(text);
			}
		}
	}

	private void teleportStudentTo() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				PacketDispatcher.sendToServer(new ServerCommandMessage(
						"/tp " + selectedEntry.getTitle().split("-")[0] + " " + mentor.getDisplayNameString()));
			}
		}
	}

	private void teleportToStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				PacketDispatcher.sendToServer(new ServerCommandMessage(
						"/tp " + mentor.getDisplayNameString() + " " + selectedEntry.getTitle().split("-")[0]));
			}
		}
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "rostersearch") {
			rosterDisplayList.clear();
			for (String student : DYNServerMod.roster) {
				if (student.contains(textbox.getText())) {
					rosterDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}

	private void updateUserList() {
		PacketDispatcher.sendToServer(new RequestUserlistMessage());
		getStage().display(new Home());
	}

	private void usernameAndPassword() {
		if (selectedEntry != null) {
			dynUsername = NamesManager.getDYNUsername(selectedEntry.getTitle().split("-")[0]);
			dynPassword = DBManager.getPasswordFromDYNUsername(dynUsername);
		} else {
			dynUsername = "";
			dynPassword = "";
		}
	}
}
