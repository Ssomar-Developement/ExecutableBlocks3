package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.sobject.sactivator.EventInfo;
import com.ssomar.sevents.events.player.sneak.active.PlayerActiveSneakEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

public class PlayerSneakOnEvent implements Listener {

    @EventHandler
    public void onPlayerSneakOnEvent(PlayerActiveSneakEvent e){
        Player target = e.getPlayer();
        Block block = target.getLocation().getBlock();
        if(block.getType().equals(Material.AIR)) block = target.getLocation().subtract(0,1,0).getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        ExecutableBlockPlaced eBP = null;
        List<ExecutableBlockPlacedInterface> list = ExecutableBlockPlacedManager.getInstance().getExecutableBlocksPlacedNear(bLoc, 0.8);
        if(list.size() > 0) eBP = (ExecutableBlockPlaced) list.get(0);
        if(eBP == null) return;

        EventInfo eInfo = new EventInfo(e);
        eInfo.setTargetPlayer(Optional.of(target));
        EventsManager.getInstance().activeOption(Option.PLAYER_SNEAK_ON, eBP, eInfo);
    }
}
