package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.events.EventsManager;
import com.ssomar.score.SCore;
import com.ssomar.score.SsomarDev;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.sobject.sactivator.EventInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemsAdderEvents implements Listener {

    private static final boolean DEBUG = false;

    /**
     * REPAIR DESCTRUCTION OF THE EB ON FURINITURES
     **/
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        SsomarDev.testMsg("EntityDamageByEntityEvent", DEBUG);
        Entity entity = e.getEntity();

        if (entity instanceof ArmorStand && e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Block block = e.getEntity().getLocation().getBlock();
            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) {
                SsomarDev.testMsg("EntityDamageByEntityEvent : NOT EBP", DEBUG);
                return;
            }
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

            NewExecutableBlock eB = eBP.getExecutableBlock();

            if (!eB.hasBlockPerm(damager, true)) {
                e.setCancelled(true);
                SsomarDev.testMsg("EntityDamageByEntityEvent NOT PERM", DEBUG);
                return;
            }

            if (SCore.hasExecutableItems && eB.getOnlyBreakableWithEI().getValue().size() > 0) {

                ItemStack item1 = damager.getInventory().getItemInMainHand();
                if (item1.getType().equals(Material.AIR)) {
                    item1 = damager.getInventory().getItemInOffHand();
                }
                if (item1.getType().equals(Material.AIR)) {
                    e.setCancelled(true);
                    return;
                }

                if(!eB.getOnlyBreakableWithEI().isValid(item1)) {
                    e.setCancelled(true);
                    return;
                }
            }

            if (eB.getItemsAdderBlockFeatures().isHasItemsAdderBlock()) {

                EventInfo eInfo = new EventInfo(e);
                eInfo.setTargetPlayer(Optional.of(damager));
                EventsManager.getInstance().activeOption(Option.PLAYER_RIGHT_CLICK_ON, eBP, eInfo);

                /* No remove if the event is cancelled */
                if (e.isCancelled()) return;

                EventInfo eInfo2 = new EventInfo(e);
                eInfo2.setTargetPlayer(Optional.of(damager));
                EventsManager.getInstance().activeOption(Option.PLAYER_ALL_CLICK_ON, eBP, eInfo2);

                /* No remove if the event is cancelled */
                if (e.isCancelled()) return;

                eBP.remove();
            } else SsomarDev.testMsg("EntityDamageByEntityEvent : NOT EB", DEBUG);
        } else SsomarDev.testMsg("EntityDamageByEntityEvent : NOT ItemFrame >> " + entity.getType().toString(), DEBUG);
    }

    /**
     * REPAIR CLICK ACTIVATOR ON FURNITURES
     **/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntityEvent(PlayerInteractAtEntityEvent e) {
        SsomarDev.testMsg("PlayerAtInteractEntityEvent", DEBUG);
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            SsomarDev.testMsg("PlayerInteractEntityEvent : OFF HAND", DEBUG);
            return;
        }

        Entity entity = e.getRightClicked();

        if (entity instanceof ArmorStand) {
            Block block = entity.getLocation().getBlock();
            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) {
                SsomarDev.testMsg("EntityDamageByEntityEvent : NOT EBP");
                return;
            }
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();
            NewExecutableBlock eB = eBP.getExecutableBlock();

            SsomarDev.testMsg("PlayerInteractAtEntityEvent : EBP", DEBUG);

            if (eB.getItemsAdderBlockFeatures().isHasItemsAdderBlock()) {

                EventInfo eInfo = new EventInfo(e);
                eInfo.setTargetPlayer(Optional.of(e.getPlayer()));
                EventsManager.getInstance().activeOption(Option.PLAYER_LEFT_CLICK_ON, eBP, eInfo);

                SsomarDev.testMsg("PlayerInteractAtEntityEvent : EBP : LEFT", DEBUG);

                /* No remove if the event is cancelled */
                if (e.isCancelled()) {
                    SsomarDev.testMsg("PlayerInteractAtEntityEvent : EBP : LEFT : CANCELED", DEBUG);
                    return;
                }

                EventInfo eInfo2 = new EventInfo(e);
                eInfo2.setTargetPlayer(Optional.of(e.getPlayer()));
                EventsManager.getInstance().activeOption(Option.PLAYER_ALL_CLICK_ON, eBP, eInfo2);

                /* No remove if the event is cancelled */
                if (e.isCancelled()) return;
            } else SsomarDev.testMsg("PlayerInteractAtEntityEvent : NOT EB", DEBUG);
        } else SsomarDev.testMsg("PlayerInteractEntityEvent : NOT ArmorStand >> " + entity.getType().toString(), DEBUG);
    }
}
