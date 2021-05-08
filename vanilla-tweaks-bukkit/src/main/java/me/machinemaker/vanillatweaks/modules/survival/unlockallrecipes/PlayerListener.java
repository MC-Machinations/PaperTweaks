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
package me.machinemaker.vanillatweaks.modules.survival.unlockallrecipes;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

class PlayerListener implements ModuleListener {

    private final UnlockAllRecipes unlockAllRecipes;

    @Inject
    PlayerListener(UnlockAllRecipes unlockAllRecipes) {
        this.unlockAllRecipes = unlockAllRecipes;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        unlockAllRecipes.discoverAllRecipes(event.getPlayer());
    }
}
