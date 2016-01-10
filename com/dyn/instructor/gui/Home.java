package com.dyn.instructor.gui;

import com.dyn.instructor.TeacherMod;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBox;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.control.Slider;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Home extends Show {

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

		if(TeacherMod.teacher == null){
			TeacherMod.teacher = Minecraft.getMinecraft().thePlayer;
		}
		isCreative = TeacherMod.teacher.capabilities.isCreativeMode;

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20,
				"Teacher Control", TextAlignment.CENTER));

		this.registerComponent(new Button((int) (this.width * .75), (int) (this.height * .1), 30, 20, ">>")
.setClickListener(but -> {
					TeacherMod.currentTab = new Roster();
					this.getStage().display(TeacherMod.currentTab);
				}));

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

		// time of day
		this.registerComponent(new TextLabel(this.width / 6, (int) (this.height * .75), this.width / 3, 20,
				"Set the Time of Day", TextAlignment.CENTER));

		this.registerComponent(new Slider(this.width / 6 + 15, (int) (this.height * .8), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos))
				.setProgress(mapClamp((Minecraft.getMinecraft().theWorld.getWorldTime() + 6000) % 24000, 0,
						24000, 0, 1))
				.setId("tod"));

		// speed slider
		this.registerComponent(new TextLabel((int) (this.width * .5), (int) (this.height * .75), this.width / 3, 20,
				"Set your movement speed", TextAlignment.CENTER));

		this.registerComponent(new Slider((int) (this.width * .5 + 15), (int) (this.height * .8), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos)).setId("speed"));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void toggleCreative() {
		TeacherMod.teacher.sendChatMessage("/gamemode " + (isCreative ? "0" : "1"));
		isCreative = !isCreative;
	}

	private void teleportStudentsToMe() {
		/// tp <Player1> <Player2>. Player1 is the person doing the teleporting,
		/// Player2 is the person that Player1 is teleporting to
		for (String student : TeacherMod.roster) {
			TeacherMod.teacher.sendChatMessage("/tp " + student + " " + TeacherMod.teacher.getDisplayName());
		}
	}

	private void dropDownSelection(DropDown dropdown, String selected) {
		selection = selected;
	}

	private void sliderChanged(Slider s, float pos) {
		System.out.println(s.getId() + " " + s.isScrolling());
		if (s.getId() == "tod") {
			int sTime = (int) (24000 * pos); // get the absolute time
			sTime -= 6000; // minecraft time is offset so lets move things
							// backward
							// to go from 0-24 instead of 6-5
			if (sTime < 0) {
				sTime += 24000;
			}
			TeacherMod.teacher.sendChatMessage("/time set " + sTime);
		}
		if (s.getId() == "speed") {
			TeacherMod.teacher.sendChatMessage("/speed " + (1 + pos * 2));
		}
	}

	private void teleportToStudent() {
		TeacherMod.teacher.sendChatMessage("/tp " + TeacherMod.teacher.getDisplayName() + " " + selection);
	}

	private float mapClamp(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
		float outVal = ((value - inputMin) / (inputMax - inputMin) * (outputMax - outputMin) + outputMin);
		return Math.max(outputMin, Math.min(outputMax, outVal));
	}
}
