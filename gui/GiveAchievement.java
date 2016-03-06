package com.dyn.instructor.gui;

import java.util.ArrayList;
import java.util.List;

import com.dyn.achievements.achievement.AchievementPlus;
import com.dyn.achievements.gui.Info;
import com.dyn.achievements.handlers.AchievementHandler;
import com.dyn.instructor.TeacherMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.MentorGivingAchievementMessage;
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

import net.minecraft.util.ResourceLocation;

public class GiveAchievement extends Show {

	private ScrollableDisplayList achDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private StringEntry selectedUser;
	private StringEntry selectedAchievement;

	public GiveAchievement() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui";
	}

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		if (list.getId() == "itms") {
			this.selectedAchievement = entry;
		} else if (list.getId() == "roster") {
			this.selectedUser = entry;
		}

	}

	@Override
	public void setup() {
		super.setup();

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20,
				"Award Achievements", TextAlignment.CENTER));

		// the side buttons
		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .2), 30, 30,
				new ResourceLocation("minecraft", "textures/items/nether_star.png")).setIsEnabled(true)
						.addHoverText("Home Page").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new Home())));

		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .35), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ruby.png")).setIsEnabled(true)
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
				new ResourceLocation("minecraft", "textures/items/ender_eye.png")).setIsEnabled(false)
						.addHoverText("Award Achievements").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new GiveAchievement())));

		this.registerComponent(
				new TextBox((int) (this.width * .2), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText)));
		this.registerComponent(new TextBox((int) (this.width * .55), (int) (this.height * .25), this.width / 4, 20,
				"Search Achievements").setId("achsearch").setTextChangedListener(
						(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText)));

		List<ListEntry> dslist = new ArrayList<ListEntry>();

		for (AchievementPlus a : AchievementHandler.getAllAchievements()) {
			dslist.add(new StringEntry(a.getName(), (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
		}

		this.achDisplayList = new ScrollableDisplayList((int) (this.width * .5), (int) (this.height * .35),
				this.width / 3, 100, 15, dslist);
		this.achDisplayList.setId("itms");

		this.registerComponent(this.achDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> this
					.entryClicked(entry, dlist, mouseX, mouseY)));
		}

		this.rosterDisplayList = new ScrollableDisplayList((int) (this.width * .15), (int) (this.height * .35),
				this.width / 3, 100, 15, rlist);
		this.rosterDisplayList.setId("roster");
		this.registerComponent(this.rosterDisplayList);

		// we need a way to get the players DYN account too if possible...
		this.registerComponent(
				new Button((int) (this.width * .55), (int) (this.height * .8), this.width / 4, 20, "Award to Player")
						.setClickListener(but -> {
							if ((this.selectedUser != null) && (this.selectedAchievement != null)
									&& !this.selectedUser.getTitle().isEmpty()
									&& !this.selectedAchievement.getTitle().isEmpty()) {
								PacketDispatcher
										.sendToServer(new MentorGivingAchievementMessage(this.selectedUser.getTitle(),
												AchievementHandler
														.findAchievementByName(this.selectedAchievement.getTitle())
														.getId()));
							}
						}));

		this.registerComponent(
				new Button((int) (this.width * .2), (int) (this.height * .8), this.width / 4, 20, "Achievement Info")
						.setClickListener(but -> {
							if (this.selectedAchievement != null) {
								this.getStage().display(new Info(
										AchievementHandler.findAchievementByName(this.selectedAchievement.getTitle())));
							}
						}));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .15), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .8), new ResourceLocation("dyn", "textures/gui/background.png")));

	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "achsearch") {
			this.achDisplayList.clear();
			for (AchievementPlus a : AchievementHandler.getAllAchievements()) {
				if (a.getName().contains(textbox.getText().toLowerCase())) {
					this.achDisplayList.add(new StringEntry(a.getName(), (StringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "usersearch") {
			this.rosterDisplayList.clear();
			for (String student : TeacherMod.roster) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					this.rosterDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}
}
