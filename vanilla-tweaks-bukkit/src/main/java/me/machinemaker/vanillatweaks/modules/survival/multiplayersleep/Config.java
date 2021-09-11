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
package me.machinemaker.vanillatweaks.modules.survival.multiplayersleep;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.vanillatweaks.config.VTConfig;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

import java.util.List;

@VTConfig
class Config extends ModuleConfig {

    @Key("included-worlds")
    @Description("Worlds to count player's from")
    private List<World> includedWorlds = List.of(Bukkit.getWorlds().get(0));

    @Key("defaults.display")
    @Description("Default display for new players")
    public Settings.DisplaySetting defaultDisplaySetting = Settings.DisplaySetting.CHAT;

    @Key("boss-bar-color")
    @Description("Color of the boss bar for players who have that as their display style. Can be one of: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    public BossBar.Color bossBarColor = BossBar.Color.WHITE;

    @Key("always-reset-weather-cycle")
    @Description("When the night is skipped, always reset the weather cycle even if there is not storm presently")
    public boolean alwaysResetWeatherCycle = false;

    public List<World> worlds(boolean log) {
        for (World world : this.includedWorlds) {
            if (log && !Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
                MultiplayerSleep.LOGGER.warn("{} does not have the gamerule doDaylightCycle set to true, passing the night will have no effect there.", world.getName());
            }
        }
        if (this.includedWorlds.isEmpty()) {
            MultiplayerSleep.LOGGER.warn("You haven't enabled any worlds to be tracked by the MultiplayerSleep module");
        }
        return this.includedWorlds;
    }
}
