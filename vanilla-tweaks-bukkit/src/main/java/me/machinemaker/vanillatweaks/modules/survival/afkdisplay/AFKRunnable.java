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
package me.machinemaker.vanillatweaks.modules.survival.afkdisplay;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@Singleton
class AFKRunnable implements Runnable {

    private final Map<UUID, LocationTime> locationMap = Maps.newConcurrentMap();
    private final Config config;

    @Inject
    AFKRunnable(Config config) {
        this.config = config;
    }

    public void addPlayer(Player player) {
        locationMap.put(player.getUniqueId(), new LocationTime(System.currentTimeMillis(), player.getLocation()));
    }

    public void clear() {
        this.locationMap.clear();
    }

    @Override
    public void run() {
        var iterator = locationMap.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            if (!player.isOnline() || !player.hasPlayedBefore() || player.getPlayer() == null) iterator.remove();
            else if (!player.getPlayer().hasPermission("vanillatweaks.afkdisplay")) iterator.remove();
            else if (notEqual(entry.getValue().loc, player.getPlayer().getLocation())) {
                entry.getValue().loc = player.getPlayer().getLocation();
                entry.getValue().time = System.currentTimeMillis();
            } else if (entry.getValue().time < System.currentTimeMillis() - (1000L * config.secondsBeforeAFK)) {
                player.getPlayer().setDisplayName(ChatColor.GRAY + player.getPlayer().getDisplayName() + ChatColor.RESET);
                player.getPlayer().setPlayerListName(ChatColor.GRAY + player.getPlayer().getDisplayName() + ChatColor.RESET);
                AFKDisplay.AFK_DISPLAY.setTo(player.getPlayer(), true);
                locationMap.remove(entry.getKey());
            }
        }
    }

    private static class LocationTime {
        public Long time;
        public Location loc;

        private LocationTime(Long time, Location loc) {
            this.time = time;
            this.loc = loc;
        }
    }

    private boolean notEqual(Location loc1, Location loc2) {
        return loc1.getBlockX() != loc2.getBlockX()
                || loc1.getBlockY() != loc2.getBlockY()
                || loc1.getBlockZ() != loc2.getBlockZ();
    }
}
