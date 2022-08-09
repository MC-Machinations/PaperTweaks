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

import java.util.Objects;
import java.util.function.Function;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.config.I18nKey;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ConfigMenuOptionBuilder<T> implements OptionBuilder {

    public static String labelKey(final ValueNode<?> valueNode) {
        final String labelKey;
        if (valueNode.meta().containsKey(I18nKey.META_KEY)) {
            labelKey = ((I18nKey) valueNode.meta().get(I18nKey.META_KEY)).value();
        } else {
            labelKey = valueNode.key();
        }
        return labelKey;
    }

    public abstract Class<T> typeClass();

    protected final <C extends MenuModuleConfig<C, ?>> Function<C, @Nullable T> typeMapper(final ValueNode<?> valueNode) {
        return c -> Objects.requireNonNull(c.rootNode().get(valueNode.path()));
    }
}
