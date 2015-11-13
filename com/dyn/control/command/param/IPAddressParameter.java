package com.dyn.control.command.param;

import org.w3c.dom.Node;

public class IPAddressParameter extends Parameter {
	public IPAddressParameter(Node node) {
		super(node);
		this.type = Parameter.ParameterType.IPAddress;
	}
}
