package com.dyn.control.command;

import java.util.Vector;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.dyn.control.command.param.IPAddressParameter;
import com.dyn.control.command.param.ItemMetaParameter;
import com.dyn.control.command.param.ItemParameter;
import com.dyn.control.command.param.NumberParameter;
import com.dyn.control.command.param.Parameter;
import com.dyn.control.command.param.SelectParameter;

public class Command extends CommandListItem {
	String command;
	public Vector<Parameter> parameters;

	public Command(Node node) {
		super(node);

		Node commandAttribute = node.getAttributes().getNamedItem("command");
		if (commandAttribute != null) {
			this.command = commandAttribute.getNodeValue();
		}

		if (node.hasChildNodes()) {
			Node fstCldNode = node.getFirstChild();

			if ((fstCldNode.getNodeType() == 3) && (fstCldNode.getTextContent().trim().length() == 0)) {
				fstCldNode = fstCldNode.getNextSibling();
			}

			if ((fstCldNode.getNodeType() == 1)
					&& ((fstCldNode.getNodeName() == "category") || (fstCldNode.getNodeName() == "command"))) {

				loadCommandList(node.getChildNodes());

			} else {
				loadCommandParameters(node.getChildNodes());
			}
		}
	}

	private void loadCommandParameters(NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if ((node.getNodeType() == 3) || ((node.getNodeType() == 1) && (node.getNodeName() == "param"))) {

				String str = node.getTextContent().trim();
				if (str.length() > 0) {
					addParameter(new Parameter(str));
				}
			} else if (node.getNodeType() == 1) {
				if (node.getNodeName() == "number") {
					addParameter(new NumberParameter(node));
				} else if (node.getNodeName() == "item") {
					addParameter(new ItemParameter(node));
				} else if (node.getNodeName() == "itemmeta") {
					addParameter(new ItemMetaParameter());
				} else if (node.getNodeName() == "select") {
					addParameter(new SelectParameter(node));
				} else if (node.getNodeName() == "ip") {
					addParameter(new IPAddressParameter(node));
				} else {
					addParameter(new Parameter(node));
				}
			}
		}
	}

	public String getCommand() {
		StringBuilder sb = new StringBuilder();
		if (this.command != null) {
			sb.append(this.command);
			sb.append(" ");
		}
		if (this.parameters != null) {
			for (Parameter item : this.parameters) {
				String value = item.toString();
				if (value != null) {
					sb.append(value);
					sb.append(" ");
				}
			}
		}
		return sb.toString().trim();
	}

	public boolean NeedsInput() {
		return getNextInputParameter() != null;
	}

	public void addParameter(Parameter commandPart) {
		if (this.parameters == null) {
			this.parameters = new Vector();
		}

		this.parameters.add(commandPart);
	}

	public Parameter getNextInputParameter() {
		if (this.parameters != null) {
			for (Parameter item : this.parameters) {
				if (item.NeedsInput()) {
					return item;
				}
			}
		}
		return null;
	}

	public void clearInput() {
		if (this.parameters != null) {
			for (Parameter item : this.parameters) {
				item.clear();
			}
		}
	}

	public void enableMatchingCommands(String name) {
		super.enableMatchingCommands(name);

		if ((this.command != null) && (this.command.equals(name))) {
			setEnabled(true);
		}
	}

	public void listCommands(Vector<String> list) {
		super.listCommands(list);

		if ((this.command != null) && (!list.contains(this.command))) {
			list.add(this.command);
		}
	}
}