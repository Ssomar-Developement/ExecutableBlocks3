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
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

public class PlayerDeathOnEvent implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {

        //SsomarDev.testMsg("étape 1 " + this.toString() + " " + e.getEventName());

        Player p = e.getEntity();
        Location pLoc = p.getLocation();
        pLoc = pLoc.subtract(0, 1, 0);
        Block block = pLoc.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        EventInfo eInfo = new EventInfo(e);
        eInfo.setTargetPlayer(Optional.of(p));
        EventsManager.getInstance().activeOption(Option.PLAYER_DEATH_ON, eBP, eInfo);
    }
}
