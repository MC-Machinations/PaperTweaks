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
package me.machinemaker.vanillatweaks.modules.teleportation.homes;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.YamlConfig;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.lectern.annotations.validations.numbers.Positive;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;

@YamlConfig
class Config extends ModuleConfig {

    @Positive
    @Key("default-sethome-limit")
    @Description("The maximum number of homes allowed per player")
    int defaultSetHomeLimit = 5;

    @Key("allow-across-dimension")
    @Description("Allow teleporting to homes across dimensions")
    boolean allowAcrossDimension = true;

    @Min(0)
    @Key("home-command-cooldown-seconds")
    @Description("The cooldown in seconds for using the /home command")
    long sethomeCooldown = 0L;

    @Min(0)
    @Key("home-command-delay-seconds")
    @Description("The delay in seconds after using the /home command before teleportation")
    long sethomeDelay = 0L;
}
