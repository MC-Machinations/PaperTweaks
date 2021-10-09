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
package me.machinemaker.vanillatweaks.utils.boards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public final class Scoreboards {

    private Scoreboards() {
    }

    public static @NotNull ScoreboardManager manager() {
        var manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            throw new IllegalStateException("the ScorebaordManager is null");
        }
        return manager;
    }

    public static @NotNull Scoreboard main() {
        return manager().getMainScoreboard();
    }

    public static @NotNull Team getTeam(String name, ChatColor color) {
        Team team = main().getTeam(name);
        if (team == null) {
            team = main().registerNewTeam(name);
        }
        team.setColor(color);
        return team;
    }

    public static @NotNull Objective getDummyObjective(@NotNull String name, @NotNull String displayName) {
        Objective objective = main().getObjective(name);
        if (objective == null) {
            objective = main().registerNewObjective(name, "dummy", displayName);
        }
        return objective;
    }
}
