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
package me.machinemaker.vanillatweaks.modules.hermitcraft.treasuregems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import me.machinemaker.vanillatweaks.utils.WeightedRandomList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TreasurePool {

    private final int minRolls;
    private final int maxRolls;
    private final WeightedRandomList<Entry> entries = new WeightedRandomList<>(Entry::getWeight);

    TreasurePool(final int minRolls, final int maxRolls, final List<Entry> entries) {
        this.minRolls = minRolls;
        this.maxRolls = maxRolls;
        this.entries.addAll(entries);
    }

    public void collectLoot(final Map<String, ItemStack> gems, final Consumer<@NotNull ItemStack> stackConsumer) {
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(this.minRolls, this.maxRolls + 1); i++) {
            this.entries.next().rollSkull(gems, stackConsumer);
        }
    }

    static class Entry {

        private final int weight;
        private final int minCount;
        private final int maxCount;
        private final @Nullable String gem;

        Entry(final int weight, final int minCount, final int maxCount, @Nullable final String head) {
            this.weight = weight;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.gem = head;
        }

        public void rollSkull(final Map<String, ItemStack> gems, final Consumer<ItemStack> stackConsumer) {
            if (this.gem != null) {
                ItemStack stack = gems.get(this.gem);
                if (stack == null) {
                    throw new IllegalStateException("Could not find a gem with name " + this.gem);
                }
                stack = stack.clone();
                stack.setAmount(ThreadLocalRandom.current().nextInt(this.minCount, this.maxCount + 1));
                stackConsumer.accept(stack);
            }
        }

        public int getWeight() {
            return this.weight;
        }
    }
}
