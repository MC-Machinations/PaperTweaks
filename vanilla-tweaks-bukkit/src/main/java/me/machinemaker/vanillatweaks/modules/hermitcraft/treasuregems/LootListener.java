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
package me.machinemaker.vanillatweaks.modules.hermitcraft.treasuregems;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Registry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.loot.LootTables;

class LootListener implements ModuleListener {

    private final TreasureGems treasureGems;

    @Inject
    LootListener(TreasureGems treasureGems) {
        this.treasureGems = treasureGems;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        if (event.getInventoryHolder() != null) {
            LootTables tables = Registry.LOOT_TABLES.get(event.getLootTable().getKey());
            if (tables != null && this.treasureGems.treasurePoolMap.containsKey(tables)) {
                this.treasureGems.treasurePoolMap.get(tables).collectLoot(event.getLoot()::add);
            }
        }
    }
}
