package com.dyn.control.command.param;

import java.util.Vector;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SelectParameter extends Parameter {
	public Vector<String> selectNames;
	public Vector<String> selectValues;

	public SelectParameter(Node node) {
		super(node);
		this.type = Parameter.ParameterType.Select;

		NodeList nodes = node.getChildNodes();
		this.selectNames = new Vector();
		this.selectValues = new Vector();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node innerNode = nodes.item(i);

			if ((innerNode.getNodeType() == 1) && (innerNode.getNodeName() == "option")) {
				String value = innerNode.getFirstChild().getNodeValue();

				Node nameAttribute = innerNode.getAttributes().getNamedItem("name");
				String name;
				if (nameAttribute != null) {
					name = nameAttribute.getNodeValue();
				} else {
					name = value;
				}
				this.selectNames.add(name);
				this.selectValues.add(value);
			}
		}
	}
}
