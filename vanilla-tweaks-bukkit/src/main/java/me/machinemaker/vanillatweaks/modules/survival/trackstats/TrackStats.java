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
package me.machinemaker.vanillatweaks.modules.survival.trackstats;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Scoreboard;

@ModuleInfo(name = "TrackStats", configPath = "survival.track-stats", description = "Adds several pre-processed stats")
public class TrackStats extends ModuleBase {

    final Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager(), "null ScoreboardManager").getMainScoreboard();

    TrackStats() {
        for (final CalculatedStat stat : Stats.REGISTRY.values()) {
            if (this.board.getObjective(stat.objectiveName()) == null) {
                this.board.registerNewObjective(stat.objectiveName(), Criteria.DUMMY, stat.displayName());
            }
        }
    }

    @Override
    protected void configure() {
        super.configure();
        this.bind(Scoreboard.class).toInstance(this.board);
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
