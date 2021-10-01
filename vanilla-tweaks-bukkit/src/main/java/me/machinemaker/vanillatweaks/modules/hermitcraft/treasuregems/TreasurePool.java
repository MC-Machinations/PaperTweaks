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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import me.machinemaker.vanillatweaks.utils.WeightedRandomList;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.IntFunction;

class TreasurePool {

    private final int minRolls;
    private final int maxRolls;
    private final WeightedRandomList<Entry> entries = new WeightedRandomList<>(Entry::getWeight);

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    TreasurePool(@JsonProperty("rolls") Map<String, Integer> map, List<Entry> entries) {
        this.minRolls = map.get("min");
        this.maxRolls = map.get("max");
        this.entries.addAll(entries);
    }

    public void collectLoot(Consumer<@NotNull ItemStack> stackConsumer) {
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(this.minRolls, this.maxRolls + 1); i++) {
            stackConsumer.accept(this.entries.next().rollSkull());
        }
    }

    private static class Entry {

        private final int weight;
        private final int minCount;
        private final int maxCount;
        private final @Nullable IntFunction<ItemStack> skullCreator;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private Entry(int weight, @JsonProperty("count") @Nullable Map<String, Integer> map, @Nullable Map<String, String> head) {
            this.weight = weight;
            if (map != null && head != null) {
                this.minCount = map.get("min");
                this.maxCount = map.get("max");
                this.skullCreator = value -> {
                    return VTUtils.getSkull(LegacyComponentSerializer.legacySection().serialize(GsonComponentSerializer.gson().deserialize(head.get("name"))), UUID.fromString(head.get("uuid")), head.get("texture"), value);
                };
            } else if (map == null && head == null) {
                this.minCount = 0;
                this.maxCount = 0;
                this.skullCreator = null;
            } else {
                throw new IllegalArgumentException("Invalid treasure gem json");
            }
        }

        public @NotNull ItemStack rollSkull() {
            if (skullCreator != null) {
                return this.skullCreator.apply(ThreadLocalRandom.current().nextInt(this.minCount, this.maxCount + 1));
            } else {
                return new ItemStack(Material.AIR, 0);
            }
        }

        public int getWeight() {
            return weight;
        }
    }
}
