/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.modules.survival.unlockallrecipes;

import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.utils.Cacheable;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ModuleInfo(name = "UnlockAllRecipes", configPath = "survival.unlock-all-recipes", description = "Unlocks all recipes for all players by default")
public class UnlockAllRecipes extends ModuleBase {

    private final Cacheable<Collection<NamespacedKey>> recipes = new Cacheable<>(() -> {
        Iterable<Recipe> iterable = Bukkit::recipeIterator;
        return StreamSupport.stream(iterable.spliterator(), false).filter(Keyed.class::isInstance).map(Keyed.class::cast).map(Keyed::getKey).collect(Collectors.toSet());
    }, 1000 * 60 * 5); // 5 minutes

    void discoverAllRecipes(HumanEntity humanEntity) {
        humanEntity.discoverRecipes(recipes.get());
    }

    Cacheable<Collection<NamespacedKey>> getRecipeCache() {
        return this.recipes;
    }

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }
}
