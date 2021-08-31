/*
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
package me.machinemaker.vanillatweaks.utils;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Cacheable<T> {

    private T object;
    private long time;
    private final Supplier<T> objectSupplier;
    private final long cacheLength;

    public Cacheable(Supplier<T> objectSupplier, long cacheLength) {
        this.objectSupplier = objectSupplier;
        this.cacheLength = cacheLength;
    }

    @NotNull
    public T get() {
        if (this.object == null || this.time + this.cacheLength < System.currentTimeMillis()) {
            this.object = objectSupplier.get();
            this.time = System.currentTimeMillis();
        }
        return this.object;
    }

    public void invalidate() {
        this.object = null;
        this.time = 0;
    }
}
