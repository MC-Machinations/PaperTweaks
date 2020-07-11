package me.machinemaker.vanillatweaks.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

public class Tags {
    public static final Tag<Material> REDSTONE_COMPONENTS = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.REPEATER,
            Material.COMPARATOR,
            Material.OBSERVER,
            Material.DISPENSER,
            Material.DROPPER,
            Material.HOPPER,
            Material.STICKY_PISTON,
            Material.PISTON
    );

    public static final Tag<Material> GLAZED_TERRACOTTA = new MaterialSetTag(NamespacedKey.randomKey(),
            Material.WHITE_GLAZED_TERRACOTTA,
            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.YELLOW_GLAZED_TERRACOTTA,
            Material.LIME_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA,
            Material.GRAY_GLAZED_TERRACOTTA,
            Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Material.CYAN_GLAZED_TERRACOTTA,
            Material.PURPLE_GLAZED_TERRACOTTA,
            Material.BLUE_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA,
            Material.GREEN_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA,
            Material.BLACK_GLAZED_TERRACOTTA
    );
}
