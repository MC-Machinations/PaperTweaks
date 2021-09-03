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
package me.machinemaker.vanillatweaks.adventure.translations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import me.machinemaker.vanillatweaks.adventure.AbstractComponentBuilder;
import net.kyori.adventure.text.AbstractComponent;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Template;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class MappedTranslatableComponent extends AbstractComponent implements BuildableComponent<MappedTranslatableComponent, MappedTranslatableComponent.Builder>, ScopedComponent<MappedTranslatableComponent> {

    private final TranslatableComponent delegate;
    private final Map<String, Component> args;

    public MappedTranslatableComponent(@NotNull TranslatableComponent delegate, @NotNull Map<String, Component> args) {
        super(delegate.children(), delegate.style());
        this.delegate = delegate;
        this.args = args;

    }

    public static MappedTranslatableComponent mapped(@NotNull @Pattern("[a-zA-Z_\\-\\.]+")  String key) {
        return new MappedTranslatableComponent(Component.translatable(key), Collections.emptyMap());
    }

    public static MappedTranslatableComponent mapped(@NotNull @Pattern("[a-zA-Z_\\-\\.]+")  String key, @NotNull Map<String, Component> mappedArguments) {
        return new MappedTranslatableComponent(Component.translatable(key), mappedArguments);
    }

    public static MappedTranslatableComponent mapped(@NotNull @Pattern("[a-zA-Z_\\-\\.]+") String key, @NotNull TextColor color) {
        return new MappedTranslatableComponent(Component.translatable(key, color), Collections.emptyMap());
    }

    public static MappedTranslatableComponent mapped(@NotNull @Pattern("[a-zA-Z_\\-\\.]+")  String key, @NotNull TextColor color, @NotNull Map<String, Component> mappedArguments) {
        return new MappedTranslatableComponent(Component.translatable(key, color), mappedArguments);
    }

    public static Builder mappedBuilder(@NotNull @Pattern("[a-zA-Z_\\-\\.]+")  String key) {
        return new Builder().key(key);
    }

    public static Builder mappedBuilder(@NotNull @Pattern("[a-zA-Z_\\-\\.]+")  String key, @NotNull TextColor color) {
        return new Builder().key(key).color(color);
    }

    public @NotNull List<Template> templates(Locale locale) {
        var builder = ImmutableList.<Template>builder();
        for (var entry : this.args.entrySet()) {
            if (entry.getValue() instanceof MappedTranslatableComponent) {
                builder.add(Template.of(entry.getKey(), MappedTranslatableComponentRenderer.GLOBAL_INSTANCE.render(entry.getValue(), locale)));
            } else {
                builder.add(Template.of(entry.getKey(), entry.getValue()));
            }
        }
        return builder.build();
    }

    public @NotNull String key() {
        return this.delegate.key();
    }

    public @NotNull MappedTranslatableComponent key(@NotNull String key) {
        return new MappedTranslatableComponent(this.delegate.key(key), this.args);
    }

    public @NotNull Map<String, Component> args() {
        return this.args;
    }

    public @NotNull MappedTranslatableComponent args(@NotNull Map<String, Component> args) {
        return new MappedTranslatableComponent(this.delegate, args);
    }

    @Override
    public @NotNull MappedTranslatableComponent children(@NotNull List<? extends ComponentLike> children) {
        return new MappedTranslatableComponent(this.delegate.children(children), this.args);
    }

    @Override
    public @NotNull MappedTranslatableComponent style(@NotNull Style style) {
        return new MappedTranslatableComponent(this.delegate.style(style), this.args);
    }

    @Override
    public @NotNull Component replaceText(@NotNull Consumer<TextReplacementConfig.Builder> configurer) {
        return this.delegate.replaceText(configurer);
    }

    @Override
    public @NotNull Component replaceText(@NotNull TextReplacementConfig config) {
        return this.delegate.replaceText(config);
    }

    @Override
    public @NotNull Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MappedTranslatableComponent) obj;
        return Objects.equals(this.delegate, that.delegate) &&
                Objects.equals(this.args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, args);
    }

    @Override
    public String toString() {
        return "ModuleTranslationComponent[" +
                "delegate=" + delegate + ", " +
                "mappedArguments=" + args + ']';
    }


    public static final class Builder extends AbstractComponentBuilder<MappedTranslatableComponent, Builder> {

        private String key;
        private Map<String, Component> args = Maps.newLinkedHashMap();

        private Builder() {
        }

        private Builder(final @NotNull MappedTranslatableComponent component) {
            super(component);
            this.key = component.key();
            this.args = Maps.newHashMap(component.args());
        }

        public @NotNull Builder key(@NotNull String key) {
            this.key = key;
            return this;
        }

        public @NotNull Builder args(@NotNull Map<String, Component> args) {
            this.args = args;
            return this;
        }

        public @NotNull Builder arg(@NotNull String key, @NotNull Component component) {
            this.args.put(key, component);
            return this;
        }

        public @NotNull Builder arg(@NotNull String key, @NotNull String value) {
            this.args.put(key, Component.text(value));
            return this;
        }

        public @NotNull Builder arg(@NotNull String key, int value) {
            this.args.put(key, Component.text(value));
            return this;
        }

        public @NotNull Builder arg(@NotNull String key, long value) {
            this.args.put(key, Component.text(value));
            return this;
        }

        @Override
        public @NotNull MappedTranslatableComponent build() {
            if (this.key == null) throw new IllegalStateException("key must be set");
            return new MappedTranslatableComponent(Component.translatable(this.key, this.buildStyle()).children(this.children), ImmutableMap.copyOf(this.args));
        }
    }
}
