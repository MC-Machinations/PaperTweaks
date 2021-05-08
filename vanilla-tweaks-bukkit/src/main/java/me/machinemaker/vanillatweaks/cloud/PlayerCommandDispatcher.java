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

import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player command sender
 */
public class PlayerCommandDispatcher extends CommandDispatcher implements PersistentDataHolder {

    private final Player player;

    public PlayerCommandDispatcher(Player player, Audience audience) {
        super(player, audience);
        this.player = player;
    }

    @Override
    public Player sender() {
        return player;
    }

    public static @NotNull Player from(@NotNull CommandContext<?> context) {
        if (context.getSender() instanceof PlayerCommandDispatcher playerCommandDispatcher) {
            return playerCommandDispatcher.sender();
        }
        throw new IllegalArgumentException("Not a PlayerCommandDispatcher");
    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.sender().getPersistentDataContainer();
    }
}
