package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.events.BlockBreakEventExtension;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

public class PlayerBreakBlockEvent{

    public static void activatorBreak(BlockBreakEvent e){
        boolean fromMineInCube = false;
        if(e instanceof BlockBreakEventExtension){
            fromMineInCube = ((BlockBreakEventExtension)e).isMineInCubeCommand();
        }

        Player target = e.getPlayer();

        Block block = e.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        EventInfo eInfo = new EventInfo(e);
        eInfo.setTargetPlayer(Optional.of(target));
        eInfo.setEventCallByMineinCube(fromMineInCube);
        EventsManager.getInstance().activeOption(Option.PLAYER_BREAK, eBP, eInfo);
    }

    public static void activatorBreak(EntityDamageByEntityEvent e){

        Player target = (Player) e.getDamager();

        Block block = e.getEntity().getLocation().getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        EventInfo eInfo = new EventInfo(e);
        eInfo.setTargetPlayer(Optional.of(target));
        EventsManager.getInstance().activeOption(Option.PLAYER_BREAK, eBP, eInfo);
    }
}
