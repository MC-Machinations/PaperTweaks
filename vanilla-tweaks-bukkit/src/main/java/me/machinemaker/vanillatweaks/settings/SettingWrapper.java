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
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SettingWrapper<T, C> implements Setting<T, C> {

    Setting<T, C> wrappedSetting;

    @Override
    public @Nullable T get(@NotNull C container) {
        validateWrapper();
        return this.wrappedSetting.get(container);
    }

    @Override
    public void set(@NotNull C holder, T value) {
        validateWrapper();
        this.wrappedSetting.set(holder, value);
    }

    @Override
    public @NotNull Class<T> valueType() {
        validateWrapper();
        return this.wrappedSetting.valueType();
    }

    @Override
    public @NotNull T defaultValue() {
        validateWrapper();
        return this.wrappedSetting.defaultValue();
    }

    @Override
    public @NotNull String indexKey() {
        validateWrapper();
        return this.wrappedSetting.indexKey();
    }

    @Override
    public @NotNull ArgumentParser<CommandDispatcher, T> argumentParser() {
        validateWrapper();
        return this.wrappedSetting.argumentParser();
    }

    private void validateWrapper() {
        if (this.wrappedSetting == null) {
            throw new IllegalStateException("This wrapped setting hasn't had its delegate set yet");
        }
    }

    public static <T> @NotNull PDC<T> pdc(@NotNull NamespacedKey key) {
        return new PDC<>(key);
    }

    public static class PDC<T> extends SettingWrapper<T, Player> {

        public final NamespacedKey key;

        PDC(@NotNull NamespacedKey key) {
            this.key = key;
        }
    }
}
