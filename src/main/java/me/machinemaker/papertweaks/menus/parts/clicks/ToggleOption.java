/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.menus.parts.clicks;

import me.machinemaker.papertweaks.menus.options.ClickableOption;
import me.machinemaker.papertweaks.menus.parts.Labelled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface ToggleOption<T> extends Labelled, ClickableOption<T> {

    Component OFF = text("[ ❌ ]", RED);
    Component ON = text("[ ✔ ]", GREEN);

    boolean isSelected(T selected);

    String clickCommandValue(T selected);

    @Override
    default Component createClickComponent(final T selected, final String commandPrefix) {
        return this.createClickComponent(selected, this.createRunCommand(commandPrefix, this.clickCommandValue(selected)), true);
    }

    default Component createClickComponent(final T selected, final ClickEvent clickEvent, final boolean actionIfSelected) {
        Component component = this.isSelected(selected) ? ON : OFF;
        if (!this.isSelected(selected) || (this.isSelected(selected) && actionIfSelected)) {
            component = component.hoverEvent(showText(this.createClickHoverComponent(selected))).clickEvent(clickEvent);
        }
        return component;
    }

    @Override
    default Component createClickHoverComponent(final T selected) {
        Component component = translatable("commands.config.bool-toggle." + this.isSelected(selected), this.isSelected(selected) ? RED : GREEN, this.label().color(WHITE));
        if (this.extendedDescription() != Component.empty()) {
            component = component.append(newline()).append(this.extendedDescription().color(GRAY)).append(newline()).append(translatable("commands.config.default-value", DARK_GRAY, this.defaultValueDescription()));
        }
        return component;
    }
}
