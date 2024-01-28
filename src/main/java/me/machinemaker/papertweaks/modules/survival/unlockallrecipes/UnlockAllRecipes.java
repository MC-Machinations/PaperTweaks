/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.unlockallrecipes;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.utils.Cacheable;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Recipe;

@ModuleInfo(name = "UnlockAllRecipes", configPath = "survival.unlock-all-recipes", description = "Unlocks all recipes for all players by default")
public class UnlockAllRecipes extends ModuleBase {

    private final Cacheable<Collection<NamespacedKey>> recipes = new Cacheable<>(() -> {
        Iterable<Recipe> iterable = Bukkit::recipeIterator;
        return StreamSupport.stream(iterable.spliterator(), false).filter(Keyed.class::isInstance).map(Keyed.class::cast).map(Keyed::getKey).collect(Collectors.toSet());
    }, 1000 * 60 * 5); // 5 minutes

    void discoverAllRecipes(final HumanEntity humanEntity) {
        humanEntity.discoverRecipes(this.recipes.get());
    }

    Cacheable<Collection<NamespacedKey>> getRecipeCache() {
        return this.recipes;
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }
}
