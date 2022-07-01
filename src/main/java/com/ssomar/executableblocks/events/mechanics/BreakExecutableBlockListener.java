package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.events.activators.PlayerBreakBlockEvent;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.score.SsomarDev;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BreakExecutableBlockListener implements Listener {

    private static final Boolean DEBUG = true;

    /* Take the priority of Orawen or ItemsAdder */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreakEventOraxenOrItemsAdder(BlockBreakEvent e) {
        SsomarDev.testMsg("BlockBreakEvent", DEBUG);
        Player target = e.getPlayer();
        Block block = e.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);
        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        SsomarDev.testMsg("BlockBreakEvent eBP found: " + eBPOpt.isPresent(), DEBUG);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();
        NewExecutableBlock eB = eBP.getExecutableBlock();

        if (!eB.hasBlockPerm(target, true)) {
            SsomarDev.testMsg("BlockBreakEvent NOT PERM", DEBUG);
            e.setCancelled(true);
            return;
        }else {
            SsomarDev.testMsg("BlockBreakEvent PERM", DEBUG);
        }


        ItemStack item1 = target.getInventory().getItemInMainHand();
        if (item1.getType().equals(Material.AIR)) {
            item1 = target.getInventory().getItemInOffHand();
        }

        if (!eB.getOnlyBreakableWithEI().isValid(item1)) {
            SsomarDev.testMsg("BlockBreakEvent NOT THE GOOD EI", DEBUG);
            e.setCancelled(true);
            return;
        }


        /* TODO if (!eB.getOraxenTextures().isHasOraxenTexture() && !eB.getItemsAdderBlockFeatures().isHasItemsAdderBlock()){
            SsomarDev.testMsg("BlockBreakEvent NOT ORAXEN OR ITEMSADDER", DEBUG);
            return;
        }*/

        PlayerBreakBlockEvent.activatorBreak(e);

        /* Check if the EB placed has been removed during the activation of the activatiors BREAK */
        if(eBP.isRemoved()) return;

        /* No remove if the event is cancelled */
        if (e.isCancelled()){
            SsomarDev.testMsg("BlockBreakEvent CANCELLED", DEBUG);
            return;
        }

        //TODO if (eB.getOraxenTextures().isHasOraxenTexture()) eB.getOraxenTextures().removePlaceDOraxenBlock(block);

        SsomarDev.testMsg("BlockBreakEvent BREAK", DEBUG);
        eBP.breakBlock(target, true);

        e.setCancelled(true);
        block.setType(Material.AIR);
    }

    /** IIRC ITS FOR ITEMSADDER **/
    /* @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent e) {

        Player target = e.getPlayer();

        Block block = e.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);


        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        PlayerBreakBlockEvent.activatorBreak(e);

        e.setDropItems(false);

        if (e.isCancelled()) return;

        ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);
        block.setType(Material.AIR);

        if (eBP.getExecutableBlock().isDropBlockIfItIsBroken()) {
            ItemStack is = eBP.getExecutableBlock().buildItem(1, Optional.ofNullable(target));
            if (!target.getInventory().addItem(is).isEmpty()) {
                bLoc.getWorld().dropItem(bLoc, is);
            }
        }
    }*/
}
