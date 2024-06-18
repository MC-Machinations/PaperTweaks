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
package me.machinemaker.papertweaks.cloud.dispatchers;

import com.google.inject.Singleton;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Factory for creating implementations of {@link CommandDispatcher}.
 */
@Singleton
public class CommandDispatcherFactory {

    public CommandDispatcher from(final CommandSourceStack sourceStack) {
        final CommandSender sender = sourceStack.getExecutor() != null ? sourceStack.getExecutor() : sourceStack.getSender();
        return switch (sender) {
            case final ConsoleCommandSender ignored -> new ConsoleCommandDispatcher(sourceStack);
            case final Player ignored2 -> new PlayerCommandDispatcher(sourceStack);
            case final Entity ignored3 -> new EntityCommandDispatcher(sourceStack);
            default -> new FallbackCommandDispatcher(sourceStack);
        };
    }
}
