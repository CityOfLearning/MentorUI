package com.dyn.instructor.proxy;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;

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
		// TODO Auto-generated method stub

	}

	protected int getOpLevel(GameProfile profile) {
		// does the configuration manager return null on the client side?
		MinecraftServer minecraftServer = getServer();
		if (minecraftServer == null)
			return 0;
		if (!minecraftServer.getConfigurationManager().func_152596_g(profile))
			return 0;
		UserListOpsEntry entry = (UserListOpsEntry) minecraftServer.getConfigurationManager().func_152603_m()
				.func_152683_b(profile);
		return entry != null ? entry.func_152644_a() : MinecraftServer.getServer().getOpPermissionLevel();
	}

	private MinecraftServer getServer() {
		// for some reason getting the server returns null unless you use this
		// method
		WorldServer worldServer = DimensionManager.getWorld(0); // default world
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "FakePlayer");
		FakePlayer fakePlayer = new FakePlayer(worldServer, gameProfile);
		return fakePlayer.mcServer;
	}

}