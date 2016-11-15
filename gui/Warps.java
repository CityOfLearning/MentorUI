package com.dyn.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.server.RequestWorldListMessage;
import com.dyn.utils.BooleanChangeListener;
import com.dyn.utils.CCOLPlayerInfo;
import com.google.common.collect.Maps;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectElementEntry;
import com.rabbit.gui.component.list.entries.SelectListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.show.Show;

import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWarp2Stone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Warps extends Show {

	private static final Map<String, WaystoneEntry> waystones = Maps.newHashMap();

	private EntityPlayerSP mentor;
	private SelectElementEntry selectedEntry;
	private ScrollableDisplayList rosterDisplayList;

	private ScrollableDisplayList WarpDisplayList;
	private String selectedWarpEntry;

	private Map<Integer, List<String>> warpWorlds = Maps.newHashMap();
	private TextLabel currentWorld;

	public Warps() {
		setBackground(new DefaultBackground());
		title = "Warp Areas";
		mentor = Minecraft.getMinecraft().thePlayer;
		NetworkManager.sendToServer(new RequestWorldListMessage());

		BooleanChangeListener worldlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				WarpDisplayList.clear();

				for (int dim : warpWorlds.keySet()) {
					WarpDisplayList.add(new SelectStringEntry("-- World: " + DYNServerMod.worlds.get(dim) + " --",
							(SelectStringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry,
									dlist, mouseX, mouseY)));
					for (String warps : warpWorlds.get(dim)) {
						WarpDisplayList.add(new SelectStringEntry(warps, (SelectStringEntry entry, DisplayList dlist,
								int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
					}
				}
				currentWorld.setText("Current World: " + (DYNServerMod.worlds.get(mentor.dimension) != null
						? DYNServerMod.worlds.get(mentor.dimension) : getWorldName(mentor.dimension)));
				event.getDispatcher().setFlag(false);
			}
		};

		DYNServerMod.worldsMessageRecieved.addBooleanChangeListener(worldlistener, this);
	}

	private void entryClicked(SelectListEntry entry, DisplayList list, int mouseX, int mouseY) {
		if (list.getId().equals("warps")) {
			selectedWarpEntry = ((SelectStringEntry) entry).getTitle().trim();
		} else if (list.getId().equals("roster")) {
			selectedEntry = (SelectElementEntry) entry;
		}
	}

	public String getWorldName(int dimId) {
		switch (dimId) {
		case 0:
			return "Spawn";
		case -1:
			return "Nether";
		case 1:
			return "End";
		default:
			return Integer.toString(dimId);
		}
	}

	@Override
	public void onClose() {
		DYNServerMod.worldsMessageRecieved.removeBooleanChangeListener(this);
	}

	@Override
	public void setup() {

		SideButtons.init(this, 6);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (CCOLPlayerInfo student : DYNServerMod.roster) {
			if (DYNServerMod.usernames.contains(student.getMinecraftUsername())) {
				rlist.add(new SelectElementEntry(student.getMinecraftUsername(), student.getCCOLName(),
						(SelectElementEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry,
								dlist, mouseX, mouseY)));
			} else {
				rlist.add(new SelectElementEntry(student.getMinecraftUsername(), student.getCCOLName(), Color.GRAY,
						(SelectElementEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry,
								dlist, mouseX, mouseY)));
			}
		}

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .2), width / 3, 20, Color.black,
				"Roster Count: " + DYNServerMod.roster.size()));

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .25), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		List<ListEntry> warpList = new ArrayList();

		for (WaystoneEntry warp : WaystoneManager.getServerWaystones()) {
			waystones.put(warp.getName(), warp);
			if (!warpWorlds.containsKey(warp.getDimensionId())) {
				List<String> warps = new ArrayList<String>();
				warps.add(warp.getName());
				warpWorlds.put(warp.getDimensionId(), warps);
			} else {
				warpWorlds.get(warp.getDimensionId()).add(warp.getName());
			}
		}

		for (int dim : warpWorlds.keySet()) {
			warpList.add(
					new SelectStringEntry("-- World: " + DYNServerMod.worlds.get(dim) + " --", (SelectStringEntry entry,
							DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
			for (String warps : warpWorlds.get(dim)) {
				warpList.add(new SelectStringEntry(warps, (SelectStringEntry entry, DisplayList dlist, int mouseX,
						int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
			}
		}

		registerComponent(
				new TextLabel((int) (width * .5), (int) (height * .2), width / 3, 20, Color.black, "Warp Points: "));

		WarpDisplayList = new ScrollableDisplayList((int) (width * .5), (int) (height * .25), width / 3, 100, 15,
				warpList);
		WarpDisplayList.setId("warps");
		registerComponent(WarpDisplayList);

		registerComponent(currentWorld = new TextLabel((int) (width * .525), (int) (height * .7), (int) (width / 3.3),
				20, Color.black, "Current World: " + (DYNServerMod.worlds.get(mentor.dimension) != null
						? DYNServerMod.worlds.get(mentor.dimension) : getWorldName(mentor.dimension))));

		registerComponent(new Button((int) (width * .525), (int) (height * .8), (int) (width / 3.5), 20,
				"Warp me to this location").setClickListener(btn -> {
					if ((selectedWarpEntry != null) && !selectedWarpEntry.isEmpty()) {
						WaystoneEntry entry = waystones.get(selectedWarpEntry);
						if (entry != null) {
							getStage().close();
							NetworkHandler.channel.sendToServer(new MessageWarp2Stone(entry, mentor));
						}
					}
				}));

		registerComponent(new Button((int) (width * .175), (int) (height * .7), (int) (width / 3.5), 20, "Warp Student")
				.setClickListener(btn -> {
					if ((selectedWarpEntry != null) && !selectedWarpEntry.isEmpty()) {
						WaystoneEntry entry = waystones.get(selectedWarpEntry);
						if ((entry != null) && (selectedEntry != null)) {
							NetworkHandler.channel
									.sendToServer(new MessageWarp2Stone(entry, (String) selectedEntry.getValue()));
						}
					}
				}));

		registerComponent(new Button((int) (width * .175), (int) (height * .8), (int) (width / 3.5), 20, "Warp Roster")
				.setClickListener(btn -> {
					if ((selectedWarpEntry != null) && !selectedWarpEntry.isEmpty()) {
						WaystoneEntry entry = waystones.get(selectedWarpEntry);
						if (entry != null) {
							getStage().close();
							for (CCOLPlayerInfo student : DYNServerMod.roster) {
								if (DYNServerMod.usernames.contains(student.getMinecraftUsername())) {
									NetworkHandler.channel
											.sendToServer(new MessageWarp2Stone(entry, student.getMinecraftUsername()));
								}
							}
						}
					}
				}));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}
}
