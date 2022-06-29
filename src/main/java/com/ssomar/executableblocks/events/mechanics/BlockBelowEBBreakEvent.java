package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;

public class BlockBelowEBBreakEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent e) {

        Block block = e.getBlock();

        Block downBlock = e.getBlock().getRelative(BlockFace.UP);

        if (downBlock != null &&
                ( downBlock.getPistonMoveReaction().equals(PistonMoveReaction.BREAK)
                ||  downBlock.getType().toString().contains("CARPET")
                ||  (downBlock.getType().toString().contains("SIGN") && !(downBlock.getBlockData() instanceof WallSign)))
        ) {
            breakIfEB(downBlock);
        }

        Block eastBlock = block.getRelative(BlockFace.EAST);
        if(eastBlock.getType().toString().contains("SIGN") && (eastBlock.getBlockData() instanceof WallSign) && ((WallSign) eastBlock.getBlockData()).getFacing().equals(BlockFace.EAST)){
            breakIfEB(eastBlock);
        }

        Block westBlock = block.getRelative(BlockFace.WEST);
        if(westBlock.getType().toString().contains("SIGN") && (westBlock.getBlockData() instanceof WallSign) && ((WallSign) westBlock.getBlockData()).getFacing().equals(BlockFace.WEST)){
            breakIfEB(westBlock);
        }

        Block northBlock = block.getRelative(BlockFace.NORTH);
        if(northBlock.getType().toString().contains("SIGN") && (northBlock.getBlockData() instanceof WallSign) && ((WallSign) northBlock.getBlockData()).getFacing().equals(BlockFace.NORTH)){
            breakIfEB(northBlock);
        }

        Block southBlock = block.getRelative(BlockFace.SOUTH);
        if(southBlock.getType().toString().contains("SIGN") && (southBlock.getBlockData() instanceof WallSign) && ((WallSign) southBlock.getBlockData()).getFacing().equals(BlockFace.SOUTH)){
            breakIfEB(southBlock);
        }

    }

    public static void breakIfEB(Block block) {
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (eBPOpt.isPresent()) {
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();
            eBP.breakBlock(null, true);
        }
    }

}
