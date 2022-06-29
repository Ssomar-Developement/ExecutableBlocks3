package com.ssomar.executableblocks.executableblocks;


import com.mojang.authlib.GameProfile;
import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.editor.NewExecutableBlocksEditor;
import com.ssomar.executableblocks.executableblocks.activators.NewActivatorEBFeature;
import com.ssomar.executableblocks.executableblocks.editor.NewExecutableBlockEditor;
import com.ssomar.executableblocks.executableblocks.editor.NewExecutableBlockEditorManager;
import com.ssomar.executableblocks.executableblocks.features.customization.ExecutableItemsFeatures;
import com.ssomar.executableblocks.executableblocks.features.customization.ItemsAdderFeatures;
import com.ssomar.executableblocks.executableblocks.features.customization.OraxenFeatures;
import com.ssomar.executableblocks.executableblocks.loader.NewExecutableBlockLoader;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlacedManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.LocationConverter;
import com.ssomar.executableblocks.configs.api.PlaceholderAPI;
import com.ssomar.executableblocks.configs.messages.Message;
import com.ssomar.executableblocks.features.CreationTypeFeature;
import com.ssomar.executableblocks.utils.CreationType;
import com.ssomar.score.SCore;
import com.ssomar.score.api.executableblocks.config.ExecutableBlockInterface;
import com.ssomar.score.api.executableblocks.placed.ExecutableBlockPlacedInterface;
import com.ssomar.score.configs.messages.MessageMain;
import com.ssomar.score.menu.GUI;
import com.ssomar.score.splugin.SPlugin;
import com.ssomar.score.utils.FixedMaterial;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.StringConverter;
import com.ssomar.score.utils.ToolsListMaterial;
import com.ssomar.score.utils.placeholders.StringPlaceholder;
import com.ssomar.scoretestrecode.features.FeatureInterface;
import com.ssomar.scoretestrecode.features.FeatureParentInterface;
import com.ssomar.scoretestrecode.features.custom.activators.activator.NewSActivator;
import com.ssomar.scoretestrecode.features.custom.activators.group.ActivatorsFeature;
import com.ssomar.scoretestrecode.features.custom.blocktitle.BlockTitleFeatures;
import com.ssomar.scoretestrecode.features.types.*;
import com.ssomar.scoretestrecode.features.types.list.ListColoredStringFeature;
import com.ssomar.scoretestrecode.sobject.NewSObject;
import com.ssomar.scoretestrecode.sobject.menu.NewSObjectsManagerEditor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Getter
@Setter
public class NewExecutableBlock extends NewSObject<NewExecutableBlock, NewExecutableBlockEditor, NewExecutableBlockEditorManager>  implements ExecutableBlockInterface {

    private String id;
    private String path;

    /**
     * Activators / triggers
     **/
    private ActivatorsFeature activatorsFeature;

    /**
     * Features
     **/

    private CreationTypeFeature creationType;

    /* When Type is Basic creation */
    private ColoredStringFeature displayName;
    private ListColoredStringFeature lore;
    private MaterialFeature material;
    private EntityTypeFeature spawnerType;

    private BlockTitleFeatures title;

    private IntegerFeature usage;

    private BooleanFeature cancelGravity;


   // TODO : Add features
    private boolean dropBlockIfItIsBroken;
    private boolean dropBlockWhenItExplodes;
    private boolean dropBlockWhenItBurns;

    private List<String> onlyBreakableWithEI;

    private OraxenFeatures oraxenTextures;

    private ItemsAdderFeatures itemsAdderBlockFeatures;

    private ExecutableItemsFeatures executableItemsFeatures;


    /**
     * For the clone method, the parent is the real instance
     **/
    public NewExecutableBlock(FeatureParentInterface parent, String id, String path) {
        super(parent, "EB", "EB", new String[]{}, FixedMaterial.getMaterial(Arrays.asList("GRASS_BLOCK", "GRASS")));
        this.id = id;
        this.path = path;
        reset();
    }

    public NewExecutableBlock(String id, String path) {
        super("EB", "EB", new String[]{}, Material.EMERALD);
        this.id = id;
        this.path = path;
        reset();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String s) {
        this.id = s;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("§7ID: §f" + id);
        description.add("§7Path: §f" + path);
        description.add("§7Activators: ");
        for (NewSActivator activator : activatorsFeature.getActivators().values()) {
            description.add("§7- " + activator.getId());
        }
        return description;
    }

