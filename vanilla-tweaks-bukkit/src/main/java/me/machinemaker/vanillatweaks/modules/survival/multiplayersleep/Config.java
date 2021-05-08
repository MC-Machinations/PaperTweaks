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
package me.machinemaker.vanillatweaks.modules.survival.multiplayersleep;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.*;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@LecternConfiguration
class Config extends ModuleConfig {

    @Inject private static BukkitAudiences audiences;

    @Key("sleep-percentage")
    @Description("Percentage of eligible players required to sleep (UNUSED, use the vanilla gamerule)")
    public Double sleepPercentage = 0.5;

    @Key("included-worlds")
    @Description("Worlds to count player's from")
    public List<String> includedWorlds = Lists.newArrayList("world");

    @Key("defaults.display")
    @Description("Default display for new players")
    public Settings.DisplaySetting defaultDisplaySetting = Settings.DisplaySetting.CHAT;

    @Key("boss-bar-color")
    @Description("Color of the boss bar for players who have that as their display style. Can be one of: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    public BossBar.Color bossBarColor = BossBar.Color.WHITE;

    @Key("always-reset-weather-cycle")
    @Description("When the night is skipped, always reset the weather cycle even if there is not storm presently")
    public boolean alwaysResetWeatherCycle = false;

    public List<World> worlds(boolean firstLoad) {
        List<World> worlds = Lists.newArrayList();
        includedWorlds.forEach(levelName -> {
            World world = Bukkit.getWorld(levelName);
            if (world == null) {
                audiences.console().sendMessage(translatable("modules.multiplayer-sleep.failure.level-name", NamedTextColor.RED, text(levelName, NamedTextColor.GOLD)));
                if (levelName.equals("world")) {
                    audiences.console().sendMessage(translatable("modules.multiplayer-sleep.failure.level-name.world", NamedTextColor.RED));
                }
                return;
            }
            if (firstLoad && !Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
                audiences.console().sendMessage(translatable("modules.multiplayer-sleep.failure.no-daylight-cycle", text(world.getName())));
            }
            worlds.add(world);
        });
        if (worlds.isEmpty()) {
            audiences.console().sendMessage(translatable("modules.multiplayer-sleep.failure.zero-worlds", NamedTextColor.YELLOW));
        }
        return worlds;
    }
}
