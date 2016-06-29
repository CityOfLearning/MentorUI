package com.dyn.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.text.WordUtils;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.server.http.GetProgramRoster;
import com.dyn.server.http.GetPrograms;
import com.dyn.server.http.GetScheduledPrograms;
import com.dyn.server.keys.KeyManager;
import com.dyn.utils.BooleanChangeListener;
import com.dyn.utils.CCOLPlayerInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

public class Roster extends Show {

	private Map<UUID, CCOLPlayerInfo> tmpPlayerInfo = new HashMap<UUID, CCOLPlayerInfo>();

	private DropDown<Integer> orgs;
	private DropDown<Integer> programs;
	private DropDown<Integer> scheduledProg;
	private ScrollableDisplayList rosterDisplayList;
	TextLabel numberOfStudentsOnRoster;
	TextLabel rosterStatus;

	int selectedOrg;
	int selectedProgram;

	public Roster() {
		setBackground(new DefaultBackground());
		title = "Mentor Gui Roster Management";
	}

	private void dropdownSelected(DropDown<Integer> dropdown, String selected) {

		if (dropdown.getId().equals("org")) {
			selectedOrg = dropdown.getElement(selected).getValue();
			selectedProgram = 0;
			programs.clear();
			scheduledProg.clear();
			rosterDisplayList.clear();
			tmpPlayerInfo.clear();
			rosterStatus.setText("");

			GetPrograms progRequest = new GetPrograms(dropdown.getElement(selected).getValue(),
					KeyManager.getSecretKey(dropdown.getElement(selected).getValue()),
					KeyManager.getOrgKey(dropdown.getElement(selected).getValue()));

			BooleanChangeListener listener = event -> {
				if (event.getDispatcher().getFlag()) {
					JsonObject jObj = progRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							if (entryObj.has("id") && entryObj.has("name")) {
								programs.add(entryObj.get("name").getAsString(), entryObj.get("id").getAsInt());
							}
						}
						// if(!programs.isEmpty()){
						// programs.setIsEnabled(true);
						// }
					}
				}
			};

