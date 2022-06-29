package com.ssomar.executableblocks.executableblocks.features.customization;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.configs.api.PlaceholderAPI;
import com.ssomar.score.SsomarDev;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Optional;

public class ItemsAdderFeatures {

    @Getter
    @Setter
    private boolean hasItemsAdderBlock;
    @Getter
    @Setter
    private String itemsAdderID;

    private static final boolean DEBUG = false;

    public ItemsAdderFeatures() {
        hasItemsAdderBlock = false;
    }

    public ItemsAdderFeatures(ConfigurationSection section, List<String> error, String id) {
        String itemsAdderID = section.getString("itemsAdderID", "");
        if (itemsAdderID.trim().isEmpty()) hasItemsAdderBlock = false;
        else {
            if (!PlaceholderAPI.isLotOfWork()) {
                hasItemsAdderBlock = true;
                this.setItemsAdderID(itemsAdderID);
            } else {
                hasItemsAdderBlock = false;
                error.add("[" + ExecutableBlocks.NAME + "] REQUIRE PREMIUM to add itemsAdder textures to your ExecutableBlock: " + id);
            }
        }
    }

    public void saveItemsAdderBlock(ConfigurationSection section) {
        if (!hasItemsAdderBlock) section.set("itemsAdderID", null);
        else section.set("itemsAdderID", itemsAdderID);
    }

    public Optional<ItemStack> buildItemsAdderItemStack() {
        ItemStack itemStack = null;
        if (hasItemsAdderBlock) {
            itemStack = CustomStack.getInstance(itemsAdderID).getItemStack();
        }
        return Optional.ofNullable(itemStack);
    }

    public void placeItemsAdderItemStack(Location location, ItemStack itemStack) {
        if (hasItemsAdderBlock) {
            try {
                CustomBlock customBlock = CustomBlock.getInstance(itemsAdderID);
                if (customBlock != null) {
                    SsomarDev.testMsg("placeItemsAdder Block: " + itemsAdderID, DEBUG);
                    customBlock.place(location);
                }
            } catch (Exception e) {
                try {
                    SsomarDev.testMsg("placeItemsAdder is Furniture " + itemsAdderID, DEBUG);
                    CustomFurniture customFurniture = CustomFurniture.spawnPreciseNonSolid(itemsAdderID, location);
                    EntityEquipment entityEquip = ((ArmorStand) customFurniture.getArmorstand()).getEquipment();
                    BukkitRunnable runnable3 = new BukkitRunnable() {
                        @Override
                        public void run() {
                            entityEquip.setItem(EquipmentSlot.HEAD, itemStack);
                        }
                    };
                    runnable3.runTaskLater(ExecutableBlocks.plugin, 10);

                } catch (Exception e1) {
                    e.printStackTrace();
                }
            }
        }
    }
}
