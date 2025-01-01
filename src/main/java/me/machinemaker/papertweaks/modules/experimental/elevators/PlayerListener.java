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
package me.machinemaker.papertweaks.modules.experimental.elevators;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.utils.Entities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

class PlayerListener implements ModuleListener {

    private final Config config;
    private final JavaPlugin plugin;

    @Inject
    PlayerListener(final Config config, final JavaPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    private static boolean canUseElevator(final PlayerEvent event) {
        return event.getPlayer().hasPermission("vanillatweaks.elevators.use");
    }

    private static boolean isOnElevator(final Location location) {
        return Entities.getSingleNearbyEntityOfType(Marker.class, location.subtract(0, 1, 0).getBlock().getLocation().add(0.5, 0.5, 0.5), 0.1, 0.1, 0.1, Elevators.IS_ELEVATOR::has) != null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (canUseElevator(event) && event.getPlayer().getVelocity().getY() > 0 && isOnElevator(event.getPlayer().getLocation())) {
            final Location elevatorLoc = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getLocation();
            final Collection<Marker> elevators = Entities.getNearbyEntitiesOfType(Marker.class, elevatorLoc.add(0.5, 0.5, 0.5), 0.01, this.config.maxVerticalSearch + 0.01, 0.01, marker -> Elevators.IS_ELEVATOR.has(marker) && marker.getLocation().getBlockY() > elevatorLoc.getBlockY());
            this.teleportPlayer(event.getPlayer(), elevatorLoc, elevators);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSneak(final PlayerToggleSneakEvent event) {
        if (canUseElevator(event) && event.isSneaking() && isOnElevator(event.getPlayer().getLocation())) {
            final Location elevatorLoc = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getLocation();
            final Collection<Marker> elevators = Entities.getNearbyEntitiesOfType(Marker.class, elevatorLoc.add(0.5, 0.5, 0.5), 0.01, this.config.maxVerticalSearch + 0.01, 0.01, marker -> Elevators.IS_ELEVATOR.has(marker) && marker.getLocation().getBlockY() < elevatorLoc.getBlockY());
            this.teleportPlayer(event.getPlayer(), elevatorLoc, elevators);
        }
    }

    private void teleportPlayer(final Player player, final Location start, final Collection<Marker> possibleLocations) {
        if (!possibleLocations.isEmpty()) {
            final Marker next = Collections.min(possibleLocations, Comparator.comparingDouble(value -> value.getLocation().distanceSquared(start)));
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                player.teleport(new Location(next.getWorld(), next.getLocation().getX(), next.getLocation().getBlockY() + 1D, next.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);
            }, 1L);
        }
    }
}
