package com.ssomar.executableblocks.executableblocks.activators;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.configs.api.PlaceholderAPI;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.activators.editor.NewActivatorEBFeatureEditor;
import com.ssomar.executableblocks.executableblocks.activators.editor.NewActivatorEBFeatureEditorManager;
import com.ssomar.executableblocks.executableblocks.placedblocks.ExecutableBlockPlaced;
import com.ssomar.executableitems.ExecutableItems;
import com.ssomar.score.SsomarDev;
import com.ssomar.score.commands.runnable.ActionInfo;
import com.ssomar.score.menu.GUI;
import com.ssomar.score.sobject.HigherFormSObject;
import com.ssomar.score.sobject.sactivator.EventInfo;
import com.ssomar.score.sobject.sactivator.SOption;
import com.ssomar.score.splugin.SPlugin;
import com.ssomar.score.utils.FixedMaterial;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.placeholders.StringPlaceholder;
import com.ssomar.scoretestrecode.features.FeatureAbstract;
import com.ssomar.scoretestrecode.features.FeatureInterface;
import com.ssomar.scoretestrecode.features.FeatureParentInterface;
import com.ssomar.scoretestrecode.features.custom.activators.activator.NewSActivator;
import com.ssomar.scoretestrecode.features.custom.activators.activator.NewSActivatorWithLoopFeature;
import com.ssomar.scoretestrecode.features.custom.commands.block.BlockCommandsFeature;
import com.ssomar.scoretestrecode.features.custom.commands.entity.EntityCommandsFeature;
import com.ssomar.scoretestrecode.features.custom.commands.player.PlayerCommandsFeature;
import com.ssomar.scoretestrecode.features.custom.conditions.block.parent.BlockConditionsFeature;
import com.ssomar.scoretestrecode.features.custom.conditions.customei.parent.CustomEIConditionsFeature;
import com.ssomar.scoretestrecode.features.custom.conditions.entity.parent.EntityConditionsFeature;
import com.ssomar.scoretestrecode.features.custom.conditions.placeholders.group.PlaceholderConditionGroupFeature;
import com.ssomar.scoretestrecode.features.custom.conditions.player.parent.PlayerConditionsFeature;
import com.ssomar.scoretestrecode.features.custom.conditions.world.parent.WorldConditionsFeature;
import com.ssomar.scoretestrecode.features.custom.detailedblocks.DetailedBlocks;
import com.ssomar.scoretestrecode.features.custom.loop.LoopFeatures;
import com.ssomar.scoretestrecode.features.custom.required.parent.RequiredGroup;
import com.ssomar.scoretestrecode.features.types.BooleanFeature;
import com.ssomar.scoretestrecode.features.types.ColoredStringFeature;
import com.ssomar.scoretestrecode.features.types.IntegerFeature;
import com.ssomar.scoretestrecode.features.types.SOptionFeature;
import com.ssomar.scoretestrecode.features.types.list.ListDamageCauseFeature;
import com.ssomar.scoretestrecode.features.types.list.ListDetailedEntityFeature;
import com.ssomar.scoretestrecode.features.types.list.ListUncoloredStringFeature;
import com.ssomar.sevents.events.projectile.hitentity.ProjectileHitEntityEvent;
import com.ssomar.sevents.events.projectile.hitplayer.ProjectileHitPlayerEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@Getter
@Setter
public class NewActivatorEBFeature extends NewSActivator<NewActivatorEBFeature, NewActivatorEBFeatureEditor, NewActivatorEBFeatureEditorManager> implements NewSActivatorWithLoopFeature {


    private ColoredStringFeature displayName;

    private SOptionFeature optionFeature;

    private RequiredGroup requiredGroup;

    private IntegerFeature usageModification;

    private BlockCommandsFeature commands;
    private PlayerCommandsFeature ownerCommands;
    private PlayerCommandsFeature playerCommands;
    private EntityCommandsFeature entityCommands;
    private BlockCommandsFeature blockCommands;

    private BooleanFeature silenceOutput;
    private BooleanFeature cancelEvent;

    private DetailedBlocks detailedBlocks;
    private ListDetailedEntityFeature detailedEntities;
    private ListDamageCauseFeature detailedDamageCauses;
    private ListUncoloredStringFeature detailedCommands;

