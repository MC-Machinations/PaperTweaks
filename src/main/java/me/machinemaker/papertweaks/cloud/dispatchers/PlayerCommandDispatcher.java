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
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.incendo.cloud.context.CommandContext;

/**
 * Represents a player command sender
 */
public class PlayerCommandDispatcher extends CommandDispatcher implements PersistentDataHolder {

    public PlayerCommandDispatcher(final CommandSourceStack player) {
        super(player);
    }

    public static Player from(final CommandContext<?> context) {
        if (context.sender() instanceof final PlayerCommandDispatcher playerCommandDispatcher) {
            return playerCommandDispatcher.sender();
        }
        throw new IllegalArgumentException("Not a PlayerCommandDispatcher");
    }

    @Override
    public Player sender() {
        return (Player) super.sender();
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.sender().getPersistentDataContainer();
    }

    @Override
    public UUID getUUID() {
        return this.sender().getUniqueId();
    }

    @Override
    public Locale locale() {
        return this.sender().locale();
    }
}
