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
package me.machinemaker.vanillatweaks.menus.config;

import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.config.I18nKey;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class ConfigMenuOptionBuilder<T> implements OptionBuilder {

    public abstract @NotNull Class<T> typeClass();

    protected @NotNull String labelKey(@NotNull ValueNode<?> valueNode) {
        String labelKey;
        if (valueNode.meta().containsKey("i18n")) {
            labelKey = ((I18nKey) valueNode.meta().get("i18n")).value();
        } else {
            labelKey = valueNode.key();
        }
        return labelKey;
    }

    protected final <C extends MenuModuleConfig<C>> @NotNull Function<C, T> typeMapper(ValueNode<?> valueNode) {
        return c -> c.rootNode().get(valueNode.path());
    }
}
