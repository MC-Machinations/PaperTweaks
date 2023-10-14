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
package me.machinemaker.papertweaks.modules.mobs.countmobdeaths;

import java.util.LinkedHashSet;
import java.util.List;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.PTConfig;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import org.bukkit.entity.EntityType;

@PTConfig
class Config extends ModuleConfig {

    @SuppressWarnings("CollectionDeclaredAsConcreteClass") // for the config scraper
    @Key("counted-mobs")
    @Description("Any mobs listed here will be counted by the module")
    // warden, giant, piglin, piglin brute, zoglin are missing from the defaults for some reason in the VT datapack
    public LinkedHashSet<EntityType> countedMobs = new LinkedHashSet<>(List.of(
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
    ));
}
