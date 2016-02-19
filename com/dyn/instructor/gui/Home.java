package com.dyn.instructor.gui;

import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.CheckBox;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.Slider;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;

public class Home extends Show {

	private EntityClientPlayerMP teacher;
	private boolean isCreative;
	private String selection;

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

		// the side buttons
				this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .2), 30, 30,
						new ResourceLocation("minecraft", "textures/items/nether_star.png")).setIsEnabled(false)
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
						new ResourceLocation("minecraft", "textures/items/ender_eye.png")).setIsEnabled(true)
								.addHoverText("Award Achievements").doesDrawHoverText(true)
								.setClickListener(but -> this.getStage().display(new GiveAchievement())));

		this.registerComponent(
				new CheckBox((int) (this.width * .55), (int) (this.height * .22), "Set Creative Mode", isCreative)
						.setStatusChangedListener(btn -> toggleCreative()));

		// time of day
		this.registerComponent(new TextLabel(this.width / 6, (int) (this.height * .75), this.width / 3, 20,
				"Set the Time of Day", TextAlignment.CENTER));

		this.registerComponent(new Slider(this.width / 6 + 15, (int) (this.height * .8), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos))
				.setProgress(
						mapClamp((Minecraft.getMinecraft().theWorld.getWorldTime() + 6000) % 24000, 0, 24000, 0, 1))
				.setId("tod"));

		// speed slider
		this.registerComponent(new TextLabel((int) (this.width * .5), (int) (this.height * .75), this.width / 3, 20,
				"Set your movement speed", TextAlignment.CENTER));

		this.registerComponent(new Slider((int) (this.width * .5 + 15), (int) (this.height * .8), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos)).setId("speed"));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .15), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .8), new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void toggleCreative() {
		teacher.sendChatMessage("/gamemode " + (isCreative ? "0" : "1"));
		isCreative = !isCreative;
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
			teacher.sendChatMessage("/time set " + sTime);
		}
		if (s.getId() == "speed") { // speed has to be an integer value
			teacher.sendChatMessage("/speed " + (int) (1 + pos * 3));
		}
	}

	private float mapClamp(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
		float outVal = ((value - inputMin) / (inputMax - inputMin) * (outputMax - outputMin) + outputMin);
		return Math.max(outputMin, Math.min(outputMax, outVal));
	}
}
