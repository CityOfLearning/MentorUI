package com.dyn.control.command;

import java.io.File;

public class ModFileInfo {
	private String modName;
	private String modId;
	private File configFile;

	public ModFileInfo(String modName, String modId, File configFile) {
		this.modName = modName;
		this.modId = modId;
		this.configFile = configFile;
	}

	public String getModName() {
		return this.modName;
	}

	public String getModId() {
		return this.modId;
	}

	public File getConfigFile() {
		return this.configFile;
	}
}