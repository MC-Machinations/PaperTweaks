package me.machinemaker.vanillatweaks.utils;

import java.util.Objects;

/**
 * Caches the hash code of the wrapped object
 * @param <T>
 */
public class CachedHashObjectWrapper<T> {
    public final T item;
    private final int hash;
    public CachedHashObjectWrapper(T item) {
        this.item = item;
        this.hash = Objects.hash(item);
    }

    @Override
    public boolean equals(Object o) {
        return item.equals(o);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
