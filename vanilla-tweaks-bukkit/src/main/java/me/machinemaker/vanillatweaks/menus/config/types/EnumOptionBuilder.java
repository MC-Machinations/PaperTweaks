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
package me.machinemaker.vanillatweaks.menus.config.types;

import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.menus.config.ConfigMenuOptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.OptionBuilder;
import me.machinemaker.vanillatweaks.menus.options.EnumMenuOption;
import me.machinemaker.vanillatweaks.menus.options.MenuOption;
import me.machinemaker.vanillatweaks.menus.parts.enums.MenuEnum;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnumOptionBuilder<E extends Enum<E> & MenuEnum<E>> extends ConfigMenuOptionBuilder<E> {

    private final Class<E> enumClass;

    private EnumOptionBuilder(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public @NotNull Class<E> typeClass() {
        return this.enumClass;
    }

    @Override
    public @NotNull <C extends MenuModuleConfig<C>> MenuOption<E, C> buildOption(@NotNull ValueNode<?> valueNode, @NotNull Map<String, ConfigSetting<?, C>> settings) {
        var setting = ConfigSetting.<E, C>ofEnum(valueNode, this.enumClass);
        settings.put(setting.indexKey(), setting);
        return EnumMenuOption.of(this.enumClass, c -> c.rootNode().get(valueNode.path()), setting);
    }

    public static class Factory implements OptionBuilder {

        @Override
        public @Nullable <C extends MenuModuleConfig<C>> MenuOption<?, C> buildOption(@NotNull ValueNode<?> valueNode, @NotNull Map<String, ConfigSetting<?, C>> settings) {
            if (valueNode.type().isEnumImplType() && MenuEnum.class.isAssignableFrom(valueNode.type().getRawClass())) {
                return createBuilder(valueNode.type().getRawClass()).buildOption(valueNode, settings);
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        private <E extends Enum<E> & MenuEnum<E>> EnumOptionBuilder<E> createBuilder(Class<?> enumClass) {
            return new EnumOptionBuilder<>((Class<E>) enumClass);
        }
    }
}
