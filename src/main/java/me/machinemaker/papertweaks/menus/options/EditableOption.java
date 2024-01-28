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
package me.machinemaker.papertweaks.menus.options;

import me.machinemaker.papertweaks.menus.parts.Labelled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public interface EditableOption<T> extends Labelled, ClickableOption<T> {

    Component EDIT = text("[ ✎ ]", GRAY);

    @Override
    default Component createClickComponent(final T selected, final String commandPrefix) {
        return EDIT.hoverEvent(showText(this.createClickHoverComponent(selected))).clickEvent(this.createSuggestCommand(commandPrefix));
    }

    @Override
    default Component createClickHoverComponent(final T selected) {
        final TranslatableComponent.Builder builder = translatable().key("commands.config.editable").color(GRAY).arguments(this.label().color(WHITE));
        if (this.extendedDescription() != Component.empty()) {
            builder.append(newline()).append(this.extendedDescription().color(GRAY));
        }
        if (this.validations() != Component.empty()) {
            builder.append(newline()).append(this.validations().color(DARK_GRAY));
        }
        builder.append(newline()).append(translatable("commands.config.default-value", DARK_GRAY, this.defaultValueDescription()));
        return builder.build();
    }

    default Component validations() {
        return Component.empty();
    }
}
