package com.ssomar.executableblocks.utils;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Crops {

    public static List<Material> getCrops(){
        List<Material> crops = new ArrayList<>();
        crops.add(Material.WHEAT_SEEDS);
        crops.add(Material.PUMPKIN_SEEDS);
        crops.add(Material.BEETROOT_SEEDS);
        crops.add(Material.MELON_SEEDS);
        crops.add(Material.CARROT);
        crops.add(Material.POTATO);
        return crops;
    }

    public static Material getBlockMatCrop(Material matItem){
        switch (matItem){
            case WHEAT_SEEDS:
                return Material.WHEAT;
            case PUMPKIN_SEEDS:
                return Material.PUMPKIN_STEM;
            case MELON_SEEDS:
                return Material.MELON_STEM;
            case BEETROOT_SEEDS:
                return Material.BEETROOTS;
            case POTATO:
                return Material.POTATOES;
            case CARROT:
                return Material.CARROTS;
        }
        return null;
    }
}
