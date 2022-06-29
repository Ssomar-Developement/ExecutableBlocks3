package com.ssomar.executableblocks.executableblocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;

public class ConfigConverter {


	public static void updateTo(File file) {

		FileConfiguration config; 
		try {
			config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}

		if (config.isConfigurationSection("activators")) {
			ConfigurationSection activatorsSection = config.getConfigurationSection("activators");
			for (String activatorID : activatorsSection.getKeys(false)) {

				ConfigurationSection activatorSection = activatorsSection.getConfigurationSection(activatorID);

				if (activatorSection.contains("entity")) {
					List<String> entityList = activatorSection.getStringList("entity");
					activatorSection.set("detailedEntities", entityList);
					activatorSection.set("entity", null);
				}
			}
		}

		if(!config.contains("config_1_2")) {
			if (config.isConfigurationSection("activators")) {
				ConfigurationSection activatorsSection = config.getConfigurationSection("activators");
				for (String activatorID : activatorsSection.getKeys(false)) {
					ConfigurationSection activatorSection = activatorsSection.getConfigurationSection(activatorID);
					
					if(activatorSection.contains("commands")) {
						List<String> commands = activatorSection.getStringList("commands");
						for(int i = 0; i < commands.size(); i++) {
							String command = commands.get(i);
							if(command.startsWith("AROUND")) {
								command = command.replaceAll("%target", "%around_target%");
								command = command.replaceAll("%target_uuid", "%around_target_uuid%");
								command = command.replaceAll("%target_x", "%around_target_x_int%");
								command = command.replaceAll("%target_y", "%around_target_y_int%");
								command = command.replaceAll("%target_z", "%around_target_z_int%");
							}
							command = command.replaceAll("%player%", "%owner%");
							command = command.replaceAll("%target%", "%player%");
							commands.set(i, command);
						}
						activatorSection.set("commands", commands);
					}

					if(activatorSection.contains("playerCommands")) {
						List<String> commands = activatorSection.getStringList("playerCommands");
						for(int i = 0; i < commands.size(); i++) {
							String command = commands.get(i);
							if(command.startsWith("AROUND")) {
								command = command.replaceAll("%target", "%around_target%");
								command = command.replaceAll("%target_uuid", "%around_target_uuid%");
								command = command.replaceAll("%target_x", "%around_target_x_int%");
								command = command.replaceAll("%target_y", "%around_target_y_int%");
								command = command.replaceAll("%target_z", "%around_target_z_int%");
							}
							commands.set(i, command);
						}
						activatorSection.set("playerCommands", commands);
					}
					
					if(activatorSection.contains("ownerCommands")) {
						List<String> commands = activatorSection.getStringList("ownerCommands");
						for(int i = 0; i < commands.size(); i++) {
							String command = commands.get(i);
							if(command.startsWith("AROUND")) {
								command = command.replaceAll("%target", "%around_target%");
								command = command.replaceAll("%target_uuid", "%around_target_uuid%");
								command = command.replaceAll("%target_x", "%around_target_x_int%");
								command = command.replaceAll("%target_y", "%around_target_y_int%");
								command = command.replaceAll("%target_z", "%around_target_z_int%");
							}
							command = command.replaceAll("%player%", "%owner%");
							command = command.replaceAll("%target%", "%player%");
							commands.set(i, command);
						}
						activatorSection.set("ownerCommands", commands);
					}



					if(activatorSection.contains("blockCommands")) {
						List<String> commands = activatorSection.getStringList("blockCommands");
						for(int i = 0; i < commands.size(); i++) {
							String command = commands.get(i);
							if(command.startsWith("AROUND")) {
								command = command.replaceAll("%target", "%around_target%");
								command = command.replaceAll("%target_uuid", "%around_target_uuid%");
								command = command.replaceAll("%target_x", "%around_target_x_int%");
								command = command.replaceAll("%target_y", "%around_target_y_int%");
								command = command.replaceAll("%target_z", "%around_target_z_int%");
							}
							commands.set(i, command);
						}
						activatorSection.set("blockCommands", commands);
					}
				}
			}
			config.set("config_1_2", "true");
			try {
				Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);

				try {
					writer.write(config.saveToString());
				} finally {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
