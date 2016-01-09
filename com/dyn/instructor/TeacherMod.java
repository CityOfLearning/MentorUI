package com.dyn.instructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.dyn.instructor.gui.Home;
import com.dyn.instructor.proxy.Proxy;
import com.dyn.instructor.reference.Reference;
import com.mojang.authlib.GameProfile;
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

	public static KeyBinding teacherKey;

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

		this.teacherKey = new KeyBinding("key.toggle.teacherui", Keyboard.KEY_K, "key.categories.toggle");

		ClientRegistry.registerKeyBinding(this.teacherKey);
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
		if (this.teacherKey.getIsKeyPressed()) {
			//this is broken because of computercraft... for now we will keep it in our hands until this gets sorted out
			//if(getOpLevel(Minecraft.getMinecraft().thePlayer.getGameProfile())>0)
				GuiFoundation.display(new Home());
		}
	}
	
	protected int getOpLevel(GameProfile profile)
    {
		//does the configuration manager return null on the client side?
		MinecraftServer minecraftServer = getServer();
		if(minecraftServer==null)
			return 0;
        if (!minecraftServer.getConfigurationManager().func_152596_g(profile))
            return 0;
        UserListOpsEntry entry = (UserListOpsEntry) minecraftServer.getConfigurationManager().func_152603_m().func_152683_b(profile);
        return entry != null ? entry.func_152644_a() : MinecraftServer.getServer().getOpPermissionLevel();
    }
	
	private MinecraftServer getServer(){
		//for some reason getting the server returns null unless you use this method
		WorldServer worldServer = DimensionManager.getWorld(0); // default world
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "FakePlayer");
		FakePlayer fakePlayer = new FakePlayer(worldServer, gameProfile);
		return fakePlayer.mcServer;
	}
}