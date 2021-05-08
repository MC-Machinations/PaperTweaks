/*
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
package me.machinemaker.vanillatweaks.modules.survival.graves;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;

import java.util.List;

@LecternConfiguration
class Config extends ModuleConfig {

    @Key("legacy-shift-behavior")
    @Description("Enable to use crouching on the grave to retrieve it")
    public boolean legacyShiftBehavior = false;

    @Key("grave-robbing")
    @Description("When enabled, players can open graves they don't own")
    public boolean graveRobbing = false;

    @Key("grave-locating")
    @Description("When enabled, players can see the coordinates of their last grave")
    public boolean graveLocating = true;

    @Key("xp-collection")
    @Description("When enabled, graves collect experience dropped on death")
    public boolean xpCollection = true;

    @Key("disabled-worlds")
    @Description("Worlds listed here will not create graves for players")
    public List<String> disabledWorlds = List.of("disabled_world_name");
}
