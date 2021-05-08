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
package me.machinemaker.vanillatweaks.modules.mobs.villagerdeathmessages;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.utils.PaperVTUtils;
import org.bukkit.Location;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;

import static me.machinemaker.vanillatweaks.adventure.translations.MappedTranslatableComponent.mappedBuilder;


class EntityListener implements ModuleListener {

    private final Config config;

    @Inject
    EntityListener(Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof AbstractVillager && config.showMessageOnDeath) {
            Location loc = event.getEntity().getLocation();
            PaperVTUtils.broadcast(mappedBuilder("modules.villager-death-messages.on-death").arg("x", loc.getBlockX()).arg("y", loc.getBlockY()).arg("z", loc.getBlockZ()).arg("world", event.getEntity().getWorld().getName()).build(), "vanillatweaks.villagerdeathmessages.death");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityConvert(EntityTransformEvent event) {
        if (event.getEntity() instanceof Villager && event.getTransformReason() == EntityTransformEvent.TransformReason.INFECTION && config.showMessageOnConversion) {
            Location loc = event.getEntity().getLocation();
            PaperVTUtils.broadcast(mappedBuilder("modules.villager-death-messages.on-conversion").arg("x", loc.getBlockX()).arg("y", loc.getBlockY()).arg("z", loc.getBlockZ()).arg("world", event.getEntity().getWorld().getName()).build(), "vanillatweaks.villagerdeathmessages.conversion");
        }
    }

}
