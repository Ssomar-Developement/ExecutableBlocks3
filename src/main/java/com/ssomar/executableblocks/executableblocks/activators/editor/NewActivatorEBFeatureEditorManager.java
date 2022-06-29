package com.ssomar.executableblocks.executableblocks.activators.editor;


import com.ssomar.executableblocks.executableblocks.activators.NewActivatorEBFeature;
import com.ssomar.scoretestrecode.editor.NewInteractionClickedGUIManager;
import com.ssomar.scoretestrecode.features.FeatureInterface;
import com.ssomar.scoretestrecode.features.editor.FeatureEditorManagerAbstract;
import com.ssomar.scoretestrecode.features.types.SOptionFeature;
import com.ssomar.scoretestrecode.features.types.TypeTargetFeature;

public class NewActivatorEBFeatureEditorManager extends FeatureEditorManagerAbstract<NewActivatorEBFeatureEditor, NewActivatorEBFeature> {

    private static NewActivatorEBFeatureEditorManager instance;

    public static NewActivatorEBFeatureEditorManager getInstance() {
        if (instance == null) {
            instance = new NewActivatorEBFeatureEditorManager();
        }
        return instance;
    }

    @Override
    public NewActivatorEBFeatureEditor buildEditor(NewActivatorEBFeature parent) {
        return new NewActivatorEBFeatureEditor(parent.clone());
    }

    public void reloadEditor(NewInteractionClickedGUIManager<NewActivatorEBFeatureEditor> i) {
        for (FeatureInterface feature : i.gui.getParent().getFeatures()) {
            if (feature instanceof SOptionFeature || feature instanceof TypeTargetFeature) {
                i.gui.load();
            }
        }
    }


    @Override
    public boolean shiftLeftClicked(NewInteractionClickedGUIManager<NewActivatorEBFeatureEditor> i) {
        boolean result = super.shiftLeftClicked(i);
        if (result) {
            reloadEditor(i);
        }
        return result;
    }

    @Override
    public boolean shiftRightClicked(NewInteractionClickedGUIManager<NewActivatorEBFeatureEditor> i) {
        boolean result = super.shiftRightClicked(i);
        if (result) {
            reloadEditor(i);
        }
        return result;
    }

    @Override
    public boolean leftClicked(NewInteractionClickedGUIManager<NewActivatorEBFeatureEditor> i) {
        boolean result = super.leftClicked(i);
        if (result) {
            reloadEditor(i);
        }
        return result;
    }

    @Override
    public boolean rightClicked(NewInteractionClickedGUIManager<NewActivatorEBFeatureEditor> i) {
        boolean result = super.rightClicked(i);
        if (result) {
            reloadEditor(i);
        }
        return result;
    }
}
