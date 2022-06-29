package com.ssomar.executableblocks.executableblocks.placedblocks.data;

import com.google.common.base.Charsets;
import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;

public class BlockWriter {

    public static final String folder = "data";

    public static void write(ExecutableBlockPlaced eBP) {
        File normalInvFolder = new File(ExecutableBlocks.getPluginSt().getDataFolder() + "/" + folder);
        normalInvFolder.mkdirs();

        File file = null;
        try {
            file = new File(ExecutableBlocks.getPluginSt().getDataFolder() + "/" + folder + "/" + eBP.getId() + ".yml");
            file.createNewFile();
        } catch (IOException e) {
        }

        FileConfiguration config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);

        if (!eBP.getOwnerUUID().isPresent()) {
            config.set("owner", "unowned");
            config.set("ownerUUID", "unowned");
        } else {
            Player owner = eBP.getOwner();
            if (owner != null) config.set("owner", owner.getName());
            else config.set("owner", "OWNER NAME NOT FOUND");
            config.set("ownerUUID", eBP.getOwnerUUID().get().toString());
        }

        config.set("location", eBP.getLocation());
        config.set("executableBlockID", eBP.getExecutableBlock().getId());

        config.set("usage", eBP.getUsage());

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

    public static void delete(ExecutableBlockPlaced eBP) {
        File normalInvFolder = new File(ExecutableBlocks.getPluginSt().getDataFolder() + "/" + folder);
        normalInvFolder.mkdirs();

        File file = null;
        file = new File(ExecutableBlocks.getPluginSt().getDataFolder() + "/" + folder + "/" + eBP.getId() + ".yml");
        file.delete();
    }
}
