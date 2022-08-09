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
package me.machinemaker.vanillatweaks.modules.items.playerheaddrops;

import com.google.inject.Inject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

class PlayerListener implements ModuleListener {

    private final Config config;

    @Inject
    PlayerListener(final Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerKilledByPlayer(final PlayerDeathEvent event) {
        final @Nullable Player killer = event.getEntity().getKiller();
        if ((this.config.requirePlayerKill && killer == null) || (killer != null && !killer.hasPermission("vanillatweaks.playerheaddrops"))) {
            return;
        }
        if (ThreadLocalRandom.current().nextDouble() < this.config.dropChance) {
            final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            final SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta == null) return; // shouldn't be possible
            final GameProfile profile = VTUtils.getGameProfile(event.getEntity());
            final Property texture = profile.getProperties().get("textures").iterator().next();
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", new Property("textures", texture.getValue()));
            VTUtils.loadMeta(meta, profile);
            meta.setLore(List.of("Killed by " + event.getEntity().getKiller().getName()));
            skull.setItemMeta(meta);
            event.getDrops().add(skull);
        }
    }
}