    private PlayerConditionsFeature playerConditions;
    private PlayerConditionsFeature ownerConditions;
    private BlockConditionsFeature blockConditions;
    private BlockConditionsFeature targetBlockConditions;
    private EntityConditionsFeature entityConditions;
    private WorldConditionsFeature worldConditions;
    private CustomEIConditionsFeature customEIConditions;
    private PlaceholderConditionGroupFeature placeholderConditions;

    /**
     * LOOP option only
     **/
    private LoopFeatures loopFeatures;

    /* Cooldowns / delay */
    private int globalPlayerCooldown = 0;
    private boolean inTickGlobalPlayerCooldown = false;
    private boolean displayGlobalPlayerCooldownMsg = true;
    private String globalPlayerCooldownMsg = "";
    private boolean cancelEventIfInGlobalPlayerCooldown = false;

    private int perPlayerCooldown = 0;
    private boolean inTickPerPlayerCooldown = false;
    private boolean displayPerPlayerCooldownMsg = true;
    private String perPlayerCooldownMsg = "";
    private boolean cancelEventIfInPerPlayerCooldown = false;


    private static final boolean DEBUG = false;

    public NewActivatorEBFeature(FeatureParentInterface parent, String id) {
        super(parent, id);
        reset();
    }

    @Override
    public String getParentObjectId() {
        if (getParent() instanceof FeatureAbstract) {
            FeatureAbstract parent = (FeatureAbstract) getParent();
            while (parent instanceof FeatureAbstract && parent.getParent() != parent) {
                if (parent instanceof NewExecutableBlock) {
                    return ((NewExecutableBlock) parent).getId();
                } else parent = (FeatureAbstract) parent.getParent();
            }
        }
        return "";
    }

    @Override
    public SOption getOption() {
        return optionFeature.getValue();
    }

