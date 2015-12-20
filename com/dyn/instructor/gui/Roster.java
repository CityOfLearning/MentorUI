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

	public Roster() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui";
	}

	@Override
	public void setup() {
		super.setup();

		ArrayList<String> userlist = new ArrayList();

		for (String s : MinecraftServer.getServer().getAllUsernames()) {
			if(!TeacherMod.roster.contains(s)){
				userlist.add(s);
			}
		}
		
		for(int i=10;i>0;i--){
			userlist.add("Test"+i);
		}
		
		TeacherMod.roster.clear();
		for(int i=10;i>0;i--){
			TeacherMod.roster.add("Test user"+i);
		}
		
		int xPos, yPos, width, height;
		xPos = this.width / 3;
		yPos = (int) (this.height * .1);
		width = this.width / 3;
		height = 20;
		String text = "Teacher Gui";
		TextAlignment align = TextAlignment.CENTER;
		this.registerComponent(new TextLabel(xPos, yPos, width, height, text, align));

		//The students not on the Roster List for this class
		ArrayList<ListEntry> list = new ArrayList();

		for (String s : userlist) {
			list.add(new StringEntry(s, (DisplayList dlist, int mouseX, int mouseY) -> entryClicked(dlist, mouseX, mouseY)));
		}

		this.registerComponent(new ScrollableDisplayList((int) (this.width * .2), (int) (this.height * .25), this.width /4, 150, 15, list).setId("users"));
		
		//The students on the Roster List for this class
		ArrayList<ListEntry> rosterlist = new ArrayList();

		for (String s : TeacherMod.roster) {
			rosterlist.add(new StringEntry(s, (DisplayList dlist, int mouseX, int mouseY) -> entryClicked(dlist, mouseX, mouseY)));
		}

		this.registerComponent(new ScrollableDisplayList((int) (this.width *.55), (int) (this.height * .25), this.width /4, 150, 15, rosterlist).setId("roster"));
		
		//Buttons
		this.registerComponent(new Button(this.width / 2-10, (int)(this.height * .4), 20, 20, ">>"));
		this.registerComponent(new Button(this.width / 2-10, (int)(this.height * .6), 20, 20, "<<"));
		this.registerComponent(new Button((int) (this.width * .2)-10, (int) (this.height * .1), 30, 20, "<<")
				.setClickListener(but -> this.getStage().displayPrevious()));
		
		//The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("tutorial", "textures/gui/background.png")));
	}
	
	private void entryClicked(DisplayList list, int mouseX, int mouseY) {
		if(list.getId()=="users"){
			System.out.println("user list");
		} else if (list.getId()=="roster"){
			System.out.println("roster list");
		}
	}
}
