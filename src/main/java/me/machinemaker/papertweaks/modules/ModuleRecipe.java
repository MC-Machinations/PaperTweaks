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
package me.machinemaker.papertweaks.modules;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public record ModuleRecipe<R extends Recipe & Keyed>(R recipe) {

    @SuppressWarnings("unchecked")
    public Class<R> recipeType() {
        return (Class<R>) this.recipe.getClass();
    }

    public NamespacedKey key() {
        return this.recipe.getKey();
    }
}