    @Override
    public void run(HigherFormSObject higherFormSObject, EventInfo eInfo) {

        ExecutableBlockPlaced eBP = (ExecutableBlockPlaced) higherFormSObject;

        if (DEBUG) SsomarDev.testMsg("Activator 0");

        /* Event that activate the activator */
        Event eSrc = eInfo.getEventSource();
        /* Block if there is one */
        Block block = null;
        /* Owner if there is one */
        Player owner = null;
        /* Target Block if there is one */
        Block targetBlock = null;
        /* Material of this block when the activator was activated */
        Material oldMaterial;
        /* Target Entity if there is one */
        Entity entity = null;
        /* Target Player if there is one */
        Player targetPlayer = null;

        block = eBP.getLocation().getBlock();
        Location bLoc = block.getLocation();
        eSrc = eInfo.getEventSource();

        StringPlaceholder sp = new StringPlaceholder();
        sp.setUsage(eBP.getUsage() + "");
        sp.setItem(eBP.getExecutableBlock().getDisplayName().getValue().get());
        sp.setActivator(this.getName());
        if (eInfo.getBlockface().isPresent()) sp.setBlockface(eInfo.getBlockface().get());
        Optional<Projectile> projOpt = eInfo.getProjectile();
        if (projOpt.isPresent()) {
            if (DEBUG) SsomarDev.testMsg(" has projectile");
            if (eInfo.getBlockface().isPresent()) sp.setProjectilePlcHldr(projOpt.get(), eInfo.getBlockface().get());
            else sp.setProjectilePlcHldr(projOpt.get(), "");
        }
        if (eBP.getOwnerUUID().isPresent()) {
            owner = eBP.getOwner();
            sp.setPlayerPlcHldr(eBP.getOwnerUUID().get());
            sp.setOwnerPlcHldr(eBP.getOwnerUUID().get());
        }
        sp.setBlockPlcHldr(block, eBP.getExecutableBlock().getMaterial().getValue().get());

        /* ActionInfo init */
        ActionInfo actionInfo = new ActionInfo(eBP.getExecutableBlock().getDisplayName().getValue().get(), sp);
        actionInfo.setSlot(0);
        actionInfo.setDetailedBlocks(detailedBlocks);
        actionInfo.setEventCallByMineInCube(eInfo.isEventCallByMineinCube());
        actionInfo.setSilenceOutput(silenceOutput.getValue());
        if (eBP.getOwnerUUID().isPresent()) {
            actionInfo.setLauncherUUID(eBP.getOwnerUUID().get());
            actionInfo.setReceiverUUID(eBP.getOwnerUUID().get());
        } else {
            actionInfo.setLauncherUUID(null);
            actionInfo.setReceiverUUID(null);
        }
        actionInfo.setVelocity(eInfo.getVelocity());
        actionInfo.setBlock(block);
        if (eInfo.getOldMaterialBlock().isPresent())
            actionInfo.setOldBlockMaterialName(eInfo.getOldMaterialBlock().get().toString());
        else
			actionInfo.setOldBlockMaterialName(eBP.getExecutableBlock().getMaterial().getValue().get().toString());

        if (DEBUG) SsomarDev.testMsg("Activator 1");

        if (DEBUG) {
            if (eInfo.getDamageCause().isPresent())
                SsomarDev.testMsg("Activator 3 >> &e" + eInfo.getDamageCause().get().name());
            else SsomarDev.testMsg("Activator 3 >> &cNO CAUSE");
        }

        /* Verification of detailedDamageCause option for the activator that have it */
        if (Option.getOptWithDamageCause().contains(optionFeature.getValue()) && !this.detailedDamageCauses.verifCause(eInfo.getDamageCause().get()))
            return;


        /* Verification of detailedCommands option for the activator that have it */
        if (Option.getOptWithCommand().contains(optionFeature.getValue())) {

            SsomarDev.testMsg("Command activator " + eInfo.getCommand());
            String[] split = eInfo.getCommand().get().split(" ");
            int i = 0;
            Map<String, String> map = sp.getExtraPlaceholders();
            for (String arg : split) {
                map.put("%arg" + i + "%", arg);
                i++;
            }

            if (!this.detailedCommands.getValue().isEmpty()) {
                boolean invalid = true;
                for (String s : this.detailedCommands.getValue()) {
                    if (eInfo.getCommand().get().contains(s)) {
                        invalid = false;
                        break;
                    }
                }
                if (invalid) return;
            }
        }

        /* Target block init */
        Optional<Block> targetBlockOpt = eInfo.getTargetBlock();
        if (targetBlockOpt.isPresent()) {
            targetBlock = targetBlockOpt.get();
            sp.setTargetBlockPlcHldr(block);
        }

        if (DEBUG) SsomarDev.testMsg("Activator 4");

        /* Target entity init */
        Optional<Entity> targetEntityOpt = eInfo.getTargetEntity();
        if (targetEntityOpt.isPresent()) {
            entity = targetEntityOpt.get();
            sp.setEntityPlcHldr(entity.getUniqueId());
            actionInfo.setEntityUUID(entity.getUniqueId());
        }
        /* Target player init */
        Optional<Player> targetPlayerOpt = eInfo.getTargetPlayer();
        if (targetPlayerOpt.isPresent()) {
            targetPlayer = targetPlayerOpt.get();
            sp.setTargetPlcHldr(targetPlayer.getUniqueId());
        }

        if (DEBUG) SsomarDev.testMsg("Activator 5");

        SendMessage sm = new SendMessage();
        sm.setSp(sp);

        /* Verification of the world conditions */
        if (!worldConditions.verifCondition(block.getWorld(), Optional.ofNullable(owner), sm, eSrc)) return;

        /* Verification of the block conditions */
        if (!blockConditions.verifConditions(block, Optional.ofNullable(owner), sm, eSrc)) return;

        /* verification of the owner conditions */
        if (owner != null && !ownerConditions.verifCondition(owner, Optional.ofNullable(owner), sm, eSrc)) return;

        /* Verification of the target block conditions */
        if (Option.getOptWithTargetBlock().contains(optionFeature.getValue()) && targetBlock != null) {
            if (!detailedBlocks.isValidMaterial(eInfo.getOldMaterialBlock().get(), eInfo.getOldStatesBlock(), Optional.ofNullable(owner), eSrc))
                return;

            if (!blockConditions.verifConditions(targetBlock, Optional.ofNullable(owner), sm, eSrc)) return;
        }

        /* verification of the target player conditions */
        if (Option.getOptWithPlayer().contains(optionFeature.getValue()) && targetPlayer != null) {
            if (!playerConditions.verifCondition(targetPlayer, Optional.ofNullable(owner), sm, eSrc)) return;
        }

        /* verification of the entity conditions */
        if (Option.getOptWithEntity().contains(optionFeature.getValue()) && entity != null) {
            /* Verification of the detailedEntities option  */
            if (!detailedEntities.isValidEntity(entity)) return;

            if (!entityConditions.verifConditions(entity, Optional.ofNullable(owner), sm, eSrc)) return;
        }

        if (DEBUG) SsomarDev.testMsg("Activator 7");

        /* Verification of the placeholders conditions */
        if (!placeholderConditions.verify(owner, targetPlayer, sp, eSrc)) return;

        if (DEBUG) SsomarDev.testMsg("Activator 7.9");

        /* Verification of the required ExecutableItems option  */
        if (targetPlayer != null && !requiredGroup.verify(targetPlayer, eSrc)) return;

        if (DEBUG) SsomarDev.testMsg("Activator 8-9");


		/* if(targetPlayer != null) {
			if(this.getGlobalPlayerCooldown() > 0) {
				Cooldown cd = new Cooldown(ExecutableBlocks.plugin, eBP.getExecutableBlock(), this, targetPlayer.getUniqueId(), this.getGlobalPlayerCooldown(), this.isInTickGlobalPlayerCooldown(), System.currentTimeMillis(), true);
				CooldownsManager.getInstance().addCooldown(cd);
			}

			if(this.getPerPlayerCooldown() > 0) {
				Cooldown cd = new Cooldown(ExecutableBlocks.plugin, eBP.getExecutableBlock(), this, targetPlayer.getUniqueId(), this.getPerPlayerCooldown(), this.isInTickPerPlayerCooldown(), System.currentTimeMillis(), false);
				CooldownsManager.getInstance().addCooldown(cd);
			}
		}*/

        if (DEBUG) PlaceholderAPI.testMsg("activator debug 4");

        /* Feature to cancel an activator before his complete activation */
        if (eInfo.isMustDoNothing()) {
            cancelEvent(eSrc, cancelEvent.getValue());
            return;
        }

        /* HERE we finish to check all conditions, the activator can be executed */

        /* cancel the event */
        cancelEvent(eSrc, cancelEvent.getValue());

        /* Finally commands are run */
        commands.runCommands(actionInfo, eBP.getExecutableBlock().getDisplayName().getValue().get());

        if (owner != null) {
            ownerCommands.runCommands(actionInfo, eBP.getExecutableBlock().getDisplayName().getValue().get());
        }

        if (targetPlayer != null) {
            ActionInfo aInfo2 = actionInfo.clone();
            aInfo2.setReceiverUUID(targetPlayer.getUniqueId());
            playerCommands.runCommands(aInfo2, eBP.getExecutableBlock().getDisplayName().getValue().get());
        }

        if (entity != null) {
            entityCommands.runCommands(actionInfo, eBP.getExecutableBlock().getDisplayName().getValue().get());
        }

        if (targetBlock != null) {
            actionInfo.setBlock(targetBlock);
            blockCommands.runCommands(actionInfo, eBP.getExecutableBlock().getDisplayName().getValue().get());
        }

        eBP.changeUsage(usageModification.getValue().get());

        SsomarDev.testMsg("Activator 15", DEBUG);
    }

