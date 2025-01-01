/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.trackstats;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import me.machinemaker.papertweaks.utils.runnables.TimerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

@Singleton
class StatsRunnable extends TimerRunnable {

    private static final List<List<CalculatedStat>> PARTITIONS = Lists.partition(new ArrayList<>(Stats.REGISTRY.values()), Stats.REGISTRY.values().size() / 3);

    private final Scoreboard board;
    private int count = 0;

    @Inject
    StatsRunnable(final Plugin plugin, final Scoreboard board) {
        super(plugin);
        this.board = board;
    }


    @Override
    public void run() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            for (final CalculatedStat stat : PARTITIONS.get(this.count % 3)) {
                stat.updateScore(this.board, player);
            }
        }
        this.count++;
    }
}
