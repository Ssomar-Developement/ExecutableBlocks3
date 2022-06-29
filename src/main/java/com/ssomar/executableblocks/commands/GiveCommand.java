package com.ssomar.executableblocks.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.configs.messages.Message;
import com.ssomar.score.SCore;
import com.ssomar.score.configs.messages.MessageMain;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.StringConverter;

public class GiveCommand {

	private static SendMessage sm = new SendMessage();


	public GiveCommand() {}

	public boolean simpleGive(Player p, NewExecutableBlock eBlock) {

		Map<Integer,ItemStack> over= p.getInventory().addItem(eBlock.buildItem(1, Optional.ofNullable(p)));
		if(over.size()>0) {
			for(Integer index: over.keySet()) {
				p.getWorld().dropItem(p.getLocation(), over.get(index));
			}
			return false;
		}

		return true;
	}

	public void multipleGive(Player p, NewExecutableBlock eBlock, int quantity) {

		int cptOut = 0;

		for(int i = 0; i < quantity; i++) {
			if(!this.simpleGive(p, eBlock)) cptOut++;
		}
		
		if(cptOut != 0) sm.sendMessage(p,StringConverter.replaceVariable(MessageMain.getInstance().getMessage(ExecutableBlocks.plugin, Message.FULL_INVENTORY), p.getName(), eBlock.getName(), cptOut+"", 0));
		sm.sendMessage(p,StringConverter.replaceVariable(MessageMain.getInstance().getMessage(ExecutableBlocks.plugin, Message.RECEIVE_BLOCK_MESSAGE), p.getName(), eBlock.getName(), quantity+"", 0));
	}

	public GiveCommand(CommandSender sender, String[] args, boolean all) {
		Player p;
		NewExecutableBlock eBlock;
		int quantity;
		if(all) {

			if (args.length >= 1) {
				Optional<NewExecutableBlock> oOpt = NewExecutableBlocksManager.getInstance().getLoadedObjectWithID(args[0]);
				if (oOpt.isPresent()) {
					eBlock = oOpt.get();
				} else {
					sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Block " + args[0] + " not found");
					return;
				} 
				if (args.length > 1) {
					if (args[1].matches("\\d+")) {
						quantity = Integer.valueOf(args[1]).intValue();
						if(quantity > 100) {
							sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Quantity > 100 is blocked for security !");
							return;
						}
					} else {
						sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Quantity " + args[1] + " is invalid.");
						return;
					} 
				} else {
					quantity = 1;
				}
				World world = null;
				if (args.length > 2) {
					try {
						world = Bukkit.getServer().getWorld(args[2]);
					}catch(Exception e) {}
				}
				List<String> playerList = new ArrayList<>();
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(world != null) {
						if(player.getWorld() == world) {
							this.multipleGive(player, eBlock, quantity);
							playerList.add(player.getName());
						}
					}
					else {
						this.multipleGive(player, eBlock, quantity);
						playerList.add(player.getName());
					}	
				}
				sender.sendMessage(StringConverter.replaceVariable(MessageMain.getInstance().getMessage(SCore.plugin, Message.GIVE_MESSAGE), "all players", eBlock.getName(), quantity+"", 0));
			} else {
				sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Use /eb giveall BLOCK_ID QUANTITY");
			} 

		}
		else {
			try {
				if (sender.getServer().getPlayerExact(args[0]) != null) {
					p = sender.getServer().getPlayerExact(args[0]);
					Optional<NewExecutableBlock> oOpt = NewExecutableBlocksManager.getInstance().getLoadedObjectWithID(args[1]);
					if (oOpt.isPresent()) {
						eBlock = oOpt.get();
					} else {
						sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Block " + args[1] + " not found");
						return;
					}
					if (args.length > 2) {
						if (args[2].matches("\\d+")) {
							quantity = Integer.valueOf(args[2]).intValue();
							if(quantity>100) {
								sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Quantity > 100 is blocked for security !");
								return;
							}
						} else {
							sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Quantity " + args[2] + " is invalid.");
							return;
						} 
					} else {
						quantity = 1;
					} 
					this.multipleGive(p, eBlock, quantity);
					sender.sendMessage(StringConverter.replaceVariable(MessageMain.getInstance().getMessage(SCore.plugin, Message.GIVE_MESSAGE), p.getName(), eBlock.getName(), quantity+"", 0));

				}else {
					sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Player " + args[0] + " is not online."); 
					return;
				}
			}catch(Exception e) {
				//e.printStackTrace();
				sender.sendMessage(ChatColor.RED + ExecutableBlocks.NAME_2+" Use /eb give PLAYER BLOCK_ID QUANTITY"); 
			}
		}


	}

}
