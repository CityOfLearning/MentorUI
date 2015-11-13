package com.dyn.control.command;

import java.io.File;

public class ListFileInfo {
	private String serverType;
	private File configFile;
	private String detectionCommand;
	private int priority;

	public ListFileInfo(String serverType, File configFile, String detectionCommand, int priority) {
		this.serverType = serverType;
		this.configFile = configFile;
		this.detectionCommand = detectionCommand;
		this.priority = priority;
	}

	public String getServerType() {
		return this.serverType;
	}

	public File getConfigFile() {
		return this.configFile;
	}

	public String getDetectionCommand() {
		return this.detectionCommand;
	}

	public int getPriority() {
		return this.priority;
	}

	public boolean checkLocale(String locale) {
		return this.configFile.getName().contains(locale);
	}
}