    @Override
    public List<FeatureInterface> getFeatures() {
        /** Variables must be loaded before activator feature **/
        List<FeatureInterface> features = new ArrayList<FeatureInterface>();
        features.add(creationType);

        if(creationType.getValue().get().equals(CreationType.BASIC_CREATION)) {
            features.add(displayName);
            features.add(lore);
            features.add(material);
            features.add(spawnerType);
        }
        else if(creationType.getValue().get().equals(CreationType.IMPORT_FROM_ORAXEN)){

        }
        else if(creationType.getValue().get().equals(CreationType.IMPORT_FROM_EXECUTABLE_ITEMS)){

        }
        else if(creationType.getValue().get().equals(CreationType.IMPORT_FROM_ITEMS_ADDER)){

        }
        features.add(title);
        features.add(usage);
        features.add(cancelGravity);
        features.add(activatorsFeature);
        return features;
    }

    @Override
    public String getParentInfo() {
        return "(Item: " + id + ")";
    }

    @Override
    public ConfigurationSection getConfigurationSection() {
        File file = getFile();

        FileConfiguration config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
        return config;
    }

    @Override
    public File getFile() {
        File file = new File(path);
        if (!file.exists()) {
            try {
                new File(path).createNewFile();
                file = NewExecutableBlockLoader.getInstance().searchFileOfObject(id);
            } catch (IOException ignored) {
                return null;
            }
        }
        return file;
    }

    @Override
    public void reload() {
        if (getParent() instanceof NewExecutableBlock) {
            NewExecutableBlock item = (NewExecutableBlock) getParent();
            item.setActivatorsFeature(activatorsFeature);
            item.setCreationType(creationType);
            item.setDisplayName(displayName);
            item.setLore(lore);
            item.setMaterial(material);
            item.setSpawnerType(spawnerType);
            item.setTitle(title);
            item.setUsage(usage);
            item.setCancelGravity(cancelGravity);
        }
    }

    @Override
    public NewExecutableBlock clone() {
        NewExecutableBlock item = new NewExecutableBlock(this, id, path);
        item.setActivatorsFeature(activatorsFeature.clone());
        item.setCreationType(creationType.clone());
        item.setDisplayName(displayName.clone());
        item.setLore(lore.clone());
        item.setMaterial(material.clone());
        item.setSpawnerType(spawnerType.clone());
        item.setTitle(title.clone());
        item.setUsage(usage.clone());
        item.setCancelGravity(cancelGravity.clone());

        return item;
    }

    @Override
    public void openEditor(@NotNull Player player) {
        NewExecutableBlockEditorManager.getInstance().startEditing(player, this);
    }

    @Override
    public void openBackEditor(@NotNull Player player) {
        NewSObjectsManagerEditor.getInstance().startEditing(player, new NewExecutableBlocksEditor());
    }

    @Override
    public List<String> load(SPlugin sPlugin, ConfigurationSection configurationSection, boolean b) {
        List<String> errors = new ArrayList<>();
        for (FeatureInterface feature : getFeatures()) {
            errors.addAll(feature.load(sPlugin, configurationSection, b));
        }

        /** Otherwise theses features are not loaded since the creationType is not load when getFeatures **/
        CreationType cT = creationType.getValue().get();

        if(cT.equals(CreationType.BASIC_CREATION)) {
            errors.addAll(displayName.load(sPlugin, configurationSection, b));
            errors.addAll(lore.load(sPlugin, configurationSection, b));
            errors.addAll(material.load(sPlugin, configurationSection, b));
            errors.addAll(spawnerType.load(sPlugin, configurationSection, b));
        }
        else if(cT.equals(CreationType.IMPORT_FROM_ORAXEN)){

        }
        else if(cT.equals(CreationType.IMPORT_FROM_EXECUTABLE_ITEMS)){

        }
        else if(cT.equals(CreationType.IMPORT_FROM_ITEMS_ADDER)){

        }

        return errors;
    }

    @Override
    public void save(ConfigurationSection configurationSection) {
        for (FeatureInterface feature : getFeatures()) {
            feature.save(configurationSection);
        }
        configurationSection.set("config_3", true);
        configurationSection.set("config_update", !ExecutableBlocks.plugin.isLotOfWork());
    }

    @Override
    public NewExecutableBlock getValue() {
        return this;
    }

    @Override
    public NewExecutableBlock initItemParentEditor(GUI gui, int i) {
        return null;
    }

    @Override
    public void updateItemParentEditor(GUI gui) {

    }

