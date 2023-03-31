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
package me.machinemaker.vanillatweaks.moonshine.module;

import java.lang.reflect.AnnotatedElement;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AnnotationBase {

    protected final @Nullable TextColor getTextColor(final @Nullable AnnotatedElement element) {
        if (element != null) {
            if (element.isAnnotationPresent(me.machinemaker.vanillatweaks.moonshine.annotation.TextColor.class)) {
                return NamedTextColor.NAMES.value(element.getAnnotation(me.machinemaker.vanillatweaks.moonshine.annotation.TextColor.class).value());
            }
        }
        return null;
    }
}
