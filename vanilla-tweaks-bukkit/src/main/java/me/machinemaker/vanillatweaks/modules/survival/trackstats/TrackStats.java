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
package me.machinemaker.vanillatweaks.modules.survival.trackstats;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "TrackStats", configPath = "survival.track-stats", description = "Adds several pre-processed stats")
public class TrackStats extends ModuleBase {

    final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

    TrackStats() {
        for (Stat stat : Stat.values()) {
            if (board.getObjective(stat.objName()) == null) {
                board.registerNewObjective(stat.objName(), "dummy", stat.displayName());
            }
        }
    }

    @Override
    protected void configure() {
        super.configure();
        bind(Scoreboard.class).toInstance(board);
        // bind(StatsRunnable.class);
    }

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
