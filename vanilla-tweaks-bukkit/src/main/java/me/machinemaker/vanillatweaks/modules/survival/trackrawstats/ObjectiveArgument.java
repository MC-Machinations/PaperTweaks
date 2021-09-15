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
package me.machinemaker.vanillatweaks.modules.survival.trackrawstats;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class ObjectiveArgument extends CommandArgument<CommandDispatcher, Tracked> {

    private ObjectiveArgument(@NonNull String name) {
        super(true, name, new Parser(), "", Tracked.class, null, RichDescription.translatable("modules.track-raw-stats.commands.arguments.objective"));
    }

    private static class Parser implements ArgumentParser<CommandDispatcher, Tracked> {

        private static final Set<String> SUGGESTIONS = Collections.unmodifiableSet(RawStats.OBJECTIVE_DATA.keySet());

        @Override
        public @NonNull ArgumentParseResult<@NonNull Tracked> parse(@NonNull CommandContext<@NonNull CommandDispatcher> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (!RawStats.OBJECTIVE_DATA.containsKey(input)) {
                return ArgumentParseResult.failure(new IllegalArgumentException(input + " does not match a valid criteria"));
            }
            Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(input);
            if (objective == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException(input + " does not match a valid criteria"));
            }
            inputQueue.remove();
            return ArgumentParseResult.success(RawStats.OBJECTIVE_DATA.get(input));
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandDispatcher> commandContext, @NonNull String input) {
            Objective currentObjective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(DisplaySlot.SIDEBAR);
            Set<String> suggestions;
            if (currentObjective != null && SUGGESTIONS.contains(currentObjective.getName())) {
                suggestions = new LinkedHashSet<>(SUGGESTIONS);
                suggestions.remove(currentObjective.getName());
            } else {
                suggestions = SUGGESTIONS;
            }
            return new ArrayList<>(suggestions);
        }

        @Override
        public boolean isContextFree() {
            return true;
        }
    }

    static @NonNull ObjectiveArgument of(@NonNull String name) {
        return new ObjectiveArgument(name);
    }
}
