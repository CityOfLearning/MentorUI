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
import net.minecraft.client.entity.EntityClientPlayerMP;
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

		for (String s : ServerMod.usernames) {
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
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(false)
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
		
		this.registerComponent(new Button((int) (this.width * .5), (int) (this.height * .6), 150, 20, "Teleport Students to me")
				.setClickListener(but -> teleportStudentsToMe()));
		
		this.registerComponent(new Button((int) (this.width * .5), (int) (this.height * .5), 150, 20, "Check Student Inventory")
				.setClickListener(but -> checkStudentInventory()));
		
		this.registerComponent(new Button((int) (this.width * .5), (int) (this.height * .7), 150, 20, "Clear Student Roster")
				.setClickListener(but -> {
					TeacherMod.roster.clear();
				}));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .15), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .8), new ResourceLocation("dyn", "textures/gui/background.png")));
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
	
	private void teleportStudentsToMe() {
		/// tp <Player1> <Player2>. Player1 is the person doing the teleporting,
		/// Player2 is the person that Player1 is teleporting to
		for (String student : TeacherMod.roster) { //evidently this works for multi world teleportation...
			teacher.sendChatMessage("/tp " + student + " " + teacher.getDisplayName());
		}
	}
	
	private void checkStudentInventory() {
		if (!selectedEntry.getTitle().isEmpty())
			teacher.sendChatMessage("/invsee " + selectedEntry.getTitle());
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
