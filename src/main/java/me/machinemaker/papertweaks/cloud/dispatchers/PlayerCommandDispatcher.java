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

import cloud.commandframework.context.CommandContext;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a player command sender
 */
public class PlayerCommandDispatcher extends CommandDispatcher implements PersistentDataHolder {

    private final Player player;
    private final Function<Player, Audience> audienceSupplier;
    private @MonotonicNonNull Audience audience;

    public PlayerCommandDispatcher(final Player player, final Function<Player, Audience> audienceSupplier) {
        super(player);
        this.player = player;
        this.audienceSupplier = audienceSupplier;
    }

    public static Player from(final CommandContext<?> context) {
        if (context.getSender() instanceof final PlayerCommandDispatcher playerCommandDispatcher) {
            return playerCommandDispatcher.sender();
        }
        throw new IllegalArgumentException("Not a PlayerCommandDispatcher");
    }

    @Override
    public Player sender() {
        return (Player) super.sender();
    }

    @Override
    public Audience audience() {
        if (this.audience == null) {
            this.audience = this.audienceSupplier.apply(this.player);
        }
        return this.audience;
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.sender().getPersistentDataContainer();
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.player.getUniqueId();
    }

    @Override
    public Locale locale() {
        return this.player.locale();
    }
}
