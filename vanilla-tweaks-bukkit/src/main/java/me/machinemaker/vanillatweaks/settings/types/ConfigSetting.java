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
package me.machinemaker.vanillatweaks.settings.types;

import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.leangen.geantyref.GenericTypeReflector;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@SuppressWarnings("unchecked")
public record ConfigSetting<T, C extends MenuModuleConfig<C, ?>>(ValueNode<T> node, ArgumentParser<CommandDispatcher, T> argumentParser, Component validations) implements Setting<T, C> {

    public ConfigSetting(final ValueNode<T> node, final ArgumentParser<CommandDispatcher, T> argumentParser) {
        this(node, argumentParser, createValidations(node));
    }

    public static <C extends MenuModuleConfig<C, ?>> ConfigSetting<Boolean, C> ofBoolean(final ValueNode<?> valueNode) {
        return new ConfigSetting<>((ValueNode<Boolean>) valueNode, new BooleanArgument.BooleanParser<>(false));
    }

    public static <E extends Enum<E>, C extends MenuModuleConfig<C, ?>> ConfigSetting<E, C> ofEnum(final ValueNode<?> valueNode, final Class<E> classOfE) {
        return new ConfigSetting<>((ValueNode<E>) valueNode, new EnumArgument.EnumParser<>(classOfE));
    }

    public static <C extends MenuModuleConfig<C, ?>> ConfigSetting<Integer, C> ofInt(final ValueNode<?> valueNode) {
        return new ConfigSetting<>((ValueNode<Integer>) valueNode, new IntegerArgument.IntegerParser<>(Integer.parseInt(valueNode.meta().getOrDefault("min", IntegerArgument.IntegerParser.DEFAULT_MINIMUM).toString()), Integer.parseInt(valueNode.meta().getOrDefault("max", IntegerArgument.IntegerParser.DEFAULT_MAXIMUM).toString())));
    }

    public static <C extends MenuModuleConfig<C, ?>> ConfigSetting<Double, C> ofDouble(final ValueNode<?> valueNode) {
        return new ConfigSetting<>((ValueNode<Double>) valueNode, new DoubleArgument.DoubleParser<>(Double.parseDouble(valueNode.meta().getOrDefault("min", DoubleArgument.DoubleParser.DEFAULT_MINIMUM).toString()), Double.parseDouble(valueNode.meta().getOrDefault("max", DoubleArgument.DoubleParser.DEFAULT_MAXIMUM).toString())));
    }

    private static Component createValidations(final ValueNode<?> valueNode) {
        final TextComponent.Builder builder = text();
        if (valueNode.meta().containsKey("min") && valueNode.meta().containsKey("max")) {
            builder.append(translatable("commands.config.validation.between", text(valueNode.meta().get("min").toString(), GRAY), text(valueNode.meta().get("max").toString(), GRAY)));
        } else if (valueNode.meta().containsKey("min")) {
            builder.append(translatable("commands.config.validation.min", text(valueNode.meta().get("min").toString(), GRAY)));
        } else if (valueNode.meta().containsKey("max")) {
            builder.append(translatable("commands.config.validation.max", text(valueNode.meta().get("max").toString(), GRAY)));
        }

        if (builder.children().isEmpty()) {
            return Component.empty();
        }
        return builder.build();
    }

    @Override
    public @Nullable T get(final C container) {
        return container.rootNode().get(this.indexKey());
    }

    @Override
    public void set(final C holder, final T value) {
        holder.rootNode().set(this.indexKey(), value);
    }

    @Override
    public Class<T> valueType() {
        return (Class<T>) GenericTypeReflector.box(TypeFactory.rawClass(this.node.type()));
    }

    @Override
    public T defaultValue() {
        if (this.node.defaultValue() == null) {
            throw new IllegalStateException(this.node.path() + " cannot have a default value for null if used in a menu");
        }
        return this.node.defaultValue();
    }

    @Override
    public String indexKey() {
        return this.node.path();
    }
}
