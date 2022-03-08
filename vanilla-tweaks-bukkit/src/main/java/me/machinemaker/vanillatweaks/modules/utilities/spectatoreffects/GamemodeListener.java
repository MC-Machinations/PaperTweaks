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
package me.machinemaker.vanillatweaks.modules.utilities.spectatoreffects;

import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.potion.PotionEffectType;

public class GamemodeListener implements ModuleListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR && event.getNewGameMode() != GameMode.SPECTATOR) {
            event.getPlayer().removePotionEffect(PotionEffectType.CONDUIT_POWER);
            event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}
