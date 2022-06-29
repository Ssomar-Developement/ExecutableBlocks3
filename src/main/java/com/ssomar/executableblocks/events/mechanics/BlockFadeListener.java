package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

import java.util.Optional;

public class BlockFadeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFadeEvent(BlockFadeEvent e) {

        //SsomarDev.testMsg("BlockFadeEvent "+e.getBlock().getType());

        Block block = e.getBlock();

        if (block != null) {

            if(block.getType().toString().toUpperCase().contains("CORAL")) return;

            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) return;
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();
            eBP.remove();
        }
    }
}
