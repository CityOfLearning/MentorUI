package com.dyn.instructor.gui;

import java.util.ArrayList;

import com.dyn.instructor.TeacherMod;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.FeedPlayerMessage;
import com.dyn.server.packets.server.RemoveEffectsMessage;
import com.dyn.server.packets.server.RequestFreezePlayerMessage;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class ManageStudents extends Show {

	private EntityPlayerSP teacher;
	private StringEntry selectedEntry;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList<String>();

	public ManageStudents() {
		setBackground(new DefaultBackground());
		title = "Teacher Gui Roster Management";
	}

	private void checkStudentInventory() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				teacher.sendChatMessage("/invsee " + selectedEntry.getTitle());
			}
		}
	}

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
	}

	private void feedStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				PacketDispatcher.sendToServer(new FeedPlayerMessage(selectedEntry.getTitle()));
			}
		}
	}

	private void healStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				teacher.sendChatMessage("/heal " + selectedEntry.getTitle());
			}
		}
	}

	private void muteStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				teacher.sendChatMessage("/mute " + selectedEntry.getTitle());
			}
		}
	}

	@Override
	public void setup() {
		super.setup();

		teacher = Minecraft.getMinecraft().thePlayer;

		for (String s : ServerMod.usernames) {
			if (!TeacherMod.roster.contains(s) && (s != Minecraft.getMinecraft().thePlayer.getDisplayNameString())) {
				userlist.add(s);
			}
		}

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Roster Management",
				TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList<ListEntry>();

		for (String s : userlist) {
			ulist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		registerComponent(new TextBox((int) (width * .2), (int) (height * .25), width / 4, 20, "Search for User")
				.setId("rostersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// the side buttons
		registerComponent(new PictureButton((int) (width * .03), (int) (height * .2), 30, 30,
				new ResourceLocation("minecraft", "textures/items/nether_star.png")).setIsEnabled(true)
						.addHoverText("Home Page").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .35), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ruby.png")).setIsEnabled(true)
						.addHoverText("Setup Student Roster").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .5), 30, 30,
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(false)
						.addHoverText("Manage a Student").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudents())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .65), 30, 30,
				new ResourceLocation("minecraft", "textures/items/fish_clownfish_raw.png")).setIsEnabled(true)
						.addHoverText("Manage Students").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudents2())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .8), 30, 30,
				new ResourceLocation("minecraft", "textures/items/emerald.png")).setIsEnabled(true)
						.addHoverText("Give Items").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new GiveItem())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .65), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ender_eye.png")).setIsEnabled(true)
						.addHoverText("Award Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new GiveAchievement())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .8), 30, 30,
				new ResourceLocation("minecraft", "textures/items/book_writable.png")).setIsEnabled(true)
						.addHoverText("Check Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new CheckPlayerAchievements())));

		// GUI main section
		registerComponent(new Button((int) (width * .5), (int) (height * .2), 150, 20, "Teleport to Student")
				.setClickListener(but -> teleportToStudent()));

		registerComponent(new Button((int) (width * .5), (int) (height * .3), 150, 20, "Teleport Student to Me")
				.setClickListener(but -> teleportStudentTo()));

		registerComponent(new Button((int) (width * .525), (int) (height * .4), 60, 20, "Mute")
				.setClickListener(but -> muteStudent()));

		registerComponent(new Button((int) (width * .675), (int) (height * .4), 60, 20, "Unmute")
				.setClickListener(but -> unmuteStudent()));

		registerComponent(new Button((int) (width * .5), (int) (height * .5), 150, 20, "Check Student Inventory")
				.setClickListener(but -> checkStudentInventory()));

		registerComponent(
				new Button((int) (width * .525), (int) (height * .6), 60, 20, "Freeze").setClickListener(but -> {
					if ((selectedEntry != null) && !selectedEntry.getTitle().isEmpty()) {
						PacketDispatcher.sendToServer(new RequestFreezePlayerMessage(selectedEntry.getTitle(), true));
					}
				}));

		registerComponent(
				new Button((int) (width * .675), (int) (height * .6), 60, 20, "Unfreeze").setClickListener(but -> {
					if ((selectedEntry != null) && !selectedEntry.getTitle().isEmpty()) {
						PacketDispatcher.sendToServer(new RequestFreezePlayerMessage(selectedEntry.getTitle(), false));
					}
				}));

		registerComponent(new Button((int) (width * .5), (int) (height * .7), 150, 20, "Remove Effects")
				.addHoverText("Removes effects like poison and invisibility").doesDrawHoverText(true)
				.setClickListener(but -> {
					if ((selectedEntry != null) && !selectedEntry.getTitle().isEmpty()) {
						PacketDispatcher.sendToServer(new RemoveEffectsMessage(selectedEntry.getTitle()));
					}
				}));

		registerComponent(new Button((int) (width * .525), (int) (height * .8), 60, 20, "Heal")
				.setClickListener(but -> healStudent()));

		registerComponent(new Button((int) (width * .675), (int) (height * .8), 60, 20, "Feed")
				.setClickListener(but -> feedStudent()));

		registerComponent(new Button((int) (width * .175), (int) (height * .8), 60, 20, "Creative")
				.setClickListener(but -> switchMode(1)));

		registerComponent(new Button((int) (width * .325), (int) (height * .8), 60, 20, "Survival")
				.setClickListener(but -> switchMode(0)));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void switchMode(int mode) {
		if (selectedEntry != null) {
			teacher.sendChatMessage("/gamemode " + mode + " " + selectedEntry.getTitle());
		}
	}

	private void teleportStudentTo() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				teacher.sendChatMessage("/tp " + selectedEntry.getTitle() + " " + teacher.getDisplayNameString());
			}
		}
	}

	private void teleportToStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				teacher.sendChatMessage("/tp " + teacher.getDisplayNameString() + " " + selectedEntry.getTitle());
			}
		}
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "rostersearch") {
			rosterDisplayList.clear();
			for (String student : TeacherMod.roster) {
				if (student.contains(textbox.getText())) {
					rosterDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}

	private void unmuteStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				teacher.sendChatMessage("/unmute " + selectedEntry.getTitle());
			}
		}
	}
}
