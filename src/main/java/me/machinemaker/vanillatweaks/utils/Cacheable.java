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
