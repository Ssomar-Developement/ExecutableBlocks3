package com.ssomar.executableblocks.events;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.editor.NewEditorInteractionsListener;
import com.ssomar.executableblocks.events.mechanics.*;


public class EventsHandler {

    private static EventsHandler instance;

    private ExecutableBlocks main;

    public void setup(ExecutableBlocks main) {
        this.main = main;
        setupEvents();
    }

    public void setupEvents() {

        main.getServer().getPluginManager().registerEvents(new NewEditorInteractionsListener(), main);

        main.getServer().getPluginManager().registerEvents(new VersionEvt(), main);

        main.getServer().getPluginManager().registerEvents(new MechanicBlockModificationEvent(), main);

        main.getServer().getPluginManager().registerEvents(new BreakExecutableBlockListener(), main);

        main.getServer().getPluginManager().registerEvents(new CancelGravityListener(), main);

        main.getServer().getPluginManager().registerEvents(new BlockBelowEBBreakEvent(), main);

        main.getServer().getPluginManager().registerEvents(new EBPBurnsListerner(), main);

        main.getServer().getPluginManager().registerEvents(new EBPExplodesListener(), main);

        main.getServer().getPluginManager().registerEvents(new EBPMovesListener(), main);

        main.getServer().getPluginManager().registerEvents(new EBPPlacementListener(), main);

        main.getServer().getPluginManager().registerEvents(new BlockFadeListener(), main);

        if (ExecutableBlocks.hasItemsAdder)
            main.getServer().getPluginManager().registerEvents(new ItemsAdderEvents(), main);
    }

    public static EventsHandler getInstance() {
        if (instance == null) instance = new EventsHandler();
        return instance;
    }
}
