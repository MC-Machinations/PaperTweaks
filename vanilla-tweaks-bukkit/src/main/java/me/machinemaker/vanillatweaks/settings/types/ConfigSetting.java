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
package me.machinemaker.vanillatweaks.settings.types;

import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.leangen.geantyref.GenericTypeReflector;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.parsers.BooleanParser;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@SuppressWarnings("unchecked")
public record ConfigSetting<T, C extends MenuModuleConfig<C, ?>>(@NotNull ValueNode<T> node, @NotNull ArgumentParser<CommandDispatcher, T> argumentParser, @NotNull Component validations) implements Setting<T, C> {

    public static <C extends MenuModuleConfig<C, ?>> ConfigSetting<Boolean, C> ofBoolean(@NotNull ValueNode<?> valueNode) {
        return new ConfigSetting<>((ValueNode<Boolean>) valueNode, new BooleanParser());
    }

    public static <E extends Enum<E>, C extends MenuModuleConfig<C, ?>> ConfigSetting<E, C> ofEnum(@NotNull ValueNode<?> valueNode, @NotNull Class<E> classOfE) {
        return new ConfigSetting<>((ValueNode<E>) valueNode, new EnumArgument.EnumParser<>(classOfE));
    }

    public static <C extends MenuModuleConfig<C, ?>> ConfigSetting<Integer, C> ofInt(@NotNull ValueNode<?> valueNode) {
        return new ConfigSetting<>((ValueNode<Integer>) valueNode, new IntegerArgument.IntegerParser<>(Integer.parseInt(valueNode.meta().getOrDefault("min", IntegerArgument.IntegerParser.DEFAULT_MINIMUM).toString()), Integer.parseInt(valueNode.meta().getOrDefault("max", IntegerArgument.IntegerParser.DEFAULT_MAXIMUM).toString())));
    }

    public ConfigSetting(@NotNull ValueNode<T> node, @NotNull ArgumentParser<CommandDispatcher, T> argumentParser) {
        this(node, argumentParser, createValidations(node));
    }

    @Override
    public @Nullable T get(@NotNull C container) {
        return container.rootNode().get(this.indexKey());
    }

    @Override
    public void set(@NotNull C holder, T value) {
        holder.rootNode().set(this.indexKey(), value);
    }

    @Override
    public @NotNull Class<T> valueType() {
        return (Class<T>) GenericTypeReflector.box(TypeFactory.rawClass(node.type()));
    }

    @Override
    public @NotNull T defaultValue() {
        if (this.node.defaultValue() == null) {
            throw new IllegalStateException(this.node.path() + " cannot have a default value for null if used in a menu");
        }
        return this.node.defaultValue();
    }

    @Override
    public @NotNull String indexKey() {
        return this.node.path();
    }

    private static @NotNull Component createValidations(ValueNode<?> valueNode) {
        var builder = text();
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
}
