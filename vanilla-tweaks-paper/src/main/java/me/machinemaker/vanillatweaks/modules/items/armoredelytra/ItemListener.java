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
package me.machinemaker.vanillatweaks.modules.items.armoredelytra;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.utils.Keys;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;

class ItemListener implements ModuleListener {

    static final PDCKey<Boolean> IS_ARMORED_ELYTRA = PDCKey.bool(Keys.key("ae.is_armored_elytra"));

    private final Plugin plugin;

    @Inject
    ItemListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.ELYTRA) {
            if (!IS_ARMORED_ELYTRA.has(event.getItemDrop().getItemStack())) {
                new ItemDropRunnable(event.getItemDrop(), ItemDropRunnable.LookingFor.CHESTPLATE).runTaskTimer(this.plugin, 1L, 2L);
            } else {
                new ItemDropRunnable(event.getItemDrop(), ItemDropRunnable.LookingFor.ARMORED_ELYTRA).runTaskTimer(this.plugin, 1L, 2L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemBurn(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item && Boolean.TRUE.equals(IS_ARMORED_ELYTRA.has(item.getItemStack()))) {
            ItemDropRunnable.breakArmoredElytra(item.getWorld(), item.getLocation(), item, false);
        }
    }
}
