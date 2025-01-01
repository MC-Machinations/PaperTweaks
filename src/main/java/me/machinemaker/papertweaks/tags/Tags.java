/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.papertweaks.tags;

import me.machinemaker.papertweaks.utils.Keys;
import org.bukkit.Material;

public final class Tags {

    private Tags() {
    }

    public static final MaterialTag DURABILITY = material("durability_items").add(m -> m.getMaxDurability() > 0).build();

    public static final MaterialTag DAMAGEABLE_TOOLS = material("damageable_tools")
            .endsWith("_SWORD")
            .endsWith("_SHOVEL")
            .endsWith("_PICKAXE")
            .endsWith("_AXE")
            .endsWith("_HOE")
            .endsWith("_ON_A_STICK")
            .endsWith("BOW")
            .add(Material.FLINT_AND_STEEL, Material.SHEARS, Material.FISHING_ROD, Material.TRIDENT)
            .build();

    public static final MaterialTag DAMAGEABLE_ARMOR = material("damageable_armor")
            .add(DURABILITY)
            .remove(DAMAGEABLE_TOOLS)
            .build();

    public static final MaterialTag REDSTONE_COMPONENTS = material("redstone_components")
            .add(Material.REPEATER)
            .add(Material.COMPARATOR)
            .add(Material.OBSERVER)
            .add(Material.DISPENSER)
            .add(Material.DROPPER)
            .add(Material.HOPPER)
            .add(Material.STICKY_PISTON)
            .add(Material.PISTON)
            .build();

    public static final MaterialTag GLAZED_TERRACOTTA = material("glazed_terracotta")
            .endsWith("_GLAZED_TERRACOTTA").verify(16).build();

    public static final MaterialTag CHESTPLATES = material("chestplates")
            .endsWith("_CHESTPLATE").verify(6).build();

    private static MaterialTag.Builder material(String name) {
        return MaterialTag.builder(Keys.legacyKey(name));
    }
}
