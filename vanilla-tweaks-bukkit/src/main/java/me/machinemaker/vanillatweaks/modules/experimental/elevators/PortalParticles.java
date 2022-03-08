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
package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.Marker;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

class PortalParticles extends BukkitRunnable {

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Marker marker : world.getEntitiesByClass(Marker.class)) {
                if (Elevators.IS_ELEVATOR.has(marker)) {
                    if (!Tag.WOOL.isTagged(marker.getLocation().getBlock().getType())) {
                        marker.remove();
                        marker.getWorld().dropItem(marker.getLocation(), new ItemStack(Material.ENDER_PEARL));
                        return;
                    }
                    marker.getWorld().spawnParticle(Particle.REVERSE_PORTAL, marker.getLocation().add(0, 0.5, 0), 1, 0.25, 0, 0.25, 0.02);
                }
            }
        }
    }
}
