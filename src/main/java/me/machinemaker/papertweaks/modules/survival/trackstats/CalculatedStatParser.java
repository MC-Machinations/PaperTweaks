/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

class CalculatedStatParser<C> implements ArgumentParser<C, CalculatedStat>, BlockingSuggestionProvider.Strings<C> {

    @Override
    public ArgumentParseResult<CalculatedStat> parse(final CommandContext<C> commandContext, final CommandInput commandInput) {
        final String input = commandInput.readString();
        final @Nullable CalculatedStat stat = Stats.REGISTRY.get(input);
        if (stat == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException(input + " is not a valid stat"));
        } else {
            return ArgumentParseResult.success(stat);
        }
    }

    @Override
    public Iterable<String> stringSuggestions(final CommandContext<C> commandContext, final CommandInput input) {
        return Stats.REGISTRY.keySet();
    }
}
