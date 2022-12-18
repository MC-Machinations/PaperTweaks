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
package me.machinemaker.vanillatweaks.cloud.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.db.dao.teleportation.homes.HomesDAO;
import me.machinemaker.vanillatweaks.db.model.teleportation.homes.Home;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HomeArgument extends CommandArgument<CommandDispatcher, Home> {

    @Inject
    HomeArgument(final HomesDAO homesDAO, @Assisted final boolean required, @Assisted final String name) {
        super(required, name, new Parser(homesDAO), "home", Home.class, null, RichDescription.translatable("modules.homes.commands.arguments.home"));
    }

    private static final class Parser implements ArgumentParser<CommandDispatcher, Home> {

        private final HomesDAO homesDAO;

        private Parser(final HomesDAO homesDAO) {
            this.homesDAO = homesDAO;
        }

        @Override
        public ArgumentParseResult<Home> parse(final CommandContext<CommandDispatcher> commandContext, final Queue<String> inputQueue) {
            final @Nullable String input = inputQueue.peek();
            if (input == null || input.isBlank()) {
                return ArgumentParseResult.failure(new NoInputProvidedException(Parser.class, commandContext));
            }
            if (!commandContext.getSender().isPlayer()) {
                return ArgumentParseResult.failure(new IllegalStateException("Must be player"));
            }
            final Home home = this.homesDAO.getPlayerHome(commandContext.getSender().getUUID(), input);
            if (home == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException(input + " is not a valid home"));
            }

            inputQueue.remove();
            return ArgumentParseResult.success(home);
        }

        @Override
        public List<String> suggestions(final CommandContext<CommandDispatcher> commandContext, final String input) {
            if (commandContext.getSender() instanceof PlayerCommandDispatcher playerCommandDispatcher) {
                return List.copyOf(this.homesDAO.getHomesForPlayer(playerCommandDispatcher.getUUID()).keySet());
            }
            return Collections.emptyList();
        }
    }
}
