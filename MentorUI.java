package com.dyn.mentor;

import java.util.ArrayList;

import com.dyn.DYNServerMod;
import com.dyn.mentor.proxy.Proxy;
import com.dyn.mentor.reference.MetaData;
import com.dyn.mentor.reference.Reference;
import com.dyn.server.ServerMod;
import com.dyn.server.utils.PlayerLevel;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class MentorUI {

	public static ArrayList<String> roster = new ArrayList<String>();

	@Mod.Instance(Reference.MOD_ID)
	public static MentorUI instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.Metadata(Reference.MOD_ID)
	public ModMetadata metadata;

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		if(ServerMod.status == PlayerLevel.MENTOR){
			proxy.init();
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		metadata = MetaData.init(metadata);

		Configuration configs = new Configuration(event.getSuggestedConfigurationFile());
		try {
			configs.load();
		} catch (RuntimeException e) {
			DYNServerMod.logger.warn(e);
		}

	}
}