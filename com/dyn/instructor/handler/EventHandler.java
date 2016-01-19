package com.dyn.instructor.handler;

import com.dyn.instructor.TeacherMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.TeacherSettingsMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;

public class EventHandler {

	@SubscribeEvent
	public void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
		System.out.println("Player Logging in with Username of " + event.player.getDisplayName() );
		if (TeacherMod.proxy.getOpLevel(event.player.getGameProfile()) > 0) {
			PacketDispatcher.sendTo(new TeacherSettingsMessage(TeacherMod.proxy.getServerUsers(), true),
					(EntityPlayerMP) event.player);
		}
	}

}