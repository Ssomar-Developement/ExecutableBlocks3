package com.ssomar.executableblocks.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.score.utils.SendMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Optional;

public class PlaceCommand {

    /*
    *
    * Get region of Worldedit
    * for each block add the ExecutableBlock config with the good location
    * then run the /we set with the type of the EB
    *
    *
    * */

    public static void sendPlace(org.bukkit.entity.Player player, String[]args){
        if(!ExecutableBlocks.hasWorldEdit){
            SendMessage.sendMessageNoPlch(player, "&4&l[ExecutableBlocks] &cERROR &6WorldEdit plugin&c is required for the command &6&o/eb we-place {ExecutableBlockID}");
            return;
        }
        if(args.length == 1){
            Optional<NewExecutableBlock> oOpt = NewExecutableBlocksManager.getInstance().getLoadedObjectWithID(args[0]);
            if(!oOpt.isPresent()) {
                SendMessage.sendMessageNoPlch(player, "&4&l[ExecutableBlocks] &cERROR Please select a valid executableBlocks. &7&o/eb we-place {ExecutableBlockID}");
                return;
            }
            NewExecutableBlock eB = oOpt.get();

            Player actor = BukkitAdapter.adapt(player); // WorldEdit's native Player class extends Actor
            SessionManager manager = WorldEdit.getInstance().getSessionManager();
            LocalSession localSession = manager.get(actor);

            Region region; // declare the region variable
            World selectionWorld = localSession.getSelectionWorld();
            try {
                if (selectionWorld == null) throw new IncompleteRegionException();
                region = localSession.getSelection(selectionWorld);
            } catch (IncompleteRegionException ex) {
                SendMessage.sendMessageNoPlch(player, "&4&l[ExecutableBlocks] &cERROR Please make a region selection first then &6&o/eb we-place {ExecutableBlockID}");
                return;
            }

            for (BlockVector3 point : region){
                Location bLoc = new Location(BukkitAdapter.adapt(region.getWorld()), point.getBlockX(), point.getBlockY(), point.getBlockZ());
                Block b = bLoc.getBlock();
                b.setType(Material.AIR);
                eB.place(Optional.ofNullable(player), bLoc, true);
            }
        }
        else SendMessage.sendMessageNoPlch(player, "&4&l[ExecutableBlocks] &cERROR not enought arguments &6&o/eb we-place {ExecutableBlockID}");
    }
}
