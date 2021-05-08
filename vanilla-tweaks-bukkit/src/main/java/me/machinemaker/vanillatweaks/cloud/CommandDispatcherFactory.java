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
package me.machinemaker.vanillatweaks.cloud;

import com.google.inject.Inject;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory for creating implementations of {@link CommandDispatcher}.
 */
public class CommandDispatcherFactory {

    private final BukkitAudiences audiences;

    @Inject
    public CommandDispatcherFactory(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    /**
     * Create an implementation of {@link CommandDispatcher}.
     *
     * @param sender the bukkit sender
     * @return the created dispatcher
     */
    @NonNull
    public CommandDispatcher from(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return new ConsoleCommandDispatcher((ConsoleCommandSender) sender, audiences.console());
        } else if (sender instanceof Player) {
            Audience audience = audiences.player((Player) sender);
            return new PlayerCommandDispatcher((Player) sender, audience);
        }
        throw new IllegalArgumentException(sender + " is unknown");
    }
}
