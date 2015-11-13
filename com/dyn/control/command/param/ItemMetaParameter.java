package com.dyn.control.command.param;

public class ItemMetaParameter extends Parameter {
	int sub;
	boolean setOK;

	public ItemMetaParameter() {
		super(Parameter.ParameterType.ItemMeta);
	}

	public void setFromItemParameter(ItemParameter item) {
		this.sub = item.sub;
		this.setOK = true;
	}

	public boolean NeedsInput() {
		return !this.setOK;
	}

	public String toString() {
		if (this.sub >= 0) {
			return "" + this.sub;
		}

		return null;
	}

	public void clear() {
		this.setOK = false;
		this.sub = -1;
	}
}