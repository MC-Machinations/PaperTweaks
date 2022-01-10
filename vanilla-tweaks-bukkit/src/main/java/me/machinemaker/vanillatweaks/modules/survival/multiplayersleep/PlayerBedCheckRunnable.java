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
package me.machinemaker.vanillatweaks.modules.survival.multiplayersleep;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

class PlayerBedCheckRunnable extends BukkitRunnable {

    private final Player player;
    private final Consumer<Player> sleepingCallback;

    PlayerBedCheckRunnable(Player player, Consumer<Player> sleepingCallback) {
        this.player = player;
        this.sleepingCallback = sleepingCallback;
    }

    @Override
    public void run() {
        if (player.getSleepTicks() >= 100) {
            sleepingCallback.accept(player);
        }
    }
}
