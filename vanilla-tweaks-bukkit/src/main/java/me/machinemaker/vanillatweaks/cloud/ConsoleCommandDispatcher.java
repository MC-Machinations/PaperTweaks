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

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Represents the console command sender
 */
public class ConsoleCommandDispatcher extends CommandDispatcher {

    private final ConsoleCommandSender console;

    ConsoleCommandDispatcher(ConsoleCommandSender console, Audience audience) {
        super(console, audience);
        this.console = console;
    }

    @Override
    public ConsoleCommandSender sender() {
        return console;
    }
}
