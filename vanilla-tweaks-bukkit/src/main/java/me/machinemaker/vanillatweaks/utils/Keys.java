/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.vanillatweaks.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class Keys {

    private static final String NAMESPACE = "vanillatweaks";

    private Keys() {
    }

    @SuppressWarnings("deprecation")
    public static NamespacedKey key(final String string) {
        return new NamespacedKey(NAMESPACE, string);
    }

    public static String itemTranslationKey(final Material material) {
        return "item." + material.getKey().getNamespace() + "." + material.getKey().getKey().replace('/', '.');
    }
}
