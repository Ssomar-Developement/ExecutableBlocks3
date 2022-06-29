package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Optional;

public class CropGrowEvent implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockGrowEvent(BlockGrowEvent e) {

        Block block = e.getBlock();
        if(block.getType().equals(Material.AIR)) return;

        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);
        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        EventInfo eInfo = new EventInfo(e);

        EventsManager.getInstance().activeOption(Option.CROP_GROW, eBP, eInfo);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStructureGrowEvent(StructureGrowEvent e) {

        Block block = e.getLocation().getBlock();
        if(block.getType().equals(Material.AIR)) return;

        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);
        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        EventInfo eInfo = new EventInfo(e);

        EventsManager.getInstance().activeOption(Option.CROP_GROW, eBP, eInfo);
    }

}
