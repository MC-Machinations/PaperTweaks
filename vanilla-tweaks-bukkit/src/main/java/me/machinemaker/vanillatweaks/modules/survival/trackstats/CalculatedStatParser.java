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

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Queue;

public class CalculatedStatParser implements ArgumentParser<CommandDispatcher, CalculatedStat> {

    @Override
    public ArgumentParseResult<CalculatedStat> parse(CommandContext<CommandDispatcher> commandContext, Queue<String> inputQueue) {
        final @Nullable String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(CalculatedStatParser.class, commandContext));
        }
        final @Nullable CalculatedStat stat = Stats.REGISTRY.get(input);
        if (stat == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException(input + " is not a valid stat"));
        } else {
            inputQueue.remove();
            return ArgumentParseResult.success(stat);
        }
    }

    @Override
    public List<String> suggestions(CommandContext<CommandDispatcher> commandContext, String input) {
        return List.copyOf(Stats.REGISTRY.keySet());
    }
}
