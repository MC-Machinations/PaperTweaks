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
package me.machinemaker.vanillatweaks.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;

public class WeightedRandomList<T> {

    private final NavigableMap<Double, T> entries = new TreeMap<>();
    private final Random random;
    private final ToDoubleFunction<T> toDoubleFunction;
    private double totalWeight = 0;

    public WeightedRandomList(@NotNull ToDoubleFunction<T> toDoubleFunction) {
        this(new Random(), toDoubleFunction);
    }

    public WeightedRandomList(@NotNull Random random, @NotNull ToDoubleFunction<T> toDoubleFunction) {
        this.random = random;
        this.toDoubleFunction = toDoubleFunction;
    }

    public void add(T entry) {
        this.totalWeight += this.toDoubleFunction.applyAsDouble(entry);
        this.entries.put(this.totalWeight, entry);
    }

    public void addAll(Collection<T> entries) {
        for (T entry : entries) {
            this.add(entry);
        }
    }

    public @NotNull T next() {
        if (entries.isEmpty()) {
            throw new IllegalStateException("Must have at least 1 entry");
        }
        return this.entries.higherEntry(this.random.nextDouble() * this.totalWeight).getValue();
    }
}
