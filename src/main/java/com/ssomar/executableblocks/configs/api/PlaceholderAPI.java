package com.ssomar.executableblocks.configs.api;

import org.bukkit.Bukkit;

public class PlaceholderAPI {

	// TRUE = FREE
	private static final boolean placeOfWork = false;

	public static boolean isLotOfWork() {
		return placeOfWork;
	}
	
	public static void testMsg(String message) {
		try {
			Bukkit.getPlayer("Ssomar").sendMessage(message);
		}catch(Exception err) {
			
		}
	}
}
