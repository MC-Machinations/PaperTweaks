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
package me.machinemaker.vanillatweaks.menus.config;

import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.menus.options.MenuOption;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.Setting;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public abstract class SimpleConfigMenuOptionBuilder<T> extends ConfigMenuOptionBuilder<T> {

    @Override
    public final <C extends MenuModuleConfig<C, ?>> MenuOption.@NotNull Builder<T, ?, C, ?> buildOption(@NotNull ValueNode<?> valueNode, final @NotNull Map<String, ConfigSetting<?, C>> settings) {
        var setting = this.<C>createSetting(valueNode);
        settings.put(setting.indexKey(), setting);
        var builder =  this.<C>getBuilder().createBuilder(labelKey(valueNode), typeMapper(valueNode), setting);
        if (valueNode.meta().containsKey("desc")) {
            builder.extendedDescription((String) valueNode.meta().get("desc"));
        }
        return builder;
    }

    protected abstract <C extends MenuModuleConfig<C, ?>> @NotNull MenuOptionBuilderCreator<T, C> getBuilder();

    protected abstract <C extends MenuModuleConfig<C, ?>> @NotNull ConfigSetting<T, C> createSetting(ValueNode<?> valueNode);

    @FunctionalInterface
    protected interface MenuOptionBuilderCreator<T, S> {

        @NotNull MenuOption.Builder<T, ?, S, ?> createBuilder(@NotNull String labelKey, @NotNull Function<S, T> typeMapper, @NotNull Setting<T, S> setting);
    }
}
