package com.dyn.control.command.param;

import org.w3c.dom.Node;

public class Parameter {
	protected ParameterType type;
	protected String fixedCommandText;
	protected String inputValue;
	protected String label;

	public static enum ParameterType {
		Text, Number, Player, IPAddress, Select, Item, FixedText, ItemMeta;

		private ParameterType() {
		}
	}

	public Parameter(ParameterType type) {
		this.type = type;
	}

	public Parameter(String fixedCommandText) {
		this(ParameterType.FixedText);

		this.fixedCommandText = fixedCommandText;
	}

	public Parameter(Node node) {
		if (node.getNodeName() == "text") {
			this.type = ParameterType.Text;
		} else if (node.getNodeName() == "player") {
			this.type = ParameterType.Player;
		}

		org.w3c.dom.NamedNodeMap attrib = node.getAttributes();
		Node label = attrib.getNamedItem("label");
		if (label != null) {
			this.label = label.getNodeValue();
		}
	}

	public boolean NeedsInput() {
		return (this.type != ParameterType.FixedText) && (this.inputValue == null);
	}

	public String toString() {
		switch (this.type) {
		case Text:
			return this.inputValue;
		case Player:
			return this.inputValue;
		case IPAddress:
			return this.inputValue;
		case Select:
			return this.inputValue;
		case FixedText:
			return this.fixedCommandText;
		}
		return "";
	}

	public ParameterType getType() {
		return this.type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	public String getLabel() {
		if (this.label == null) {
			switch (this.type) {
			case Text:
				return net.minecraft.client.resources.I18n.format("gui.label.text", new Object[0]);
			case Number:
				return net.minecraft.client.resources.I18n.format("gui.label.number", new Object[0]);
			case Player:
				return net.minecraft.client.resources.I18n.format("gui.label.player", new Object[0]);
			case IPAddress:
				return net.minecraft.client.resources.I18n.format("gui.label.ip", new Object[0]);
			case Select:
				return net.minecraft.client.resources.I18n.format("gui.label.select", new Object[0]);
			case Item:
				return net.minecraft.client.resources.I18n.format("gui.label.item", new Object[0]);
			}
			return "";
		}

		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean setInput(String inputText) {
		this.inputValue = inputText;
		return true;
	}

	public void clear() {
		this.inputValue = null;
	}
}