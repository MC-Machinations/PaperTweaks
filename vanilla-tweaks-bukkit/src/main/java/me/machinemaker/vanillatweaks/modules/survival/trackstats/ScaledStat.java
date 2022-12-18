/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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

import java.util.function.IntUnaryOperator;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

final class ScaledStat extends CalculatedStat {

    private final Statistic stat;
    private final IntUnaryOperator scaleFunction;
    private @MonotonicNonNull String translationKey;

    ScaledStat(final Statistic stat, final IntUnaryOperator scaleFunction, final String displayName, final String objectiveName) {
        super(objectiveName, displayName);
        this.stat = stat;
        this.scaleFunction = scaleFunction;
    }

    @Override
    public int computeScore(final Score score, final Player player) {
        return this.scaleFunction.applyAsInt(score.getScore());
    }

    @Override
    public String translationKey() {
        if (this.translationKey == null) {
            String key = "stat.minecraft.";
            if (this.stat == Statistic.PLAY_ONE_MINUTE) {
                key += "play_time";
            } else {
                key += this.stat.getKey().getKey();
            }
            this.translationKey = key;
        }
        return this.translationKey;
    }
}
