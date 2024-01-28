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
package me.machinemaker.papertweaks.modules.survival.multiplayersleep;

import com.google.inject.Inject;
import java.util.UUID;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

class PlayerListener implements ModuleListener {

    private final Config config;

    @Inject
    PlayerListener(final Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final SleepContext context = MultiplayerSleep.SLEEP_CONTEXT_MAP.get(event.getPlayer().getWorld().getUID());
        if (context != null) {
            context.removePlayer(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        this.config.worlds(false).forEach(world -> {
            final @Nullable SleepContext context = MultiplayerSleep.SLEEP_CONTEXT_MAP.computeIfAbsent(world.getUID(), uuid -> SleepContext.from(Bukkit.getWorld(uuid)));
            if (context != null) {
                context.startSleeping(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBedLeave(final PlayerBedLeaveEvent event) {
        final UUID uuid = event.getPlayer().getWorld().getUID();
        if (MultiplayerSleep.SLEEP_CONTEXT_MAP.containsKey(uuid)) {
            MultiplayerSleep.SLEEP_CONTEXT_MAP.get(uuid).removePlayer(event.getPlayer());
        }
    }
}
