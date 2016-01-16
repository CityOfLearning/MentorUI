package com.dyn.instructor.gui;

import java.util.ArrayList;
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
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class ManageStudents extends Show {

	private EntityClientPlayerMP teacher;
	private boolean isCreative;
	private StringEntry selectedEntry;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList();

	public ManageStudents() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui Roster Management";
	}

	@Override
	public void setup() {
		super.setup();

		teacher = Minecraft.getMinecraft().thePlayer;

		for (String s : MinecraftServer.getServer().getAllUsernames()) {
			if (!TeacherMod.roster.contains(s) && s != Minecraft.getMinecraft().thePlayer.getDisplayName()) {
				userlist.add(s);
			}
		}

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20,
				"Roster Management", TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList();

		for (String s : userlist) {
			ulist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		this.registerComponent(
				new TextBox((int) (this.width * .2), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("rostersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (this.width * .15), (int) (this.height * .35),
				this.width / 3, 100, 15, rlist);
		rosterDisplayList.setId("roster");
		this.registerComponent(rosterDisplayList);

		this.registerComponent(new Button((int) (this.width * .2) - 10, (int) (this.height * .1), 30, 20, "<<")
				.setClickListener(but -> this.getStage().displayPrevious()));

		this.registerComponent(
				new Button((int) (this.width * .5), (int) (this.height * .2), 150, 20, "Teleport to Student")
						.setClickListener(but -> teleportToStudent()));

		this.registerComponent(
				new Button((int) (this.width * .5), (int) (this.height * .3), 150, 20, "Teleport Student to Me")
						.setClickListener(but -> teleportStudentTo()));
		
		this.registerComponent(
				new Button((int) (this.width * .525), (int) (this.height * .4), 60, 20, "Mute")
						.setClickListener(but -> muteStudent()));

		this.registerComponent(
				new Button((int) (this.width * .675), (int) (this.height * .4), 60, 20, "Unmute")
						.setClickListener(but -> unmuteStudent()));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("dyn", "textures/gui/background.png")));
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

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
	}

	private void teleportStudentTo() {
		if (!selectedEntry.getTitle().isEmpty())
			teacher.sendChatMessage("/tp " + selectedEntry.getTitle() + " " + teacher.getDisplayName());
	}

	private void teleportToStudent() {
		if (!selectedEntry.getTitle().isEmpty())
			teacher.sendChatMessage("/tp " + teacher.getDisplayName() + " " + selectedEntry.getTitle());
	}
	
	private void muteStudent() {
		if (!selectedEntry.getTitle().isEmpty())
			teacher.sendChatMessage("/mute " + selectedEntry.getTitle());
	}

	private void unmuteStudent() {
		if (!selectedEntry.getTitle().isEmpty())
			teacher.sendChatMessage("/unmute " + selectedEntry.getTitle());
	}
}
