package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MechanicFallBlockEvent implements Listener {

    private Map<UUID, ExecutableBlockPlaced> fallBlocks = new HashMap<>();

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent e) {
        Entity ent = e.getEntity();
        Block block = e.getBlock();

        Location bLoc = LocationConverter.convert(block.getLocation(), false, true);
        UUID entUUID = ent.getUniqueId();

        if (fallBlocks.containsKey(entUUID)) {
            ExecutableBlockPlaced exBP = fallBlocks.get(entUUID);
            NewExecutableBlock eb = exBP.getExecutableBlock();
            eb.place(Optional.ofNullable(exBP.getOwner()), block.getLocation(), false);
            fallBlocks.remove(entUUID);
            return;
        }

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();


        if (!(ent instanceof FallingBlock)) return;

        if (eBP.getExecutableBlock().getCancelGravity().getValue()) {
            e.setCancelled(true);
            return;
        } else eBP.remove();

        fallBlocks.put(entUUID, eBP);
        ((FallingBlock) ent).setDropItem(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                /* No drop if the block has been placed */
                if(!fallBlocks.containsKey(entUUID)){
                    cancel();
                    return;
                }
                if(ent == null || ent.isDead()){
                    eBP.drop(ent.getLocation());
                    cancel();
                }
           }
        }.runTaskTimer(ExecutableBlocks.plugin, 0, 10);
    }
}
