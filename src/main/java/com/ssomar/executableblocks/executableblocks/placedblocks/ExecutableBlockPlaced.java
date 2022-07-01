package com.ssomar.executableblocks.executableblocks.placedblocks;

import java.util.Optional;
import java.util.UUID;

import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.sobject.HigherFormSObject;
import com.ssomar.scoretestrecode.features.custom.activators.activator.NewSActivator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.activators.NewActivatorEBFeature;
import com.ssomar.executableblocks.executableblocks.activators.Option;
import org.jetbrains.annotations.Nullable;

@Getter @Setter
public class ExecutableBlockPlaced implements ExecutableBlockPlacedInterface, HigherFormSObject {

	private UUID id;

	private Location location;

	private Location holoLocation;

	private Optional<UUID> ownerUUID;

	private String EB_ID;

	private int usage;

	private boolean isRemoved;

	public ExecutableBlockPlaced(UUID id, Location location, @Nullable UUID ownerUUID, String EB_ID, int usage) {
		super();
		this.id = id;
		this.location = location;
		this.ownerUUID = Optional.ofNullable(ownerUUID);
		this.EB_ID = EB_ID;
		this.usage = usage;
		this.isRemoved = false;
	}

	public ExecutableBlockPlaced(Location location, @Nullable UUID ownerUUID, String EB_ID) {
		super();
		this.id = UUID.randomUUID();
		this.location = location;
		this.ownerUUID = Optional.ofNullable(ownerUUID);
		this.EB_ID = EB_ID;	
		this.usage = -1;
		this.isRemoved = false;
	}

	public void changeUsage(int usageModification) {
		if(usage == -1) return;

		/* if <= 0 so we need to delete the EB placed */
		if(usage + usageModification <= 0) {
			runBreakBlockAnimation();
			remove();
			Block b = this.location.getBlock();


			/* A DELAY IS REQUIRED FOR PLATE 
			 * 
			 * https://www.spigotmc.org/threads/solved-playerinteractevent-settype-not-doing-anything.77669/
			 * Here's my theory. This is the sequence of events if you don't have a delay:
				- Player steps on the plate
				- Interact event is fired
				- Your code changes the plate to air
				- Event completes
				- After the event, the block is turned into a "pushed pressure plate" since the event was not cancelled.

				With the delay, the block is changed after all of that.*/
			new BukkitRunnable() {
				@Override
				public void run() {
					b.setType(Material.AIR);
				}
			}.runTaskLater(ExecutableBlocks.plugin, 2L);
		}

		/* we just update the usage of the object */
		else {
			usage = usage + usageModification;
		}
	}

	public void drop(Location location) {
		ItemStack is = getExecutableBlock().buildItem(1, Optional.ofNullable(getOwner()));
		location.getWorld().dropItem(location, is);
	}

	public Optional<UUID> getOwnerUUID() {
		return ownerUUID;
	}

	public String getOwnerName() {
		if(!getOwnerUUID().isPresent()) return "";
		OfflinePlayer p = Bukkit.getOfflinePlayer(this.getOwnerUUID().get());
		return p.getName();
	}

	@Override
	public String getExecutableBlockID() {
		return EB_ID;
	}

	@Override
	public Location getTitleLocation() {
		return holoLocation;
	}

	@Nullable
	public Player getOwner() {
		if(!getOwnerUUID().isPresent()) return null;
		return Bukkit.getPlayer(this.ownerUUID.get());
	}

	@Override
	public void breakBlock(@Nullable Player player, boolean drop) {

		/* Check if the player has the perm to break this EB placed */
		if(player != null) {
			if(!getExecutableBlock().hasBlockPerm(player, true)) return;
		}

		/* Check if the block must be dropped if it is broken */
		else if (drop && getExecutableBlock().getDropBlockIfItIsBroken().getValue()) {
			ItemStack is = getExecutableBlock().buildItem(1, Optional.ofNullable(player));
			if (player == null || (player != null && !player.getInventory().addItem(is).isEmpty())) {
				drop(location);
			}
		}

		runBreakBlockAnimation();
		remove();
	}

	public void moveBlock(Location location) {
		remove();
		getExecutableBlock().place2(ownerUUID, location , true);
	}

	public void runBreakBlockAnimation(){
		// TODO add sound
		BlockData typeData = this.location.getBlock().getType().createBlockData();
		this.location.getWorld().spawnParticle(Particle.BLOCK_CRACK, this.location, 25, 0.5, 0.5, 0.5, 1, typeData);
	}

	@Override
	public void remove() {
		isRemoved = true;
		this.location.getBlock().setType(Material.AIR);
		ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced(this);
	}

	@Nullable
	public NewExecutableBlock getExecutableBlock() {
		Optional<NewExecutableBlock> oOpt = NewExecutableBlocksManager.getInstance().getLoadedObjectWithID(EB_ID);
		if(oOpt.isPresent()) return  oOpt.get();
		return null;
	}

	public boolean hasLoop() {
		NewExecutableBlock eB = this.getExecutableBlock();
		if(eB == null) return false;

		for(NewSActivator act : eB.getActivators().getActivators().values()) {
			if(act.getOption().equals(Option.LOOP)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasEntityOn() {
		NewExecutableBlock eB = this.getExecutableBlock();
		if(eB == null) return false;

		for(NewSActivator act : eB.getActivators().getActivators().values()) {
			if(act.getOption().equals(Option.ENTITY_WALK_ON)) {
				return true;
			}
		}

		return false;
	}
}
