package com.ssomar.executableblocks.configs;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableitems.ExecutableItems;
import com.ssomar.score.SCore;
import com.ssomar.score.config.Config;
import com.ssomar.score.usedapi.MultiverseAPI;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GeneralConfig extends Config {
	
	private static GeneralConfig instance;
	private List<String> whitelistedWorlds;

	@Getter
	private boolean checkVersionMsg;

	public GeneralConfig() {
		super("config.yml");
		super.setup(ExecutableBlocks.plugin);
	}
	
	@Override
	public void load() {
		checkVersionMsg = config.getBoolean("checkVersionMsg", true);

		/* Disableworld config */
		whitelistedWorlds = new ArrayList<>();
		for (String str : config.getStringList("whitelistedWorlds")) {
			if((SCore.hasMultiverse && MultiverseAPI.getWorld(str) != null) || Bukkit.getWorld(str) != null){
				whitelistedWorlds.add(str);
			}
			else
				ExecutableBlocks.plugin.getServer().getLogger()
						.severe("[ExecutableBlocks] Invalid world: " + str + " in the option whitelistedWorlds");
		}
		if(!whitelistedWorlds.isEmpty()){
			ExecutableBlocks.plugin.getServer().getLogger().info("[ExecutableBlocks] ExecutableBlocks is enabled only in the worlds: ");
			for (String str : whitelistedWorlds) {
				ExecutableBlocks.plugin.getServer().getLogger().info("[ExecutableBlocks] " + str);
			}
		}

	}

	public static GeneralConfig getInstance() {
		if(instance == null) instance = new GeneralConfig();
		return instance;
	}
	
}
