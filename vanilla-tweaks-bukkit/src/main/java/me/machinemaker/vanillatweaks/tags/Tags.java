/*
 * GNU General Public License v3
 *
 * VanillaTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
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
package me.machinemaker.vanillatweaks.tags;

import me.machinemaker.vanillatweaks.utils.Keys;
import me.machinemaker.vanillatweaks.tags.types.MaterialTag;
import org.bukkit.Material;

public final class Tags {

    private Tags() {
    }

    public static final MaterialTag DURABILITY = new MaterialTag(Keys.key("durability_items"))
            .add(material -> material.getMaxDurability() > 0);

    public static final MaterialTag DAMAGEABLE_TOOLS = new MaterialTag(Keys.key("damageable_tools"))
            .endsWith("_SWORD")
            .endsWith("_SHOVEL")
            .endsWith("_PICKAXE")
            .endsWith("_AXE")
            .endsWith("_HOE")
            .endsWith("_ON_A_STICK")
            .add(Material.FLINT_AND_STEEL)
            .endsWith("BOW")
            .add(Material.SHEARS)
            .add(Material.FISHING_ROD)
            .add(Material.TRIDENT);

    public static final MaterialTag DAMAGEABLE_ARMOR = new MaterialTag(Keys.key("damageable_armor"))
            .add(DURABILITY)
            .not(DAMAGEABLE_TOOLS);

    public static final MaterialTag REDSTONE_COMPONENTS = new MaterialTag(Keys.key("redstone_components"))
            .add(Material.REPEATER)
            .add(Material.COMPARATOR)
            .add(Material.OBSERVER)
            .add(Material.DISPENSER)
            .add(Material.DROPPER)
            .add(Material.HOPPER)
            .add(Material.STICKY_PISTON)
            .add(Material.PISTON);
    public static final MaterialTag GLAZED_TERRACOTTA = new MaterialTag(Keys.key("glazed_terracotta"))
            .endsWith("_GLAZED_TERRACOTTA").ensureSize("glazed terracotta", 16);

    public static final MaterialTag CHESTPLATES = new MaterialTag(Keys.key("chestplates"))
            .endsWith("_CHESTPLATE").ensureSize("chestplates", 6);
}
