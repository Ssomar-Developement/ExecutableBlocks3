package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.score.SsomarDev;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;
import java.util.Optional;

public class PistonEvents implements Listener {

    private static final Boolean DEBUG = false;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent e) {
        breakCorrectlyModifiedBlocks(e.getBlocks(), e);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent e) {
        breakCorrectlyModifiedBlocks(e.getBlocks(), e);
    }

    public static void breakCorrectlyModifiedBlocks(List<Block> blocks, BlockPistonEvent e) {

        for (Block b : blocks) {

            SsomarDev.testMsg("PistonEvent + block: " + b.getType().name(), DEBUG);
            Location bLoc = LocationConverter.convert(b.getLocation(), false, false);


            @SuppressWarnings("unused")
            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (eBPOpt.isPresent()) {
                e.setCancelled(true);
                return;
            }

            for(BlockFace face : BlockFace.values()) {
                Block block = b.getRelative(face);
                if(block != null && block.getPistonMoveReaction().equals(PistonMoveReaction.BREAK)) {
                    Location bLoc2 = LocationConverter.convert(block.getLocation(), false, false);
                    Optional<ExecutableBlockPlacedInterface> eBP2Opt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc2);

                    if (eBP2Opt.isPresent()) {
                        ExecutableBlockPlaced eBP2 = (ExecutableBlockPlaced) eBP2Opt.get();
                        eBP2.breakBlock(null, true);
                    }
                }
            }
        }
    }
}
