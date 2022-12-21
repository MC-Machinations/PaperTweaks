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
package me.machinemaker.vanillatweaks.settings;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public abstract class ModuleSettings<S extends Setting<?, ?>> {

    private final List<S> settings = Lists.newArrayList();
    private @MonotonicNonNull Map<String, S> index;
    private boolean acceptingRegistrations = true;

    protected void register(final S setting) {
        if (!this.acceptingRegistrations) {
            throw new IllegalStateException("Not accepting further setting registrations, the index has already been created");
        }
        this.settings.add(setting);
    }

    public Map<String, S> index() {
        if (this.index == null) {
            this.index = this.settings.stream().collect(Collectors.toMap(Setting::indexKey, Function.identity()));
            this.acceptingRegistrations = false;
        }
        return this.index;
    }
}
