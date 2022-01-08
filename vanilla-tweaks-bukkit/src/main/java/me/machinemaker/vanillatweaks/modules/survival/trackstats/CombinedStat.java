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

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

final class CombinedStat extends CalculatedStat {

    private final List<ToIntFunction<Player>> stats;

    private CombinedStat(String objectiveName, String displayName, List<ToIntFunction<Player>> stats) {
        super(objectiveName, displayName);
        this.stats = List.copyOf(stats);
    }

    @Override
    protected int computeScore(Score score, Player player) {
        int value = 0;
        for (ToIntFunction<Player> stat : this.stats) {
            value += stat.applyAsInt(player);
        }
        return value;
    }

    @Override
    public String translationKey() {
        return "modules.track-stats.stat." + this.objectiveName();
    }

    static final class Builder {

        private final String objectiveName;
        private final String displayName;
        private final List<ToIntFunction<Player>> stats = new ArrayList<>();

        Builder(String objectiveName, String displayName) {
            this.objectiveName = objectiveName;
            this.displayName = displayName;
        }

        Builder add(Statistic stat, Material material) {
            this.stats.add(player -> player.getStatistic(stat, material));
            return this;
        }

        Builder add(Statistic stat, EntityType entityType) {
            this.stats.add(player -> player.getStatistic( stat, entityType));
            return this;
        }

        Builder addMined(Material...materials) {
            for (Material material : materials) {
                this.add(Statistic.MINE_BLOCK, material);
            }
            return this;
        }

        CombinedStat build() {
            return new CombinedStat(this.objectiveName, this.displayName, this.stats);
        }
    }
}
