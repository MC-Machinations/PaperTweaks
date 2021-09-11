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
package me.machinemaker.vanillatweaks.modules.items.wrenches;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.vanillatweaks.annotations.ConfigureModuleConfig;
import me.machinemaker.vanillatweaks.config.VTConfig;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;

@VTConfig
@ConfigureModuleConfig(folder = "wrenches")
class Config extends ModuleConfig {

    @Key("wrench.terracotta")
    public boolean terracottaWrench = true;

    @Key("wrench.redstone")
    public boolean redstoneWrench = true;

    @Key("suggest-resource-pack")
    @Description("Disable to turn off suggesting the wrench resource pack to players.")
    public boolean suggestResourcePack = true;
}
