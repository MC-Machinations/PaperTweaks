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
package me.machinemaker.papertweaks.menus.options;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public interface ClickableOption<T> extends Option {

    static ClickEvent createRunCommand(final String commandPrefix, final String optionKey, final Object value) {
        return ClickEvent.runCommand(String.join(" ", commandPrefix, optionKey, value.toString()));
    }

    static ClickEvent createSuggestCommand(final String commandPrefix, final String optionKey) {
        return ClickEvent.suggestCommand(String.join(" ", commandPrefix, optionKey) + " ");
    }

    Component extendedDescription();

    Component defaultValueDescription();

    Component createClickComponent(T selected, String commandPrefix);

    Component createClickHoverComponent(T selected);

    default ClickEvent createRunCommand(final String commandPrefix, final Object value) {
        return createRunCommand(commandPrefix, this.optionKey(), value);
    }

    default ClickEvent createSuggestCommand(final String commandPrefix) {
        return createSuggestCommand(commandPrefix, this.optionKey());
    }
}
