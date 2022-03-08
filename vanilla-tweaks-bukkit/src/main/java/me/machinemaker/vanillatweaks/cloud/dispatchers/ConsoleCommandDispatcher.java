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
package me.machinemaker.vanillatweaks.cloud.dispatchers;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents the console command sender
 */
public class ConsoleCommandDispatcher extends CommandDispatcher {

    private static final UUID CONSOLE_UUID = UUID.randomUUID(); // For tracking cooldowns

    private final Audience audience;

    ConsoleCommandDispatcher(ConsoleCommandSender console, Audience audience) {
        super(console);
        this.audience = audience;
    }

    @Override
    public ConsoleCommandSender sender() {
        return (ConsoleCommandSender) super.sender();
    }

    @Override
    public @NotNull Audience audience() {
        return this.audience;
    }

    @Override
    public @Nullable UUID getUUID() {
        return CONSOLE_UUID;
    }
}
