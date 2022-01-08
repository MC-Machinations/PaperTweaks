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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.machinemaker.vanillatweaks.utils.runnables.TimerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

@Singleton
class StatsRunnable extends TimerRunnable {

    private static final List<List<CalculatedStat>> PARTITIONS = Lists.partition(Lists.newArrayList(Stats.REGISTRY.values()), Stats.REGISTRY.values().size() / 3);

    private final Scoreboard board;
    private int count = 0;

    @Inject
    StatsRunnable(Plugin plugin, Scoreboard board) {
        super(plugin);
        this.board = board;
    }


    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (CalculatedStat stat : PARTITIONS.get(this.count % 3)) {
                stat.updateScore(this.board, player);
            }
        }
        this.count++;
    }
}
