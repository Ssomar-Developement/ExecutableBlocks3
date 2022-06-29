package com.ssomar.executableblocks.events;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.activators.NewActivatorEBFeature;
import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.score.SCore;
import com.ssomar.score.sobject.sactivator.EventInfo;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.placeholders.StringPlaceholder;
import com.ssomar.scoretestrecode.features.custom.activators.activator.NewSActivator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class EventsManager {

    public SendMessage sm = new SendMessage();

    @SuppressWarnings("unused")
    private NewExecutableBlocksManager iM = NewExecutableBlocksManager.getInstance();

    //private CooldownsManager gCM = CooldownsManager.getInstance();

    private static EventsManager instance;

    public void activeOption(@NotNull Option o, @NotNull ExecutableBlockPlaced eBP, EventInfo eInfo) {
        this.activeOption(o, eBP, eInfo, new ArrayList<>());
    }

    public void activeOption(@NotNull Option o, @NotNull ExecutableBlockPlaced eBP, EventInfo eInfo, List<NewActivatorEBFeature> whiteList) {

        if (eBP.getExecutableBlock() == null) return;

        NewExecutableBlock eB = eBP.getExecutableBlock();

        if (eB == null) {
            ExecutableBlocks.plugin.getLogger().severe(ExecutableBlocks.NAME_2 + " Error can't activate the ExecutableBlock: " + eBP.getEB_ID());
            return;
        }

        Player target = null;

        if (eInfo.getTargetPlayer().isPresent()) {
            target = eInfo.getTargetPlayer().get();
            if (!eB.hasBlockPerm(target, true)) return;
        }

        StringPlaceholder sp = new StringPlaceholder();

        for (NewSActivator activator : eB.getActivators().getActivators().values()) {
            Option activatorOption = (Option) activator.getOption();

            if (activatorOption.equals(o)) {

                if ((activatorOption.equals(Option.LOOP) || activatorOption.equals(Option.ENTITY_WALK_ON)) && !whiteList.contains(activator)) {
                    continue;
                }

                sp.setActivator(activator.getName());

                if (target != null) {

                    if (eBP.getOwnerUUID().isPresent()) {
                        sp.setOwnerPlcHldr(eBP.getOwnerUUID().get());
                    }
                    sp.setPlayerPlcHldr(target.getUniqueId());

					/* TODO Optional<Cooldown> cooldown;
					if (activator.getGlobalPlayerCooldown() > 0 && gCM.isInCooldown(ExecutableBlocks.plugin, eB, activator)) {
						if (activator.isDisplayGlobalPlayerCooldownMsg()) {
							String message = activator.getGlobalPlayerCooldownMsg();
							int cd = activator.getGlobalPlayerCooldown() - gCM.getMaxGlobalCooldown(ExecutableBlocks.plugin, eB, activator);
							sp.getTimePlch().setTimePlcHldr(cd);
							sp.setCooldown(cd + "");
							message = sp.replacePlaceholder(message);

							target.sendMessage(StringConverter.coloredString(message));
						}
						continue;
					} else if (activator.getPerPlayerCooldown() > 0 && (cooldown = gCM.getCooldown(ExecutableBlocks.plugin, eB, activator, target.getUniqueId())).isPresent()) {
						if (activator.isDisplayPerPlayerCooldownMsg()) {
							String message = activator.getPerPlayerCooldownMsg();
							double cd = cooldown.get().getTimeLeft();
							sp.getTimePlch().setTimePlcHldr(cd);
							sp.setCooldown(cd + "");
							message = sp.replacePlaceholder(message);

							target.sendMessage(StringConverter.coloredString(message));
						}
						continue;
					}*/
                }
                /* The loop is ASync and activator need to be execute sync */
                if (activator.getOption().equals(Option.LOOP)) {
                    Bukkit.getScheduler().runTask(SCore.plugin, new Runnable() {
                        @Override
                        public void run() {
                            activator.run(eBP, eInfo);
                        }
                    });
                } else activator.run(eBP, eInfo);

            }
        }
    }

    public static EventsManager getInstance() {
        if (instance == null) instance = new EventsManager();
        return instance;
    }

}
