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
package me.machinemaker.papertweaks.cloud.parsers;

import com.google.inject.Inject;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.db.dao.teleportation.homes.HomesDAO;
import me.machinemaker.papertweaks.db.model.teleportation.homes.Home;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

public class HomeParser implements ArgumentParser<CommandDispatcher, Home> {

    private final HomesDAO homesDAO;

    @Inject
    HomeParser(final HomesDAO homesDAO) {
        this.homesDAO = homesDAO;
    }

    @Override
    public ArgumentParseResult<Home> parse(final CommandContext<CommandDispatcher> commandContext, final CommandInput commandInput) {
        final String input = commandInput.readString();
        if (!(commandContext.sender() instanceof final PlayerCommandDispatcher playerDispatcher)) {
            return ArgumentParseResult.failure(new IllegalStateException("Must be player"));
        }
        final @Nullable Home home = this.homesDAO.getPlayerHome(playerDispatcher.getUUID(), input);
        if (home == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException(input + " is not a valid home"));
        }

        return ArgumentParseResult.success(home);
    }

    @Override
    public @NonNull SuggestionProvider<CommandDispatcher> suggestionProvider() {
        return (context, input) -> {
            if (context.sender() instanceof final PlayerCommandDispatcher playerCommandDispatcher) {
                return CompletableFuture.supplyAsync(() -> this.homesDAO.getHomesForPlayer(playerCommandDispatcher.getUUID()).keySet().stream().map(Suggestion::suggestion).toList());
            }
            return CompletableFuture.completedFuture(Collections.emptyList());
        };
    }
}
