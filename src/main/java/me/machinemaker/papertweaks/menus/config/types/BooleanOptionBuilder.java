/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.menus.config.types;

import me.machinemaker.lectern.ValueNode;
import me.machinemaker.papertweaks.menus.config.SimpleConfigMenuOptionBuilder;
import me.machinemaker.papertweaks.menus.options.BooleanMenuOption;
import me.machinemaker.papertweaks.modules.MenuModuleConfig;
import me.machinemaker.papertweaks.settings.types.ConfigSetting;

public class BooleanOptionBuilder extends SimpleConfigMenuOptionBuilder<Boolean> {

    @Override
    public Class<Boolean> typeClass() {
        return boolean.class;
    }

    @Override
    protected <C extends MenuModuleConfig<C, ?>> MenuOptionBuilderCreator<Boolean, C> getBuilder() {
        return BooleanMenuOption::newBuilder;
    }

    @Override
    protected <C extends MenuModuleConfig<C, ?>> ConfigSetting<Boolean, C> createSetting(final ValueNode<?> valueNode) {
        return ConfigSetting.ofBoolean(valueNode);
    }
}
