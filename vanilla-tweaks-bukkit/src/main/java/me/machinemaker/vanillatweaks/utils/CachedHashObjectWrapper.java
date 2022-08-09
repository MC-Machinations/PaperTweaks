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

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Caches the hash code of the wrapped object
 *
 * @param <T>
 */
public class CachedHashObjectWrapper<T> {

    public final T item;
    private final int hash;

    public CachedHashObjectWrapper(final T item) {
        this.item = item;
        this.hash = Objects.hashCode(item);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this.item == null) {
            return o == null;
        }
        if (o instanceof CachedHashObjectWrapper<?>) {
            o = ((CachedHashObjectWrapper<?>) o).item;
        }
        return this.item.equals(o);
    }

    @Override
    public int hashCode() {
        return this.hash;
    }
}
