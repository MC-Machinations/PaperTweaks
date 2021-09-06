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
package me.machinemaker.vanillatweaks.modules.mobs.countmobdeaths;

import com.google.common.collect.Sets;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@LecternConfiguration
class Config extends ModuleConfig {

    @Key("counted-mobs")
    @Description("Any mobs listed here will be counted by the module")
    public Set<String> countedMobs = Stream.of(
            EntityType.BLAZE,
            EntityType.CAVE_SPIDER,
            EntityType.CREEPER,
            EntityType.DROWNED,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.GHAST,
            EntityType.GUARDIAN,
            EntityType.HUSK,
            EntityType.RAVAGER,
            EntityType.ILLUSIONER,
            EntityType.MAGMA_CUBE,
            EntityType.PHANTOM,
            EntityType.PILLAGER,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SKELETON_HORSE,
            EntityType.SLIME,
            EntityType.SPIDER,
            EntityType.STRAY,
            EntityType.VEX,
            EntityType.VINDICATOR,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.ZOMBIE_VILLAGER
    ).map(type -> type.getKey().toString()).collect(Collectors.toUnmodifiableSet());

    public Set<EntityType> countedMobs(boolean log) {
        Set<EntityType> types = Sets.newHashSet();
        countedMobs.forEach(mob -> {
            NamespacedKey key = NamespacedKey.fromString(mob);
            if (key == null || Registry.ENTITY_TYPE.get(key) == null) {
                if (log) {
                    CountMobDeaths.LOGGER.warn("{} did not match an existing entity", mob);
                }
            } else {
                types.add(Registry.ENTITY_TYPE.get(key));
            }
        });
        return types;
    }
}
