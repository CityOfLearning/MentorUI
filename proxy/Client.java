package com.dyn.mentor.proxy;

import org.lwjgl.input.Keyboard;

import com.dyn.DYNServerMod;
import com.dyn.mentor.gui.Home;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.RequestUserlistMessage;
import com.dyn.utils.PlayerLevel;
import com.rabbit.gui.RabbitGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class Client implements Proxy {

	private KeyBinding mentorKey;

	@Override
	public void init() {
		if (DYNServerMod.status == PlayerLevel.MENTOR) {
			MinecraftForge.EVENT_BUS.register(this);

			mentorKey = new KeyBinding("key.toggle.mentorui", Keyboard.KEY_M, "key.categories.toggle");

			ClientRegistry.registerKeyBinding(mentorKey);
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {

		if ((Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
			return;
		}
		if ((DYNServerMod.status == PlayerLevel.MENTOR) && mentorKey.isPressed()) {
			PacketDispatcher.sendToServer(new RequestUserlistMessage());
			RabbitGui.proxy.display(new Home());
		}
	}

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Render GUI when on call from client
	}
}