    @Override
    public List<String> getMenuDescription() {
        return null;
    }

    @Override
    public NewSActivator getBuilderInstance(FeatureParentInterface featureParentInterface, String s) {
        return new NewActivatorEBFeature(featureParentInterface, s);
    }

    @Override
    public List<FeatureInterface> getFeatures() {
        List<FeatureInterface> features = new ArrayList<FeatureInterface>(Arrays.asList(displayName, optionFeature));

        features.addAll(Arrays.asList(usageModification, cancelEvent, silenceOutput, requiredGroup, commands, blockConditions, worldConditions, customEIConditions, placeholderConditions));

        if (!Option.getOptionWithoutOwner().contains(optionFeature.getValue())) {
            features.add(ownerCommands);
            features.add(ownerConditions);
        }

        if (Option.getOptWithDamageCause().contains(optionFeature.getValue())) {
            features.add(detailedDamageCauses);
        }

        if (Option.getOptWithCommand().contains(optionFeature.getValue())) {
            features.add(detailedCommands);
        }

        if (Option.getOptWithPlayer().contains(optionFeature.getValue())) {
            features.add(playerCommands);
            features.add(playerConditions);
        }

        if (Option.getOptWithEntity().contains(optionFeature.getValue())) {
            features.add(detailedEntities);
            features.add(entityCommands);
            features.add(entityConditions);
        }

        if (Option.getOptWithTargetBlock().contains(optionFeature.getValue())) {
            features.add(detailedBlocks);
            features.add(blockCommands);
            features.add(targetBlockConditions);
        }

        return features;
    }

