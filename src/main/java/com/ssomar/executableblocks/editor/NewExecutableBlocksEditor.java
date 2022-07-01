package com.ssomar.executableblocks.editor;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.loader.NewExecutableBlockLoader;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.executableitems.ExecutableItems;
import com.ssomar.scoretestrecode.sobject.menu.NewSObjectsEditorAbstract;

public class NewExecutableBlocksEditor extends NewSObjectsEditorAbstract {


    public NewExecutableBlocksEditor() {
        super(ExecutableBlocks.plugin, "&lExecutableBlocks", "/blocks", NewExecutableBlocksManager.getInstance(), NewExecutableBlockLoader.getInstance());
    }
}
