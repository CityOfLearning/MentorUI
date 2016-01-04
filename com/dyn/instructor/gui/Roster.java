package com.dyn.instructor.gui;

import java.util.ArrayList;
import java.util.List;

import com.dyn.instructor.TeacherMod;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBox;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.control.MultiTextbox;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.component.list.entries.StringEntry.OnClickListener;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class Roster extends Show {

	private boolean isCreative;
	private StringEntry selectedEntry;
	private DisplayList selectedList;
	private ScrollableDisplayList userList;
	private ScrollableDisplayList rosterList;
	private ArrayList<String> userlist = new ArrayList();
	private ArrayList<String> filteredlist = new ArrayList();

	public Roster() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui Roster";
	}

	@Override
	public void setup() {
		super.setup();

		for (String s : MinecraftServer.getServer().getAllUsernames()) {
			if (!TeacherMod.roster.contains(s) && s != Minecraft.getMinecraft().thePlayer.getDisplayName()) {
				userlist.add(s);
			}
		}

		/*userlist.clear();
		for (int i = 10; i > 0; i--) {
			userlist.add("Test" + i);
		}
		TeacherMod.roster.clear();
		for (int i = 10; i > 0; i--) {
			TeacherMod.roster.add("Test user" + i);
		}*/
		
		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20, "Roster",
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

		userList = new ScrollableDisplayList((int) (this.width * .2), (int) (this.height * .35), this.width / 4, 130,
				15, ulist);
		userList.setId("users");
		this.registerComponent(userList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterList = new ScrollableDisplayList((int) (this.width * .55), (int) (this.height * .35), this.width / 4, 130,
				15, rlist);
		rosterList.setId("roster");
		this.registerComponent(rosterList);

		// Buttons
		this.registerComponent(new Button(this.width / 2 - 10, (int) (this.height * .4), 20, 20, ">>")
				.setClickListener(but -> addToRoster()));
		this.registerComponent(new Button(this.width / 2 - 10, (int) (this.height * .6), 20, 20, "<<")
				.setClickListener(but -> removeFromRoster()));
		this.registerComponent(new Button((int) (this.width * .2) - 10, (int) (this.height * .1), 30, 20, "<<")
				.setClickListener(but -> this.getStage().displayPrevious()));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("tutorial", "textures/gui/background.png")));
	}

	private void addToRoster() {
		if (selectedList.getId() == "users") {
			System.out.println("moving user " + selectedEntry.getTitle() + " from user list to roster");
			TeacherMod.roster.add(selectedEntry.getTitle());
			selectedEntry.setSelected(false);
			rosterList.add(selectedEntry);
			userList.remove(selectedEntry);
		}
	}

	private void removeFromRoster() {
		if (selectedList.getId() == "roster") {
			System.out.println("moving user " + selectedEntry.getTitle() + " from roster to user list");
			TeacherMod.roster.remove(selectedEntry.getTitle());
			selectedEntry.setSelected(false);
			rosterList.remove(selectedEntry);
			userList.add(selectedEntry);
		}
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "usersearch") {
			userList.clear();
			for (String student : userlist) {
				if(student.contains(textbox.getText())){
					userList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "rostersearch") {
			rosterList.clear();
			for (String student : TeacherMod.roster) {
				if(student.contains(textbox.getText())){
					rosterList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
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
