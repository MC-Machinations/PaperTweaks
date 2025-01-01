/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2023-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.mobs.largerphantoms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.PTConfig;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

@PTConfig
class Config extends ModuleConfig {

    transient @MonotonicNonNull List<SpawnData> sortedSpawns;

    @Key("spawns")
    @Description("Spawn configuration for different levels of phantoms. Each entry in the list is sorted by the 'minimumTicks' value to determine the threshold for spawning different size phantoms.")
    private List<SpawnData> spawns = List.of(
        new SpawnData(144_000, 3, 25, 1, 20, 15),
        new SpawnData(216_000, 5, 30, 1.3, 24, 17),
        new SpawnData(288_000, 7, 35, 1.6, 28, 20),
        new SpawnData(2_400_000, 20, 100, 2, 50, 30)
    );

    record SpawnData(int minimumTicks, int size, int maxHealth, double movementSpeed, int followRange, int attackDamage) {}

    @Override
    public void reload() {
        super.reload();
        this.sortedSpawns = new ArrayList<>(this.spawns);
        this.sortedSpawns.sort(Comparator.comparing(SpawnData::minimumTicks, Comparator.reverseOrder()));
    }
}
