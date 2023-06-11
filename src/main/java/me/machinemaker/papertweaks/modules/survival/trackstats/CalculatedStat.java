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
package me.machinemaker.papertweaks.modules.survival.trackstats;

import java.util.Objects;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

abstract class CalculatedStat implements Translatable {

    private final String objectiveName;
    private final String displayName;

    protected CalculatedStat(final String objectiveName, final String displayName) {
        Stats.REGISTRY.put(objectiveName, this);
        this.objectiveName = objectiveName;
        this.displayName = displayName;
    }

    public final String objectiveName() {
        return this.objectiveName;
    }

    public final String displayName() {
        return this.displayName;
    }

    protected abstract int computeScore(Score score, Player player);

    public final Score getScore(final Scoreboard board, final Player player) {
        return this.getObjective(board).getScore(player.getName());
    }

    public final Objective getObjective(final Scoreboard board) {
        return Objects.requireNonNull(board.getObjective(this.objectiveName()), "Could not find objective for " + this.displayName());
    }

    public final void updateScore(final Scoreboard board, final Player player) {
        final Score score = this.getScore(board, player);
        score.setScore(this.computeScore(score, player));
    }

}
