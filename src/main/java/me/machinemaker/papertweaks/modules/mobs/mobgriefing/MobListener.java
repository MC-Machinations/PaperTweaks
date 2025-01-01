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
package me.machinemaker.papertweaks.modules.mobs.mobgriefing;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

class MobListener implements ModuleListener {

    private final Config config;

    @Inject
    MobListener(final Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlockEvent(final EntityChangeBlockEvent event) {
        if (this.config.antiEndermanGrief && event.getEntity() instanceof Enderman) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (this.config.antiCreeperGrief && this.config.disableEntityDamage && event.getEntity() instanceof final Creeper creeper) {
            creeper.setExplosionRadius(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(final ExplosionPrimeEvent event) {
        if (this.config.antiCreeperGrief && this.config.disableEntityDamage && event.getEntity() instanceof final Creeper creeper) {
            creeper.setExplosionRadius(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (this.config.antiGhastGrief && event.getEntity() instanceof final Fireball fireball && fireball.getShooter() instanceof Ghast) {
            if (this.config.disableEntityDamage) {
                event.setCancelled(true);
            } else {
                event.blockList().clear();
            }
        }
        if (event.getEntity() instanceof Creeper && this.config.antiCreeperGrief && !this.config.disableEntityDamage) {
            event.blockList().clear();
        }
    }

}
