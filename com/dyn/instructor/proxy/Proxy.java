package com.dyn.instructor.proxy;

import com.mojang.authlib.GameProfile;

public interface Proxy {
	public void renderGUI();
	public void init();
	public String[] getServerUsers();
	public int getOpLevel(GameProfile profile);
}