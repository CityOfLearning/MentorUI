package com.dyn.mentor;

import java.util.ArrayList;

import com.dyn.mentor.proxy.Proxy;
import com.dyn.mentor.reference.MetaData;
import com.dyn.mentor.reference.Reference;
import com.dyn.utils.CCOLPlayerInfo;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:dyn|server")
public class MentorUI {

	public static ArrayList<CCOLPlayerInfo> roster = new ArrayList<>();

	@Mod.Instance(Reference.MOD_ID)
	public static MentorUI instance;

	@SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {

		proxy.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MetaData.init(event.getModMetadata());
	}
}