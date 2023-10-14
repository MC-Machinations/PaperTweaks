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
package me.machinemaker.papertweaks.modules.survival.coordinateshud;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.PTConfig;
import me.machinemaker.papertweaks.modules.ModuleConfig;

@PTConfig
class Config extends ModuleConfig {

    @Key("hud-update-time-in-ticks")
    @Description("Time in ticks between updates of the HUD in the player's action bar")
    public long ticks = 2L;

    @Key("enabled-by-default")
    @Description("Enabling this will set the player's HUD to be on when they join the game for the first time")
    public boolean enabledByDefault = false;
}
