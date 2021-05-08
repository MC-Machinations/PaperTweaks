/*
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
package me.machinemaker.vanillatweaks.cloud.parsers;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.context.CommandContext;
import me.machinemaker.vanillatweaks.cloud.CommandDispatcher;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public class BooleanParser implements ArgumentParser<CommandDispatcher, Boolean> {

    private final BooleanArgument.BooleanParser<CommandDispatcher> cloudParser = new BooleanArgument.BooleanParser<>(false);

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandDispatcher> commandContext, @NonNull String input) {
        return this.cloudParser.suggestions(commandContext, input).stream().map(String::toLowerCase).toList();
    }

    @Override
    public @NonNull <O> ArgumentParser<CommandDispatcher, O> map(BiFunction<CommandContext<CommandDispatcher>, Boolean, ArgumentParseResult<O>> mapper) {
        return this.cloudParser.map(mapper);
    }

    @Override
    public boolean isContextFree() {
        return this.cloudParser.isContextFree();
    }

    @Override
    public int getRequestedArgumentCount() {
        return this.cloudParser.getRequestedArgumentCount();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Boolean> parse(@NonNull CommandContext<@NonNull CommandDispatcher> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        return this.cloudParser.parse(commandContext, inputQueue);
    }
}
