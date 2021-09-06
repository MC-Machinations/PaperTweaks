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
package me.machinemaker.vanillatweaks.menus.parts.enums;

import me.machinemaker.vanillatweaks.menus.options.ClickableOption;
import me.machinemaker.vanillatweaks.menus.parts.clicks.ToggleOption;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.TextComponent.ofChildren;

public interface MenuEnum<E extends Enum<E> & MenuEnum<E>> extends ToggleOption<E> {

    @NotNull
    default Component build(@NotNull E selected, @NotNull String commandPrefix, @NotNull String optionKey) {
        return ofChildren(
                createClickComponent(selected, commandPrefix, optionKey),
                space(),
                label(),
                newline()
        );
    }

    default @NotNull Component createClickComponent(@NotNull E selected, @NotNull String commandPrefix, @NotNull String optionKey) {
        return createClickComponent(selected, ClickableOption.createRunCommand(commandPrefix, optionKey, name()), false);
    }

    @Override
    @NotNull
    default String clickCommandValue(@NotNull E selected) {
        return this.name();
    }

    @Override
    @NotNull
    default Component extendedDescription() {
        return Component.empty();
    }

    @Override
    @NotNull
    default Component defaultValueDescription() {
        return Component.empty();
    }

    @Override
    @NotNull
    default String optionKey() {
        throw new UnsupportedOperationException("MenuEnums can't have option keys");
    }

    @Override
    default boolean isSelected(@NotNull E selected) {
        return this == selected;
    }

    @NotNull Component label();

    @NotNull String name(); // Enum will implement this

}
