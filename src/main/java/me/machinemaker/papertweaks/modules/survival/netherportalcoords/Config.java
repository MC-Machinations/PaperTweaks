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
package me.machinemaker.papertweaks.modules.survival.netherportalcoords;

import com.google.common.collect.Lists;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.VTConfig;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@VTConfig
class Config extends ModuleConfig {

    @Key("overworld-type-worlds")
    @Description("World's listed here will qualify as overworlds for purposes of calculating coordinates")
    public List<String> overWorlds = Lists.newArrayList("world");

    @Key("nether-type-worlds")
    @Description("World's listed here will qualify as nethers for purposes of calculating coordinates")
    public List<String> netherWorlds = Lists.newArrayList("world_nether");

    void validateWorlds() {
        overWorlds.forEach(s -> this.validateWorld(s, true));
        netherWorlds.forEach(s -> this.validateWorld(s, true));
    }

    private World validateWorld(String s, boolean log) {
        World world = Bukkit.getWorld(s);
        if (world == null) {
            if (log) NetherPortalCoords.LOGGER.warn("{} did not match an existing world", s);
            return null;
        }
        return world;
    }

    Set<World> overWorlds() {
        return this.overWorlds.stream().map(s -> this.validateWorld(s, false)).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }

    Set<World> netherWorlds() {
        return this.netherWorlds.stream().map(s -> this.validateWorld(s, false)).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }

}
