/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.vanillatweaks.menus.parts;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface Previewable {

    Component PREVIEW_COMPONENT = text("[ ℹ ]", GRAY).hoverEvent(showText(translatable("commands.config.click-to-preview", GRAY)));

    static Component createPreviewComponent(final Component label, final String previewCommandPrefix, final String name) {
        return createPreviewComponent(label, runCommand(previewCommandPrefix + " " + name));
    }

    static Component createPreviewComponent(final Component label, final ClickEvent previewAction) {
        return PREVIEW_COMPONENT
                .hoverEvent(showText(translatable("commands.config.click-to-preview.label", label.color(WHITE))))
                .clickEvent(previewAction);
    }

    default void preview(final Player player) {
    }
}
