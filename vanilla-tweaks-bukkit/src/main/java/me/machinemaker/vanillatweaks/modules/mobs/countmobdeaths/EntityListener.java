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
package me.machinemaker.vanillatweaks.modules.mobs.countmobdeaths;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scoreboard.Objective;

class EntityListener implements ModuleListener {

    private final CountMobDeaths countMobDeaths;
    private final Config config;

    @Inject
    EntityListener(final CountMobDeaths countMobDeaths, final Config config) {
        this.countMobDeaths = countMobDeaths;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        if (this.config.countedMobs.contains(event.getEntityType()) && event.getEntity().getCustomName() == null) {
            final String entry = ChatColor.YELLOW + event.getEntity().getName();
            for (final CountMobDeaths.CountingBoard countingBoard : this.countMobDeaths.scoreboardPlayerMap.values()) {
                if (countingBoard.isCounting()) {
                    final Objective objective = this.countMobDeaths.getDeathCountObjective(countingBoard.scoreboard());
                    objective.getScore(entry).setScore(objective.getScore(entry).getScore() + 1);
                }
            }
        }
    }

}
