package com.dyn.instructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.dyn.instructor.gui.Home;
import com.dyn.instructor.proxy.Proxy;
import com.dyn.instructor.reference.Reference;
import com.rabbit.gui.GuiFoundation;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class TeacherMod {

	public static KeyBinding guiKey;

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

		FMLCommonHandler.instance().bus().register(this);

		TeacherMod.guiKey = new KeyBinding("key.toggle.tutorialGui", Keyboard.KEY_K, "key.categories.toggle");

		ClientRegistry.registerKeyBinding(TeacherMod.guiKey);
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
		if (guiKey.getIsKeyPressed()) {
			if (MinecraftServer.getServer().getConfigurationManager()
					.func_152596_g(Minecraft.getMinecraft().thePlayer.getGameProfile()))
				GuiFoundation.display(new Home());
		}
	}
}