    @Override
    public String getParentInfo() {
        return getParent().getParentInfo();
    }

    @Override
    public ConfigurationSection getConfigurationSection() {
        ConfigurationSection parentSection = getParent().getConfigurationSection();
        if (parentSection.isConfigurationSection(getId())) {
            return parentSection.getConfigurationSection(getId());
        } else return parentSection.createSection(getId());
    }

    @Override
    public File getFile() {
        return getParent().getFile();
    }

    @Override
    public void reload() {
        SsomarDev.testMsg("Reloading activator " + getId());
        for (FeatureInterface feature : getParent().getFeatures()) {
            if (feature instanceof NewActivatorEBFeature) {
                NewActivatorEBFeature a = (NewActivatorEBFeature) feature;
                if (a.getId().equals(getId())) {
                    a.setDisplayName(displayName);
                    SsomarDev.testMsg("Reloading activator " + getId() + " option: " + optionFeature.getValue().toString());
                    a.setOptionFeature(optionFeature);
                    a.setUsageModification(usageModification);
                    a.setCancelEvent(cancelEvent);
                    a.setSilenceOutput(silenceOutput);
                    a.setRequiredGroup(requiredGroup);
                    a.setLoopFeatures(loopFeatures);
                    a.setDetailedBlocks(detailedBlocks);
                    a.setDetailedEntities(detailedEntities);
                    a.setDetailedDamageCauses(detailedDamageCauses);
                    a.setDetailedCommands(detailedCommands);
                    a.setCommands(commands);
                    a.setOwnerCommands(ownerCommands);
                    a.setPlayerCommands(playerCommands);
                    a.setEntityCommands(entityCommands);
                    a.setBlockCommands(blockCommands);
                    a.setPlayerConditions(playerConditions);
                    a.setOwnerConditions(ownerConditions);
                    a.setBlockConditions(blockConditions);
                    a.setTargetBlockConditions(targetBlockConditions);
                    a.setEntityConditions(entityConditions);
                    a.setWorldConditions(worldConditions);
                    a.setCustomEIConditions(customEIConditions);
                    a.setPlaceholderConditions(placeholderConditions);
                    break;
                }
            }
        }
    }

    @Override
    public NewActivatorEBFeature clone() {
        NewActivatorEBFeature clone = new NewActivatorEBFeature(getParent(), getId());
        clone.setDisplayName(displayName.clone());
        clone.setOptionFeature(optionFeature.clone());
        clone.setUsageModification(usageModification.clone());
        clone.setCancelEvent(cancelEvent.clone());
        clone.setSilenceOutput(silenceOutput.clone());
        clone.setRequiredGroup(requiredGroup.clone());
        clone.setLoopFeatures(loopFeatures.clone());
        clone.setDetailedBlocks(detailedBlocks.clone());
        clone.setDetailedEntities(detailedEntities.clone());
        clone.setDetailedDamageCauses(detailedDamageCauses.clone());
        clone.setDetailedCommands(detailedCommands.clone());
        clone.setCommands(commands.clone());
        clone.setOwnerCommands(ownerCommands.clone());
        clone.setPlayerCommands(playerCommands.clone());
        clone.setEntityCommands(entityCommands.clone());
        clone.setBlockCommands(blockCommands.clone());
        clone.setOwnerConditions(ownerConditions.clone());
        clone.setPlayerConditions(playerConditions.clone());
        clone.setTargetBlockConditions(targetBlockConditions.clone());
        clone.setBlockConditions(blockConditions.clone());
        clone.setEntityConditions(entityConditions.clone());
        clone.setWorldConditions(worldConditions.clone());
        clone.setCustomEIConditions(customEIConditions.clone());
        clone.setPlaceholderConditions(placeholderConditions.clone());
        return clone;
    }

