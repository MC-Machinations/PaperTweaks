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
package me.machinemaker.vanillatweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

@Singleton
class HUDRunnable implements Runnable {

    static final NamespacedKey COORDINATES_HUD_KEY = Keys.key("coordinateshud");

    private final Set<Player> enabled = ConcurrentHashMap.newKeySet();
    private final BukkitAudiences audiences;

    @Inject
    HUDRunnable(final BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    public void addPlayer(final Player player) {
        this.enabled.add(player);
    }

    public void removePlayer(final Player player) {
        this.enabled.remove(player);
    }

    public void clearPlayers() {
        this.enabled.clear();
    }

    public Set<Player> getPlayers() {
        return this.enabled;
    }

    @Override
    public void run() {
        this.enabled.forEach(player -> {
            final long time = (player.getWorld().getTime() + 6000) % 24000;
            final long hours = time / 1000;
            final Long extra = (time - (hours * 1000)) * 60 / 1000;

            final Audience audience = this.audiences.player(player);
            final Location loc = player.getLocation();
            final TextComponent.Builder builder = Component.text().content("XYZ: ").color(NamedTextColor.GOLD).append(
                    Component.text(String.format("%d %d %d  ", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), NamedTextColor.WHITE),
                    Component.text(String.format("%2s      %02d:%02d", CoordinatesHUD.getDirection(loc.getYaw()).c, hours, extra))
            );
            audience.sendActionBar(builder.build()); // TODO i18n
        });
    }
}
