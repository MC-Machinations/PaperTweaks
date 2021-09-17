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
package me.machinemaker.vanillatweaks.modules.survival.coordinateshud;

import com.google.common.collect.Sets;
import com.google.inject.Singleton;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.Set;

@Singleton
class HUDRunnable implements Runnable {

    static final NamespacedKey COORDINATES_HUD_KEY = Keys.key("coordinateshud");

    private final Set<Player> enabled = Sets.newHashSet();

    public void addPlayer(Player player) {
        this.enabled.add(player);
    }

    public void removePlayer(Player player) {
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
            long time = (player.getWorld().getTime() + 6000) % 24000;
            long hours = time / 1000;
            Long extra = (time - (hours * 1000)) * 60 / 1000;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format(ChatColor.GOLD + "XYZ: "+ ChatColor.RESET + "%d %d %d  " + ChatColor.GOLD + "%2s      %02d:%02d",
                    player.getLocation().getBlockX(),
                    player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ(),
                    CoordinatesHUD.getDirection(player.getLocation().getYaw()).c,
                    hours,
                    extra
            ))); // TODO adventure
        });
    }
}
