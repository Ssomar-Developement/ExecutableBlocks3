package com.ssomar.executableblocks.editor;

import com.ssomar.executableblocks.executableblocks.activators.editor.NewActivatorEBFeatureEditor;
import com.ssomar.executableblocks.executableblocks.activators.editor.NewActivatorEBFeatureEditorManager;
import com.ssomar.executableblocks.executableblocks.editor.NewExecutableBlockEditor;
import com.ssomar.executableblocks.executableblocks.editor.NewExecutableBlockEditorManager;
import com.ssomar.score.menu.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class NewEditorInteractionsListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;

        String title = e.getView().getTitle();
        Player player = (Player) e.getWhoClicked();
        try {
            if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        } catch (NullPointerException error) {
            return;
        }

        int slot = e.getSlot();
        ItemStack itemS = e.getView().getItem(slot);
        //boolean notNullItem = itemS!=null;

        try {
            if (e.getInventory().getHolder() instanceof GUI)
                this.manage(player, itemS, title, "", e);
        } catch (NullPointerException error) {
            error.printStackTrace();
        }
    }

    public void manage(Player player, ItemStack itemS, String title, String guiType, InventoryClickEvent e) {

        e.setCancelled(true);

        if (itemS == null) return;

        if (!itemS.hasItemMeta()) return;

        if (itemS.getItemMeta().getDisplayName().isEmpty()) return;

        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof NewExecutableBlockEditor) {
            NewExecutableBlockEditorManager.getInstance().clicked(player, itemS, title, e.getClick());
            return;
        } else if (holder instanceof NewActivatorEBFeatureEditor) {
            NewActivatorEBFeatureEditorManager.getInstance().clicked(player, itemS, title, e.getClick());
            return;
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatEvent(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        if (NewExecutableBlockEditorManager.getInstance().getRequestWriting().containsKey(p)) {
            e.setCancelled(true);
            NewExecutableBlockEditorManager.getInstance().receiveMessage(p, e.getMessage());
        } else if (NewActivatorEBFeatureEditorManager.getInstance().getRequestWriting().containsKey(p)) {
            e.setCancelled(true);
            NewActivatorEBFeatureEditorManager.getInstance().receiveMessage(p, e.getMessage());
        }
    }
}
