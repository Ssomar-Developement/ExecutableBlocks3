package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.configs.GeneralConfig;
import com.ssomar.executableblocks.configs.messages.Message;
import com.ssomar.executableblocks.events.activators.PlayerBlockPlaceEvent;
import com.ssomar.executableblocks.events.activators.PlayerBreakBlockEvent;
import com.ssomar.score.SCore;
import com.ssomar.score.SsomarDev;
import com.ssomar.score.api.executableblocks.config.ExecutableBlockInterface;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.configs.messages.MessageMain;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.StringConverter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;

import static io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic.FURNITURE_KEY;

public class MechanicBlockModificationEvent implements Listener {

    private static final Boolean DEBUG = false;

    /* Take the priority of Oraxen */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockDamageEventOraxen(EntityDamageByEntityEvent e) {

        Entity entity;
        if(!((entity = e.getDamager()) instanceof Player) || !(e.getEntity() instanceof ItemFrame)) return;
        Player target = (Player) entity;
        ItemFrame frame = (ItemFrame) e.getEntity();

        Block block = e.getEntity().getLocation().getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        if (!eBP.getExecutableBlock().hasBlockPerm(target, true)) {
            e.setCancelled(true);
            return;
        }

        if (SCore.hasExecutableItems && eBP.getExecutableBlock().getOnlyBreakableWithEI().size() > 0) {

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

        if(eBP.getExecutableBlock().getOraxenTextures().isHasOraxenTexture()) {
            PlayerBreakBlockEvent.activatorBreak(e);

            /* No remove if the event is cancelled */
            if (e.isCancelled()) return;

            frame.getPersistentDataContainer().remove(FURNITURE_KEY);

            ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);

            e.setCancelled(true);
            frame.remove();
            block.setType(Material.AIR);

            if (eBP.getExecutableBlock().isDropBlockIfItIsBroken()) {
                ItemStack is = eBP.getExecutableBlock().buildItem(1, Optional.ofNullable(target));
                if (!target.getInventory().addItem(is).isEmpty()) {
                    bLoc.getWorld().dropItem(bLoc, is);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplodeEvent(BlockExplodeEvent e) {

        Block block = e.getBlock();
        Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);

        e.setYield(0);

        if (eBP.getExecutableBlock().isDropBlockWhenItExplodes()) {
            ItemStack is = eBP.getExecutableBlock().buildItem(1, Optional.empty());
            bLoc.getWorld().dropItem(bLoc, is);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
        for (Block block : e.blockList()) {
            Location bLoc = LocationConverter.convert(block.getLocation(), false, false);

            Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
            if (!eBPOpt.isPresent()) return;
            ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

            ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);

            e.setYield(0);

            if (eBP.getExecutableBlock().isDropBlockWhenItExplodes()) {
                ItemStack is = eBP.getExecutableBlock().buildItem(1, Optional.empty());
                bLoc.getWorld().dropItem(bLoc, is);
            }
        }
    }

    /* To cancel the usage of bone meal on seagrass when seagrass cant grow up*/
    @EventHandler
    public void onPlayerBoneMealOnSeagrassEvent(PlayerInteractEvent e) {
        if (!SCore.is1v12()) {

            PlayerInventory inv = e.getPlayer().getInventory();

            ItemStack mainHand = inv.getItemInMainHand();
            if (!mainHand.getType().equals(Material.BONE_MEAL)) {
                ItemStack offHand = inv.getItemInOffHand();
                if (!offHand.getType().equals(Material.BONE_MEAL)) {
                    return;
                }
            }

            if (Action.RIGHT_CLICK_BLOCK.equals(e.getAction()) && e.getClickedBlock() != null) {
                Block b = e.getClickedBlock();
                if (b.getType().equals(Material.SEAGRASS)) {
                    Location locB = b.getLocation();
                    locB.add(0, 1, 0);
                    Block verifyB = locB.getBlock();
                    if (verifyB.getType() != Material.WATER) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


    /* HIGHTEST to pass after worldguard flag */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent e) {

        SsomarDev.testMsg("BlockPlaceEvent");

        Player p = e.getPlayer();
        Block b = e.getBlockPlaced();
        //SsomarDev.testMsg("block place: "+b.getType());
        //SsomarDev.testMsg("hand place: "+e.getHand());

        //TODO ItemsAdder doesnt return the offhand item when it simulate the placing of a furniture.
        ItemStack is = e.getPlayer().getInventory().getItem(e.getHand());
        Optional<ExecutableBlockInterface> ebOpt = NewExecutableBlocksManager.getInstance().getExecutableBlock(is);
        if (ebOpt.isPresent()) {
            NewExecutableBlock eB = (NewExecutableBlock) ebOpt.get();
            if(!GeneralConfig.getInstance().getWhitelistedWorlds().isEmpty() && !GeneralConfig.getInstance().getWhitelistedWorlds().contains(b.getWorld().getName())){
                SendMessage.sendMessageNoPlch(p, StringConverter.replaceVariable(MessageMain.getInstance().getMessage(ExecutableBlocks.plugin, Message.NOT_WHITELISTED_WORLD), p.getName(), "", "", 0));
                e.setCancelled(true);
                return;
            }

            ExecutableBlockPlaced eBP = eB.place(Optional.ofNullable(p), b.getLocation(), false);
            PlayerBlockPlaceEvent.onBlockPlaceEvent(e, eBP);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetractEvent(BlockFromToEvent e) {
        Location bLoc = LocationConverter.convert(e.getToBlock().getLocation(), false, false);


        Optional<ExecutableBlockPlacedInterface> eBPOpt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (!eBPOpt.isPresent()) return;
        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) eBPOpt.get();

        ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(eBP);
        e.getToBlock().setType(Material.AIR);
        e.setCancelled(true);

        ItemStack is = eBP.getExecutableBlock().buildItem(1, Optional.empty());
        bLoc.getWorld().dropItem(bLoc, is);

    }

}
