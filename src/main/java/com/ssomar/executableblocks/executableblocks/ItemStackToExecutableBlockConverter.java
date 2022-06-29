package com.ssomar.executableblocks.executableblocks;

import javax.annotation.Nullable;

import com.ssomar.executableblocks.utils.CreationType;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class ItemStackToExecutableBlockConverter {


	public static NewExecutableBlock convert(@Nullable ItemStack item, String id, String path) {

		NewExecutableBlock result = new NewExecutableBlock(id, path);

		if(item == null) return result;

		result.getCreationType().setValue(Optional.of(CreationType.BASIC_CREATION));
		/* get Material */
		Material material = item.getType();
		if(!material.equals(Material.AIR)) {
			result.getMaterial().setValue(Optional.of(material));

			if(item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();

				result.getDisplayName().setValue(Optional.of(meta.getDisplayName()));
				if(meta.hasLore()) result.getLore().setValue(meta.getLore());

				if(material.equals(Material.SPAWNER)) {
					BlockStateMeta bMeta = (BlockStateMeta)item.getItemMeta();
					CreatureSpawner csm = (CreatureSpawner)bMeta.getBlockState();
					result.getSpawnerType().setValue(Optional.of(csm.getSpawnedType()));
				}

			}
		}
		return result;
	}
}
