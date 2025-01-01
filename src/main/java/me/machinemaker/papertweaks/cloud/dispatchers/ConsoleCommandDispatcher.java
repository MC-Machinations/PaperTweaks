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
package me.machinemaker.papertweaks.cloud.dispatchers;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents the console command sender
 */
public class ConsoleCommandDispatcher extends CommandDispatcher {

    private static final UUID CONSOLE_UUID = UUID.randomUUID(); // For tracking cooldowns

    ConsoleCommandDispatcher(final CommandSourceStack sourceStack) {
        super(sourceStack);
    }

    @Override
    public ConsoleCommandSender sender() {
        return (ConsoleCommandSender) super.sender();
    }

    @Override
    public @Nullable UUID getUUID() {
        return CONSOLE_UUID;
    }

    @Override
    public Locale locale() {
        return Locale.US;
    }
}
