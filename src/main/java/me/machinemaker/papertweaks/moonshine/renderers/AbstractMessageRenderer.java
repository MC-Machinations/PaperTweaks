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
package me.machinemaker.papertweaks.moonshine.renderers;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import me.machinemaker.papertweaks.moonshine.module.AnnotationBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.moonshine.message.IMessageRenderer;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;

abstract class AbstractMessageRenderer<I> extends AnnotationBase implements IMessageRenderer<Audience, I, Component, Component> {

    protected abstract Component render(I intermediateMessage, Map<String, ? extends Component> resolvedPlaceholders);

    @Override
    public final Component render(final Audience receiver, final I intermediateMessage, final Map<String, ? extends Component> resolvedPlaceholders, final @Nullable Method method, final Type owner) {
        return text()
            .color(this.getTextColor(method))
            .append(this.render(intermediateMessage, resolvedPlaceholders))
            .build();
    }


}
