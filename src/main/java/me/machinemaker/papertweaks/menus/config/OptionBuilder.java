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
package me.machinemaker.papertweaks.menus.config;

import java.util.Map;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.papertweaks.menus.options.MenuOption;
import me.machinemaker.papertweaks.modules.MenuModuleConfig;
import me.machinemaker.papertweaks.settings.types.ConfigSetting;
import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface OptionBuilder {

    <C extends MenuModuleConfig<C, ?>> MenuOption.Builder<?, ?, C, ?> buildOption(ValueNode<?> valueNode, Map<String, ConfigSetting<?, C>> settings);

    @FunctionalInterface
    interface Factory {

        <C extends MenuModuleConfig<C, ?>> MenuOption.@Nullable Builder<?, ?, C, ?> buildOption(ValueNode<?> valueNode, Map<String, ConfigSetting<?, C>> settings);
    }
}
