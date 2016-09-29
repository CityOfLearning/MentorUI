package com.dyn.mentor.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dyn.DYNServerConstants;
import com.dyn.admin.gui.Home;
import com.dyn.admin.gui.ManageStudent;
import com.dyn.admin.gui.ManageStudentsInventory;
import com.dyn.admin.gui.MonitorAchievements;
import com.dyn.admin.gui.Roster;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.google.common.collect.Maps;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.show.Show;

import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWarpStone;
import net.blay09.mods.waystones.util.WaystoneEntry;

public class Warps extends Show {

	private static final Map<String, WaystoneEntry> waystones = Maps.newHashMap();

	private ScrollableDisplayList WarpDisplayList;
	String selectedEntry;

	public Warps() {
		setBackground(new DefaultBackground());
		title = "Warp Areas";
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = (entry.getTitle().substring(6, entry.getTitle().indexOf("-"))).trim();
		System.out.println(selectedEntry);
	}

	@Override
	public void setup() {

		// the side buttons
		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_1.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_1.getRight()), 30, 30,
				DYNServerConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_2.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_2.getRight()), 30, 30,
				DYNServerConstants.ROSTER_IMAGE).setIsEnabled(true).addHoverText("Student Rosters")
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

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_6.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_6.getRight()), 30, 30, DYNServerConstants.WARP_IMAGE)
						.setIsEnabled(false).addHoverText("Warp Locations").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Warps())));

		List<ListEntry> warpList = new ArrayList();

		for (WaystoneEntry warp : WaystoneManager.getServerWaystones()) {
			waystones.put(warp.getName(), warp);
			warpList.add(new SelectStringEntry(
					"Name: " + warp.getName() + " - World: "
							+ ModuleMultiworld.getMultiworldManager().getWorldName(warp.getDimensionId()),
					(SelectStringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry, dlist,
							mouseX, mouseY)));
		}

		WarpDisplayList = new ScrollableDisplayList((int) (width * .475), (int) (height * .25), (int) (width / 2.75),
				150, 15, warpList);
		WarpDisplayList.setId("roster");
		registerComponent(WarpDisplayList);

		registerComponent(new Button((int) (width * .15), (int) (height * .8), (int) (width / 3.3), 20,
				"Warp me to this location").setClickListener(btn -> {
					if ((selectedEntry != null) && !selectedEntry.isEmpty()) {
						WaystoneEntry entry = waystones.get(selectedEntry);
						System.out.println(entry);
						if (entry != null) {
							this.getStage().close();
							NetworkHandler.channel.sendToServer(new MessageWarpStone(entry));
						}
					}
				}));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}
}
