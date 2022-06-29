package com.ssomar.executableblocks.executableblocks.activators.editor;

import com.ssomar.executableblocks.executableblocks.activators.NewActivatorEBFeature;
import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.score.menu.GUI;
import com.ssomar.score.utils.TypeTarget;
import com.ssomar.scoretestrecode.features.editor.FeatureEditorInterface;

public class NewActivatorEBFeatureEditor extends FeatureEditorInterface<NewActivatorEBFeature> {

    public NewActivatorEBFeature activator;

    public NewActivatorEBFeatureEditor(NewActivatorEBFeature activator) {
        super("&lActivator EI feature Editor", 6 * 9);
        this.activator = activator.clone();
        load();
    }

    @Override
    public void load() {
        clearAndSetBackground();

        // TODO ADD OWNER THING

        activator.getDisplayName().initAndUpdateItemParentEditor(this, 0);
        activator.getOptionFeature().initAndUpdateItemParentEditor(this, 1);

        activator.getUsageModification().initAndUpdateItemParentEditor(this, 3);

        if (activator.getOption().equals(Option.LOOP))
            activator.getLoopFeatures().initAndUpdateItemParentEditor(this, 5);

        if(Option.getOptWithDamageCause().contains(activator.getOption())){
            activator.getDetailedDamageCauses().initAndUpdateItemParentEditor(this, 6);
        }
        else if(Option.getOptWithCommand().contains(activator.getOption())){
            activator.getDetailedCommands().initAndUpdateItemParentEditor(this, 6);
        }
        activator.getBlockConditions().initAndUpdateItemParentEditor(this, 7);
        activator.getCommands().initAndUpdateItemParentEditor(this, 8);

        if (Option.getOptWithEntity().contains(activator.getOption())) {
            activator.getEntityCommands().initAndUpdateItemParentEditor(this, 17);
            activator.getEntityConditions().initAndUpdateItemParentEditor(this, 16);
            activator.getDetailedEntities().initAndUpdateItemParentEditor(this, 15);
        } else if (Option.getOptWithPlayer().contains(activator.getOption())) {
            activator.getPlayerCommands().initAndUpdateItemParentEditor(this, 17);
            activator.getPlayerConditions().initAndUpdateItemParentEditor(this, 16);
        }
        else if (Option.getOptWithTargetBlock().contains(activator.getOption())) {
            activator.getBlockCommands().initAndUpdateItemParentEditor(this, 17);
            activator.getTargetBlockConditions().initAndUpdateItemParentEditor(this, 16);
            activator.getDetailedBlocks().initAndUpdateItemParentEditor(this, 15);
        }

        activator.getRequiredGroup().initAndUpdateItemParentEditor(this, 21);

        activator.getWorldConditions().initAndUpdateItemParentEditor(this, 25);

        activator.getCancelEvent().initAndUpdateItemParentEditor(this, 27);

        if(!Option.getOptionWithoutOwner().contains(activator.getOption())){
            activator.getOwnerCommands().initAndUpdateItemParentEditor(this, 35);
            activator.getOwnerConditions().initAndUpdateItemParentEditor(this, 34);
        }

        activator.getSilenceOutput().initAndUpdateItemParentEditor(this, 44);

        activator.getCustomEIConditions().initAndUpdateItemParentEditor(this, 43);

        activator.getPlaceholderConditions().initAndUpdateItemParentEditor(this, 52);

        // Back
        createItem(RED, 1, 45, GUI.BACK, false, false);

        // Reset menu
        createItem(ORANGE, 1, 46, GUI.RESET, false, false, "", "&c&oClick here to reset");

        // Save menu
        createItem(GREEN, 1, 53, GUI.SAVE, false, false, "", "&a&oClick here to save");
    }

    @Override
    public NewActivatorEBFeature getParent() {
        return activator;
    }
}
