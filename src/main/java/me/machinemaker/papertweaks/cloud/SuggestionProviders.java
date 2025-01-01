/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.cloud;

import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

public final class SuggestionProviders {

    private static final BiFunction<?, String, List<String>> EMPTY = (c, s) -> Collections.emptyList();
    private static final PlayerParser<CommandDispatcher> DUMMY_PLAYER_PARSER = new PlayerParser<>();

    private SuggestionProviders() {
    }

    @SuppressWarnings("unchecked")
    public static <C> BiFunction<C, String, List<String>> empty() {
        return (BiFunction<C, String, List<String>>) EMPTY;
    }

    public static BlockingSuggestionProvider<CommandDispatcher> playersWithoutSelf() {
        return (context, input) -> {
            if (context.sender().sender() instanceof final Player player) {
                return Iterables.filter(DUMMY_PLAYER_PARSER.suggestions(context, input), name -> !name.suggestion().equals(player.getName()));
            }
            return DUMMY_PLAYER_PARSER.suggestions(context, input);
        };
    }
}
