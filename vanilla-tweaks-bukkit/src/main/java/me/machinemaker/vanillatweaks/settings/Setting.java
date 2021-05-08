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
package me.machinemaker.vanillatweaks.settings;

import cloud.commandframework.arguments.parser.ArgumentParser;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Setting<T, C> {

    @Nullable T get(@NotNull C container);

    default @NotNull T getOrDefault(@NotNull C container) {
        T value = get(container);
        if (value == null) {
            return defaultValue();
        }
        return value;
    }

    void set(@NotNull C holder, T value);

    @SuppressWarnings("unchecked")
    default<A extends Setting<T, C>> A loadWrapper(SettingWrapper<T, C> wrapper) {
        if (wrapper.wrappedSetting != null) {
            throw new IllegalArgumentException("wrapper has already had its setting set");
        }
        if (this instanceof SettingWrapper<?, ?>) {
            throw new IllegalArgumentException("cannot wrap a setting wrapper");
        }
        wrapper.wrappedSetting = this;
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    default void setObject(@NonNull C holder, Object value) {
        if (!valueType().isInstance(value)) {
            throw new IllegalArgumentException(value + " could not be cast to " + valueType().getName());
        }
        set(holder, (T) value);
    }

    default void reset(@NotNull C holder) {
        set(holder, defaultValue());
    }

    Class<T> valueType();

    @NotNull T defaultValue();

    @NotNull String indexKey();

    @NotNull ArgumentParser<CommandDispatcher, T> argumentParser();
}
