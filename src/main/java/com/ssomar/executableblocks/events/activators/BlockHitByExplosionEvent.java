package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Optional;

public class BlockHitByExplosionEvent implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplodeEvent(EntityExplodeEvent e) {

        for(Block block : e.blockList()) {

            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);
            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) continue;
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();
            EventInfo eInfo = new EventInfo(e);

            EventsManager.getInstance().activeOption(Option.EXPLOSION_HIT, eBP, eInfo);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockExplodeEvent(BlockExplodeEvent e) {

        Block block = e.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        EventInfo eInfo = new EventInfo(e);

        EventsManager.getInstance().activeOption(Option.EXPLOSION_HIT, eBP, eInfo);
    }
}
