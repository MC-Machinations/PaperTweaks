/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2022-2023 Machine_Maker
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

final class CombinedStat extends CalculatedStat {

    private final List<ToIntFunction<Player>> stats;

    private CombinedStat(final String objectiveName, final String displayName, final List<ToIntFunction<Player>> stats) {
        super(objectiveName, displayName);
        this.stats = List.copyOf(stats);
    }

    @Override
    protected int computeScore(final Score score, final Player player) {
        int value = 0;
        for (final ToIntFunction<Player> stat : this.stats) {
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

        Builder(final String objectiveName, final String displayName) {
            this.objectiveName = objectiveName;
            this.displayName = displayName;
        }

        Builder add(final Statistic stat, final Material material) {
            this.stats.add(player -> player.getStatistic(stat, material));
            return this;
        }

        Builder add(final Statistic stat, final EntityType entityType) {
            this.stats.add(player -> player.getStatistic(stat, entityType));
            return this;
        }

        Builder addMined(final Material... materials) {
            for (final Material material : materials) {
                this.add(Statistic.MINE_BLOCK, material);
            }
            return this;
        }

        CombinedStat build() {
            return new CombinedStat(this.objectiveName, this.displayName, this.stats);
        }
    }
}
