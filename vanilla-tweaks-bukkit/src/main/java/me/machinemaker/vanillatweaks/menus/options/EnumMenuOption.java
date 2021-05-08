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
package me.machinemaker.vanillatweaks.menus.options;

import me.machinemaker.vanillatweaks.menus.parts.enums.MenuEnum;
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static net.kyori.adventure.text.TextComponent.ofChildren;

public class EnumMenuOption<E extends Enum<E> & MenuEnum<E>, S> extends MenuOption<E, S> {

    private final List<E> options;

    private EnumMenuOption(@NotNull Function<S, E> typeMapper, @NotNull Setting<E, ?> setting, @NotNull Class<E> classOfE) {
        super(Component.empty(), typeMapper, setting, Component.empty(), null);
        this.options = Arrays.stream(classOfE.getEnumConstants()).toList();
    }

    @Override
    public @NotNull Component build(@NotNull S object, @NotNull String commandPrefix) {
        Component[] components = new Component[this.options.size()];
        for (int i = 0; i < options.size(); i++) {
            components[i] = this.options.get(i).build(this.selected(object), commandPrefix);
        }
        return ofChildren(components);
    }

    public static <E extends Enum<E> & MenuEnum<E>, S> EnumMenuOption<E, S> of(Class<E> classOfE, @NotNull Function<S, E> typeMapper, @NotNull Setting<E, ?> setting) {
        return new EnumMenuOption<>(typeMapper, setting, classOfE);
    }
}
