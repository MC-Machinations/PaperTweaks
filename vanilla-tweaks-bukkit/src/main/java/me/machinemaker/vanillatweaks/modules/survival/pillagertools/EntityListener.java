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
package me.machinemaker.vanillatweaks.modules.survival.pillagertools;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.entity.Pillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;

class EntityListener implements ModuleListener {

    private final Config config;

    @Inject
    EntityListener(Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.PATROL && event.getEntity() instanceof Pillager pillager) {
            if (!config.patrolLeaders && pillager.isPatrolLeader() && pillager.getPatrolTarget() != null) {
                event.setCancelled(true);
            }
            if (!config.patrols && pillager.getPatrolTarget() != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStatusEffectChange(EntityPotionEffectEvent event) {
        if (!config.badOmen && event.getCause() == EntityPotionEffectEvent.Cause.PATROL_CAPTAIN) event.setCancelled(true);
    }

}
