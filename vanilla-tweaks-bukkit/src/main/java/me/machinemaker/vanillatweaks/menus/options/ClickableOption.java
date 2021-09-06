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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public interface ClickableOption<T> extends Option {

    @NotNull Component extendedDescription();

    @NotNull Component defaultValueDescription();

    @NotNull Component createClickComponent(T selected, String commandPrefix);

    @NotNull Component createClickHoverComponent(T selected);

    default @NotNull ClickEvent createRunCommand(@NotNull String commandPrefix, @NotNull Object value) {
        return createRunCommand(commandPrefix, optionKey(), value);
    }

    static @NotNull ClickEvent createRunCommand(@NotNull String commandPrefix, @NotNull String optionKey, @NotNull Object value) {
        return ClickEvent.runCommand(String.join(" ", commandPrefix, optionKey, value.toString()));
    }
}
