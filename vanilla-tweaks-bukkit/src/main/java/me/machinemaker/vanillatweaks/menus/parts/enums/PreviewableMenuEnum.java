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

import me.machinemaker.vanillatweaks.menus.parts.Previewable;
import net.kyori.adventure.text.Component;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public interface PreviewableMenuEnum<E extends Enum<E> & PreviewableMenuEnum<E>> extends MenuEnum<E>, Previewable {

    @Override
    default Component build(final E selected, final String labelKey, final String commandPrefix, final String optionKey) {
        return join(
                this.createClickComponent(selected, commandPrefix, optionKey),
                text(' '),
                Previewable.createPreviewComponent(this.createLabel(labelKey), this.previewCommandPrefix(), this.name()),
                text(' '),
                this.createLabel(labelKey),
                newline()
        );
    }

    default Component buildWithoutPreview(final E selected, final String labelKey, final String commandPrefix, final String optionKey) {
        return MenuEnum.super.build(selected, labelKey, commandPrefix, optionKey);
    }

    String previewCommandPrefix();
}
