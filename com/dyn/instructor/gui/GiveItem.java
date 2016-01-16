package com.dyn.instructor.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;

public class GiveItem extends Show {

	private ScrollableDisplayList itemDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<Item> itemList = new ArrayList();
	private ArrayList<String> itemNameList = new ArrayList();
	private StringEntry selectedUser;
	private StringEntry selectedItem;
	private TextBox userBox;
	private TextBox itemBox;
	private TextBox amountBox;

	public GiveItem() {
		this.setBackground(new DefaultBackground());
		this.title = "Teacher Gui";
	}

	@Override
	public void setup() {
		super.setup();

		this.registerComponent(new TextLabel(this.width / 3, (int) (this.height * .1), this.width / 3, 20, "Give Items",
				TextAlignment.CENTER));

		this.registerComponent(new Button((int) (this.width * .2) - 10, (int) (this.height * .1), 30, 20, "<<")
				.setClickListener(but -> this.getStage().displayPrevious()));

		this.registerComponent(new Button((int) (this.width * .75), (int) (this.height * .1), 30, 20, ">>")
				.setClickListener(but -> this.getStage().display(new ManageStudents())));
		
		// get all the items in the registry
		RegistryNamespaced blockRegistry = GameData.getBlockRegistry();
		Iterator iterator = blockRegistry.iterator();

		List<Item> blockList = new ArrayList();

		while (iterator.hasNext()) {
			Block blocks = (Block) iterator.next();
			blockList.add(Item.getItemFromBlock(blocks));
		}

		RegistryNamespaced itemRegistry = GameData.getItemRegistry();
		iterator = itemRegistry.iterator();

		List<Item> itemsList = new ArrayList();

		while (iterator.hasNext()) {
			Item items = (Item) iterator.next();
			itemsList.add(items);

		}

		for (Item i : blockList) {
			if (!itemList.contains(i)) {
				itemList.add(i);
			}
		}
		for (Item i : itemsList) {
			if (!itemList.contains(i)) {
				itemList.add(i);
			}
		}

		itemList.remove(null);

		ArrayList<ListEntry> dslist = new ArrayList();

		for (Item i : itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						dslist.add(new StringEntry(is.getDisplayName(), (StringEntry entry, DisplayList dlist,
								int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
					}
				} else {
					ItemStack is = new ItemStack(i);
					dslist.add(new StringEntry(
							is.getDisplayName(), (StringEntry entry, DisplayList dlist, int mouseX,
									int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}

		this.registerComponent(
				new TextBox((int) (this.width * .2), (int) (this.height * .25), this.width / 4, 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		this.registerComponent(
				new TextBox((int) (this.width * .55), (int) (this.height * .25), this.width / 4, 20, "Search for Item")
						.setId("itemsearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		itemDisplayList = new ScrollableDisplayList((int) (this.width * .5), (int) (this.height * .35),
				this.width / 3, 100, 15, dslist);
		itemDisplayList.setId("itms");

		this.registerComponent(itemDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList();

		for (String s : TeacherMod.roster) {
			rlist.add(new StringEntry(s, (StringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rlist.add(new StringEntry(Minecraft.getMinecraft().thePlayer.getDisplayName(), (StringEntry entry,
				DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));

		rosterDisplayList = new ScrollableDisplayList((int) (this.width * .15), (int) (this.height * .35),
				this.width / 3, 100, 15, rlist);
		rosterDisplayList.setId("roster");
		this.registerComponent(rosterDisplayList);

		userBox = new TextBox((int) (this.width * .15), (int) (this.height * .8), this.width / 4, 20, "User")
				.setId("user")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		this.registerComponent(userBox);

		amountBox = new TextBox((int) (this.width * .45) - 16, (int) (this.height * .8), 30, 20, "Amt").setId("amt")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		this.registerComponent(amountBox);

		itemBox = new TextBox((int) (this.width * .5), (int) (this.height * .8), this.width / 4, 20, "Item")
				.setId("item")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		this.registerComponent(itemBox);

		this.registerComponent(new Button((int) (this.width * .8) - 10, (int) (this.height * .8), 30, 20, "Give")
				.setClickListener(but -> giveItem()));
		
		// The background
		this.registerComponent(new Picture(this.width / 8, (int) (this.height * .05), (int) (this.width * (6.0 / 8.0)),
				(int) (this.height * .9), new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "itemsearch") {
			itemDisplayList.clear();
			for (Item i : itemList) {
				ItemStack is = new ItemStack(i);
				if (is.getDisplayName().toLowerCase().contains(textbox.getText().toLowerCase())) {
					itemDisplayList.add(new StringEntry(is.getDisplayName(), (StringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "usersearch") {
			rosterDisplayList.clear();
			for (String student : TeacherMod.roster) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					rosterDisplayList.add(new StringEntry(student, (StringEntry entry, DisplayList dlist, int mouseX,
							int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
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

	private void entryClicked(StringEntry entry, DisplayList list, int mouseX, int mouseY) {
		if (list.getId() == "itms") {
			selectedItem = entry;
			itemBox.setText(entry.getTitle());
		} else if (list.getId() == "roster") {
			selectedUser = entry;
			userBox.setText(entry.getTitle());
		}

	}

	private void giveItem() {
		if (userBox.getText().isEmpty() || itemBox.getText().isEmpty()) {
			return;
		}
		Item tItem = null;
		ItemStack itmSt = null;
		System.out.println(itemBox.getText());
		for (Item i : itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						if (is.getDisplayName().contentEquals(itemBox.getText())) {
							tItem = i;
							itmSt = is;
						}
					}
				} else {
					ItemStack is = new ItemStack(i);
					if (is.getDisplayName().contentEquals(itemBox.getText())) {
						tItem = i;
					}
				}
			}
		}
		System.out.println(userBox.getText() + ", " + tItem + ", " + amountBox.getText() + ", " + itmSt);
		if (tItem == null)
			return;
		String itemMod = "";
		if (itmSt != null)
			itemMod = " " + itmSt.getItemDamage();
		Minecraft.getMinecraft().thePlayer.sendChatMessage(
				"/give " + userBox.getText() + " " + Item.getIdFromItem(tItem) + " " + amountBox.getText() + " " + itemMod);
	}
}
