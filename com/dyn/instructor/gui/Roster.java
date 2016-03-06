package com.dyn.instructor.gui;

import java.util.ArrayList;

import com.dyn.instructor.TeacherMod;
import com.dyn.server.ServerMod;
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
import net.minecraft.util.ResourceLocation;

public class Roster extends Show {

	private StringEntry selectedEntry;
	private DisplayList selectedList;
	private ScrollableDisplayList userDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList<String>();

	public Roster() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui Roster Management";
	}

	private void addToRoster() {
		if (this.selectedList.getId() == "users") {
			TeacherMod.roster.add(this.selectedEntry.getTitle());
			this.selectedEntry.setSelected(false);
			this.rosterDisplayList.add(this.selectedEntry);
			this.userDisplayList.remove(this.selectedEntry);
		}
	}

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		this.selectedEntry = entry;
		this.selectedList = list;

	}

	private void removeFromRoster() {
		if (this.selectedList.getId() == "roster") {
			TeacherMod.roster.remove(this.selectedEntry.getTitle());
			this.selectedEntry.setSelected(false);
			this.rosterDisplayList.remove(this.selectedEntry);
			this.userDisplayList.add(this.selectedEntry);
		}
	}

	@Override
	public void setup() {
		super.setup();

		for (String s : ServerMod.usernames) {
			if (!TeacherMod.roster.contains(s)
					&& !s.equals(Minecraft.getMinecraft().thePlayer.getDisplayNameString())) {
				this.userlist.add(s);
			}
		}

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20,
				"Roster Management", TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList<ListEntry>();

		for (String s : this.userlist) {
			ulist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> this
					.entryClicked(entry, dlist, mouseX, mouseY)));
		}

		this.registerComponent(
				new TextBox((int) (this.width * .2), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText)));
		this.registerComponent(
				new TextBox((int) (this.width * .55), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("rostersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText)));

		this.userDisplayList = new ScrollableDisplayList((int) (this.width * .2), (int) (this.height * .35),
				this.width / 4, 130, 15, ulist);
		this.userDisplayList.setId("users");
		this.registerComponent(this.userDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> this
					.entryClicked(entry, dlist, mouseX, mouseY)));
		}

		this.rosterDisplayList = new ScrollableDisplayList((int) (this.width * .55), (int) (this.height * .35),
				this.width / 4, 130, 15, rlist);
		this.rosterDisplayList.setId("roster");
		this.registerComponent(this.rosterDisplayList);

		// Buttons
		this.registerComponent(new Button((this.width / 2) - 10, (int) (this.height * .4), 20, 20, ">>")
				.setClickListener(but -> this.addToRoster()));
		this.registerComponent(new Button((this.width / 2) - 10, (int) (this.height * .6), 20, 20, "<<")
				.setClickListener(but -> this.removeFromRoster()));

		// the side buttons
		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .2), 30, 30,
				new ResourceLocation("minecraft", "textures/items/nether_star.png")).setIsEnabled(true)
						.addHoverText("Home Page").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new Home())));

		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .35), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ruby.png")).setIsEnabled(false)
						.addHoverText("Setup Student Roster").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new Roster())));

		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .5), 30, 30,
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(true)
						.addHoverText("Manage Students").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new ManageStudents())));

		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .65), 30, 30,
				new ResourceLocation("minecraft", "textures/items/emerald.png")).setIsEnabled(true)
						.addHoverText("Give Items").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new GiveItem())));

		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .8), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ender_eye.png")).setIsEnabled(true)
						.addHoverText("Award Achievements").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new GiveAchievement())));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .15), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .8), new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "usersearch") {
			this.userDisplayList.clear();
			for (String student : this.userlist) {
				if (student.contains(textbox.getText())) {
					this.userDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "rostersearch") {
			this.rosterDisplayList.clear();
			for (String student : TeacherMod.roster) {
				if (student.contains(textbox.getText())) {
					this.rosterDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}
}
