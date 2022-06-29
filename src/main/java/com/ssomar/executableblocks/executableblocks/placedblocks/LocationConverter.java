package com.ssomar.executableblocks.executableblocks.placedblocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;

public class LocationConverter {


	/* When near is true we dont add 0.5 to have the exact position
	 * 
	 * for placement we dont want the methods to adapt the height
	 * */
	public static Location convert(Location location, boolean near, boolean placement) {
		Location result = location.clone();

		if(!near) result.add(0.5, 0.5, 0.5);
		
		/* TRAPDOOR is bissected but in the same block, so we ignore it */
		if(!placement) {
			Block block = result.getBlock();
			if(block.getBlockData() instanceof Bisected && !block.getType().toString().contains("TRAPDOOR") && ((Bisected) result.getBlock().getBlockData()).getHalf().equals(Half.TOP)) {				
				result.subtract(0, 1, 0);
			}
		}

		return result;
	}

//	public static List<Material> getBlock2Height(){
//		List<Material> result = new ArrayList<>();
//
//		for(Material mat : Material.values()) {
//			if(mat.toString().contains("DOOR")) {
//				result.add(mat);
//			}
//		}
//
//		return result;
//	}
}
