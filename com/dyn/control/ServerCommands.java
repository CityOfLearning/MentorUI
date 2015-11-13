package com.dyn.control;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.lwjgl.input.Keyboard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.dyn.control.command.CommandListItem;
import com.dyn.control.command.ListFileInfo;
import com.dyn.control.command.ModFileInfo;
import com.dyn.control.gui.GuiLoading;
import com.dyn.control.gui.GuiServerCommand;
import com.dyn.control.reference.Reference;;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class ServerCommands {
	public static String commandPrefix = "/";
	public static KeyBinding keyCommandMenu = new KeyBinding("key.servercommand", Keyboard.KEY_C, "key.categories.multiplayer");
	
	public static ServerCommands instance;
	private boolean fetchingCommandList;
	private String serverType;
	private String fileOverride;
	private boolean ready = false;
	public CommandListItem commandList;
	private boolean lastCommandFound = false;
	private String langCode = "en_US";
	private Configuration config;

	public ServerCommands() throws Exception {
		instance = this;

		this.fetchingCommandList = false;
		this.commandList = null;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Property prop = this.config.get("general", "CommandFile", "AUTO");
		prop.comment = "This allows you to override which file is used to load the command list. (AUTO, scg_vanilla.xml, scg_bukkit.xml, or other file in config folder)";
		String temp = prop.getString();
		if (!temp.toUpperCase().equals("AUTO")) {
			this.fileOverride = temp;
		}

		this.config.save();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		ClientRegistry.registerKeyBinding(keyCommandMenu);

		this.langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
		this.ready = false;
		this.commandList = null;

		if ((!IsReady()) && (!this.fetchingCommandList)) {
			Load();
		}
	}

	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent e) {
		if (keyCommandMenu.isPressed()) {
			Minecraft mc = Minecraft.getMinecraft();
			if ((mc.currentScreen != null) || (!IsReady())) {
				return;
			}

			mc.ingameGUI.getChatGUI().clearChatMessages();

			mc.displayGuiScreen(new GuiServerCommand(this));
		}
	}

	public boolean IsReady() {
		return this.ready;
	}

	public void enableCommand(String command) {
		if (this.commandList == null) {
			return;
		}

		this.commandList.enableMatchingCommands(command);
	}

	public void commandFound(String command) {
		if (this.commandList == null) {
			if ((command != null) && (command.length() > 0)) {
				this.lastCommandFound = true;
			}

		} else {
			enableCommand(command);
		}
	}

	private void disableCommands() {
		if (this.commandList == null) {
			return;
		}

		this.commandList.setEnabled(false);
	}

	private void Load() {
		Thread t = new Thread() {

			public void run() {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
				}

				GuiLoading loading = new GuiLoading(ServerCommands.instance);
				Minecraft.getMinecraft().displayGuiScreen(loading);

				loading.setStatus(I18n.format("gui.loadingcommands", new Object[0]), -1);

				boolean ok = false;
				if (ServerCommands.this.fileOverride != null) {

					ok = ServerCommands.this.loadFile(new File(
							Minecraft.getMinecraft().mcDataDir + "/config/" + ServerCommands.this.fileOverride));

				} else {
					List<ListFileInfo> commandLists = ServerCommands.this.findFiles();

					for (ListFileInfo info : commandLists) {

						ServerCommands.this.lastCommandFound = false;
						loading.requestCommand(info.getDetectionCommand());

						while (loading.isWaiting()) {
							try {
								Thread.sleep(10L);
							} catch (InterruptedException e) {
							}
						}

						if (ServerCommands.this.lastCommandFound) {
							ok = ServerCommands.this.loadFile(info.getConfigFile());
							break;
						}
					}
				}

				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
				}

				if (ok) {
					loading.setStatus(I18n.format("gui.loadingmodcommands", new Object[0]), -1);

					List<ModFileInfo> modLists = ServerCommands.this.findModFiles();

					CommandListItem modCategory = new CommandListItem(I18n.format("gui.modlist", new Object[0]));
					boolean hasModCommands = false;
					for (ModFileInfo info : modLists) {

						if (Loader.isModLoaded(info.getModId())) {
							CommandListItem modCommands = ServerCommands.this.loadModFile(info);
							if (modCommands != null) {
								modCategory.addSubCommand(modCommands);
								hasModCommands = true;
							}
						}
					}

					if (hasModCommands) {
						ServerCommands.this.commandList.addSubCommand(modCategory);
					}

					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
					}

					loading.setStatus(I18n.format("gui.checkpermissions", new Object[0]), 0);

					ServerCommands.this.disableCommands();

					Vector<String> list = new Vector();
					ServerCommands.this.commandList.listCommands(list);

					Vector<String> list2 = new Vector();
					for (String cmd : list) {
						String first = cmd.substring(0, 1);
						if (!list2.contains(first)) {
							list2.add(first);
						}
					}

					int count = 0;
					for (String cmd : list2) {
						loading.requestCommand(cmd);

						count++;
						loading.setStatus(I18n.format("gui.checkpermissions", new Object[0]),
								(int) (count / list2.size() * 100.0F));

						while (loading.isWaiting()) {
							try {
								Thread.sleep(10L);
							} catch (InterruptedException e) {
							}
						}
					}

					loading.setStatus(I18n.format("gui.checkpermissions", new Object[0]), 100);
				}

				ServerCommands.this.loadComplete(ok);
			}
		};
		t.start();
	}

	private List<ListFileInfo> findFiles() {
		List<ListFileInfo> list = new ArrayList();
		try {
			File dir = new File(Minecraft.getMinecraft().mcDataDir + "/config/");
			if (!new File(Minecraft.getMinecraft().mcDataDir + "/config/scg_vanilla.xml").exists()) {

				dir = new File(Minecraft.getMinecraft().mcDataDir + "/../../source/SCG/config/");
			}

			File[] files = dir.listFiles(new FilenameFilter() {

				public boolean accept(File dir, String filename) {
					return (filename.startsWith("scg_")) && (filename.endsWith(".xml"));
				}
			});

			for (File file : files) {
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(file);
					Element docNode = doc.getDocumentElement();

					String server = docNode.getAttribute("server");
					String detect = docNode.getAttribute("detect");
					int priority = Integer.parseInt(docNode.getAttribute("priority"));

					ListFileInfo info = new ListFileInfo(server, file, detect, priority);
					list.add(info);
				} catch (Exception e) {
					System.err.println("####################  SCG  ####################");
					System.err.println("########  Error loading file: " + file.getName() + "  ########");
					e.printStackTrace();
					System.err.println("####################  SCG  ####################");
				}
			}
		} catch (Exception e) {
			System.err.println("####################  SCG  ####################");
			System.err.println("########  Error loading config files!  ########");
			e.printStackTrace();
			System.err.println("####################  SCG  ####################");
		}

		Collections.sort(list, new Comparator() {

			public int compare(ListFileInfo o1, ListFileInfo o2) {
				int temp = o2.getPriority() - o1.getPriority();
				if (temp == 0) {

					if (o1.checkLocale(ServerCommands.this.langCode)) {
						return -1;
					}
					if (o2.checkLocale(ServerCommands.this.langCode)) {
						return 1;
					}
				}
				return temp;
			}

			@Override
			public int compare(Object arg0, Object arg1) {
				// TODO Auto-generated method stub
				return 0;
			}

		});
		return list;
	}

	private List<ModFileInfo> findModFiles() {
		List<ModFileInfo> list = new ArrayList();
		try {
			File dir = new File(Minecraft.getMinecraft().mcDataDir + "/config/");
			if (!new File(Minecraft.getMinecraft().mcDataDir + "/config/scg_vanilla.xml").exists()) {

				dir = new File(Minecraft.getMinecraft().mcDataDir + "/../../source/SCG/config/");
			}

			File[] files = dir.listFiles(new FilenameFilter() {

				public boolean accept(File dir, String filename) {
					return (filename.startsWith("scgmod_")) && (filename.endsWith(".xml"));
				}
			});

			for (File file : files) {
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(file);
					Element docNode = doc.getDocumentElement();

					String name = docNode.getAttribute("name");
					String modid = docNode.getAttribute("modid");

					ModFileInfo info = new ModFileInfo(name, modid, file);
					list.add(info);
				} catch (Exception e) {
					System.err.println("####################  SCG  ####################");
					System.err.println("########  Error loading file: " + file.getName() + "  ########");
					e.printStackTrace();
					System.err.println("####################  SCG  ####################");
				}
			}
		} catch (Exception e) {
		}

		Collections.sort(list, new Comparator() {

			public int compare(ModFileInfo o1, ModFileInfo o2) {
				return o1.getModName().compareTo(o2.getModName());
			}

			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return 0;
			}

		});
		return list;
	}

	private boolean loadFile(File file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			this.serverType = doc.getDocumentElement().getAttribute("server");

			NodeList nodeList = doc.getDocumentElement().getChildNodes();
			this.commandList = new CommandListItem(nodeList);

			return true;
		} catch (Exception e) {
			System.err.println("####################  SCG  ####################");
			System.err.println("########  Error loading file: " + file.getName() + "  ########");
			e.printStackTrace();
			System.err.println("####################  SCG  ####################");
		}
		return false;
	}

	private CommandListItem loadModFile(ModFileInfo mod) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(mod.getConfigFile());
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getDocumentElement().getChildNodes();
			CommandListItem modCommands = new CommandListItem(nodeList);
			modCommands.setName(mod.getModName());

			return modCommands;
		} catch (Exception e) {
			System.err.println("####################  SCG  ####################");
			System.err.println("########  Error loading file: " + mod.getConfigFile().getName() + "  ########");
			e.printStackTrace();
			System.err.println("####################  SCG  ####################");
		}
		return null;
	}

	private void loadComplete(boolean successful) {
		this.ready = true;

		Minecraft.getMinecraft().displayGuiScreen(null);
		Minecraft.getMinecraft().setIngameFocus();

		GuiNewChat gui = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		if (successful) {
			gui.printChatMessage(new ChatComponentText(I18n.format("gui.scgloaded", new Object[] { getModVersion() })));
			gui.printChatMessage(new ChatComponentText(
					I18n.format("gui.openmenu", new Object[] { Keyboard.getKeyName(keyCommandMenu.getKeyCode()) })));
			gui.printChatMessage(new ChatComponentText(I18n.format("gui.usingtype", new Object[] { this.serverType })));
		} else {
			gui.printChatMessage(
					new ChatComponentText(I18n.format("gui.scgnotavailable", new Object[] { getModVersion() })));
		}
		
		this.fetchingCommandList = false;
	}
	
	private String getModVersion(){
		return Reference.VERSION;
	}
}