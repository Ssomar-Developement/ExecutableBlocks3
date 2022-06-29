package com.ssomar.executableblocks.executableblocks.placedblocks.data;

import java.io.File;
import java.util.*;

import com.ssomar.executableblocks.executableblocks.loader.NewExecutableBlockLoader;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.score.utils.Couple;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;

public class BlockReader {

	public static final String folder = "data";


	public static List<ExecutableBlockPlaced> getBlocksPlaced() { 

		List<ExecutableBlockPlaced> result = new ArrayList<>();

		List<String> listFiles = new ArrayList<>();
		try {
			listFiles = Arrays.asList(new File(ExecutableBlocks.getPluginSt().getDataFolder() + "/"+folder).list());
		}
		catch(Exception e) {}
		Collections.sort(listFiles);

		for (String s : listFiles) {
			File fileEntry = new File(ExecutableBlocks.getPluginSt().getDataFolder() + "/"+folder+"/" + s);
			if (!fileEntry.getName().contains(".yml")) continue;
			String currentId = fileEntry.getName().split(".yml")[0];
			Couple<ExecutableBlockPlaced, Boolean> couple = getBlockPlaced(currentId);
			ExecutableBlockPlaced eBP =  couple.getElem1();
			if(eBP != null) result.add(eBP);
			else if(couple.getElem2()) ExecutableBlocks.plugin.getServer().getLogger().severe("["+ExecutableBlocks.NAME+"] Error for the placed block: "+currentId+" not loaded !");
			else {
				/* EB file not found so this EBP is useless, we delete it */
				fileEntry.delete();
			}
		}
		return result;
	}

	/*
	* @return a couple with the ExecutableBlockPlaced (can be null) and boolean to know if the EB file associate with the EBP exists or not.
	*
	* */
	public static Couple<ExecutableBlockPlaced, Boolean> getBlockPlaced(String id) {

		Location location = null;
		Location holoLocation = null;
		UUID uuid = null;
		UUID ownerUUID = null;
		NewExecutableBlock eB = null;
		int usage;
		boolean ebFileExists = true;

		File file = new File(ExecutableBlocks.getPluginSt().getDataFolder() + "/"+folder+"/"+id+".yml");
		if(file.exists()) {
			FileConfiguration config;
			try {
				config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
			}
			catch (Exception e){
				ExecutableBlocks.plugin.getServer().getLogger().severe("["+ExecutableBlocks.NAME+"] Error for the placed block: "+id+" (location problem / world deleted)");
				return new Couple(null, ebFileExists);
			}
			String eBID = config.getString("executableBlockID", "");
			ebFileExists = fileOfEBExists(eBID);

			location = config.getLocation("location");
			if(location == null || location.getWorld() == null) {
				ExecutableBlocks.plugin.getServer().getLogger().severe("["+ExecutableBlocks.NAME+"] Error for the placed block: "+id+" (location)");
				return new Couple(null, ebFileExists);
			}

			holoLocation = config.getLocation("holoLocation");

			try {
				uuid = UUID.fromString(id);
			}
			catch(Exception e) {
				ExecutableBlocks.plugin.getServer().getLogger().severe("["+ExecutableBlocks.NAME+"] Error for the placed block: "+id+" (id)");
				return new Couple(null, ebFileExists);
			}

			String ownerUUIDStr = config.getString("ownerUUID");
			if(!ownerUUIDStr.equals("unowned")) {
				try {
					ownerUUID = UUID.fromString(ownerUUIDStr);
				}
				catch(Exception e) {
					ExecutableBlocks.plugin.getServer().getLogger().severe("["+ExecutableBlocks.NAME+"] Error for the placed block: "+id+" (ownerUUID)");
					return new Couple(null, ebFileExists);
				}
			}

			Optional<NewExecutableBlock> oOpt = NewExecutableBlocksManager.getInstance().getLoadedObjectWithID(eBID);
			if(!oOpt.isPresent()) {
				ExecutableBlocks.plugin.getServer().getLogger().severe("["+ExecutableBlocks.NAME+"] Error for the placed block: "+id+" (executableBlockID)");
				return new Couple(null, ebFileExists);
			}
			
			usage = config.getInt("usage", -1);

			eB = oOpt.get();
		}
		else return null;

		ExecutableBlockPlaced eBP = new ExecutableBlockPlaced(uuid, location, ownerUUID, eB.getId(), usage);
		eBP.setHoloLocation(holoLocation);
		return new Couple(eBP, ebFileExists);
	}

	public static boolean fileOfEBExists(String eBID){
		if(eBID.length() == 0) return false;
		return NewExecutableBlockLoader.getInstance().searchFileOfObject(eBID) != null;
	}
}
