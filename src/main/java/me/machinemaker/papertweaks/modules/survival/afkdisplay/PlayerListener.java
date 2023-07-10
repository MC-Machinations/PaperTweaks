/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.afkdisplay;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

class PlayerListener implements ModuleListener {

    private final AFKRunnable afkRunnable;

    @Inject
    PlayerListener(final AFKRunnable afkRunnable) {
        this.afkRunnable = afkRunnable;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (AFKDisplay.AFK_DISPLAY.has(event.getPlayer())) {
            event.getPlayer().displayName(null);
            event.getPlayer().playerListName(null);
            AFKDisplay.AFK_DISPLAY.remove(event.getPlayer());
            this.afkRunnable.addPlayer(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.afkRunnable.addPlayer(event.getPlayer());
    }
}