    @Override
    public void openEditor(@NotNull Player player) {
        NewActivatorEBFeatureEditorManager.getInstance().startEditing(player, this);
    }

    @Override
    public void openBackEditor(@NotNull Player player) {
        getParent().openEditor(player);
    }

    @Override
    public List<String> load(SPlugin sPlugin, ConfigurationSection config, boolean b) {
        List<String> errors = new ArrayList<>();
        if (config.isConfigurationSection(getId())) {
            ConfigurationSection section = config.getConfigurationSection(getId());

            for (FeatureInterface f : getFeatures()) {
                errors.addAll(f.load(sPlugin, section, b));
            }

            if (!Option.getOptionWithoutOwner().contains(optionFeature.getValue())) {
                errors.addAll(ownerCommands.load(sPlugin, section, b));
                errors.addAll(ownerConditions.load(sPlugin, section, b));
            }

            if (Option.getOptWithDamageCause().contains(optionFeature.getValue())) {
                errors.addAll(detailedDamageCauses.load(sPlugin, section, b));
            }

            if (Option.getOptWithCommand().contains(optionFeature.getValue())) {
                errors.addAll(detailedCommands.load(sPlugin, section, b));
            }

            if (Option.getOptWithPlayer().contains(optionFeature.getValue())) {
                errors.addAll(playerCommands.load(sPlugin, section, b));
                errors.addAll(playerConditions.load(sPlugin, section, b));
            }

            if (Option.getOptWithEntity().contains(optionFeature.getValue())) {
                errors.addAll(entityCommands.load(sPlugin, section, b));
                errors.addAll(entityConditions.load(sPlugin, section, b));
                errors.addAll(detailedEntities.load(sPlugin, section, b));
            }

            if (Option.getOptWithTargetBlock().contains(optionFeature.getValue())) {
                errors.addAll(targetBlockConditions.load(sPlugin, section, b));
                errors.addAll(blockCommands.load(sPlugin, section, b));
                errors.addAll(detailedBlocks.load(sPlugin, section, b));
            }

        }
        return errors;
    }

    @Override
    public void save(ConfigurationSection config) {
        config.set(getId(), null);
        ConfigurationSection attributeConfig = config.createSection(getId());
        for (FeatureInterface feature : getFeatures()) {
            feature.save(attributeConfig);
        }
    }

    @Override
    public NewActivatorEBFeature getValue() {
        return this;
    }

    @Override
    public NewActivatorEBFeature initItemParentEditor(GUI gui, int slot) {
        String[] finalDescription = new String[getEditorDescription().length + 2];
        System.arraycopy(getEditorDescription(), 0, finalDescription, 0, getEditorDescription().length);
        finalDescription[finalDescription.length - 2] = "&7Option: &e" + optionFeature.getValue();
        finalDescription[finalDescription.length - 1] = gui.CLICK_HERE_TO_CHANGE;

        gui.createItem(getEditorMaterial(), 1, slot, gui.TITLE_COLOR + getEditorName() + " - " + "(" + getId() + ")", false, false, finalDescription);
        return this;
    }

    @Override
    public void updateItemParentEditor(GUI gui) {

    }

    @Override
    public boolean isTheFeatureClickedParentEditor(String featureClicked) {
        return featureClicked.contains(getEditorName()) && featureClicked.contains("(" + getId() + ")");
    }

