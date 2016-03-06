package com.dyn.instructor;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import com.dyn.instructor.proxy.Proxy;
import com.dyn.instructor.reference.MetaData;
import com.dyn.instructor.reference.Reference;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class TeacherMod {

	public static ArrayList<String> roster = new ArrayList<String>();

	
	  @Mod.Instance(Reference.MOD_ID)
	  public static TeacherMod instance;
	  
	  @Mod.Metadata(Reference.MOD_ID)
		public ModMetadata metadata;
	 

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	public static Logger logger;

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.metadata = MetaData.init(this.metadata);
		
		logger = event.getModLog();

		Configuration configs = new Configuration(event.getSuggestedConfigurationFile());
		try {
			configs.load();
		} catch (RuntimeException e) {
			logger.warn(e);
		}

		proxy.init();
	}
}