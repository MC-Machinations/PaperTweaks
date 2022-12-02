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
package me.machinemaker.vanillatweaks.menus.config.types;

import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.menus.config.SimpleConfigMenuOptionBuilder;
import me.machinemaker.vanillatweaks.menus.options.DoubleMenuOption;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;

public class DoubleOptionBuilder extends SimpleConfigMenuOptionBuilder<Double> {

    @Override
    public Class<Double> typeClass() {
        return double.class;
    }

    @Override
    protected <C extends MenuModuleConfig<C, ?>> MenuOptionBuilderCreator<Double, C> getBuilder() {
        return DoubleMenuOption::builder;
    }

    @Override
    protected <C extends MenuModuleConfig<C, ?>> ConfigSetting<Double, C> createSetting(final ValueNode<?> valueNode) {
        return ConfigSetting.ofDouble(valueNode);
    }
}