    @Override
    public void reset() {
        this.displayName = new ColoredStringFeature(this, "name", Optional.of("&eActivator"), "DisplayName", new String[]{"&7&oThe display name"}, GUI.WRITABLE_BOOK, false, false);
        this.optionFeature = new SOptionFeature(ExecutableItems.plugin, Option.PLAYER_ALL_CLICK_ON, this, "option", "Option", new String[]{"&7&oThe Option"}, Material.COMPASS, false);

        if (ExecutableBlocks.plugin.isLotOfWork())
            this.usageModification = new IntegerFeature(this, "usageModification", Optional.of(-1), "Usage Modification", new String[]{"&7&oThe usage modification"}, Material.BUCKET, true);
        else
            this.usageModification = new IntegerFeature(this, "usageModification", Optional.of(0), "Usage Modification", new String[]{"&7&oThe usage modification"}, Material.BUCKET, true);

        this.cancelEvent = new BooleanFeature(this, "cancelEvent", false, "Cancel Event", new String[]{"&7&oCancel the vanilla event"}, Material.LEVER, false, false);
        this.silenceOutput = new BooleanFeature(this, "silenceOutput", false, "Silence Output", new String[]{"&7&oSilence the output of the commands", "&7&o(Only in the console)"}, Material.LEVER, false, false);

        this.requiredGroup = new RequiredGroup(this);

        this.loopFeatures = new LoopFeatures(this);

        this.detailedBlocks = new DetailedBlocks(this);

        this.detailedEntities = new ListDetailedEntityFeature(this, "detailedEntities", new ArrayList<>(), "Detailed Entities", new String[]{"&7&oSpecify a list of entities that", "&7&ocan be affected", "&7&oempty = all entities"}, FixedMaterial.getMaterial(Arrays.asList("ZOMBIE_HEAD", "MONSTER_EGG")), false, false);

        this.detailedDamageCauses = new ListDamageCauseFeature(this, "detailedDamageCauses", new ArrayList<>(), "Detailed DamageCauses", new String[]{"&7&oSpecify a list of damageCauses that", "&7&ocan be affected", "&7&oempty = all causes"}, Material.BONE, false, false);

        this.detailedCommands = new ListUncoloredStringFeature(this, "detailedCommands", new ArrayList<>(), "Detailed Commands", new String[]{"&7&oSpecify a list of commands that", "&7&ocan be affected", "&7&oempty = no command", "&7Example: &a/test"}, GUI.WRITABLE_BOOK, false, false, Optional.empty());

        this.commands = new BlockCommandsFeature(this, "commands", "Block Commands", new String[]{"&7&oThe block commands to execute"}, FixedMaterial.getMaterial(Arrays.asList("COMMAND_BLOCK", "COMMAND")), false);

        this.ownerCommands = new PlayerCommandsFeature(this, "ownerCommands", "Owner Commands", new String[]{"&7&oThe owner commands to execute"}, FixedMaterial.getMaterial(Arrays.asList("COMMAND_BLOCK", "COMMAND")), false);

        this.playerCommands = new PlayerCommandsFeature(this, "playerCommands", "Player Commands", new String[]{"&7&oThe player commands to execute"}, FixedMaterial.getMaterial(Arrays.asList("COMMAND_BLOCK", "COMMAND")), false);

        this.entityCommands = new EntityCommandsFeature(this, "entityCommands", "Entity Commands", new String[]{"&7&oThe entity commands to execute"}, FixedMaterial.getMaterial(Arrays.asList("COMMAND_BLOCK", "COMMAND")), false);

        this.blockCommands = new BlockCommandsFeature(this, "blockCommands", "Target Block Commands", new String[]{"&7&oThe target block commands to execute"}, FixedMaterial.getMaterial(Arrays.asList("COMMAND_BLOCK", "COMMAND")), false);

        this.playerConditions = new PlayerConditionsFeature(this, "playerConditions", "Player Conditions", new String[]{});

        this.ownerConditions = new PlayerConditionsFeature(this, "ownerConditions", "Owner Conditions", new String[]{});

        this.blockConditions = new BlockConditionsFeature(this, "blockConditions", "Block Conditions", new String[]{});

        this.targetBlockConditions = new BlockConditionsFeature(this, "targetBlockConditions", "Target Block Conditions", new String[]{});

        this.entityConditions = new EntityConditionsFeature(this, "entityConditions", "Entity Conditions", new String[]{});

        this.worldConditions = new WorldConditionsFeature(this, "worldConditions", "World Conditions", new String[]{});

        this.customEIConditions = new CustomEIConditionsFeature(this, "customConditions", "Custom EI Conditions", new String[]{});

        this.placeholderConditions = new PlaceholderConditionGroupFeature(this);

    }

    static void cancelEvent(Event e, boolean condition) {
        if (e != null && condition && e instanceof Cancellable) {
            if (e instanceof ProjectileHitEntityEvent) {
                ((ProjectileHitEntityEvent) e).getEntity().remove();
            } else if (e instanceof ProjectileHitPlayerEvent) {
                ((ProjectileHitPlayerEvent) e).getEntity().remove();
            }

            ((Cancellable) e).setCancelled(true);
        }

    }
}
