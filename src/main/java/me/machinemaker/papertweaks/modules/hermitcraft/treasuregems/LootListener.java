/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.hermitcraft.treasuregems;

import com.google.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Registry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.loot.LootTables;
import org.checkerframework.checker.nullness.qual.Nullable;

class LootListener implements ModuleListener {

    private final TreasureGems treasureGems;
    private final TreasurePool treasurePool;

    @Inject
    LootListener(final TreasureGems treasureGems) {
        this.treasureGems = treasureGems;
        final List<TreasurePool.Entry> entries = this.treasureGems.heads.keySet().stream()
            .map(gem -> new TreasurePool.Entry(1, 1, 2, gem))
            .collect(Collectors.toList());
        entries.add(new TreasurePool.Entry(2, 0, 0, null));
        this.treasurePool = new TreasurePool(1, 2, entries);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(final LootGenerateEvent event) {
        if (event.getInventoryHolder() != null) {
            final @Nullable LootTables tables = Registry.LOOT_TABLES.get(event.getLootTable().getKey());
            if (tables != null && this.treasureGems.tables.contains(tables)) {
                this.treasurePool.collectLoot(this.treasureGems.heads, event.getLoot()::add);
            }
        }
    }
}
