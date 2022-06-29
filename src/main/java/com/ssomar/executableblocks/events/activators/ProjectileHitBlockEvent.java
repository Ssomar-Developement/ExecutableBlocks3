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
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class ProjectileHitBlockEvent implements Listener {

    @EventHandler
    public void onProjectileHitBlockEvent(com.ssomar.sevents.events.projectile.hitblock.ProjectileHitBlockEvent e){
        Block block = e.getBlock();

        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        EventInfo eInfo = new EventInfo(e);
        Projectile proj = (Projectile) e.getEntity();
        eInfo.setTargetEntity(Optional.of(proj));
        if(proj.getShooter() instanceof Player) {
            eInfo.setTargetPlayer(Optional.of((Player) proj.getShooter()));
        }

        EventsManager.getInstance().activeOption(Option.PROJECTILE_HIT, eBP, eInfo);
    }
}
