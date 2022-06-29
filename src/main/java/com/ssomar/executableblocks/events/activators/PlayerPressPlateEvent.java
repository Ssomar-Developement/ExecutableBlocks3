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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class PlayerPressPlateEvent implements Listener {

    @EventHandler
    public void onPlayerPressPlateEvent(com.ssomar.sevents.events.player.press.PlayerPressPlateEvent e){
        if(e.hasBlock()){
            Block block = e.getBlock();
            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);
            Player p = e.getPlayer();

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) return;
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

            EventInfo eInfo = new EventInfo(e);
            eInfo.setTargetPlayer(Optional.of(p));
            EventsManager.getInstance().activeOption(Option.PLAYER_PRESS, eBP, eInfo);
        }
    }
}
