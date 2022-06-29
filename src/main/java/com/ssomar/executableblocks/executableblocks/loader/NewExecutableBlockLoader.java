package com.ssomar.executableblocks.executableblocks.loader;

import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.executableitems.ExecutableItems;
import com.ssomar.executableitems.configs.ConfigConverter4v2;
import com.ssomar.executableitems.configs.api.PlaceholderAPI;
import com.ssomar.score.events.loop.LoopManager;
import com.ssomar.scoretestrecode.sobject.NewSObjectLoader;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class NewExecutableBlockLoader extends NewSObjectLoader<NewExecutableBlock> {

    private static NewExecutableBlockLoader instance;

    private static final String CUSTOM = "Custom";
    private static final String GENERATORS = "Generators";
    private static final String STRUCTURES = "Structures";
    private static final String TRAPS = "Traps";
    private static final String BUILDS = "Builds";


    public NewExecutableBlockLoader() {
        super(ExecutableItems.plugin, "/com/ssomar/executableblocks/configs/blocks/", NewExecutableBlocksManager.getInstance(), 15);
    }

    @Override
    public void load() {
        LoopManager.getInstance().resetLoopActivatorsEI();
        NewExecutableBlocksManager.getInstance().setDefaultObjects(new ArrayList<>());
        /* // TODO if (!GeneralConfig.getInstance().isDisableTestItems()) {*/
        if (PlaceholderAPI.isLotOfWork()) {
            this.loadDefaultPremiumObjects(this.getPremiumDefaultObjectsName());
        }
        this.loadDefaultEncodedPremiumObjects(this.getPremiumPackObjectsName());
        //}

        // ITEMS CONFIG
        NewExecutableBlocksManager.getInstance().setLoadedObjects(new ArrayList<>());

        this.resetCpt();
        File itemsDirectory;
        if ((itemsDirectory = new File(ExecutableItems.getPluginSt().getDataFolder() + "/blocks")).exists()) {
            this.loadObjectsInFolder(itemsDirectory, !PlaceholderAPI.isLotOfWork());
        } else {
            this.createDefaultObjectsFile(!PlaceholderAPI.isLotOfWork());
            this.load();
        }
    }


    public Map<String, List<String>> getPremiumPackObjectsName() {
        Map<String, List<String>> defaultItems = new HashMap<>();

        return defaultItems;
    }

    @Override
    public Map<String, List<String>> getPremiumDefaultObjectsName() {
        Map<String, List<String>> defaultBlocks = new HashMap<>();

        List<String> custom = new ArrayList<>();
        custom.add("Prem_Target");
        defaultBlocks.put(CUSTOM, custom);

        List<String> gen = new ArrayList<>();
        gen.add("Prem_Generator");
        gen.add("Prem_Generator2");
        defaultBlocks.put(GENERATORS, gen);

        List<String> struc = new ArrayList<>();
        struc.add("Prem_Totem");
        defaultBlocks.put(STRUCTURES, struc);

        List<String> traps = new ArrayList<>();
        defaultBlocks.put(TRAPS, traps);

        List<String> builds = new ArrayList<>();
        defaultBlocks.put(BUILDS, builds);

        return defaultBlocks;
    }

    @Override
    public Map<String, List<String>> getFreeDefaultObjectsName() {
        Map<String, List<String>> defaultBlocks = new HashMap<>();

        List<String> custom = new ArrayList<>();
        custom.add("Free_Test");
        custom.add("Free_Test2");
        defaultBlocks.put(CUSTOM, custom);

        List<String> gen = new ArrayList<>();
        defaultBlocks.put(GENERATORS, gen);

        List<String> struc = new ArrayList<>();
        defaultBlocks.put(STRUCTURES, struc);

        List<String> traps = new ArrayList<>();
        traps.add("Free_Mine");
        defaultBlocks.put(TRAPS, traps);

        List<String> builds = new ArrayList<>();
        builds.add("Free_Hut");
        defaultBlocks.put(BUILDS, builds);

        return defaultBlocks;
    }

    @Override
    public void configVersionsConverter(File file) {
        ConfigConverter4v2.updateTo4v2(file);
    }

    @Override
    public Optional<NewExecutableBlock> getObject(FileConfiguration itemConfig, String id, boolean showError, boolean isPremiumLoading, String path) {

        List<String> errors = new ArrayList<>();
        NewExecutableBlock item = new NewExecutableBlock(id, path);
        errors.addAll(item.load(ExecutableItems.plugin, itemConfig, isPremiumLoading));

        if (showError) {
            for (String s : errors) {
                ExecutableItems.plugin.getServer().getLogger().severe(s);
            }
        }
        return Optional.ofNullable(item);
    }

    public static NewExecutableBlockLoader getInstance() {
        if (instance == null) instance = new NewExecutableBlockLoader();
        return instance;
    }

}
