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

import com.google.common.cache.CacheLoader;
import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Factory for creating implementations of {@link CommandDispatcher}.
 */
// TODO native audience
@Singleton
public class CommandDispatcherFactory extends CacheLoader<CommandSender, CommandDispatcher> {

    public CommandDispatcher from(final CommandSender sender) {
        if (sender instanceof final ConsoleCommandSender consoleCommandSender) {
            return new ConsoleCommandDispatcher(consoleCommandSender, consoleCommandSender);
        } else if (sender instanceof final Player player) {
            return new PlayerCommandDispatcher(player, ignored -> player);
        }
        throw new IllegalArgumentException(sender + " is unknown");
    }

    @Override
    public CommandDispatcher load(final CommandSender key) {
        return this.from(key);
    }
}
