/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2022-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.utilities.spawningspheres;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Callables;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import me.machinemaker.mirror.Mirror;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.Nullable;

class PaperDespawnDistances implements DespawnDistances {

    private static final Function<World, Callable<Integer>> HARD_DESPAWN_DISTANCE_FUNCTION;

    static {
        Function<World, Callable<Integer>> hardDespawnDistanceFunction = world -> Callables.returning(128);
        try {
            final @Nullable Class<?> paperWorldConfigClass = Mirror.maybeGetClass("io.papermc.paper.configuration.WorldConfiguration");
            if (paperWorldConfigClass != null) {
                hardDespawnDistanceFunction = new PerCategoryDespawnDistances();
            } else {
                throw new IllegalArgumentException("Old version");
            }
        } catch (final IllegalArgumentException exception) {
            SpawningSpheres.LOGGER.warn("Paper environment detected, but could not hook into any custom spawning ranges. This might be a bug", exception);
        }
        HARD_DESPAWN_DISTANCE_FUNCTION = hardDespawnDistanceFunction;
    }

    private final Cache<NamespacedKey, Integer> hardDespawnCache;

    PaperDespawnDistances() {
        this.hardDespawnCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    }

    @Override
    public int soft(final World world) {
        return 24;
    }

    @Override
    public int hard(final World world) {
        try {
            return this.hardDespawnCache.get(world.getKey(), HARD_DESPAWN_DISTANCE_FUNCTION.apply(world));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Error getting hard despawn distance for " + world.getKey(), e);
        }
    }

    // WorldConfiguration.java per-category despawn distances
    static class PerCategoryDespawnDistances implements Function<World, Callable<Integer>> {

        static final String PATH = "entities.spawning.despawn-ranges.monster.hard";

        @Override
        public Callable<Integer> apply(final World world) {
            final @Nullable ConfigurationSection worlds = Bukkit.getServer().spigot().getPaperConfig().getConfigurationSection("__________WORLDS__________");
            if (worlds != null) {
                return Callables.returning(worlds.getInt(world.getName() + "." + PATH, worlds.getInt("__defaults__." + PATH, 128)));
            }
            return Callables.returning(128); // fallback to default
        }
    }
}
