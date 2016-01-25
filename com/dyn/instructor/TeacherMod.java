package com.dyn.instructor;

import net.minecraftforge.common.config.Configuration;
import java.util.ArrayList;
import org.apache.logging.log4j.Logger;
import com.dyn.instructor.proxy.Proxy;
import com.dyn.instructor.reference.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class TeacherMod {

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

		proxy.init();
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}