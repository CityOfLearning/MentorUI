package com.dyn.control.command.param;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import org.w3c.dom.Node;

public class ItemParameter extends Parameter {
	String item;
	int sub;
	boolean setOK;

	public ItemParameter(Node node) {
		super(node);
		this.type = Parameter.ParameterType.Item;
	}

	public boolean NeedsInput() {
		return !this.setOK;
	}

	public String toString() {
		return "" + this.item;
	}

	public boolean setInput(String inputText) {
		this.setOK = false;
		try {
			String[] array = inputText.split("/");

			if (array.length == 2) {
				int sub = Integer.parseInt(array[1]);

				this.item = array[0];
				this.sub = sub;
				this.setOK = true;

			} else {

				this.item = array[0];
				this.sub = -1;
				this.setOK = true;
			}
		} catch (NumberFormatException ex) {
		}

		return this.setOK;
	}

	public void clear() {
		this.setOK = false;
	}

	private boolean checkItem(int item) {
		Item itemObj = Item.getItemById(item);
		return itemObj != null;
	}

	private boolean checkSubItem(int item, int sub) {
		if (checkItem(item)) {
			Item itemObj = Item.getItemById(item);
			if (itemObj.getHasSubtypes()) {
				List items = new ArrayList();
				itemObj.getSubItems(itemObj, null, items);
				if ((sub >= 0) && (sub < items.size())) {
					return items.get(sub) != null;
				}
			}
		}
		return false;
	}
}