			progRequest.responseReceived.addBooleanChangeListener(listener);

		} else if (dropdown.getId().equals("program")) {

			scheduledProg.clear();
			rosterDisplayList.clear();
			tmpPlayerInfo.clear();
			rosterStatus.setText("");

			GetScheduledPrograms scheduleRequest = new GetScheduledPrograms(selectedOrg,
					dropdown.getElement(selected).getValue(), KeyManager.getSecretKey(selectedOrg),
					KeyManager.getOrgKey(selectedOrg));

			selectedProgram = dropdown.getElement(selected).getValue();

			BooleanChangeListener listener = event -> {
				if (event.getDispatcher().getFlag()) {
					JsonObject jObj = scheduleRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							if (entryObj.has("id") && entryObj.has("name")) {
								scheduledProg.add(entryObj.get("name").getAsString(), entryObj.get("id").getAsInt());
							}
						}
						// if(!scheduledProg.isEmpty()){
						// scheduledProg.setIsEnabled(true);
						// }
					}
				}
			};

			scheduleRequest.responseReceived.addBooleanChangeListener(listener);

		} else if (dropdown.getId().equals("schedule")) {

			rosterDisplayList.clear();
			tmpPlayerInfo.clear();
			rosterStatus.setText("");

			GetProgramRoster rosterRequest = new GetProgramRoster(selectedOrg, selectedProgram,
					dropdown.getElement(selected).getValue(), KeyManager.getSecretKey(selectedOrg),
					KeyManager.getOrgKey(selectedOrg));

			BooleanChangeListener listener = event -> {
				if (event.getDispatcher().getFlag()) {
					JsonObject jObj = rosterRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						rosterDisplayList.add(new StringEntry("Student Name"));
						rosterDisplayList.add(new StringEntry("------------------------------------------"));
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							JsonObject userEntryObj = entryObj.get("user").getAsJsonObject();
							if (userEntryObj.has("username") && userEntryObj.has("full_name")) {
								if (!tmpPlayerInfo
										.containsKey(UUID.fromString(userEntryObj.get("uuid").getAsString()))) {
									String name = WordUtils.capitalizeFully(userEntryObj.get("full_name").isJsonNull()
											? "Unavailable" : userEntryObj.get("full_name").getAsString());
									tmpPlayerInfo.put(UUID.fromString(userEntryObj.get("uuid").getAsString()),
											new CCOLPlayerInfo(UUID.fromString(userEntryObj.get("uuid").getAsString()),
													Integer.parseInt(entryObj.get("link_id").getAsString()), name,
													userEntryObj.get("username").getAsString(), false));
									rosterDisplayList.add(new StringEntry(name));
								}
							}
						}
						numberOfStudentsOnRoster
								.setText("Roster Count: " + (rosterDisplayList.getContent().size() - 2));
					}
				}
			};

			rosterRequest.responseReceived.addBooleanChangeListener(listener);
		}
	}

	@Override
	public void setup() {
		super.setup();

		registerComponent(
				new TextLabel((int) (width * .15), (int) (height * .2), (int) (width / 3.3), 20, Color.black, "Orgs"));

		orgs = new DropDown<Integer>((int) (width * .15), (int) (height * .25), (int) (width / 3.3), 20).add("DYN", 42)
				.setId("org").setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(orgs);

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .35), (int) (width / 3.3), 20, Color.black,
				"Programs"));

		programs = new DropDown<Integer>((int) (width * .15), (int) (height * .40), (int) (width / 3.3), 20)
				.setId("program").setDrawUnicode(true)
				.setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(programs);

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .5), (int) (width / 3.3), 20, Color.black,
				"Scheduled Programs"));

		scheduledProg = new DropDown<Integer>((int) (width * .15), (int) (height * .55), (int) (width / 3.3), 20)
				.setId("schedule").setDrawUnicode(true)
				.setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(scheduledProg);

		registerComponent(rosterStatus = new TextLabel((int) (width * .145), (int) (height * .75), width / 3, 20,
				Color.black, ""));

		registerComponent(
				new Button((int) (width * .15), (int) (height * .8), (int) (width / 3.3), 20, "Set this as my Roster")
						.setClickListener(but -> {
							if (tmpPlayerInfo.size() > 0) {
								but.setIsEnabled(false);
								DYNServerMod.roster.addAll(tmpPlayerInfo.values());
								rosterStatus.setText("Adding Students to Roster");
								Runnable task = () -> {
									// this blocks and so we gotta thread it
									for (CCOLPlayerInfo player : DYNServerMod.roster) {
										player.grabMissingData();
									}
									but.setIsEnabled(true);
									rosterStatus.setText("Added Students to Roster");
								};
								new Thread(task).start();

							}
						}));

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Roster Management",
				TextAlignment.CENTER));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (CCOLPlayerInfo user : DYNServerMod.roster) {
			rlist.add(new StringEntry(user.getCCOLName()));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .475), (int) (height * .25), (int) (width / 2.75),
				150, 15, rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		numberOfStudentsOnRoster = new TextLabel((int) (width * .5) + 20, (int) (height * .2), 90, 20, Color.black,
				"Roster Count: " + DYNServerMod.roster.size(), TextAlignment.LEFT);
		registerComponent(numberOfStudentsOnRoster);

		// the side buttons
		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_1.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_1.getRight()), 30, 30,
				DYNServerConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_2.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_2.getRight()), 30, 30,
				DYNServerConstants.ROSTER_IMAGE).setIsEnabled(false).addHoverText("Student Rosters")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_3.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_3.getRight()), 30, 30,
				DYNServerConstants.STUDENT_IMAGE).setIsEnabled(true).addHoverText("Manage a Student")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new ManageStudent())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_4.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_4.getRight()), 30, 30,
				DYNServerConstants.INVENTORY_IMAGE).setIsEnabled(true).addHoverText("Manage Inventory")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudentsInventory())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_5.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_5.getRight()), 30, 30,
				DYNServerConstants.ACHIEVEMENT_IMAGE).setIsEnabled(true).addHoverText("Award Achievements")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new MonitorAchievements())));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}
}
