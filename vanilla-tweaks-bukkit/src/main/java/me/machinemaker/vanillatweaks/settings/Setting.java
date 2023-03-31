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
package me.machinemaker.vanillatweaks.settings;

import cloud.commandframework.arguments.parser.ArgumentParser;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Setting<T, C> {

    @Nullable T get(C container);

    default T getOrDefault(final C container) {
        final T value = this.get(container);
        if (value == null) {
            return this.defaultValue();
        }
        return value;
    }

    void set(C holder, T value);

    @SuppressWarnings("unchecked")
    default void setObject(final C holder, final Object value) {
        if (!this.valueType().isInstance(value)) {
            throw new IllegalArgumentException(value + " could not be cast to " + this.valueType().getName());
        }
        this.set(holder, (T) value);
    }

    default void reset(final C holder) {
        this.set(holder, this.defaultValue());
    }

    Class<T> valueType();

    T defaultValue();

    String indexKey();

    ArgumentParser<CommandDispatcher, T> argumentParser();

    default Component validations() {
        return Component.empty();
    }
}
