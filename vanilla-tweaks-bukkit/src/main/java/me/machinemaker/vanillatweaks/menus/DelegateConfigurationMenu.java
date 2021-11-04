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
package me.machinemaker.vanillatweaks.menus;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public class DelegateConfigurationMenu<S> implements ConfigurationMenu<S> {

    private final ConfigurationMenu<S> menu;

    protected DelegateConfigurationMenu(@NotNull ConfigurationMenu<S> menu) {
        this.menu = menu;
    }

    @Override
    public @NotNull ComponentLike[] buildHeader(@NotNull S object) {
        return this.menu.buildHeader(object);
    }

    @Override
    public @NotNull Iterable<? extends ComponentLike> buildParts(@NotNull S object) {
        return this.menu.buildParts(object);
    }

    @Override
    public @NotNull ComponentLike[] buildFooter(@NotNull S object) {
        return this.menu.buildFooter(object);
    }

    @Override
    public @NotNull ComponentLike build(@NotNull S object) {
        return this.menu.build(object);
    }

    @Override
    public void send(@NotNull Audience audience, S object) {
        this.menu.send(audience, object);
    }

    protected @NotNull ConfigurationMenu<S> menu1() {
        return this.menu;
    }
}
