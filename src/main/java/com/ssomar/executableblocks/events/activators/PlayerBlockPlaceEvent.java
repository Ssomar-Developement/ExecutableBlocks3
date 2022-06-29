package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public class PlayerBlockPlaceEvent implements Listener {

    /* CALL IN com.ssomar.executableblocks.events.mechanics.MechanicBlockModificationEvent.onBlockPlaceEvent */
    public static void onBlockPlaceEvent(BlockPlaceEvent e, ExecutableBlockPlaced eBP) {

        Player p = e.getPlayer();
        EventInfo eInfo = new EventInfo(e);
        eInfo.setTargetPlayer(Optional.of(p));
        EventsManager.getInstance().activeOption(Option.PLAYER_PLACE, eBP, eInfo);
    }
}
