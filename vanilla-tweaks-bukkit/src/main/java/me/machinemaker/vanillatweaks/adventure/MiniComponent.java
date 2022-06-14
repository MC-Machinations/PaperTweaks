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
package me.machinemaker.vanillatweaks.adventure;

import java.util.stream.Stream;

import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.text.AbstractComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

@DefaultQualifier(NonNull.class)
public class MiniComponent extends AbstractComponent implements ScopedComponent<MiniComponent> {

    private final String key;
    private final Map<String, Component> args;

    MiniComponent(String key) {
        this(key, Collections.emptyMap());
    }

    MiniComponent(String key, Map<String, Component> args) {
        this(key,Collections.emptyList(), Style.empty(), args);
    }

    MiniComponent(String key, List<? extends ComponentLike> children, Style style, Map<String, Component> args) {
        super(children, style);
        this.key = key;
        this.args = args;
    }

    public String key() {
        return this.key;
    }

    public Map<String, Component> args() {
        return this.args;
    }

    @Override
    public MiniComponent children(List<? extends ComponentLike> children) {
        return new MiniComponent(this.key, children, this.style, this.args);
    }

    @Override
    public MiniComponent style(Style style) {
        return new MiniComponent(this.key, this.children, style, this.args);
    }

    @Override
    public boolean equals(final @Nullable Object other) {
        if (this == other) return true;
        if (!(other instanceof final MiniComponent that)) return false;
        if (!super.equals(other)) return false;
        return Objects.equals(this.key, that.key) && Objects.equals(this.args, that.args);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = (31 * result) + this.key.hashCode();
        result = (31 * result) + this.args.hashCode();
        return result;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(
                Stream.of(
                        ExaminableProperty.of("key", this.key()),
                        ExaminableProperty.of("args", this.args())
                ),
                super.examinableProperties()
        );
    }

    @Override
    public String toString() {
        return Internals.toString(this);
    }
}