    @Override
    public void reset() {
        this.activatorsFeature = new ActivatorsFeature(this, new NewActivatorEBFeature(null, "null"));

        this.displayName = new ColoredStringFeature(this, "name", Optional.of("&eDefault name"), "Custom name", new String[]{"&7&oThe custom name of the block"}, Material.NAME_TAG, false, false);

        this.lore = new ListColoredStringFeature(this, "lore", new ArrayList<>(Arrays.asList("&b&oDefault lore")), "Lore", new String[]{"&7&oThe custom lore of the block"}, Material.PAPER, false, false, Optional.empty());

        this.material = new MaterialFeature(this, "material", Optional.of(Material.STONE), "Material", new String[]{"&7&oThe material of the block"}, Material.STONE, false);

        this.spawnerType = new EntityTypeFeature(this, "spawnerType", Optional.of(EntityType.ZOMBIE), "Spawner type", new String[]{"&7&oThe type of the spawner"}, Material.SPAWNER, false);

        this.title = new BlockTitleFeatures(this);

        this.usage = new IntegerFeature(this, "usage", Optional.of(-1), "Usage", new String[]{"&7&oThe usage of the block"}, Material.BUCKET, false);

        this.cancelGravity = new BooleanFeature(this, "cancelGravity", false, "Cancel gravity", new String[]{"&7&oIf the block has gravity"}, Material.LEVER, false, false);
    }


    @Override
    public ItemStack buildItem(int i, Optional<Player> creatorOpt) {
        return buildItem(i, creatorOpt, Optional.empty());
    }

    @Override
    public Item dropItem(Location location, int i) {
        ItemStack item = buildItem(i, Optional.empty(), Optional.empty());
        Item drop = location.getWorld().dropItem(location, item);
        return drop;
    }

    @Override
    public ItemStack addExecutableBlockInfos(ItemStack itemStack, Optional<Player> creatorOpt) {
        return buildItem(1, creatorOpt, Optional.ofNullable(itemStack));
    }

    public ItemStack buildItem(int i, Optional<Player> creatorOpt, Optional<ItemStack> itemStackOpt) {
        ItemStack item = null;

        // TODO TO REWORK
        /*
        if (!itemStackOpt.isPresent()) {
            Optional<ItemStack> itemsAdderItemStackOpt = itemsAdderBlockFeatures.buildItemsAdderItemStack();
            if (itemsAdderItemStackOpt.isPresent()) item = itemsAdderItemStackOpt.get();

            if (item == null) {
                Optional<ItemStack> oraxenItemStackOpt = oraxenTextures.buildOraxenItemStack();
                if (oraxenItemStackOpt.isPresent()) item = oraxenItemStackOpt.get();
            }
        }

        if (executableItemsFeatures.isHasExecutableItems()) {
            Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(executableItemsFeatures.getExecutableItemsID());
            if (eiOpt.isPresent()) {
                if (itemStackOpt.isPresent()) item = eiOpt.get().addExecutableItemInfos(itemStackOpt.get(), creatorOpt);
                else item = eiOpt.get().buildItem(1, creatorOpt);
            }
        }

        if (item == null) {
            item = new ItemStack(material);
            ItemMeta itemMeta1 = item.getItemMeta();
            itemMeta1.setDisplayName(StringConverter.coloredString(this.name));
            List<String> convert = new ArrayList<>();
            for (String s : this.lore) {
                if (creatorOpt.isPresent()) {
                    convert.add(s.replaceAll("%player%", creatorOpt.get().getName()));
                    continue;
                }
                convert.add(s);
            }
            itemMeta1.setLore(convert);
            item.setItemMeta(itemMeta1);
        }*/

        ItemMeta itemMeta2 = item.getItemMeta();
        NamespacedKey key2 = new NamespacedKey(ExecutableBlocks.getPluginSt(), "EB-ID");
        itemMeta2.getPersistentDataContainer().set(key2, PersistentDataType.STRING, this.id);

        item.setItemMeta(itemMeta2);

        return item;
    }



