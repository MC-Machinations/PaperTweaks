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
package me.machinemaker.papertweaks.menus.config.types;

import java.util.Map;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.papertweaks.menus.config.ConfigMenuOptionBuilder;
import me.machinemaker.papertweaks.menus.config.OptionBuilder;
import me.machinemaker.papertweaks.menus.options.EnumMenuOption;
import me.machinemaker.papertweaks.menus.options.MenuOption;
import me.machinemaker.papertweaks.menus.options.SelectableEnumMenuOption;
import me.machinemaker.papertweaks.menus.parts.enums.MenuEnum;
import me.machinemaker.papertweaks.modules.MenuModuleConfig;
import me.machinemaker.papertweaks.settings.types.ConfigSetting;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EnumOptionBuilderFactory implements OptionBuilder.Factory {

    @Override
    public <C extends MenuModuleConfig<C, ?>> MenuOption.@Nullable Builder<? extends Enum<?>, ?, C, ?> buildOption(final ValueNode<?> valueNode, final Map<String, ConfigSetting<?, C>> settings) {
        if (valueNode.type().isEnumImplType() && MenuEnum.class.isAssignableFrom(valueNode.type().getRawClass())) {
            return this.createMenuEnumOption(valueNode.type().getRawClass(), valueNode, settings);
        } else if (valueNode.type().isEnumImplType()) {
            return this.createEnumOption(valueNode.type().getRawClass(), valueNode, settings);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E> & MenuEnum<E>, C extends MenuModuleConfig<C, ?>> MenuOption.Builder<E, ?, C, ?> createMenuEnumOption(final Class<?> enumClass, final ValueNode<?> valueNode, final Map<String, ConfigSetting<?, C>> settings) {
        final ConfigSetting<E, C> setting = ConfigSetting.ofEnum(valueNode, (Class<E>) enumClass);
        settings.put(setting.indexKey(), setting);
        return SelectableEnumMenuOption.builder(setting.valueType(), ConfigMenuOptionBuilder.labelKey(valueNode), setting);
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>, C extends MenuModuleConfig<C, ?>> MenuOption.Builder<E, ?, C, ?> createEnumOption(final Class<?> enumClass, final ValueNode<?> valueNode, final Map<String, ConfigSetting<?, C>> settings) {
        final ConfigSetting<E, C> setting = ConfigSetting.ofEnum(valueNode, (Class<E>) enumClass);
        settings.put(setting.indexKey(), setting);
        final EnumMenuOption.Builder<E, C> builder = EnumMenuOption.builder(ConfigMenuOptionBuilder.labelKey(valueNode), setting);
        if (valueNode.meta().containsKey("desc")) {
            builder.extendedDescription(valueNode.meta().get("desc").toString());
        }
        return builder;
    }

}
