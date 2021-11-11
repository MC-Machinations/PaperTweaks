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
package me.machinemaker.vanillatweaks.cloud.dispatchers;

import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class for representing command senders.
 */
public abstract class CommandDispatcher implements ForwardingAudience.Single {

    private final CommandSender bukkitCommandSender;

    protected CommandDispatcher(CommandSender bukkitCommandSender) {
        this.bukkitCommandSender = bukkitCommandSender;
    }

    /**
     * The Bukkit command sender.
     *
     * @return the Bukkit command sender
     */
    public CommandSender sender() {
        return bukkitCommandSender;
    }

    public boolean isPlayer() {
        return this instanceof PlayerCommandDispatcher;
    }

    public abstract @Nullable UUID getUUID();

    public boolean hasPermission(@NonNull String permission) {
        return this.bukkitCommandSender.hasPermission(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandDispatcher that = (CommandDispatcher) o;
        return bukkitCommandSender.equals(that.bukkitCommandSender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bukkitCommandSender);
    }
}
