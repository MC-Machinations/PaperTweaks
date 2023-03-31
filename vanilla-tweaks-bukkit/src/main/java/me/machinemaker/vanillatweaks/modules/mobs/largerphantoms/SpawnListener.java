/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2020-2023 Machine_Maker
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
package me.machinemaker.vanillatweaks.modules.mobs.largerphantoms;

import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.apache.commons.lang3.Range;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;

class SpawnListener implements ModuleListener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.PHANTOM) return;
        Phantom phantom = (Phantom) event.getEntity();
        double closest = -1;
        Player player = null;
        for (Player tempPlayer : event.getEntity().getWorld().getPlayers()) {
            if (closest == -1) {
                closest = event.getLocation().distanceSquared(tempPlayer.getLocation());
                player = tempPlayer;
            } else {
                double tempDistance = event.getLocation().distanceSquared(tempPlayer.getLocation());
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
            int ticksSinceSleep = player.getStatistic(Statistic.TIME_SINCE_REST);
            int size;
            double maxHealth;
            double movementSpeed;
            double followRange;
            double attackDamage;
            if (ticksSinceSleep < 140000) return;
            else if (Range.between(144000, 216000).contains(ticksSinceSleep)) {
                size = 3;
                maxHealth = 25;
                movementSpeed = 1;
                followRange = 20;
                attackDamage = 15;
            } else if (Range.between(216000, 288000).contains(ticksSinceSleep)) {
                size = 5;
                maxHealth = 30;
                movementSpeed = 1.3;
                followRange = 24;
                attackDamage = 17;
            } else if (Range.between(288000, 2400000).contains(ticksSinceSleep)) {
                size = 7;
                maxHealth = 35;
                movementSpeed = 1.6;
                followRange = 28;
                attackDamage = 20;
            } else {
                size = 20;
                maxHealth = 100;
                movementSpeed = 2;
                followRange = 50;
                attackDamage = 30;
            }
            phantom.setSize(size);
            phantom.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
            phantom.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
            phantom.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(followRange);
            phantom.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage);
        }
    }

}
