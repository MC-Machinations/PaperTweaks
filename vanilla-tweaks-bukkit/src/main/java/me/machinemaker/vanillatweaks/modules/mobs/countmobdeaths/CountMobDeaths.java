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
package me.machinemaker.vanillatweaks.modules.mobs.countmobdeaths;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Set;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.utils.PlayerMapFactory;
import me.machinemaker.vanillatweaks.utils.boards.Scoreboards;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

@ModuleInfo(name = "CountMobDeaths", configPath = "mobs.count-mob-deaths", description = "Toggleable scoreboard for counting mob deaths")
public class CountMobDeaths extends ModuleBase {

    static final String DEATH_COUNT_OBJECTIVE = "mobDeathCount";

    final PlayerMapFactory.PlayerMap<CountingBoard> scoreboardPlayerMap;

    @Inject
    public CountMobDeaths(final PlayerMapFactory factory) {
        this.scoreboardPlayerMap = factory.concurrent(PlayerMapFactory.Key.of("mdc_scoreboard", CountingBoard.class));
    }

    CountingBoard getOrCreateBoard(final Player player) {
        return this.scoreboardPlayerMap.computeIfAbsent(player, p -> {
            final Scoreboard scoreboard = Scoreboards.manager().getNewScoreboard();
            final Objective objective = scoreboard.registerNewObjective(DEATH_COUNT_OBJECTIVE, Criteria.DUMMY, ChatColor.GOLD + "No. Mob Deaths");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            return new CountingBoard(scoreboard);
        });
    }

    Objective getDeathCountObjective(final Scoreboard board) {
        final @Nullable Objective objective = board.getObjective(DEATH_COUNT_OBJECTIVE);
        if (objective == null) {
            throw new IllegalArgumentException(board + " does not contain the required objective");
        }
        return objective;
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(EntityListener.class);
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }

    static final class CountingBoard {

        private final Scoreboard scoreboard;
        private boolean counting;

        CountingBoard(final Scoreboard scoreboard) {
            this.scoreboard = scoreboard;
        }

        public Scoreboard scoreboard() {
            return this.scoreboard;
        }

        public boolean isCounting() {
            return this.counting;
        }

        public void setCounting(final boolean counting) {
            this.counting = counting;
        }
    }
}
