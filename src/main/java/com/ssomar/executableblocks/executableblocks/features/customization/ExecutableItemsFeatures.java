package com.ssomar.executableblocks.executableblocks.features.customization;

import com.ssomar.executableblocks.ExecutableBlocks;

import com.ssomar.score.SCore;
import com.ssomar.score.api.ExecutableItemsAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@Getter @Setter
public class ExecutableItemsFeatures {

    private boolean hasExecutableItems;
    private String executableItemsID;

    public ExecutableItemsFeatures() {
        this.hasExecutableItems = false;
        this.executableItemsID = "";
    }

    public ExecutableItemsFeatures(ConfigurationSection section, List<String> error, String id){

        hasExecutableItems = false;
        if (SCore.hasExecutableItems) {
            String eiID = section.getString("executableItem", "");
            if (!eiID.isEmpty()) {
                if (ExecutableItemsAPI.isValidID(eiID)) {
                    executableItemsID = eiID;
                    hasExecutableItems = true;
                } else{
                    hasExecutableItems = false;
                    error.add("[" + ExecutableBlocks.NAME + "] Invalid ExecutableItem id (" + eiID + ") for the ExecutableBlock " + id);
                }
            }
        }
    }

    public void saveExecutableItems(ConfigurationSection section){
        if(!hasExecutableItems) section.set("executableItem", null);
        else section.set("executableItem", executableItemsID);
    }
}
