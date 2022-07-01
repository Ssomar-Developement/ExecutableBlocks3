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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EBPMovesListener implements Listener {

    private static final Boolean DEBUG = true;


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent e) {
        breakCorrectlyModifiedBlocks(e.getBlocks(), e);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent e) {
        breakCorrectlyModifiedBlocks(e.getBlocks(), e);
    }

    public static void breakCorrectlyModifiedBlocks(List<Block> blocks, BlockPistonEvent e) {
        List<Block> blocksImpactedByTheBlocksMoved = new ArrayList<>();
        List<ExecutableBlockPlaced> eBMoved = new ArrayList<>();
        for (Block b : blocks) {

            SsomarDev.testMsg("PistonEvent + block: " + b.getType().name(), DEBUG);
            Location bLoc = LocationConverter.convert(b.getLocation(), false, false);

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (eBPOpt.isPresent()) {
                ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();
                eBMoved.add(eBP);
                if (!eBP.getExecutableBlock().getCanBeMoved().getValue()) {
                    e.setCancelled(true);
                    return;
                }
            }

            List<BlockFace> faces = new ArrayList<>();
            faces.add(BlockFace.UP);
            for (BlockFace face : faces) {
                Block block = b.getRelative(face);
                blocksImpactedByTheBlocksMoved.add(block);
            }
        }

        /* Check block impacted */
        for (Block block : blocksImpactedByTheBlocksMoved) {
            if (block != null && block.getPistonMoveReaction().equals(PistonMoveReaction.BREAK)) {
                Location bLoc2 = LocationConverter.convert(block.getLocation(), false, false);
                Optional<ExecutableBlockPlacedInterface> eBP2Opt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc2);

                if (eBP2Opt.isPresent()) {
                    ExecutableBlockPlaced eBP2 = (ExecutableBlockPlaced) eBP2Opt.get();
                    eBP2.breakBlock(null, true);
                }
            }
        }

        /* Move the EBP correctly */
        for (ExecutableBlockPlaced eBP : eBMoved) {
            eBP.moveBlock(eBP.getLocation().getBlock().getRelative(e.getDirection()).getLocation());
        }
    }
}
