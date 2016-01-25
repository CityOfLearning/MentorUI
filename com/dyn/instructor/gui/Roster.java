package com.dyn.instructor.gui;

import java.util.ArrayList;
import com.dyn.instructor.TeacherMod;
import com.dyn.server.ServerMod;
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

public class Roster extends Show {

	private boolean isCreative;
	private StringEntry selectedEntry;
	private DisplayList selectedList;
	private ScrollableDisplayList userDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList();
	private ArrayList<String> filteredlist = new ArrayList();

	public Roster() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui Roster Management";
	}

	@Override
	public void setup() {
		super.setup();

		for (String s : ServerMod.usernames) {
			if (!TeacherMod.roster.contains(s) && s != Minecraft.getMinecraft().thePlayer.getDisplayName()) {
				userlist.add(s);
			}
		}
		
		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20, "Roster Management",
				TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList();

		for (String s : userlist) {
			ulist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		this.registerComponent(
				new TextBox((int) (this.width * .2), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		this.registerComponent(
				new TextBox((int) (this.width * .55), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("rostersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		userDisplayList = new ScrollableDisplayList((int) (this.width * .2), (int) (this.height * .35), this.width / 4, 130,
				15, ulist);
		userDisplayList.setId("users");
		this.registerComponent(userDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (this.width * .55), (int) (this.height * .35), this.width / 4, 130,
				15, rlist);
		rosterDisplayList.setId("roster");
		this.registerComponent(rosterDisplayList);

		// Buttons
		this.registerComponent(new Button(this.width / 2 - 10, (int) (this.height * .4), 20, 20, ">>")
				.setClickListener(but -> addToRoster()));
		this.registerComponent(new Button(this.width / 2 - 10, (int) (this.height * .6), 20, 20, "<<")
				.setClickListener(but -> removeFromRoster()));
		
		this.registerComponent(new Button((int) (this.width * .75), (int) (this.height * .1), 30, 20, ">>")
				.setClickListener(but -> this.getStage().display(new GiveItem())));
		
		this.registerComponent(new Button((int) (this.width * .2) - 10, (int) (this.height * .1), 30, 20, "<<")
				.setClickListener(but -> this.getStage().displayPrevious()));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void addToRoster() {
		if (selectedList.getId() == "users") {
			System.out.println("moving user " + selectedEntry.getTitle() + " from user list to roster");
			TeacherMod.roster.add(selectedEntry.getTitle());
			selectedEntry.setSelected(false);
			rosterDisplayList.add(selectedEntry);
			userDisplayList.remove(selectedEntry);
		}
	}

	private void removeFromRoster() {
		if (selectedList.getId() == "roster") {
			System.out.println("moving user " + selectedEntry.getTitle() + " from roster to user list");
			TeacherMod.roster.remove(selectedEntry.getTitle());
			selectedEntry.setSelected(false);
			rosterDisplayList.remove(selectedEntry);
			userDisplayList.add(selectedEntry);
		}
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "usersearch") {
			userDisplayList.clear();
			for (String student : userlist) {
				if(student.contains(textbox.getText())){
					userDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "rostersearch") {
			rosterDisplayList.clear();
			for (String student : TeacherMod.roster) {
				if(student.contains(textbox.getText())){
					rosterDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
		selectedList = list;

	}
}
