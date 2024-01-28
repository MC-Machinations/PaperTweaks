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
package me.machinemaker.papertweaks.menus;

import java.util.List;
import me.machinemaker.papertweaks.menus.parts.MenuPartLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public abstract class AbstractConfigurationMenu<S> implements ConfigurationMenu<S> {

    private static final int WIDTH = 80;
    public static final Component TITLE_LINE = text(" ".repeat(WIDTH) + "\n", DARK_GRAY, TextDecoration.STRIKETHROUGH);
    public static final Component END_LINE = text(" ".repeat(WIDTH), DARK_GRAY, TextDecoration.STRIKETHROUGH); // no newline

    private final Component title;
    private final String commandPrefix;
    private final List<MenuPartLike<S>> parts;

    protected AbstractConfigurationMenu(final Component title, final String commandPrefix, final List<MenuPartLike<S>> parts) {
        this.title = title;
        this.commandPrefix = commandPrefix;
        this.parts = parts;
    }

    @Override
    public ComponentLike[] buildHeader(final S object) {
        return new ComponentLike[]{TITLE_LINE, this.title, TITLE_LINE};
    }

    @Override
    public Iterable<? extends ComponentLike> buildParts(final S object) {
        return this.parts.stream().map(MenuPartLike::asMenuPart).map(part -> part.build(object, this.commandPrefix)).toList();
    }

    @Override
    public ComponentLike[] buildFooter(final S object) {
        return new ComponentLike[]{END_LINE};
    }

    @Override
    public ComponentLike build(final S object) {
        return text()
                .append(this.buildHeader(object))
                .append(this.buildParts(object))
                .append(this.buildFooter(object));
    }
}
