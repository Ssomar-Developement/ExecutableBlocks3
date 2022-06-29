package com.ssomar.executableblocks.executableblocks.placedblocks;

import java.util.*;

import com.ssomar.executableblocks.utils.CreationType;
import com.ssomar.executableblocks.utils.Crops;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlocksPlacedManagerInterface;
import com.ssomar.score.utils.placeholders.StringPlaceholder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.placedblocks.data.BlockReader;
import com.ssomar.executableblocks.executableblocks.placedblocks.data.BlockWriter;
import org.bukkit.block.Block;

public class ExecutableBlockPlacedManager implements ExecutableBlocksPlacedManagerInterface {

	private Map<Location, ExecutableBlockPlaced> ExecutableBlocksPlaced = new HashMap<>();

	private static ExecutableBlockPlacedManager instance;

	public void load() {
		ExecutableBlocksPlaced = new HashMap<>();
		int i = 0;
		for(ExecutableBlockPlaced eBP : BlockReader.getBlocksPlaced()) {
			ExecutableBlocksPlaced.put(eBP.getLocation(), eBP);
			i++;

			Material material = eBP.getExecutableBlock().getMaterial().getValue().get();
			Material actual = eBP.getLocation().getBlock().getType();

			StringPlaceholder sp = new StringPlaceholder();
			if(eBP.getOwnerUUID().isPresent()){
				sp.setOwnerPlcHldr(eBP.getOwnerUUID().get());
			}
			eBP.getExecutableBlock().getTitle().spawn(eBP.getLocation(), sp);

			/* No check for the imported blocks */
			if(!eBP.getExecutableBlock().getCreationType().equals(CreationType.BASIC_CREATION)) continue;

			/* Exception for the heads */
			if(material.equals(Material.PLAYER_HEAD) && actual.equals(Material.PLAYER_WALL_HEAD)) continue;

			/* Exception for crops */
			if(Crops.getCrops().contains(material)) {
				if(!actual.equals(Crops.getBlockMatCrop(material))){
					eBP.getLocation().getBlock().setType(Crops.getBlockMatCrop(material));
					ExecutableBlocks.plugin.getServer().getLogger().info("["+ExecutableBlocks.NAME+"] ExecutableBlock(s)Placed "+eBP.getId()+" replaced !");
				}
				continue;
			}

			/* FAIRE ATTENTION AVEC CETTE METHODE  (PEUT CASSER LES PANNEAUX SI MAL GéRé) */
			if((material.toString().contains("SIGN") && !actual.toString().contains("WALL_SIGN"))
					|| (!material.toString().contains("SIGN") && !actual.equals(material)))
			{
				Material ebMat = eBP.getExecutableBlock().getMaterial().getValue().get();
				if(!ebMat.isBlock()) ebMat = Material.SPONGE;
				if(!material.equals(actual)) eBP.getLocation().getBlock().setType(ebMat);
				ExecutableBlocks.plugin.getServer().getLogger().info("["+ExecutableBlocks.NAME+"] ExecutableBlock(s)Placed "+eBP.getId()+" replaced !");
			}
		}
		ExecutableBlocks.plugin.getServer().getLogger().info("["+ExecutableBlocks.NAME+"] "+i+" ExecutableBlock(s)Placed loaded !");
	}	

	public void addExecutableBlockPlaced(ExecutableBlockPlaced eBP) {
		ExecutableBlocksPlaced.put(eBP.getLocation(), eBP);
		BlockWriter.write(eBP);
	}

	public void removeExecutableBlockPlaced(ExecutableBlockPlaced eBP) {
		if(eBP.getHoloLocation() != null) eBP.getExecutableBlock().getTitle().remove(eBP.getHoloLocation());
		ExecutableBlocksPlaced.remove(eBP.getLocation());
		BlockWriter.delete(eBP);
	}


	public Optional<ExecutableBlockPlacedInterface> getExecutableBlockPlaced(Location location) {
		if(ExecutableBlocksPlaced.containsKey(location)) {
			return Optional.ofNullable(ExecutableBlocksPlaced.get(location));
		}
		else return Optional.empty();
	}

	@Override
	public Optional<ExecutableBlockPlacedInterface> getExecutableBlockPlaced(Block block) {
		return getExecutableBlockPlaced(LocationConverter.convert(block.getLocation(), false, false));
	}


	public List<ExecutableBlockPlacedInterface> getExecutableBlocksPlaced(Chunk chunk) {
		int x = chunk.getX() * 16;
		int z = chunk.getZ() * 16;
		List<ExecutableBlockPlacedInterface> list = new ArrayList<>();
		for (Location loc : ExecutableBlocksPlaced.keySet()) {
			if (x >= loc.getX() && x <= loc.getX() + 16 && z >= loc.getZ() && z <= loc.getZ() + 16) {
				list.add(ExecutableBlocksPlaced.get(loc));
			}
		}
		return list;
	}

	@Override
	public List<ExecutableBlockPlacedInterface> getExecutableBlocksPlacedNear(Location location, double distanceMax) {
		List<ExecutableBlockPlacedInterface> list = new ArrayList<>();
		for(Location loc : ExecutableBlocksPlaced.keySet()) {
			if(loc.getWorld().equals(location.getWorld())) {
				double distance = location.distance(loc);
				//PlaceholderAPI.testMsg("near: "+distance);
				if(distance <= distanceMax) {
					list.add(ExecutableBlocksPlaced.get(loc));
				}
			}
		}
		return list;
	}

	@Override
	public Map<Location, ExecutableBlockPlacedInterface> getAllExecutableBlocksPlaced() {
		Map<Location, ExecutableBlockPlacedInterface> copy = new HashMap<>();
		copy.putAll(ExecutableBlocksPlaced);
		return copy;
	}

	public static ExecutableBlockPlacedManager getInstance() {
		if(instance == null) instance = new ExecutableBlockPlacedManager();
		return instance;
	}

}
