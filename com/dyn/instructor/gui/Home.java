package com.dyn.instructor.gui;

import java.util.ArrayList;
import java.util.List;

import com.dyn.instructor.TeacherMod;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBox;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.control.MultiTextbox;
import com.rabbit.gui.component.control.Slider;
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

public class Home extends Show {

	private EntityClientPlayerMP teacher;
	private boolean isCreative;
	private DropDown studentDrop;
	private String selection;
	private DropDown studentMenu;

	public Home() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui";
	}

	@Override
	public void setup() {
		super.setup();

		teacher = Minecraft.getMinecraft().thePlayer;
		isCreative = teacher.capabilities.isCreativeMode;

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20,
				"Teacher Control", TextAlignment.CENTER));

		this.registerComponent(new Button((int) (this.width * .75), (int) (this.height * .1), 30, 20, ">>")
				.setClickListener(but -> this.getStage().display(new Roster())));

		this.registerComponent(
				new CheckBox((int) (this.width * .55), (int) (this.height * .22), "Set Creative Mode", isCreative)
						.setStatusChangedListener(btn -> toggleCreative()));

		this.registerComponent(new Button(this.width / 6, (int) (this.height * .2), 150, 20, "Clear Student Roster")
				.setClickListener(but -> {
					TeacherMod.roster.clear();
					studentMenu.clear();
				}));

		this.registerComponent(new Button(this.width / 6, (int) (this.height * .3), 150, 20, "Teleport Students to me")
				.setClickListener(but -> teleportStudentsToMe()));

		this.registerComponent(new Button(this.width / 6, (int) (this.height * .4), 150, 20, "Teleport to Student")
				.setClickListener(but -> teleportToStudent()));

		studentMenu = new DropDown((int) (this.width * .55), (int) (this.height * .42), 100, "Teleport to Student")
				.addAll(TeacherMod.roster.toArray())
				.setItemSelectedListener((DropDown dropdown, String selected) -> dropDownSelection(dropdown, selected));

		this.registerComponent(studentMenu);

		this.registerComponent(new Slider((int) (this.width * .25), (int) (this.height * .6), 100, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos)));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("tutorial", "textures/gui/background.png")));
	}

	private void toggleCreative() {
		teacher.sendChatMessage("/gamemode " + (isCreative ? "0" : "1"));
		isCreative = !isCreative;
	}

	private void teleportStudentsToMe() {
		/// tp <Player1> <Player2>. Player1 is the person doing the teleporting,
		/// Player2 is the person that Player1 is teleporting to
		for (String student : TeacherMod.roster) {
			teacher.sendChatMessage("/tp " + student + " " + teacher.getDisplayName());
		}
	}

	private void dropDownSelection(DropDown dropdown, String selected) {
		selection = selected;
	}

	private void sliderChanged(Slider s, float pos) {
		System.out.println("Slider pos: " + pos);
	}

	private void teleportToStudent() {
		teacher.sendChatMessage("/tp " + teacher.getDisplayName() + " " + selection);
	}
}
