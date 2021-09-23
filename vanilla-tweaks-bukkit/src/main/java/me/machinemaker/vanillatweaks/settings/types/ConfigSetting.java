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
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.leangen.geantyref.GenericTypeReflector;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.parsers.BooleanParser;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ConfigSetting<T, C extends MenuModuleConfig<C>>(@NotNull ValueNode<T> node, @NotNull ArgumentParser<CommandDispatcher, T> argumentParser) implements Setting<T, C> {

    public static <C extends MenuModuleConfig<C>> ConfigSetting<Boolean, C> ofBoolean(@NotNull ValueNode<?> valueNode) {
        return new ConfigSetting<>((ValueNode<Boolean>) valueNode, new BooleanParser());
    }

    @Override
    public @Nullable T get(@NotNull C container) {
        return container.rootNode().get(this.indexKey());
    }

    @Override
    public void set(@NotNull C holder, T value) {
        holder.rootNode().set(this.indexKey(), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> valueType() {
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
}
