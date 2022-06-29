package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.Optional;

public class PlayerFallOnEvent implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            Player target = (Player) e.getEntity();

            ExecutableBlockPlaced eBP = null;
            List<ExecutableBlockPlacedInterface> list = ExecutableBlockPlacedManager.getInstance().getExecutableBlocksPlacedNear(LocationConverter.convert(target.getLocation(), true, false), 0.8);
            if(list.size() > 0) eBP = (ExecutableBlockPlaced) list.get(0);
            if (eBP == null) return;

            EventInfo eInfo = new EventInfo(e);
            eInfo.setTargetPlayer(Optional.of(target));
            EventsManager.getInstance().activeOption(Option.PLAYER_FALL_ON, eBP, eInfo);
        }
    }
}
