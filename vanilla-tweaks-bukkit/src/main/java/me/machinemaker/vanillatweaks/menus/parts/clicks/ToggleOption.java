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
package me.machinemaker.vanillatweaks.menus.parts.clicks;

import me.machinemaker.vanillatweaks.menus.options.ClickableOption;
import me.machinemaker.vanillatweaks.menus.parts.Labelled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface ToggleOption<T> extends Labelled, ClickableOption<T> {

    Component OFF = text("[ ❌ ]", RED);
    Component ON = text("[ ✔ ]", NamedTextColor.GREEN);

    boolean isSelected(@NotNull T selected);

    @NotNull String clickCommandValue(@NotNull T selected);

    @Override
    default @NotNull Component createClickComponent(T selected, String commandPrefix) {
        return createClickComponent(selected, createRunCommand(commandPrefix, clickCommandValue(selected)), true);
    }

    default @NotNull Component createClickComponent(@NotNull T selected, @NotNull ClickEvent clickEvent, boolean actionIfSelected) {
        Component component = isSelected(selected) ? ON : OFF;
        if (!isSelected(selected) || (isSelected(selected) && actionIfSelected)) {
            component = component.hoverEvent(showText(createClickHoverComponent(selected))).clickEvent(clickEvent);
        }
        return component;
    }

    @Override
    default @NotNull Component createClickHoverComponent(T selected) {
        Component component = translatable("commands.config.bool-toggle." + isSelected(selected), isSelected(selected) ? RED : GREEN, this.label().color(WHITE));
        if (extendedDescription() != Component.empty()) {
            component = component.append(newline()).append(extendedDescription().color(GRAY)).append(newline()).append(translatable("commands.config.default-value", DARK_GRAY, defaultValueDescription()));
        }
        return component;
    }
}
