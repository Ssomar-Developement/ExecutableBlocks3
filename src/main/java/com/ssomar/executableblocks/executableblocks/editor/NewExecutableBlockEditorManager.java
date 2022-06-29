package com.ssomar.executableblocks.executableblocks.editor;

import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.scoretestrecode.editor.NewInteractionClickedGUIManager;
import com.ssomar.scoretestrecode.features.editor.FeatureEditorManagerAbstract;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class NewExecutableBlockEditorManager extends FeatureEditorManagerAbstract<NewExecutableBlockEditor, NewExecutableBlock> {

    private static NewExecutableBlockEditorManager instance;

    @Override
    public NewExecutableBlockEditor buildEditor(NewExecutableBlock featureParentInterface) {
        return new NewExecutableBlockEditor(featureParentInterface.clone());
    }

    public static NewExecutableBlockEditorManager getInstance() {
        if (instance == null) {
            instance = new NewExecutableBlockEditorManager();
        }
        return instance;
    }

    public void reloadEditor(NewInteractionClickedGUIManager<NewExecutableBlockEditor> i) {
        i.gui.load();
        i.player.updateInventory();
    }


    @Override
    public void receiveMessage(NewInteractionClickedGUIManager<NewExecutableBlockEditor> i) {
        super.receiveMessage(i);
        reloadEditor(i);
    }

    @Override
    public void clicked(ItemStack item, NewInteractionClickedGUIManager<NewExecutableBlockEditor> interact, ClickType click) {
        super.clicked(item, interact, click);
        reloadEditor(interact);
    }

}
