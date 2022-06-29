package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.activators.PlayerBreakBlockEvent;
import com.ssomar.score.SCore;
import com.ssomar.score.SsomarDev;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
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

    private static final Boolean DEBUG = false;

    /* Take the priority of Orawen or ItemsAdder */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreakEventOraxenOrItemsAdder(BlockBreakEvent e) {
        if(DEBUG) SsomarDev.testMsg("BlockBreakEvent");
        Player target = e.getPlayer();
        Block block = e.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);
        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();
        NewExecutableBlock eB = eBP.getExecutableBlock();

        if (!eB.hasBlockPerm(target, true)) {
            e.setCancelled(true);
            return;
        }

        if (SCore.hasExecutableItems && eB.getOnlyBreakableWithEI().size() > 0) {

            ItemStack item1 = target.getInventory().getItemInMainHand();
            if (item1.getType().equals(Material.AIR)) {
                item1 = target.getInventory().getItemInOffHand();
            }
            if (item1.getType().equals(Material.AIR)) {
                e.setCancelled(true);
                return;
            }
            Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(item1);
            if (!eiOpt.isPresent() || !eBP.getExecutableBlock().verifyOnlyBreakableWithEI(eiOpt.get().getId())) {
                e.setCancelled(true);
                return;
            }
        }

        if(!eB.getOraxenTextures().isHasOraxenTexture() && !eB.getItemsAdderBlockFeatures().isHasItemsAdderBlock()) return;

        PlayerBreakBlockEvent.activatorBreak(e);

        /* No remove if the event is cancelled */
        if (e.isCancelled()) return;

        if(eB.getOraxenTextures().isHasOraxenTexture()) eB.getOraxenTextures().removePlaceDOraxenBlock(block);

        eBP.breakBlock(target, true);

        e.setCancelled(true);
        block.setType(Material.AIR);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent e) {

        Player target = e.getPlayer();

        Block block = e.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);


        //PlaceholderAPI.testMsg("Passe  jump: "+bLoc.toString());

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        PlayerBreakBlockEvent.activatorBreak(e);

        e.setDropItems(false);
        /* No remove if the event is cancelled */
        if (e.isCancelled()) return;

        ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);
        block.setType(Material.AIR);

        if (eBP.getExecutableBlock().isDropBlockIfItIsBroken()) {
            ItemStack is = eBP.getExecutableBlock().buildItem(1, Optional.ofNullable(target));
            if (!target.getInventory().addItem(is).isEmpty()) {
                bLoc.getWorld().dropItem(bLoc, is);
            }
        }
    }
}
