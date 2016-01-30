package com.dyn.instructor.gui;

import java.util.ArrayList;
import java.util.List;

import com.dyn.achievements.achievement.AchievementPlus;
import com.dyn.achievements.gui.Info;
import com.dyn.achievements.handlers.AchievementHandler;
import com.dyn.instructor.TeacherMod;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
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

public class GiveAchievement extends Show {

	private ScrollableDisplayList achDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private StringEntry selectedUser;
	private StringEntry selectedAchievement;

	public GiveAchievement() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui";
	}

	@Override
	public void setup() {
		super.setup();

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20, "Award Achievements",
				TextAlignment.CENTER));

		this.registerComponent(new Button((int) (this.width * .2) - 10, (int) (this.height * .1), 30, 20, "<<")
				.setClickListener(but -> this.getStage().displayPrevious()));

		/*this.registerComponent(new Button((int) (this.width * .75), (int) (this.height * .1), 30, 20, ">>")
				.setClickListener(but -> this.getStage().display(new ManageStudents())));*/

		this.registerComponent(
				new TextBox((int) (this.width * .2), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		this.registerComponent(
				new TextBox((int) (this.width * .55), (int) (this.height * .25), this.width / 4, 20, "Search for Item")
						.setId("achsearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		List<ListEntry> dslist = new ArrayList();
		
		for(AchievementPlus a : AchievementHandler.getAllAchievements()){
			dslist.add(new StringEntry(a.getName(), (StringEntry entry, DisplayList dlist,
					int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}
		
		achDisplayList = new ScrollableDisplayList((int) (this.width * .5), (int) (this.height * .35),
				this.width / 3, 100, 15, dslist);
		achDisplayList.setId("itms");

		this.registerComponent(achDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rlist.add(new StringEntry(Minecraft.getMinecraft().thePlayer.getDisplayName(), (StringEntry entry,
				DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));

		rosterDisplayList = new ScrollableDisplayList((int) (this.width * .15), (int) (this.height * .35),
				this.width / 3, 100, 15, rlist);
		rosterDisplayList.setId("roster");
		this.registerComponent(rosterDisplayList);

		//we need a way to get the players DYN account too if possible...
		this.registerComponent(new Button((int) (this.width * .8) - 10, (int) (this.height * .8), 40, 20, "Award")
				.setClickListener(but -> {}/*PacketDispatcher.sendToServer(new AwardAchievementMessage(AchievementHandler.findAchievementByName(selectedAchievement.getTitle()).getId(), selectedUser.getTitle()))*/));
		
		this.registerComponent(new Button((int) (this.width * .6) - 10, (int) (this.height * .8), 40, 20, "Info")
				.setClickListener(but -> {this.getStage().display(new Info(AchievementHandler.findAchievementByName(selectedAchievement.getTitle())));}/*PacketDispatcher.sendToServer(new AwardAchievementMessage(AchievementHandler.findAchievementByName(selectedAchievement.getTitle()).getId(), selectedUser.getTitle()))*/));

		
		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "achsearch") {
			achDisplayList.clear();
			for (AchievementPlus a : AchievementHandler.getAllAchievements()) {
				if (a.getName().contains(textbox.getText().toLowerCase())) {
					achDisplayList.add(new StringEntry(a.getName(), (StringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "usersearch") {
			rosterDisplayList.clear();
			for (String student : TeacherMod.roster) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					rosterDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} 
	}

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		if (list.getId() == "itms") {
			selectedAchievement = entry;
		} else if (list.getId() == "roster") {
			selectedUser = entry;
		}

	}
}
