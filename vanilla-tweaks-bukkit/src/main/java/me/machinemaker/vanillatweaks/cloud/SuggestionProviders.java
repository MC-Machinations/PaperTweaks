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
package me.machinemaker.vanillatweaks.cloud;

import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public final class SuggestionProviders {

    private SuggestionProviders() {
    }

    private static final BiFunction<?, String, List<String>> EMPTY = (c, s) -> Collections.emptyList();
    private static final PlayerArgument.PlayerParser<CommandDispatcher> DUMMY_PLAYER_PARSER = new PlayerArgument.PlayerParser<>();

    @SuppressWarnings("unchecked")
    public static <C> BiFunction<C, String, List<String>> empty() {
        return (BiFunction<C, String, List<String>>) EMPTY;
    }

    public static BiFunction<CommandContext<CommandDispatcher>, String, List<String>> playersWithoutSelf() {
        return (context, s) -> {
            if (context.getSender().sender() instanceof Player player) {
                return DUMMY_PLAYER_PARSER.suggestions(context, s).stream().filter(name -> !name.equals(player.getName())).toList();
            }
            return DUMMY_PLAYER_PARSER.suggestions(context, s);
        };
    }
}
