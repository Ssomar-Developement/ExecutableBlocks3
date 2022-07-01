package com.ssomar.executableblocks.events.mechanics;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.configs.GeneralConfig;
import com.ssomar.executableblocks.configs.messages.Message;
import com.ssomar.executableblocks.events.activators.PlayerBlockPlaceEvent;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.score.SsomarDev;
import com.ssomar.score.api.executableblocks.config.ExecutableBlockInterface;
import com.ssomar.score.configs.messages.MessageMain;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.StringConverter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class EBPPlacementListener implements Listener {

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
        SsomarDev.testMsg("PRESENT: "+ebOpt.isPresent());
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
}
