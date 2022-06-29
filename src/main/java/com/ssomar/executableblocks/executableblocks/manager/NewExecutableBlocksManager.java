package com.ssomar.executableblocks.executableblocks.manager;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.events.optimize.OptimizedEventsHandler;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.activators.NewActivatorEBFeature;
import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableitems.events.optimize.OptimizedSlotsVerification;
import com.ssomar.score.SCore;
import com.ssomar.score.api.executableblocks.config.ExecutableBlockInterface;
import com.ssomar.score.api.executableblocks.config.ExecutableBlocksManagerInterface;
import com.ssomar.score.events.loop.LoopManager;
import com.ssomar.score.sobject.sactivator.SOption;
import com.ssomar.scoretestrecode.features.custom.activators.activator.NewSActivator;
import com.ssomar.scoretestrecode.sobject.NewSObjectManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NewExecutableBlocksManager extends NewSObjectManager<NewExecutableBlock> implements ExecutableBlocksManagerInterface {

    private static NewExecutableBlocksManager instance;

    private NamespacedKey key;

    public NewExecutableBlocksManager() {
        super(ExecutableBlocks.plugin);

        if (!SCore.is1v13Less()) key = new NamespacedKey(ExecutableBlocks.getPluginSt(), "EB-ID");
    }

    @Override
    public void actionOnObjectWhenLoading(NewExecutableBlock newExecutableBlock) {

        /* fill loopActivators in LoopManager */
        for (NewSActivator activator : newExecutableBlock.getActivators().getActivators().values()) {
            if (activator.getOption().equals(Option.LOOP) && !LoopManager.getInstance().getLoopActivators().containsKey(activator)) {
                LoopManager.getInstance().getLoopActivators().put(activator, 0);
            }

            /* Register only the activators from EI*/
            SOption option = activator.getOption();
            if (option instanceof Option && activator instanceof NewActivatorEBFeature) {
                NewActivatorEBFeature activatorEB = (NewActivatorEBFeature) activator;
                Option optionEI = (Option) option;
                OptimizedEventsHandler.getInstance().read(optionEI);
            }
        }
    }

    @Override
    public void actionOnObjectWhenReloading(NewExecutableBlock newExecutableBlock) {
        for (NewSActivator act : newExecutableBlock.getActivators().getActivators().values()) {
            LoopManager.getInstance().getLoopActivators().remove(act);
        }
    }

    @Override
    public Optional<NewExecutableBlock> methodObjectLoading(String s) {
        return Optional.empty();
    }

    public static NewExecutableBlocksManager getInstance() {
        if (instance == null) {
            instance = new NewExecutableBlocksManager();
        }
        return instance;
    }

    @Override
    public boolean isValidID(String id) {
        for (NewExecutableBlock item : this.getLoadedObjects()) {
            if (item.getId().equals(id)) return true;
        }
        return false;
    }

    @Override
    public Optional<ExecutableBlockInterface> getExecutableBlock(String s) {
        for (NewExecutableBlock item : this.getLoadedObjects()) {
            if (item.getId().equals(s)) return Optional.of(item);
        }
        return Optional.empty();
    }

    public Optional<ExecutableBlockInterface> getExecutableBlock(ItemStack itemStack) {
        // TODO return Optional.ofNullable(getExecutableItem(itemStack, new ArrayList<>()));
        return Optional.empty();
    }

    /* @Override
    public List<String> getExecutableBlockIdsList() {
        List<String> list = new ArrayList<>();
        for (NewExecutableBlock item : this.getLoadedObjects()) {
            list.add(item.getId());
        }
        return list;
    }*/

    /**
     * @param whiteListedID If empty, all EB are check in the recognition that takes performance (lore , name)
     **/
    /* // TODO @Nullable
    public NewExecutableBlock getExecutableBlock(ItemStack item) {

        ItemMeta itemMeta = null;
        boolean hasItemMeta;
        try {
            hasItemMeta = (itemMeta = item.getItemMeta()) != null;
        } catch (NullPointerException error) {
            return null;
        }

        String id = "";
        if (SCore.is1v13Less()) {
            if (item == null || item.getType() == Material.AIR) return null;
            boolean idFound = false;
            if (ExecutableBlocks.hasNBTAPI) {
                NBTItem nbti = new NBTItem(item);
                if (nbti.hasKey("EI-ID")) {
                    id = nbti.getString("EI-ID");
                    idFound = true;
                }
            }
        } else if (hasItemMeta && (id = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING)) == null) {
            id = "";
        }

        if (!id.isEmpty()) {
            for (NewExecutableItem infoItem : this.getAllObjects()) {
                if (infoItem.getId().equals(id)) return infoItem;
            }
        }

        return null;
    }*/
}
