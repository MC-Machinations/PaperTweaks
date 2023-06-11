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
package me.machinemaker.papertweaks.modules.teleportation.spawn;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.VTConfig;
import me.machinemaker.papertweaks.modules.ModuleConfig;

@VTConfig
class Config extends ModuleConfig {

    @Key("spawn-cooldown")
    @Description("Time in seconds between using /spawn")
    public long cooldown = 0;

    @Key("delay")
    @Description("Delay in seconds after using /spawn before teleportation occurs")
    public long delay = 0;

    @Key("defaults-to-main-world")
    @Description("If set, running the /spawn command without any argument will go to the spawnpoint of the main world")
    public boolean defaultsToMainWorld = false;
}
