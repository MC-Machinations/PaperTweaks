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
package me.machinemaker.vanillatweaks.modules.survival.afkdisplay;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;

class PlayerListener implements ModuleListener {

    private final AFKRunnable afkRunnable;

    @Inject
    PlayerListener(me.machinemaker.vanillatweaks.modules.survival.afkdisplay.AFKRunnable afkRunnable) {
        this.afkRunnable = afkRunnable;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getPersistentDataContainer().has(this.afkRunnable.afkDisplayKey, PersistentDataType.BYTE)) {
            event.getPlayer().setDisplayName(event.getPlayer().getName());
            event.getPlayer().setPlayerListName(event.getPlayer().getName());
            event.getPlayer().getPersistentDataContainer().remove(this.afkRunnable.afkDisplayKey);
            this.afkRunnable.addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.afkRunnable.addPlayer(event.getPlayer());
    }
}
