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
package me.machinemaker.papertweaks.adventure;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.examination.ExaminableProperty;

import static java.util.Objects.requireNonNull;

public record MiniComponent(List<Component> children, Style style, String key, Map<String, Component> args) implements ScopedComponent<MiniComponent> {

    public static MiniComponent create(final String key) {
        return create(Collections.emptyList(), Style.empty(), key, Collections.emptyMap());
    }

    public static MiniComponent create(final List<? extends ComponentLike> children, final Style style, final String key, final Map<String, Component> args) {
        final List<Component> filteredChildren = ComponentLike.asComponents(children, IS_NOT_EMPTY);

        return new MiniComponent(filteredChildren, requireNonNull(style, "style"), requireNonNull(key, "key"), args);
    }


    public String key() {
        return this.key;
    }

    public Map<String, Component> args() {
        return this.args;
    }

    @Override
    public MiniComponent children(final List<? extends ComponentLike> children) {
        return create(children, this.style, this.key, this.args);
    }

    @Override
    public MiniComponent style(final Style style) {
        return create(this.children, style, this.key, this.args);
    }

    @Override
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(
            Stream.of(
                ExaminableProperty.of("key", this.key()),
                ExaminableProperty.of("args", this.args())
            ),
            ScopedComponent.super.examinableProperties()
        );
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public String toString() {
        return Internals.toString(this);
    }
}
