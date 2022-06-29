package com.ssomar.executableblocks.executableblocks.features.customization;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.configs.api.PlaceholderAPI;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.BlockLocation;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.shaded.customblockdata.CustomBlockData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;

import static io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic.*;

@Getter
@Setter
public class OraxenFeatures {

    private boolean hasOraxenTexture;
    private String oraxenID;

    public OraxenFeatures() {
        hasOraxenTexture = false;
    }

    public OraxenFeatures(ConfigurationSection section, List<String> error, String id) {
        String oraxenID = section.getString("oraxenID", "");
        boolean validTexture;
        try {
            validTexture = !oraxenID.trim().isEmpty() && OraxenItems.exists(oraxenID);
        } catch (Exception e) {
            validTexture = false;
        }
        hasOraxenTexture = validTexture;
        if (validTexture) {
            if (!PlaceholderAPI.isLotOfWork()) {
                this.oraxenID = oraxenID;
            } else {
                hasOraxenTexture = false;
                error.add("[" + ExecutableBlocks.NAME + "] REQUIRE PREMIUM to add oraxen textures to your ExecutableBlock: " + id);
            }
        } else if (!oraxenID.isEmpty()) {
            error.add("[" + ExecutableBlocks.NAME + "] ERROR Oraxen textures not found, there is no OraxenBlock with the ID: " + oraxenID + " for your ExecutableBlock: " + id);
        }
    }

    public void saveOraxentextures(ConfigurationSection section) {
        if (!hasOraxenTexture) section.set("oraxenID", null);
        else section.set("oraxenID", oraxenID);
    }

    public Optional<ItemStack> buildOraxenItemStack() {
        ItemStack itemStack = null;
        if (hasOraxenTexture) {
            itemStack = OraxenItems.getItemById(oraxenID).build();
        }
        return Optional.ofNullable(itemStack);
    }

    public void removePlaceDOraxenBlock(Block block) {

        final PersistentDataContainer customBlockData = new CustomBlockData(block, OraxenPlugin.get());
        if (!customBlockData.has(FURNITURE_KEY, PersistentDataType.STRING))
            return;

        final String oraxenID = customBlockData.get(FURNITURE_KEY, PersistentDataType.STRING);
        Mechanic mechanic = FurnitureFactory.getInstance().getMechanic(oraxenID);
        if (mechanic instanceof FurnitureMechanic) {
            final BlockLocation rootBlockLocation = new BlockLocation(customBlockData.get(ROOT_KEY, PersistentDataType.STRING));
            ((FurnitureMechanic) mechanic).removeSolid(block.getWorld(), rootBlockLocation, customBlockData.get(ORIENTATION_KEY, PersistentDataType.FLOAT));
        }
    }

}
