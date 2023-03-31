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
package me.machinemaker.vanillatweaks.menus;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;

public class DelegateConfigurationMenu<S> implements ConfigurationMenu<S> {

    private final ConfigurationMenu<? super S> menu;

    protected DelegateConfigurationMenu(final ConfigurationMenu<? super S> menu) {
        this.menu = menu;
    }

    @Override
    public ComponentLike[] buildHeader(final S object) {
        return this.menu.buildHeader(object);
    }

    @Override
    public Iterable<? extends ComponentLike> buildParts(final S object) {
        return this.menu.buildParts(object);
    }

    @Override
    public ComponentLike[] buildFooter(final S object) {
        return this.menu.buildFooter(object);
    }

    @Override
    public ComponentLike build(final S object) {
        return this.menu.build(object);
    }

    @Override
    public void send(final Audience audience, final S object) {
        this.menu.send(audience, object);
    }
}
