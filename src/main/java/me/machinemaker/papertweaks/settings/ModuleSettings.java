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
package me.machinemaker.papertweaks.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ModuleSettings<C, S extends ModuleSetting<?, C>> {

    private final Map<SettingKey<?>, S> settings = new HashMap<>();
    private @MonotonicNonNull Map<String, S> index;
    private boolean acceptingRegistrations = true;

    protected void register(final S setting) {
        if (!this.acceptingRegistrations) {
            throw new IllegalStateException("Not accepting further setting registrations, the index has already been created");
        }
        this.settings.put(setting.settingKey(), setting);
    }

    public Map<String, S> index() {
        if (this.index == null) {
            this.index = this.settings.values().stream().collect(Collectors.toMap(Setting::indexKey, Function.identity()));
            this.acceptingRegistrations = false;
        }
        return this.index;
    }

    public SettingGetter createGetter(final C container) {
        return new SettingGetter() {

            @Override
            public <V> @Nullable V get(final SettingKey<V> settingKey) {
                return ModuleSettings.this.getSetting(settingKey).get(container);
            }

            @Override
            public <V> V getOrDefault(final SettingKey<V> settingKey) {
                return ModuleSettings.this.getSetting(settingKey).getOrDefault(container);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <V> Setting<V, C> getSetting(final SettingKey<V> settingKey) {
        return (Setting<V, C>) Objects.requireNonNull(ModuleSettings.this.settings.get(settingKey));
    }

    public interface SettingGetter {

        <V> @Nullable V get(final SettingKey<V> settingKey);

        <V> V getOrDefault(final SettingKey<V> settingKey);
    }
}
