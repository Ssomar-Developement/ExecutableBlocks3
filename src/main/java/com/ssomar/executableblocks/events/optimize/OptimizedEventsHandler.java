package com.ssomar.executableblocks.events.optimize;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.executableblocks.activators.Option;
import com.ssomar.executableblocks.events.activators.*;
import com.ssomar.score.utils.Couple;
import com.ssomar.sevents.EventName;
import com.ssomar.sevents.registration.DynamicRegistration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptimizedEventsHandler {

    private static OptimizedEventsHandler instance;

    private Map<Option, Couple<List<EventName>, Listener>> registered;

    public OptimizedEventsHandler() {
        init();
    }

    public void init() {
        registered = new HashMap<>();
    }

    public void reload() {
        for (Couple<List<EventName>, Listener> couple : registered.values()) {
            if (couple.getElem1() != null) {
                for (EventName eN : couple.getElem1()) {
                    DynamicRegistration.getInstance().unregister(eN, ExecutableBlocks.plugin);
                }
            }
            if (couple.getElem2() != null) {
                HandlerList.unregisterAll(couple.getElem2());
            }
        }
        init();
    }

    public void read(Option option) {
        if (!registered.containsKey(option)) {
            List<EventName> eventsName = new ArrayList<>();
            Listener mainListerner = null;
            switch (option) {

                case LOOP:
                    /* Not registered here */
                    break;
                case PLAYER_WALK_ON:
                    mainListerner = new PlayerWalkOnEvent();
                    break;
                case ENTITY_WALK_ON:
                    /* Not registered here */
                    break;
                case CROP_GROW:
                    mainListerner = new CropGrowEvent();
                    break;
                case CROP_PLACE_BLOCK:
                    mainListerner = new CropPlaceBlockEvent();
                    break;
                case PLAYER_JUMP_ON:
                    mainListerner = new PlayerJumpOnEvent();
                    break;
                case PLAYER_RIGHT_CLICK_ON:
                    eventsName.add(EventName.PLAYER_RIGHT_CLICK_EVENT);
                    DynamicRegistration.getInstance().register(EventName.PLAYER_RIGHT_CLICK_EVENT, ExecutableBlocks.plugin);
                    mainListerner = new PlayerRightClickOnEvent();
                    break;
                case PLAYER_LEFT_CLICK_ON:
                    eventsName.add(EventName.PLAYER_LEFT_CLICK_EVENT);
                    DynamicRegistration.getInstance().register(EventName.PLAYER_LEFT_CLICK_EVENT, ExecutableBlocks.plugin);
                    mainListerner = new PlayerLeftClickOnEvent();
                    break;
                case PLAYER_ALL_CLICK_ON:
                    eventsName.add(EventName.PLAYER_RIGHT_CLICK_EVENT);
                    eventsName.add(EventName.PLAYER_LEFT_CLICK_EVENT);
                    DynamicRegistration.getInstance().register(EventName.PLAYER_LEFT_CLICK_EVENT, ExecutableBlocks.plugin);
                    DynamicRegistration.getInstance().register(EventName.PLAYER_RIGHT_CLICK_EVENT, ExecutableBlocks.plugin);
                    mainListerner = new PlayerAllClickOnEvent();
                    break;
                case PLAYER_PLACE:
                    mainListerner = new PlayerBlockPlaceEvent();
                    break;
                case PLAYER_PRESS:
                    eventsName.add(EventName.PLAYER_PRESS_PLATE_EVENT);
                    DynamicRegistration.getInstance().register(EventName.PLAYER_PRESS_PLATE_EVENT, ExecutableBlocks.plugin);
                    mainListerner = new PlayerPressPlateEvent();
                    break;
                case PROJECTILE_HIT:
                    eventsName.add(EventName.PROJECTILE_HIT_BLOCK);
                    DynamicRegistration.getInstance().register(EventName.PROJECTILE_HIT_BLOCK, ExecutableBlocks.plugin);
                    mainListerner = new ProjectileHitBlockEvent();
                    break;
                case EXPLOSION_HIT:
                    mainListerner = new BlockHitByExplosionEvent();
                    break;
                case PLAYER_DEATH_ON:
                    mainListerner = new PlayerDeathOnEvent();
                    break;
                case PLAYER_EAT_ON:
                    mainListerner = new PlayerEatOnEvent();
                    break;
                case PLAYER_SNEAK_ON:
                    eventsName.add(EventName.PLAYER_ACTIVE_SNEAK_EVENT);
                    DynamicRegistration.getInstance().register(EventName.PLAYER_ACTIVE_SNEAK_EVENT, ExecutableBlocks.plugin);
                    mainListerner = new PlayerSneakOnEvent();
                    break;
                case PLAYER_FALL_ON:
                    mainListerner = new PlayerFallOnEvent();
                    break;
                case PLAYER_SPRINT_ON:
                    mainListerner = new PlayerSprintOnEvent();
                    break;
            }
            if (mainListerner != null) {
                ExecutableBlocks.plugin.getServer().getPluginManager().registerEvents(mainListerner, ExecutableBlocks.plugin);
                registered.put(option, new Couple<>(eventsName, mainListerner));
            }
        }
    }

    public void displayOptimisation() {
        ExecutableBlocks.plugin.getServer().getLogger().info("================ [ExecutableBlocks Check Options] ================");
        ExecutableBlocks.plugin.getServer().getLogger().info("All options available in ExecutableBlocks: ");
        //ExecutableItems.plugin.getServer().getLogger().info("More * = More performance hungry");
        ExecutableBlocks.plugin.getServer().getLogger().info("true: an EwecutableBlock has an activator that use it.");
        ExecutableBlocks.plugin.getServer().getLogger().info("false: no EwecutableBlock require this event. (better performance)");
        ExecutableBlocks.plugin.getServer().getLogger().info("Use only what you need, more false = better performance");
        ExecutableBlocks.plugin.getServer().getLogger().info("");
        for (Option o : Option.values()) {
            ExecutableBlocks.plugin.getServer().getLogger().info(o + " >> " + registered.containsKey(o));
        }
        ExecutableBlocks.plugin.getServer().getLogger().info("================ [ExecutableBlocks Check Options] ================");
    }

    public static OptimizedEventsHandler getInstance() {
        if (instance == null) instance = new OptimizedEventsHandler();
        return instance;
    }
}
