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
package me.machinemaker.vanillatweaks.settings;

import cloud.commandframework.arguments.parser.ArgumentParser;
import me.machinemaker.vanillatweaks.cloud.CommandDispatcher;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SettingWrapper<T, C> implements Setting<T, C> {

    private final NamespacedKey key;
    Setting<T, C> wrappedSetting;

    protected SettingWrapper(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey key() {
        return this.key;
    }

    @Override
    public @Nullable T get(@NotNull C container) {
        checkSettingSet();
        return this.wrappedSetting.get(container);
    }

    @Override
    public void set(@NotNull C holder, T value) {
        checkSettingSet();
        this.wrappedSetting.set(holder, value);
    }

    @Override
    public Class<T> valueType() {
        return this.wrappedSetting.valueType();
    }

    @Override
    public @NotNull T defaultValue() {
        checkSettingSet();
        return this.wrappedSetting.defaultValue();
    }

    @Override
    public @NotNull String indexKey() {
        return this.key.getKey();
    }

    @Override
    public @NotNull ArgumentParser<CommandDispatcher, T> argumentParser() {
        checkSettingSet();
        return this.wrappedSetting.argumentParser();
    }

    private void checkSettingSet() {
        if (this.wrappedSetting == null) {
            throw new IllegalStateException("This wrapped setting hasn't had its delegate set yet");
        }
    }

    public static <T> @NotNull PDC<T> pdc(@NotNull NamespacedKey key) {
        return new PDC<>(key);
    }

    public static class PDC<T> extends SettingWrapper<T, PersistentDataHolder> {

        PDC(@NotNull NamespacedKey key) {
            super(key);
        }
    }
}
