package com.ssomar.executableblocks;


import com.ssomar.executableblocks.executableblocks.loader.NewExecutableBlockLoader;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.configs.api.PlaceholderAPI;
import com.ssomar.executableblocks.events.optimize.OptimizedEventsHandler;
import com.ssomar.score.api.executableblocks.load.ExecutableBlocksPostLoadEvent;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.splugin.SPlugin;
import com.ssomar.score.utils.placeholders.StringPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.commands.CommandsClass;
import com.ssomar.executableblocks.configs.GeneralConfig;
import com.ssomar.executableblocks.configs.messages.Message;
import com.ssomar.executableblocks.data.Database;
import com.ssomar.executableblocks.events.EventsHandler;
import com.ssomar.score.configs.messages.MessageInterface;
import com.ssomar.score.configs.messages.MessageMain;

public class ExecutableBlocks extends JavaPlugin implements SPlugin {

	public static ExecutableBlocks plugin;

	private CommandsClass commandClass;
	
	public static final String NAME = "ExecutableBlocks";
	
	public static final String NAME_2 = "[ExecutableBlocks]";

	public static boolean hasWorldEdit;

	public static boolean hasOraxen;

	public static boolean hasItemsAdder;

	@Override
	public void onEnable() {
		plugin = this;
		plugin.getLogger().info("================ ["+NAME+"] ================");
		commandClass = new CommandsClass(this);
		plugin.saveDefaultConfig();
		
		MessageMain.getInstance().loadMessagesOf(plugin, MessageInterface.getMessagesEnum(Message.values()));

		/* Soft-Dependency part */
		if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
			ExecutableBlocks.plugin.getServer().getLogger().info("[ExecutableBlocks] WorldEdit hooked !");
			hasWorldEdit = true;
		}

		if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
			ExecutableBlocks.plugin.getServer().getLogger().info("[ExecutableBlocks] Oraxen hooked !");
			hasOraxen = true;
		}

		if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
			ExecutableBlocks.plugin.getServer().getLogger().info("[ExecutableBlocks] ItemsAdder hooked !");
			hasItemsAdder = true;
		}

		/* Events instance part */	
		EventsHandler.getInstance().setup(this);

		/* Items parts */
		NewExecutableBlockLoader.getInstance().load();

		/* Load of the general config */	
		GeneralConfig.getInstance();

		/* Database */
		Database.getInstance().load();

		/* Commands part */
		this.getCommand("eb").setExecutor(commandClass);

		/* BStats part */
		int pluginId = 11452;
		@SuppressWarnings("unused")
		MetricsLite metrics = new MetricsLite(this, pluginId);
		
		/* block placed part */
		ExecutableBlockPlacedManager.getInstance().load();

		plugin.getLogger().info("================ ["+NAME+"] ================");		

		Bukkit.getPluginManager().callEvent(new ExecutableBlocksPostLoadEvent());
	}


	public void onReload(boolean PluginCommand) {
		plugin.getLogger().info("================ ["+NAME+"] ================");
		plugin.saveDefaultConfig();

		GeneralConfig.getInstance().load();

		OptimizedEventsHandler.getInstance().reload();
		
		MessageMain.getInstance().loadMessagesOf(plugin, MessageInterface.getMessagesEnum(Message.values()));

		/* Items instance part */
		NewExecutableBlockLoader.getInstance().reload();

		/* reload titles */
		for(ExecutableBlockPlacedInterface eBP : ExecutableBlockPlacedManager.getInstance().getAllExecutableBlocksPlaced().values()){
			ExecutableBlockPlaced eBPCasted = (ExecutableBlockPlaced) eBP;
			eBPCasted.getExecutableBlock().getTitle().update(eBPCasted.getLocation(), new StringPlaceholder());
		}

		plugin.getLogger().info("================ ["+NAME+"] ================");

	}

	@Override
	public void onDisable() {}


	public static ExecutableBlocks getPluginSt() {
		return plugin;
	}


	@Override
	public String getNameDesign() {
		return NAME_2;
	}

	@Override
	public String getObjectName() {
		return "blocks";
	}


	@Override
	public Plugin getPlugin() {
		return getPluginSt();
	}


	@Override
	public String getShortName() {
		return "EB";
	}


	@Override
	public boolean isLotOfWork() {
		return PlaceholderAPI.isLotOfWork();
	}

	@Override
	public int getMaxSObjectsLimit() {
		return 15;
	}
}
