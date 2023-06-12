/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.items.armoredelytra;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.pdc.PDCKey;
import me.machinemaker.papertweaks.utils.Keys;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;

class ItemListener implements ModuleListener {

    static final PDCKey<Boolean> IS_ARMORED_ELYTRA = PDCKey.bool(Keys.legacyKey("ae.is_armored_elytra"));

    private final Plugin plugin;

    @Inject
    ItemListener(final Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(final PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.ELYTRA) {
            ItemDropRunnable.LookingFor lookingFor = null;
            if (!IS_ARMORED_ELYTRA.has(event.getItemDrop().getItemStack()) && event.getPlayer().hasPermission("vanillatweaks.armoredelytra.create")) {
                lookingFor = ItemDropRunnable.LookingFor.CHESTPLATE;
            } else if (IS_ARMORED_ELYTRA.has(event.getItemDrop().getItemStack()) && event.getPlayer().hasPermission("vanillatweaks.armoredelytra.destroy")) {
                lookingFor = ItemDropRunnable.LookingFor.ARMORED_ELYTRA;
            }
            if (lookingFor != null) {
                new ItemDropRunnable(event.getItemDrop(), lookingFor).runTaskTimer(this.plugin, 1L, 2L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemBurn(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item && Boolean.TRUE.equals(IS_ARMORED_ELYTRA.has(item.getItemStack()))) {
            ItemDropRunnable.breakArmoredElytra(item.getWorld(), item.getLocation(), item, false);
        }
    }
}
