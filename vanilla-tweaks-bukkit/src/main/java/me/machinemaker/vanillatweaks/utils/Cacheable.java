/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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

import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Cacheable<T> {

    private final Supplier<? extends T> objectSupplier;
    private final long cacheLength;
    private @Nullable T object;
    private long time;

    public Cacheable(final Supplier<? extends T> objectSupplier, final long cacheLength) {
        this.objectSupplier = objectSupplier;
        this.cacheLength = cacheLength;
    }

    public T get() {
        if (this.object == null || this.time + this.cacheLength < System.currentTimeMillis()) {
            this.object = this.objectSupplier.get();
            this.time = System.currentTimeMillis();
        }
        return this.object;
    }

    public void invalidate() {
        this.object = null;
        this.time = 0;
    }
}
