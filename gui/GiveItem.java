package com.dyn.instructor.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dyn.instructor.TeacherMod;
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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GiveItem extends Show {

	private ScrollableDisplayList itemDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private TextBox userBox;
	private TextBox itemBox;
	private TextBox amountBox;

	public GiveItem() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui";
	}

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		if (list.getId() == "itms") {
			this.itemBox.setText(entry.getTitle());
		} else if (list.getId() == "roster") {
			this.userBox.setText(entry.getTitle());
		}

	}

	private void giveItemToPlayer() {
		if (this.userBox.getText().isEmpty() || this.itemBox.getText().isEmpty()) {
			return;
		}
		Item tItem = null;
		ItemStack itmSt = null;
		for (Item i : this.itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList<ItemStack>();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						if (is.getDisplayName().contentEquals(this.itemBox.getText())) {
							tItem = i;
							itmSt = is;
						}
					}
				} else {
					ItemStack is = new ItemStack(i);
					if (is.getDisplayName().contentEquals(this.itemBox.getText())) {
						tItem = i;
					}
				}
			}
		}
		if (tItem == null) {
			return;
		}
		String itemMod = "";
		if (itmSt != null) {
			itemMod = " " + itmSt.getItemDamage();
		}
		System.out.println("/give " + this.userBox.getText() + " " + GameRegistry.findUniqueIdentifierFor(tItem) + " "
				+ this.amountBox.getText() + " " + itemMod);
		Minecraft.getMinecraft().thePlayer.sendChatMessage("/give " + this.userBox.getText() + " "
				+ GameRegistry.findUniqueIdentifierFor(tItem) + " " + this.amountBox.getText() + " " + itemMod);
	}

	@Override
	public void setup() {
		super.setup();

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20, "Give Items",
				TextAlignment.CENTER));

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
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(true)
						.addHoverText("Manage Students").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new ManageStudents())));

		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .65), 30, 30,
				new ResourceLocation("minecraft", "textures/items/emerald.png")).setIsEnabled(false)
						.addHoverText("Give Items").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new GiveItem())));

		this.registerComponent(new PictureButton((int) (this.width * .03), (int) (this.height * .8), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ender_eye.png")).setIsEnabled(true)
						.addHoverText("Award Achievements").doesDrawHoverText(true)
						.setClickListener(but -> this.getStage().display(new GiveAchievement())));

		// get all the items in the registry
		RegistryNamespaced blockRegistry = GameData.getBlockRegistry();
		Iterator<?> iterator = blockRegistry.iterator();

		List<Item> blockList = new ArrayList<Item>();

		while (iterator.hasNext()) {
			Block blocks = (Block) iterator.next();
			blockList.add(Item.getItemFromBlock(blocks));
		}

		RegistryNamespaced itemRegistry = GameData.getItemRegistry();
		iterator = itemRegistry.iterator();

		List<Item> itemsList = new ArrayList<Item>();

		while (iterator.hasNext()) {
			Item items = (Item) iterator.next();
			itemsList.add(items);

		}

		for (Item i : blockList) {
			if (!this.itemList.contains(i)) {
				this.itemList.add(i);
			}
		}
		for (Item i : itemsList) {
			if (!this.itemList.contains(i)) {
				this.itemList.add(i);
			}
		}

		this.itemList.remove(null);

		ArrayList<ListEntry> dslist = new ArrayList<ListEntry>();

		for (Item i : this.itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList<ItemStack>();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						dslist.add(new StringEntry(is.getDisplayName(), (StringEntry entry, DisplayList dlist,
								int mouseX, int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
					}
				} else {
					ItemStack is = new ItemStack(i);
					dslist.add(new StringEntry(is.getDisplayName(), (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}

		this.registerComponent(
				new TextBox((int) (this.width * .2), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText)));
		this.registerComponent(
				new TextBox((int) (this.width * .55), (int) (this.height * .25), this.width / 4, 20, "Search for Item")
						.setId("itemsearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText)));

		this.itemDisplayList = new ScrollableDisplayList((int) (this.width * .5), (int) (this.height * .35),
				this.width / 3, 100, 15, dslist);
		this.itemDisplayList.setId("itms");

		this.registerComponent(this.itemDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> this
					.entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rlist.add(new StringEntry(Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText(),
				(StringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> this.entryClicked(entry, dlist,
						mouseX, mouseY)));

		this.rosterDisplayList = new ScrollableDisplayList((int) (this.width * .15), (int) (this.height * .35),
				this.width / 3, 100, 15, rlist);
		this.rosterDisplayList.setId("roster");
		this.registerComponent(this.rosterDisplayList);

		this.userBox = new TextBox((int) (this.width * .15), (int) (this.height * .8), this.width / 4, 20, "User")
				.setId("user").setTextChangedListener(
						(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText));
		this.registerComponent(this.userBox);

		this.amountBox = new TextBox((int) (this.width * .45) - 16, (int) (this.height * .8), 30, 20, "Amt")
				.setId("amt").setTextChangedListener(
						(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText));
		this.registerComponent(this.amountBox);

		this.itemBox = new TextBox((int) (this.width * .5), (int) (this.height * .8), this.width / 4, 20, "Item")
				.setId("item").setTextChangedListener(
						(TextBox textbox, String previousText) -> this.textChanged(textbox, previousText));
		this.registerComponent(this.itemBox);

		this.registerComponent(new Button((int) (this.width * .8) - 10, (int) (this.height * .8), 30, 20, "Give")
				.setClickListener(but -> this.giveItemToPlayer()));

		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .15), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .8), new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "itemsearch") {
			this.itemDisplayList.clear();
			for (Item i : this.itemList) {
				ItemStack is = new ItemStack(i);
				if (is.getDisplayName().toLowerCase().contains(textbox.getText().toLowerCase())) {
					this.itemDisplayList.add(new StringEntry(is.getDisplayName(), (StringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "usersearch") {
			this.rosterDisplayList.clear();
			for (String student : TeacherMod.roster) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					this.rosterDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> this.entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "amt") {
			try {
				int num = Integer.parseInt(textbox.getText());
				int cnum = Math.max(Math.min(num, 64), 0);
				if (num != cnum) {
					textbox.setText("" + cnum);
				}
			} catch (NumberFormatException e) {
				textbox.setText("");
			}
		}
	}
}
