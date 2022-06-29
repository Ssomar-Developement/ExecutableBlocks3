package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.Optional;

public class PlayerJumpOnEvent implements Listener {

    @EventHandler
    public void onPlayerStatisticIncrementEvent(PlayerStatisticIncrementEvent e) {

        if (e.getStatistic().equals(Statistic.JUMP)) {
            Player target = e.getPlayer();

            Location pLoc = target.getLocation();
            pLoc.subtract(0, 1, 0);

            Block block = pLoc.getBlock();
            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

            //PlaceholderAPI.testMsg("Passe  jump: "+bLoc.toString());

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) return;
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

            EventInfo eInfo = new EventInfo(e);
            eInfo.setTargetPlayer(Optional.of(target));
            EventsManager.getInstance().activeOption(Option.PLAYER_JUMP_ON, eBP, eInfo);
        }
    }
}
