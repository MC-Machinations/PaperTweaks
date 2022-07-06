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
package me.machinemaker.vanillatweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class PlayerListener implements ModuleListener {

    private final HUDRunnable hudRunnable;
    private final Config config;

    @Inject
    PlayerListener(final HUDRunnable hudRunnable, final Config config) {
        this.hudRunnable = hudRunnable;
        this.config = config;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!HUDRunnable.COORDINATES_HUD_KEY.has(event.getPlayer())) {
            HUDRunnable.COORDINATES_HUD_KEY.setTo(event.getPlayer(), this.config.enabledByDefault);
        }
        if (Boolean.TRUE.equals(HUDRunnable.COORDINATES_HUD_KEY.getFrom(event.getPlayer()))) {
            this.hudRunnable.addPlayer(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(final PlayerQuitEvent event) {
        this.hudRunnable.removePlayer(event.getPlayer());
    }
}
