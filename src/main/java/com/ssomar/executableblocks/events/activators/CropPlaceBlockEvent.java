package com.ssomar.executableblocks.events.activators;

import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.SCore;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class CropPlaceBlockEvent implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockGrowEvent(BlockGrowEvent e) {

        Block block = e.getBlock();
        if(block.getType().equals(Material.AIR)){
            BlockState bS = e.getNewState();
            Block newBlock = bS.getBlock();
            Material material = bS.getType();

            BukkitRunnable runnable = new BukkitRunnable(){

                public void run(){
                    Optional<Block> stemOpt = checkAllStemPossibility(newBlock, material);
                    if(stemOpt.isPresent()){
                        Block stem = stemOpt.get();
                        Location bLoc = LocationConverter.convert(stem.getLocation(), false, false);
                        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
                        if (!eBPOpt.isPresent()) return;

                        EventInfo eInfo = new EventInfo(e);
                        eInfo.setTargetBlock(Optional.of(newBlock));

                        EventsManager.getInstance().activeOption(Option.CROP_PLACE_BLOCK, (ExecutableBlockPlaced) eBPOpt.get(), eInfo);
                    }

                }
            };
            runnable.runTaskLater(SCore.plugin, 1);
        }
    }

    public Optional<Block> checkAllStemPossibility(Block block, Material growMat){
        Material stemMat;
        if(growMat.equals(Material.PUMPKIN)){
            stemMat = Material.ATTACHED_PUMPKIN_STEM;
        }
        else if(growMat.equals(Material.MELON)){
            stemMat = Material.ATTACHED_MELON_STEM;
        }
        else return Optional.ofNullable(null);

        BlockFace[] toCheck = {BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};
        for(BlockFace blockFace: toCheck){
            Optional<Block> stemOpt = checkIfCorrectStem(block, blockFace, stemMat);
            if(stemOpt.isPresent()) return stemOpt;
        }
        return Optional.ofNullable(null);
    }

    public Optional<Block> checkIfCorrectStem(Block block, BlockFace blockFace, Material stemMat){
        Block potentialStem = block.getRelative(blockFace);
        if(potentialStem.getType().equals(stemMat)){
            BlockData pSData = potentialStem.getBlockData();
            if(pSData instanceof Directional){
                Directional pSDir = (Directional) pSData;
                if(pSDir.getFacing().getOppositeFace().equals(blockFace)){
                    return Optional.ofNullable(potentialStem);
                }
            }
        }
        return Optional.ofNullable(null);
    }
}
