package com.dyn.control.command.param;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NumberParameter extends Parameter {
	private int value = Integer.MIN_VALUE;
	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;
	private int defval = Integer.MIN_VALUE;
	private String format = "%d";
	private boolean set = false;

	public NumberParameter(Node node) {
		super(node);
		this.type = Parameter.ParameterType.Number;

		NamedNodeMap attrib = node.getAttributes();
		Node min = attrib.getNamedItem("min");
		Node max = attrib.getNamedItem("max");
		Node defval = attrib.getNamedItem("default");
		Node format = attrib.getNamedItem("format");
		if (min != null) {
			this.min = Integer.parseInt(min.getNodeValue());
		}
		if (max != null) {
			this.max = Integer.parseInt(max.getNodeValue());
		}
		if (defval != null) {
			this.defval = Integer.parseInt(defval.getNodeValue());
		} else {
			this.defval = this.min;
		}
		if (format != null) {
			this.format = format.getNodeValue();
		}
	}

	public boolean setInput(String inputText) {
		try {
			this.value = Integer.parseInt(inputText);

			if ((this.value >= this.min) && (this.value <= this.max)) {
				this.set = true;
				return true;
			}
		} catch (NumberFormatException ex) {
		}

		this.set = false;
		return false;
	}

	public int getMin() {
		return this.min;
	}

	public int getMax() {
		return this.max;
	}

	public int getDefault() {
		return this.defval;
	}

	public boolean NeedsInput() {
		return !this.set;
	}

	public void clear() {
		this.set = false;
		this.value = this.defval;
	}

	public String toString() {
		return String.format(this.format, new Object[] { Integer.valueOf(this.value) });
	}
}