package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class EBPExplodesListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplodeEvent(BlockExplodeEvent e) {
        List<Block> blocks = e.blockList();
        blocks.add(e.getBlock());
        explosion(blocks, e);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
        explosion(e.blockList(), e);
    }

    public void explosion(List<Block> blocks, Event e){
        for (Block block : blocks) {
            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) return;
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

            ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);

            if(e instanceof BlockExplodeEvent)
                ((BlockExplodeEvent) e).setYield(0);
            else if(e instanceof EntityExplodeEvent){
                ((EntityExplodeEvent) e).setYield(0);
            }

            if (eBP.getExecutableBlock().getDropBlockWhenItExplodes().getValue()) {
                ItemStack is = eBP.getExecutableBlock().buildItem(1, Optional.empty());
                bLoc.getWorld().dropItem(bLoc, is);
            }
        }
    }

}
