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
package me.machinemaker.vanillatweaks.cloud.dispatchers;

import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

/**
 * Represents a player command sender
 */
public class PlayerCommandDispatcher extends CommandDispatcher implements PersistentDataHolder {

    private final Player player;
    private final Function<Player, Audience> audienceSupplier;
    private @MonotonicNonNull Audience audience;

    public PlayerCommandDispatcher(Player player, Function<Player, Audience> audienceSupplier) {
        super(player);
        this.player = player;
        this.audienceSupplier = audienceSupplier;
    }

    @Override
    public Player sender() {
        return (Player) super.sender();
    }

    public static @NotNull Player from(@NotNull CommandContext<?> context) {
        if (context.getSender() instanceof PlayerCommandDispatcher playerCommandDispatcher) {
            return playerCommandDispatcher.sender();
        }
        throw new IllegalArgumentException("Not a PlayerCommandDispatcher");
    }

    @Override
    public @NotNull Audience audience() {
        if (this.audience == null) {
            this.audience = this.audienceSupplier.apply(this.player);
        }
        return this.audience;
    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.sender().getPersistentDataContainer();
    }

    @Override
    public @Nullable UUID getUUID() {
        return player.getUniqueId();
    }
}
