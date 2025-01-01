/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2024-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.trackrawstats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import me.machinemaker.papertweaks.utils.boards.Scoreboards;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

class ObjectiveParser<C> implements ArgumentParser<C, Tracked>, BlockingSuggestionProvider.Strings<C> {

    private static final Set<String> SUGGESTIONS = Collections.unmodifiableSet(RawStats.OBJECTIVE_DATA.keySet());

    @Override
    public ArgumentParseResult<Tracked> parse(final CommandContext<C> commandContext, final CommandInput commandInput) {
        final String input = commandInput.readString();
        if (!RawStats.OBJECTIVE_DATA.containsKey(input)) {
            return ArgumentParseResult.failure(new IllegalArgumentException(input + " does not match a valid criteria"));
        }
        final @Nullable Objective objective = Scoreboards.main().getObjective(input);
        if (objective == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException(input + " does not match a valid criteria"));
        }
        return ArgumentParseResult.success(RawStats.OBJECTIVE_DATA.get(input));
    }

    @Override
    public Iterable<String> stringSuggestions(final CommandContext<C> commandContext, final CommandInput input) {
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
}
