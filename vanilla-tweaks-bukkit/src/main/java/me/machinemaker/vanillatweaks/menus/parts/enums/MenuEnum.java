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
package me.machinemaker.vanillatweaks.menus.parts.enums;

import me.machinemaker.vanillatweaks.menus.options.ClickableOption;
import me.machinemaker.vanillatweaks.menus.parts.clicks.ToggleOption;
import net.kyori.adventure.text.Component;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.translatable;

public interface MenuEnum<E extends Enum<E> & MenuEnum<E>> extends ToggleOption<E> {

    default Component build(final E selected, final String labelKey, final String commandPrefix, final String optionKey) {
        return join(
                this.createClickComponent(selected, commandPrefix, optionKey),
                space(),
                this.createLabel(labelKey),
                newline()
        );
    }

    default Component createClickComponent(final E selected, final String commandPrefix, final String optionKey) {
        return this.createClickComponent(selected, ClickableOption.createRunCommand(commandPrefix, optionKey, this.name()), false);
    }

    @Override
    default String clickCommandValue(final E selected) {
        return this.name();
    }

    @Override
    default Component extendedDescription() {
        return Component.empty();
    }

    @Override
    default Component defaultValueDescription() {
        return Component.empty();
    }

    @Override
    default String optionKey() {
        throw new UnsupportedOperationException("MenuEnums can't have option keys");
    }

    @Override
    default boolean isSelected(final E selected) {
        return this == selected;
    }

    default Component createLabel(final String labelKey) {
        return translatable(labelKey, this.label());
    }

    Component label();

    String name(); // Enum will implement this

}
