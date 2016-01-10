package com.dyn.instructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.dyn.instructor.gui.Home;
import com.dyn.instructor.proxy.Proxy;
import com.dyn.instructor.reference.Reference;
import com.mojang.authlib.GameProfile;
import com.rabbit.gui.GuiFoundation;
import com.rabbit.gui.show.Show;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class TeacherMod {

	public static KeyBinding teacherKey;
	public static EntityPlayerSP teacher;
	public static Show currentTab;

	public static ArrayList<String> roster = new ArrayList();

	/*
	 * @Mod.Instance public static TeacherMod instance;
	 */

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	public static Logger logger;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		Configuration configs = new Configuration(event.getSuggestedConfigurationFile());
		try {
			configs.load();
		} catch (RuntimeException e) {
			logger.warn(e);
		}

		TeacherMod.currentTab = new Home();

		FMLCommonHandler.instance().bus().register(this);

		TeacherMod.teacherKey = new KeyBinding("key.toggle.teacherui", Keyboard.KEY_K, "key.categories.toggle");

		ClientRegistry.registerKeyBinding(TeacherMod.teacherKey);
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {

		if ((Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
			return;
		}
		if (TeacherMod.teacherKey.isPressed()) {
			// this is broken because of computercraft... for now we will keep
			// it in our hands until this gets sorted out
			// if(getOpLevel(Minecraft.getMinecraft().thePlayer.getGameProfile())>0)
			GuiFoundation.display(currentTab);
		}
	}

	protected int getOpLevel(GameProfile profile) {
		// does the configuration manager return null on the client side?
		MinecraftServer minecraftServer = getServer();
		if (minecraftServer == null)
			return 0;
		if (!minecraftServer.getConfigurationManager().canSendCommands(profile))
			return 0;
		UserListOpsEntry entry = (UserListOpsEntry) minecraftServer.getConfigurationManager().getOppedPlayers()
				.getEntry(profile);
		return entry != null ? entry.getPermissionLevel() : MinecraftServer.getServer().getOpPermissionLevel();
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