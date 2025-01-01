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
package me.machinemaker.papertweaks.modules.mobs.silencemobs;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleListener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;

class EntityInteractionListener implements ModuleListener {

    private final Plugin plugin;

    @Inject
    EntityInteractionListener(final Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        if (!event.getPlayer().hasPermission("vanillatweaks.silencemobs")) return;
        final ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (item.getType() == Material.NAME_TAG) {
            final @Nullable ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.displayName() != null) {
                final String name = PlainTextComponentSerializer.plainText().serializeOr(meta.displayName(), "");
                final boolean toSilent = name.equalsIgnoreCase("silence me") || name.equalsIgnoreCase("silence_me");
                if (toSilent) {
                    final Entity clickedEntity = event.getRightClicked();
                    clickedEntity.setSilent(true);
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> clickedEntity.customName(text("silenced")), 10L);
                }
            }
        }
    }

}
