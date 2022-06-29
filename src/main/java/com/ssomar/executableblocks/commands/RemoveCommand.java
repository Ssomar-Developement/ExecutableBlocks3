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
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.utils.SendMessage;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Optional;

public class RemoveCommand {

    /*
    *
    * Get region of Worldedit
    * for each block add the ExecutableBlock config with the good location
    * then run the /we set with the type of the EB
    *
    *
    * */

    public static void sendRemove(org.bukkit.entity.Player player, String[]args){
        boolean replaceTheEBbyAir = false;
        if(!ExecutableBlocks.hasWorldEdit){
            SendMessage.sendMessageNoPlch(player, "&4&l[ExecutableBlocks] &cERROR &6WorldEdit plugin&c is required for the command &6&o/eb we-remove [replaceTheEBbyAir]]");
            return;
        }
        if(args.length == 1){
            replaceTheEBbyAir = Boolean.parseBoolean(args[0]);
        }

            Player actor = BukkitAdapter.adapt(player); // WorldEdit's native Player class extends Actor
            SessionManager manager = WorldEdit.getInstance().getSessionManager();
            LocalSession localSession = manager.get(actor);

            Region region; // declare the region variable
            World selectionWorld = localSession.getSelectionWorld();
            try {
                if (selectionWorld == null) throw new IncompleteRegionException();
                region = localSession.getSelection(selectionWorld);
            } catch (IncompleteRegionException ex) {
                SendMessage.sendMessageNoPlch(player, "&4&l[ExecutableBlocks] &cERROR Please make a region selection first then &6&o/eb we-remove [replaceTheEBbyAir]");
                return;
            }

            for (BlockVector3 point : region){
                Location bLoc = new Location(BukkitAdapter.adapt(region.getWorld()), point.getBlockX(), point.getBlockY(), point.getBlockZ());
                bLoc = LocationConverter.convert(bLoc, false, true);

                /* REMOVE   */
                Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
                if (!eBPOpt.isPresent()) continue;
                ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

                ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);
                if(replaceTheEBbyAir) bLoc.getBlock().setType(Material.AIR);
            }
    }
}
