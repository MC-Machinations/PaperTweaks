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
package me.machinemaker.vanillatweaks.modules.mobs.silencemobs;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

class EntityInteractionListener implements ModuleListener {

    private final Plugin plugin;

    @Inject
    EntityInteractionListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!event.getPlayer().hasPermission("vanillatweaks.silencemobs")) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.NAME_TAG) {
            if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
                String name = item.getItemMeta().getDisplayName();
                boolean toSilent = name.equalsIgnoreCase("silence me") || name.equalsIgnoreCase("silence_me");
                if (toSilent) {
                    event.getRightClicked().setSilent(true);
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getRightClicked().setCustomName("silenced"), 10L);
                }
            }
        }
    }

}
