package com.ssomar.executableblocks.executableblocks;

import com.google.common.base.Charsets;
import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableitems.ExecutableItems;
import com.ssomar.score.SCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionType;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConfigConverter {


    public static void updateTo(File file) {

        FileConfiguration config;
        try {
            config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (!config.contains("config_3")) {
            NewExecutableBlock eb = new NewExecutableBlock("test", "");
            config.set("config_3", true);

            eb.getDisplayName().setValue(Optional.of(config.getString("name", "")));
            eb.getLore().setValue(config.getStringList("lore"));

            String mat = config.getString("material", "");

            try {
                eb.getMaterial().setValue(Optional.of(Material.valueOf(mat)));
            } catch (Exception e) {
            }

            eb.getUsage().setValue(Optional.of(config.getInt("usage", 0)));


            if (config.isConfigurationSection("activators")) {
                ConfigurationSection activatorsSection = config.getConfigurationSection("activators");
                for (String activatorID : activatorsSection.getKeys(false)) {

                    ConfigurationSection activatorSection = activatorsSection.getConfigurationSection(activatorID);
                    String optionsStr = activatorSection.getString("activator", "NO_OPTION_IN_CONFIG");
                    activatorSection.set("option", optionsStr);

                    if (activatorSection.isConfigurationSection("conditions.playerConditions")) {
                        ConfigurationSection playerConditionsSection = activatorSection.getConfigurationSection("conditions.playerConditions");
                        activatorSection.set("playerConditions", playerConditionsSection);
                    }

					if (activatorSection.isConfigurationSection("conditions.ownerConditions")) {
						ConfigurationSection playerConditionsSection = activatorSection.getConfigurationSection("conditions.ownerConditions");
						activatorSection.set("ownerConditions", playerConditionsSection);
					}

                    List<String> inplayerCdt = new ArrayList<>();
                    inplayerCdt.add("playerConditions");
                    inplayerCdt.add("ownerConditions");
                    for (String s : inplayerCdt) {
                        if (activatorSection.isConfigurationSection("conditions." + s + ".IfPlayerMustBeInHisTown")) {
                            activatorSection.set(s + ".ifPlayerMustBeInHisTown", activatorSection.getBoolean("conditions." + s + ".IfPlayerMustBeInHisTown", false));
                            activatorSection.set(s + ".ifPlayerMustBeInHisTownMsg", activatorSection.getString("conditions." + s + ".IfPlayerMustBeInHisTownMsg", ""));
                            activatorSection.set(s + ".ifPlayerMustBeInHisTownCE", activatorSection.getBoolean("conditions." + s + ".IfPlayerMustBeInHisTownCE", false));
                        }

                        if (activatorSection.isConfigurationSection("conditions." + s + ".ifPlayerHasExecutableItem")) {
                            ConfigurationSection executableItemSection = activatorSection.getConfigurationSection("conditions." + s + ".ifPlayerHasExecutableItem");
                            for (String executableItemID : executableItemSection.getKeys(false)) {
                                ConfigurationSection executableItemSection2 = executableItemSection.getConfigurationSection(executableItemID);
                                activatorSection.set(s + ".ifHasExecutableItems." + executableItemID + ".executableItem", executableItemSection2.getString("executableItemID", ""));
                                int slot = executableItemSection2.getInt("slot", -5);
                                if (slot > -2) {
                                    activatorSection.set(s + ".ifHasExecutableItems." + executableItemID + ".detailedSlots", new ArrayList<>(Arrays.asList(slot)));
                                }
                                activatorSection.set(s + ".ifHasExecutableItems." + executableItemID + ".usageCondition", executableItemSection2.getString("usageCalcul", ""));
                            }
                        }
                        if (activatorSection.isConfigurationSection("conditions." + s + ".ifPlayerHasExecutableItemMsg")) {
                            activatorSection.set(s + ".ifHasExecutableItemsMsg", activatorSection.getString("conditions." + s + ".ifPlayerHasExecutableItemMsg", ""));
                        }
                        if (activatorSection.isConfigurationSection("conditions." + s + ".ifPlayerHasExecutableItemCE")) {
                            activatorSection.set(s + ".ifHasExecutableItemsCE", activatorSection.getBoolean("conditions." + s + ".ifPlayerHasExecutableItemCE", false));
                        }

                        List<String> items = activatorSection.getStringList("conditions." + s + ".ifPlayerHasItem");
                        int j = 0;
                        for (String item : items) {
                            String[] itemSplit = item.split(":");
                            if (itemSplit.length == 2) {
                                activatorSection.set(s + ".ifHasItems.hasItem" + j + ".material", itemSplit[0]);
                                activatorSection.set(s + ".ifHasItems.hasItem" + j + ".amount", itemSplit[1]);
                            }
                            j++;
                        }

                        if (activatorSection.isConfigurationSection("conditions." + s + ".ifPlayerHasItemMsg")) {
                            activatorSection.set(s + ".ifHasItemsMsg", activatorSection.getString("conditions." + s + ".ifPlayerHasItemMsg", ""));
                        }
                        if (activatorSection.isConfigurationSection("conditions." + s + ".ifPlayerHasItemCE")) {
                            activatorSection.set(s + ".ifHasItemsCE", activatorSection.getBoolean("conditions." + s + ".ifPlayerHasItemCE", false));
                        }
                    }

                    if (activatorSection.isConfigurationSection("conditions.entityConditions")) {
                        ConfigurationSection playerConditionsSection = activatorSection.getConfigurationSection("conditions.entityConditions");
                        activatorSection.set("entityConditions", playerConditionsSection);
                    }

                    if (activatorSection.isConfigurationSection("conditions.worldConditions")) {
                        ConfigurationSection playerConditionsSection = activatorSection.getConfigurationSection("conditions.worldConditions");
                        activatorSection.set("worldConditions", playerConditionsSection);
                    }

					if (activatorSection.isConfigurationSection("conditions.blockConditions")) {
						ConfigurationSection playerConditionsSection = activatorSection.getConfigurationSection("conditions.blockConditions");
						activatorSection.set("blockConditions", playerConditionsSection);
					}

                    if (activatorSection.isConfigurationSection("conditions.targetBlockConditions")) {
                        ConfigurationSection playerConditionsSection = activatorSection.getConfigurationSection("conditions.targetBlockConditions");
                        activatorSection.set("targetBlockConditions", playerConditionsSection);
                    }

                    if (activatorSection.isConfigurationSection("conditions.placeholdersConditions")) {
                        ConfigurationSection playerConditionsSection = activatorSection.getConfigurationSection("conditions.placeholdersConditions");
                        activatorSection.set("placeholdersConditions", playerConditionsSection);
                    }

                    if (activatorSection.contains("detailedBlocks")) {
                        List<String> detailedBlocks = activatorSection.getStringList("detailedBlocks");
                        activatorSection.set("detailedBlocks.blocks", detailedBlocks);
                    }
                    activatorSection.set("detailedBlocks.cancelEventIfNotValid", activatorSection.getBoolean("cancelEventIfNotDetailedBlocks", false));

                    if (!activatorSection.contains("detailedSlots")) {
                        activatorSection.set("detailedSlots", new ArrayList<>(Arrays.asList(-1)));
                    }
                }
            }

            eb.getActivators().load(ExecutableBlocks.plugin, config, true);

            for (String s : config.getKeys(false)) {
                config.set(s, null);
            }
            eb.save(config);
            config.set("config_3", true);

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
