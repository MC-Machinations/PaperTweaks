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
package me.machinemaker.vanillatweaks.moonshine.renderers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiniMessageMessageRenderer extends AbstractMessageRenderer<String> {

    @Override
    protected @NotNull Component render(@NotNull String intermediateMessage, @NotNull Map<String, ? extends Component> resolvedPlaceholders) {
        final List<Template> templates = new ArrayList<>();
        for (final var entry : resolvedPlaceholders.entrySet()) {
            templates.add(Template.of(entry.getKey(), entry.getValue()));
        }

        return MiniMessage.get().parse(intermediateMessage, templates);
    }
}
