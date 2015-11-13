package com.dyn.control.command;

import java.util.List;
import java.util.Vector;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommandListItem {
	private boolean enabled;
	private String name;
	private String help;
	protected List<CommandListItem> subCommands;

	public CommandListItem(String name) {
		this.name = name;
	}

	public CommandListItem(Node node) {
		loadSharedAttributes(node);
	}

	public CommandListItem(NodeList nodeList) {
		loadCommandList(nodeList);
	}

	public CommandListItem(Node node, NodeList nodeList) {
		loadSharedAttributes(node);

		loadCommandList(nodeList);
	}

	protected void loadSharedAttributes(Node node) {
		Node nameAttribute = node.getAttributes().getNamedItem("name");
		Node helpAttribute = node.getAttributes().getNamedItem("help");
		setName(nameAttribute.getNodeValue());
		if (helpAttribute != null) {
			setHelp(helpAttribute.getNodeValue());
		}
	}

	protected void loadCommandList(NodeList nodeLst) {
		for (int i = 0; i < nodeLst.getLength(); i++) {
			Node node = nodeLst.item(i);

			if (node.getNodeType() == 1) {
				if (node.getNodeName() == "category") {
					CommandListItem category = new CommandListItem(node, node.getChildNodes());
					addSubCommand(category);
				} else if (node.getNodeName() == "command") {
					Command command = new Command(node);
					addSubCommand(command);
				}
			}
		}
	}

	public boolean isEnabled() {
		if (this.subCommands != null) {
			for (CommandListItem sub : this.subCommands) {
				if (sub.isEnabled()) {
					return true;
				}
			}
			return false;
		}

		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.subCommands != null) {
			for (CommandListItem sub : this.subCommands) {
				sub.setEnabled(enabled);
			}

		} else {
			this.enabled = enabled;
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHelp() {
		return this.help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public void addSubCommand(CommandListItem command) {
		if (this.subCommands == null) {
			this.subCommands = new Vector();
		}
		this.subCommands.add(command);
	}

	public boolean hasSubCommands() {
		return (this.subCommands != null) && (countSubCommands() > 0);
	}

	public CommandListItem getSubCommand(int index) {
		int count = 0;
		for (CommandListItem sub : this.subCommands) {
			if (sub.isEnabled()) {
				if (count == index) {
					return sub;
				}
				count++;
			}
		}
		return null;
	}

	public int countSubCommands() {
		int count = 0;
		for (CommandListItem sub : this.subCommands) {
			if (sub.isEnabled()) {
				count++;
			}
		}
		return count;
	}

	public void enableMatchingCommands(String name) {
		if (this.subCommands != null) {
			for (CommandListItem sub : this.subCommands) {
				sub.enableMatchingCommands(name);
			}
		}
	}

	public void listCommands(Vector<String> list) {
		if (this.subCommands != null) {
			for (CommandListItem sub : this.subCommands) {
				sub.listCommands(list);
			}
		}
	}
}