package com.ssomar.executableblocks.executableblocks.editor;

import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.utils.CreationType;
import com.ssomar.score.menu.GUI;
import com.ssomar.scoretestrecode.features.editor.FeatureEditorInterface;

import java.util.Optional;

public class NewExecutableBlockEditor extends FeatureEditorInterface<NewExecutableBlock> {

    private NewExecutableBlock newExecutableBlock;

    public NewExecutableBlockEditor(NewExecutableBlock newExecutableBlock) {
        super("&lExecutable Block Editor", 6 * 9);
        this.newExecutableBlock = newExecutableBlock;
        load();
    }

    @Override
    public void load() {
        clearAndSetBackground();
        CreationType creationType = newExecutableBlock.getCreationType().getValue().get();
        newExecutableBlock.getCreationType().initAndUpdateItemParentEditor(this, 0);
        if (creationType.equals(CreationType.BASIC_CREATION)) {
            newExecutableBlock.getMaterial().initAndUpdateItemParentEditor(this, 1);
            newExecutableBlock.getDisplayName().initAndUpdateItemParentEditor(this, 2);
            newExecutableBlock.getLore().initAndUpdateItemParentEditor(this, 3);

            newExecutableBlock.getSpawnerType().initAndUpdateItemParentEditor(this, 10);
        }
        newExecutableBlock.getUsage().initAndUpdateItemParentEditor(this, 4);
        newExecutableBlock.getTitle().initAndUpdateItemParentEditor(this, 5);
        newExecutableBlock.getCancelGravity().initAndUpdateItemParentEditor(this, 6);

        newExecutableBlock.getOnlyBreakableWithEI().initAndUpdateItemParentEditor(this, 29);
        newExecutableBlock.getDropBlockIfItIsBroken().initAndUpdateItemParentEditor(this, 30);
        newExecutableBlock.getDropBlockWhenItExplodes().initAndUpdateItemParentEditor(this, 31);
        newExecutableBlock.getDropBlockWhenItBurns().initAndUpdateItemParentEditor(this, 32);

        newExecutableBlock.getCanBeMoved().initAndUpdateItemParentEditor(this, 34);


        //Reset menu
        createItem(ORANGE, 1, 46, GUI.RESET, false, false, "", "&c&oClick here to reset", "&c&oall options of this block");
        // exit
        createItem(RED, 1, 45, GUI.BACK, false, false);

        getInv().setItem(52, newExecutableBlock.buildItem(1, Optional.empty()));

        //Save menu
        createItem(GREEN, 1, 53, GUI.SAVE, false, false, "", "&a&oClick here to save", "&a&oyour modification in config.yml");

    }

    @Override
    public NewExecutableBlock getParent() {
        return newExecutableBlock;
    }
}
