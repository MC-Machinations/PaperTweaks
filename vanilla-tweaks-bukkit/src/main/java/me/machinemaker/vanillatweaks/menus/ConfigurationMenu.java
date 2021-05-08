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
package me.machinemaker.vanillatweaks.menus;

import me.machinemaker.vanillatweaks.menus.parts.MenuPartLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.TextComponent.ofChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ConfigurationMenu<S> {

    private static final int WIDTH = 80;
    private static final Component TITLE_LINE = text(" ".repeat(WIDTH) + "\n", DARK_GRAY, TextDecoration.STRIKETHROUGH);
    private static final Component END_LINE = text(" ".repeat(WIDTH), DARK_GRAY, TextDecoration.STRIKETHROUGH); // no newline

    private final Component title;
    private final String commandPrefix;
    private final List<MenuPartLike<S>> parts;

    protected ConfigurationMenu(@NotNull Component title, String commandPrefix, @NotNull List<MenuPartLike<S>> parts) {
        this.title = title;
        this.commandPrefix = commandPrefix;
        this.parts = parts;
    }

    public @NotNull Component build(@NotNull S object) {
        ComponentLike[] children = new Component[this.parts.size() + 4];
        children[0] = TITLE_LINE;
        children[1] = title;
        children[2] = TITLE_LINE;
        for (int i = 0; i < this.parts.size(); i++) {
            children[i + 3] = this.parts.get(i).asMenuPart().build(object, this.commandPrefix);
        }
        children[children.length - 1] = END_LINE;
        return ofChildren(
                children
        );
    }
}
