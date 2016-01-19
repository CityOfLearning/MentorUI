package com.dyn.instructor.proxy;

import com.dyn.instructor.handler.EventHandler;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraftforge.common.MinecraftForge;

public class Server implements Proxy {

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Actions on render GUI for the server (logging)

	}

	@Override
	public void init() {
		EventHandler eH = new EventHandler();
		
		FMLCommonHandler.instance().bus().register(eH);
		
		MinecraftForge.EVENT_BUS.register(eH);

	}

	@Override
	public String[] getServerUsers(){
		return MinecraftServer.getServer().getAllUsernames();
	}
	
	@Override
	public int getOpLevel(GameProfile profile) {
		// does the configuration manager return null on the client side?
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		if (minecraftServer == null)
			return 0;
		if (!minecraftServer.getConfigurationManager().func_152596_g(profile))
			return 0;
		UserListOpsEntry entry = (UserListOpsEntry) minecraftServer.getConfigurationManager().func_152603_m()
				.func_152683_b(profile);
		return entry != null ? entry.func_152644_a() : MinecraftServer.getServer().getOpPermissionLevel();
	}


}