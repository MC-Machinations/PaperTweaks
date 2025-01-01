/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2020-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.mobs.largerphantoms;

import com.google.inject.Inject;
import java.util.Optional;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

class SpawnListener implements ModuleListener {

    private final Config config;

    @Inject
    SpawnListener(final Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(final EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof final Phantom phantom)) return;
        double closest = -1;
        @Nullable Player player = null;
        for (final Player tempPlayer : event.getEntity().getWorld().getPlayers()) {
            if (closest == -1) {
                closest = event.getLocation().distanceSquared(tempPlayer.getLocation());
                player = tempPlayer;
            } else {
                final double tempDistance = event.getLocation().distanceSquared(tempPlayer.getLocation());
                if (tempDistance < closest) {
                    closest = tempDistance;
                    player = tempPlayer;
                }
            }
        }
        if (player == null) {
            return;
        }
        if (closest != -1 && player.hasPermission("vanillatweaks.largerphantoms")) {
            final int ticksSinceSleep = player.getStatistic(Statistic.TIME_SINCE_REST);
            final Optional<Config.SpawnData> spawnData = this.config.sortedSpawns.stream().filter(sd -> sd.minimumTicks() <= ticksSinceSleep).findFirst();
            spawnData.ifPresent(sd -> {
                phantom.setSize(sd.size());
                requireNonNull(phantom.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(sd.maxHealth());
                requireNonNull(phantom.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(sd.movementSpeed());
                requireNonNull(phantom.getAttribute(Attribute.FOLLOW_RANGE)).setBaseValue(sd.followRange());
                requireNonNull(phantom.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(sd.attackDamage());
            });
        }
    }

}