    private static void changeSkin(Block b, ItemMeta meta) {

        Field profileField = null;
        GameProfile profile = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profile = (GameProfile) profileField.get(meta);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (profile == null) return;


        final Skull skull = (Skull) b.getState();
        try {
            profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.update(); // so that the result can be seen
    }

    public void addSpawnerType(Location location) {
        if (!creationType.getValue().get().equals(CreationType.BASIC_CREATION) || !material.getValue().get().equals(Material.SPAWNER)) return;
        BukkitRunnable runnable3 = new BukkitRunnable() {
            @Override
            public void run() {
                Block block = location.getBlock();
                if (block.getType() != Material.SPAWNER) return;
                CreatureSpawner cs = (CreatureSpawner) block.getState();
                cs.setSpawnedType(getSpawnerType().getValue().get());
            }
        };
        runnable3.runTaskLater(SCore.plugin, 1);
    }

    public ExecutableBlockPlaced place(Optional<Player> ownerOpt, @NotNull Location location, boolean place) {
        if(ownerOpt.isPresent()) return place2(Optional.ofNullable(ownerOpt.get().getUniqueId()), location, place);
        else return place2(Optional.empty(), location, place);
    }

    public ExecutableBlockPlaced place2(Optional<UUID> ownerOpt, @NotNull Location location, boolean place) {
        //SsomarDev.testMsg("Loc 1: "+loc);
        Location bLoc = LocationConverter.convert(location, false, true);
        UUID ownerUUID = null;
        if (ownerOpt.isPresent()) ownerUUID = ownerOpt.get();

        //SsomarDev.testMsg("Loc 2: "+bLoc);
        //SsomarDev.testMsg("Loc 3: "+bLoc.getBlock().getLocation());

        /* Remove if one exists */
        Optional<ExecutableBlockPlacedInterface> eBP2Opt = ExecutableBlockPlacedManager.getInstance().getExecutableBlockPlaced(bLoc);
        if (eBP2Opt.isPresent()) {
            ExecutableBlockPlacedManager.getInstance().removeExecutableBlockPlaced((ExecutableBlockPlaced) eBP2Opt.get());
        }

        Block block = bLoc.getBlock();

        if (place) {
            if (itemsAdderBlockFeatures.isHasItemsAdderBlock()) {
                itemsAdderBlockFeatures.placeItemsAdderItemStack(bLoc.clone().subtract(0, 0.5, 0), buildItem(1, Optional.empty(), Optional.empty()));
            } else if (executableItemsFeatures.isHasExecutableItems()) {
                ItemStack item = buildItem(1, Optional.empty());
                block.setType(ToolsListMaterial.getInstance().getBlockMaterialOfItem(item.getType()));
                if (item.getType().equals(Material.PLAYER_HEAD)) {
                    if (item.hasItemMeta()) {
                        changeSkin(block, item.getItemMeta());
                    }
                }
            } else {
                block.setType(ToolsListMaterial.getInstance().getBlockMaterialOfItem(this.getMaterial().getValue().get()));
            }
        }

        ExecutableBlockPlaced eBP = new ExecutableBlockPlaced(UUID.randomUUID(), bLoc, ownerUUID, this.id, this.usage.getValue().get());
        ExecutableBlockPlacedManager.getInstance().addExecutableBlockPlaced(eBP);

        StringPlaceholder placeholder = new StringPlaceholder();
        placeholder.setOwnerPlcHldr(ownerUUID);
        Location holoLoc = title.spawn(eBP.getLocation(), placeholder);
        if(holoLoc != null) eBP.setHoloLocation(holoLoc);

        addSpawnerType(bLoc);

        return eBP;
    }

    public boolean hasBlockPerm(Player p, boolean showError) {

        if (p.isOp()) return true;

        if (PlaceholderAPI.isLotOfWork()) {
            if (!(p.hasPermission("ExecutableBlocks.block." + id) || p.hasPermission("eb.block." + id)
                    || p.hasPermission("ExecutableBlocks.block.*") || p.hasPermission("eb.block.*")
                    || p.hasPermission("*"))) {
                if (showError)
                    SendMessage.sendMessageNoPlch(p, StringConverter.replaceVariable(MessageMain.getInstance().getMessage(ExecutableBlocks.plugin, Message.PERMISSION_MESSAGE), p.getName(),
                            displayName.getValue().get(), "", 0));
                return false;
            }
        } else {
            if (p.hasPermission("*"))
                return true;
            if (!(p.hasPermission("ExecutableBlocks.block." + id) || p.hasPermission("eb.block." + id)
                    || p.hasPermission("ExecutableBlocks.block.*") || p.hasPermission("eb.block.*"))
                    || p.hasPermission("-eb.block." + id)) {
                if (showError)
                    SendMessage.sendMessageNoPlch(p, StringConverter.replaceVariable(MessageMain.getInstance().getMessage(ExecutableBlocks.plugin, Message.PERMISSION_MESSAGE), p.getName(),
                            displayName.getValue().get(), "", 0));
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable NewSActivator getActivator(String s) {
        for (NewSActivator acti : activatorsFeature.getActivators().values()) {
            if (acti.getId().equalsIgnoreCase(id)) {
                return acti;
            }
        }
        return null;
    }

    @Override
    public ActivatorsFeature getActivators() {
        return activatorsFeature;
    }


    public boolean verifyOnlyBreakableWithEI(String id) {
        for (String s : this.onlyBreakableWithEI) {
            if (s.equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    public boolean hasActiveTitle() {
        return title != null && title.getValue().getActiveTitle().getValue();
    }
}
