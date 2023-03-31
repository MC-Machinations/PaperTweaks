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
package me.machinemaker.vanillatweaks.modules.survival.trackrawstats;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.utils.boards.Scoreboards;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class ObjectiveArgument extends CommandArgument<CommandDispatcher, Tracked> {

    private ObjectiveArgument(final String name) {
        super(true, name, new Parser(), "", Tracked.class, null, RichDescription.translatable("modules.track-raw-stats.commands.arguments.objective"));
    }

    static ObjectiveArgument of(final String name) {
        return new ObjectiveArgument(name);
    }

    private static class Parser implements ArgumentParser<CommandDispatcher, Tracked> {

        private static final Set<String> SUGGESTIONS = Collections.unmodifiableSet(RawStats.OBJECTIVE_DATA.keySet());

        @Override
        public ArgumentParseResult<Tracked> parse(final CommandContext<CommandDispatcher> commandContext, final Queue<String> inputQueue) {
            final @Nullable String input = inputQueue.peek();
            if (!RawStats.OBJECTIVE_DATA.containsKey(input) || input == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException(input + " does not match a valid criteria"));
            }
            final @Nullable Objective objective = Scoreboards.main().getObjective(input);
            if (objective == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException(input + " does not match a valid criteria"));
            }
            inputQueue.remove();
            return ArgumentParseResult.success(RawStats.OBJECTIVE_DATA.get(input));
        }

        @Override
        public List<String> suggestions(final CommandContext<CommandDispatcher> commandContext, final String input) {
            final @Nullable Objective currentObjective = Scoreboards.main().getObjective(DisplaySlot.SIDEBAR);
            final Set<String> suggestions;
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
}
