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
package me.machinemaker.vanillatweaks.menus.options;

import me.machinemaker.vanillatweaks.menus.parts.Labelled;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface EditableOption<T> extends Labelled, ClickableOption<T>{

    Component EDIT = text("[ ✎ ]", GRAY);

    @Override
    @NotNull
    default Component createClickComponent(T selected, String commandPrefix) {
        return EDIT.hoverEvent(showText(createClickHoverComponent(selected))).clickEvent(createSuggestCommand(commandPrefix));
    }

    @Override
    @NotNull
    default Component createClickHoverComponent(T selected) {
        var builder = translatable().key("commands.config.editable").color(GRAY).args(this.label().color(WHITE));
        if (extendedDescription() != Component.empty()) {
            builder.append(newline()).append(extendedDescription().color(GRAY));
        }
        if (validations() != Component.empty()) {
            builder.append(newline()).append(validations().color(DARK_GRAY));
        }
        builder.append(newline()).append(translatable("commands.config.default-value", DARK_GRAY, defaultValueDescription()));
        return builder.build();
    }

    default Component validations() {
        return Component.empty();
    }
}
