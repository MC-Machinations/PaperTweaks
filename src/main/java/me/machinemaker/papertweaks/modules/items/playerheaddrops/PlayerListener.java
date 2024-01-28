/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.items.playerheaddrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.inject.Inject;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.utils.PTUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;

class PlayerListener implements ModuleListener {

    private final Config config;

    @Inject
    PlayerListener(final Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerKilledByPlayer(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final @Nullable Player killer = player.getKiller();
        if (killer == null && this.config.requirePlayerKill) {
            return;
        } else if (killer != null && !killer.hasPermission("vanillatweaks.playerheaddrops")) {
            return;
        }
        if (ThreadLocalRandom.current().nextDouble() < this.config.dropChance) {
            final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            final @Nullable SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta == null) return; // shouldn't be possible

            final PlayerProfile profile = event.getEntity().getPlayerProfile();
            PTUtils.sanitizeTextures(profile);
            meta.setPlayerProfile(profile);
            if (killer != null) {
                meta.lore(List.of(text("Killed by " + killer.getName())));
            }
            skull.setItemMeta(meta);
            event.getDrops().add(skull);
        }
    }
}
